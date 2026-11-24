package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.service.SolarStringService;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import ar.com.heliguard.app.service.mapper.SolarStringMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SolarString}.
 */
@Service
@Transactional
public class SolarStringServiceImpl implements SolarStringService {

    private static final Logger LOG = LoggerFactory.getLogger(SolarStringServiceImpl.class);

    private final SolarStringRepository solarStringRepository;

    private final SolarStringMapper solarStringMapper;

    public SolarStringServiceImpl(SolarStringRepository solarStringRepository, SolarStringMapper solarStringMapper) {
        this.solarStringRepository = solarStringRepository;
        this.solarStringMapper = solarStringMapper;
    }

    @Override
    public Mono<SolarStringDTO> save(SolarStringDTO solarStringDTO) {
        LOG.debug("Request to save SolarString : {}", solarStringDTO);
        return solarStringRepository.save(solarStringMapper.toEntity(solarStringDTO)).map(solarStringMapper::toDto);
    }

    @Override
    public Mono<SolarStringDTO> update(SolarStringDTO solarStringDTO) {
        LOG.debug("Request to update SolarString : {}", solarStringDTO);
        return solarStringRepository.save(solarStringMapper.toEntity(solarStringDTO)).map(solarStringMapper::toDto);
    }

    @Override
    public Mono<SolarStringDTO> partialUpdate(SolarStringDTO solarStringDTO) {
        LOG.debug("Request to partially update SolarString : {}", solarStringDTO);

        return solarStringRepository
            .findById(solarStringDTO.getId())
            .map(existingSolarString -> {
                solarStringMapper.partialUpdate(existingSolarString, solarStringDTO);

                return existingSolarString;
            })
            .flatMap(solarStringRepository::save)
            .map(solarStringMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SolarStringDTO> findAll() {
        LOG.debug("Request to get all SolarStrings");
        return solarStringRepository.findAll().map(solarStringMapper::toDto);
    }

    public Flux<SolarStringDTO> findAllWithEagerRelationships() {
        return solarStringRepository.findAllWithEagerRelationships().map(solarStringMapper::toDto);
    }

    public Mono<Long> countAll() {
        return solarStringRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SolarStringDTO> findOne(Long id) {
        LOG.debug("Request to get SolarString : {}", id);
        return solarStringRepository.findOneWithEagerRelationships(id).map(solarStringMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SolarString : {}", id);
        return solarStringRepository.deleteById(id);
    }
}
