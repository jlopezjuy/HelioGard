package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.TelemetryChannelRepository;
import ar.com.heliguard.app.service.TelemetryChannelService;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import ar.com.heliguard.app.service.mapper.TelemetryChannelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryChannel}.
 */
@Service
@Transactional
public class TelemetryChannelServiceImpl implements TelemetryChannelService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelServiceImpl.class);

    private final TelemetryChannelRepository telemetryChannelRepository;

    private final TelemetryChannelMapper telemetryChannelMapper;

    public TelemetryChannelServiceImpl(
        TelemetryChannelRepository telemetryChannelRepository,
        TelemetryChannelMapper telemetryChannelMapper
    ) {
        this.telemetryChannelRepository = telemetryChannelRepository;
        this.telemetryChannelMapper = telemetryChannelMapper;
    }

    @Override
    public Mono<TelemetryChannelDTO> save(TelemetryChannelDTO telemetryChannelDTO) {
        LOG.debug("Request to save TelemetryChannel : {}", telemetryChannelDTO);
        return telemetryChannelRepository.save(telemetryChannelMapper.toEntity(telemetryChannelDTO)).map(telemetryChannelMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelDTO> update(TelemetryChannelDTO telemetryChannelDTO) {
        LOG.debug("Request to update TelemetryChannel : {}", telemetryChannelDTO);
        return telemetryChannelRepository.save(telemetryChannelMapper.toEntity(telemetryChannelDTO)).map(telemetryChannelMapper::toDto);
    }

    @Override
    public Mono<TelemetryChannelDTO> partialUpdate(TelemetryChannelDTO telemetryChannelDTO) {
        LOG.debug("Request to partially update TelemetryChannel : {}", telemetryChannelDTO);

        return telemetryChannelRepository
            .findById(telemetryChannelDTO.getId())
            .map(existingTelemetryChannel -> {
                telemetryChannelMapper.partialUpdate(existingTelemetryChannel, telemetryChannelDTO);

                return existingTelemetryChannel;
            })
            .flatMap(telemetryChannelRepository::save)
            .map(telemetryChannelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryChannelDTO> findAll() {
        LOG.debug("Request to get all TelemetryChannels");
        return telemetryChannelRepository.findAll().map(telemetryChannelMapper::toDto);
    }

    public Flux<TelemetryChannelDTO> findAllWithEagerRelationships() {
        return telemetryChannelRepository.findAllWithEagerRelationships().map(telemetryChannelMapper::toDto);
    }

    public Mono<Long> countAll() {
        return telemetryChannelRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryChannelDTO> findOne(Long id) {
        LOG.debug("Request to get TelemetryChannel : {}", id);
        return telemetryChannelRepository.findOneWithEagerRelationships(id).map(telemetryChannelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TelemetryChannel : {}", id);
        return telemetryChannelRepository.deleteById(id);
    }
}
