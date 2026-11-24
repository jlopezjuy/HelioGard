package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.IncidentEventDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.IncidentEvent}.
 */
public interface IncidentEventService {
    /**
     * Save a incidentEvent.
     *
     * @param incidentEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<IncidentEventDTO> save(IncidentEventDTO incidentEventDTO);

    /**
     * Updates a incidentEvent.
     *
     * @param incidentEventDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<IncidentEventDTO> update(IncidentEventDTO incidentEventDTO);

    /**
     * Partially updates a incidentEvent.
     *
     * @param incidentEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<IncidentEventDTO> partialUpdate(IncidentEventDTO incidentEventDTO);

    /**
     * Get all the incidentEvents.
     *
     * @return the list of entities.
     */
    Flux<IncidentEventDTO> findAll();

    /**
     * Get all the incidentEvents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<IncidentEventDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of incidentEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" incidentEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<IncidentEventDTO> findOne(Long id);

    /**
     * Delete the "id" incidentEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
