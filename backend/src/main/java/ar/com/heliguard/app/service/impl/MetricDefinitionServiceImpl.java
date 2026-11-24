package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MetricDefinitionRepository;
import ar.com.heliguard.app.service.MetricDefinitionService;
import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
import ar.com.heliguard.app.service.mapper.MetricDefinitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MetricDefinition}.
 */
@Service
@Transactional
public class MetricDefinitionServiceImpl implements MetricDefinitionService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricDefinitionServiceImpl.class);

    private final MetricDefinitionRepository metricDefinitionRepository;

    private final MetricDefinitionMapper metricDefinitionMapper;

    public MetricDefinitionServiceImpl(
        MetricDefinitionRepository metricDefinitionRepository,
        MetricDefinitionMapper metricDefinitionMapper
    ) {
        this.metricDefinitionRepository = metricDefinitionRepository;
        this.metricDefinitionMapper = metricDefinitionMapper;
    }

    @Override
    public Mono<MetricDefinitionDTO> save(MetricDefinitionDTO metricDefinitionDTO) {
        LOG.debug("Request to save MetricDefinition : {}", metricDefinitionDTO);
        return metricDefinitionRepository.save(metricDefinitionMapper.toEntity(metricDefinitionDTO)).map(metricDefinitionMapper::toDto);
    }

    @Override
    public Mono<MetricDefinitionDTO> update(MetricDefinitionDTO metricDefinitionDTO) {
        LOG.debug("Request to update MetricDefinition : {}", metricDefinitionDTO);
        return metricDefinitionRepository.save(metricDefinitionMapper.toEntity(metricDefinitionDTO)).map(metricDefinitionMapper::toDto);
    }

    @Override
    public Mono<MetricDefinitionDTO> partialUpdate(MetricDefinitionDTO metricDefinitionDTO) {
        LOG.debug("Request to partially update MetricDefinition : {}", metricDefinitionDTO);

        return metricDefinitionRepository
            .findById(metricDefinitionDTO.getId())
            .map(existingMetricDefinition -> {
                metricDefinitionMapper.partialUpdate(existingMetricDefinition, metricDefinitionDTO);

                return existingMetricDefinition;
            })
            .flatMap(metricDefinitionRepository::save)
            .map(metricDefinitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MetricDefinitionDTO> findAll() {
        LOG.debug("Request to get all MetricDefinitions");
        return metricDefinitionRepository.findAll().map(metricDefinitionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return metricDefinitionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MetricDefinitionDTO> findOne(Long id) {
        LOG.debug("Request to get MetricDefinition : {}", id);
        return metricDefinitionRepository.findById(id).map(metricDefinitionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MetricDefinition : {}", id);
        return metricDefinitionRepository.deleteById(id);
    }
}
