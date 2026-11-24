package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.PlanSubscription}.
 */
public interface PlanSubscriptionService {
    /**
     * Save a planSubscription.
     *
     * @param planSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlanSubscriptionDTO> save(PlanSubscriptionDTO planSubscriptionDTO);

    /**
     * Updates a planSubscription.
     *
     * @param planSubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlanSubscriptionDTO> update(PlanSubscriptionDTO planSubscriptionDTO);

    /**
     * Partially updates a planSubscription.
     *
     * @param planSubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlanSubscriptionDTO> partialUpdate(PlanSubscriptionDTO planSubscriptionDTO);

    /**
     * Get all the planSubscriptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlanSubscriptionDTO> findAll(Pageable pageable);

    /**
     * Get all the planSubscriptions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlanSubscriptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of planSubscriptions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" planSubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlanSubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" planSubscription.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
