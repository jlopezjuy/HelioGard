package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.service.OrganizationMembershipService;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.OrganizationMembershipMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.OrganizationMembership}
 * scoped to the active tenant.
 */
@Service
@Transactional
public class OrganizationMembershipServiceImpl implements OrganizationMembershipService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationMembershipServiceImpl.class);

    private final OrganizationMembershipRepository membershipRepository;
    private final OrganizationMembershipMapper membershipMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public OrganizationMembershipServiceImpl(
        OrganizationMembershipRepository membershipRepository,
        OrganizationMembershipMapper membershipMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.membershipRepository = membershipRepository;
        this.membershipMapper = membershipMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<OrganizationMembershipDTO> save(OrganizationMembershipDTO dto) {
        LOG.debug("Request to save OrganizationMembership : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                OrganizationMembership entity = membershipMapper.toEntity(dto);
                entity.setId(null);
                entity.setOrganizationId(tenant.organizationId());
                entity.setOrganization(new Organization().id(tenant.organizationId()));
                if (entity.getStatus() == null) {
                    entity.setStatus(MembershipStatus.ACTIVE);
                }
                if (entity.getStatus() == MembershipStatus.ACTIVE && entity.getActivatedAt() == null) {
                    entity.setActivatedAt(Instant.now());
                }
                return membershipRepository.save(entity);
            })
            .map(membershipMapper::toDto);
    }

    @Override
    public Mono<OrganizationMembershipDTO> update(OrganizationMembershipDTO dto) {
        LOG.debug("Request to update OrganizationMembership : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedMembership(dto.getId(), tenant.organizationId())
                    .flatMap(existing -> {
                        OrganizationMembership entity = membershipMapper.toEntity(dto);
                        entity.setOrganizationId(existing.getOrganizationId());
                        return membershipRepository.save(entity);
                    })
            )
            .map(membershipMapper::toDto);
    }

    @Override
    public Mono<OrganizationMembershipDTO> partialUpdate(OrganizationMembershipDTO dto) {
        LOG.debug("Request to partially update OrganizationMembership : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwnedMembership(dto.getId(), tenant.organizationId())
                    .flatMap(existing -> {
                        membershipMapper.partialUpdate(existing, dto);
                        existing.setOrganizationId(tenant.organizationId());
                        return membershipRepository.save(existing);
                    })
            )
            .map(membershipMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrganizationMembershipDTO> findAll() {
        LOG.debug("Request to get all OrganizationMemberships for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> membershipRepository.findByOrganization(tenant.organizationId()))
            .map(membershipMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrganizationMembershipDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> membershipRepository.findByOrganization(tenant.organizationId()).count());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrganizationMembershipDTO> findOne(Long id) {
        LOG.debug("Request to get OrganizationMembership : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedMembership(id, tenant.organizationId()))
            .map(membershipMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OrganizationMembership : {}", id);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwnedMembership(id, tenant.organizationId()))
            .flatMap(m -> membershipRepository.deleteById(m.getId()));
    }

    private Mono<OrganizationMembership> findOwnedMembership(Long id, Long organizationId) {
        return membershipRepository
            .findById(id)
            .filter(m -> organizationId.equals(m.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "MEMBERSHIP_NOT_FOUND", "Organization membership not found"))
            );
    }
}
