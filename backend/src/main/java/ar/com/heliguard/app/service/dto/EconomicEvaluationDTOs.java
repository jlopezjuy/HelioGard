package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.LossCause;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTOs for Sprint S6 loss attribution and economic evaluation ({@code POST /api/economic-assessments/evaluate}).
 */
public final class EconomicEvaluationDTOs {

    private EconomicEvaluationDTOs() {}

    public record CauseAllocation(
        @NotNull LossCause cause,
        @NotNull BigDecimal estimatedWh,
        @NotNull String method,
        String evidenceNote
    ) implements Serializable {}

    public record EconomicEvaluateRequest(
        @NotNull Long modelRunId,
        @NotNull Long tariffPlanId,
        Instant assessedAt,
        List<@Valid CauseAllocation> causes,
        BigDecimal selfConsumptionLostWh,
        BigDecimal exportedLostWh,
        BigDecimal cleaningCostEstimate,
        Integer expectedBenefitDurationDays,
        boolean safetyCheckPassed,
        boolean hasComparablePostRecoveryEvidence
    ) implements Serializable {}

    public record EconomicScenario(
        String label,
        String assumption,
        BigDecimal selfConsumptionKwh,
        BigDecimal exportedKwh,
        BigDecimal estimatedAmount
    ) implements Serializable {}

    public record EconomicEvaluateResponse(
        Long lossAssessmentId,
        Long economicAssessmentId,
        BigDecimal totalLossWh,
        BigDecimal attributedWh,
        BigDecimal residualUnknownWh,
        String currencyCode,
        boolean exactAmountAvailable,
        BigDecimal estimatedLossAmount,
        List<EconomicScenario> scenarios,
        boolean cleaningRecoveryPromised,
        String cleaningNote
    ) implements Serializable {}
}
