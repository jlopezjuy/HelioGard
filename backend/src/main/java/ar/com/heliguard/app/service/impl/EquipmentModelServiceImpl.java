package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.service.EquipmentModelService;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EquipmentModelMapper;
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
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EquipmentModel}.
 * Global catalog: readable by any authenticated tenant user, writable by MANAGE roles.
 */
@Service
@Transactional
public class EquipmentModelServiceImpl implements EquipmentModelService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentModelServiceImpl.class);

    private final EquipmentModelRepository equipmentModelRepository;
    private final EquipmentModelMapper equipmentModelMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EquipmentModelServiceImpl(
        EquipmentModelRepository equipmentModelRepository,
        EquipmentModelMapper equipmentModelMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.equipmentModelRepository = equipmentModelRepository;
        this.equipmentModelMapper = equipmentModelMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EquipmentModelDTO> save(EquipmentModelDTO dto) {
        LOG.debug("Request to save EquipmentModel : {}", dto);
        return permissionService
            .requireManageRole()
            .then(Mono.defer(() -> equipmentModelRepository.save(equipmentModelMapper.toEntity(dto))))
            .map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<EquipmentModelDTO> update(EquipmentModelDTO dto) {
        LOG.debug("Request to update EquipmentModel : {}", dto);
        return permissionService
            .requireManageRole()
            .then(
                equipmentModelRepository
                    .findById(dto.getId())
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "EQUIPMENT_MODEL_NOT_FOUND", "EquipmentModel not found"))
                    )
                    .flatMap(existing -> equipmentModelRepository.save(equipmentModelMapper.toEntity(dto)))
            )
            .map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<EquipmentModelDTO> partialUpdate(EquipmentModelDTO dto) {
        LOG.debug("Request to partially update EquipmentModel : {}", dto);
        return permissionService
            .requireManageRole()
            .then(
                equipmentModelRepository
                    .findById(dto.getId())
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "EQUIPMENT_MODEL_NOT_FOUND", "EquipmentModel not found"))
                    )
                    .map(existing -> {
                        equipmentModelMapper.partialUpdate(existing, dto);
                        return existing;
                    })
                    .flatMap(equipmentModelRepository::save)
            )
            .map(equipmentModelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentModelDTO> findAll() {
        LOG.debug("Request to get all EquipmentModels");
        return tenantContextService.requireCurrentTenant().thenMany(equipmentModelRepository.findAll()).map(equipmentModelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().then(equipmentModelRepository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentModelDTO> findOne(Long id) {
        LOG.debug("Request to get EquipmentModel : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .then(
                equipmentModelRepository
                    .findById(id)
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "EQUIPMENT_MODEL_NOT_FOUND", "EquipmentModel not found"))
                    )
            )
            .map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EquipmentModel : {}", id);
        return permissionService.requireManageRole().then(equipmentModelRepository.deleteById(id));
    }
}
