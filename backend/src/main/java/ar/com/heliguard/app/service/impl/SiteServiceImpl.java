package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.service.SiteService;
import ar.com.heliguard.app.service.dto.SiteDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SiteMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Site}
 * with tenant isolation and server-side organization assignment.
 */
@Service
@Transactional
public class SiteServiceImpl implements SiteService {

    private static final Logger LOG = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SiteServiceImpl(
        SiteRepository siteRepository,
        SiteMapper siteMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SiteDTO> save(SiteDTO siteDTO) {
        LOG.debug("Request to save Site : {}", siteDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                Site entity = siteMapper.toEntity(siteDTO);
                entity.setId(null);
                entity.setOrganizationId(tenant.organizationId());
                entity.setOrganization(new Organization().id(tenant.organizationId()));
                return siteRepository.save(entity);
            })
            .map(siteMapper::toDto);
    }

    @Override
    public Mono<SiteDTO> update(SiteDTO siteDTO) {
        LOG.debug("Request to update Site : {}", siteDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedSite(siteDTO.getId(), tenant.organizationId())
                    .flatMap(existing -> {
                        if (
                            siteDTO.getOrganization() != null &&
                            siteDTO.getOrganization().getId() != null &&
                            !tenant.organizationId().equals(siteDTO.getOrganization().getId())
                        ) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.FORBIDDEN,
                                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                                    "Reassigning site organization is forbidden"
                                )
                            );
                        }
                        Site entity = siteMapper.toEntity(siteDTO);
                        entity.setOrganizationId(existing.getOrganizationId());
                        return siteRepository.save(entity);
                    })
            )
            .map(siteMapper::toDto);
    }

    @Override
    public Mono<SiteDTO> partialUpdate(SiteDTO siteDTO) {
        LOG.debug("Request to partially update Site : {}", siteDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedSite(siteDTO.getId(), tenant.organizationId())
                    .flatMap(existing -> {
                        siteMapper.partialUpdate(existing, siteDTO);
                        existing.setOrganizationId(tenant.organizationId());
                        return siteRepository.save(existing);
                    })
            )
            .map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SiteDTO> findAll() {
        LOG.debug("Request to get all Sites for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> siteRepository.findByOrganization(tenant.organizationId()))
            .map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SiteDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> siteRepository.findByOrganization(tenant.organizationId()).count());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SiteDTO> findOne(Long id) {
        LOG.debug("Request to get Site : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedSite(id, tenant.organizationId()))
            .map(siteMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Site : {}", id);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwnedSite(id, tenant.organizationId()))
            .flatMap(site -> siteRepository.deleteById(site.getId()));
    }

    private Mono<Site> findOwnedSite(Long id, Long organizationId) {
        return siteRepository
            .findById(id)
            .filter(site -> organizationId.equals(site.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SITE_NOT_FOUND", "Site not found")));
    }
}
