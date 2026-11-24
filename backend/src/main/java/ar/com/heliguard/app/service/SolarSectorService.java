package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SolarSector}.
 */
public interface SolarSectorService {
    /**
     * Save a solarSector.
     *
     * @param solarSectorDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SolarSectorDTO> save(SolarSectorDTO solarSectorDTO);

    /**
     * Updates a solarSector.
     *
     * @param solarSectorDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SolarSectorDTO> update(SolarSectorDTO solarSectorDTO);

    /**
     * Partially updates a solarSector.
     *
     * @param solarSectorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SolarSectorDTO> partialUpdate(SolarSectorDTO solarSectorDTO);

    /**
     * Get all the solarSectors.
     *
     * @return the list of entities.
     */
    Flux<SolarSectorDTO> findAll();

    /**
     * Get all the solarSectors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SolarSectorDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of solarSectors available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" solarSector.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SolarSectorDTO> findOne(Long id);

    /**
     * Delete the "id" solarSector.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
