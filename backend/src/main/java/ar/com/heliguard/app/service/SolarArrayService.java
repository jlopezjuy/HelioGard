package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SolarArray}.
 */
public interface SolarArrayService {
    /**
     * Save a solarArray.
     *
     * @param solarArrayDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SolarArrayDTO> save(SolarArrayDTO solarArrayDTO);

    /**
     * Updates a solarArray.
     *
     * @param solarArrayDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SolarArrayDTO> update(SolarArrayDTO solarArrayDTO);

    /**
     * Partially updates a solarArray.
     *
     * @param solarArrayDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SolarArrayDTO> partialUpdate(SolarArrayDTO solarArrayDTO);

    /**
     * Get all the solarArrays.
     *
     * @return the list of entities.
     */
    Flux<SolarArrayDTO> findAll();

    /**
     * Get all the solarArrays with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SolarArrayDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of solarArrays available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" solarArray.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SolarArrayDTO> findOne(Long id);

    /**
     * Delete the "id" solarArray.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
