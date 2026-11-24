package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.IntegrityAssessment}.
 */
public interface IntegrityAssessmentService {
    /**
     * Save a integrityAssessment.
     *
     * @param integrityAssessmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<IntegrityAssessmentDTO> save(IntegrityAssessmentDTO integrityAssessmentDTO);

    /**
     * Updates a integrityAssessment.
     *
     * @param integrityAssessmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<IntegrityAssessmentDTO> update(IntegrityAssessmentDTO integrityAssessmentDTO);

    /**
     * Partially updates a integrityAssessment.
     *
     * @param integrityAssessmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<IntegrityAssessmentDTO> partialUpdate(IntegrityAssessmentDTO integrityAssessmentDTO);

    /**
     * Get all the integrityAssessments.
     *
     * @return the list of entities.
     */
    Flux<IntegrityAssessmentDTO> findAll();

    /**
     * Get all the integrityAssessments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<IntegrityAssessmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of integrityAssessments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" integrityAssessment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<IntegrityAssessmentDTO> findOne(Long id);

    /**
     * Delete the "id" integrityAssessment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
