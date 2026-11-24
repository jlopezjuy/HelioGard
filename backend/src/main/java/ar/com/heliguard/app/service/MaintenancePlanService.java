package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MaintenancePlan}.
 */
public interface MaintenancePlanService {
    /**
     * Save a maintenancePlan.
     *
     * @param maintenancePlanDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanDTO> save(MaintenancePlanDTO maintenancePlanDTO);

    /**
     * Updates a maintenancePlan.
     *
     * @param maintenancePlanDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanDTO> update(MaintenancePlanDTO maintenancePlanDTO);

    /**
     * Partially updates a maintenancePlan.
     *
     * @param maintenancePlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanDTO> partialUpdate(MaintenancePlanDTO maintenancePlanDTO);

    /**
     * Get all the maintenancePlans.
     *
     * @return the list of entities.
     */
    Flux<MaintenancePlanDTO> findAll();

    /**
     * Get all the maintenancePlans with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<MaintenancePlanDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of maintenancePlans available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" maintenancePlan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MaintenancePlanDTO> findOne(Long id);

    /**
     * Delete the "id" maintenancePlan.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
