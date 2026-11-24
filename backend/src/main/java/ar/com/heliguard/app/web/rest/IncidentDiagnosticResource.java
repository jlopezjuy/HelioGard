package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.PhysicalIntegrityEvaluateResponse;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateRequest;
import ar.com.heliguard.app.service.dto.AlertDiagnosticDTOs.TelemetryAlertEvaluateResponse;
import ar.com.heliguard.app.service.integrity.AlertDiagnosticService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for Sprint S5 alert diagnostic evaluation and physical integrity checks.
 */
@RestController
@RequestMapping("/api")
public class IncidentDiagnosticResource {

    private final AlertDiagnosticService diagnosticService;

    public IncidentDiagnosticResource(AlertDiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping("/incidents/evaluate")
    public Mono<ResponseEntity<TelemetryAlertEvaluateResponse>> evaluateAlert(
        @Valid @RequestBody TelemetryAlertEvaluateRequest request
    ) {
        return diagnosticService.evaluateAlert(request).map(ResponseEntity::ok);
    }

    @PostMapping("/integrity/evaluate")
    public Mono<ResponseEntity<PhysicalIntegrityEvaluateResponse>> evaluatePhysicalIntegrity(
        @Valid @RequestBody PhysicalIntegrityEvaluateRequest request
    ) {
        return diagnosticService.evaluatePhysicalIntegrity(request).map(ResponseEntity::ok);
    }
}
