package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.service.SolarArrayService;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.mapper.SolarArrayMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarArray}.
 */
@Service
@Transactional
public class SolarArrayServiceImpl implements SolarArrayService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarArrayServiceImpl.class);

    private final SolarArrayRepository solarArrayRepository;

    private final SolarArrayMapper solarArrayMapper;

    public SolarArrayServiceImpl(SolarArrayRepository solarArrayRepository, SolarArrayMapper solarArrayMapper) {
        this.solarArrayRepository = solarArrayRepository;
        this.solarArrayMapper = solarArrayMapper;
    }

    @Override
    public Mono<SolarArrayDTO> save(SolarArrayDTO solarArrayDTO) {
        LOG.debug("Request to save SolarArray : {}", solarArrayDTO);
        return solarArrayRepository.save(solarArrayMapper.toEntity(solarArrayDTO)).map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<SolarArrayDTO> update(SolarArrayDTO solarArrayDTO) {
        LOG.debug("Request to update SolarArray : {}", solarArrayDTO);
        return solarArrayRepository.save(solarArrayMapper.toEntity(solarArrayDTO)).map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<SolarArrayDTO> partialUpdate(SolarArrayDTO solarArrayDTO) {
        LOG.debug("Request to partially update SolarArray : {}", solarArrayDTO);

        return solarArrayRepository
            .findById(solarArrayDTO.getId())
            .map(existingSolarArray -> {
                solarArrayMapper.partialUpdate(existingSolarArray, solarArrayDTO);

                return existingSolarArray;
            })
            .flatMap(solarArrayRepository::save)
            .map(solarArrayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarArrayDTO> findAll() {
        LOG.debug("Request to get all SolarArrays");
        return solarArrayRepository.findAll().map(solarArrayMapper::toDto);
    }

    public Flux<SolarArrayDTO> findAllWithEagerRelationships() {
        return solarArrayRepository.findAllWithEagerRelationships().map(solarArrayMapper::toDto);
    }

    public Mono<Long> countAll() {
        return solarArrayRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarArrayDTO> findOne(Long id) {
        LOG.debug("Request to get SolarArray : {}", id);
        return solarArrayRepository.findOneWithEagerRelationships(id).map(solarArrayMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarArray : {}", id);
        return solarArrayRepository.deleteById(id);
    }
}
