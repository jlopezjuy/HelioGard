package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EquipmentConnection}.
 */
public interface EquipmentConnectionService {
    /**
     * Save a equipmentConnection.
     *
     * @param equipmentConnectionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EquipmentConnectionDTO> save(EquipmentConnectionDTO equipmentConnectionDTO);

    /**
     * Updates a equipmentConnection.
     *
     * @param equipmentConnectionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EquipmentConnectionDTO> update(EquipmentConnectionDTO equipmentConnectionDTO);

    /**
     * Partially updates a equipmentConnection.
     *
     * @param equipmentConnectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EquipmentConnectionDTO> partialUpdate(EquipmentConnectionDTO equipmentConnectionDTO);

    /**
     * Get all the equipmentConnections.
     *
     * @return the list of entities.
     */
    Flux<EquipmentConnectionDTO> findAll();

    /**
     * Get all the equipmentConnections with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<EquipmentConnectionDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of equipmentConnections available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" equipmentConnection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EquipmentConnectionDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentConnection.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
