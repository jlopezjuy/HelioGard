package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.service.EnergyModelRunService;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EnergyModelRunMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link EnergyModelRun} enforcing INSUFFICIENT_DATA -> expectedWh == null (RB-ENE-002).
 */
@Service
@Transactional
public class EnergyModelRunServiceImpl implements EnergyModelRunService {

    private final EnergyModelRunRepository repository;
    private final EnergyModelRunMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EnergyModelRunServiceImpl(
        EnergyModelRunRepository repository,
        EnergyModelRunMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EnergyModelRunDTO> save(EnergyModelRunDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getPeriodStart(), dto.getPeriodEnd())
                    .then(validateRunInvariants(dto))
                    .then(
                        dto.getInstallation() == null || dto.getInstallation().getId() == null
                            ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                            : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    )
                    .flatMap(inst -> {
                        EnergyModelRun entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<EnergyModelRunDTO> update(EnergyModelRunDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.validateTemporalWindow(dto.getPeriodStart(), dto.getPeriodEnd()))
                            .then(validateRunInvariants(dto))
                            .then(
                                Mono.defer(() -> {
                                    EnergyModelRun entity = mapper.toEntity(dto);
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
    public Mono<EnergyModelRunDTO> partialUpdate(EnergyModelRunDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
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
    public Flux<EnergyModelRunDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EnergyModelRunDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EnergyModelRunDTO> findOne(Long id) {
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
            .flatMap(r -> repository.deleteById(r.getId()));
    }

    private Mono<Void> validateRunInvariants(EnergyModelRunDTO dto) {
        if (dto.getStatus() == EnergyModelRunStatus.INSUFFICIENT_DATA && dto.getExpectedWh() != null) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "INSUFFICIENT_DATA_MUST_NOT_HAVE_EXPECTED_WH",
                    "INSUFFICIENT_DATA run must leave expectedWh as null"
                )
            );
        }
        return Mono.empty();
    }

    private Mono<EnergyModelRun> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ENERGY_MODEL_RUN_NOT_FOUND", "EnergyModelRun not found")));
    }
}
