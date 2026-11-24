package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.LossCause;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.CauseAllocation;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateRequest;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateResponse;
import ar.com.heliguard.app.web.rest.vm.LoginVM;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
 * Sprint S6 integration tests for no double counting (T14), scenario ranges without
 * self-consumption curve (T15), active tariff validity, and cleaning ROI guard.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class EconomicLossSddIT {

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
    private EnergyModelRunRepository modelRunRepository;

    @Autowired
    private TariffPlanRepository tariffPlanRepository;

    @Autowired
    private TariffRateRepository tariffRateRepository;

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
    void t14_t15_noDoubleCountingAndScenarioRangeWithoutSelfConsumptionCurve() throws Exception {
        String s = suffix();
        User userA = createUser("eco-a-" + s);
        User userB = createUser("eco-b-" + s);
        Organization orgA = createOrg("eco-orga-" + s);
        Organization orgB = createOrg("eco-orgb-" + s);
        addOwner(orgA, userA);
        addOwner(orgB, userB);

        Installation instA = createInstallation(orgA, "ECO-A-" + s);
        Instant now = Instant.now();

        // 4 kWh deficit (10000 Wh expected - 6000 Wh actual = 4000 Wh)
        EnergyModelRun run = new EnergyModelRun();
        run.setOrganizationId(orgA.getId());
        run.setInstallationId(instA.getId());
        run.setModelName("PV_BASELINE_V1");
        run.setModelVersion("1.0");
        run.setPeriodStart(now.minus(2, ChronoUnit.HOURS));
        run.setPeriodEnd(now);
        run.setStatus(EnergyModelRunStatus.SUCCEEDED);
        run.setExpectedWh(new BigDecimal("10000"));
        run.setActualWh(new BigDecimal("6000"));
        run.setCalculatedAt(now);
        run = modelRunRepository.save(run).block();

        TariffPlan plan = new TariffPlan();
        plan.setOrganizationId(orgA.getId());
        plan.setInstallationId(instA.getId());
        plan.setName("Tariff " + s);
        plan.setCurrencyCode("USD");
        plan = tariffPlanRepository.save(plan).block();

        TariffRate rate = new TariffRate();
        rate.setOrganizationId(orgA.getId());
        rate.setTariffPlanId(plan.getId());
        rate.setValidFrom(now.minus(5, ChronoUnit.DAYS));
        rate.setValidTo(now.plus(5, ChronoUnit.DAYS));
        rate.setPurchasePricePerKwh(new BigDecimal("0.20"));
        rate.setExportPricePerKwh(new BigDecimal("0.10"));
        rate = tariffRateRepository.save(rate).block();

        String jwtA = login(userA.getLogin());
        String jwtB = login(userB.getLogin());

        // T14: Deficit 4000 Wh with attributions 3000 Wh + 2000 Wh -> 409 Conflict (no double counting)
        EconomicEvaluateRequest doubleCount = new EconomicEvaluateRequest(
            run.getId(),
            plan.getId(),
            now,
            List.of(
                new CauseAllocation(LossCause.SOILING, new BigDecimal("3000"), "TREND", "Soiling trend"),
                new CauseAllocation(LossCause.SHADING, new BigDecimal("2000"), "GEOMETRY", "Shading")
            ),
            null,
            null,
            null,
            null,
            false,
            false
        );

        webTestClient
            .post()
            .uri("/api/economic-assessments/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(doubleCount))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // T15: Valid attribution (2500 Wh -> 1500 Wh residual UNKNOWN), NO self-consumption curve -> scenarios
        EconomicEvaluateRequest scenarioReq = new EconomicEvaluateRequest(
            run.getId(),
            plan.getId(),
            now,
            List.of(new CauseAllocation(LossCause.SOILING, new BigDecimal("2500"), "TREND", "Confirmed trend")),
            null,
            null,
            new BigDecimal("50"),
            30,
            true,
            false // missing comparable post-recovery evidence -> cleaningRecoveryPromised == false
        );

        EconomicEvaluateResponse t15Resp = webTestClient
            .post()
            .uri("/api/economic-assessments/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(scenarioReq))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EconomicEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t15Resp.totalLossWh()).isEqualByComparingTo("4000");
        assertThat(t15Resp.attributedWh()).isEqualByComparingTo("2500");
        assertThat(t15Resp.residualUnknownWh()).isEqualByComparingTo("1500");
        assertThat(t15Resp.exactAmountAvailable()).isFalse();
        assertThat(t15Resp.estimatedLossAmount()).isNull();
        assertThat(t15Resp.scenarios()).hasSize(3);
        assertThat(t15Resp.cleaningRecoveryPromised()).isFalse();

        // With self-consumption curve + comparable post-recovery evidence -> exact amount & cleaning promised
        EconomicEvaluateRequest exactReq = new EconomicEvaluateRequest(
            run.getId(),
            plan.getId(),
            now,
            List.of(new CauseAllocation(LossCause.SOILING, new BigDecimal("2500"), "TREND", "Confirmed trend")),
            new BigDecimal("3000"), // 3 kWh * $0.20 = $0.60
            new BigDecimal("1000"), // 1 kWh * $0.10 = $0.10 -> total $0.70
            new BigDecimal("50"),
            30,
            true,
            true
        );

        EconomicEvaluateResponse exactResp = webTestClient
            .post()
            .uri("/api/economic-assessments/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exactReq))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EconomicEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(exactResp.exactAmountAvailable()).isTrue();
        assertThat(exactResp.estimatedLossAmount()).isEqualByComparingTo("0.70");
        assertThat(exactResp.cleaningRecoveryPromised()).isTrue();

        // Cross-tenant IDOR by Org B -> 404
        webTestClient
            .post()
            .uri("/api/economic-assessments/evaluate")
            .header("Authorization", "Bearer " + jwtB)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(scenarioReq))
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}
