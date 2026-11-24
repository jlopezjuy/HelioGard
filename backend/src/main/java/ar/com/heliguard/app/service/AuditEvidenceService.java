package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditEvidence}.
 */
public interface AuditEvidenceService {
    /**
     * Save a auditEvidence.
     *
     * @param auditEvidenceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditEvidenceDTO> save(AuditEvidenceDTO auditEvidenceDTO);

    /**
     * Updates a auditEvidence.
     *
     * @param auditEvidenceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditEvidenceDTO> update(AuditEvidenceDTO auditEvidenceDTO);

    /**
     * Partially updates a auditEvidence.
     *
     * @param auditEvidenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditEvidenceDTO> partialUpdate(AuditEvidenceDTO auditEvidenceDTO);

    /**
     * Get all the auditEvidences.
     *
     * @return the list of entities.
     */
    Flux<AuditEvidenceDTO> findAll();

    /**
     * Get all the auditEvidences with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditEvidenceDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditEvidences available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditEvidence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditEvidenceDTO> findOne(Long id);

    /**
     * Delete the "id" auditEvidence.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
