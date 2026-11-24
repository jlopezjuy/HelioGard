package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrderAssignment;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderAssignmentService;
import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.WorkOrderAssignmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link WorkOrderAssignment}.
 */
@Service
@Transactional
public class WorkOrderAssignmentServiceImpl implements WorkOrderAssignmentService {

    private final WorkOrderAssignmentRepository repository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderAssignmentMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public WorkOrderAssignmentServiceImpl(
        WorkOrderAssignmentRepository repository,
        WorkOrderRepository workOrderRepository,
        WorkOrderAssignmentMapper mapper,
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
    public Mono<WorkOrderAssignmentDTO> save(WorkOrderAssignmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                workOrderRepository
                    .findById(dto.getWorkOrder().getId())
                    .filter(w -> tenant.organizationId().equals(w.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_NOT_FOUND", "WorkOrder not found")))
                    .flatMap(wo -> {
                        WorkOrderAssignment entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<WorkOrderAssignmentDTO> update(WorkOrderAssignmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    WorkOrderAssignment entity = mapper.toEntity(dto);
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
    public Mono<WorkOrderAssignmentDTO> partialUpdate(WorkOrderAssignmentDTO dto) {
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
    public Flux<WorkOrderAssignmentDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant ->
                Flux.merge(
                    repository.findByOrganization(tenant.organizationId()),
                    repository.findByProviderOrganization(tenant.organizationId())
                ).distinct(WorkOrderAssignment::getId)
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderAssignmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderAssignmentDTO> findOne(Long id) {
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
            .flatMap(a -> repository.deleteById(a.getId()));
    }

    private Mono<WorkOrderAssignment> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()) || orgId.equals(a.getProviderOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_ASSIGNMENT_NOT_FOUND", "Not found")));
    }
}
