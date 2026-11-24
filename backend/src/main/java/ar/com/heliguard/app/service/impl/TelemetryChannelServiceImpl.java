package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.repository.TelemetryChannelRepository;
import ar.com.heliguard.app.service.TelemetryChannelService;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.TelemetryChannelMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryChannel}
 * with tenant isolation and equipment ownership checks.
 */
@Service
@Transactional
public class TelemetryChannelServiceImpl implements TelemetryChannelService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelServiceImpl.class);

    private final TelemetryChannelRepository channelRepository;
    private final EquipmentRepository equipmentRepository;
    private final TelemetryChannelMapper channelMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public TelemetryChannelServiceImpl(
        TelemetryChannelRepository channelRepository,
        EquipmentRepository equipmentRepository,
        TelemetryChannelMapper channelMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.channelRepository = channelRepository;
        this.equipmentRepository = equipmentRepository;
        this.channelMapper = channelMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<TelemetryChannelDTO> save(TelemetryChannelDTO dto) {
        LOG.debug("Request to save TelemetryChannel : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                checkEquipment(dto.getEquipment() != null ? dto.getEquipment().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            TelemetryChannel entity = channelMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return channelRepository.save(entity);
                        })
                    )
            )
            .map(channelMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelDTO> update(TelemetryChannelDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(checkEquipment(dto.getEquipment() != null ? dto.getEquipment().getId() : null, tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    TelemetryChannel entity = channelMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return channelRepository.save(entity);
                                })
                            )
                    )
            )
            .map(channelMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelDTO> partialUpdate(TelemetryChannelDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    channelMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return checkEquipment(existing.getEquipmentId(), tenant.organizationId())
                                        .then(channelRepository.save(existing));
                                })
                            )
                    )
            )
            .map(channelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> channelRepository.findByOrganization(tenant.organizationId()))
            .map(channelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryChannelDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(channelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(c -> channelRepository.deleteById(c.getId()));
    }

    private Mono<Void> checkEquipment(Long equipmentId, Long orgId) {
        if (equipmentId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_REQUIRED", "equipment.id is required"));
        }
        return equipmentRepository
            .findById(equipmentId)
            .filter(eq -> orgId.equals(eq.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_NOT_IN_TENANT", "Equipment not found in tenant")))
            .then();
    }

    private Mono<TelemetryChannel> findOwned(Long id, Long orgId) {
        return channelRepository
            .findById(id)
            .filter(c -> orgId.equals(c.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "TELEMETRY_CHANNEL_NOT_FOUND", "TelemetryChannel not found"))
            );
    }
}
