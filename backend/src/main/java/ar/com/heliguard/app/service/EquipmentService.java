package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EquipmentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.Equipment}.
 */
public interface EquipmentService {
    /**
     * Save a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EquipmentDTO> save(EquipmentDTO equipmentDTO);

    /**
     * Updates a equipment.
     *
     * @param equipmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EquipmentDTO> update(EquipmentDTO equipmentDTO);

    /**
     * Partially updates a equipment.
     *
     * @param equipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EquipmentDTO> partialUpdate(EquipmentDTO equipmentDTO);

    /**
     * Get all the equipments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EquipmentDTO> findAll(Pageable pageable);

    /**
     * Get all the equipments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EquipmentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of equipments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" equipment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EquipmentDTO> findOne(Long id);

    /**
     * Delete the "id" equipment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
