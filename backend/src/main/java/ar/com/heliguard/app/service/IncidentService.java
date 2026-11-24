package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.IncidentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.Incident}.
 */
public interface IncidentService {
    /**
     * Save a incident.
     *
     * @param incidentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<IncidentDTO> save(IncidentDTO incidentDTO);

    /**
     * Updates a incident.
     *
     * @param incidentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<IncidentDTO> update(IncidentDTO incidentDTO);

    /**
     * Partially updates a incident.
     *
     * @param incidentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<IncidentDTO> partialUpdate(IncidentDTO incidentDTO);

    /**
     * Get all the incidents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<IncidentDTO> findAll(Pageable pageable);

    /**
     * Get all the incidents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<IncidentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of incidents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" incident.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<IncidentDTO> findOne(Long id);

    /**
     * Delete the "id" incident.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
