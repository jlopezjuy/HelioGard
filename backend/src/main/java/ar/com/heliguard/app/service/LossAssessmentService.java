package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.LossAssessment}.
 */
public interface LossAssessmentService {
    /**
     * Save a lossAssessment.
     *
     * @param lossAssessmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<LossAssessmentDTO> save(LossAssessmentDTO lossAssessmentDTO);

    /**
     * Updates a lossAssessment.
     *
     * @param lossAssessmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<LossAssessmentDTO> update(LossAssessmentDTO lossAssessmentDTO);

    /**
     * Partially updates a lossAssessment.
     *
     * @param lossAssessmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<LossAssessmentDTO> partialUpdate(LossAssessmentDTO lossAssessmentDTO);

    /**
     * Get all the lossAssessments.
     *
     * @return the list of entities.
     */
    Flux<LossAssessmentDTO> findAll();

    /**
     * Get all the lossAssessments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<LossAssessmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of lossAssessments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" lossAssessment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<LossAssessmentDTO> findOne(Long id);

    /**
     * Delete the "id" lossAssessment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
