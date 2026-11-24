package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MissionSafetyCheckDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MissionSafetyCheck}.
 */
public interface MissionSafetyCheckService {
    /**
     * Save a missionSafetyCheck.
     *
     * @param missionSafetyCheckDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MissionSafetyCheckDTO> save(MissionSafetyCheckDTO missionSafetyCheckDTO);

    /**
     * Updates a missionSafetyCheck.
     *
     * @param missionSafetyCheckDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MissionSafetyCheckDTO> update(MissionSafetyCheckDTO missionSafetyCheckDTO);

    /**
     * Partially updates a missionSafetyCheck.
     *
     * @param missionSafetyCheckDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MissionSafetyCheckDTO> partialUpdate(MissionSafetyCheckDTO missionSafetyCheckDTO);

    /**
     * Get all the missionSafetyChecks.
     *
     * @return the list of entities.
     */
    Flux<MissionSafetyCheckDTO> findAll();

    /**
     * Get all the missionSafetyChecks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<MissionSafetyCheckDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of missionSafetyChecks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" missionSafetyCheck.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MissionSafetyCheckDTO> findOne(Long id);

    /**
     * Delete the "id" missionSafetyCheck.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
