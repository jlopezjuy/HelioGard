package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.ClimateRiskProfileRepository;
import ar.com.heliguard.app.service.ClimateRiskProfileService;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.ClimateRiskProfileMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Global catalog service for {@link ar.com.heliguard.app.domain.ClimateRiskProfile}.
 */
@Service
@Transactional
public class ClimateRiskProfileServiceImpl implements ClimateRiskProfileService {

    private final ClimateRiskProfileRepository repository;
    private final ClimateRiskProfileMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public ClimateRiskProfileServiceImpl(
        ClimateRiskProfileRepository repository,
        ClimateRiskProfileMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<ClimateRiskProfileDTO> save(ClimateRiskProfileDTO dto) {
        return permissionService
            .requireManageRole()
            .then(Mono.defer(() -> repository.save(mapper.toEntity(dto))))
            .map(mapper::toDto);
    }

    @Override
    public Mono<ClimateRiskProfileDTO> update(ClimateRiskProfileDTO dto) {
        return permissionService
            .requireManageRole()
            .then(
                repository
                    .findById(dto.getId())
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "CLIMATE_PROFILE_NOT_FOUND", "Not found")))
                    .flatMap(existing -> repository.save(mapper.toEntity(dto)))
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<ClimateRiskProfileDTO> partialUpdate(ClimateRiskProfileDTO dto) {
        return permissionService
            .requireManageRole()
            .then(
                repository
                    .findById(dto.getId())
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "CLIMATE_PROFILE_NOT_FOUND", "Not found")))
                    .map(existing -> {
                        mapper.partialUpdate(existing, dto);
                        return existing;
                    })
                    .flatMap(repository::save)
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClimateRiskProfileDTO> findAll() {
        return tenantContextService.requireCurrentTenant().thenMany(repository.findAll()).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().then(repository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ClimateRiskProfileDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .then(
                repository
                    .findById(id)
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "CLIMATE_PROFILE_NOT_FOUND", "Not found")))
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService.requireManageRole().then(repository.deleteById(id));
    }
}
