package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.MissionAuthorization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.service.MissionAuthorizationService;
import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.MissionAuthorizationMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link MissionAuthorization} (MANAGE roles only for authorization decisions).
 */
@Service
@Transactional
public class MissionAuthorizationServiceImpl implements MissionAuthorizationService {

    private final MissionAuthorizationRepository repository;
    private final DroneMissionRepository missionRepository;
    private final MissionAuthorizationMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public MissionAuthorizationServiceImpl(
        MissionAuthorizationRepository repository,
        DroneMissionRepository missionRepository,
        MissionAuthorizationMapper mapper,
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
    public Mono<MissionAuthorizationDTO> save(MissionAuthorizationDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                missionRepository
                    .findById(dto.getMission().getId())
                    .filter(m -> tenant.organizationId().equals(m.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_MISSION_NOT_FOUND", "Not found")))
                    .flatMap(m -> {
                        MissionAuthorization entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<MissionAuthorizationDTO> update(MissionAuthorizationDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    MissionAuthorization entity = mapper.toEntity(dto);
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
    public Mono<MissionAuthorizationDTO> partialUpdate(MissionAuthorizationDTO dto) {
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
    public Flux<MissionAuthorizationDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MissionAuthorizationDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MissionAuthorizationDTO> findOne(Long id) {
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

    private Mono<MissionAuthorization> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "MISSION_AUTHORIZATION_NOT_FOUND", "Not found")));
    }
}
