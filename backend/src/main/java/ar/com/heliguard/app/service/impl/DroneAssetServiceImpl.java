package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.service.DroneAssetService;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import ar.com.heliguard.app.service.mapper.DroneAssetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DroneAsset}.
 */
@Service
@Transactional
public class DroneAssetServiceImpl implements DroneAssetService {

    private static final Logger LOG = LoggerFactory.getLogger(DroneAssetServiceImpl.class);

    private final DroneAssetRepository droneAssetRepository;

    private final DroneAssetMapper droneAssetMapper;

    public DroneAssetServiceImpl(DroneAssetRepository droneAssetRepository, DroneAssetMapper droneAssetMapper) {
        this.droneAssetRepository = droneAssetRepository;
        this.droneAssetMapper = droneAssetMapper;
    }

    @Override
    public Mono<DroneAssetDTO> save(DroneAssetDTO droneAssetDTO) {
        LOG.debug("Request to save DroneAsset : {}", droneAssetDTO);
        return droneAssetRepository.save(droneAssetMapper.toEntity(droneAssetDTO)).map(droneAssetMapper::toDto);
    }

    @Override
    public Mono<DroneAssetDTO> update(DroneAssetDTO droneAssetDTO) {
        LOG.debug("Request to update DroneAsset : {}", droneAssetDTO);
        return droneAssetRepository.save(droneAssetMapper.toEntity(droneAssetDTO)).map(droneAssetMapper::toDto);
    }

    @Override
    public Mono<DroneAssetDTO> partialUpdate(DroneAssetDTO droneAssetDTO) {
        LOG.debug("Request to partially update DroneAsset : {}", droneAssetDTO);

        return droneAssetRepository
            .findById(droneAssetDTO.getId())
            .map(existingDroneAsset -> {
                droneAssetMapper.partialUpdate(existingDroneAsset, droneAssetDTO);

                return existingDroneAsset;
            })
            .flatMap(droneAssetRepository::save)
            .map(droneAssetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DroneAssetDTO> findAll() {
        LOG.debug("Request to get all DroneAssets");
        return droneAssetRepository.findAll().map(droneAssetMapper::toDto);
    }

    public Flux<DroneAssetDTO> findAllWithEagerRelationships() {
        return droneAssetRepository.findAllWithEagerRelationships().map(droneAssetMapper::toDto);
    }

    public Mono<Long> countAll() {
        return droneAssetRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DroneAssetDTO> findOne(Long id) {
        LOG.debug("Request to get DroneAsset : {}", id);
        return droneAssetRepository.findOneWithEagerRelationships(id).map(droneAssetMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DroneAsset : {}", id);
        return droneAssetRepository.deleteById(id);
    }
}
