package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvidence}.
 */
public interface SecurityCaseEvidenceService {
    /**
     * Save a securityCaseEvidence.
     *
     * @param securityCaseEvidenceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEvidenceDTO> save(SecurityCaseEvidenceDTO securityCaseEvidenceDTO);

    /**
     * Updates a securityCaseEvidence.
     *
     * @param securityCaseEvidenceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEvidenceDTO> update(SecurityCaseEvidenceDTO securityCaseEvidenceDTO);

    /**
     * Partially updates a securityCaseEvidence.
     *
     * @param securityCaseEvidenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEvidenceDTO> partialUpdate(SecurityCaseEvidenceDTO securityCaseEvidenceDTO);

    /**
     * Get all the securityCaseEvidences.
     *
     * @return the list of entities.
     */
    Flux<SecurityCaseEvidenceDTO> findAll();

    /**
     * Get all the securityCaseEvidences with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SecurityCaseEvidenceDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of securityCaseEvidences available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" securityCaseEvidence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SecurityCaseEvidenceDTO> findOne(Long id);

    /**
     * Delete the "id" securityCaseEvidence.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
