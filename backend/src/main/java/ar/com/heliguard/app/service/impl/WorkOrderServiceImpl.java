package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderService;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.WorkOrderMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link WorkOrder} enforcing lifecycle transitions (RB-WO-001, RB-WO-002).
 */
@Service
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(WorkOrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.CREATED, EnumSet.of(WorkOrderStatus.CREATED, WorkOrderStatus.ASSIGNED, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.ASSIGNED, EnumSet.of(WorkOrderStatus.ASSIGNED, WorkOrderStatus.ACCEPTED, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.ACCEPTED, EnumSet.of(WorkOrderStatus.ACCEPTED, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.IN_PROGRESS, EnumSet.of(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.DONE, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.DONE, EnumSet.of(WorkOrderStatus.DONE, WorkOrderStatus.VERIFIED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.VERIFIED, EnumSet.of(WorkOrderStatus.VERIFIED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.CANCELLED, EnumSet.of(WorkOrderStatus.CANCELLED));
    }

    private final WorkOrderRepository repository;
    private final WorkOrderMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public WorkOrderServiceImpl(
        WorkOrderRepository repository,
        WorkOrderMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<WorkOrderDTO> save(WorkOrderDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        WorkOrder entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(inst.getOrganizationId());
                        entity.setOrganization(new Organization().id(inst.getOrganizationId()));
                        entity.setStatus(WorkOrderStatus.CREATED);
                        if (entity.getOpenedAt() == null) {
                            entity.setOpenedAt(Instant.now());
                        }
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<WorkOrderDTO> update(WorkOrderDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(validateTransition(existing.getStatus(), dto.getStatus()))
                            .then(
                                Mono.defer(() -> {
                                    WorkOrder entity = mapper.toEntity(dto);
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
    public Mono<WorkOrderDTO> partialUpdate(WorkOrderDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(dto.getStatus() != null ? validateTransition(existing.getStatus(), dto.getStatus()) : Mono.empty())
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
    public Flux<WorkOrderDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().flatMap(tenant -> repository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderDTO> findOne(Long id) {
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
            .flatMap(w -> repository.deleteById(w.getId()));
    }

    private Mono<Void> validateTransition(WorkOrderStatus from, WorkOrderStatus to) {
        if (to == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "STATUS_REQUIRED", "status is required"));
        }
        Set<WorkOrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(WorkOrderStatus.class));
        if (!allowed.contains(to)) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "INVALID_WORK_ORDER_TRANSITION",
                    "Transition from " + from + " to " + to + " is not allowed"
                )
            );
        }
        return Mono.empty();
    }

    private Mono<WorkOrder> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(w -> orgId.equals(w.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WORK_ORDER_NOT_FOUND", "WorkOrder not found")));
    }
}
