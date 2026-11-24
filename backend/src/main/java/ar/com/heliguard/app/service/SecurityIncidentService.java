package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SecurityIncident}.
 */
public interface SecurityIncidentService {
    /**
     * Save a securityIncident.
     *
     * @param securityIncidentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SecurityIncidentDTO> save(SecurityIncidentDTO securityIncidentDTO);

    /**
     * Updates a securityIncident.
     *
     * @param securityIncidentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SecurityIncidentDTO> update(SecurityIncidentDTO securityIncidentDTO);

    /**
     * Partially updates a securityIncident.
     *
     * @param securityIncidentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SecurityIncidentDTO> partialUpdate(SecurityIncidentDTO securityIncidentDTO);

    /**
     * Get all the securityIncidents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SecurityIncidentDTO> findAll(Pageable pageable);

    /**
     * Get all the securityIncidents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SecurityIncidentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of securityIncidents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" securityIncident.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SecurityIncidentDTO> findOne(Long id);

    /**
     * Delete the "id" securityIncident.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
