package ar.com.heliguard.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.config.Constants;
import ar.com.heliguard.app.domain.AuditAnswer;
import ar.com.heliguard.app.domain.AuditEvidence;
import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditReportSignature;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.ServiceAgreement;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import ar.com.heliguard.app.domain.enumeration.FindingSeverity;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.MaintenancePlanStatus;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import ar.com.heliguard.app.domain.enumeration.ServiceAgreementStatus;
import ar.com.heliguard.app.domain.enumeration.SubscriptionStatus;
import ar.com.heliguard.app.repository.AuditAnswerRepository;
import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.repository.AuditReportSignatureRepository;
import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.ServiceAgreementRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.AuditApprovalResponse;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateResponse;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
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
 * Sprint S7 integration tests for audit approval gates (T16), critical-finding plan
 * eligibility (T17), published template/plan-version immutability, and frozen subscription price.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@IntegrationTest
class AuditAndPlanSddIT {

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
    private AuditTemplateRepository templateRepository;

    @Autowired
    private AuditQuestionRepository questionRepository;

    @Autowired
    private AuditReportRepository reportRepository;

    @Autowired
    private AuditAnswerRepository answerRepository;

    @Autowired
    private AuditFindingRepository findingRepository;

    @Autowired
    private AuditEvidenceRepository evidenceRepository;

    @Autowired
    private AuditReportSignatureRepository signatureRepository;

    @Autowired
    private MaintenancePlanRepository planRepository;

    @Autowired
    private MaintenancePlanVersionRepository planVersionRepository;

    @Autowired
    private ServiceAgreementRepository serviceAgreementRepository;

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

    private OrganizationMembership addOwner(Organization org, User user) {
        OrganizationMembership m = new OrganizationMembership();
        m.setOrganizationId(org.getId());
        m.setMemberId(user.getId());
        m.setRole(MembershipRole.OWNER);
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
    void t16_t17_and_s7ImmutabilityAndFrozenSubscription() throws Exception {
        String s = suffix();
        User userA = createUser("aud-a-" + s);
        User userB = createUser("aud-b-" + s);
        Organization orgA = createOrg("aud-orga-" + s);
        Organization orgB = createOrg("aud-orgb-" + s);
        OrganizationMembership ownerA = addOwner(orgA, userA);
        addOwner(orgB, userB);

        Installation instA = createInstallation(orgA, "AUD-A-" + s);
        Instant now = Instant.now();

        AuditTemplate template = new AuditTemplate();
        template.setOrganizationId(orgA.getId());
        template.setTitle("Electrical Safety " + s);
        template.setTemplateVersion("v1");
        template.setActive(true);
        template.setPublishedAt(now.minus(1, ChronoUnit.DAYS));
        template = templateRepository.save(template).block();

        AuditQuestion q1 = new AuditQuestion();
        q1.setOrganizationId(orgA.getId());
        q1.setAuditTemplateId(template.getId());
        q1.setQuestionCode("Q_GROUNDING");
        q1.setLabel("Grounding continuity verified?");
        q1.setAnswerType("BOOLEAN");
        q1.setSortOrder(1);
        q1.setMandatory(true);
        q1 = questionRepository.save(q1).block();

        AuditReport report = new AuditReport();
        report.setOrganizationId(orgA.getId());
        report.setInstallationId(instA.getId());
        report.setAuditTemplateId(template.getId());
        report.setAuditorMembershipId(ownerA.getId());
        report.setStatus(AuditStatus.DRAFT);
        report.setStartedAt(now);
        report = reportRepository.save(report).block();

        String jwtA = login(userA.getLogin());
        String jwtB = login(userB.getLogin());

        // T16: Missing mandatory question -> 400 with missing question code
        webTestClient
            .post()
            .uri("/api/audit-reports/" + report.getId() + "/approve")
            .header("Authorization", "Bearer " + jwtA)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.detail")
            .value(detail -> assertThat((String) detail).contains("Q_GROUNDING"));

        // Answer mandatory question + add CRITICAL finding with evidence + signature
        AuditAnswer ans = new AuditAnswer();
        ans.setOrganizationId(orgA.getId());
        ans.setAuditReportId(report.getId());
        ans.setAuditQuestionId(q1.getId());
        ans.setAnswerBoolean(true);
        ans.setAnsweredAt(now);
        answerRepository.save(ans).block();

        AuditFinding critFinding = new AuditFinding();
        critFinding.setOrganizationId(orgA.getId());
        critFinding.setAuditReportId(report.getId());
        critFinding.setTitle("DC Arc Fault Hazard");
        critFinding.setDescription("Exposed DC arc fault hazard on combiner box");
        critFinding.setSeverity(FindingSeverity.CRITICAL);
        critFinding = findingRepository.save(critFinding).block();

        AuditEvidence ev = new AuditEvidence();
        ev.setOrganizationId(orgA.getId());
        ev.setFindingId(critFinding.getId());
        ev.setStorageKey("s3://audits/crit-" + s + ".jpg");
        ev.setMimeType("image/jpeg");
        evidenceRepository.save(ev).block();

        AuditReportSignature sig = new AuditReportSignature();
        sig.setOrganizationId(orgA.getId());
        sig.setAuditReportId(report.getId());
        sig.setSignerMembershipId(ownerA.getId());
        sig.setSignedAt(now);
        sig.setDocumentSha256("a".repeat(64));
        signatureRepository.save(sig).block();

        AuditApprovalResponse approved = webTestClient
            .post()
            .uri("/api/audit-reports/" + report.getId() + "/approve")
            .header("Authorization", "Bearer " + jwtA)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(AuditApprovalResponse.class)
            .returnResult()
            .getResponseBody();
        assertThat(approved.approved()).isTrue();
        assertThat(approved.status()).isEqualTo(AuditStatus.APPROVED);

        // RB-AUD-002: Published AuditTemplate is immutable (409)
        AuditTemplateDTO tmplDto = new AuditTemplateDTO();
        tmplDto.setId(template.getId());
        tmplDto.setTitle("Mutated");
        tmplDto.setTemplateVersion("v1");
        tmplDto.setActive(true);
        webTestClient
            .put()
            .uri("/api/audit-templates/" + template.getId())
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tmplDto))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // T17: Critical finding -> REQUIRES_REMEDIATION with requiresCorrection == true & autoContracted == false
        MaintenancePlan plan = new MaintenancePlan();
        plan.setOrganizationId(orgA.getId());
        plan.setName("Care Pro " + s);
        plan.setStatus(MaintenancePlanStatus.ACTIVE);
        plan = planRepository.save(plan).block();

        MaintenancePlanVersion version = new MaintenancePlanVersion();
        version.setOrganizationId(orgA.getId());
        version.setMaintenancePlanId(plan.getId());
        version.setVersionCode("2026.1");
        version.setPublishedAt(now.minus(1, ChronoUnit.DAYS));
        version.setCurrencyCode("USD");
        version.setRecurringAmount(new BigDecimal("149.99"));
        version.setTermsReference("terms://v2026.1");
        version = planVersionRepository.save(version).block();

        PlanEligibilityEvaluateResponse t17Resp = webTestClient
            .post()
            .uri("/api/plan-eligibility-assessments/evaluate")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(new PlanEligibilityEvaluateRequest(instA.getId(), report.getId(), version.getId())))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PlanEligibilityEvaluateResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(t17Resp.status()).isEqualTo(PlanEligibilityStatus.REQUIRES_REMEDIATION);
        assertThat(t17Resp.requiresCorrection()).isTrue();
        assertThat(t17Resp.autoContracted()).isFalse();

        ServiceAgreement agreement = new ServiceAgreement();
        agreement.setOrganizationId(orgA.getId());
        agreement.setProviderOrganizationId(orgA.getId());
        agreement.setInstallationId(instA.getId());
        agreement.setStatus(ServiceAgreementStatus.ACTIVE);
        agreement.setStartsAt(now.minus(1, ChronoUnit.DAYS));
        agreement = serviceAgreementRepository.save(agreement).block();

        // RB-PLAN-002: Subscription freezes recurringAmount & currencyCode from MaintenancePlanVersion
        PlanSubscriptionDTO subDto = new PlanSubscriptionDTO();
        subDto.setStatus(SubscriptionStatus.PROPOSED);
        subDto.setAgreedAmount(new BigDecimal("1.00")); // client tries to spoof $1
        subDto.setCurrencyCode("EUR");
        InstallationDTO instRef = new InstallationDTO();
        instRef.setId(instA.getId());
        subDto.setInstallation(instRef);
        MaintenancePlanVersionDTO verRef = new MaintenancePlanVersionDTO();
        verRef.setId(version.getId());
        subDto.setPlanVersion(verRef);
        ServiceAgreementDTO agRef = new ServiceAgreementDTO();
        agRef.setId(agreement.getId());
        subDto.setServiceAgreement(agRef);

        PlanSubscriptionDTO createdSub = webTestClient
            .post()
            .uri("/api/plan-subscriptions")
            .header("Authorization", "Bearer " + jwtA)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDto))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlanSubscriptionDTO.class)
            .returnResult()
            .getResponseBody();

        assertThat(createdSub.getAgreedAmount()).isEqualByComparingTo("149.99");
        assertThat(createdSub.getCurrencyCode()).isEqualTo("USD");

        // Cross-tenant IDOR by Org B -> 404
        webTestClient
            .post()
            .uri("/api/audit-reports/" + report.getId() + "/approve")
            .header("Authorization", "Bearer " + jwtB)
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}
