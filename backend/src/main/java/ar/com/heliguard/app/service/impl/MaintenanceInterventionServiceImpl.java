package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.service.MaintenanceInterventionService;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.mapper.MaintenanceInterventionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MaintenanceIntervention}.
 */
@Service
@Transactional
public class MaintenanceInterventionServiceImpl implements MaintenanceInterventionService {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenanceInterventionServiceImpl.class);

    private final MaintenanceInterventionRepository maintenanceInterventionRepository;

    private final MaintenanceInterventionMapper maintenanceInterventionMapper;

    public MaintenanceInterventionServiceImpl(
        MaintenanceInterventionRepository maintenanceInterventionRepository,
        MaintenanceInterventionMapper maintenanceInterventionMapper
    ) {
        this.maintenanceInterventionRepository = maintenanceInterventionRepository;
        this.maintenanceInterventionMapper = maintenanceInterventionMapper;
    }

    @Override
    public Mono<MaintenanceInterventionDTO> save(MaintenanceInterventionDTO maintenanceInterventionDTO) {
        LOG.debug("Request to save MaintenanceIntervention : {}", maintenanceInterventionDTO);
        return maintenanceInterventionRepository
            .save(maintenanceInterventionMapper.toEntity(maintenanceInterventionDTO))
            .map(maintenanceInterventionMapper::toDto);
    }

    @Override
    public Mono<MaintenanceInterventionDTO> update(MaintenanceInterventionDTO maintenanceInterventionDTO) {
        LOG.debug("Request to update MaintenanceIntervention : {}", maintenanceInterventionDTO);
        return maintenanceInterventionRepository
            .save(maintenanceInterventionMapper.toEntity(maintenanceInterventionDTO))
            .map(maintenanceInterventionMapper::toDto);
    }

    @Override
    public Mono<MaintenanceInterventionDTO> partialUpdate(MaintenanceInterventionDTO maintenanceInterventionDTO) {
        LOG.debug("Request to partially update MaintenanceIntervention : {}", maintenanceInterventionDTO);

        return maintenanceInterventionRepository
            .findById(maintenanceInterventionDTO.getId())
            .map(existingMaintenanceIntervention -> {
                maintenanceInterventionMapper.partialUpdate(existingMaintenanceIntervention, maintenanceInterventionDTO);

                return existingMaintenanceIntervention;
            })
            .flatMap(maintenanceInterventionRepository::save)
            .map(maintenanceInterventionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenanceInterventionDTO> findAll() {
        LOG.debug("Request to get all MaintenanceInterventions");
        return maintenanceInterventionRepository.findAll().map(maintenanceInterventionMapper::toDto);
    }

    public Flux<MaintenanceInterventionDTO> findAllWithEagerRelationships() {
        return maintenanceInterventionRepository.findAllWithEagerRelationships().map(maintenanceInterventionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return maintenanceInterventionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenanceInterventionDTO> findOne(Long id) {
        LOG.debug("Request to get MaintenanceIntervention : {}", id);
        return maintenanceInterventionRepository.findOneWithEagerRelationships(id).map(maintenanceInterventionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MaintenanceIntervention : {}", id);
        return maintenanceInterventionRepository.deleteById(id);
    }
}
