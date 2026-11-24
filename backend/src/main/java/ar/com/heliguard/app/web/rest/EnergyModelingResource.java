package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteRequest;
import ar.com.heliguard.app.service.dto.EnergyModelingDTOs.EnergyModelExecuteResponse;
import ar.com.heliguard.app.service.energy.EnergyModelingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for executing versioned physical energy modeling runs (Sprint S4).
 */
@RestController
@RequestMapping("/api/energy-runs")
public class EnergyModelingResource {

    private final EnergyModelingService energyModelingService;

    public EnergyModelingResource(EnergyModelingService energyModelingService) {
        this.energyModelingService = energyModelingService;
    }

    @PostMapping("/execute")
    public Mono<ResponseEntity<EnergyModelExecuteResponse>> executeModel(@Valid @RequestBody EnergyModelExecuteRequest request) {
        return energyModelingService.execute(request).map(ResponseEntity::ok);
    }
}
