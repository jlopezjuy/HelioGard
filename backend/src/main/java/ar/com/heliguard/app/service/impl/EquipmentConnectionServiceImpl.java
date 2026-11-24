package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentConnection;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentConnectionRepository;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.service.EquipmentConnectionService;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EquipmentConnectionMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EquipmentConnection}
 * enforcing same-tenant, same-installation, non-self endpoints, and temporal windows (RB-ASSET-002).
 */
@Service
@Transactional
public class EquipmentConnectionServiceImpl implements EquipmentConnectionService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentConnectionServiceImpl.class);

    private final EquipmentConnectionRepository connectionRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentConnectionMapper connectionMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EquipmentConnectionServiceImpl(
        EquipmentConnectionRepository connectionRepository,
        EquipmentRepository equipmentRepository,
        EquipmentConnectionMapper connectionMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.connectionRepository = connectionRepository;
        this.equipmentRepository = equipmentRepository;
        this.connectionMapper = connectionMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EquipmentConnectionDTO> save(EquipmentConnectionDTO dto) {
        LOG.debug("Request to save EquipmentConnection : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateConnection(
                    dto.getFromEquipment() != null ? dto.getFromEquipment().getId() : null,
                    dto.getToEquipment() != null ? dto.getToEquipment().getId() : null,
                    dto.getValidFrom(),
                    dto.getValidUntil(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            EquipmentConnection entity = connectionMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return connectionRepository.save(entity);
                        })
                    )
            )
            .map(connectionMapper::toDto);
    }

    @Override
    public Mono<EquipmentConnectionDTO> update(EquipmentConnectionDTO dto) {
        LOG.debug("Request to update EquipmentConnection : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedConnection(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateConnection(
                                    dto.getFromEquipment() != null ? dto.getFromEquipment().getId() : null,
                                    dto.getToEquipment() != null ? dto.getToEquipment().getId() : null,
                                    dto.getValidFrom(),
                                    dto.getValidUntil(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    EquipmentConnection entity = connectionMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return connectionRepository.save(entity);
                                })
                            )
                    )
            )
            .map(connectionMapper::toDto);
    }

    @Override
    public Mono<EquipmentConnectionDTO> partialUpdate(EquipmentConnectionDTO dto) {
        LOG.debug("Request to partially update EquipmentConnection : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedConnection(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    connectionMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateConnection(
                                        existing.getFromEquipmentId(),
                                        existing.getToEquipmentId(),
                                        existing.getValidFrom(),
                                        existing.getValidUntil(),
                                        tenant.organizationId()
                                    )
                                        .then(connectionRepository.save(existing));
                                })
                            )
                    )
            )
            .map(connectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentConnectionDTO> findAll() {
        LOG.debug("Request to get all EquipmentConnections for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> connectionRepository.findByOrganization(tenant.organizationId()))
            .map(connectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentConnectionDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentConnectionDTO> findOne(Long id) {
        LOG.debug("Request to get EquipmentConnection : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedConnection(id, tenant.organizationId()))
            .map(connectionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EquipmentConnection : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedConnection(id, tenant.organizationId()))
            .flatMap(c -> connectionRepository.deleteById(c.getId()));
    }

    private Mono<Void> validateConnection(Long fromId, Long toId, Instant validFrom, Instant validUntil, Long orgId) {
        if (fromId == null || toId == null) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "CONNECTION_ENDPOINTS_REQUIRED",
                    "fromEquipment.id and toEquipment.id are required"
                )
            );
        }
        if (fromId.equals(toId)) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "SELF_CONNECTION_FORBIDDEN", "Cannot connect equipment to itself")
            );
        }
        return permissionService
            .validateTemporalWindow(validFrom, validUntil)
            .then(Mono.zip(loadOwnedEquipment(fromId, orgId), loadOwnedEquipment(toId, orgId)))
            .flatMap(tuple ->
                tuple.getT1().getInstallationId().equals(tuple.getT2().getInstallationId())
                    ? Mono.empty()
                    : Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "CROSS_INSTALLATION_CONNECTION_FORBIDDEN",
                            "Connected equipment must belong to the same installation"
                        )
                    )
            );
    }

    private Mono<Equipment> loadOwnedEquipment(Long id, Long orgId) {
        return equipmentRepository
            .findById(id)
            .filter(eq -> orgId.equals(eq.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_NOT_IN_TENANT", "Equipment not found in tenant")));
    }

    private Mono<EquipmentConnection> findOwnedConnection(Long id, Long orgId) {
        return connectionRepository
            .findById(id)
            .filter(c -> orgId.equals(c.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "EQUIPMENT_CONNECTION_NOT_FOUND", "EquipmentConnection not found"))
            );
    }
}
