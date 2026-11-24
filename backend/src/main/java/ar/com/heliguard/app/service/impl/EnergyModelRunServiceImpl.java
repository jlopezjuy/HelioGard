package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.service.EnergyModelRunService;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.mapper.EnergyModelRunMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EnergyModelRun}.
 */
@Service
@Transactional
public class EnergyModelRunServiceImpl implements EnergyModelRunService {

    private static final Logger LOG = LoggerFactory.getLogger(EnergyModelRunServiceImpl.class);

    private final EnergyModelRunRepository energyModelRunRepository;

    private final EnergyModelRunMapper energyModelRunMapper;

    public EnergyModelRunServiceImpl(EnergyModelRunRepository energyModelRunRepository, EnergyModelRunMapper energyModelRunMapper) {
        this.energyModelRunRepository = energyModelRunRepository;
        this.energyModelRunMapper = energyModelRunMapper;
    }

    @Override
    public Mono<EnergyModelRunDTO> save(EnergyModelRunDTO energyModelRunDTO) {
        LOG.debug("Request to save EnergyModelRun : {}", energyModelRunDTO);
        return energyModelRunRepository.save(energyModelRunMapper.toEntity(energyModelRunDTO)).map(energyModelRunMapper::toDto);
    }

    @Override
    public Mono<EnergyModelRunDTO> update(EnergyModelRunDTO energyModelRunDTO) {
        LOG.debug("Request to update EnergyModelRun : {}", energyModelRunDTO);
        return energyModelRunRepository.save(energyModelRunMapper.toEntity(energyModelRunDTO)).map(energyModelRunMapper::toDto);
    }

    @Override
    public Mono<EnergyModelRunDTO> partialUpdate(EnergyModelRunDTO energyModelRunDTO) {
        LOG.debug("Request to partially update EnergyModelRun : {}", energyModelRunDTO);

        return energyModelRunRepository
            .findById(energyModelRunDTO.getId())
            .map(existingEnergyModelRun -> {
                energyModelRunMapper.partialUpdate(existingEnergyModelRun, energyModelRunDTO);

                return existingEnergyModelRun;
            })
            .flatMap(energyModelRunRepository::save)
            .map(energyModelRunMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EnergyModelRunDTO> findAll() {
        LOG.debug("Request to get all EnergyModelRuns");
        return energyModelRunRepository.findAll().map(energyModelRunMapper::toDto);
    }

    public Flux<EnergyModelRunDTO> findAllWithEagerRelationships() {
        return energyModelRunRepository.findAllWithEagerRelationships().map(energyModelRunMapper::toDto);
    }

    public Mono<Long> countAll() {
        return energyModelRunRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EnergyModelRunDTO> findOne(Long id) {
        LOG.debug("Request to get EnergyModelRun : {}", id);
        return energyModelRunRepository.findOneWithEagerRelationships(id).map(energyModelRunMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EnergyModelRun : {}", id);
        return energyModelRunRepository.deleteById(id);
    }
}
