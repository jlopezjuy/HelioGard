package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.WeatherIngestionBatchService;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.WeatherIngestionBatchMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link WeatherIngestionBatch}.
 */
@Service
@Transactional
public class WeatherIngestionBatchServiceImpl implements WeatherIngestionBatchService {

    private final WeatherIngestionBatchRepository repository;
    private final WeatherIngestionBatchMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public WeatherIngestionBatchServiceImpl(
        WeatherIngestionBatchRepository repository,
        WeatherIngestionBatchMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> save(WeatherIngestionBatchDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getPeriodStart(), dto.getPeriodEnd())
                    .then(
                        dto.getInstallation() == null || dto.getInstallation().getId() == null
                            ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                            : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    )
                    .flatMap(inst -> {
                        WeatherIngestionBatch entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> update(WeatherIngestionBatchDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.validateTemporalWindow(dto.getPeriodStart(), dto.getPeriodEnd()))
                            .then(permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()))
                            .flatMap(inst -> {
                                WeatherIngestionBatch entity = mapper.toEntity(dto);
                                entity.setOrganizationId(existing.getOrganizationId());
                                entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                return repository.save(entity);
                            })
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> partialUpdate(WeatherIngestionBatchDTO dto) {
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
                                        .validateTemporalWindow(existing.getPeriodStart(), existing.getPeriodEnd())
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WeatherIngestionBatchDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WeatherIngestionBatchDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WeatherIngestionBatchDTO> findOne(Long id) {
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
            .flatMap(b -> repository.deleteById(b.getId()));
    }

    private Mono<WeatherIngestionBatch> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(b -> orgId.equals(b.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "WEATHER_BATCH_NOT_FOUND", "WeatherIngestionBatch not found")));
    }
}
