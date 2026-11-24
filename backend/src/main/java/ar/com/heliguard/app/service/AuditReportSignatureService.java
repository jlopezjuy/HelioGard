package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditReportSignatureDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditReportSignature}.
 */
public interface AuditReportSignatureService {
    /**
     * Save a auditReportSignature.
     *
     * @param auditReportSignatureDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditReportSignatureDTO> save(AuditReportSignatureDTO auditReportSignatureDTO);

    /**
     * Updates a auditReportSignature.
     *
     * @param auditReportSignatureDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditReportSignatureDTO> update(AuditReportSignatureDTO auditReportSignatureDTO);

    /**
     * Partially updates a auditReportSignature.
     *
     * @param auditReportSignatureDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditReportSignatureDTO> partialUpdate(AuditReportSignatureDTO auditReportSignatureDTO);

    /**
     * Get all the auditReportSignatures.
     *
     * @return the list of entities.
     */
    Flux<AuditReportSignatureDTO> findAll();

    /**
     * Get all the auditReportSignatures with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditReportSignatureDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditReportSignatures available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditReportSignature.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditReportSignatureDTO> findOne(Long id);

    /**
     * Delete the "id" auditReportSignature.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
