package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MetricDefinition}.
 */
public interface MetricDefinitionService {
    /**
     * Save a metricDefinition.
     *
     * @param metricDefinitionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MetricDefinitionDTO> save(MetricDefinitionDTO metricDefinitionDTO);

    /**
     * Updates a metricDefinition.
     *
     * @param metricDefinitionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MetricDefinitionDTO> update(MetricDefinitionDTO metricDefinitionDTO);

    /**
     * Partially updates a metricDefinition.
     *
     * @param metricDefinitionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MetricDefinitionDTO> partialUpdate(MetricDefinitionDTO metricDefinitionDTO);

    /**
     * Get all the metricDefinitions.
     *
     * @return the list of entities.
     */
    Flux<MetricDefinitionDTO> findAll();

    /**
     * Returns the number of metricDefinitions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" metricDefinition.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MetricDefinitionDTO> findOne(Long id);

    /**
     * Delete the "id" metricDefinition.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
