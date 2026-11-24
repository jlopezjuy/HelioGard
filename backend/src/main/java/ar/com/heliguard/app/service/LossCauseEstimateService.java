package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.LossCauseEstimate}.
 */
public interface LossCauseEstimateService {
    /**
     * Save a lossCauseEstimate.
     *
     * @param lossCauseEstimateDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<LossCauseEstimateDTO> save(LossCauseEstimateDTO lossCauseEstimateDTO);

    /**
     * Updates a lossCauseEstimate.
     *
     * @param lossCauseEstimateDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<LossCauseEstimateDTO> update(LossCauseEstimateDTO lossCauseEstimateDTO);

    /**
     * Partially updates a lossCauseEstimate.
     *
     * @param lossCauseEstimateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<LossCauseEstimateDTO> partialUpdate(LossCauseEstimateDTO lossCauseEstimateDTO);

    /**
     * Get all the lossCauseEstimates.
     *
     * @return the list of entities.
     */
    Flux<LossCauseEstimateDTO> findAll();

    /**
     * Get all the lossCauseEstimates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<LossCauseEstimateDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of lossCauseEstimates available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" lossCauseEstimate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<LossCauseEstimateDTO> findOne(Long id);

    /**
     * Delete the "id" lossCauseEstimate.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
