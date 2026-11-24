package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.WeatherIngestionBatchService;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import ar.com.heliguard.app.service.mapper.WeatherIngestionBatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.WeatherIngestionBatch}.
 */
@Service
@Transactional
public class WeatherIngestionBatchServiceImpl implements WeatherIngestionBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherIngestionBatchServiceImpl.class);

    private final WeatherIngestionBatchRepository weatherIngestionBatchRepository;

    private final WeatherIngestionBatchMapper weatherIngestionBatchMapper;

    public WeatherIngestionBatchServiceImpl(
        WeatherIngestionBatchRepository weatherIngestionBatchRepository,
        WeatherIngestionBatchMapper weatherIngestionBatchMapper
    ) {
        this.weatherIngestionBatchRepository = weatherIngestionBatchRepository;
        this.weatherIngestionBatchMapper = weatherIngestionBatchMapper;
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> save(WeatherIngestionBatchDTO weatherIngestionBatchDTO) {
        LOG.debug("Request to save WeatherIngestionBatch : {}", weatherIngestionBatchDTO);
        return weatherIngestionBatchRepository
            .save(weatherIngestionBatchMapper.toEntity(weatherIngestionBatchDTO))
            .map(weatherIngestionBatchMapper::toDto);
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> update(WeatherIngestionBatchDTO weatherIngestionBatchDTO) {
        LOG.debug("Request to update WeatherIngestionBatch : {}", weatherIngestionBatchDTO);
        return weatherIngestionBatchRepository
            .save(weatherIngestionBatchMapper.toEntity(weatherIngestionBatchDTO))
            .map(weatherIngestionBatchMapper::toDto);
    }

    @Override
    public Mono<WeatherIngestionBatchDTO> partialUpdate(WeatherIngestionBatchDTO weatherIngestionBatchDTO) {
        LOG.debug("Request to partially update WeatherIngestionBatch : {}", weatherIngestionBatchDTO);

        return weatherIngestionBatchRepository
            .findById(weatherIngestionBatchDTO.getId())
            .map(existingWeatherIngestionBatch -> {
                weatherIngestionBatchMapper.partialUpdate(existingWeatherIngestionBatch, weatherIngestionBatchDTO);

                return existingWeatherIngestionBatch;
            })
            .flatMap(weatherIngestionBatchRepository::save)
            .map(weatherIngestionBatchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WeatherIngestionBatchDTO> findAll() {
        LOG.debug("Request to get all WeatherIngestionBatches");
        return weatherIngestionBatchRepository.findAll().map(weatherIngestionBatchMapper::toDto);
    }

    public Flux<WeatherIngestionBatchDTO> findAllWithEagerRelationships() {
        return weatherIngestionBatchRepository.findAllWithEagerRelationships().map(weatherIngestionBatchMapper::toDto);
    }

    public Mono<Long> countAll() {
        return weatherIngestionBatchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WeatherIngestionBatchDTO> findOne(Long id) {
        LOG.debug("Request to get WeatherIngestionBatch : {}", id);
        return weatherIngestionBatchRepository.findOneWithEagerRelationships(id).map(weatherIngestionBatchMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete WeatherIngestionBatch : {}", id);
        return weatherIngestionBatchRepository.deleteById(id);
    }
}
