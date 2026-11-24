package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrderApproval;
import ar.com.heliguard.app.repository.WorkOrderApprovalRepository;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderApprovalService;
import ar.com.heliguard.app.service.dto.WorkOrderApprovalDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.WorkOrderApprovalMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link WorkOrderApproval}.
 */
@Service
@Transactional
public class WorkOrderApprovalServiceImpl implements WorkOrderApprovalService {

    private final WorkOrderApprovalRepository repository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderApprovalMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public WorkOrderApprovalServiceImpl(
        WorkOrderApprovalRepository repository,
        WorkOrderRepository workOrderRepository,
        WorkOrderApprovalMapper mapper,
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
    public Mono<WorkOrderApprovalDTO> save(WorkOrderApprovalDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                workOrderRepository
                    .findById(dto.getWorkOrder().getId())
                    .filter(w -> tenant.organizationId().equals(w.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_NOT_FOUND", "WorkOrder not found")))
                    .flatMap(wo -> {
                        WorkOrderApproval entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<WorkOrderApprovalDTO> update(WorkOrderApprovalDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    WorkOrderApproval entity = mapper.toEntity(dto);
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
    public Mono<WorkOrderApprovalDTO> partialUpdate(WorkOrderApprovalDTO dto) {
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
    public Flux<WorkOrderApprovalDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderApprovalDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderApprovalDTO> findOne(Long id) {
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

    private Mono<WorkOrderApproval> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_APPROVAL_NOT_FOUND", "Not found")));
    }
}
