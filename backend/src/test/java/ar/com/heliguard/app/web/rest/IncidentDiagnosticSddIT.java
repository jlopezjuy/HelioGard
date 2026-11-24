package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.RuleComparison;
import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.repository.IncidentEventRepository;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateResponse;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateResponse;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.dto.IncidentDTO;
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
 * Sprint S5 integration tests for night/offline/fault differentiation (T11, T12),
 * physical integrity vs cyberattack (T13), rule immutability, and incident dedup/lifecycle.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class IncidentDiagnosticSddIT {

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
    private AlertRuleRepository alertRuleRepository;

    @Autowired
    private AlertRuleVersionRepository ruleVersionRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentEventRepository incidentEventRepository;

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

    private void addOwner(Organization org, User user) {
        OrganizationMembership m = new OrganizationMembership();
        m.setOrganizationId(org.getId());
        m.setMemberId(user.getId());
        m.setRole(MembershipRole.OWNER);
        m.setStatus(MembershipStatus.ACTIVE);
        m.setActivatedAt(Instant.now());
        membershipRepository.save(m).block();
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
    void t11_t12_t13_and_s5LifecycleRules() throws Exception {
        String s = suffix();
        User userA = createUser("inc-a-" + s);
        User userB = createUser("inc-b-" + s);
        Organization orgA = createOrg("inc-orga-" + s);
        Organization orgB = createOrg("inc-orgb-" + s);
        addOwner(orgA, userA);
        addOwner(orgB, userB);

        Installation instA = createInstallation(orgA, "INC-A-" + s);

        AlertRule rule = new AlertRule();
        rule.setOrganizationId(orgA.getId());
        rule.setInstallationId(instA.getId());
        rule.setName("Low Production " + s);
        rule.setRuleCode("LOW_PROD_" + s);
        rule.setSeverity(AlertSeverity.HIGH);
        rule.setEnabled(true);
        rule = alertRuleRepository.save(rule).block();

        AlertRuleVersion rv = new AlertRuleVersion();
        rv.setOrganizationId(orgA.getId());
        rv.setAlertRuleId(rule.getId());
        rv.setVersionCode("v1");
        rv.setComparator(RuleComparison.DEVIATION_PERCENT);
        rv.setThresholdValue(new BigDecimal("50"));
        rv.setWindowSeconds(900);
        rv.setCooldownSeconds(300);
        rv.setMinCoveragePct(new BigDecimal("50"));
        rv.setValidFrom(Instant.now().minus(1, ChronoUnit.DAYS));
        rv = ruleVersionRepository.save(rv).block();

        String jwtA = login(userA.getLogin());
        String jwtB = login(userB.getLogin());

        // T11: Midnight / zero irradiance -> NO_PRODUCTION_NIGHT, no incident
        TelemetryAlertEvaluateRequest nightReq = new TelemetryAlertEvaluateRequest(
            instA.getId(),
            rv.getId(),
            LocalizationScope.INVERTER,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            10L,
            false,
            new BigDecimal("100"),
            null,
            null,
            null
        );
        TelemetryAlertEvaluateResponse t11Resp = webTestClient
            .post()
            .uri("/api/incidents/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(nightReq))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryAlertEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t11Resp.classification()).isEqualTo("NO_PRODUCTION_NIGHT");
        assertThat(t11Resp.incidentCreatedOrUpdated()).isFalse();
        assertThat(incidentRepository.findByInstallation(instA.getId()).collectList().block()).isEmpty();

        // T12: Stale telemetry -> TELEMETRY_OFFLINE (not PANEL_FAILURE) + dedup on repeat
        TelemetryAlertEvaluateRequest offlineReq = new TelemetryAlertEvaluateRequest(
            instA.getId(),
            rv.getId(),
            LocalizationScope.INVERTER,
            new BigDecimal("600"),
            BigDecimal.ZERO,
            new BigDecimal("5000"),
            1800L,
            false,
            new BigDecimal("100"),
            null,
            null,
            null
        );
        TelemetryAlertEvaluateResponse t12First = webTestClient
            .post()
            .uri("/api/incidents/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offlineReq))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryAlertEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t12First.classification()).isEqualTo("TELEMETRY_OFFLINE");
        assertThat(t12First.deduplicated()).isFalse();

        TelemetryAlertEvaluateResponse t12Second = webTestClient
            .post()
            .uri("/api/incidents/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offlineReq))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryAlertEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t12Second.deduplicated()).isTrue();
        assertThat(t12Second.incidentId()).isEqualTo(t12First.incidentId());
        assertThat(incidentRepository.findByInstallation(instA.getId()).collectList().block()).hasSize(1);
        assertThat(incidentEventRepository.findByIncident(t12First.incidentId()).collectList().block()).hasSize(2);

        // RB-INC-001: AlertRuleVersion is now immutable (409 on PUT)
        AlertRuleVersionDTO rvDto = new AlertRuleVersionDTO();
        rvDto.setId(rv.getId());
        rvDto.setVersionCode("v1-edited");
        rvDto.setComparator(RuleComparison.DEVIATION_PERCENT);
        rvDto.setValidFrom(rv.getValidFrom());
        AlertRuleDTO ruleRef = new AlertRuleDTO();
        ruleRef.setId(rule.getId());
        rvDto.setAlertRule(ruleRef);

        webTestClient
            .put()
            .uri("/api/alert-rule-versions/" + rv.getId())
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rvDto))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // RB-INC-003: Invalid Incident transition OPEN -> CLOSED rejected (409), valid OPEN -> ACKNOWLEDGED (200)
        IncidentDTO invalidTransition = new IncidentDTO();
        invalidTransition.setId(t12First.incidentId());
        invalidTransition.setStatus(IncidentStatus.CLOSED);
        webTestClient
            .patch()
            .uri("/api/incidents/" + t12First.incidentId())
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(invalidTransition))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        IncidentDTO validAck = new IncidentDTO();
        validAck.setId(t12First.incidentId());
        validAck.setStatus(IncidentStatus.ACKNOWLEDGED);
        webTestClient
            .patch()
            .uri("/api/incidents/" + t12First.incidentId())
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(validAck))
            .exchange()
            .expectStatus()
            .isOk();

        // T13: Physically impossible reading (50000 W on 10 kW plant) -> SUSPECT, not confirmed attack
        PhysicalIntegrityEvaluateRequest impossible = new PhysicalIntegrityEvaluateRequest(
            instA.getId(),
            new BigDecimal("50000"),
            new BigDecimal("800"),
            Instant.now(),
            "Power spike 5x nominal"
        );
        PhysicalIntegrityEvaluateResponse t13Resp = webTestClient
            .post()
            .uri("/api/integrity/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(impossible))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PhysicalIntegrityEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t13Resp.physicallyConsistent()).isFalse();
        assertThat(t13Resp.trustLevel()).isEqualTo(DataTrustLevel.SUSPECT);
        assertThat(t13Resp.integrityEventType()).isEqualTo(IntegrityEventType.PHYSICAL_INCONSISTENCY);
        assertThat(t13Resp.cyberAttackConfirmed()).isFalse();

        // Cross-tenant IDOR by Org B -> 404
        webTestClient
            .get()
            .uri("/api/incidents/" + t12First.incidentId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}
