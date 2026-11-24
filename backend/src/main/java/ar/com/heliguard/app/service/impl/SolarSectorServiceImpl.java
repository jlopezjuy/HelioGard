package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SolarSectorRepository;
import ar.com.heliguard.app.service.SolarSectorService;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.mapper.SolarSectorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarSector}.
 */
@Service
@Transactional
public class SolarSectorServiceImpl implements SolarSectorService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarSectorServiceImpl.class);

    private final SolarSectorRepository solarSectorRepository;

    private final SolarSectorMapper solarSectorMapper;

    public SolarSectorServiceImpl(SolarSectorRepository solarSectorRepository, SolarSectorMapper solarSectorMapper) {
        this.solarSectorRepository = solarSectorRepository;
        this.solarSectorMapper = solarSectorMapper;
    }

    @Override
    public Mono<SolarSectorDTO> save(SolarSectorDTO solarSectorDTO) {
        LOG.debug("Request to save SolarSector : {}", solarSectorDTO);
        return solarSectorRepository.save(solarSectorMapper.toEntity(solarSectorDTO)).map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<SolarSectorDTO> update(SolarSectorDTO solarSectorDTO) {
        LOG.debug("Request to update SolarSector : {}", solarSectorDTO);
        return solarSectorRepository.save(solarSectorMapper.toEntity(solarSectorDTO)).map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<SolarSectorDTO> partialUpdate(SolarSectorDTO solarSectorDTO) {
        LOG.debug("Request to partially update SolarSector : {}", solarSectorDTO);

        return solarSectorRepository
            .findById(solarSectorDTO.getId())
            .map(existingSolarSector -> {
                solarSectorMapper.partialUpdate(existingSolarSector, solarSectorDTO);

                return existingSolarSector;
            })
            .flatMap(solarSectorRepository::save)
            .map(solarSectorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarSectorDTO> findAll() {
        LOG.debug("Request to get all SolarSectors");
        return solarSectorRepository.findAll().map(solarSectorMapper::toDto);
    }

    public Flux<SolarSectorDTO> findAllWithEagerRelationships() {
        return solarSectorRepository.findAllWithEagerRelationships().map(solarSectorMapper::toDto);
    }

    public Mono<Long> countAll() {
        return solarSectorRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarSectorDTO> findOne(Long id) {
        LOG.debug("Request to get SolarSector : {}", id);
        return solarSectorRepository.findOneWithEagerRelationships(id).map(solarSectorMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarSector : {}", id);
        return solarSectorRepository.deleteById(id);
    }
}
