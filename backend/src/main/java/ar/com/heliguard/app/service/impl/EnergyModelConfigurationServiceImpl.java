package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.service.EnergyModelConfigurationService;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EnergyModelConfigurationMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link EnergyModelConfiguration}.
 */
@Service
@Transactional
public class EnergyModelConfigurationServiceImpl implements EnergyModelConfigurationService {

    private final EnergyModelConfigurationRepository repository;
    private final EnergyModelConfigurationMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EnergyModelConfigurationServiceImpl(
        EnergyModelConfigurationRepository repository,
        EnergyModelConfigurationMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> save(EnergyModelConfigurationDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getActiveFrom(), dto.getActiveUntil())
                    .then(
                        dto.getInstallation() == null || dto.getInstallation().getId() == null
                            ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                            : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    )
                    .flatMap(inst -> {
                        EnergyModelConfiguration entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> update(EnergyModelConfigurationDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.validateTemporalWindow(dto.getActiveFrom(), dto.getActiveUntil()))
                            .then(permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()))
                            .flatMap(inst -> {
                                EnergyModelConfiguration entity = mapper.toEntity(dto);
                                entity.setOrganizationId(existing.getOrganizationId());
                                entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                return repository.save(entity);
                            })
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> partialUpdate(EnergyModelConfigurationDTO dto) {
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
                                    return permissionService
                                        .validateTemporalWindow(existing.getActiveFrom(), existing.getActiveUntil())
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EnergyModelConfigurationDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EnergyModelConfigurationDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EnergyModelConfigurationDTO> findOne(Long id) {
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
            .flatMap(c -> repository.deleteById(c.getId()));
    }

    private Mono<EnergyModelConfiguration> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(c -> orgId.equals(c.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ENERGY_CONFIG_NOT_FOUND", "EnergyModelConfiguration not found"))
            );
    }
}
