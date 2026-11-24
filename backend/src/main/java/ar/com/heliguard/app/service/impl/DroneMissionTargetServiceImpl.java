package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DroneMissionTargetRepository;
import ar.com.heliguard.app.service.DroneMissionTargetService;
import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionTargetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DroneMissionTarget}.
 */
@Service
@Transactional
public class DroneMissionTargetServiceImpl implements DroneMissionTargetService {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionTargetServiceImpl.class);

    private final DroneMissionTargetRepository droneMissionTargetRepository;

    private final DroneMissionTargetMapper droneMissionTargetMapper;

    public DroneMissionTargetServiceImpl(
        DroneMissionTargetRepository droneMissionTargetRepository,
        DroneMissionTargetMapper droneMissionTargetMapper
    ) {
        this.droneMissionTargetRepository = droneMissionTargetRepository;
        this.droneMissionTargetMapper = droneMissionTargetMapper;
    }

    @Override
    public Mono<DroneMissionTargetDTO> save(DroneMissionTargetDTO droneMissionTargetDTO) {
        LOG.debug("Request to save DroneMissionTarget : {}", droneMissionTargetDTO);
        return droneMissionTargetRepository
            .save(droneMissionTargetMapper.toEntity(droneMissionTargetDTO))
            .map(droneMissionTargetMapper::toDto);
    }

    @Override
    public Mono<DroneMissionTargetDTO> update(DroneMissionTargetDTO droneMissionTargetDTO) {
        LOG.debug("Request to update DroneMissionTarget : {}", droneMissionTargetDTO);
        return droneMissionTargetRepository
            .save(droneMissionTargetMapper.toEntity(droneMissionTargetDTO))
            .map(droneMissionTargetMapper::toDto);
    }

    @Override
    public Mono<DroneMissionTargetDTO> partialUpdate(DroneMissionTargetDTO droneMissionTargetDTO) {
        LOG.debug("Request to partially update DroneMissionTarget : {}", droneMissionTargetDTO);

        return droneMissionTargetRepository
            .findById(droneMissionTargetDTO.getId())
            .map(existingDroneMissionTarget -> {
                droneMissionTargetMapper.partialUpdate(existingDroneMissionTarget, droneMissionTargetDTO);

                return existingDroneMissionTarget;
            })
            .flatMap(droneMissionTargetRepository::save)
            .map(droneMissionTargetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneMissionTargetDTO> findAll() {
        LOG.debug("Request to get all DroneMissionTargets");
        return droneMissionTargetRepository.findAll().map(droneMissionTargetMapper::toDto);
    }

    public Flux<DroneMissionTargetDTO> findAllWithEagerRelationships() {
        return droneMissionTargetRepository.findAllWithEagerRelationships().map(droneMissionTargetMapper::toDto);
    }

    public Mono<Long> countAll() {
        return droneMissionTargetRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneMissionTargetDTO> findOne(Long id) {
        LOG.debug("Request to get DroneMissionTarget : {}", id);
        return droneMissionTargetRepository.findOneWithEagerRelationships(id).map(droneMissionTargetMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DroneMissionTarget : {}", id);
        return droneMissionTargetRepository.deleteById(id);
    }
}
