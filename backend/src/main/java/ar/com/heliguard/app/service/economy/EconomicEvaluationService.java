package ar.com.heliguard.app.service.economy;

import ar.com.heliguard.app.domain.EconomicAssessment;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.LossCauseEstimate;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.LossAssessmentStatus;
import ar.com.heliguard.app.domain.enumeration.LossCause;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EconomicAssessmentRepository;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.repository.LossCauseEstimateRepository;
import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.CauseAllocation;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateRequest;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateResponse;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicScenario;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.tenant.PermissionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Evaluates energy loss attribution (no double counting, visible unknowns) and
 * monetary impact using active tariff validity or scenario ranges (RB-ECO-001..003, T14, T15).
 */
@Service
@Transactional
public class EconomicEvaluationService {

    private static final BigDecimal WH_PER_KWH = new BigDecimal("1000");

    private final PermissionService permissionService;
    private final EnergyModelRunRepository modelRunRepository;
    private final TariffPlanRepository tariffPlanRepository;
    private final TariffRateRepository tariffRateRepository;
    private final LossAssessmentRepository lossAssessmentRepository;
    private final LossCauseEstimateRepository causeEstimateRepository;
    private final EconomicAssessmentRepository economicAssessmentRepository;

    public EconomicEvaluationService(
        PermissionService permissionService,
        EnergyModelRunRepository modelRunRepository,
        TariffPlanRepository tariffPlanRepository,
        TariffRateRepository tariffRateRepository,
        LossAssessmentRepository lossAssessmentRepository,
        LossCauseEstimateRepository causeEstimateRepository,
        EconomicAssessmentRepository economicAssessmentRepository
    ) {
        this.permissionService = permissionService;
        this.modelRunRepository = modelRunRepository;
        this.tariffPlanRepository = tariffPlanRepository;
        this.tariffRateRepository = tariffRateRepository;
        this.lossAssessmentRepository = lossAssessmentRepository;
        this.causeEstimateRepository = causeEstimateRepository;
        this.economicAssessmentRepository = economicAssessmentRepository;
    }

    public Mono<EconomicEvaluateResponse> evaluate(EconomicEvaluateRequest request) {
        Instant assessedAt = request.assessedAt() != null ? request.assessedAt() : Instant.now();
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                modelRunRepository
                    .findById(request.modelRunId())
                    .filter(r -> tenant.organizationId().equals(r.getOrganizationId()))
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ENERGY_MODEL_RUN_NOT_FOUND", "EnergyModelRun not found"))
                    )
                    .flatMap(run -> {
                        if (run.getStatus() != EnergyModelRunStatus.SUCCEEDED || run.getExpectedWh() == null || run.getActualWh() == null) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.CONFLICT,
                                    "INSUFFICIENT_ENERGY_DATA",
                                    "Cannot evaluate economic loss on an INSUFFICIENT_DATA or failed EnergyModelRun"
                                )
                            );
                        }
                        return tariffPlanRepository
                            .findById(request.tariffPlanId())
                            .filter(p ->
                                tenant.organizationId().equals(p.getOrganizationId()) &&
                                run.getInstallationId().equals(p.getInstallationId())
                            )
                            .switchIfEmpty(
                                Mono.error(
                                    new DomainRuleException(
                                        HttpStatus.NOT_FOUND,
                                        "TARIFF_PLAN_NOT_FOUND",
                                        "TariffPlan not found for installation"
                                    )
                                )
                            )
                            .flatMap(plan ->
                                tariffRateRepository
                                    .findByTariffPlan(plan.getId())
                                    .filter(rate ->
                                        tenant.organizationId().equals(rate.getOrganizationId()) &&
                                        !rate.getValidFrom().isAfter(assessedAt) &&
                                        (rate.getValidTo() == null || rate.getValidTo().isAfter(assessedAt))
                                    )
                                    .next()
                                    .switchIfEmpty(
                                        Mono.error(
                                            new DomainRuleException(
                                                HttpStatus.BAD_REQUEST,
                                                "NO_ACTIVE_TARIFF_RATE",
                                                "No active TariffRate found for assessedAt timestamp"
                                            )
                                        )
                                    )
                                    .flatMap(rate -> persistAndCompute(run, plan, rate, assessedAt, request))
                            );
                    })
            );
    }

    private Mono<EconomicEvaluateResponse> persistAndCompute(
        EnergyModelRun run,
        TariffPlan plan,
        TariffRate rate,
        Instant assessedAt,
        EconomicEvaluateRequest request
    ) {
        BigDecimal totalLossWh = run.getExpectedWh().subtract(run.getActualWh()).max(BigDecimal.ZERO);
        List<CauseAllocation> causes = request.causes() != null ? request.causes() : List.of();

        BigDecimal attributedWh = BigDecimal.ZERO;
        for (CauseAllocation c : causes) {
            if (c.estimatedWh().compareTo(BigDecimal.ZERO) < 0) {
                return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_ESTIMATED_WH", "estimatedWh must be >= 0"));
            }
            if (c.cause() != LossCause.UNKNOWN) {
                attributedWh = attributedWh.add(c.estimatedWh());
            }
        }
        // RB-ECO-001 / T14: No double counting
        if (attributedWh.compareTo(totalLossWh) > 0) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "LOSS_DOUBLE_COUNTING_FORBIDDEN",
                    "Sum of attributed loss causes (" + attributedWh + " Wh) exceeds totalLossWh (" + totalLossWh + " Wh)"
                )
            );
        }

        BigDecimal residualUnknownWh = totalLossWh.subtract(attributedWh);
        BigDecimal lossPct = run.getExpectedWh().compareTo(BigDecimal.ZERO) > 0
            ? totalLossWh.multiply(new BigDecimal("100")).divide(run.getExpectedWh(), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        LossAssessment lossAssessment = new LossAssessment();
        lossAssessment.setOrganizationId(run.getOrganizationId());
        lossAssessment.setModelRunId(run.getId());
        lossAssessment.setTotalLossWh(totalLossWh);
        lossAssessment.setLossPct(lossPct);
        lossAssessment.setMethodology("SDD_NO_DOUBLE_COUNT_V1");
        lossAssessment.setAssessedAt(assessedAt);
        lossAssessment.setValidationStatus(
            residualUnknownWh.compareTo(BigDecimal.ZERO) > 0 ? LossAssessmentStatus.ESTIMATED : LossAssessmentStatus.VALIDATED
        );

        BigDecimal finalAttributed = attributedWh;
        return lossAssessmentRepository
            .save(lossAssessment)
            .flatMap(savedLoss -> {
                List<LossCauseEstimate> causeEntities = new java.util.ArrayList<>(
                    causes
                        .stream()
                        .filter(c -> c.cause() != LossCause.UNKNOWN)
                        .map(c -> {
                            LossCauseEstimate e = new LossCauseEstimate();
                            e.setOrganizationId(run.getOrganizationId());
                            e.setLossAssessmentId(savedLoss.getId());
                            e.setCause(c.cause());
                            e.setEstimatedWh(c.estimatedWh());
                            e.setMethod(c.method());
                            e.setEvidenceNote(c.evidenceNote());
                            return e;
                        })
                        .toList()
                );
                if (residualUnknownWh.compareTo(BigDecimal.ZERO) > 0) {
                    LossCauseEstimate unknown = new LossCauseEstimate();
                    unknown.setOrganizationId(run.getOrganizationId());
                    unknown.setLossAssessmentId(savedLoss.getId());
                    unknown.setCause(LossCause.UNKNOWN);
                    unknown.setEstimatedWh(residualUnknownWh);
                    unknown.setMethod("RESIDUAL_UNATTRIBUTED");
                    unknown.setEvidenceNote("Visible residual deficit not attributed to a specific cause");
                    causeEntities.add(unknown);
                }

                return Flux.fromIterable(causeEntities)
                    .flatMap(causeEstimateRepository::save)
                    .then(saveEconomic(savedLoss, plan, rate, totalLossWh, finalAttributed, residualUnknownWh, assessedAt, request));
            });
    }

    private Mono<EconomicEvaluateResponse> saveEconomic(
        LossAssessment savedLoss,
        TariffPlan plan,
        TariffRate rate,
        BigDecimal totalLossWh,
        BigDecimal attributedWh,
        BigDecimal residualUnknownWh,
        Instant assessedAt,
        EconomicEvaluateRequest request
    ) {
        BigDecimal exportPrice = rate.getExportPricePerKwh() != null ? rate.getExportPricePerKwh() : BigDecimal.ZERO;
        BigDecimal purchasePrice = rate.getPurchasePricePerKwh();
        BigDecimal totalKwh = totalLossWh.divide(WH_PER_KWH, 6, RoundingMode.HALF_UP);

        boolean hasCurve = request.selfConsumptionLostWh() != null && request.exportedLostWh() != null;
        BigDecimal exactAmount;
        List<EconomicScenario> scenarios;
        String snapshotNote;

        if (hasCurve) {
            BigDecimal selfKwh = request.selfConsumptionLostWh().divide(WH_PER_KWH, 6, RoundingMode.HALF_UP);
            BigDecimal expKwh = request.exportedLostWh().divide(WH_PER_KWH, 6, RoundingMode.HALF_UP);
            exactAmount = selfKwh.multiply(purchasePrice).add(expKwh.multiply(exportPrice)).setScale(2, RoundingMode.HALF_UP);
            scenarios = List.of();
            snapshotNote = "EXACT_CURVE_RATE_ID=" + rate.getId();
        } else {
            // RB-ECO-002 / T15: Missing self-consumption curve -> return scenario range with assumptions
            exactAmount = BigDecimal.ZERO;
            BigDecimal halfKwh = totalKwh.divide(new BigDecimal("2"), 6, RoundingMode.HALF_UP);
            scenarios = List.of(
                new EconomicScenario(
                    "ALL_EXPORTED",
                    "Assumes 100% of lost energy would have been exported at exportPricePerKwh",
                    BigDecimal.ZERO,
                    totalKwh,
                    totalKwh.multiply(exportPrice).setScale(2, RoundingMode.HALF_UP)
                ),
                new EconomicScenario(
                    "MIXED_50_50",
                    "Assumes 50% self-consumption (avoided purchase) and 50% export",
                    halfKwh,
                    halfKwh,
                    halfKwh.multiply(purchasePrice).add(halfKwh.multiply(exportPrice)).setScale(2, RoundingMode.HALF_UP)
                ),
                new EconomicScenario(
                    "ALL_SELF_CONSUMED",
                    "Assumes 100% of lost energy would have displaced grid purchase at purchasePricePerKwh",
                    totalKwh,
                    BigDecimal.ZERO,
                    totalKwh.multiply(purchasePrice).setScale(2, RoundingMode.HALF_UP)
                )
            );
            snapshotNote = "SCENARIO_RANGE_ONLY: missing self-consumption/export curve (rateId=" + rate.getId() + ")";
        }

        boolean cleaningPromised =
            request.cleaningCostEstimate() != null &&
            request.expectedBenefitDurationDays() != null &&
            request.expectedBenefitDurationDays() > 0 &&
            request.safetyCheckPassed() &&
            request.hasComparablePostRecoveryEvidence();

        String cleaningNote = cleaningPromised
            ? "Cleaning ROI backed by cost, benefit duration, safety check, and comparable post-recovery evidence"
            : "Cleaning financial recovery not promised without cost, duration, safety, and comparable post-recovery evidence";

        EconomicAssessment econ = new EconomicAssessment();
        econ.setOrganizationId(savedLoss.getOrganizationId());
        econ.setLossAssessmentId(savedLoss.getId());
        econ.setTariffRateId(rate.getId());
        econ.setAssessedAt(assessedAt);
        econ.setCurrencyCode(plan.getCurrencyCode());
        econ.setSelfConsumptionLostWh(hasCurve ? request.selfConsumptionLostWh() : null);
        econ.setExportedLostWh(hasCurve ? request.exportedLostWh() : null);
        econ.setEstimatedLossAmount(exactAmount);
        econ.setTariffSnapshotNote(snapshotNote);

        return economicAssessmentRepository
            .save(econ)
            .map(savedEcon ->
                new EconomicEvaluateResponse(
                    savedLoss.getId(),
                    savedEcon.getId(),
                    totalLossWh,
                    attributedWh,
                    residualUnknownWh,
                    plan.getCurrencyCode(),
                    hasCurve,
                    hasCurve ? exactAmount : null,
                    scenarios,
                    cleaningPromised,
                    cleaningNote
                )
            );
    }
}
