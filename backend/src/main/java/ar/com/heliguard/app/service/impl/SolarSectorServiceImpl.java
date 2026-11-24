package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.SolarSectorRepository;
import ar.com.heliguard.app.service.SolarSectorService;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SolarSectorMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarSector}
 * with tenant isolation, temporal window validation, and same-tenant installation checks.
 */
@Service
@Transactional
public class SolarSectorServiceImpl implements SolarSectorService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarSectorServiceImpl.class);

    private final SolarSectorRepository solarSectorRepository;
    private final SolarSectorMapper solarSectorMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SolarSectorServiceImpl(
        SolarSectorRepository solarSectorRepository,
        SolarSectorMapper solarSectorMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.solarSectorRepository = solarSectorRepository;
        this.solarSectorMapper = solarSectorMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SolarSectorDTO> save(SolarSectorDTO dto) {
        LOG.debug("Request to save SolarSector : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getValidFrom(), dto.getValidUntil())
                    .then(
                        dto.getInstallation() == null || dto.getInstallation().getId() == null
                            ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                            : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    )
                    .flatMap(installation -> {
                        SolarSector entity = solarSectorMapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return solarSectorRepository.save(entity);
                    })
            )
            .map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<SolarSectorDTO> update(SolarSectorDTO dto) {
        LOG.debug("Request to update SolarSector : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedSector(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.validateTemporalWindow(dto.getValidFrom(), dto.getValidUntil()))
                            .then(
                                dto.getInstallation() == null || dto.getInstallation().getId() == null
                                    ? Mono.error(
                                        new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required")
                                    )
                                    : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                            )
                            .flatMap(installation -> {
                                SolarSector entity = solarSectorMapper.toEntity(dto);
                                entity.setOrganizationId(existing.getOrganizationId());
                                entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                return solarSectorRepository.save(entity);
                            })
                    )
            )
            .map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<SolarSectorDTO> partialUpdate(SolarSectorDTO dto) {
        LOG.debug("Request to partially update SolarSector : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedSector(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getInstallation() != null && dto.getInstallation().getId() != null
                                    ? permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()).then()
                                    : Mono.empty()
                            )
                            .then(
                                Mono.defer(() -> {
                                    solarSectorMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return permissionService
                                        .validateTemporalWindow(existing.getValidFrom(), existing.getValidUntil())
                                        .then(solarSectorRepository.save(existing));
                                })
                            )
                    )
            )
            .map(solarSectorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarSectorDTO> findAll() {
        LOG.debug("Request to get all SolarSectors for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> solarSectorRepository.findByOrganization(tenant.organizationId()))
            .map(solarSectorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarSectorDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarSectorDTO> findOne(Long id) {
        LOG.debug("Request to get SolarSector : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedSector(id, tenant.organizationId()))
            .map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarSector : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedSector(id, tenant.organizationId()))
            .flatMap(sector -> solarSectorRepository.deleteById(sector.getId()));
    }

    private Mono<SolarSector> findOwnedSector(Long id, Long orgId) {
        return solarSectorRepository
            .findById(id)
            .filter(s -> orgId.equals(s.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SOLAR_SECTOR_NOT_FOUND", "SolarSector not found")));
    }
}
