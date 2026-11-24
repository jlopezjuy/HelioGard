package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.WeatherIngestionBatch}.
 */
public interface WeatherIngestionBatchService {
    /**
     * Save a weatherIngestionBatch.
     *
     * @param weatherIngestionBatchDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<WeatherIngestionBatchDTO> save(WeatherIngestionBatchDTO weatherIngestionBatchDTO);

    /**
     * Updates a weatherIngestionBatch.
     *
     * @param weatherIngestionBatchDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<WeatherIngestionBatchDTO> update(WeatherIngestionBatchDTO weatherIngestionBatchDTO);

    /**
     * Partially updates a weatherIngestionBatch.
     *
     * @param weatherIngestionBatchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<WeatherIngestionBatchDTO> partialUpdate(WeatherIngestionBatchDTO weatherIngestionBatchDTO);

    /**
     * Get all the weatherIngestionBatches.
     *
     * @return the list of entities.
     */
    Flux<WeatherIngestionBatchDTO> findAll();

    /**
     * Get all the weatherIngestionBatches with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<WeatherIngestionBatchDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of weatherIngestionBatches available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" weatherIngestionBatch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<WeatherIngestionBatchDTO> findOne(Long id);

    /**
     * Delete the "id" weatherIngestionBatch.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
