package ar.com.heliguard.app.service.maintenance;

import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionEvent;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.domain.VerificationResult;
import ar.com.heliguard.app.domain.enumeration.AssignmentStatus;
import ar.com.heliguard.app.domain.enumeration.DroneAssetStatus;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.MissionAuthorizationStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.SafetyCheckResult;
import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.repository.DroneMissionEventRepository;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.repository.MissionSafetyCheckRepository;
import ar.com.heliguard.app.repository.RecoveryAssessmentRepository;
import ar.com.heliguard.app.repository.VerificationResultRepository;
import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.WorkOrderAcceptResponse;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Enforces Sprint S8 workflows: external provider work order acceptance (RB-WO-003, T18),
 * simulated-only controlled drone dispatch (RB-DRONE-001..003, T19),
 * and irradiance-normalized recovery verification (RB-REC-001, T20).
 */
@Service
@Transactional
public class MaintenanceAndDroneWorkflowService {

    private static final BigDecimal DEFAULT_MAX_WIND_MS = new BigDecimal("8.0");
    private static final int MIN_POST_SAMPLES = 3;

    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderAssignmentRepository assignmentRepository;
    private final DroneMissionRepository missionRepository;
    private final DroneAssetRepository droneAssetRepository;
    private final MissionAuthorizationRepository authorizationRepository;
    private final MissionSafetyCheckRepository safetyCheckRepository;
    private final DroneMissionEventRepository missionEventRepository;
    private final MaintenanceInterventionRepository interventionRepository;
    private final EnergyModelRunRepository modelRunRepository;
    private final RecoveryAssessmentRepository recoveryRepository;
    private final VerificationResultRepository verificationRepository;

    public MaintenanceAndDroneWorkflowService(
        TenantContextService tenantContextService,
        PermissionService permissionService,
        WorkOrderRepository workOrderRepository,
        WorkOrderAssignmentRepository assignmentRepository,
        DroneMissionRepository missionRepository,
        DroneAssetRepository droneAssetRepository,
        MissionAuthorizationRepository authorizationRepository,
        MissionSafetyCheckRepository safetyCheckRepository,
        DroneMissionEventRepository missionEventRepository,
        MaintenanceInterventionRepository interventionRepository,
        EnergyModelRunRepository modelRunRepository,
        RecoveryAssessmentRepository recoveryRepository,
        VerificationResultRepository verificationRepository
    ) {
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
        this.workOrderRepository = workOrderRepository;
        this.assignmentRepository = assignmentRepository;
        this.missionRepository = missionRepository;
        this.droneAssetRepository = droneAssetRepository;
        this.authorizationRepository = authorizationRepository;
        this.safetyCheckRepository = safetyCheckRepository;
        this.missionEventRepository = missionEventRepository;
        this.interventionRepository = interventionRepository;
        this.modelRunRepository = modelRunRepository;
        this.recoveryRepository = recoveryRepository;
        this.verificationRepository = verificationRepository;
    }

    /**
     * T18 / RB-WO-003: Provider technician can accept an assigned WorkOrder only with an active grant.
     */
    public Mono<WorkOrderAcceptResponse> acceptWorkOrder(Long workOrderId) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                workOrderRepository
                    .findById(workOrderId)
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_NOT_FOUND", "WorkOrder not found")))
                    .flatMap(wo ->
                        assignmentRepository
                            .findByWorkOrder(wo.getId())
                            .filter(a ->
                                tenant.organizationId().equals(a.getProviderOrganizationId()) &&
                                (a.getStatus() == AssignmentStatus.OFFERED || a.getStatus() == AssignmentStatus.ACTIVE)
                            )
                            .next()
                            .switchIfEmpty(
                                Mono.error(
                                    new DomainRuleException(
                                        HttpStatus.FORBIDDEN,
                                        "WORK_ORDER_NOT_ASSIGNED_TO_PROVIDER",
                                        "No active assignment for current organization"
                                    )
                                )
                            )
                            .flatMap(assignment -> {
                                Mono<Boolean> authorizedMono = tenant.organizationId().equals(wo.getOrganizationId())
                                    ? Mono.just(true)
                                    : permissionService.hasValidGrant(
                                        wo.getInstallationId(),
                                        tenant.organizationId(),
                                        PermissionScope.MAINTENANCE,
                                        Instant.now()
                                    );
                                return authorizedMono.flatMap(hasGrant -> {
                                    if (!hasGrant) {
                                        return Mono.error(
                                            new DomainRuleException(
                                                HttpStatus.FORBIDDEN,
                                                "PROVIDER_GRANT_REQUIRED",
                                                "External provider lacks an active InstallationAccessGrant"
                                            )
                                        );
                                    }
                                    Instant now = Instant.now();
                                    assignment.setStatus(AssignmentStatus.ACCEPTED);
                                    assignment.setAcceptedAt(now);
                                    wo.setStatus(WorkOrderStatus.ACCEPTED);
                                    return assignmentRepository
                                        .save(assignment)
                                        .then(workOrderRepository.save(wo))
                                        .map(savedWo -> new WorkOrderAcceptResponse(savedWo.getId(), assignment.getId(), savedWo.getStatus()));
                                });
                            })
                    )
            );
    }

    /**
     * T19 / RB-DRONE-001..003: Simulated dispatch only (physical drones disabled).
     */
    public Mono<DroneDispatchResponse> dispatchSimulatedMission(Long missionId, DroneDispatchRequest request) {
        Instant now = Instant.now();
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                missionRepository
                    .findById(missionId)
                    .filter(m -> tenant.organizationId().equals(m.getOrganizationId()))
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_MISSION_NOT_FOUND", "DroneMission not found"))
                    )
                    .flatMap(mission -> checkDispatchPreconditions(mission, request, now))
            );
    }

    /**
     * T20 / RB-REC-001: Recovery verification returns INCONCLUSIVE if comparable post-maintenance data is insufficient.
     */
    public Mono<RecoveryVerifyResponse> verifyRecovery(RecoveryVerifyRequest request) {
        Instant now = Instant.now();
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                interventionRepository
                    .findById(request.interventionId())
                    .filter(i -> tenant.organizationId().equals(i.getOrganizationId()))
                    .switchIfEmpty(
                        Mono.error(
                            new DomainRuleException(
                                HttpStatus.NOT_FOUND,
                                "INTERVENTION_NOT_FOUND",
                                "MaintenanceIntervention not found"
                            )
                        )
                    )
                    .flatMap(intervention ->
                        workOrderRepository
                            .findById(intervention.getWorkOrderId())
                            .flatMap(wo -> {
                                Mono<Boolean> modelValidMono = request.modelRunId() == null
                                    ? Mono.just(false)
                                    : modelRunRepository
                                        .findById(request.modelRunId())
                                        .filter(r ->
                                            tenant.organizationId().equals(r.getOrganizationId()) &&
                                            r.getStatus() == EnergyModelRunStatus.SUCCEEDED
                                        )
                                        .hasElement();

                                return modelValidMono.flatMap(modelValid -> {
                                    boolean sufficient =
                                        request.irradianceNormalizedAvailable() &&
                                        request.postSamplesCount() >= MIN_POST_SAMPLES &&
                                        modelValid &&
                                        request.verifiedRecoveredWh() != null;

                                    VerificationStatus status = sufficient
                                        ? (request.verifiedRecoveredWh().compareTo(BigDecimal.ZERO) > 0
                                            ? VerificationStatus.RECOVERED
                                            : VerificationStatus.NOT_RECOVERED)
                                        : VerificationStatus.INCONCLUSIVE;

                                    RecoveryAssessment rec = new RecoveryAssessment();
                                    rec.setOrganizationId(tenant.organizationId());
                                    rec.setInterventionId(intervention.getId());
                                    rec.setModelRunId(modelValid ? request.modelRunId() : null);
                                    rec.setAssessedAt(now);
                                    rec.setBaselineMethod("IRRADIANCE_NORMALIZED_V1");
                                    rec.setVerifiedRecoveredWh(sufficient ? request.verifiedRecoveredWh() : null);
                                    rec.setConfidenceNote(
                                        sufficient
                                            ? "Comparable post-maintenance baseline verified"
                                            : "INCONCLUSIVE: insufficient post-maintenance samples or irradiance-normalized baseline"
                                    );

                                    return recoveryRepository
                                        .save(rec)
                                        .flatMap(savedRec -> {
                                            VerificationResult vr = new VerificationResult();
                                            vr.setOrganizationId(tenant.organizationId());
                                            vr.setInstallationId(wo.getInstallationId());
                                            vr.setDroneMissionId(request.droneMissionId());
                                            vr.setRecoveryAssessmentId(savedRec.getId());
                                            vr.setStatus(status);
                                            vr.setAssessedAt(now);
                                            vr.setBaselineMethod("IRRADIANCE_NORMALIZED_V1");
                                            vr.setRecoveredWh(sufficient ? request.verifiedRecoveredWh() : null);
                                            vr.setEvidenceSummary(rec.getConfidenceNote());
                                            return verificationRepository
                                                .save(vr)
                                                .map(savedVr ->
                                                    new RecoveryVerifyResponse(
                                                        savedRec.getId(),
                                                        savedVr.getId(),
                                                        savedVr.getStatus(),
                                                        savedVr.getRecoveredWh(),
                                                        savedVr.getEvidenceSummary()
                                                    )
                                                );
                                        });
                                });
                            })
                    )
            );
    }

    private Mono<DroneDispatchResponse> checkDispatchPreconditions(DroneMission mission, DroneDispatchRequest request, Instant now) {
        if (request != null && (request.icePresent() || request.energizedDamagedEquipment())) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "UNSAFE_MISSION_CONDITION",
                    "Mission blocked due to ice or energized damaged equipment"
                )
            );
        }

        Mono<DroneAsset> assetMono = mission.getDroneAssetId() == null
            ? Mono.just(new DroneAsset().status(DroneAssetStatus.READY).maximumWindMs(DEFAULT_MAX_WIND_MS))
            : droneAssetRepository
                .findById(mission.getDroneAssetId())
                .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "DRONE_ASSET_NOT_FOUND", "DroneAsset not found")));

        Mono<Boolean> authorizedMono = authorizationRepository
            .findByMission(mission.getId())
            .filter(auth ->
                auth.getStatus() == MissionAuthorizationStatus.APPROVED &&
                (auth.getExpiresAt() == null || auth.getExpiresAt().isAfter(now))
            )
            .hasElements();

        Mono<Boolean> safetyPassedMono = safetyCheckRepository
            .findByMission(mission.getId())
            .collectList()
            .map(checks ->
                checks.stream().anyMatch(c -> c.getResult() == SafetyCheckResult.PASS) &&
                checks.stream().noneMatch(c -> c.getResult() == SafetyCheckResult.FAIL)
            );

        return Mono.zip(assetMono, authorizedMono, safetyPassedMono)
            .flatMap(tuple -> {
                DroneAsset asset = tuple.getT1();
                boolean hasAuth = tuple.getT2();
                boolean safetyOk = tuple.getT3();

                if (!hasAuth) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "MISSION_NOT_AUTHORIZED",
                            "DroneMission requires an active APPROVED MissionAuthorization before dispatch"
                        )
                    );
                }
                if (!safetyOk) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "SAFETY_CHECK_REQUIRED",
                            "DroneMission requires passing safety checks with zero failures"
                        )
                    );
                }
                if (asset.getStatus() != DroneAssetStatus.READY) {
                    return Mono.error(
                        new DomainRuleException(HttpStatus.CONFLICT, "DRONE_ASSET_NOT_READY", "DroneAsset is not READY")
                    );
                }
                BigDecimal maxWind = asset.getMaximumWindMs() != null ? asset.getMaximumWindMs() : DEFAULT_MAX_WIND_MS;
                if (request != null && request.windSpeedMs() != null && request.windSpeedMs().compareTo(maxWind) > 0) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "WIND_SPEED_EXCEEDS_LIMIT",
                            "Wind speed " + request.windSpeedMs() + " m/s exceeds safe limit " + maxWind + " m/s"
                        )
                    );
                }

                mission.setStatus(DroneMissionStatus.DISPATCHED);
                mission.setStartedAt(now);

                DroneMissionEvent ev = new DroneMissionEvent();
                ev.setOrganizationId(mission.getOrganizationId());
                ev.setMissionId(mission.getId());
                ev.setEventCode("SIMULATED_DISPATCH");
                ev.setOccurredAt(now);
                ev.setDescription("Simulated mission dispatched; physical drone hardware disabled in MVP");

                return missionRepository
                    .save(mission)
                    .then(missionEventRepository.save(ev))
                    .thenReturn(
                        new DroneDispatchResponse(
                            mission.getId(),
                            DroneMissionStatus.DISPATCHED,
                            true,
                            false,
                            "Simulated mission dispatched; physical drone hardware disabled"
                        )
                    );
            });
    }
}
