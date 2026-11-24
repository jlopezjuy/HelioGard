package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.service.MaintenancePlanVersionService;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.mapper.MaintenancePlanVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MaintenancePlanVersion}.
 */
@Service
@Transactional
public class MaintenancePlanVersionServiceImpl implements MaintenancePlanVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenancePlanVersionServiceImpl.class);

    private final MaintenancePlanVersionRepository maintenancePlanVersionRepository;

    private final MaintenancePlanVersionMapper maintenancePlanVersionMapper;

    public MaintenancePlanVersionServiceImpl(
        MaintenancePlanVersionRepository maintenancePlanVersionRepository,
        MaintenancePlanVersionMapper maintenancePlanVersionMapper
    ) {
        this.maintenancePlanVersionRepository = maintenancePlanVersionRepository;
        this.maintenancePlanVersionMapper = maintenancePlanVersionMapper;
    }

    @Override
    public Mono<MaintenancePlanVersionDTO> save(MaintenancePlanVersionDTO maintenancePlanVersionDTO) {
        LOG.debug("Request to save MaintenancePlanVersion : {}", maintenancePlanVersionDTO);
        return maintenancePlanVersionRepository
            .save(maintenancePlanVersionMapper.toEntity(maintenancePlanVersionDTO))
            .map(maintenancePlanVersionMapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanVersionDTO> update(MaintenancePlanVersionDTO maintenancePlanVersionDTO) {
        LOG.debug("Request to update MaintenancePlanVersion : {}", maintenancePlanVersionDTO);
        return maintenancePlanVersionRepository
            .save(maintenancePlanVersionMapper.toEntity(maintenancePlanVersionDTO))
            .map(maintenancePlanVersionMapper::toDto);
    }

    @Override
    public Mono<MaintenancePlanVersionDTO> partialUpdate(MaintenancePlanVersionDTO maintenancePlanVersionDTO) {
        LOG.debug("Request to partially update MaintenancePlanVersion : {}", maintenancePlanVersionDTO);

        return maintenancePlanVersionRepository
            .findById(maintenancePlanVersionDTO.getId())
            .map(existingMaintenancePlanVersion -> {
                maintenancePlanVersionMapper.partialUpdate(existingMaintenancePlanVersion, maintenancePlanVersionDTO);

                return existingMaintenancePlanVersion;
            })
            .flatMap(maintenancePlanVersionRepository::save)
            .map(maintenancePlanVersionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenancePlanVersionDTO> findAll() {
        LOG.debug("Request to get all MaintenancePlanVersions");
        return maintenancePlanVersionRepository.findAll().map(maintenancePlanVersionMapper::toDto);
    }

    public Flux<MaintenancePlanVersionDTO> findAllWithEagerRelationships() {
        return maintenancePlanVersionRepository.findAllWithEagerRelationships().map(maintenancePlanVersionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return maintenancePlanVersionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenancePlanVersionDTO> findOne(Long id) {
        LOG.debug("Request to get MaintenancePlanVersion : {}", id);
        return maintenancePlanVersionRepository.findOneWithEagerRelationships(id).map(maintenancePlanVersionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MaintenancePlanVersion : {}", id);
        return maintenancePlanVersionRepository.deleteById(id);
    }
}
