package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.PlanEligibilityAssessmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.PlanEligibilityAssessment}.
 */
public interface PlanEligibilityAssessmentService {
    /**
     * Save a planEligibilityAssessment.
     *
     * @param planEligibilityAssessmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlanEligibilityAssessmentDTO> save(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO);

    /**
     * Updates a planEligibilityAssessment.
     *
     * @param planEligibilityAssessmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlanEligibilityAssessmentDTO> update(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO);

    /**
     * Partially updates a planEligibilityAssessment.
     *
     * @param planEligibilityAssessmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlanEligibilityAssessmentDTO> partialUpdate(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO);

    /**
     * Get all the planEligibilityAssessments.
     *
     * @return the list of entities.
     */
    Flux<PlanEligibilityAssessmentDTO> findAll();

    /**
     * Get all the planEligibilityAssessments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<PlanEligibilityAssessmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of planEligibilityAssessments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" planEligibilityAssessment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlanEligibilityAssessmentDTO> findOne(Long id);

    /**
     * Delete the "id" planEligibilityAssessment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
