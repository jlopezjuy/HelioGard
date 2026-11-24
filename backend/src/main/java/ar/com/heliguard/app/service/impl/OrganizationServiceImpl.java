package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.security.SecurityUtils;
import ar.com.heliguard.app.service.OrganizationService;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.OrganizationMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Organization}
 * scoped to the authenticated user's active memberships and active tenant.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public OrganizationServiceImpl(
        OrganizationRepository organizationRepository,
        OrganizationMembershipRepository membershipRepository,
        UserRepository userRepository,
        OrganizationMapper organizationMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.organizationMapper = organizationMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<OrganizationDTO> save(OrganizationDTO organizationDTO) {
        LOG.debug("Request to save Organization : {}", organizationDTO);
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required")))
            .flatMap(userRepository::findOneByLogin)
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "User not found")))
            .flatMap(user -> {
                Organization org = organizationMapper.toEntity(organizationDTO);
                org.setId(null);
                if (org.getStatus() == null) {
                    org.setStatus(OrganizationStatus.ACTIVE);
                }
                if (org.getCreatedAt() == null) {
                    org.setCreatedAt(Instant.now());
                }
                return organizationRepository
                    .save(org)
                    .flatMap(savedOrg -> {
                        OrganizationMembership ownerMembership = new OrganizationMembership();
                        ownerMembership.setOrganizationId(savedOrg.getId());
                        ownerMembership.setMemberId(user.getId());
                        ownerMembership.setRole(MembershipRole.OWNER);
                        ownerMembership.setStatus(MembershipStatus.ACTIVE);
                        ownerMembership.setActivatedAt(Instant.now());
                        return membershipRepository.save(ownerMembership).thenReturn(savedOrg);
                    });
            })
            .map(organizationMapper::toDto);
    }

    @Override
    public Mono<OrganizationDTO> update(OrganizationDTO organizationDTO) {
        LOG.debug("Request to update Organization : {}", organizationDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                if (!tenant.organizationId().equals(organizationDTO.getId())) {
                    return Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"));
                }
                return organizationRepository
                    .findById(tenant.organizationId())
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"))
                    )
                    .flatMap(existing -> {
                        Organization updated = organizationMapper.toEntity(organizationDTO);
                        updated.setId(existing.getId());
                        updated.setCreatedAt(existing.getCreatedAt());
                        return organizationRepository.save(updated);
                    });
            })
            .map(organizationMapper::toDto);
    }

    @Override
    public Mono<OrganizationDTO> partialUpdate(OrganizationDTO organizationDTO) {
        LOG.debug("Request to partially update Organization : {}", organizationDTO);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                if (!tenant.organizationId().equals(organizationDTO.getId())) {
                    return Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"));
                }
                return organizationRepository
                    .findById(tenant.organizationId())
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"))
                    )
                    .map(existing -> {
                        organizationMapper.partialUpdate(existing, organizationDTO);
                        return existing;
                    })
                    .flatMap(organizationRepository::save);
            })
            .map(organizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrganizationDTO> findAll() {
        LOG.debug("Request to get user's active Organizations");
        return tenantContextService
            .getCurrentUserContext()
            .flatMapMany(ctx -> Flux.fromIterable(ctx.getMemberships()))
            .flatMap(summary -> organizationRepository.findById(summary.getOrganizationId()))
            .map(organizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrganizationDTO> findOne(Long id) {
        LOG.debug("Request to get Organization : {}", id);
        return tenantContextService
            .getCurrentUserContext()
            .flatMap(ctx -> {
                boolean hasMembership = ctx.getMemberships().stream().anyMatch(m -> m.getOrganizationId().equals(id));
                if (!hasMembership) {
                    return Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"));
                }
                return organizationRepository.findById(id);
            })
            .map(organizationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Organization : {}", id);
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> {
                if (!tenant.organizationId().equals(id)) {
                    return Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", "Organization not found"));
                }
                return organizationRepository.deleteById(id);
            });
    }
}
