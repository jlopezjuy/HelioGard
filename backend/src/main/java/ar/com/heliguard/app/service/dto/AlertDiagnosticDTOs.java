package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTOs for Sprint S5 alert diagnostic & physical integrity evaluation endpoints.
 */
public final class AlertDiagnosticDTOs {

    private AlertDiagnosticDTOs() {}

    public record TelemetryAlertEvaluateRequest(
        @NotNull Long installationId,
        @NotNull Long alertRuleVersionId,
        @NotNull LocalizationScope affectedScope,
        BigDecimal irradianceWm2,
        BigDecimal actualPowerW,
        BigDecimal expectedPowerW,
        Long telemetryAgeSeconds,
        boolean weatherLimited,
        BigDecimal coveragePct,
        Long solarSectorId,
        Long solarStringId,
        Long equipmentId
    ) implements Serializable {}

    public record TelemetryAlertEvaluateResponse(
        String classification,
        boolean incidentCreatedOrUpdated,
        boolean deduplicated,
        Long incidentId,
        String dedupKey,
        LocalizationScope localizationScope
    ) implements Serializable {}

    public record PhysicalIntegrityEvaluateRequest(
        @NotNull Long installationId,
        @NotNull BigDecimal reportedPowerW,
        BigDecimal irradianceWm2,
        Instant observedAt,
        String detail
    ) implements Serializable {}

    public record PhysicalIntegrityEvaluateResponse(
        boolean physicallyConsistent,
        DataTrustLevel trustLevel,
        IntegrityEventType integrityEventType,
        boolean cyberAttackConfirmed,
        Long integrityEventId,
        Long integrityAssessmentId,
        String reason
    ) implements Serializable {}
}
