package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.service.DroneAssetService;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.DroneAssetMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link DroneAsset}.
 */
@Service
@Transactional
public class DroneAssetServiceImpl implements DroneAssetService {

    private final DroneAssetRepository repository;
    private final DroneAssetMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public DroneAssetServiceImpl(
        DroneAssetRepository repository,
        DroneAssetMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<DroneAssetDTO> save(DroneAssetDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> {
                DroneAsset entity = mapper.toEntity(dto);
                entity.setId(null);
                entity.setOrganizationId(tenant.organizationId());
                entity.setOrganization(new Organization().id(tenant.organizationId()));
                return repository.save(entity);
            })
            .map(mapper::toDto);
    }

    @Override
    public Mono<DroneAssetDTO> update(DroneAssetDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    DroneAsset entity = mapper.toEntity(dto);
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
    public Mono<DroneAssetDTO> partialUpdate(DroneAssetDTO dto) {
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
    public Flux<DroneAssetDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneAssetDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneAssetDTO> findOne(Long id) {
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
            .flatMap(d -> repository.deleteById(d.getId()));
    }

    private Mono<DroneAsset> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(d -> orgId.equals(d.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DRONE_ASSET_NOT_FOUND", "Not found")));
    }
}
