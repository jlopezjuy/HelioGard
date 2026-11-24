package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.service.GatewayService;
import ar.com.heliguard.app.service.dto.GatewayDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.GatewayMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Gateway}
 * with tenant isolation and server-side organization assignment.
 */
@Service
@Transactional
public class GatewayServiceImpl implements GatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayServiceImpl.class);

    private final GatewayRepository gatewayRepository;
    private final GatewayMapper gatewayMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public GatewayServiceImpl(
        GatewayRepository gatewayRepository,
        GatewayMapper gatewayMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.gatewayRepository = gatewayRepository;
        this.gatewayMapper = gatewayMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<GatewayDTO> save(GatewayDTO dto) {
        LOG.debug("Request to save Gateway : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                (dto.getInstallation() == null || dto.getInstallation().getId() == null
                    ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                    : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()))
                    .flatMap(inst -> {
                        Gateway entity = gatewayMapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return gatewayRepository.save(entity);
                    })
            )
            .map(gatewayMapper::toDto);
    }

    @Override
    public Mono<GatewayDTO> update(GatewayDTO dto) {
        LOG.debug("Request to update Gateway : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedGateway(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getInstallation() == null || dto.getInstallation().getId() == null
                                    ? Mono.error(
                                        new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required")
                                    )
                                    : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                            )
                            .flatMap(inst -> {
                                Gateway entity = gatewayMapper.toEntity(dto);
                                entity.setOrganizationId(existing.getOrganizationId());
                                entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                return gatewayRepository.save(entity);
                            })
                    )
            )
            .map(gatewayMapper::toDto);
    }

    @Override
    public Mono<GatewayDTO> partialUpdate(GatewayDTO dto) {
        LOG.debug("Request to partially update Gateway : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedGateway(dto.getId(), tenant.organizationId())
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
                                    gatewayMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return gatewayRepository.save(existing);
                                })
                            )
                    )
            )
            .map(gatewayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GatewayDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Gateways for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> gatewayRepository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(gatewayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GatewayDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> gatewayRepository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<GatewayDTO> findOne(Long id) {
        LOG.debug("Request to get Gateway : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedGateway(id, tenant.organizationId()))
            .map(gatewayMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Gateway : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedGateway(id, tenant.organizationId()))
            .flatMap(g -> gatewayRepository.deleteById(g.getId()));
    }

    private Mono<Gateway> findOwnedGateway(Long id, Long orgId) {
        return gatewayRepository
            .findById(id)
            .filter(g -> orgId.equals(g.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "GATEWAY_NOT_FOUND", "Gateway not found")));
    }
}
