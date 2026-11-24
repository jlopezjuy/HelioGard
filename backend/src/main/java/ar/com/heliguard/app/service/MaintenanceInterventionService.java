package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MaintenanceIntervention}.
 */
public interface MaintenanceInterventionService {
    /**
     * Save a maintenanceIntervention.
     *
     * @param maintenanceInterventionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MaintenanceInterventionDTO> save(MaintenanceInterventionDTO maintenanceInterventionDTO);

    /**
     * Updates a maintenanceIntervention.
     *
     * @param maintenanceInterventionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MaintenanceInterventionDTO> update(MaintenanceInterventionDTO maintenanceInterventionDTO);

    /**
     * Partially updates a maintenanceIntervention.
     *
     * @param maintenanceInterventionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MaintenanceInterventionDTO> partialUpdate(MaintenanceInterventionDTO maintenanceInterventionDTO);

    /**
     * Get all the maintenanceInterventions.
     *
     * @return the list of entities.
     */
    Flux<MaintenanceInterventionDTO> findAll();

    /**
     * Get all the maintenanceInterventions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<MaintenanceInterventionDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of maintenanceInterventions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" maintenanceIntervention.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MaintenanceInterventionDTO> findOne(Long id);

    /**
     * Delete the "id" maintenanceIntervention.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
