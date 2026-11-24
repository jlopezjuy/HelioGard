package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DroneMission}.
 */
public interface DroneMissionService {
    /**
     * Save a droneMission.
     *
     * @param droneMissionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DroneMissionDTO> save(DroneMissionDTO droneMissionDTO);

    /**
     * Updates a droneMission.
     *
     * @param droneMissionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DroneMissionDTO> update(DroneMissionDTO droneMissionDTO);

    /**
     * Partially updates a droneMission.
     *
     * @param droneMissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DroneMissionDTO> partialUpdate(DroneMissionDTO droneMissionDTO);

    /**
     * Get all the droneMissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DroneMissionDTO> findAll(Pageable pageable);

    /**
     * Get all the droneMissions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DroneMissionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of droneMissions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" droneMission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DroneMissionDTO> findOne(Long id);

    /**
     * Delete the "id" droneMission.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
