package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditQuestion}.
 */
public interface AuditQuestionService {
    /**
     * Save a auditQuestion.
     *
     * @param auditQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditQuestionDTO> save(AuditQuestionDTO auditQuestionDTO);

    /**
     * Updates a auditQuestion.
     *
     * @param auditQuestionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditQuestionDTO> update(AuditQuestionDTO auditQuestionDTO);

    /**
     * Partially updates a auditQuestion.
     *
     * @param auditQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditQuestionDTO> partialUpdate(AuditQuestionDTO auditQuestionDTO);

    /**
     * Get all the auditQuestions.
     *
     * @return the list of entities.
     */
    Flux<AuditQuestionDTO> findAll();

    /**
     * Get all the auditQuestions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AuditQuestionDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of auditQuestions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditQuestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditQuestionDTO> findOne(Long id);

    /**
     * Delete the "id" auditQuestion.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
