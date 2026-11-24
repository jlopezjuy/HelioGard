package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.service.MaintenancePlanService;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import ar.com.heliguard.app.service.mapper.MaintenancePlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MaintenancePlan}.
 */
@Service
@Transactional
public class MaintenancePlanServiceImpl implements MaintenancePlanService {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenancePlanServiceImpl.class);

    private final MaintenancePlanRepository maintenancePlanRepository;

    private final MaintenancePlanMapper maintenancePlanMapper;

    public MaintenancePlanServiceImpl(MaintenancePlanRepository maintenancePlanRepository, MaintenancePlanMapper maintenancePlanMapper) {
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.maintenancePlanMapper = maintenancePlanMapper;
    }

    @Override
    public Mono<MaintenancePlanDTO> save(MaintenancePlanDTO maintenancePlanDTO) {
        LOG.debug("Request to save MaintenancePlan : {}", maintenancePlanDTO);
        return maintenancePlanRepository.save(maintenancePlanMapper.toEntity(maintenancePlanDTO)).map(maintenancePlanMapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanDTO> update(MaintenancePlanDTO maintenancePlanDTO) {
        LOG.debug("Request to update MaintenancePlan : {}", maintenancePlanDTO);
        return maintenancePlanRepository.save(maintenancePlanMapper.toEntity(maintenancePlanDTO)).map(maintenancePlanMapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanDTO> partialUpdate(MaintenancePlanDTO maintenancePlanDTO) {
        LOG.debug("Request to partially update MaintenancePlan : {}", maintenancePlanDTO);

        return maintenancePlanRepository
            .findById(maintenancePlanDTO.getId())
            .map(existingMaintenancePlan -> {
                maintenancePlanMapper.partialUpdate(existingMaintenancePlan, maintenancePlanDTO);

                return existingMaintenancePlan;
            })
            .flatMap(maintenancePlanRepository::save)
            .map(maintenancePlanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenancePlanDTO> findAll() {
        LOG.debug("Request to get all MaintenancePlans");
        return maintenancePlanRepository.findAll().map(maintenancePlanMapper::toDto);
    }

    public Flux<MaintenancePlanDTO> findAllWithEagerRelationships() {
        return maintenancePlanRepository.findAllWithEagerRelationships().map(maintenancePlanMapper::toDto);
    }

    public Mono<Long> countAll() {
        return maintenancePlanRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenancePlanDTO> findOne(Long id) {
        LOG.debug("Request to get MaintenancePlan : {}", id);
        return maintenancePlanRepository.findOneWithEagerRelationships(id).map(maintenancePlanMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MaintenancePlan : {}", id);
        return maintenancePlanRepository.deleteById(id);
    }
}
