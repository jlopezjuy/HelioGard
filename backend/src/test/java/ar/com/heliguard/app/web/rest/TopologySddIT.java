package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import ar.com.heliguard.app.domain.enumeration.EquipmentStatus;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.TopologyConnectionKind;
import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import ar.com.heliguard.app.service.topology.TopologyLocalizationService;
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
 * Sprint S2 integration tests for electrical topology, temporal non-overlap (btree_gist),
 * catalog kind validation, tenant/installation isolation, and RB-LOC-001 localization precision.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class TopologySddIT {

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
    private EquipmentModelRepository equipmentModelRepository;

    @Autowired
    private TopologyLocalizationService localizationService;

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
        inst.setNominalDcKw(new BigDecimal("100.00"));
        inst.setOrganizationId(org.getId());
        inst.setSiteId(site.getId());
        return installationRepository.save(inst).block();
    }

    private EquipmentModel createModel(String code, EquipmentKind kind) {
        EquipmentModel m = new EquipmentModel();
        m.setManufacturer("SolarTech");
        m.setModelCode(code);
        m.setKind(kind);
        m.setNominalPowerW(new BigDecimal("550"));
        return equipmentModelRepository.save(m).block();
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

    private <T> T post(String uri, String jwt, Object payload, Class<T> responseType) throws Exception {
        return webTestClient
            .post()
            .uri(uri)
            .header("Authorization", "Bearer " + jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(responseType)
            .returnResult()
            .getResponseBody();
    }

    @Test
    void s2_fullTopologyHierarchyOverlapExclusionAndIsolation() throws Exception {
        String s = suffix();
        User userA = createUser("topo-a-" + s);
        User userB = createUser("topo-b-" + s);
        Organization orgA = createOrg("topo-orga-" + s);
        Organization orgB = createOrg("topo-orgb-" + s);
        addOwner(orgA, userA);
        addOwner(orgB, userB);

        Installation instA1 = createInstallation(orgA, "A1-" + s);
        Installation instA2 = createInstallation(orgA, "A2-" + s);
        EquipmentModel modModel = createModel("MOD-" + s, EquipmentKind.MODULE);
        EquipmentModel invModel = createModel("INV-" + s, EquipmentKind.INVERTER);

        String jwtA = login(userA.getLogin());
        String jwtB = login(userB.getLogin());

        Instant t0 = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant t1 = t0.plus(1, ChronoUnit.DAYS);
        Instant t2 = t1.plus(1, ChronoUnit.DAYS);

        // 1. Create SolarSector in instA1 with spoofed orgB -> server assigns orgA
        SolarSectorDTO sectorDto = new SolarSectorDTO();
        sectorDto.setSectorCode("SEC-1");
        sectorDto.setName("North Sector");
        sectorDto.setValidFrom(t0);
        InstallationDTO instA1Ref = new InstallationDTO();
        instA1Ref.setId(instA1.getId());
        sectorDto.setInstallation(instA1Ref);
        OrganizationDTO spoofedB = new OrganizationDTO();
        spoofedB.setId(orgB.getId());
        sectorDto.setOrganization(spoofedB);

        SolarSectorDTO sector = post("/api/solar-sectors", jwtA, sectorDto, SolarSectorDTO.class);
        assertThat(sector.getOrganization().getId()).isEqualTo(orgA.getId());

        // 2. SolarArray referencing sector from instA1 while targeting instA2 -> 400
        SolarArrayDTO badArray = new SolarArrayDTO();
        badArray.setName("Bad Array");
        InstallationDTO instA2Ref = new InstallationDTO();
        instA2Ref.setId(instA2.getId());
        badArray.setInstallation(instA2Ref);
        SolarSectorDTO sectorRef = new SolarSectorDTO();
        sectorRef.setId(sector.getId());
        badArray.setSolarSector(sectorRef);

        webTestClient
            .post()
            .uri("/api/solar-arrays")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(badArray))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Valid SolarArray in instA1
        SolarArrayDTO arrayDto = new SolarArrayDTO();
        arrayDto.setName("Array 1");
        arrayDto.setInstallation(instA1Ref);
        arrayDto.setSolarSector(sectorRef);
        SolarArrayDTO array = post("/api/solar-arrays", jwtA, arrayDto, SolarArrayDTO.class);

        // 3. SolarString with INVERTER model kind -> 400; with MODULE model kind -> 201
        SolarStringDTO badString = new SolarStringDTO();
        badString.setStringCode("STR-BAD");
        badString.setModulesInSeries(18);
        badString.setActive(true);
        SolarArrayDTO arrayRef = new SolarArrayDTO();
        arrayRef.setId(array.getId());
        badString.setSolarArray(arrayRef);
        EquipmentModelDTO invModelRef = new EquipmentModelDTO();
        invModelRef.setId(invModel.getId());
        badString.setModuleModel(invModelRef);

        webTestClient
            .post()
            .uri("/api/solar-strings")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(badString))
            .exchange()
            .expectStatus()
            .isBadRequest();

        SolarStringDTO stringDto = new SolarStringDTO();
        stringDto.setStringCode("STR-1");
        stringDto.setModulesInSeries(18);
        stringDto.setActive(true);
        stringDto.setSolarArray(arrayRef);
        EquipmentModelDTO modModelRef = new EquipmentModelDTO();
        modModelRef.setId(modModel.getId());
        stringDto.setModuleModel(modModelRef);
        SolarStringDTO solarString = post("/api/solar-strings", jwtA, stringDto, SolarStringDTO.class);

        // 4. Equipment (INVERTER in instA1 and INVERTER in instA2) + MPPT inputs
        EquipmentDTO inv1Dto = new EquipmentDTO();
        inv1Dto.setTag("INV-A1-" + s);
        inv1Dto.setStatus(EquipmentStatus.ACTIVE);
        inv1Dto.setInstallation(instA1Ref);
        inv1Dto.setEquipmentModel(invModelRef);
        EquipmentDTO inv1 = post("/api/equipment", jwtA, inv1Dto, EquipmentDTO.class);

        EquipmentDTO inv2Dto = new EquipmentDTO();
        inv2Dto.setTag("INV-A2-" + s);
        inv2Dto.setStatus(EquipmentStatus.ACTIVE);
        inv2Dto.setInstallation(instA2Ref);
        inv2Dto.setEquipmentModel(invModelRef);
        EquipmentDTO inv2 = post("/api/equipment", jwtA, inv2Dto, EquipmentDTO.class);

        InverterMpptInputDTO mppt1Dto = new InverterMpptInputDTO();
        mppt1Dto.setInputCode("MPPT-1");
        mppt1Dto.setEnabled(true);
        mppt1Dto.setValidFrom(t0);
        EquipmentDTO inv1Ref = new EquipmentDTO();
        inv1Ref.setId(inv1.getId());
        mppt1Dto.setInverter(inv1Ref);
        InverterMpptInputDTO mppt1 = post("/api/inverter-mppt-inputs", jwtA, mppt1Dto, InverterMpptInputDTO.class);

        InverterMpptInputDTO mppt2Dto = new InverterMpptInputDTO();
        mppt2Dto.setInputCode("MPPT-2");
        mppt2Dto.setEnabled(true);
        mppt2Dto.setValidFrom(t0);
        EquipmentDTO inv2Ref = new EquipmentDTO();
        inv2Ref.setId(inv2.getId());
        mppt2Dto.setInverter(inv2Ref);
        InverterMpptInputDTO mppt2 = post("/api/inverter-mppt-inputs", jwtA, mppt2Dto, InverterMpptInputDTO.class);

        // 5. Cross-installation wiring (string in instA1 -> mppt2 in instA2) -> 400
        StringInputAssignmentDTO crossWiring = new StringInputAssignmentDTO();
        crossWiring.setValidFrom(t0);
        crossWiring.setValidUntil(t1);
        SolarStringDTO strRef = new SolarStringDTO();
        strRef.setId(solarString.getId());
        crossWiring.setSolarString(strRef);
        InverterMpptInputDTO mppt2Ref = new InverterMpptInputDTO();
        mppt2Ref.setId(mppt2.getId());
        crossWiring.setMpptInput(mppt2Ref);

        webTestClient
            .post()
            .uri("/api/string-input-assignments")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(crossWiring))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // 6. Valid assignment [t0, t1) + overlapping assignment -> 409 + sequential [t1, t2) -> 201
        InverterMpptInputDTO mppt1Ref = new InverterMpptInputDTO();
        mppt1Ref.setId(mppt1.getId());

        StringInputAssignmentDTO assign1 = new StringInputAssignmentDTO();
        assign1.setValidFrom(t0);
        assign1.setValidUntil(t1);
        assign1.setSolarString(strRef);
        assign1.setMpptInput(mppt1Ref);
        post("/api/string-input-assignments", jwtA, assign1, StringInputAssignmentDTO.class);

        StringInputAssignmentDTO overlap = new StringInputAssignmentDTO();
        overlap.setValidFrom(t0.plus(12, ChronoUnit.HOURS));
        overlap.setValidUntil(t2);
        overlap.setSolarString(strRef);
        overlap.setMpptInput(mppt1Ref);

        webTestClient
            .post()
            .uri("/api/string-input-assignments")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(overlap))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        StringInputAssignmentDTO sequential = new StringInputAssignmentDTO();
        sequential.setValidFrom(t1);
        sequential.setValidUntil(t2);
        sequential.setSolarString(strRef);
        sequential.setMpptInput(mppt1Ref);
        post("/api/string-input-assignments", jwtA, sequential, StringInputAssignmentDTO.class);

        // 7. EquipmentConnection self-loop & cross-installation rejected
        EquipmentConnectionDTO selfConn = new EquipmentConnectionDTO();
        selfConn.setKind(TopologyConnectionKind.DC);
        selfConn.setConnectionCode("SELF-" + s);
        selfConn.setValidFrom(t0);
        selfConn.setFromEquipment(inv1Ref);
        selfConn.setToEquipment(inv1Ref);

        webTestClient
            .post()
            .uri("/api/equipment-connections")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(selfConn))
            .exchange()
            .expectStatus()
            .isBadRequest();

        EquipmentConnectionDTO crossConn = new EquipmentConnectionDTO();
        crossConn.setKind(TopologyConnectionKind.DC);
        crossConn.setConnectionCode("CROSS-" + s);
        crossConn.setValidFrom(t0);
        crossConn.setFromEquipment(inv1Ref);
        crossConn.setToEquipment(inv2Ref);

        webTestClient
            .post()
            .uri("/api/equipment-connections")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(crossConn))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // 8. Cross-tenant IDOR on Org A topology by Org B -> 404
        webTestClient
            .get()
            .uri("/api/solar-sectors/" + sector.getId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();

        webTestClient
            .get()
            .uri("/api/equipment/" + inv1.getId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();

        // 9. RB-LOC-001 precision check without fabricating module level
        assertThat(localizationService.resolvePrecision(false, true, true, true, true)).isEqualTo(LocalizationScope.STRING);
        assertThat(localizationService.resolvePrecision(false, false, true, true, true)).isEqualTo(LocalizationScope.INVERTER);
        assertThat(localizationService.resolvePrecision(false, false, false, true, true)).isEqualTo(LocalizationScope.SECTOR);
        assertThat(localizationService.resolvePrecision(false, false, false, false, true)).isEqualTo(LocalizationScope.INSTALLATION);
        assertThat(localizationService.resolvePrecision(false, false, false, false, false)).isEqualTo(LocalizationScope.UNKNOWN);
    }
}
