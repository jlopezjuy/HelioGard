package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.service.DroneMissionService;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.DroneMissionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link DroneMission} (RB-DRONE-001, RB-DRONE-002).
 */
@Service
@Transactional
public class DroneMissionServiceImpl implements DroneMissionService {

    private final DroneMissionRepository repository;
    private final DroneMissionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public DroneMissionServiceImpl(
        DroneMissionRepository repository,
        DroneMissionMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<DroneMissionDTO> save(DroneMissionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        DroneMission entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(inst.getOrganizationId());
                        entity.setOrganization(new Organization().id(inst.getOrganizationId()));
                        entity.setStatus(DroneMissionStatus.DRAFT);
                        if (entity.getProposedAt() == null) {
                            entity.setProposedAt(Instant.now());
                        }
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<DroneMissionDTO> update(DroneMissionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getStatus() == DroneMissionStatus.DISPATCHED && existing.getStatus() != DroneMissionStatus.DISPATCHED
                                    ? Mono.error(
                                        new DomainRuleException(
                                            HttpStatus.CONFLICT,
                                            "USE_DRONE_DISPATCH_ENDPOINT",
                                            "Dispatch DroneMission via POST /api/drone-missions/{id}/dispatch"
                                        )
                                    )
                                    : Mono.empty()
                            )
                            .then(
                                Mono.defer(() -> {
                                    DroneMission entity = mapper.toEntity(dto);
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
    public Mono<DroneMissionDTO> partialUpdate(DroneMissionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getStatus() == DroneMissionStatus.DISPATCHED && existing.getStatus() != DroneMissionStatus.DISPATCHED
                                    ? Mono.error(
                                        new DomainRuleException(
                                            HttpStatus.CONFLICT,
                                            "USE_DRONE_DISPATCH_ENDPOINT",
                                            "Dispatch DroneMission via POST /api/drone-missions/{id}/dispatch"
                                        )
                                    )
                                    : Mono.empty()
                            )
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
    public Flux<DroneMissionDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneMissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().flatMap(tenant -> repository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneMissionDTO> findOne(Long id) {
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
            .flatMap(m -> repository.deleteById(m.getId()));
    }

    private Mono<DroneMission> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(m -> orgId.equals(m.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_MISSION_NOT_FOUND", "Not found")));
    }
}
