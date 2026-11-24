package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditTemplate}.
 */
public interface AuditTemplateService {
    /**
     * Save a auditTemplate.
     *
     * @param auditTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditTemplateDTO> save(AuditTemplateDTO auditTemplateDTO);

    /**
     * Updates a auditTemplate.
     *
     * @param auditTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditTemplateDTO> update(AuditTemplateDTO auditTemplateDTO);

    /**
     * Partially updates a auditTemplate.
     *
     * @param auditTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditTemplateDTO> partialUpdate(AuditTemplateDTO auditTemplateDTO);

    /**
     * Get all the auditTemplates.
     *
     * @return the list of entities.
     */
    Flux<AuditTemplateDTO> findAll();

    /**
     * Get all the auditTemplates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditTemplateDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditTemplates available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" auditTemplate.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
