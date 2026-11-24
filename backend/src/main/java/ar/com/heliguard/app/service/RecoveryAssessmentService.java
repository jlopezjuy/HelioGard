package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.RecoveryAssessment}.
 */
public interface RecoveryAssessmentService {
    /**
     * Save a recoveryAssessment.
     *
     * @param recoveryAssessmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RecoveryAssessmentDTO> save(RecoveryAssessmentDTO recoveryAssessmentDTO);

    /**
     * Updates a recoveryAssessment.
     *
     * @param recoveryAssessmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RecoveryAssessmentDTO> update(RecoveryAssessmentDTO recoveryAssessmentDTO);

    /**
     * Partially updates a recoveryAssessment.
     *
     * @param recoveryAssessmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RecoveryAssessmentDTO> partialUpdate(RecoveryAssessmentDTO recoveryAssessmentDTO);

    /**
     * Get all the recoveryAssessments.
     *
     * @return the list of entities.
     */
    Flux<RecoveryAssessmentDTO> findAll();

    /**
     * Get all the recoveryAssessments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<RecoveryAssessmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of recoveryAssessments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" recoveryAssessment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RecoveryAssessmentDTO> findOne(Long id);

    /**
     * Delete the "id" recoveryAssessment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
