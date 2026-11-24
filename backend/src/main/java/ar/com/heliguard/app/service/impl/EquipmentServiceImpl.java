package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.service.EquipmentService;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EquipmentMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Equipment}
 * enforcing tenant isolation, catalog model existence, and same-installation string link (RB-ASSET-001, RB-ASSET-002, RB-ID-003).
 */
@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentModelRepository equipmentModelRepository;
    private final SolarStringRepository solarStringRepository;
    private final SolarArrayRepository solarArrayRepository;
    private final EquipmentMapper equipmentMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EquipmentServiceImpl(
        EquipmentRepository equipmentRepository,
        EquipmentModelRepository equipmentModelRepository,
        SolarStringRepository solarStringRepository,
        SolarArrayRepository solarArrayRepository,
        EquipmentMapper equipmentMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentModelRepository = equipmentModelRepository;
        this.solarStringRepository = solarStringRepository;
        this.solarArrayRepository = solarArrayRepository;
        this.equipmentMapper = equipmentMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EquipmentDTO> save(EquipmentDTO dto) {
        LOG.debug("Request to save Equipment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateEquipment(
                    dto.getInstallation() != null ? dto.getInstallation().getId() : null,
                    dto.getEquipmentModel() != null ? dto.getEquipmentModel().getId() : null,
                    dto.getSolarString() != null ? dto.getSolarString().getId() : null,
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            Equipment entity = equipmentMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return equipmentRepository.save(entity);
                        })
                    )
            )
            .map(equipmentMapper::toDto);
    }

    @Override
    public Mono<EquipmentDTO> update(EquipmentDTO dto) {
        LOG.debug("Request to update Equipment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedEquipment(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateEquipment(
                                    dto.getInstallation() != null ? dto.getInstallation().getId() : null,
                                    dto.getEquipmentModel() != null ? dto.getEquipmentModel().getId() : null,
                                    dto.getSolarString() != null ? dto.getSolarString().getId() : null,
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    Equipment entity = equipmentMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return equipmentRepository.save(entity);
                                })
                            )
                    )
            )
            .map(equipmentMapper::toDto);
    }

    @Override
    public Mono<EquipmentDTO> partialUpdate(EquipmentDTO dto) {
        LOG.debug("Request to partially update Equipment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedEquipment(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    equipmentMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateEquipment(
                                        existing.getInstallationId(),
                                        existing.getEquipmentModelId(),
                                        existing.getSolarStringId(),
                                        tenant.organizationId()
                                    )
                                        .then(equipmentRepository.save(existing));
                                })
                            )
                    )
            )
            .map(equipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Equipment for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> equipmentRepository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(equipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> equipmentRepository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Equipment : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedEquipment(id, tenant.organizationId()))
            .map(equipmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Equipment : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedEquipment(id, tenant.organizationId()))
            .flatMap(eq -> equipmentRepository.deleteById(eq.getId()));
    }

    private Mono<Void> validateEquipment(Long installationId, Long equipmentModelId, Long solarStringId, Long orgId) {
        if (installationId == null || equipmentModelId == null) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "EQUIPMENT_LINKS_REQUIRED",
                    "installation.id and equipmentModel.id are required"
                )
            );
        }
        Mono<Void> checkInst = permissionService.requireOwnedInstallation(installationId, orgId).then();
        Mono<Void> checkModel = equipmentModelRepository
            .findById(equipmentModelId)
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_MODEL_NOT_FOUND", "EquipmentModel not found"))
            )
            .then();
        if (solarStringId == null) {
            return checkInst.then(checkModel);
        }
        Mono<Void> checkString = solarStringRepository
            .findById(solarStringId)
            .filter(s -> orgId.equals(s.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SOLAR_STRING_NOT_IN_TENANT", "SolarString not found in tenant"))
            )
            .flatMap(s -> solarArrayRepository.findById(s.getSolarArrayId()))
            .filter(arr -> installationId.equals(arr.getInstallationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.BAD_REQUEST,
                        "STRING_INSTALLATION_MISMATCH",
                        "SolarString must belong to the same installation as Equipment"
                    )
                )
            )
            .then();
        return checkInst.then(checkModel).then(checkString);
    }

    private Mono<Equipment> findOwnedEquipment(Long id, Long orgId) {
        return equipmentRepository
            .findById(id)
            .filter(eq -> orgId.equals(eq.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "EQUIPMENT_NOT_FOUND", "Equipment not found")));
    }
}
