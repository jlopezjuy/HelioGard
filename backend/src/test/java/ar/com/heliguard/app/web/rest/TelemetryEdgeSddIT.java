package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.TelemetryIngestEvent;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.DeviceTrustStatus;
import ar.com.heliguard.app.domain.enumeration.GatewayStatus;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.DeviceIdentityRepository;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.TelemetryIngestEventRepository;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryEventPayload;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestRequest;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestResponse;
import ar.com.heliguard.app.service.edge.DeviceAuthService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Sprint S3 integration tests for M2M edge authentication, idempotency,
 * offline batch replay, and integrity/security event generation (T06-T09).
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class TelemetryEdgeSddIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private DeviceIdentityRepository deviceIdentityRepository;

    @Autowired
    private TelemetryIngestEventRepository ingestRepository;

    @Autowired
    private TelemetryIntegrityEventRepository integrityEventRepository;

    private String suffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private record DeviceFixture(Organization org, Installation inst, Gateway gw, DeviceIdentity device, String rawSecret) {}

    private DeviceFixture createDeviceFixture(String s) {
        Organization org = new Organization();
        org.setName("Edge Org " + s);
        org.setSlug("edge-org-" + s);
        org.setStatus(OrganizationStatus.ACTIVE);
        org.setKind(OrganizationKind.BUSINESS);
        org.setCreatedAt(Instant.now());
        org = organizationRepository.save(org).block();

        Site site = new Site();
        site.setName("Edge Site " + s);
        site.setTimeZone("America/Argentina/Buenos_Aires");
        site.setOrganizationId(org.getId());
        site = siteRepository.save(site).block();

        Installation inst = new Installation();
        inst.setCode("EDGE-" + s);
        inst.setName("Edge Plant " + s);
        inst.setGridMode(GridMode.GRID_TIED);
        inst.setStatus(InstallationStatus.ACTIVE);
        inst.setNominalDcKw(new BigDecimal("100.00"));
        inst.setOrganizationId(org.getId());
        inst.setSiteId(site.getId());
        inst = installationRepository.save(inst).block();

        Gateway gw = new Gateway();
        gw.setHardwareId("GW-" + s);
        gw.setFirmwareVersion("1.0.0");
        gw.setStatus(GatewayStatus.ONLINE);
        gw.setOrganizationId(org.getId());
        gw.setInstallationId(inst.getId());
        gw = gatewayRepository.save(gw).block();

        String rawSecret = "secret-" + s + "-" + UUID.randomUUID();
        DeviceIdentity dev = new DeviceIdentity();
        dev.setDeviceUid("dev-" + s);
        dev.setKeyFingerprint(DeviceAuthService.sha256Hex(rawSecret));
        dev.setProtocolName("HTTPS_JSON");
        dev.setTrustStatus(DeviceTrustStatus.ACTIVE);
        dev.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        dev.setOrganizationId(org.getId());
        dev.setGatewayId(gw.getId());
        dev = deviceIdentityRepository.save(dev).block();

        return new DeviceFixture(org, inst, gw, dev, rawSecret);
    }

    @Test
    void t06_foreignInstallationRejectedAndSecurityEventRecorded() throws Exception {
        String s = suffix();
        DeviceFixture fixA = createDeviceFixture("a-" + s);
        DeviceFixture fixB = createDeviceFixture("b-" + s);

        TelemetryEventPayload foreignEvent = new TelemetryEventPayload(
            "evt-foreign-" + s,
            "boot-1",
            1L,
            "dc_power_w",
            "W",
            new BigDecimal("4200.5"),
            Instant.now().minusSeconds(10),
            fixB.inst().getId(),
            null
        );

        webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fixA.device().getDeviceUid())
            .header("X-Device-Secret", fixA.rawSecret())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new TelemetryIngestRequest(List.of(foreignEvent))))
            .exchange()
            .expectStatus()
            .isForbidden();

        List<TelemetryIntegrityEvent> events = integrityEventRepository
            .findByOrganization(fixA.org().getId())
            .collectList()
            .block();
        assertThat(events).anyMatch(e -> e.getEventType() == IntegrityEventType.AUTH_FAILURE);
    }

    @Test
    void t07_offline100EventsRetriedThreeTimesPersistExactly100UniqueEvents() throws Exception {
        String s = suffix();
        DeviceFixture fix = createDeviceFixture("batch-" + s);
        Instant base = Instant.now().minus(1, ChronoUnit.HOURS);

        List<TelemetryEventPayload> batch = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            batch.add(
                new TelemetryEventPayload(
                    "evt-" + s + "-" + i,
                    "boot-offline-1",
                    (long) (i + 1),
                    "dc_power_w",
                    "W",
                    new BigDecimal("3500." + (i % 10)),
                    base.plusSeconds(i * 10L),
                    null,
                    null
                )
            );
        }
        byte[] payloadBytes = om.writeValueAsBytes(new TelemetryIngestRequest(batch));

        TelemetryIngestResponse first = webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fix.device().getDeviceUid())
            .header("X-Device-Secret", fix.rawSecret())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payloadBytes)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryIngestResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(first).isNotNull();
        assertThat(first.acceptedCount()).isEqualTo(100);
        assertThat(first.duplicateIdempotentCount()).isZero();

        for (int retry = 0; retry < 2; retry++) {
            TelemetryIngestResponse retryResp = webTestClient
                .post()
                .uri("/api/ingest/telemetry")
                .header("X-Device-Uid", fix.device().getDeviceUid())
                .header("X-Device-Secret", fix.rawSecret())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payloadBytes)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TelemetryIngestResponse.class)
                .returnResult()
                .getResponseBody();

            assertThat(retryResp).isNotNull();
            assertThat(retryResp.acceptedCount()).isZero();
            assertThat(retryResp.duplicateIdempotentCount()).isEqualTo(100);
            assertThat(retryResp.quarantinedCount()).isZero();
        }

        Long persistedCount = ingestRepository.countByGatewayId(fix.gw().getId()).block();
        assertThat(persistedCount).isEqualTo(100L);
    }

    @Test
    void t08_t09_idempotentDuplicateVsHashMismatchQuarantineAndInvalidAuth() throws Exception {
        String s = suffix();
        DeviceFixture fix = createDeviceFixture("idem-" + s);
        Instant ts = Instant.now().minusSeconds(30);

        // Invalid secret -> 401 + AUTH_FAILURE integrity event
        TelemetryEventPayload ev1 = new TelemetryEventPayload(
            "evt-1-" + s,
            "boot-1",
            1L,
            "ac_power_w",
            "W",
            new BigDecimal("5000.00"),
            ts,
            null,
            null
        );

        webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fix.device().getDeviceUid())
            .header("X-Device-Secret", "wrong-secret")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new TelemetryIngestRequest(List.of(ev1))))
            .exchange()
            .expectStatus()
            .isUnauthorized();

        // Valid ingest
        TelemetryIngestResponse ok = webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fix.device().getDeviceUid())
            .header("X-Device-Secret", fix.rawSecret())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new TelemetryIngestRequest(List.of(ev1))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryIngestResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(ok.acceptedCount()).isEqualTo(1);

        // T08: Exact retry -> DUPLICATE_IDEMPOTENT, no REPLAY incident
        TelemetryIngestResponse dup = webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fix.device().getDeviceUid())
            .header("X-Device-Secret", fix.rawSecret())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new TelemetryIngestRequest(List.of(ev1))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryIngestResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(dup.duplicateIdempotentCount()).isEqualTo(1);

        // T09: Same eventId with altered numericValue -> QUARANTINED + REPLAY integrity event, original untouched
        TelemetryEventPayload tampered = new TelemetryEventPayload(
            "evt-1-" + s,
            "boot-1",
            1L,
            "ac_power_w",
            "W",
            new BigDecimal("99999.00"),
            ts,
            null,
            null
        );
        TelemetryIngestResponse quar = webTestClient
            .post()
            .uri("/api/ingest/telemetry")
            .header("X-Device-Uid", fix.device().getDeviceUid())
            .header("X-Device-Secret", fix.rawSecret())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new TelemetryIngestRequest(List.of(tampered))))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(TelemetryIngestResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(quar.quarantinedCount()).isEqualTo(1);

        TelemetryIngestEvent stored = ingestRepository.findByGatewayIdAndEventId(fix.gw().getId(), "evt-1-" + s).block();
        assertThat(stored.getNumericValue()).isEqualByComparingTo("5000.00");

        List<TelemetryIntegrityEvent> integrity = integrityEventRepository
            .findByOrganization(fix.org().getId())
            .collectList()
            .block();
        assertThat(integrity)
            .anyMatch(e -> e.getEventType() == IntegrityEventType.REPLAY && e.getStatus() == IntegrityEventStatus.QUARANTINED);
    }
}
