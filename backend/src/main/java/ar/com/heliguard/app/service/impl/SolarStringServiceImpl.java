package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.service.SolarStringService;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SolarStringMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarString}
 * enforcing array tenant ownership and MODULE catalog kind (RB-ASSET-001, RB-ASSET-002).
 */
@Service
@Transactional
public class SolarStringServiceImpl implements SolarStringService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarStringServiceImpl.class);

    private final SolarStringRepository solarStringRepository;
    private final SolarArrayRepository solarArrayRepository;
    private final EquipmentModelRepository equipmentModelRepository;
    private final SolarStringMapper solarStringMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SolarStringServiceImpl(
        SolarStringRepository solarStringRepository,
        SolarArrayRepository solarArrayRepository,
        EquipmentModelRepository equipmentModelRepository,
        SolarStringMapper solarStringMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.solarStringRepository = solarStringRepository;
        this.solarArrayRepository = solarArrayRepository;
        this.equipmentModelRepository = equipmentModelRepository;
        this.solarStringMapper = solarStringMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SolarStringDTO> save(SolarStringDTO dto) {
        LOG.debug("Request to save SolarString : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateString(
                    dto.getSolarArray() != null ? dto.getSolarArray().getId() : null,
                    dto.getModuleModel() != null ? dto.getModuleModel().getId() : null,
                    dto.getModulesInSeries(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            SolarString entity = solarStringMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return solarStringRepository.save(entity);
                        })
                    )
            )
            .map(solarStringMapper::toDto);
    }

    @Override
    public Mono<SolarStringDTO> update(SolarStringDTO dto) {
        LOG.debug("Request to update SolarString : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedString(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateString(
                                    dto.getSolarArray() != null ? dto.getSolarArray().getId() : null,
                                    dto.getModuleModel() != null ? dto.getModuleModel().getId() : null,
                                    dto.getModulesInSeries(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    SolarString entity = solarStringMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return solarStringRepository.save(entity);
                                })
                            )
                    )
            )
            .map(solarStringMapper::toDto);
    }

    @Override
    public Mono<SolarStringDTO> partialUpdate(SolarStringDTO dto) {
        LOG.debug("Request to partially update SolarString : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedString(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    solarStringMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateString(
                                        existing.getSolarArrayId(),
                                        existing.getModuleModelId(),
                                        existing.getModulesInSeries(),
                                        tenant.organizationId()
                                    )
                                        .then(solarStringRepository.save(existing));
                                })
                            )
                    )
            )
            .map(solarStringMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarStringDTO> findAll() {
        LOG.debug("Request to get all SolarStrings for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> solarStringRepository.findByOrganization(tenant.organizationId()))
            .map(solarStringMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarStringDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarStringDTO> findOne(Long id) {
        LOG.debug("Request to get SolarString : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedString(id, tenant.organizationId()))
            .map(solarStringMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarString : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedString(id, tenant.organizationId()))
            .flatMap(s -> solarStringRepository.deleteById(s.getId()));
    }

    private Mono<Void> validateString(Long solarArrayId, Long moduleModelId, Integer modulesInSeries, Long orgId) {
        if (modulesInSeries == null || modulesInSeries < 1) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_MODULES_IN_SERIES", "modulesInSeries must be >= 1"));
        }
        if (solarArrayId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SOLAR_ARRAY_REQUIRED", "solarArray.id is required"));
        }
        Mono<Void> checkArray = solarArrayRepository
            .findById(solarArrayId)
            .filter(arr -> orgId.equals(arr.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SOLAR_ARRAY_NOT_IN_TENANT", "SolarArray not found in tenant"))
            )
            .then();

        if (moduleModelId == null) {
            return checkArray;
        }
        Mono<Void> checkModel = equipmentModelRepository
            .findById(moduleModelId)
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "EQUIPMENT_MODEL_NOT_FOUND", "Module EquipmentModel not found"))
            )
            .flatMap(model ->
                model.getKind() == EquipmentKind.MODULE
                    ? Mono.empty()
                    : Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "INVALID_MODULE_MODEL_KIND",
                            "SolarString moduleModel must have kind MODULE"
                        )
                    )
            );
        return checkArray.then(checkModel);
    }

    private Mono<SolarString> findOwnedString(Long id, Long orgId) {
        return solarStringRepository
            .findById(id)
            .filter(s -> orgId.equals(s.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SOLAR_STRING_NOT_FOUND", "SolarString not found")));
    }
}
