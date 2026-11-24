package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.DroneMissionTarget;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.repository.DroneMissionTargetRepository;
import ar.com.heliguard.app.service.DroneMissionTargetService;
import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.DroneMissionTargetMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link DroneMissionTarget}.
 */
@Service
@Transactional
public class DroneMissionTargetServiceImpl implements DroneMissionTargetService {

    private final DroneMissionTargetRepository repository;
    private final DroneMissionRepository missionRepository;
    private final DroneMissionTargetMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public DroneMissionTargetServiceImpl(
        DroneMissionTargetRepository repository,
        DroneMissionRepository missionRepository,
        DroneMissionTargetMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.missionRepository = missionRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<DroneMissionTargetDTO> save(DroneMissionTargetDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                missionRepository
                    .findById(dto.getMission().getId())
                    .filter(m -> tenant.organizationId().equals(m.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_MISSION_NOT_FOUND", "Not found")))
                    .flatMap(m -> {
                        DroneMissionTarget entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<DroneMissionTargetDTO> update(DroneMissionTargetDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    DroneMissionTarget entity = mapper.toEntity(dto);
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
    public Mono<DroneMissionTargetDTO> partialUpdate(DroneMissionTargetDTO dto) {
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
    public Flux<DroneMissionTargetDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneMissionTargetDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneMissionTargetDTO> findOne(Long id) {
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
            .flatMap(t -> repository.deleteById(t.getId()));
    }

    private Mono<DroneMissionTarget> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(t -> orgId.equals(t.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_MISSION_TARGET_NOT_FOUND", "Not found")));
    }
}
