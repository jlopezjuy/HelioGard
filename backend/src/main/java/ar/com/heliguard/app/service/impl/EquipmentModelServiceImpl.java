package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.service.EquipmentModelService;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.mapper.EquipmentModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EquipmentModel}.
 */
@Service
@Transactional
public class EquipmentModelServiceImpl implements EquipmentModelService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentModelServiceImpl.class);

    private final EquipmentModelRepository equipmentModelRepository;

    private final EquipmentModelMapper equipmentModelMapper;

    public EquipmentModelServiceImpl(EquipmentModelRepository equipmentModelRepository, EquipmentModelMapper equipmentModelMapper) {
        this.equipmentModelRepository = equipmentModelRepository;
        this.equipmentModelMapper = equipmentModelMapper;
    }

    @Override
    public Mono<EquipmentModelDTO> save(EquipmentModelDTO equipmentModelDTO) {
        LOG.debug("Request to save EquipmentModel : {}", equipmentModelDTO);
        return equipmentModelRepository.save(equipmentModelMapper.toEntity(equipmentModelDTO)).map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<EquipmentModelDTO> update(EquipmentModelDTO equipmentModelDTO) {
        LOG.debug("Request to update EquipmentModel : {}", equipmentModelDTO);
        return equipmentModelRepository.save(equipmentModelMapper.toEntity(equipmentModelDTO)).map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<EquipmentModelDTO> partialUpdate(EquipmentModelDTO equipmentModelDTO) {
        LOG.debug("Request to partially update EquipmentModel : {}", equipmentModelDTO);

        return equipmentModelRepository
            .findById(equipmentModelDTO.getId())
            .map(existingEquipmentModel -> {
                equipmentModelMapper.partialUpdate(existingEquipmentModel, equipmentModelDTO);

                return existingEquipmentModel;
            })
            .flatMap(equipmentModelRepository::save)
            .map(equipmentModelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentModelDTO> findAll() {
        LOG.debug("Request to get all EquipmentModels");
        return equipmentModelRepository.findAll().map(equipmentModelMapper::toDto);
    }

    public Mono<Long> countAll() {
        return equipmentModelRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentModelDTO> findOne(Long id) {
        LOG.debug("Request to get EquipmentModel : {}", id);
        return equipmentModelRepository.findById(id).map(equipmentModelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EquipmentModel : {}", id);
        return equipmentModelRepository.deleteById(id);
    }
}
