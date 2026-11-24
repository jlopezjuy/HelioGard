package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.service.InstallationService;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.InstallationMapper;
import ar.com.heliguard.app.service.tenant.CurrentTenant;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.math.BigDecimal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Installation}
 * with SDD tenant isolation, role/grant authorization, and domain invariants.
 */
@Service
@Transactional
public class InstallationServiceImpl implements InstallationService {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationServiceImpl.class);

    private final InstallationRepository installationRepository;
    private final SiteRepository siteRepository;
    private final InstallationMapper installationMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public InstallationServiceImpl(
        InstallationRepository installationRepository,
        SiteRepository siteRepository,
        InstallationMapper installationMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.installationRepository = installationRepository;
        this.siteRepository = siteRepository;
        this.installationMapper = installationMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<InstallationDTO> save(InstallationDTO installationDTO) {
        LOG.debug("Request to save Installation : {}", installationDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                validateInstallationInput(installationDTO, tenant.organizationId(), null)
                    .then(
                        Mono.defer(() -> {
                            Installation entity = installationMapper.toEntity(installationDTO);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            entity.setStatus(InstallationStatus.COMMISSIONING);
                            return installationRepository.save(entity);
                        })
                    )
            )
            .map(installationMapper::toDto);
    }

    @Override
    public Mono<InstallationDTO> update(InstallationDTO installationDTO) {
        LOG.debug("Request to update Installation : {}", installationDTO);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                permissionService
                    .requireAccessToInstallation(installationDTO.getId(), PermissionScope.MANAGE)
                    .flatMap(existing -> {
                        if (!tenant.organizationId().equals(existing.getOrganizationId())) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Only the owning organization can modify an installation"
                                )
                            );
                        }
                        if (
                            installationDTO.getOrganization() != null &&
                            installationDTO.getOrganization().getId() != null &&
                            !tenant.organizationId().equals(installationDTO.getOrganization().getId())
                        ) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Reassigning installation organization is forbidden"
                                )
                            );
                        }
                        return validateInstallationInput(installationDTO, tenant.organizationId(), existing.getId())
                            .then(
                                Mono.defer(() -> {
                                    Installation entity = installationMapper.toEntity(installationDTO);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    if (entity.getStatus() == null) {
                                        entity.setStatus(existing.getStatus());
                                    }
                                    return installationRepository.save(entity);
                                })
                            );
                    })
            )
            .map(installationMapper::toDto);
    }

    @Override
    public Mono<InstallationDTO> partialUpdate(InstallationDTO installationDTO) {
        LOG.debug("Request to partially update Installation : {}", installationDTO);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                permissionService
                    .requireAccessToInstallation(installationDTO.getId(), PermissionScope.MANAGE)
                    .flatMap(existing -> {
                        if (!tenant.organizationId().equals(existing.getOrganizationId())) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Only the owning organization can modify an installation"
                                )
                            );
                        }
                        if (
                            installationDTO.getOrganization() != null &&
                            installationDTO.getOrganization().getId() != null &&
                            !tenant.organizationId().equals(installationDTO.getOrganization().getId())
                        ) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Reassigning installation organization is forbidden"
                                )
                            );
                        }
                        if (
                            installationDTO.getNominalDcKw() != null &&
                            installationDTO.getNominalDcKw().compareTo(BigDecimal.ZERO) <= 0
                        ) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.BAD_REQUEST,
                                    "INVALID_NOMINAL_DC_KW",
                                    "nominalDcKw must be strictly greater than zero"
                                )
                            );
                        }
                        Mono<Void> siteCheck = Mono.empty();
                        if (installationDTO.getSite() != null && installationDTO.getSite().getId() != null) {
                            siteCheck = verifySiteBelongsToTenant(installationDTO.getSite().getId(), tenant.organizationId());
                        }
                        return siteCheck.then(
                            Mono.defer(() -> {
                                installationMapper.partialUpdate(existing, installationDTO);
                                existing.setOrganizationId(tenant.organizationId());
                                existing.setOrganization(new Organization().id(tenant.organizationId()));
                                return installationRepository.save(existing);
                            })
                        );
                    })
            )
            .map(installationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get accessible Installations for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant ->
                installationRepository.findAllAccessibleByOrganization(
                    tenant.organizationId(),
                    Instant.now(),
                    pageable.getPageSize(),
                    pageable.getOffset()
                )
            )
            .map(installationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> installationRepository.countAccessibleByOrganization(tenant.organizationId(), Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstallationDTO> findOne(Long id) {
        LOG.debug("Request to get Installation : {}", id);
        return permissionService.requireAccessToInstallation(id, PermissionScope.READ_ONLY).map(installationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Installation : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                permissionService
                    .requireAccessToInstallation(id, PermissionScope.MANAGE)
                    .flatMap(existing -> {
                        if (!tenant.organizationId().equals(existing.getOrganizationId())) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Only the owning organization can delete an installation"
                                )
                            );
                        }
                        return installationRepository.deleteById(id);
                    })
            );
    }

    private Mono<Void> validateInstallationInput(InstallationDTO dto, Long orgId, Long currentInstallationId) {
        if (dto.getNominalDcKw() == null || dto.getNominalDcKw().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_NOMINAL_DC_KW",
                    "nominalDcKw must be strictly greater than zero"
                )
            );
        }
        if (dto.getGridMode() == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_GRID_MODE", "gridMode is required"));
        }
        if (dto.getSite() == null || dto.getSite().getId() == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SITE_REQUIRED", "site.id is required"));
        }
        return verifySiteBelongsToTenant(dto.getSite().getId(), orgId)
            .then(
                installationRepository
                    .findByOrganizationIdAndCode(orgId, dto.getCode())
                    .filter(found -> currentInstallationId == null || !found.getId().equals(currentInstallationId))
                    .flatMap(duplicate ->
                        Mono.<Void>error(
                            new DomainRuleException(
                                HttpStatus.CONFLICT,
                                "INSTALLATION_CODE_EXISTS",
                                "Installation code already exists in the active organization"
                            )
                        )
                    )
            );
    }

    private Mono<Void> verifySiteBelongsToTenant(Long siteId, Long orgId) {
        return siteRepository
            .findById(siteId)
            .filter(site -> orgId.equals(site.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "SITE_NOT_IN_TENANT",
                        "Site does not exist in the active organization"
                    )
                )
            )
            .then();
    }
}
