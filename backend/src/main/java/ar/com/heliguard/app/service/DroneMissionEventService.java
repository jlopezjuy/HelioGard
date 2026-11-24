package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DroneMissionEventDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DroneMissionEvent}.
 */
public interface DroneMissionEventService {
    /**
     * Save a droneMissionEvent.
     *
     * @param droneMissionEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DroneMissionEventDTO> save(DroneMissionEventDTO droneMissionEventDTO);

    /**
     * Updates a droneMissionEvent.
     *
     * @param droneMissionEventDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DroneMissionEventDTO> update(DroneMissionEventDTO droneMissionEventDTO);

    /**
     * Partially updates a droneMissionEvent.
     *
     * @param droneMissionEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DroneMissionEventDTO> partialUpdate(DroneMissionEventDTO droneMissionEventDTO);

    /**
     * Get all the droneMissionEvents.
     *
     * @return the list of entities.
     */
    Flux<DroneMissionEventDTO> findAll();

    /**
     * Get all the droneMissionEvents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<DroneMissionEventDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of droneMissionEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" droneMissionEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DroneMissionEventDTO> findOne(Long id);

    /**
     * Delete the "id" droneMissionEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
