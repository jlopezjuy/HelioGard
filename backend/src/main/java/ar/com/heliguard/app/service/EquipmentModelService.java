package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EquipmentModel}.
 */
public interface EquipmentModelService {
    /**
     * Save a equipmentModel.
     *
     * @param equipmentModelDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EquipmentModelDTO> save(EquipmentModelDTO equipmentModelDTO);

    /**
     * Updates a equipmentModel.
     *
     * @param equipmentModelDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EquipmentModelDTO> update(EquipmentModelDTO equipmentModelDTO);

    /**
     * Partially updates a equipmentModel.
     *
     * @param equipmentModelDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EquipmentModelDTO> partialUpdate(EquipmentModelDTO equipmentModelDTO);

    /**
     * Get all the equipmentModels.
     *
     * @return the list of entities.
     */
    Flux<EquipmentModelDTO> findAll();

    /**
     * Returns the number of equipmentModels available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" equipmentModel.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EquipmentModelDTO> findOne(Long id);

    /**
     * Delete the "id" equipmentModel.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
