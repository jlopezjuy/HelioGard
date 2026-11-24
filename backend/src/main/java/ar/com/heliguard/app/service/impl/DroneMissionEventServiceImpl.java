package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DroneMissionEventRepository;
import ar.com.heliguard.app.service.DroneMissionEventService;
import ar.com.heliguard.app.service.dto.DroneMissionEventDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DroneMissionEvent}.
 */
@Service
@Transactional
public class DroneMissionEventServiceImpl implements DroneMissionEventService {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionEventServiceImpl.class);

    private final DroneMissionEventRepository droneMissionEventRepository;

    private final DroneMissionEventMapper droneMissionEventMapper;

    public DroneMissionEventServiceImpl(
        DroneMissionEventRepository droneMissionEventRepository,
        DroneMissionEventMapper droneMissionEventMapper
    ) {
        this.droneMissionEventRepository = droneMissionEventRepository;
        this.droneMissionEventMapper = droneMissionEventMapper;
    }

    @Override
    public Mono<DroneMissionEventDTO> save(DroneMissionEventDTO droneMissionEventDTO) {
        LOG.debug("Request to save DroneMissionEvent : {}", droneMissionEventDTO);
        return droneMissionEventRepository.save(droneMissionEventMapper.toEntity(droneMissionEventDTO)).map(droneMissionEventMapper::toDto);
    }

    @Override
    public Mono<DroneMissionEventDTO> update(DroneMissionEventDTO droneMissionEventDTO) {
        LOG.debug("Request to update DroneMissionEvent : {}", droneMissionEventDTO);
        return droneMissionEventRepository.save(droneMissionEventMapper.toEntity(droneMissionEventDTO)).map(droneMissionEventMapper::toDto);
    }

    @Override
    public Mono<DroneMissionEventDTO> partialUpdate(DroneMissionEventDTO droneMissionEventDTO) {
        LOG.debug("Request to partially update DroneMissionEvent : {}", droneMissionEventDTO);

        return droneMissionEventRepository
            .findById(droneMissionEventDTO.getId())
            .map(existingDroneMissionEvent -> {
                droneMissionEventMapper.partialUpdate(existingDroneMissionEvent, droneMissionEventDTO);

                return existingDroneMissionEvent;
            })
            .flatMap(droneMissionEventRepository::save)
            .map(droneMissionEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneMissionEventDTO> findAll() {
        LOG.debug("Request to get all DroneMissionEvents");
        return droneMissionEventRepository.findAll().map(droneMissionEventMapper::toDto);
    }

    public Flux<DroneMissionEventDTO> findAllWithEagerRelationships() {
        return droneMissionEventRepository.findAllWithEagerRelationships().map(droneMissionEventMapper::toDto);
    }

    public Mono<Long> countAll() {
        return droneMissionEventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneMissionEventDTO> findOne(Long id) {
        LOG.debug("Request to get DroneMissionEvent : {}", id);
        return droneMissionEventRepository.findOneWithEagerRelationships(id).map(droneMissionEventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DroneMissionEvent : {}", id);
        return droneMissionEventRepository.deleteById(id);
    }
}
