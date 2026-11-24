package ar.com.heliguard.app.service.audit;

import ar.com.heliguard.app.domain.AuditAnswer;
import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.PlanEligibilityAssessment;
import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import ar.com.heliguard.app.domain.enumeration.FindingSeverity;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import ar.com.heliguard.app.repository.AuditAnswerRepository;
import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.repository.AuditReportSignatureRepository;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.repository.PlanEligibilityAssessmentRepository;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.AuditApprovalResponse;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateResponse;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.tenant.PermissionService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Enforces Sprint S7 audit approval gates (RB-AUD-001, T16) and
 * versioned plan eligibility rules (RB-PLAN-001, T17).
 */
@Service
@Transactional
public class AuditAndPlanWorkflowService {

    private final PermissionService permissionService;
    private final AuditReportRepository reportRepository;
    private final AuditQuestionRepository questionRepository;
    private final AuditAnswerRepository answerRepository;
    private final AuditFindingRepository findingRepository;
    private final AuditEvidenceRepository evidenceRepository;
    private final AuditReportSignatureRepository signatureRepository;
    private final MaintenancePlanVersionRepository planVersionRepository;
    private final PlanEligibilityAssessmentRepository eligibilityRepository;

    public AuditAndPlanWorkflowService(
        PermissionService permissionService,
        AuditReportRepository reportRepository,
        AuditQuestionRepository questionRepository,
        AuditAnswerRepository answerRepository,
        AuditFindingRepository findingRepository,
        AuditEvidenceRepository evidenceRepository,
        AuditReportSignatureRepository signatureRepository,
        MaintenancePlanVersionRepository planVersionRepository,
        PlanEligibilityAssessmentRepository eligibilityRepository
    ) {
        this.permissionService = permissionService;
        this.reportRepository = reportRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.findingRepository = findingRepository;
        this.evidenceRepository = evidenceRepository;
        this.signatureRepository = signatureRepository;
        this.planVersionRepository = planVersionRepository;
        this.eligibilityRepository = eligibilityRepository;
    }

    public Mono<AuditApprovalResponse> approveAuditReport(Long reportId) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                reportRepository
                    .findById(reportId)
                    .filter(r -> tenant.organizationId().equals(r.getOrganizationId()))
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_REPORT_NOT_FOUND", "AuditReport not found"))
                    )
                    .flatMap(this::validateAndApprove)
            );
    }

    public Mono<PlanEligibilityEvaluateResponse> evaluateEligibility(PlanEligibilityEvaluateRequest request) {
        return permissionService
            .requireAccessToInstallation(request.installationId(), PermissionScope.AUDIT)
            .flatMap(installation ->
                reportRepository
                    .findById(request.auditReportId())
                    .filter(r ->
                        installation.getOrganizationId().equals(r.getOrganizationId()) &&
                        installation.getId().equals(r.getInstallationId())
                    )
                    .switchIfEmpty(
                        Mono.error(
                            new DomainRuleException(
                                HttpStatus.NOT_FOUND,
                                "AUDIT_REPORT_NOT_FOUND",
                                "AuditReport not found for installation"
                            )
                        )
                    )
                    .flatMap(report ->
                        planVersionRepository
                            .findById(request.planVersionId())
                            .filter(v -> installation.getOrganizationId().equals(v.getOrganizationId()))
                            .switchIfEmpty(
                                Mono.error(
                                    new DomainRuleException(
                                        HttpStatus.NOT_FOUND,
                                        "PLAN_VERSION_NOT_FOUND",
                                        "MaintenancePlanVersion not found in tenant"
                                    )
                                )
                            )
                            .flatMap(version -> assessEligibility(installation.getOrganizationId(), installation.getId(), report, version.getId()))
                    )
            );
    }

    private Mono<AuditApprovalResponse> validateAndApprove(AuditReport report) {
        Mono<List<String>> missingMandatoryMono = Mono.zip(
            questionRepository.findByAuditTemplate(report.getAuditTemplateId()).filter(q -> Boolean.TRUE.equals(q.getMandatory())).collectList(),
            answerRepository.findByAuditReport(report.getId()).collectList()
        )
            .map(tuple -> {
                Set<Long> answeredQuestionIds = tuple
                    .getT2()
                    .stream()
                    .filter(AuditAndPlanWorkflowService::hasAnswerContent)
                    .map(AuditAnswer::getAuditQuestionId)
                    .collect(Collectors.toSet());
                return tuple
                    .getT1()
                    .stream()
                    .filter(q -> !answeredQuestionIds.contains(q.getId()))
                    .map(AuditQuestion::getQuestionCode)
                    .toList();
            });

        Mono<List<String>> missingCriticalEvidenceMono = findingRepository
            .findByAuditReport(report.getId())
            .filter(f -> f.getSeverity() == FindingSeverity.CRITICAL && f.getResolvedAt() == null)
            .flatMap(finding ->
                evidenceRepository
                    .findByFinding(finding.getId())
                    .hasElements()
                    .filter(hasEv -> !hasEv)
                    .map(ignored -> finding.getTitle())
            )
            .collectList();

        Mono<Boolean> hasSignatureMono = signatureRepository.findByAuditReport(report.getId()).hasElements();

        return Mono.zip(missingMandatoryMono, missingCriticalEvidenceMono, hasSignatureMono)
            .flatMap(tuple -> {
                List<String> missingQuestions = tuple.getT1();
                List<String> missingEvidences = tuple.getT2();
                boolean hasSignature = tuple.getT3();

                if (!missingQuestions.isEmpty()) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "AUDIT_MANDATORY_QUESTIONS_MISSING",
                            "Missing mandatory audit questions: " + String.join(", ", missingQuestions)
                        )
                    );
                }
                if (!missingEvidences.isEmpty()) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "AUDIT_CRITICAL_FINDING_EVIDENCE_REQUIRED",
                            "Critical findings require evidence before approval: " + String.join(", ", missingEvidences)
                        )
                    );
                }
                if (!hasSignature) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "AUDIT_SIGNATURE_REQUIRED",
                            "Professional signature is required before approval"
                        )
                    );
                }

                report.setStatus(AuditStatus.APPROVED);
                if (report.getCompletedAt() == null) {
                    report.setCompletedAt(Instant.now());
                }
                return reportRepository
                    .save(report)
                    .map(saved -> new AuditApprovalResponse(saved.getId(), saved.getStatus(), true, List.of(), List.of(), true));
            });
    }

    private Mono<PlanEligibilityEvaluateResponse> assessEligibility(
        Long orgId,
        Long installationId,
        AuditReport report,
        Long planVersionId
    ) {
        if (report.getStatus() != AuditStatus.APPROVED) {
            return saveEligibility(
                orgId,
                installationId,
                report.getId(),
                planVersionId,
                PlanEligibilityStatus.INCOMPLETE,
                true,
                "AuditReport must be APPROVED before plan eligibility can be confirmed"
            );
        }

        return findingRepository
            .findByAuditReport(report.getId())
            .filter(f -> f.getResolvedAt() == null)
            .collectList()
            .flatMap(unresolved -> {
                long criticalCount = unresolved.stream().filter(f -> f.getSeverity() == FindingSeverity.CRITICAL).count();
                long highCount = unresolved.stream().filter(f -> f.getSeverity() == FindingSeverity.HIGH).count();

                if (criticalCount > 0) {
                    // T17 / RB-PLAN-001: Critical electrical fault blocks safe operation services
                    return saveEligibility(
                        orgId,
                        installationId,
                        report.getId(),
                        planVersionId,
                        PlanEligibilityStatus.REQUIRES_REMEDIATION,
                        true,
                        "Unresolved CRITICAL audit finding(s) block service eligibility until remediated"
                    );
                }
                if (highCount > 0) {
                    return saveEligibility(
                        orgId,
                        installationId,
                        report.getId(),
                        planVersionId,
                        PlanEligibilityStatus.CONDITIONAL,
                        true,
                        "Unresolved HIGH audit finding(s) require conditional follow-up"
                    );
                }
                return saveEligibility(
                    orgId,
                    installationId,
                    report.getId(),
                    planVersionId,
                    PlanEligibilityStatus.ELIGIBLE,
                    false,
                    "Approved audit with no blocking findings; eligible for plan proposal (not auto-contracted)"
                );
            });
    }

    private Mono<PlanEligibilityEvaluateResponse> saveEligibility(
        Long orgId,
        Long installationId,
        Long reportId,
        Long planVersionId,
        PlanEligibilityStatus status,
        boolean requiresCorrection,
        String rationale
    ) {
        PlanEligibilityAssessment assessment = new PlanEligibilityAssessment();
        assessment.setOrganizationId(orgId);
        assessment.setInstallationId(installationId);
        assessment.setAuditReportId(reportId);
        assessment.setPlanVersionId(planVersionId);
        assessment.setStatus(status);
        assessment.setRequiresCorrection(requiresCorrection);
        assessment.setAssessedAt(Instant.now());
        assessment.setRationale(rationale);
        return eligibilityRepository
            .save(assessment)
            .map(saved ->
                new PlanEligibilityEvaluateResponse(
                    saved.getId(),
                    saved.getInstallationId(),
                    saved.getStatus(),
                    Boolean.TRUE.equals(saved.getRequiresCorrection()),
                    false,
                    saved.getRationale()
                )
            );
    }

    private static boolean hasAnswerContent(AuditAnswer a) {
        return (a.getAnswerText() != null && !a.getAnswerText().isBlank()) || a.getAnswerNumber() != null || a.getAnswerBoolean() != null;
    }
}
