package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DeviceIdentity}.
 */
public interface DeviceIdentityService {
    /**
     * Save a deviceIdentity.
     *
     * @param deviceIdentityDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DeviceIdentityDTO> save(DeviceIdentityDTO deviceIdentityDTO);

    /**
     * Updates a deviceIdentity.
     *
     * @param deviceIdentityDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DeviceIdentityDTO> update(DeviceIdentityDTO deviceIdentityDTO);

    /**
     * Partially updates a deviceIdentity.
     *
     * @param deviceIdentityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DeviceIdentityDTO> partialUpdate(DeviceIdentityDTO deviceIdentityDTO);

    /**
     * Get all the deviceIdentities.
     *
     * @return the list of entities.
     */
    Flux<DeviceIdentityDTO> findAll();

    /**
     * Get all the deviceIdentities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<DeviceIdentityDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of deviceIdentities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" deviceIdentity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DeviceIdentityDTO> findOne(Long id);

    /**
     * Delete the "id" deviceIdentity.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
