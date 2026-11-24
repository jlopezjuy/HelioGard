package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DroneAsset}.
 */
public interface DroneAssetService {
    /**
     * Save a droneAsset.
     *
     * @param droneAssetDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DroneAssetDTO> save(DroneAssetDTO droneAssetDTO);

    /**
     * Updates a droneAsset.
     *
     * @param droneAssetDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DroneAssetDTO> update(DroneAssetDTO droneAssetDTO);

    /**
     * Partially updates a droneAsset.
     *
     * @param droneAssetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DroneAssetDTO> partialUpdate(DroneAssetDTO droneAssetDTO);

    /**
     * Get all the droneAssets.
     *
     * @return the list of entities.
     */
    Flux<DroneAssetDTO> findAll();

    /**
     * Get all the droneAssets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<DroneAssetDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of droneAssets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" droneAsset.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DroneAssetDTO> findOne(Long id);

    /**
     * Delete the "id" droneAsset.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
