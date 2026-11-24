package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.DroneDispatchResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyRequest;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.RecoveryVerifyResponse;
import ar.com.heliguard.app.service.dto.MaintenanceAndDroneWorkflowDTOs.WorkOrderAcceptResponse;
import ar.com.heliguard.app.service.maintenance.MaintenanceAndDroneWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for Sprint S8 work order acceptance, simulated drone dispatch, and recovery verification.
 */
@RestController
@RequestMapping("/api")
public class MaintenanceAndDroneWorkflowResource {

    private final MaintenanceAndDroneWorkflowService workflowService;

    public MaintenanceAndDroneWorkflowResource(MaintenanceAndDroneWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/work-orders/{id}/accept")
    public Mono<ResponseEntity<WorkOrderAcceptResponse>> acceptWorkOrder(@PathVariable("id") Long id) {
        return workflowService.acceptWorkOrder(id).map(ResponseEntity::ok);
    }

    @PostMapping("/drone-missions/{id}/dispatch")
    public Mono<ResponseEntity<DroneDispatchResponse>> dispatchDroneMission(
        @PathVariable("id") Long id,
        @RequestBody(required = false) DroneDispatchRequest request
    ) {
        return workflowService.dispatchSimulatedMission(id, request).map(ResponseEntity::ok);
    }

    @PostMapping("/recovery-assessments/verify")
    public Mono<ResponseEntity<RecoveryVerifyResponse>> verifyRecovery(@Valid @RequestBody RecoveryVerifyRequest request) {
        return workflowService.verifyRecovery(request).map(ResponseEntity::ok);
    }
}
