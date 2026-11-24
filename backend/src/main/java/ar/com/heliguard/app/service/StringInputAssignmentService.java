package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.StringInputAssignment}.
 */
public interface StringInputAssignmentService {
    /**
     * Save a stringInputAssignment.
     *
     * @param stringInputAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StringInputAssignmentDTO> save(StringInputAssignmentDTO stringInputAssignmentDTO);

    /**
     * Updates a stringInputAssignment.
     *
     * @param stringInputAssignmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StringInputAssignmentDTO> update(StringInputAssignmentDTO stringInputAssignmentDTO);

    /**
     * Partially updates a stringInputAssignment.
     *
     * @param stringInputAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StringInputAssignmentDTO> partialUpdate(StringInputAssignmentDTO stringInputAssignmentDTO);

    /**
     * Get all the stringInputAssignments.
     *
     * @return the list of entities.
     */
    Flux<StringInputAssignmentDTO> findAll();

    /**
     * Get all the stringInputAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<StringInputAssignmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of stringInputAssignments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" stringInputAssignment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StringInputAssignmentDTO> findOne(Long id);

    /**
     * Delete the "id" stringInputAssignment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
