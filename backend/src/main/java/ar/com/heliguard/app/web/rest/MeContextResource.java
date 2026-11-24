package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.MeContextDTO;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller exposing the authenticated user's resolved tenant context.
 */
@RestController
@RequestMapping("/api/me")
public class MeContextResource {

    private final TenantContextService tenantContextService;

    public MeContextResource(TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
    }

    @GetMapping("/context")
    public Mono<MeContextDTO> getMeContext() {
        return tenantContextService.getCurrentUserContext();
    }
}
