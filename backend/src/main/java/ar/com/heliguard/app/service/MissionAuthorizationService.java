package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.MissionAuthorization}.
 */
public interface MissionAuthorizationService {
    /**
     * Save a missionAuthorization.
     *
     * @param missionAuthorizationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MissionAuthorizationDTO> save(MissionAuthorizationDTO missionAuthorizationDTO);

    /**
     * Updates a missionAuthorization.
     *
     * @param missionAuthorizationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MissionAuthorizationDTO> update(MissionAuthorizationDTO missionAuthorizationDTO);

    /**
     * Partially updates a missionAuthorization.
     *
     * @param missionAuthorizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MissionAuthorizationDTO> partialUpdate(MissionAuthorizationDTO missionAuthorizationDTO);

    /**
     * Get all the missionAuthorizations.
     *
     * @return the list of entities.
     */
    Flux<MissionAuthorizationDTO> findAll();

    /**
     * Get all the missionAuthorizations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<MissionAuthorizationDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of missionAuthorizations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" missionAuthorization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MissionAuthorizationDTO> findOne(Long id);

    /**
     * Delete the "id" missionAuthorization.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
