package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.IncidentLocalization}.
 */
public interface IncidentLocalizationService {
    /**
     * Save a incidentLocalization.
     *
     * @param incidentLocalizationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<IncidentLocalizationDTO> save(IncidentLocalizationDTO incidentLocalizationDTO);

    /**
     * Updates a incidentLocalization.
     *
     * @param incidentLocalizationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<IncidentLocalizationDTO> update(IncidentLocalizationDTO incidentLocalizationDTO);

    /**
     * Partially updates a incidentLocalization.
     *
     * @param incidentLocalizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<IncidentLocalizationDTO> partialUpdate(IncidentLocalizationDTO incidentLocalizationDTO);

    /**
     * Get all the incidentLocalizations.
     *
     * @return the list of entities.
     */
    Flux<IncidentLocalizationDTO> findAll();

    /**
     * Get all the incidentLocalizations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<IncidentLocalizationDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of incidentLocalizations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" incidentLocalization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<IncidentLocalizationDTO> findOne(Long id);

    /**
     * Delete the "id" incidentLocalization.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
