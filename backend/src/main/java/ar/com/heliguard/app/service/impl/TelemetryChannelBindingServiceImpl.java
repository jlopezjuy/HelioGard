package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.TelemetryChannelBindingRepository;
import ar.com.heliguard.app.service.TelemetryChannelBindingService;
import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
import ar.com.heliguard.app.service.mapper.TelemetryChannelBindingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryChannelBinding}.
 */
@Service
@Transactional
public class TelemetryChannelBindingServiceImpl implements TelemetryChannelBindingService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelBindingServiceImpl.class);

    private final TelemetryChannelBindingRepository telemetryChannelBindingRepository;

    private final TelemetryChannelBindingMapper telemetryChannelBindingMapper;

    public TelemetryChannelBindingServiceImpl(
        TelemetryChannelBindingRepository telemetryChannelBindingRepository,
        TelemetryChannelBindingMapper telemetryChannelBindingMapper
    ) {
        this.telemetryChannelBindingRepository = telemetryChannelBindingRepository;
        this.telemetryChannelBindingMapper = telemetryChannelBindingMapper;
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> save(TelemetryChannelBindingDTO telemetryChannelBindingDTO) {
        LOG.debug("Request to save TelemetryChannelBinding : {}", telemetryChannelBindingDTO);
        return telemetryChannelBindingRepository
            .save(telemetryChannelBindingMapper.toEntity(telemetryChannelBindingDTO))
            .map(telemetryChannelBindingMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> update(TelemetryChannelBindingDTO telemetryChannelBindingDTO) {
        LOG.debug("Request to update TelemetryChannelBinding : {}", telemetryChannelBindingDTO);
        return telemetryChannelBindingRepository
            .save(telemetryChannelBindingMapper.toEntity(telemetryChannelBindingDTO))
            .map(telemetryChannelBindingMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelBindingDTO> partialUpdate(TelemetryChannelBindingDTO telemetryChannelBindingDTO) {
        LOG.debug("Request to partially update TelemetryChannelBinding : {}", telemetryChannelBindingDTO);

        return telemetryChannelBindingRepository
            .findById(telemetryChannelBindingDTO.getId())
            .map(existingTelemetryChannelBinding -> {
                telemetryChannelBindingMapper.partialUpdate(existingTelemetryChannelBinding, telemetryChannelBindingDTO);

                return existingTelemetryChannelBinding;
            })
            .flatMap(telemetryChannelBindingRepository::save)
            .map(telemetryChannelBindingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelBindingDTO> findAll() {
        LOG.debug("Request to get all TelemetryChannelBindings");
        return telemetryChannelBindingRepository.findAll().map(telemetryChannelBindingMapper::toDto);
    }

    public Flux<TelemetryChannelBindingDTO> findAllWithEagerRelationships() {
        return telemetryChannelBindingRepository.findAllWithEagerRelationships().map(telemetryChannelBindingMapper::toDto);
    }

    public Mono<Long> countAll() {
        return telemetryChannelBindingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryChannelBindingDTO> findOne(Long id) {
        LOG.debug("Request to get TelemetryChannelBinding : {}", id);
        return telemetryChannelBindingRepository.findOneWithEagerRelationships(id).map(telemetryChannelBindingMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TelemetryChannelBinding : {}", id);
        return telemetryChannelBindingRepository.deleteById(id);
    }
}
