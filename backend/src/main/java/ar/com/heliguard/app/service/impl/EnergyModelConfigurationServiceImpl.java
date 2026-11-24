package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.service.EnergyModelConfigurationService;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import ar.com.heliguard.app.service.mapper.EnergyModelConfigurationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EnergyModelConfiguration}.
 */
@Service
@Transactional
public class EnergyModelConfigurationServiceImpl implements EnergyModelConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(EnergyModelConfigurationServiceImpl.class);

    private final EnergyModelConfigurationRepository energyModelConfigurationRepository;

    private final EnergyModelConfigurationMapper energyModelConfigurationMapper;

    public EnergyModelConfigurationServiceImpl(
        EnergyModelConfigurationRepository energyModelConfigurationRepository,
        EnergyModelConfigurationMapper energyModelConfigurationMapper
    ) {
        this.energyModelConfigurationRepository = energyModelConfigurationRepository;
        this.energyModelConfigurationMapper = energyModelConfigurationMapper;
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> save(EnergyModelConfigurationDTO energyModelConfigurationDTO) {
        LOG.debug("Request to save EnergyModelConfiguration : {}", energyModelConfigurationDTO);
        return energyModelConfigurationRepository
            .save(energyModelConfigurationMapper.toEntity(energyModelConfigurationDTO))
            .map(energyModelConfigurationMapper::toDto);
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> update(EnergyModelConfigurationDTO energyModelConfigurationDTO) {
        LOG.debug("Request to update EnergyModelConfiguration : {}", energyModelConfigurationDTO);
        return energyModelConfigurationRepository
            .save(energyModelConfigurationMapper.toEntity(energyModelConfigurationDTO))
            .map(energyModelConfigurationMapper::toDto);
    }

    @Override
    public Mono<EnergyModelConfigurationDTO> partialUpdate(EnergyModelConfigurationDTO energyModelConfigurationDTO) {
        LOG.debug("Request to partially update EnergyModelConfiguration : {}", energyModelConfigurationDTO);

        return energyModelConfigurationRepository
            .findById(energyModelConfigurationDTO.getId())
            .map(existingEnergyModelConfiguration -> {
                energyModelConfigurationMapper.partialUpdate(existingEnergyModelConfiguration, energyModelConfigurationDTO);

                return existingEnergyModelConfiguration;
            })
            .flatMap(energyModelConfigurationRepository::save)
            .map(energyModelConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EnergyModelConfigurationDTO> findAll() {
        LOG.debug("Request to get all EnergyModelConfigurations");
        return energyModelConfigurationRepository.findAll().map(energyModelConfigurationMapper::toDto);
    }

    public Flux<EnergyModelConfigurationDTO> findAllWithEagerRelationships() {
        return energyModelConfigurationRepository.findAllWithEagerRelationships().map(energyModelConfigurationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return energyModelConfigurationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EnergyModelConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get EnergyModelConfiguration : {}", id);
        return energyModelConfigurationRepository.findOneWithEagerRelationships(id).map(energyModelConfigurationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EnergyModelConfiguration : {}", id);
        return energyModelConfigurationRepository.deleteById(id);
    }
}
