package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.service.MaintenancePlanService;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.MaintenancePlanMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link MaintenancePlan}.
 */
@Service
@Transactional
public class MaintenancePlanServiceImpl implements MaintenancePlanService {

    private final MaintenancePlanRepository repository;
    private final MaintenancePlanMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public MaintenancePlanServiceImpl(
        MaintenancePlanRepository repository,
        MaintenancePlanMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<MaintenancePlanDTO> save(MaintenancePlanDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                MaintenancePlan entity = mapper.toEntity(dto);
                entity.setId(null);
                entity.setOrganizationId(tenant.organizationId());
                entity.setOrganization(new Organization().id(tenant.organizationId()));
                return repository.save(entity);
            })
            .map(mapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanDTO> update(MaintenancePlanDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    MaintenancePlan entity = mapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return repository.save(entity);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanDTO> partialUpdate(MaintenancePlanDTO dto) {
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
    public Flux<MaintenancePlanDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenancePlanDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenancePlanDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(p -> repository.deleteById(p.getId()));
    }

    private Mono<MaintenancePlan> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(p -> orgId.equals(p.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "MAINTENANCE_PLAN_NOT_FOUND", "MaintenancePlan not found")));
    }
}
