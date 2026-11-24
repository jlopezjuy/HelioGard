package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Request and response DTOs for {@code POST /api/energy-runs/execute} (Sprint S4).
 */
public final class EnergyModelingDTOs {

    private EnergyModelingDTOs() {}

    public record EnergyIntervalSample(
        @NotNull Instant intervalStart,
        @NotNull Instant intervalEnd,
        BigDecimal irradianceWm2,
        BigDecimal ambientTempC,
        BigDecimal actualPowerW,
        BigDecimal actualEnergyWh,
        BigDecimal meterCounterStartWh,
        BigDecimal meterCounterEndWh,
        boolean cloudyOrCold
    ) implements Serializable {}

    public record EnergyModelExecuteRequest(
        @NotNull Long installationId,
        Long modelConfigurationId,
        Long weatherBatchId,
        @NotNull Instant periodStart,
        @NotNull Instant periodEnd,
        List<@Valid EnergyIntervalSample> samples,
        boolean hasSoilingTrendOrInspectionEvidence
    ) implements Serializable {}

    public record EnergyIntervalResultDTO(
        Instant intervalStart,
        Instant intervalEnd,
        BigDecimal expectedWh,
        BigDecimal expectedLowerWh,
        BigDecimal expectedUpperWh,
        BigDecimal actualWh,
        BigDecimal deficitWh,
        String intervalStatus,
        boolean soilingEvidenceEligible
    ) implements Serializable {}

    public record EnergyModelExecuteResponse(
        Long runId,
        Long installationId,
        EnergyModelRunStatus status,
        BigDecimal expectedWh,
        BigDecimal expectedLowerWh,
        BigDecimal expectedUpperWh,
        BigDecimal actualWh,
        BigDecimal deficitWh,
        BigDecimal coveragePct,
        boolean soilingHypothesisAllowed,
        String qualityNote,
        List<EnergyIntervalResultDTO> intervals
    ) implements Serializable {}
}
