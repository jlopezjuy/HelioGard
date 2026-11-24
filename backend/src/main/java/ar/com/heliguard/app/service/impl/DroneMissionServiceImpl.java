package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.service.DroneMissionService;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DroneMission}.
 */
@Service
@Transactional
public class DroneMissionServiceImpl implements DroneMissionService {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionServiceImpl.class);

    private final DroneMissionRepository droneMissionRepository;

    private final DroneMissionMapper droneMissionMapper;

    public DroneMissionServiceImpl(DroneMissionRepository droneMissionRepository, DroneMissionMapper droneMissionMapper) {
        this.droneMissionRepository = droneMissionRepository;
        this.droneMissionMapper = droneMissionMapper;
    }

    @Override
    public Mono<DroneMissionDTO> save(DroneMissionDTO droneMissionDTO) {
        LOG.debug("Request to save DroneMission : {}", droneMissionDTO);
        return droneMissionRepository.save(droneMissionMapper.toEntity(droneMissionDTO)).map(droneMissionMapper::toDto);
    }

    @Override
    public Mono<DroneMissionDTO> update(DroneMissionDTO droneMissionDTO) {
        LOG.debug("Request to update DroneMission : {}", droneMissionDTO);
        return droneMissionRepository.save(droneMissionMapper.toEntity(droneMissionDTO)).map(droneMissionMapper::toDto);
    }

    @Override
    public Mono<DroneMissionDTO> partialUpdate(DroneMissionDTO droneMissionDTO) {
        LOG.debug("Request to partially update DroneMission : {}", droneMissionDTO);

        return droneMissionRepository
            .findById(droneMissionDTO.getId())
            .map(existingDroneMission -> {
                droneMissionMapper.partialUpdate(existingDroneMission, droneMissionDTO);

                return existingDroneMission;
            })
            .flatMap(droneMissionRepository::save)
            .map(droneMissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneMissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DroneMissions");
        return droneMissionRepository.findAllBy(pageable).map(droneMissionMapper::toDto);
    }

    public Flux<DroneMissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return droneMissionRepository.findAllWithEagerRelationships(pageable).map(droneMissionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return droneMissionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneMissionDTO> findOne(Long id) {
        LOG.debug("Request to get DroneMission : {}", id);
        return droneMissionRepository.findOneWithEagerRelationships(id).map(droneMissionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DroneMission : {}", id);
        return droneMissionRepository.deleteById(id);
    }
}
