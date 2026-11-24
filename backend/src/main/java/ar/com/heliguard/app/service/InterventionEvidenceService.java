package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.InterventionEvidence}.
 */
public interface InterventionEvidenceService {
    /**
     * Save a interventionEvidence.
     *
     * @param interventionEvidenceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InterventionEvidenceDTO> save(InterventionEvidenceDTO interventionEvidenceDTO);

    /**
     * Updates a interventionEvidence.
     *
     * @param interventionEvidenceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InterventionEvidenceDTO> update(InterventionEvidenceDTO interventionEvidenceDTO);

    /**
     * Partially updates a interventionEvidence.
     *
     * @param interventionEvidenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InterventionEvidenceDTO> partialUpdate(InterventionEvidenceDTO interventionEvidenceDTO);

    /**
     * Get all the interventionEvidences.
     *
     * @return the list of entities.
     */
    Flux<InterventionEvidenceDTO> findAll();

    /**
     * Get all the interventionEvidences with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<InterventionEvidenceDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of interventionEvidences available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" interventionEvidence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InterventionEvidenceDTO> findOne(Long id);

    /**
     * Delete the "id" interventionEvidence.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
