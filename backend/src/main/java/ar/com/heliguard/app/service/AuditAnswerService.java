package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditAnswerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditAnswer}.
 */
public interface AuditAnswerService {
    /**
     * Save a auditAnswer.
     *
     * @param auditAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditAnswerDTO> save(AuditAnswerDTO auditAnswerDTO);

    /**
     * Updates a auditAnswer.
     *
     * @param auditAnswerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditAnswerDTO> update(AuditAnswerDTO auditAnswerDTO);

    /**
     * Partially updates a auditAnswer.
     *
     * @param auditAnswerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditAnswerDTO> partialUpdate(AuditAnswerDTO auditAnswerDTO);

    /**
     * Get all the auditAnswers.
     *
     * @return the list of entities.
     */
    Flux<AuditAnswerDTO> findAll();

    /**
     * Get all the auditAnswers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditAnswerDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditAnswers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditAnswer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditAnswerDTO> findOne(Long id);

    /**
     * Delete the "id" auditAnswer.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
