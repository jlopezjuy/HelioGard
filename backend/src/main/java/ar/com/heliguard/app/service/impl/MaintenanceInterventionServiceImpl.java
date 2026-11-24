package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.MaintenanceInterventionService;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.MaintenanceInterventionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link MaintenanceIntervention}.
 */
@Service
@Transactional
public class MaintenanceInterventionServiceImpl implements MaintenanceInterventionService {

    private final MaintenanceInterventionRepository repository;
    private final WorkOrderRepository workOrderRepository;
    private final MaintenanceInterventionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public MaintenanceInterventionServiceImpl(
        MaintenanceInterventionRepository repository,
        WorkOrderRepository workOrderRepository,
        MaintenanceInterventionMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.workOrderRepository = workOrderRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<MaintenanceInterventionDTO> save(MaintenanceInterventionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                workOrderRepository
                    .findById(dto.getWorkOrder().getId())
                    .filter(w -> tenant.organizationId().equals(w.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_NOT_FOUND", "WorkOrder not found")))
                    .flatMap(wo -> {
                        MaintenanceIntervention entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<MaintenanceInterventionDTO> update(MaintenanceInterventionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    MaintenanceIntervention entity = mapper.toEntity(dto);
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
    public Mono<MaintenanceInterventionDTO> partialUpdate(MaintenanceInterventionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
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
    public Flux<MaintenanceInterventionDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenanceInterventionDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenanceInterventionDTO> findOne(Long id) {
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
            .flatMap(i -> repository.deleteById(i.getId()));
    }

    private Mono<MaintenanceIntervention> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INTERVENTION_NOT_FOUND", "Not found")));
    }
}
