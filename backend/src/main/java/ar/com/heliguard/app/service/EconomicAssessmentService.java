package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EconomicAssessmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EconomicAssessment}.
 */
public interface EconomicAssessmentService {
    /**
     * Save a economicAssessment.
     *
     * @param economicAssessmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EconomicAssessmentDTO> save(EconomicAssessmentDTO economicAssessmentDTO);

    /**
     * Updates a economicAssessment.
     *
     * @param economicAssessmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EconomicAssessmentDTO> update(EconomicAssessmentDTO economicAssessmentDTO);

    /**
     * Partially updates a economicAssessment.
     *
     * @param economicAssessmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EconomicAssessmentDTO> partialUpdate(EconomicAssessmentDTO economicAssessmentDTO);

    /**
     * Get all the economicAssessments.
     *
     * @return the list of entities.
     */
    Flux<EconomicAssessmentDTO> findAll();

    /**
     * Get all the economicAssessments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<EconomicAssessmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of economicAssessments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" economicAssessment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EconomicAssessmentDTO> findOne(Long id);

    /**
     * Delete the "id" economicAssessment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
