package ar.com.heliguard.app.service.integrity;

import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentEvent;
import ar.com.heliguard.app.domain.IncidentLocalization;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.repository.IncidentEventRepository;
import ar.com.heliguard.app.repository.IncidentLocalizationRepository;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.repository.IntegrityAssessmentRepository;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateResponse;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateResponse;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.tenant.PermissionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Evaluates telemetry alert conditions (RB-TEL-006, RB-INC-001..003, T11, T12)
 * and physical plausibility without conflating anomalies with confirmed cyberattacks (RB-SEC-002, T13).
 */
@Service
@Transactional
public class AlertDiagnosticService {

    private static final BigDecimal NIGHT_IRRADIANCE_THRESHOLD = new BigDecimal("10");
    private static final long DEFAULT_STALE_SECONDS = 900L;
    private static final BigDecimal OVER_CAPACITY_FACTOR = new BigDecimal("1.25");

    private final PermissionService permissionService;
    private final AlertRuleVersionRepository ruleVersionRepository;
    private final IncidentRepository incidentRepository;
    private final IncidentEventRepository incidentEventRepository;
    private final IncidentLocalizationRepository localizationRepository;
    private final TelemetryIntegrityEventRepository integrityEventRepository;
    private final IntegrityAssessmentRepository integrityAssessmentRepository;

    public AlertDiagnosticService(
        PermissionService permissionService,
        AlertRuleVersionRepository ruleVersionRepository,
        IncidentRepository incidentRepository,
        IncidentEventRepository incidentEventRepository,
        IncidentLocalizationRepository localizationRepository,
        TelemetryIntegrityEventRepository integrityEventRepository,
        IntegrityAssessmentRepository integrityAssessmentRepository
    ) {
        this.permissionService = permissionService;
        this.ruleVersionRepository = ruleVersionRepository;
        this.incidentRepository = incidentRepository;
        this.incidentEventRepository = incidentEventRepository;
        this.localizationRepository = localizationRepository;
        this.integrityEventRepository = integrityEventRepository;
        this.integrityAssessmentRepository = integrityAssessmentRepository;
    }

    public Mono<TelemetryAlertEvaluateResponse> evaluateAlert(TelemetryAlertEvaluateRequest request) {
        return permissionService
            .requireAccessToInstallation(request.installationId(), PermissionScope.MAINTENANCE)
            .flatMap(installation ->
                ruleVersionRepository
                    .findById(request.alertRuleVersionId())
                    .filter(rv -> installation.getOrganizationId().equals(rv.getOrganizationId()))
                    .switchIfEmpty(
                        Mono.error(
                            new DomainRuleException(
                                HttpStatus.NOT_FOUND,
                                "ALERT_RULE_VERSION_NOT_FOUND",
                                "AlertRuleVersion not found in tenant"
                            )
                        )
                    )
                    .flatMap(ruleVersion -> evaluateCondition(installation, ruleVersion, request))
            );
    }

    public Mono<PhysicalIntegrityEvaluateResponse> evaluatePhysicalIntegrity(PhysicalIntegrityEvaluateRequest request) {
        return permissionService
            .requireAccessToInstallation(request.installationId(), PermissionScope.AUDIT)
            .flatMap(installation -> {
                BigDecimal maxPlausibleW = installation.getNominalDcKw().multiply(new BigDecimal("1000")).multiply(OVER_CAPACITY_FACTOR);
                boolean negativePower = request.reportedPowerW().compareTo(BigDecimal.ZERO) < 0;
                boolean overCapacity = request.reportedPowerW().compareTo(maxPlausibleW) > 0;
                boolean nightProduction =
                    request.irradianceWm2() != null &&
                    request.irradianceWm2().compareTo(NIGHT_IRRADIANCE_THRESHOLD) <= 0 &&
                    request.reportedPowerW().compareTo(new BigDecimal("50")) > 0;

                if (!negativePower && !overCapacity && !nightProduction) {
                    return Mono.just(
                        new PhysicalIntegrityEvaluateResponse(
                            true,
                            DataTrustLevel.VALIDATED,
                            null,
                            false,
                            null,
                            null,
                            "Reading is within physical installation bounds"
                        )
                    );
                }

                String reason = negativePower
                    ? "Negative reported power is physically inconsistent"
                    : (overCapacity
                        ? "Reported power exceeds 125% of nominal DC capacity"
                        : "Significant reported power during nighttime/zero irradiance");

                Instant now = Instant.now();
                TelemetryIntegrityEvent ev = new TelemetryIntegrityEvent();
                ev.setEventUid("phys-" + UUID.randomUUID());
                ev.setEventType(IntegrityEventType.PHYSICAL_INCONSISTENCY);
                ev.setStatus(IntegrityEventStatus.UNDER_REVIEW);
                ev.setObservedAt(now);
                ev.setSourceObservedAt(request.observedAt());
                ev.setOrganizationId(installation.getOrganizationId());
                ev.setInstallationId(installation.getId());
                ev.setDetail(request.detail() != null ? request.detail() : reason);

                return integrityEventRepository
                    .save(ev)
                    .flatMap(savedEv -> {
                        IntegrityAssessment assessment = new IntegrityAssessment();
                        assessment.setOrganizationId(installation.getOrganizationId());
                        assessment.setInstallationId(installation.getId());
                        assessment.setIntegrityEventId(savedEv.getId());
                        assessment.setAssessedAt(now);
                        assessment.setTrustLevel(DataTrustLevel.SUSPECT);
                        assessment.setRuleVersion("INTEGRITY_V1");
                        assessment.setReason(reason + " (classified SUSPECT, not confirmed cyberattack)");
                        return integrityAssessmentRepository
                            .save(assessment)
                            .map(savedAssess ->
                                new PhysicalIntegrityEvaluateResponse(
                                    false,
                                    DataTrustLevel.SUSPECT,
                                    IntegrityEventType.PHYSICAL_INCONSISTENCY,
                                    false,
                                    savedEv.getId(),
                                    savedAssess.getId(),
                                    savedAssess.getReason()
                                )
                            );
                    });
            });
    }

    private Mono<TelemetryAlertEvaluateResponse> evaluateCondition(
        Installation installation,
        AlertRuleVersion ruleVersion,
        TelemetryAlertEvaluateRequest request
    ) {
        // T11: Nighttime / no light -> NO_PRODUCTION_NIGHT, never emit low-production incident
        if (request.irradianceWm2() != null && request.irradianceWm2().compareTo(NIGHT_IRRADIANCE_THRESHOLD) <= 0) {
            return Mono.just(
                new TelemetryAlertEvaluateResponse("NO_PRODUCTION_NIGHT", false, false, null, null, request.affectedScope())
            );
        }

        if (request.weatherLimited()) {
            return Mono.just(
                new TelemetryAlertEvaluateResponse("WEATHER_LIMITED", false, false, null, null, request.affectedScope())
            );
        }

        // T12: Stale / unreachable telemetry -> TELEMETRY_OFFLINE (not PANEL_FAILURE)
        long maxStale = ruleVersion.getWindowSeconds() != null ? ruleVersion.getWindowSeconds() : DEFAULT_STALE_SECONDS;
        if (request.telemetryAgeSeconds() != null && request.telemetryAgeSeconds() > maxStale) {
            String dedupKey = buildDedupKey(installation.getId(), ruleVersion.getId(), request.affectedScope(), "TELEMETRY_OFFLINE");
            return upsertDeduplicatedIncident(
                installation,
                ruleVersion,
                dedupKey,
                "TELEMETRY_OFFLINE",
                "Telemetry offline / stale data detected",
                AlertSeverity.MEDIUM,
                request
            );
        }

        if (
            ruleVersion.getMinCoveragePct() != null &&
            request.coveragePct() != null &&
            request.coveragePct().compareTo(ruleVersion.getMinCoveragePct()) < 0
        ) {
            return Mono.just(
                new TelemetryAlertEvaluateResponse("INSUFFICIENT_COVERAGE", false, false, null, null, request.affectedScope())
            );
        }

        boolean faultTriggered =
            request.expectedPowerW() != null &&
            request.actualPowerW() != null &&
            request.expectedPowerW().compareTo(BigDecimal.ZERO) > 0 &&
            request.actualPowerW().compareTo(request.expectedPowerW().multiply(new BigDecimal("0.50"))) < 0;

        if (!faultTriggered) {
            return Mono.just(new TelemetryAlertEvaluateResponse("NORMAL", false, false, null, null, request.affectedScope()));
        }

        String dedupKey = buildDedupKey(installation.getId(), ruleVersion.getId(), request.affectedScope(), "INVERTER_FAULT");
        return upsertDeduplicatedIncident(
            installation,
            ruleVersion,
            dedupKey,
            "INVERTER_FAULT",
            "Significant production drop detected",
            AlertSeverity.HIGH,
            request
        );
    }

    private Mono<TelemetryAlertEvaluateResponse> upsertDeduplicatedIncident(
        Installation installation,
        AlertRuleVersion ruleVersion,
        String dedupKey,
        String classification,
        String title,
        AlertSeverity severity,
        TelemetryAlertEvaluateRequest request
    ) {
        Instant now = Instant.now();
        return incidentRepository
            .findActiveByOrgAndDedupKey(installation.getOrganizationId(), dedupKey)
            .flatMap(existing -> {
                existing.setLastSeenAt(now);
                return incidentRepository
                    .save(existing)
                    .flatMap(saved ->
                        saveIncidentEvent(saved, now, classification, "Repeated observation deduplicated")
                            .thenReturn(
                                new TelemetryAlertEvaluateResponse(
                                    classification,
                                    true,
                                    true,
                                    saved.getId(),
                                    dedupKey,
                                    request.affectedScope()
                                )
                            )
                    );
            })
            .switchIfEmpty(
                Mono.defer(() -> {
                    Incident fresh = new Incident();
                    fresh.setOrganizationId(installation.getOrganizationId());
                    fresh.setInstallationId(installation.getId());
                    fresh.setAlertRuleVersionId(ruleVersion.getId());
                    fresh.setTitle(title);
                    fresh.setSeverity(severity);
                    fresh.setStatus(IncidentStatus.OPEN);
                    fresh.setFirstSeenAt(now);
                    fresh.setLastSeenAt(now);
                    fresh.setDedupKey(dedupKey);
                    return incidentRepository
                        .save(fresh)
                        .flatMap(saved ->
                            saveIncidentEvent(saved, now, classification, "Initial incident opened")
                                .then(saveLocalization(saved, now, request))
                                .thenReturn(
                                    new TelemetryAlertEvaluateResponse(
                                        classification,
                                        true,
                                        false,
                                        saved.getId(),
                                        dedupKey,
                                        request.affectedScope()
                                    )
                                )
                        );
                })
            );
    }

    private Mono<Void> saveIncidentEvent(Incident incident, Instant now, String code, String detail) {
        IncidentEvent ev = new IncidentEvent();
        ev.setOrganizationId(incident.getOrganizationId());
        ev.setIncidentId(incident.getId());
        ev.setOccurredAt(now);
        ev.setEventCode(code);
        ev.setDetail(detail);
        return incidentEventRepository.save(ev).then();
    }

    private Mono<Void> saveLocalization(Incident incident, Instant now, TelemetryAlertEvaluateRequest request) {
        IncidentLocalization loc = new IncidentLocalization();
        loc.setOrganizationId(incident.getOrganizationId());
        loc.setIncidentId(incident.getId());
        loc.setScope(request.affectedScope());
        loc.setLocatedAt(now);
        loc.setSolarSectorId(request.solarSectorId());
        loc.setSolarStringId(request.solarStringId());
        loc.setEquipmentId(request.equipmentId());
        return localizationRepository.save(loc).then();
    }

    private static String buildDedupKey(Long installationId, Long ruleVersionId, LocalizationScope scope, String classification) {
        return installationId + ":" + ruleVersionId + ":" + scope.name() + ":" + classification;
    }
}
