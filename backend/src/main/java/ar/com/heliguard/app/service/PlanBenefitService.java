package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.PlanBenefitDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.PlanBenefit}.
 */
public interface PlanBenefitService {
    /**
     * Save a planBenefit.
     *
     * @param planBenefitDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlanBenefitDTO> save(PlanBenefitDTO planBenefitDTO);

    /**
     * Updates a planBenefit.
     *
     * @param planBenefitDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlanBenefitDTO> update(PlanBenefitDTO planBenefitDTO);

    /**
     * Partially updates a planBenefit.
     *
     * @param planBenefitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlanBenefitDTO> partialUpdate(PlanBenefitDTO planBenefitDTO);

    /**
     * Get all the planBenefits.
     *
     * @return the list of entities.
     */
    Flux<PlanBenefitDTO> findAll();

    /**
     * Get all the planBenefits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<PlanBenefitDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of planBenefits available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" planBenefit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlanBenefitDTO> findOne(Long id);

    /**
     * Delete the "id" planBenefit.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
