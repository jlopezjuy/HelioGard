package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateRequest;
import ar.com.heliguard.app.service.dto.EconomicEvaluationDTOs.EconomicEvaluateResponse;
import ar.com.heliguard.app.service.economy.EconomicEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for Sprint S6 loss attribution and economic evaluation.
 */
@RestController
@RequestMapping("/api/economic-assessments")
public class EconomicEvaluationResource {

    private final EconomicEvaluationService economicEvaluationService;

    public EconomicEvaluationResource(EconomicEvaluationService economicEvaluationService) {
        this.economicEvaluationService = economicEvaluationService;
    }

    @PostMapping("/evaluate")
    public Mono<ResponseEntity<EconomicEvaluateResponse>> evaluate(@Valid @RequestBody EconomicEvaluateRequest request) {
        return economicEvaluationService.evaluate(request).map(ResponseEntity::ok);
    }
}
