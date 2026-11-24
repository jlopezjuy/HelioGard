package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EnergyModelRun}.
 */
public interface EnergyModelRunService {
    /**
     * Save a energyModelRun.
     *
     * @param energyModelRunDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EnergyModelRunDTO> save(EnergyModelRunDTO energyModelRunDTO);

    /**
     * Updates a energyModelRun.
     *
     * @param energyModelRunDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EnergyModelRunDTO> update(EnergyModelRunDTO energyModelRunDTO);

    /**
     * Partially updates a energyModelRun.
     *
     * @param energyModelRunDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EnergyModelRunDTO> partialUpdate(EnergyModelRunDTO energyModelRunDTO);

    /**
     * Get all the energyModelRuns.
     *
     * @return the list of entities.
     */
    Flux<EnergyModelRunDTO> findAll();

    /**
     * Get all the energyModelRuns with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<EnergyModelRunDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of energyModelRuns available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" energyModelRun.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EnergyModelRunDTO> findOne(Long id);

    /**
     * Delete the "id" energyModelRun.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
