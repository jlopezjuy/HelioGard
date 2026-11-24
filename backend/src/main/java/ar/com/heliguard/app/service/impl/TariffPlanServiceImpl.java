package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.service.TariffPlanService;
import ar.com.heliguard.app.service.dto.TariffPlanDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.TariffPlanMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link TariffPlan}.
 */
@Service
@Transactional
public class TariffPlanServiceImpl implements TariffPlanService {

    private final TariffPlanRepository repository;
    private final TariffPlanMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public TariffPlanServiceImpl(
        TariffPlanRepository repository,
        TariffPlanMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<TariffPlanDTO> save(TariffPlanDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        TariffPlan entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<TariffPlanDTO> update(TariffPlanDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()))
                            .flatMap(inst -> {
                                TariffPlan entity = mapper.toEntity(dto);
                                entity.setOrganizationId(existing.getOrganizationId());
                                entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                return repository.save(entity);
                            })
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<TariffPlanDTO> partialUpdate(TariffPlanDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    mapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return repository.save(existing);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffPlanDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffPlanDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TariffPlanDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireRoleForScope(PermissionScope.MANAGE)
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(p -> repository.deleteById(p.getId()));
    }

    private Mono<TariffPlan> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(p -> orgId.equals(p.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "TARIFF_PLAN_NOT_FOUND", "TariffPlan not found")));
    }
}
