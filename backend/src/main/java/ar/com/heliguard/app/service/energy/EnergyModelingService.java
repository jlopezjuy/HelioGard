package ar.com.heliguard.app.service.energy;

import ar.com.heliguard.app.domain.EnergyIntervalResult;
import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.WeatherBatchStatus;
import ar.com.heliguard.app.repository.EnergyIntervalResultRepository;
import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyIntervalResultDTO;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyIntervalSample;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteRequest;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteResponse;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.tenant.PermissionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Versioned physical energy modeling service (RB-ENE-001..004, T10).
 * Never substitutes missing irradiance or incomplete configurations with zero expected energy.
 */
@Service
public class EnergyModelingService {

    private static final BigDecimal DEFAULT_PR = new BigDecimal("0.82");
    private static final BigDecimal UNCERTAINTY_RATIO = new BigDecimal("0.08");
    private static final BigDecimal TEMP_COEFF = new BigDecimal("0.004");
    private static final BigDecimal STC_TEMP_C = new BigDecimal("25");
    private static final BigDecimal NOCT_DELTA_C = new BigDecimal("20");
    private static final BigDecimal MIN_COVERAGE_PCT = new BigDecimal("50");

    private final PermissionService permissionService;
    private final EnergyModelConfigurationRepository configRepository;
    private final WeatherIngestionBatchRepository weatherBatchRepository;
    private final EnergyModelRunRepository runRepository;
    private final EnergyIntervalResultRepository intervalRepository;

    public EnergyModelingService(
        PermissionService permissionService,
        EnergyModelConfigurationRepository configRepository,
        WeatherIngestionBatchRepository weatherBatchRepository,
        EnergyModelRunRepository runRepository,
        EnergyIntervalResultRepository intervalRepository
    ) {
        this.permissionService = permissionService;
        this.configRepository = configRepository;
        this.weatherBatchRepository = weatherBatchRepository;
        this.runRepository = runRepository;
        this.intervalRepository = intervalRepository;
    }

    @Transactional
    public Mono<EnergyModelExecuteResponse> execute(EnergyModelExecuteRequest request) {
        return permissionService
            .validateTemporalWindow(request.periodStart(), request.periodEnd())
            .then(permissionService.requireAccessToInstallation(request.installationId(), PermissionScope.AUDIT))
            .flatMap(installation ->
                loadOptionalConfig(request.modelConfigurationId(), installation.getOrganizationId(), installation.getId())
                    .zipWith(loadOptionalWeatherBatch(request.weatherBatchId(), installation.getOrganizationId(), installation.getId()))
                    .flatMap(tuple -> runCalculation(installation, tuple.getT1().orElse(null), tuple.getT2().orElse(null), request))
            );
    }

    private Mono<java.util.Optional<EnergyModelConfiguration>> loadOptionalConfig(Long configId, Long orgId, Long instId) {
        if (configId == null) {
            return Mono.just(java.util.Optional.empty());
        }
        return configRepository
            .findById(configId)
            .filter(c -> orgId.equals(c.getOrganizationId()) && instId.equals(c.getInstallationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "ENERGY_CONFIG_NOT_IN_INSTALLATION",
                        "EnergyModelConfiguration not found for installation"
                    )
                )
            )
            .map(java.util.Optional::of);
    }

    private Mono<java.util.Optional<WeatherIngestionBatch>> loadOptionalWeatherBatch(Long batchId, Long orgId, Long instId) {
        if (batchId == null) {
            return Mono.just(java.util.Optional.empty());
        }
        return weatherBatchRepository
            .findById(batchId)
            .filter(b -> orgId.equals(b.getOrganizationId()) && instId.equals(b.getInstallationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "WEATHER_BATCH_NOT_IN_INSTALLATION",
                        "WeatherIngestionBatch not found for installation"
                    )
                )
            )
            .map(java.util.Optional::of);
    }

    private Mono<EnergyModelExecuteResponse> runCalculation(
        Installation installation,
        EnergyModelConfiguration config,
        WeatherIngestionBatch weatherBatch,
        EnergyModelExecuteRequest request
    ) {
        Instant now = Instant.now();
        List<EnergyIntervalSample> samples = request.samples() != null ? request.samples() : List.of();
        long validIrradianceCount = samples
            .stream()
            .filter(s -> s.irradianceWm2() != null && s.irradianceWm2().compareTo(BigDecimal.ZERO) >= 0)
            .count();

        BigDecimal coveragePct = samples.isEmpty()
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(validIrradianceCount * 100.0 / samples.size()).setScale(2, RoundingMode.HALF_UP);

        boolean weatherValid = weatherBatch != null && weatherBatch.getStatus() == WeatherBatchStatus.VALIDATED;
        boolean sufficientData =
            config != null &&
            weatherValid &&
            !samples.isEmpty() &&
            coveragePct.compareTo(MIN_COVERAGE_PCT) >= 0;

        if (!sufficientData) {
            EnergyModelRun run = new EnergyModelRun();
            run.setOrganizationId(installation.getOrganizationId());
            run.setInstallationId(installation.getId());
            run.setModelConfigurationId(config != null ? config.getId() : null);
            run.setWeatherBatchId(weatherBatch != null ? weatherBatch.getId() : null);
            run.setModelName(config != null ? config.getAlgorithmCode() : "PV_BASELINE_V1");
            run.setModelVersion(config != null ? config.getConfigVersion() : "1.0");
            run.setPeriodStart(request.periodStart());
            run.setPeriodEnd(request.periodEnd());
            run.setStatus(EnergyModelRunStatus.INSUFFICIENT_DATA);
            run.setExpectedWh(null);
            run.setExpectedLowerWh(null);
            run.setExpectedUpperWh(null);
            run.setActualWh(null);
            run.setCoveragePct(coveragePct);
            run.setQualityNote("INSUFFICIENT_DATA: missing validated weather batch, configuration, or sufficient irradiance");
            run.setCalculatedAt(now);

            return runRepository
                .save(run)
                .map(saved ->
                    new EnergyModelExecuteResponse(
                        saved.getId(),
                        saved.getInstallationId(),
                        saved.getStatus(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        saved.getCoveragePct(),
                        false,
                        saved.getQualityNote(),
                        List.of()
                    )
                );
        }

        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalLower = BigDecimal.ZERO;
        BigDecimal totalUpper = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalDeficit = BigDecimal.ZERO;
        List<EnergyIntervalResult> intervalEntities = new ArrayList<>();

        for (EnergyIntervalSample sample : samples) {
            if (!sample.intervalEnd().isAfter(sample.intervalStart())) {
                return Mono.error(
                    new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_INTERVAL_WINDOW", "intervalEnd must be after intervalStart")
                );
            }
            double hours = Duration.between(sample.intervalStart(), sample.intervalEnd()).toSeconds() / 3600.0;
            EnergyIntervalResult entity = new EnergyIntervalResult();
            entity.setOrganizationId(installation.getOrganizationId());
            entity.setInstallationId(installation.getId());
            entity.setIntervalStart(sample.intervalStart());
            entity.setIntervalEnd(sample.intervalEnd());
            entity.setIrradianceWm2(sample.irradianceWm2());
            entity.setAmbientTempC(sample.ambientTempC());

            if (sample.irradianceWm2() == null || sample.irradianceWm2().compareTo(BigDecimal.ZERO) < 0) {
                entity.setIntervalStatus("INSUFFICIENT_DATA");
                entity.setSoilingEvidenceEligible(false);
                intervalEntities.add(entity);
                continue;
            }

            BigDecimal cellTemp = (sample.ambientTempC() != null ? sample.ambientTempC() : STC_TEMP_C).add(NOCT_DELTA_C);
            BigDecimal tempDelta = cellTemp.subtract(STC_TEMP_C).max(BigDecimal.ZERO);
            BigDecimal tempFactor = BigDecimal.ONE.subtract(TEMP_COEFF.multiply(tempDelta)).max(new BigDecimal("0.60"));

            BigDecimal expectedWh = installation
                .getNominalDcKw()
                .multiply(sample.irradianceWm2())
                .multiply(BigDecimal.valueOf(hours))
                .multiply(DEFAULT_PR)
                .multiply(tempFactor)
                .setScale(4, RoundingMode.HALF_UP);
            BigDecimal lowerWh = expectedWh.multiply(BigDecimal.ONE.subtract(UNCERTAINTY_RATIO)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal upperWh = expectedWh.multiply(BigDecimal.ONE.add(UNCERTAINTY_RATIO)).setScale(4, RoundingMode.HALF_UP);

            entity.setExpectedWh(expectedWh);
            entity.setExpectedLowerWh(lowerWh);
            entity.setExpectedUpperWh(upperWh);

            // RB-ENE-003: Power vs Energy & Meter Reset detection
            boolean meterReset =
                sample.meterCounterStartWh() != null &&
                sample.meterCounterEndWh() != null &&
                sample.meterCounterEndWh().compareTo(sample.meterCounterStartWh()) < 0;

            if (meterReset) {
                entity.setIntervalStatus("METER_RESET_DETECTED");
                entity.setSoilingEvidenceEligible(false);
                intervalEntities.add(entity);
                continue;
            }

            BigDecimal actualWh = resolveActualWh(sample, hours);
            entity.setActualWh(actualWh);

            if (sample.cloudyOrCold()) {
                entity.setIntervalStatus("WEATHER_LIMITED");
                entity.setDeficitWh(BigDecimal.ZERO);
                entity.setSoilingEvidenceEligible(false);
            } else {
                entity.setIntervalStatus("COMPARABLE");
                BigDecimal deficit = actualWh != null ? expectedWh.subtract(actualWh).max(BigDecimal.ZERO) : BigDecimal.ZERO;
                entity.setDeficitWh(deficit);
                boolean soilingEligible = request.hasSoilingTrendOrInspectionEvidence() && deficit.compareTo(BigDecimal.ZERO) > 0;
                entity.setSoilingEvidenceEligible(soilingEligible);

                totalExpected = totalExpected.add(expectedWh);
                totalLower = totalLower.add(lowerWh);
                totalUpper = totalUpper.add(upperWh);
                if (actualWh != null) {
                    totalActual = totalActual.add(actualWh);
                }
                totalDeficit = totalDeficit.add(deficit);
            }
            intervalEntities.add(entity);
        }

        boolean soilingAllowed =
            request.hasSoilingTrendOrInspectionEvidence() && totalDeficit.compareTo(BigDecimal.ZERO) > 0;

        EnergyModelRun run = new EnergyModelRun();
        run.setOrganizationId(installation.getOrganizationId());
        run.setInstallationId(installation.getId());
        run.setModelConfigurationId(config.getId());
        run.setWeatherBatchId(weatherBatch.getId());
        run.setModelName(config.getAlgorithmCode());
        run.setModelVersion(config.getConfigVersion());
        run.setPeriodStart(request.periodStart());
        run.setPeriodEnd(request.periodEnd());
        run.setStatus(EnergyModelRunStatus.SUCCEEDED);
        run.setExpectedWh(totalExpected);
        run.setExpectedLowerWh(totalLower);
        run.setExpectedUpperWh(totalUpper);
        run.setActualWh(totalActual);
        run.setCoveragePct(coveragePct);
        run.setQualityNote("SUCCEEDED");
        run.setCalculatedAt(now);

        BigDecimal finalDeficit = totalDeficit;
        return runRepository
            .save(run)
            .flatMap(savedRun -> {
                intervalEntities.forEach(e -> e.setModelRunId(savedRun.getId()));
                return Flux.fromIterable(intervalEntities)
                    .flatMap(intervalRepository::save)
                    .collectList()
                    .map(savedIntervals ->
                        new EnergyModelExecuteResponse(
                            savedRun.getId(),
                            savedRun.getInstallationId(),
                            savedRun.getStatus(),
                            savedRun.getExpectedWh(),
                            savedRun.getExpectedLowerWh(),
                            savedRun.getExpectedUpperWh(),
                            savedRun.getActualWh(),
                            finalDeficit,
                            savedRun.getCoveragePct(),
                            soilingAllowed,
                            savedRun.getQualityNote(),
                            savedIntervals
                                .stream()
                                .map(i ->
                                    new EnergyIntervalResultDTO(
                                        i.getIntervalStart(),
                                        i.getIntervalEnd(),
                                        i.getExpectedWh(),
                                        i.getExpectedLowerWh(),
                                        i.getExpectedUpperWh(),
                                        i.getActualWh(),
                                        i.getDeficitWh(),
                                        i.getIntervalStatus(),
                                        Boolean.TRUE.equals(i.getSoilingEvidenceEligible())
                                    )
                                )
                                .toList()
                        )
                    );
            });
    }

    private static BigDecimal resolveActualWh(EnergyIntervalSample sample, double hours) {
        if (sample.actualEnergyWh() != null) {
            return sample.actualEnergyWh().setScale(4, RoundingMode.HALF_UP);
        }
        if (sample.meterCounterStartWh() != null && sample.meterCounterEndWh() != null) {
            return sample.meterCounterEndWh().subtract(sample.meterCounterStartWh()).setScale(4, RoundingMode.HALF_UP);
        }
        if (sample.actualPowerW() != null) {
            return sample.actualPowerW().multiply(BigDecimal.valueOf(hours)).setScale(4, RoundingMode.HALF_UP);
        }
        return null;
    }
}
