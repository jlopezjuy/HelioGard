package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.service.EquipmentService;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.mapper.EquipmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Equipment}.
 */
@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;

    private final EquipmentMapper equipmentMapper;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public Mono<EquipmentDTO> save(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to save Equipment : {}", equipmentDTO);
        return equipmentRepository.save(equipmentMapper.toEntity(equipmentDTO)).map(equipmentMapper::toDto);
    }

    @Override
    public Mono<EquipmentDTO> update(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to update Equipment : {}", equipmentDTO);
        return equipmentRepository.save(equipmentMapper.toEntity(equipmentDTO)).map(equipmentMapper::toDto);
    }

    @Override
    public Mono<EquipmentDTO> partialUpdate(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to partially update Equipment : {}", equipmentDTO);

        return equipmentRepository
            .findById(equipmentDTO.getId())
            .map(existingEquipment -> {
                equipmentMapper.partialUpdate(existingEquipment, equipmentDTO);

                return existingEquipment;
            })
            .flatMap(equipmentRepository::save)
            .map(equipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Equipments");
        return equipmentRepository.findAllBy(pageable).map(equipmentMapper::toDto);
    }

    public Flux<EquipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipmentRepository.findAllWithEagerRelationships(pageable).map(equipmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return equipmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Equipment : {}", id);
        return equipmentRepository.findOneWithEagerRelationships(id).map(equipmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Equipment : {}", id);
        return equipmentRepository.deleteById(id);
    }
}
