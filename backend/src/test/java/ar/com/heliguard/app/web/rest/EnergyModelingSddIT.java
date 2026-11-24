package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.EvidenceSourceKind;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.WeatherBatchStatus;
import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.repository.EvidenceSourceRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyIntervalSample;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteRequest;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteResponse;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.web.rest.vm.LoginVM;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Sprint S4 integration tests for physical energy modeling, INSUFFICIENT_DATA (T10),
 * meter reset detection, soiling evidence gating, and Sentinel/GOES provenance guard.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class EnergyModelingSddIT {

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
    private EnergyModelConfigurationRepository configRepository;

    @Autowired
    private WeatherIngestionBatchRepository weatherBatchRepository;

    @Autowired
    private EvidenceSourceRepository evidenceSourceRepository;

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
    void t10_and_s4Rules_insufficientDataMeterResetSoilingAndProvenanceGuard() throws Exception {
        String s = suffix();
        User userA = createUser("ene-a-" + s);
        User userB = createUser("ene-b-" + s);
        Organization orgA = createOrg("ene-orga-" + s);
        Organization orgB = createOrg("ene-orgb-" + s);
        addOwner(orgA, userA);
        addOwner(orgB, userB);

        Installation instA = createInstallation(orgA, "ENE-A-" + s);

        Instant t0 = Instant.now().minus(3, ChronoUnit.HOURS);
        Instant t1 = t0.plus(1, ChronoUnit.HOURS);
        Instant t2 = t1.plus(1, ChronoUnit.HOURS);
        Instant t3 = t2.plus(1, ChronoUnit.HOURS);

        EnergyModelConfiguration cfg = new EnergyModelConfiguration();
        cfg.setOrganizationId(orgA.getId());
        cfg.setInstallationId(instA.getId());
        cfg.setAlgorithmCode("PV_BASELINE_V1");
        cfg.setConfigVersion("1.0");
        cfg.setParameterSnapshotRef("s3://configs/" + s);
        cfg.setInputFingerprint("fp-" + s);
        cfg.setActiveFrom(t0.minus(1, ChronoUnit.DAYS));
        cfg = configRepository.save(cfg).block();

        EvidenceSource evSource = new EvidenceSource();
        evSource.setCode("ERA5-" + s);
        evSource.setProviderName("Copernicus ERA5");
        evSource.setKind(EvidenceSourceKind.SATELLITE_WEATHER);
        evSource = evidenceSourceRepository.save(evSource).block();

        WeatherIngestionBatch batch = new WeatherIngestionBatch();
        batch.setOrganizationId(orgA.getId());
        batch.setInstallationId(instA.getId());
        batch.setEvidenceSourceId(evSource.getId());
        batch.setProviderBatchCode("WB-" + s);
        batch.setDatasetCode("ERA5_SOLAR");
        batch.setPeriodStart(t0);
        batch.setPeriodEnd(t3);
        batch.setFetchedAt(Instant.now());
        batch.setStatus(WeatherBatchStatus.VALIDATED);
        batch = weatherBatchRepository.save(batch).block();

        String jwtA = login(userA.getLogin());
        String jwtB = login(userB.getLogin());

        // T10: Missing irradiance -> INSUFFICIENT_DATA, expectedWh == null (never 0)
        EnergyModelExecuteRequest missingIrradiance = new EnergyModelExecuteRequest(
            instA.getId(),
            cfg.getId(),
            batch.getId(),
            t0,
            t3,
            List.of(new EnergyIntervalSample(t0, t1, null, new BigDecimal("25"), null, new BigDecimal("5000"), null, null, false)),
            false
        );

        EnergyModelExecuteResponse t10Resp = webTestClient
            .post()
            .uri("/api/energy-runs/execute")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missingIrradiance))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EnergyModelExecuteResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t10Resp).isNotNull();
        assertThat(t10Resp.status()).isEqualTo(EnergyModelRunStatus.INSUFFICIENT_DATA);
        assertThat(t10Resp.expectedWh()).isNull();

        // Valid comparable + meter reset + cloudy/cold interval, WITHOUT soiling evidence
        List<EnergyIntervalSample> samples = List.of(
            // 10 kW * 800 W/m2 * 1h * 0.82 * (1 - 0.004*20=0.92) = 6035.2 Wh expected; actual = 4000 Wh -> deficit > 0
            new EnergyIntervalSample(t0, t1, new BigDecimal("800"), new BigDecimal("25"), new BigDecimal("4000"), null, null, null, false),
            // Meter reset: end < start -> METER_RESET_DETECTED
            new EnergyIntervalSample(t1, t2, new BigDecimal("800"), new BigDecimal("25"), null, null, new BigDecimal("5000"), new BigDecimal("100"), false),
            // Cloudy/cold -> WEATHER_LIMITED
            new EnergyIntervalSample(t2, t3, new BigDecimal("200"), new BigDecimal("5"), null, new BigDecimal("1000"), null, null, true)
        );

        EnergyModelExecuteRequest noSoilingEvidence = new EnergyModelExecuteRequest(
            instA.getId(),
            cfg.getId(),
            batch.getId(),
            t0,
            t3,
            samples,
            false
        );

        EnergyModelExecuteResponse okNoSoiling = webTestClient
            .post()
            .uri("/api/energy-runs/execute")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(noSoilingEvidence))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EnergyModelExecuteResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(okNoSoiling.status()).isEqualTo(EnergyModelRunStatus.SUCCEEDED);
        assertThat(okNoSoiling.expectedWh()).isGreaterThan(BigDecimal.ZERO);
        assertThat(okNoSoiling.expectedLowerWh()).isLessThan(okNoSoiling.expectedWh());
        assertThat(okNoSoiling.expectedUpperWh()).isGreaterThan(okNoSoiling.expectedWh());
        assertThat(okNoSoiling.deficitWh()).isGreaterThan(BigDecimal.ZERO);
        assertThat(okNoSoiling.soilingHypothesisAllowed()).isFalse();
        assertThat(okNoSoiling.intervals())
            .extracting(i -> i.intervalStatus())
            .containsExactly("COMPARABLE", "METER_RESET_DETECTED", "WEATHER_LIMITED");

        // With soiling trend/inspection evidence -> soilingHypothesisAllowed == true
        EnergyModelExecuteRequest withSoilingEvidence = new EnergyModelExecuteRequest(
            instA.getId(),
            cfg.getId(),
            batch.getId(),
            t0,
            t3,
            samples,
            true
        );
        EnergyModelExecuteResponse okSoiling = webTestClient
            .post()
            .uri("/api/energy-runs/execute")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(withSoilingEvidence))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EnergyModelExecuteResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(okSoiling.soilingHypothesisAllowed()).isTrue();

        // Sentinel/GOES module thermography claim rejected (400)
        DataProvenanceDTO bogusSatellite = new DataProvenanceDTO();
        bogusSatellite.setSourceRecordKey("sat-" + s);
        bogusSatellite.setObservedAt(Instant.now());
        bogusSatellite.setIngestedAt(Instant.now());
        bogusSatellite.setSourceProductCode("SENTINEL-2-L2A");
        bogusSatellite.setQualityFlags("MODULE_THERMOGRAPHY");
        bogusSatellite.setTrustLevel(DataTrustLevel.VALIDATED);
        InstallationDTO instRef = new InstallationDTO();
        instRef.setId(instA.getId());
        bogusSatellite.setInstallation(instRef);
        EvidenceSourceDTO evRef = new EvidenceSourceDTO();
        evRef.setId(evSource.getId());
        bogusSatellite.setEvidenceSource(evRef);

        webTestClient
            .post()
            .uri("/api/data-provenances")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bogusSatellite))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Cross-tenant IDOR by Org B -> 404
        webTestClient
            .post()
            .uri("/api/energy-runs/execute")
            .header("Authorization", "Bearer " + jwtB)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missingIrradiance))
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}
