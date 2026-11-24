package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.DeviceIdentityRepository;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.service.DeviceIdentityService;
import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.DeviceIdentityMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DeviceIdentity}
 * with tenant isolation and server-side organization assignment.
 */
@Service
@Transactional
public class DeviceIdentityServiceImpl implements DeviceIdentityService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceIdentityServiceImpl.class);

    private final DeviceIdentityRepository deviceIdentityRepository;
    private final GatewayRepository gatewayRepository;
    private final EquipmentRepository equipmentRepository;
    private final DeviceIdentityMapper deviceIdentityMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public DeviceIdentityServiceImpl(
        DeviceIdentityRepository deviceIdentityRepository,
        GatewayRepository gatewayRepository,
        EquipmentRepository equipmentRepository,
        DeviceIdentityMapper deviceIdentityMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.deviceIdentityRepository = deviceIdentityRepository;
        this.gatewayRepository = gatewayRepository;
        this.equipmentRepository = equipmentRepository;
        this.deviceIdentityMapper = deviceIdentityMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<DeviceIdentityDTO> save(DeviceIdentityDTO dto) {
        LOG.debug("Request to save DeviceIdentity : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                validateLinks(
                    dto.getGateway() != null ? dto.getGateway().getId() : null,
                    dto.getEquipment() != null ? dto.getEquipment().getId() : null,
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            DeviceIdentity entity = deviceIdentityMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return deviceIdentityRepository.save(entity);
                        })
                    )
            )
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<DeviceIdentityDTO> update(DeviceIdentityDTO dto) {
        LOG.debug("Request to update DeviceIdentity : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateLinks(
                                    dto.getGateway() != null ? dto.getGateway().getId() : null,
                                    dto.getEquipment() != null ? dto.getEquipment().getId() : null,
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    DeviceIdentity entity = deviceIdentityMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return deviceIdentityRepository.save(entity);
                                })
                            )
                    )
            )
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<DeviceIdentityDTO> partialUpdate(DeviceIdentityDTO dto) {
        LOG.debug("Request to partially update DeviceIdentity : {}", dto);
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    deviceIdentityMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateLinks(existing.getGatewayId(), existing.getEquipmentId(), tenant.organizationId())
                                        .then(deviceIdentityRepository.save(existing));
                                })
                            )
                    )
            )
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DeviceIdentityDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> deviceIdentityRepository.findByOrganization(tenant.organizationId()))
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DeviceIdentityDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DeviceIdentityDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireRoleForScope(PermissionScope.MANAGE)
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(d -> deviceIdentityRepository.deleteById(d.getId()));
    }

    private Mono<Void> validateLinks(Long gatewayId, Long equipmentId, Long orgId) {
        Mono<Void> gwCheck = gatewayId == null
            ? Mono.empty()
            : gatewayRepository
                .findById(gatewayId)
                .filter(g -> orgId.equals(g.getOrganizationId()))
                .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "GATEWAY_NOT_IN_TENANT", "Gateway not found in tenant")))
                .then();
        Mono<Void> eqCheck = equipmentId == null
            ? Mono.empty()
            : equipmentRepository
                .findById(equipmentId)
                .filter(e -> orgId.equals(e.getOrganizationId()))
                .switchIfEmpty(
                    Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_NOT_IN_TENANT", "Equipment not found in tenant"))
                )
                .then();
        return gwCheck.then(eqCheck);
    }

    private Mono<DeviceIdentity> findOwned(Long id, Long orgId) {
        return deviceIdentityRepository
            .findById(id)
            .filter(d -> orgId.equals(d.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DEVICE_IDENTITY_NOT_FOUND", "DeviceIdentity not found")));
    }
}
