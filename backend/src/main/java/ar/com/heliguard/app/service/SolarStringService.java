package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SolarStringDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SolarString}.
 */
public interface SolarStringService {
    /**
     * Save a solarString.
     *
     * @param solarStringDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SolarStringDTO> save(SolarStringDTO solarStringDTO);

    /**
     * Updates a solarString.
     *
     * @param solarStringDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SolarStringDTO> update(SolarStringDTO solarStringDTO);

    /**
     * Partially updates a solarString.
     *
     * @param solarStringDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SolarStringDTO> partialUpdate(SolarStringDTO solarStringDTO);

    /**
     * Get all the solarStrings.
     *
     * @return the list of entities.
     */
    Flux<SolarStringDTO> findAll();

    /**
     * Get all the solarStrings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SolarStringDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of solarStrings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" solarString.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SolarStringDTO> findOne(Long id);

    /**
     * Delete the "id" solarString.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
