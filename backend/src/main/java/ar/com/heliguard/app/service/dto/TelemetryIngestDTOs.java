package ar.com.heliguard.app.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Request and response DTOs for M2M edge telemetry ingestion ({@code POST /api/ingest/telemetry}).
 */
public final class TelemetryIngestDTOs {

    private TelemetryIngestDTOs() {}

    public record TelemetryIngestRequest(@NotEmpty List<@Valid TelemetryEventPayload> events) implements Serializable {}

    public record TelemetryEventPayload(
        @NotBlank String eventId,
        @NotBlank String bootId,
        @NotNull @PositiveOrZero Long sequenceNumber,
        @NotBlank String metricCode,
        @NotBlank String unitCode,
        @NotNull BigDecimal numericValue,
        @NotNull Instant observedAt,
        Long installationId,
        Long telemetryChannelId
    ) implements Serializable {}

    public record TelemetryEventResult(String eventId, String status, String qualityFlags) implements Serializable {}

    public record TelemetryIngestResponse(
        Long gatewayId,
        Long organizationId,
        Long installationId,
        int acceptedCount,
        int duplicateIdempotentCount,
        int quarantinedCount,
        List<TelemetryEventResult> results
    ) implements Serializable {}
}
