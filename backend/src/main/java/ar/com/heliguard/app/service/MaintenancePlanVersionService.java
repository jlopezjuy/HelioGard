package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MaintenancePlanVersion}.
 */
public interface MaintenancePlanVersionService {
    /**
     * Save a maintenancePlanVersion.
     *
     * @param maintenancePlanVersionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanVersionDTO> save(MaintenancePlanVersionDTO maintenancePlanVersionDTO);

    /**
     * Updates a maintenancePlanVersion.
     *
     * @param maintenancePlanVersionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanVersionDTO> update(MaintenancePlanVersionDTO maintenancePlanVersionDTO);

    /**
     * Partially updates a maintenancePlanVersion.
     *
     * @param maintenancePlanVersionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MaintenancePlanVersionDTO> partialUpdate(MaintenancePlanVersionDTO maintenancePlanVersionDTO);

    /**
     * Get all the maintenancePlanVersions.
     *
     * @return the list of entities.
     */
    Flux<MaintenancePlanVersionDTO> findAll();

    /**
     * Get all the maintenancePlanVersions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<MaintenancePlanVersionDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of maintenancePlanVersions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" maintenancePlanVersion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MaintenancePlanVersionDTO> findOne(Long id);

    /**
     * Delete the "id" maintenancePlanVersion.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
