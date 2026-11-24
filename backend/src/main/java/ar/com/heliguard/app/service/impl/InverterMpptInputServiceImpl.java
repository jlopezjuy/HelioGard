package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.repository.InverterMpptInputRepository;
import ar.com.heliguard.app.service.InverterMpptInputService;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.InverterMpptInputMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.InverterMpptInput}
 * enforcing inverter equipment kind (INVERTER), tenant ownership, and temporal window.
 */
@Service
@Transactional
public class InverterMpptInputServiceImpl implements InverterMpptInputService {

    private static final Logger LOG = LoggerFactory.getLogger(InverterMpptInputServiceImpl.class);

    private final InverterMpptInputRepository mpptInputRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentModelRepository equipmentModelRepository;
    private final InverterMpptInputMapper mpptInputMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public InverterMpptInputServiceImpl(
        InverterMpptInputRepository mpptInputRepository,
        EquipmentRepository equipmentRepository,
        EquipmentModelRepository equipmentModelRepository,
        InverterMpptInputMapper mpptInputMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.mpptInputRepository = mpptInputRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentModelRepository = equipmentModelRepository;
        this.mpptInputMapper = mpptInputMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<InverterMpptInputDTO> save(InverterMpptInputDTO dto) {
        LOG.debug("Request to save InverterMpptInput : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateMppt(
                    dto.getInverter() != null ? dto.getInverter().getId() : null,
                    dto.getValidFrom(),
                    dto.getValidUntil(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            InverterMpptInput entity = mpptInputMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return mpptInputRepository.save(entity);
                        })
                    )
            )
            .map(mpptInputMapper::toDto);
    }

    @Override
    public Mono<InverterMpptInputDTO> update(InverterMpptInputDTO dto) {
        LOG.debug("Request to update InverterMpptInput : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedMppt(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateMppt(
                                    dto.getInverter() != null ? dto.getInverter().getId() : null,
                                    dto.getValidFrom(),
                                    dto.getValidUntil(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    InverterMpptInput entity = mpptInputMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return mpptInputRepository.save(entity);
                                })
                            )
                    )
            )
            .map(mpptInputMapper::toDto);
    }

    @Override
    public Mono<InverterMpptInputDTO> partialUpdate(InverterMpptInputDTO dto) {
        LOG.debug("Request to partially update InverterMpptInput : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedMppt(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    mpptInputMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateMppt(
                                        existing.getInverterId(),
                                        existing.getValidFrom(),
                                        existing.getValidUntil(),
                                        tenant.organizationId()
                                    )
                                        .then(mpptInputRepository.save(existing));
                                })
                            )
                    )
            )
            .map(mpptInputMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InverterMpptInputDTO> findAll() {
        LOG.debug("Request to get all InverterMpptInputs for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> mpptInputRepository.findByOrganization(tenant.organizationId()))
            .map(mpptInputMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InverterMpptInputDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InverterMpptInputDTO> findOne(Long id) {
        LOG.debug("Request to get InverterMpptInput : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedMppt(id, tenant.organizationId()))
            .map(mpptInputMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InverterMpptInput : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedMppt(id, tenant.organizationId()))
            .flatMap(m -> mpptInputRepository.deleteById(m.getId()));
    }

    private Mono<Void> validateMppt(Long inverterId, Instant validFrom, Instant validUntil, Long orgId) {
        return permissionService
            .validateTemporalWindow(validFrom, validUntil)
            .then(
                inverterId == null
                    ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVERTER_REQUIRED", "inverter.id is required"))
                    : equipmentRepository
                        .findById(inverterId)
                        .filter(eq -> orgId.equals(eq.getOrganizationId()))
                        .switchIfEmpty(
                            Mono.error(
                                new DomainRuleException(
                                    HttpStatus.BAD_REQUEST,
                                    "INVERTER_NOT_IN_TENANT",
                                    "Inverter equipment not found in tenant"
                                )
                            )
                        )
                        .flatMap(eq ->
                            equipmentModelRepository
                                .findById(eq.getEquipmentModelId())
                                .filter(model -> model.getKind() == EquipmentKind.INVERTER)
                                .switchIfEmpty(
                                    Mono.error(
                                        new DomainRuleException(
                                            HttpStatus.BAD_REQUEST,
                                            "EQUIPMENT_NOT_INVERTER",
                                            "Referenced equipment must have model kind INVERTER"
                                        )
                                    )
                                )
                        )
                        .then()
            );
    }

    private Mono<InverterMpptInput> findOwnedMppt(Long id, Long orgId) {
        return mpptInputRepository
            .findById(id)
            .filter(m -> orgId.equals(m.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "MPPT_INPUT_NOT_FOUND", "InverterMpptInput not found"))
            );
    }
}
