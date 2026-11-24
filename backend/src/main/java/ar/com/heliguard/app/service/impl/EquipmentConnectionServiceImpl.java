package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EquipmentConnectionRepository;
import ar.com.heliguard.app.service.EquipmentConnectionService;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.service.mapper.EquipmentConnectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EquipmentConnection}.
 */
@Service
@Transactional
public class EquipmentConnectionServiceImpl implements EquipmentConnectionService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentConnectionServiceImpl.class);

    private final EquipmentConnectionRepository equipmentConnectionRepository;

    private final EquipmentConnectionMapper equipmentConnectionMapper;

    public EquipmentConnectionServiceImpl(
        EquipmentConnectionRepository equipmentConnectionRepository,
        EquipmentConnectionMapper equipmentConnectionMapper
    ) {
        this.equipmentConnectionRepository = equipmentConnectionRepository;
        this.equipmentConnectionMapper = equipmentConnectionMapper;
    }

    @Override
    public Mono<EquipmentConnectionDTO> save(EquipmentConnectionDTO equipmentConnectionDTO) {
        LOG.debug("Request to save EquipmentConnection : {}", equipmentConnectionDTO);
        return equipmentConnectionRepository
            .save(equipmentConnectionMapper.toEntity(equipmentConnectionDTO))
            .map(equipmentConnectionMapper::toDto);
    }

    @Override
    public Mono<EquipmentConnectionDTO> update(EquipmentConnectionDTO equipmentConnectionDTO) {
        LOG.debug("Request to update EquipmentConnection : {}", equipmentConnectionDTO);
        return equipmentConnectionRepository
            .save(equipmentConnectionMapper.toEntity(equipmentConnectionDTO))
            .map(equipmentConnectionMapper::toDto);
    }

    @Override
    public Mono<EquipmentConnectionDTO> partialUpdate(EquipmentConnectionDTO equipmentConnectionDTO) {
        LOG.debug("Request to partially update EquipmentConnection : {}", equipmentConnectionDTO);

        return equipmentConnectionRepository
            .findById(equipmentConnectionDTO.getId())
            .map(existingEquipmentConnection -> {
                equipmentConnectionMapper.partialUpdate(existingEquipmentConnection, equipmentConnectionDTO);

                return existingEquipmentConnection;
            })
            .flatMap(equipmentConnectionRepository::save)
            .map(equipmentConnectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipmentConnectionDTO> findAll() {
        LOG.debug("Request to get all EquipmentConnections");
        return equipmentConnectionRepository.findAll().map(equipmentConnectionMapper::toDto);
    }

    public Flux<EquipmentConnectionDTO> findAllWithEagerRelationships() {
        return equipmentConnectionRepository.findAllWithEagerRelationships().map(equipmentConnectionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return equipmentConnectionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipmentConnectionDTO> findOne(Long id) {
        LOG.debug("Request to get EquipmentConnection : {}", id);
        return equipmentConnectionRepository.findOneWithEagerRelationships(id).map(equipmentConnectionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EquipmentConnection : {}", id);
        return equipmentConnectionRepository.deleteById(id);
    }
}
