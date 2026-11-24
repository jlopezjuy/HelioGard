package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.repository.SolarSectorRepository;
import ar.com.heliguard.app.service.SolarArrayService;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SolarArrayMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarArray}
 * enforcing sector -> installation -> tenant topology consistency (RB-ASSET-002).
 */
@Service
@Transactional
public class SolarArrayServiceImpl implements SolarArrayService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarArrayServiceImpl.class);

    private final SolarArrayRepository solarArrayRepository;
    private final SolarSectorRepository solarSectorRepository;
    private final SolarArrayMapper solarArrayMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SolarArrayServiceImpl(
        SolarArrayRepository solarArrayRepository,
        SolarSectorRepository solarSectorRepository,
        SolarArrayMapper solarArrayMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.solarArrayRepository = solarArrayRepository;
        this.solarSectorRepository = solarSectorRepository;
        this.solarArrayMapper = solarArrayMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SolarArrayDTO> save(SolarArrayDTO dto) {
        LOG.debug("Request to save SolarArray : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateLinks(dto, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            SolarArray entity = solarArrayMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return solarArrayRepository.save(entity);
                        })
                    )
            )
            .map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<SolarArrayDTO> update(SolarArrayDTO dto) {
        LOG.debug("Request to update SolarArray : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedArray(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(validateLinks(dto, tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    SolarArray entity = solarArrayMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return solarArrayRepository.save(entity);
                                })
                            )
                    )
            )
            .map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<SolarArrayDTO> partialUpdate(SolarArrayDTO dto) {
        LOG.debug("Request to partially update SolarArray : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedArray(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    solarArrayMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateLinksIds(existing.getInstallationId(), existing.getSolarSectorId(), tenant.organizationId())
                                        .then(solarArrayRepository.save(existing));
                                })
                            )
                    )
            )
            .map(solarArrayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarArrayDTO> findAll() {
        LOG.debug("Request to get all SolarArrays for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> solarArrayRepository.findByOrganization(tenant.organizationId()))
            .map(solarArrayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarArrayDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarArrayDTO> findOne(Long id) {
        LOG.debug("Request to get SolarArray : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedArray(id, tenant.organizationId()))
            .map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarArray : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedArray(id, tenant.organizationId()))
            .flatMap(arr -> solarArrayRepository.deleteById(arr.getId()));
    }

    private Mono<Void> validateLinks(SolarArrayDTO dto, Long orgId) {
        Long installationId = dto.getInstallation() != null ? dto.getInstallation().getId() : null;
        Long sectorId = dto.getSolarSector() != null ? dto.getSolarSector().getId() : null;
        return validateLinksIds(installationId, sectorId, orgId);
    }

    private Mono<Void> validateLinksIds(Long installationId, Long sectorId, Long orgId) {
        if (installationId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"));
        }
        Mono<Void> checkInst = permissionService.requireOwnedInstallation(installationId, orgId).then();
        if (sectorId == null) {
            return checkInst;
        }
        Mono<Void> checkSector = solarSectorRepository
            .findById(sectorId)
            .filter(sector -> orgId.equals(sector.getOrganizationId()) && installationId.equals(sector.getInstallationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "SECTOR_INSTALLATION_MISMATCH",
                        "SolarSector must belong to the same tenant and installation"
                    )
                )
            )
            .then();
        return checkInst.then(checkSector);
    }

    private Mono<SolarArray> findOwnedArray(Long id, Long orgId) {
        return solarArrayRepository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SOLAR_ARRAY_NOT_FOUND", "SolarArray not found")));
    }
}
