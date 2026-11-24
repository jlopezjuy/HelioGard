package ar.com.heliguard.app.service.edge;

import ar.com.heliguard.app.domain.DeviceSequenceCheckpoint;
import ar.com.heliguard.app.domain.TelemetryIngestEvent;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.repository.DeviceSequenceCheckpointRepository;
import ar.com.heliguard.app.repository.TelemetryIngestEventRepository;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryEventPayload;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryEventResult;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestRequest;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestResponse;
import ar.com.heliguard.app.service.edge.DeviceAuthService.AuthenticatedDevice;
import ar.com.heliguard.app.service.error.DomainRuleException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Idempotent M2M telemetry ingestion service enforcing tenant binding,
 * payload-hash replay vs duplicate distinction, and sequence/clock-skew checks
 * (RB-TEL-001..005, RB-SEC-001..003).
 */
@Service
public class EdgeIngestionService {

    private static final Duration MAX_FUTURE_CLOCK_SKEW = Duration.ofMinutes(5);

    private final DeviceAuthService deviceAuthService;
    private final TelemetryIngestEventRepository ingestRepository;
    private final DeviceSequenceCheckpointRepository checkpointRepository;
    private final TelemetryIntegrityEventRepository integrityEventRepository;
    private final TransactionalOperator transactionalOperator;

    public EdgeIngestionService(
        DeviceAuthService deviceAuthService,
        TelemetryIngestEventRepository ingestRepository,
        DeviceSequenceCheckpointRepository checkpointRepository,
        TelemetryIntegrityEventRepository integrityEventRepository,
        TransactionalOperator transactionalOperator
    ) {
        this.deviceAuthService = deviceAuthService;
        this.ingestRepository = ingestRepository;
        this.checkpointRepository = checkpointRepository;
        this.integrityEventRepository = integrityEventRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<TelemetryIngestResponse> ingest(String deviceUid, String rawSecret, TelemetryIngestRequest request) {
        return deviceAuthService
            .authenticateDevice(deviceUid, rawSecret)
            .flatMap(device ->
                Flux.fromIterable(request.events())
                    .concatMap(event -> processSingleEvent(device, event))
                    .collectList()
                    .map(results -> summarize(device, results))
            );
    }

    private Mono<TelemetryEventResult> processSingleEvent(AuthenticatedDevice device, TelemetryEventPayload payload) {
        if (payload.installationId() != null && !payload.installationId().equals(device.installationId())) {
            return recordIntegrityEvent(
                device,
                IntegrityEventType.AUTH_FAILURE,
                IntegrityEventStatus.OBSERVED,
                payload,
                null,
                "Foreign installationId " + payload.installationId() + " declared by gateway " + device.gatewayId()
            )
                .then(
                    Mono.error(
                        new DomainRuleException(
                            HttpStatus.FORBIDDEN,
                            "FOREIGN_INSTALLATION_FORBIDDEN",
                            "Gateway is not authorized for installation " + payload.installationId()
                        )
                    )
                );
        }

        String normalizedMetric = payload.metricCode().trim().toUpperCase(Locale.ROOT);
        String normalizedUnit = payload.unitCode().trim();
        String eventId = payload.eventId().trim();
        String bootId = payload.bootId().trim();
        String payloadSha256 = computePayloadSha256(eventId, bootId, payload, normalizedMetric, normalizedUnit);

        return ingestRepository
            .findByGatewayIdAndEventId(device.gatewayId(), eventId)
            .flatMap(existing -> {
                if (existing.getPayloadSha256().equals(payloadSha256)) {
                    return Mono.just(new TelemetryEventResult(eventId, "DUPLICATE_IDEMPOTENT", existing.getQualityFlags()));
                }
                return recordIntegrityEvent(
                    device,
                    IntegrityEventType.REPLAY,
                    IntegrityEventStatus.QUARANTINED,
                    payload,
                    payloadSha256,
                    "Same eventId with different payload hash; quarantined without mutating original event"
                )
                    .thenReturn(new TelemetryEventResult(eventId, "QUARANTINED", "HASH_MISMATCH"));
            })
            .switchIfEmpty(
                Mono.defer(() ->
                    transactionalOperator.transactional(
                        persistNewEvent(device, payload, eventId, bootId, normalizedMetric, normalizedUnit, payloadSha256)
                    )
                )
            );
    }

    private Mono<TelemetryEventResult> persistNewEvent(
        AuthenticatedDevice device,
        TelemetryEventPayload payload,
        String eventId,
        String bootId,
        String normalizedMetric,
        String normalizedUnit,
        String payloadSha256
    ) {
        Instant now = Instant.now();
        return checkpointRepository
            .findByGatewayIdAndDeviceIdentityId(device.gatewayId(), device.deviceIdentityId())
            .flatMap(cp -> evaluateSequenceAndSave(device, payload, eventId, bootId, normalizedMetric, normalizedUnit, payloadSha256, now, cp))
            .switchIfEmpty(
                Mono.defer(() -> {
                    DeviceSequenceCheckpoint fresh = new DeviceSequenceCheckpoint();
                    fresh.setOrganizationId(device.organizationId());
                    fresh.setGatewayId(device.gatewayId());
                    fresh.setDeviceIdentityId(device.deviceIdentityId());
                    fresh.setBootId(bootId);
                    fresh.setLastSequenceNumber(payload.sequenceNumber());
                    fresh.setLastObservedAt(payload.observedAt());
                    fresh.setUpdatedAt(now);
                    return evaluateClockSkew(device, payload, payloadSha256, now)
                        .flatMap(flags ->
                            checkpointRepository
                                .save(fresh)
                                .then(saveAcceptedEvent(device, payload, eventId, bootId, normalizedMetric, normalizedUnit, payloadSha256, now, flags))
                        );
                })
            );
    }

    private Mono<TelemetryEventResult> evaluateSequenceAndSave(
        AuthenticatedDevice device,
        TelemetryEventPayload payload,
        String eventId,
        String bootId,
        String normalizedMetric,
        String normalizedUnit,
        String payloadSha256,
        Instant now,
        DeviceSequenceCheckpoint cp
    ) {
        List<String> flags = new ArrayList<>();
        Mono<Void> extraEvents = Mono.empty();

        if (!bootId.equals(cp.getBootId())) {
            flags.add("REBOOT_DETECTED");
            cp.setBootId(bootId);
            cp.setLastSequenceNumber(payload.sequenceNumber());
            cp.setLastObservedAt(payload.observedAt());
        } else if (payload.sequenceNumber() > cp.getLastSequenceNumber() + 1) {
            flags.add("SEQUENCE_GAP");
            cp.setLastSequenceNumber(payload.sequenceNumber());
            cp.setLastObservedAt(payload.observedAt());
            extraEvents = recordIntegrityEvent(
                device,
                IntegrityEventType.SEQUENCE_GAP,
                IntegrityEventStatus.OBSERVED,
                payload,
                payloadSha256,
                "Sequence gap from " + cp.getLastSequenceNumber() + " to " + payload.sequenceNumber()
            );
        } else if (payload.sequenceNumber() <= cp.getLastSequenceNumber()) {
            flags.add("OUT_OF_ORDER");
        } else {
            cp.setLastSequenceNumber(payload.sequenceNumber());
            cp.setLastObservedAt(payload.observedAt());
        }
        cp.setUpdatedAt(now);

        return extraEvents
            .then(evaluateClockSkew(device, payload, payloadSha256, now))
            .flatMap(skewFlag -> {
                if (skewFlag != null && !skewFlag.isBlank()) {
                    flags.add(skewFlag);
                }
                String combinedFlags = flags.isEmpty() ? null : String.join(",", flags);
                return checkpointRepository
                    .save(cp)
                    .then(
                        saveAcceptedEvent(
                            device,
                            payload,
                            eventId,
                            bootId,
                            normalizedMetric,
                            normalizedUnit,
                            payloadSha256,
                            now,
                            combinedFlags
                        )
                    );
            });
    }

    private Mono<String> evaluateClockSkew(
        AuthenticatedDevice device,
        TelemetryEventPayload payload,
        String payloadSha256,
        Instant now
    ) {
        if (payload.observedAt().isAfter(now.plus(MAX_FUTURE_CLOCK_SKEW))) {
            return recordIntegrityEvent(
                device,
                IntegrityEventType.CLOCK_SKEW,
                IntegrityEventStatus.OBSERVED,
                payload,
                payloadSha256,
                "observedAt exceeds server time by more than 5 minutes"
            )
                .thenReturn("CLOCK_SKEW");
        }
        return Mono.just("");
    }

    private Mono<TelemetryEventResult> saveAcceptedEvent(
        AuthenticatedDevice device,
        TelemetryEventPayload payload,
        String eventId,
        String bootId,
        String normalizedMetric,
        String normalizedUnit,
        String payloadSha256,
        Instant now,
        String flags
    ) {
        String cleanFlags = (flags == null || flags.isBlank()) ? null : flags;
        TelemetryIngestEvent entity = new TelemetryIngestEvent();
        entity.setOrganizationId(device.organizationId());
        entity.setInstallationId(device.installationId());
        entity.setGatewayId(device.gatewayId());
        entity.setDeviceIdentityId(device.deviceIdentityId());
        entity.setTelemetryChannelId(payload.telemetryChannelId());
        entity.setEventId(eventId);
        entity.setBootId(bootId);
        entity.setSequenceNumber(payload.sequenceNumber());
        entity.setMetricCode(normalizedMetric);
        entity.setUnitCode(normalizedUnit);
        entity.setNumericValue(payload.numericValue());
        entity.setObservedAt(payload.observedAt());
        entity.setReceivedAt(now);
        entity.setPayloadSha256(payloadSha256);
        entity.setIngestStatus("ACCEPTED");
        entity.setQualityFlags(cleanFlags);
        return ingestRepository.save(entity).map(saved -> new TelemetryEventResult(saved.getEventId(), "ACCEPTED", cleanFlags));
    }

    private Mono<Void> recordIntegrityEvent(
        AuthenticatedDevice device,
        IntegrityEventType type,
        IntegrityEventStatus status,
        TelemetryEventPayload payload,
        String sha256,
        String detail
    ) {
        TelemetryIntegrityEvent ev = new TelemetryIntegrityEvent();
        ev.setEventUid(type.name().toLowerCase(Locale.ROOT) + "-" + UUID.randomUUID());
        ev.setEventType(type);
        ev.setStatus(status);
        ev.setObservedAt(Instant.now());
        ev.setSourceObservedAt(payload.observedAt());
        ev.setBootId(payload.bootId());
        ev.setSequenceNumber(payload.sequenceNumber());
        ev.setPayloadSha256(sha256);
        ev.setOrganizationId(device.organizationId());
        ev.setInstallationId(device.installationId());
        ev.setDeviceIdentityId(device.deviceIdentityId());
        ev.setTelemetryChannelId(payload.telemetryChannelId());
        ev.setDetail(detail);
        return integrityEventRepository.save(ev).then();
    }

    private static String computePayloadSha256(
        String eventId,
        String bootId,
        TelemetryEventPayload payload,
        String normalizedMetric,
        String normalizedUnit
    ) {
        String canonical =
            eventId +
            "|" +
            bootId +
            "|" +
            payload.sequenceNumber() +
            "|" +
            normalizedMetric +
            "|" +
            normalizedUnit +
            "|" +
            payload.numericValue().stripTrailingZeros().toPlainString() +
            "|" +
            payload.observedAt() +
            "|" +
            (payload.telemetryChannelId() != null ? payload.telemetryChannelId() : "");
        return DeviceAuthService.sha256Hex(canonical);
    }

    private static TelemetryIngestResponse summarize(AuthenticatedDevice device, List<TelemetryEventResult> results) {
        int accepted = 0;
        int duplicates = 0;
        int quarantined = 0;
        for (TelemetryEventResult r : results) {
            switch (r.status()) {
                case "ACCEPTED" -> accepted++;
                case "DUPLICATE_IDEMPOTENT" -> duplicates++;
                case "QUARANTINED" -> quarantined++;
                default -> {}
            }
        }
        return new TelemetryIngestResponse(
            device.gatewayId(),
            device.organizationId(),
            device.installationId(),
            accepted,
            duplicates,
            quarantined,
            results
        );
    }
}
