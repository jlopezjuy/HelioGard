package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannelBinding;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.repository.TelemetryChannelBindingRepository;
import ar.com.heliguard.app.repository.TelemetryChannelRepository;
import ar.com.heliguard.app.service.TelemetryChannelBindingService;
import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.TelemetryChannelBindingMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryChannelBinding}
 * enforcing tenant isolation and temporal window validation.
 */
@Service
@Transactional
public class TelemetryChannelBindingServiceImpl implements TelemetryChannelBindingService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelBindingServiceImpl.class);

    private final TelemetryChannelBindingRepository bindingRepository;
    private final GatewayRepository gatewayRepository;
    private final TelemetryChannelRepository channelRepository;
    private final TelemetryChannelBindingMapper bindingMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public TelemetryChannelBindingServiceImpl(
        TelemetryChannelBindingRepository bindingRepository,
        GatewayRepository gatewayRepository,
        TelemetryChannelRepository channelRepository,
        TelemetryChannelBindingMapper bindingMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.bindingRepository = bindingRepository;
        this.gatewayRepository = gatewayRepository;
        this.channelRepository = channelRepository;
        this.bindingMapper = bindingMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> save(TelemetryChannelBindingDTO dto) {
        LOG.debug("Request to save TelemetryChannelBinding : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateBinding(
                    dto.getGateway() != null ? dto.getGateway().getId() : null,
                    dto.getTelemetryChannel() != null ? dto.getTelemetryChannel().getId() : null,
                    dto.getValidFrom(),
                    dto.getValidUntil(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            TelemetryChannelBinding entity = bindingMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return bindingRepository.save(entity);
                        })
                    )
            )
            .map(bindingMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> update(TelemetryChannelBindingDTO dto) {
        LOG.debug("Request to update TelemetryChannelBinding : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedBinding(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateBinding(
                                    dto.getGateway() != null ? dto.getGateway().getId() : null,
                                    dto.getTelemetryChannel() != null ? dto.getTelemetryChannel().getId() : null,
                                    dto.getValidFrom(),
                                    dto.getValidUntil(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    TelemetryChannelBinding entity = bindingMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return bindingRepository.save(entity);
                                })
                            )
                    )
            )
            .map(bindingMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> partialUpdate(TelemetryChannelBindingDTO dto) {
        LOG.debug("Request to partially update TelemetryChannelBinding : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedBinding(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    bindingMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateBinding(
                                        existing.getGatewayId(),
                                        existing.getTelemetryChannelId(),
                                        existing.getValidFrom(),
                                        existing.getValidUntil(),
                                        tenant.organizationId()
                                    )
                                        .then(bindingRepository.save(existing));
                                })
                            )
                    )
            )
            .map(bindingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelBindingDTO> findAll() {
        LOG.debug("Request to get all TelemetryChannelBindings for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> bindingRepository.findByOrganization(tenant.organizationId()))
            .map(bindingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelBindingDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryChannelBindingDTO> findOne(Long id) {
        LOG.debug("Request to get TelemetryChannelBinding : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedBinding(id, tenant.organizationId()))
            .map(bindingMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TelemetryChannelBinding : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedBinding(id, tenant.organizationId()))
            .flatMap(b -> bindingRepository.deleteById(b.getId()));
    }

    private Mono<Void> validateBinding(Long gatewayId, Long channelId, Instant validFrom, Instant validUntil, Long orgId) {
        if (gatewayId == null || channelId == null) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "BINDING_TARGETS_REQUIRED",
                    "gateway.id and telemetryChannel.id are required"
                )
            );
        }
        Mono<Void> checkGateway = gatewayRepository
            .findById(gatewayId)
            .filter(g -> orgId.equals(g.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "GATEWAY_NOT_IN_TENANT", "Gateway not found in tenant")))
            .then();
        Mono<Void> checkChannel = channelRepository
            .findById(channelId)
            .filter(c -> orgId.equals(c.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "TELEMETRY_CHANNEL_NOT_IN_TENANT",
                        "TelemetryChannel not found in tenant"
                    )
                )
            )
            .then();
        return permissionService.validateTemporalWindow(validFrom, validUntil).then(checkGateway).then(checkChannel);
    }

    private Mono<TelemetryChannelBinding> findOwnedBinding(Long id, Long orgId) {
        return bindingRepository
            .findById(id)
            .filter(b -> orgId.equals(b.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.NOT_FOUND, "CHANNEL_BINDING_NOT_FOUND", "TelemetryChannelBinding not found")
                )
            );
    }
}
