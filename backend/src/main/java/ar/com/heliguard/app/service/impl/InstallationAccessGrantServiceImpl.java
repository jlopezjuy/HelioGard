package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.service.InstallationAccessGrantService;
import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.InstallationAccessGrantMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.InstallationAccessGrant}
 * with tenant ownership checks and temporal interval validation.
 */
@Service
@Transactional
public class InstallationAccessGrantServiceImpl implements InstallationAccessGrantService {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationAccessGrantServiceImpl.class);

    private final InstallationAccessGrantRepository grantRepository;
    private final InstallationRepository installationRepository;
    private final OrganizationRepository organizationRepository;
    private final InstallationAccessGrantMapper grantMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public InstallationAccessGrantServiceImpl(
        InstallationAccessGrantRepository grantRepository,
        InstallationRepository installationRepository,
        OrganizationRepository organizationRepository,
        InstallationAccessGrantMapper grantMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.grantRepository = grantRepository;
        this.installationRepository = installationRepository;
        this.organizationRepository = organizationRepository;
        this.grantMapper = grantMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<InstallationAccessGrantDTO> save(InstallationAccessGrantDTO dto) {
        LOG.debug("Request to save InstallationAccessGrant : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                validateGrant(dto, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            InstallationAccessGrant entity = grantMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return grantRepository.save(entity);
                        })
                    )
            )
            .map(grantMapper::toDto);
    }

    @Override
    public Mono<InstallationAccessGrantDTO> update(InstallationAccessGrantDTO dto) {
        LOG.debug("Request to update InstallationAccessGrant : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedGrant(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        validateGrant(dto, tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    InstallationAccessGrant entity = grantMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    return grantRepository.save(entity);
                                })
                            )
                    )
            )
            .map(grantMapper::toDto);
    }

    @Override
    public Mono<InstallationAccessGrantDTO> partialUpdate(InstallationAccessGrantDTO dto) {
        LOG.debug("Request to partially update InstallationAccessGrant : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedGrant(dto.getId(), tenant.organizationId())
                    .flatMap(existing -> {
                        grantMapper.partialUpdate(existing, dto);
                        existing.setOrganizationId(tenant.organizationId());
                        if (existing.getValidUntil() != null && !existing.getValidUntil().isAfter(existing.getValidFrom())) {
                            return Mono.error(
                                new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_GRANT_INTERVAL", "validUntil must be after validFrom")
                            );
                        }
                        return grantRepository.save(existing);
                    })
            )
            .map(grantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationAccessGrantDTO> findAll() {
        LOG.debug("Request to get all InstallationAccessGrants for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant ->
                Flux.merge(
                    grantRepository.findByOrganization(tenant.organizationId()),
                    grantRepository.findByGranteeOrganization(tenant.organizationId())
                ).distinct(InstallationAccessGrant::getId)
            )
            .map(grantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationAccessGrantDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstallationAccessGrantDTO> findOne(Long id) {
        LOG.debug("Request to get InstallationAccessGrant : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                grantRepository
                    .findById(id)
                    .filter(g ->
                        tenant.organizationId().equals(g.getOrganizationId()) ||
                        tenant.organizationId().equals(g.getGranteeOrganizationId())
                    )
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "GRANT_NOT_FOUND", "Access grant not found")))
            )
            .map(grantMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InstallationAccessGrant : {}", id);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwnedGrant(id, tenant.organizationId()))
            .flatMap(grant -> grantRepository.deleteById(grant.getId()));
    }

    private Mono<Void> validateGrant(InstallationAccessGrantDTO dto, Long orgId) {
        if (dto.getValidFrom() == null || (dto.getValidUntil() != null && !dto.getValidUntil().isAfter(dto.getValidFrom()))) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_GRANT_INTERVAL", "validUntil must be after validFrom")
            );
        }
        if (dto.getInstallation() == null || dto.getInstallation().getId() == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"));
        }
        if (dto.getGranteeOrganization() == null || dto.getGranteeOrganization().getId() == null) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "GRANTEE_REQUIRED", "granteeOrganization.id is required")
            );
        }
        if (orgId.equals(dto.getGranteeOrganization().getId())) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "SELF_GRANT_FORBIDDEN", "Cannot grant installation access to own organization")
            );
        }
        Mono<Void> checkInstallation = installationRepository
            .findById(dto.getInstallation().getId())
            .filter(inst -> orgId.equals(inst.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INSTALLATION_NOT_FOUND", "Installation not found"))
            )
            .then();

        Mono<Void> checkGrantee = organizationRepository
            .findById(dto.getGranteeOrganization().getId())
            .filter(org -> org.getStatus() == OrganizationStatus.ACTIVE)
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_GRANTEE_ORG", "Grantee organization must exist and be active"))
            )
            .then();

        return checkInstallation.then(checkGrantee);
    }

    private Mono<InstallationAccessGrant> findOwnedGrant(Long id, Long orgId) {
        return grantRepository
            .findById(id)
            .filter(g -> orgId.equals(g.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "GRANT_NOT_FOUND", "Access grant not found")));
    }
}
