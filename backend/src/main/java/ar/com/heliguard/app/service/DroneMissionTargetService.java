package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DroneMissionTarget}.
 */
public interface DroneMissionTargetService {
    /**
     * Save a droneMissionTarget.
     *
     * @param droneMissionTargetDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DroneMissionTargetDTO> save(DroneMissionTargetDTO droneMissionTargetDTO);

    /**
     * Updates a droneMissionTarget.
     *
     * @param droneMissionTargetDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DroneMissionTargetDTO> update(DroneMissionTargetDTO droneMissionTargetDTO);

    /**
     * Partially updates a droneMissionTarget.
     *
     * @param droneMissionTargetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DroneMissionTargetDTO> partialUpdate(DroneMissionTargetDTO droneMissionTargetDTO);

    /**
     * Get all the droneMissionTargets.
     *
     * @return the list of entities.
     */
    Flux<DroneMissionTargetDTO> findAll();

    /**
     * Get all the droneMissionTargets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<DroneMissionTargetDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of droneMissionTargets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" droneMissionTarget.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DroneMissionTargetDTO> findOne(Long id);

    /**
     * Delete the "id" droneMissionTarget.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
