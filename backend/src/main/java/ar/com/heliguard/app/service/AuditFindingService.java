package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditFinding}.
 */
public interface AuditFindingService {
    /**
     * Save a auditFinding.
     *
     * @param auditFindingDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditFindingDTO> save(AuditFindingDTO auditFindingDTO);

    /**
     * Updates a auditFinding.
     *
     * @param auditFindingDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditFindingDTO> update(AuditFindingDTO auditFindingDTO);

    /**
     * Partially updates a auditFinding.
     *
     * @param auditFindingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditFindingDTO> partialUpdate(AuditFindingDTO auditFindingDTO);

    /**
     * Get all the auditFindings.
     *
     * @return the list of entities.
     */
    Flux<AuditFindingDTO> findAll();

    /**
     * Get all the auditFindings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditFindingDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditFindings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditFinding.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditFindingDTO> findOne(Long id);

    /**
     * Delete the "id" auditFinding.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
