package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SiteDTO;
import ar.com.heliguard.app.web.rest.AuthenticateController.ActiveOrganizationVM;
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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Sprint S1 SDD integration tests (T01-T05 + multi-org, revoked membership, IDOR, and domain invariants).
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class TenantSddIT {

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

    private Organization createOrg(String name, String slug) {
        Organization org = new Organization();
        org.setName(name);
        org.setSlug(slug);
        org.setStatus(OrganizationStatus.ACTIVE);
        org.setKind(OrganizationKind.BUSINESS);
        org.setCreatedAt(Instant.now());
        return organizationRepository.save(org).block();
    }

    private OrganizationMembership addMembership(Organization org, User user, MembershipRole role, MembershipStatus status) {
        OrganizationMembership m = new OrganizationMembership();
        m.setOrganizationId(org.getId());
        m.setMemberId(user.getId());
        m.setRole(role);
        m.setStatus(status);
        m.setActivatedAt(Instant.now());
        return membershipRepository.save(m).block();
    }

    private Site createSite(Organization org, String name) {
        Site site = new Site();
        site.setName(name);
        site.setTimeZone("America/Argentina/Buenos_Aires");
        site.setOrganizationId(org.getId());
        return siteRepository.save(site).block();
    }

    private Installation createInstallation(Organization org, Site site, String code) {
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

    private String loginAndGetToken(String username) throws Exception {
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
        JsonNode json = om.readTree(body);
        return json.get("id_token").asText();
    }

    @Test
    void t01_singleActiveOrgListsOnlyOwnAndValidGrantedInstallations() throws Exception {
        String s = suffix();
        User userA = createUser("usera-" + s);
        Organization orgA = createOrg("Org A " + s, "org-a-" + s);
        Organization orgB = createOrg("Org B " + s, "org-b-" + s);
        addMembership(orgA, userA, MembershipRole.OWNER, MembershipStatus.ACTIVE);

        Site siteA = createSite(orgA, "Site A " + s);
        Site siteB = createSite(orgB, "Site B " + s);
        Installation instA1 = createInstallation(orgA, siteA, "A1-" + s);
        Installation instB1 = createInstallation(orgB, siteB, "B1-" + s);
        Installation instB2 = createInstallation(orgB, siteB, "B2-" + s);

        InstallationAccessGrant validGrant = new InstallationAccessGrant();
        validGrant.setOrganizationId(orgB.getId());
        validGrant.setInstallationId(instB1.getId());
        validGrant.setGranteeOrganizationId(orgA.getId());
        validGrant.setScope(PermissionScope.READ_ONLY);
        validGrant.setValidFrom(Instant.now().minus(1, ChronoUnit.HOURS));
        validGrant.setValidUntil(Instant.now().plus(1, ChronoUnit.HOURS));
        grantRepository.save(validGrant).block();

        InstallationAccessGrant expiredGrant = new InstallationAccessGrant();
        expiredGrant.setOrganizationId(orgB.getId());
        expiredGrant.setInstallationId(instB2.getId());
        expiredGrant.setGranteeOrganizationId(orgA.getId());
        expiredGrant.setScope(PermissionScope.READ_ONLY);
        expiredGrant.setValidFrom(Instant.now().minus(2, ChronoUnit.HOURS));
        expiredGrant.setValidUntil(Instant.now().minus(1, ChronoUnit.MINUTES));
        grantRepository.save(expiredGrant).block();

        String jwt = loginAndGetToken(userA.getLogin());

        webTestClient
            .get()
            .uri("/api/me/context")
            .header("Authorization", "Bearer " + jwt)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.activeOrganizationId")
            .isEqualTo(orgA.getId())
            .jsonPath("$.selectionRequired")
            .isEqualTo(false);

        List<InstallationDTO> visible = webTestClient
            .get()
            .uri("/api/installations?page=0&size=20")
            .header("Authorization", "Bearer " + jwt)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("X-Total-Count", "2")
            .expectBodyList(InstallationDTO.class)
            .returnResult()
            .getResponseBody();

        assertThat(visible).extracting(InstallationDTO::getId).containsExactlyInAnyOrder(instA1.getId(), instB1.getId());
    }

    @Test
    void t02_multiOrgRequiresActiveOrgSelectionAndIgnoresClientOrgOverrides() throws Exception {
        String s = suffix();
        User multiUser = createUser("multi-" + s);
        Organization orgA = createOrg("Org A " + s, "org-a-" + s);
        Organization orgB = createOrg("Org B " + s, "org-b-" + s);
        addMembership(orgA, multiUser, MembershipRole.OWNER, MembershipStatus.ACTIVE);
        addMembership(orgB, multiUser, MembershipRole.OWNER, MembershipStatus.ACTIVE);

        Site siteA = createSite(orgA, "Site A " + s);
        long countBefore = installationRepository.count().block();

        String initialJwt = loginAndGetToken(multiUser.getLogin());

        webTestClient
            .get()
            .uri("/api/me/context")
            .header("Authorization", "Bearer " + initialJwt)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.selectionRequired")
            .isEqualTo(true)
            .jsonPath("$.memberships.length()")
            .isEqualTo(2);

        InstallationDTO payload = new InstallationDTO();
        payload.setCode("NEW-" + s);
        payload.setName("New Plant");
        payload.setGridMode(GridMode.GRID_TIED);
        payload.setNominalDcKw(new BigDecimal("50.00"));
        SiteDTO siteDto = new SiteDTO();
        siteDto.setId(siteA.getId());
        payload.setSite(siteDto);
        // Attempt client-side org spoofing to Org B
        OrganizationDTO spoofedOrg = new OrganizationDTO();
        spoofedOrg.setId(orgB.getId());
        payload.setOrganization(spoofedOrg);

        webTestClient
            .post()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + initialJwt)
            .header("X-Organization-Id", String.valueOf(orgB.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        assertThat(installationRepository.count().block()).isEqualTo(countBefore);

        // Select Org A via auth endpoint
        byte[] switchResp = webTestClient
            .post()
            .uri("/api/auth/active-organization")
            .header("Authorization", "Bearer " + initialJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new ActiveOrganizationVM(orgA.getId())))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
        String orgAJwt = om.readTree(switchResp).get("id_token").asText();

        InstallationDTO created = webTestClient
            .post()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + orgAJwt)
            .header("X-Organization-Id", String.valueOf(orgB.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstallationDTO.class)
            .returnResult()
            .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.getOrganization().getId()).isEqualTo(orgA.getId());
        assertThat(created.getStatus()).isEqualTo(InstallationStatus.COMMISSIONING);
    }

    @Test
    void t03_revokedMembershipDeniesPreviouslyIssuedJwt() throws Exception {
        String s = suffix();
        User user = createUser("revoked-" + s);
        Organization orgA = createOrg("Org A " + s, "org-a-" + s);
        OrganizationMembership membership = addMembership(orgA, user, MembershipRole.OWNER, MembershipStatus.ACTIVE);

        String jwt = loginAndGetToken(user.getLogin());

        membership.setStatus(MembershipStatus.REVOKED);
        membershipRepository.save(membership).block();

        webTestClient
            .get()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + jwt)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    void t04_t05_idorUniformDenialAndGrantScopedOnlyToGrantedInstallation() throws Exception {
        String s = suffix();
        User userA = createUser("idora-" + s);
        User userB = createUser("idorb-" + s);
        Organization orgA = createOrg("Org A " + s, "org-a-" + s);
        Organization orgB = createOrg("Org B " + s, "org-b-" + s);
        addMembership(orgA, userA, MembershipRole.OWNER, MembershipStatus.ACTIVE);
        addMembership(orgB, userB, MembershipRole.OPERATOR, MembershipStatus.ACTIVE);

        Site siteA = createSite(orgA, "Site A " + s);
        Installation instA1 = createInstallation(orgA, siteA, "A1-" + s);
        Installation instA2 = createInstallation(orgA, siteA, "A2-" + s);

        InstallationAccessGrant grant = new InstallationAccessGrant();
        grant.setOrganizationId(orgA.getId());
        grant.setInstallationId(instA1.getId());
        grant.setGranteeOrganizationId(orgB.getId());
        grant.setScope(PermissionScope.MAINTENANCE);
        grant.setValidFrom(Instant.now().minus(1, ChronoUnit.HOURS));
        grant.setValidUntil(Instant.now().plus(1, ChronoUnit.HOURS));
        grant = grantRepository.save(grant).block();

        String jwtB = loginAndGetToken(userB.getLogin());

        // T05: B can read granted A-1
        webTestClient
            .get()
            .uri("/api/installations/" + instA1.getId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isOk();

        // T04 / T05: B cannot read non-granted A-2, same 404 as non-existent ID
        webTestClient
            .get()
            .uri("/api/installations/" + instA2.getId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();

        webTestClient
            .get()
            .uri("/api/installations/99999999")
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();

        // Revoking grant immediately blocks access to A-1
        grant.setRevokedAt(Instant.now());
        grantRepository.save(grant).block();

        webTestClient
            .get()
            .uri("/api/installations/" + instA1.getId())
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void t06_domainInvariantsRejectInvalidKwCrossTenantSiteAndOrgReassign() throws Exception {
        String s = suffix();
        User userA = createUser("inva-" + s);
        Organization orgA = createOrg("Org A " + s, "org-a-" + s);
        Organization orgB = createOrg("Org B " + s, "org-b-" + s);
        addMembership(orgA, userA, MembershipRole.OWNER, MembershipStatus.ACTIVE);

        Site siteA = createSite(orgA, "Site A " + s);
        Site siteB = createSite(orgB, "Site B " + s);
        Installation instA1 = createInstallation(orgA, siteA, "DUP-" + s);

        String jwtA = loginAndGetToken(userA.getLogin());

        // nominalDcKw <= 0 rejected
        InstallationDTO zeroKw = new InstallationDTO();
        zeroKw.setCode("Z-" + s);
        zeroKw.setName("Zero KW");
        zeroKw.setGridMode(GridMode.GRID_TIED);
        zeroKw.setNominalDcKw(BigDecimal.ZERO);
        SiteDTO siteADto = new SiteDTO();
        siteADto.setId(siteA.getId());
        zeroKw.setSite(siteADto);

        webTestClient
            .post()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(zeroKw))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Site from Org B rejected
        InstallationDTO crossSite = new InstallationDTO();
        crossSite.setCode("CS-" + s);
        crossSite.setName("Cross Site");
        crossSite.setGridMode(GridMode.GRID_TIED);
        crossSite.setNominalDcKw(new BigDecimal("10.00"));
        SiteDTO siteBDto = new SiteDTO();
        siteBDto.setId(siteB.getId());
        crossSite.setSite(siteBDto);

        webTestClient
            .post()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(crossSite))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Duplicate code in Org A rejected (409)
        InstallationDTO dup = new InstallationDTO();
        dup.setCode("DUP-" + s);
        dup.setName("Dup");
        dup.setGridMode(GridMode.GRID_TIED);
        dup.setNominalDcKw(new BigDecimal("10.00"));
        dup.setSite(siteADto);

        webTestClient
            .post()
            .uri("/api/installations")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dup))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // Generic organization reassignment forbidden on PUT
        InstallationDTO reassign = new InstallationDTO();
        reassign.setId(instA1.getId());
        reassign.setCode(instA1.getCode());
        reassign.setName("Reassigned");
        reassign.setGridMode(GridMode.GRID_TIED);
        reassign.setStatus(InstallationStatus.ACTIVE);
        reassign.setNominalDcKw(new BigDecimal("100.00"));
        reassign.setSite(siteADto);
        OrganizationDTO orgBDto = new OrganizationDTO();
        orgBDto.setId(orgB.getId());
        reassign.setOrganization(orgBDto);

        webTestClient
            .put()
            .uri("/api/installations/" + instA1.getId())
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(reassign))
            .exchange()
            .expectStatus()
            .isForbidden();
    }
}
