package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.MissionAuthorization;
import ar.com.heliguard.app.domain.MissionSafetyCheck;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderAssignment;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.AssignmentStatus;
import ar.com.heliguard.app.domain.enumeration.DroneAssetStatus;
import ar.com.heliguard.app.domain.enumeration.DroneMissionKind;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.InterventionType;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.MissionAuthorizationStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.SafetyCheckResult;
import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.repository.MissionSafetyCheckRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.WorkOrderAcceptResponse;
import ar.com.heliguard.app.web.rest.vm.LoginVM;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Sprint S8 integration tests for provider grant enforcement (T18), simulated drone dispatch
 * authorization & weather safety gates (T19), and INCONCLUSIVE recovery verification (T20).
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class MaintenanceDroneSddIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMembershipRepository membershipRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private InstallationAccessGrantRepository grantRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderAssignmentRepository assignmentRepository;

    @Autowired
    private MaintenanceInterventionRepository interventionRepository;

    @Autowired
    private DroneAssetRepository droneAssetRepository;

    @Autowired
    private DroneMissionRepository droneMissionRepository;

    @Autowired
    private MissionAuthorizationRepository missionAuthRepository;

    @Autowired
    private MissionSafetyCheckRepository safetyCheckRepository;

    private String suffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private User createUser(String login) {
        User u = new User();
        u.setLogin(login);
        u.setEmail(login + "@example.com");
        u.setActivated(true);
        u.setPassword(passwordEncoder.encode("secret123"));
        u.setCreatedBy(Constants.SYSTEM);
        return userRepository.save(u).block();
    }

    private Organization createOrg(String slug) {
        Organization org = new Organization();
        org.setName("Org " + slug);
        org.setSlug(slug);
        org.setStatus(OrganizationStatus.ACTIVE);
        org.setKind(OrganizationKind.BUSINESS);
        org.setCreatedAt(Instant.now());
        return organizationRepository.save(org).block();
    }

    private OrganizationMembership addMembership(Organization org, User user, MembershipRole role) {
        OrganizationMembership m = new OrganizationMembership();
        m.setOrganizationId(org.getId());
        m.setMemberId(user.getId());
        m.setRole(role);
        m.setStatus(MembershipStatus.ACTIVE);
        m.setActivatedAt(Instant.now());
        return membershipRepository.save(m).block();
    }

    private Installation createInstallation(Organization org, String code) {
        Site site = new Site();
        site.setName("Site " + code);
        site.setTimeZone("America/Argentina/Buenos_Aires");
        site.setOrganizationId(org.getId());
        site = siteRepository.save(site).block();

        Installation inst = new Installation();
        inst.setCode(code);
        inst.setName("Plant " + code);
        inst.setGridMode(GridMode.GRID_TIED);
        inst.setStatus(InstallationStatus.ACTIVE);
        inst.setNominalDcKw(new BigDecimal("10.00"));
        inst.setOrganizationId(org.getId());
        inst.setSiteId(site.getId());
        return installationRepository.save(inst).block();
    }

    private String login(String username) throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername(username);
        login.setPassword("secret123");
        byte[] body = webTestClient
            .post()
            .uri("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(login))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
        return om.readTree(body).get("id_token").asText();
    }

    @Test
    void t18_t19_t20_workOrderGrantSimulatedDroneAndInconclusiveRecovery() throws Exception {
        String s = suffix();
        User ownerUser = createUser("wo-own-" + s);
        User providerTech = createUser("wo-tech-" + s);
        Organization ownerOrg = createOrg("wo-owner-" + s);
        Organization providerOrg = createOrg("wo-prov-" + s);
        OrganizationMembership ownerMember = addMembership(ownerOrg, ownerUser, MembershipRole.OWNER);
        OrganizationMembership techMember = addMembership(providerOrg, providerTech, MembershipRole.TECHNICIAN);

        Installation instA = createInstallation(ownerOrg, "WO-A-" + s);
        Instant now = Instant.now();

        WorkOrder wo = new WorkOrder();
        wo.setOrganizationId(ownerOrg.getId());
        wo.setInstallationId(instA.getId());
        wo.setReferenceCode("WO-" + s);
        wo.setTitle("Clean sector 1");
        wo.setStatus(WorkOrderStatus.ASSIGNED);
        wo.setPriority(AlertSeverity.MEDIUM);
        wo.setOpenedAt(now);
        wo = workOrderRepository.save(wo).block();

        WorkOrderAssignment assignment = new WorkOrderAssignment();
        assignment.setOrganizationId(ownerOrg.getId());
        assignment.setWorkOrderId(wo.getId());
        assignment.setProviderOrganizationId(providerOrg.getId());
        assignment.setAssigneeMembershipId(techMember.getId());
        assignment.setStatus(AssignmentStatus.OFFERED);
        assignment.setAssignedAt(now);
        assignmentRepository.save(assignment).block();

        String ownerJwt = login(ownerUser.getLogin());
        String techJwt = login(providerTech.getLogin());

        // T18: Provider without valid InstallationAccessGrant tries to accept WorkOrder -> 403 Forbidden
        webTestClient
            .post()
            .uri("/api/work-orders/" + wo.getId() + "/accept")
            .header("Authorization", "Bearer " + techJwt)
            .exchange()
            .expectStatus()
            .isForbidden();

        // Grant valid MAINTENANCE access -> acceptance succeeds
        InstallationAccessGrant grant = new InstallationAccessGrant();
        grant.setOrganizationId(ownerOrg.getId());
        grant.setInstallationId(instA.getId());
        grant.setGranteeOrganizationId(providerOrg.getId());
        grant.setScope(PermissionScope.MAINTENANCE);
        grant.setValidFrom(now.minus(1, ChronoUnit.HOURS));
        grant.setValidUntil(now.plus(1, ChronoUnit.DAYS));
        grantRepository.save(grant).block();

        WorkOrderAcceptResponse accepted = webTestClient
            .post()
            .uri("/api/work-orders/" + wo.getId() + "/accept")
            .header("Authorization", "Bearer " + techJwt)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(WorkOrderAcceptResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(accepted.status()).isEqualTo(WorkOrderStatus.ACCEPTED);

        // T19: Drone mission without authorization or excessive wind -> 409 Conflict
        DroneAsset asset = new DroneAsset();
        asset.setOrganizationId(ownerOrg.getId());
        asset.setAssetCode("DRN-" + s);
        asset.setStatus(DroneAssetStatus.READY);
        asset.setMaximumWindMs(new BigDecimal("8.0"));
        asset = droneAssetRepository.save(asset).block();

        DroneMission mission = new DroneMission();
        mission.setOrganizationId(ownerOrg.getId());
        mission.setInstallationId(instA.getId());
        mission.setWorkOrderId(wo.getId());
        mission.setDroneAssetId(asset.getId());
        mission.setMissionCode("MSN-" + s);
        mission.setKind(DroneMissionKind.CLEANING);
        mission.setStatus(DroneMissionStatus.SAFETY_REVIEW);
        mission.setProposedAt(now);
        mission = droneMissionRepository.save(mission).block();

        MissionSafetyCheck check = new MissionSafetyCheck();
        check.setOrganizationId(ownerOrg.getId());
        check.setMissionId(mission.getId());
        check.setCheckCode("BATTERY_AND_AIRSPACE");
        check.setResult(SafetyCheckResult.PASS);
        check.setCheckedAt(now);
        check.setCheckedByMembershipId(ownerMember.getId());
        safetyCheckRepository.save(check).block();

        // Missing authorization -> 409
        webTestClient
            .post()
            .uri("/api/drone-missions/" + mission.getId() + "/dispatch")
            .header("Authorization", "Bearer " + ownerJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new DroneDispatchRequest(new BigDecimal("4.0"), false, false)))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // Add APPROVED authorization, test wind 12.0 > 8.0 m/s -> 409
        MissionAuthorization auth = new MissionAuthorization();
        auth.setOrganizationId(ownerOrg.getId());
        auth.setMissionId(mission.getId());
        auth.setStatus(MissionAuthorizationStatus.APPROVED);
        auth.setDecidedAt(now);
        auth.setExpiresAt(now.plus(2, ChronoUnit.HOURS));
        auth.setAuthorizedByMembershipId(ownerMember.getId());
        missionAuthRepository.save(auth).block();

        webTestClient
            .post()
            .uri("/api/drone-missions/" + mission.getId() + "/dispatch")
            .header("Authorization", "Bearer " + ownerJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new DroneDispatchRequest(new BigDecimal("12.0"), false, false)))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // Safe wind 4.0 m/s -> simulated dispatch 200 OK (physicalHardwareInvoked == false)
        DroneDispatchResponse dispatched = webTestClient
            .post()
            .uri("/api/drone-missions/" + mission.getId() + "/dispatch")
            .header("Authorization", "Bearer " + ownerJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new DroneDispatchRequest(new BigDecimal("4.0"), false, false)))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(DroneDispatchResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(dispatched.status()).isEqualTo(DroneMissionStatus.DISPATCHED);
        assertThat(dispatched.simulatedOnly()).isTrue();
        assertThat(dispatched.physicalHardwareInvoked()).isFalse();

        // T20: Cleaning finished without sufficient post-maintenance measurements -> INCONCLUSIVE
        MaintenanceIntervention intervention = new MaintenanceIntervention();
        intervention.setOrganizationId(ownerOrg.getId());
        intervention.setWorkOrderId(wo.getId());
        intervention.setType(InterventionType.CLEANING);
        intervention.setStartedAt(now.minus(1, ChronoUnit.HOURS));
        intervention.setFinishedAt(now);
        intervention = interventionRepository.save(intervention).block();

        RecoveryVerifyResponse t20Resp = webTestClient
            .post()
            .uri("/api/recovery-assessments/verify")
            .header("Authorization", "Bearer " + ownerJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new RecoveryVerifyRequest(intervention.getId(), mission.getId(), null, 1, false, null)))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(RecoveryVerifyResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t20Resp.status()).isEqualTo(VerificationStatus.INCONCLUSIVE);
        assertThat(t20Resp.recoveredWh()).isNull();
    }
}
