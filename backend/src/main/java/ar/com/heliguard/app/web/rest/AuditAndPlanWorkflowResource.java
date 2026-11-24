package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.audit.AuditAndPlanWorkflowService;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.AuditApprovalResponse;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AuditAndPlanWorkflowDTOs.PlanEligibilityEvaluateResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for Sprint S7 audit approval and plan eligibility evaluation.
 */
@RestController
@RequestMapping("/api")
public class AuditAndPlanWorkflowResource {

    private final AuditAndPlanWorkflowService workflowService;

    public AuditAndPlanWorkflowResource(AuditAndPlanWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/audit-reports/{id}/approve")
    public Mono<ResponseEntity<AuditApprovalResponse>> approveAuditReport(@PathVariable("id") Long id) {
        return workflowService.approveAuditReport(id).map(ResponseEntity::ok);
    }

    @PostMapping("/plan-eligibility-assessments/evaluate")
    public Mono<ResponseEntity<PlanEligibilityEvaluateResponse>> evaluatePlanEligibility(
        @Valid @RequestBody PlanEligibilityEvaluateRequest request
    ) {
        return workflowService.evaluateEligibility(request).map(ResponseEntity::ok);
    }
}
