package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.TelemetryIntegrityEventService;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import ar.com.heliguard.app.service.mapper.TelemetryIntegrityEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryIntegrityEvent}.
 */
@Service
@Transactional
public class TelemetryIntegrityEventServiceImpl implements TelemetryIntegrityEventService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryIntegrityEventServiceImpl.class);

    private final TelemetryIntegrityEventRepository telemetryIntegrityEventRepository;

    private final TelemetryIntegrityEventMapper telemetryIntegrityEventMapper;

    public TelemetryIntegrityEventServiceImpl(
        TelemetryIntegrityEventRepository telemetryIntegrityEventRepository,
        TelemetryIntegrityEventMapper telemetryIntegrityEventMapper
    ) {
        this.telemetryIntegrityEventRepository = telemetryIntegrityEventRepository;
        this.telemetryIntegrityEventMapper = telemetryIntegrityEventMapper;
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> save(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO) {
        LOG.debug("Request to save TelemetryIntegrityEvent : {}", telemetryIntegrityEventDTO);
        return telemetryIntegrityEventRepository
            .save(telemetryIntegrityEventMapper.toEntity(telemetryIntegrityEventDTO))
            .map(telemetryIntegrityEventMapper::toDto);
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> update(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO) {
        LOG.debug("Request to update TelemetryIntegrityEvent : {}", telemetryIntegrityEventDTO);
        return telemetryIntegrityEventRepository
            .save(telemetryIntegrityEventMapper.toEntity(telemetryIntegrityEventDTO))
            .map(telemetryIntegrityEventMapper::toDto);
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> partialUpdate(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO) {
        LOG.debug("Request to partially update TelemetryIntegrityEvent : {}", telemetryIntegrityEventDTO);

        return telemetryIntegrityEventRepository
            .findById(telemetryIntegrityEventDTO.getId())
            .map(existingTelemetryIntegrityEvent -> {
                telemetryIntegrityEventMapper.partialUpdate(existingTelemetryIntegrityEvent, telemetryIntegrityEventDTO);

                return existingTelemetryIntegrityEvent;
            })
            .flatMap(telemetryIntegrityEventRepository::save)
            .map(telemetryIntegrityEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryIntegrityEventDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TelemetryIntegrityEvents");
        return telemetryIntegrityEventRepository.findAllBy(pageable).map(telemetryIntegrityEventMapper::toDto);
    }

    public Flux<TelemetryIntegrityEventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return telemetryIntegrityEventRepository.findAllWithEagerRelationships(pageable).map(telemetryIntegrityEventMapper::toDto);
    }

    public Mono<Long> countAll() {
        return telemetryIntegrityEventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryIntegrityEventDTO> findOne(Long id) {
        LOG.debug("Request to get TelemetryIntegrityEvent : {}", id);
        return telemetryIntegrityEventRepository.findOneWithEagerRelationships(id).map(telemetryIntegrityEventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TelemetryIntegrityEvent : {}", id);
        return telemetryIntegrityEventRepository.deleteById(id);
    }
}
