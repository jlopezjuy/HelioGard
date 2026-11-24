package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * DTOs for Sprint S7 audit approval and plan eligibility evaluation workflows.
 */
public final class AuditAndPlanWorkflowDTOs {

    private AuditAndPlanWorkflowDTOs() {}

    public record AuditApprovalResponse(
        Long auditReportId,
        AuditStatus status,
        boolean approved,
        List<String> missingMandatoryQuestions,
        List<String> missingEvidenceCriticalFindings,
        boolean signaturePresent
    ) implements Serializable {}

    public record PlanEligibilityEvaluateRequest(
        @NotNull Long installationId,
        @NotNull Long auditReportId,
        @NotNull Long planVersionId
    ) implements Serializable {}

    public record PlanEligibilityEvaluateResponse(
        Long assessmentId,
        Long installationId,
        PlanEligibilityStatus status,
        boolean requiresCorrection,
        boolean autoContracted,
        String rationale
    ) implements Serializable {}
}
