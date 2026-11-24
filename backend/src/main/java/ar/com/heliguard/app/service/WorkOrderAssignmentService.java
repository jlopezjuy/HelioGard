package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.WorkOrderAssignment}.
 */
public interface WorkOrderAssignmentService {
    /**
     * Save a workOrderAssignment.
     *
     * @param workOrderAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<WorkOrderAssignmentDTO> save(WorkOrderAssignmentDTO workOrderAssignmentDTO);

    /**
     * Updates a workOrderAssignment.
     *
     * @param workOrderAssignmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<WorkOrderAssignmentDTO> update(WorkOrderAssignmentDTO workOrderAssignmentDTO);

    /**
     * Partially updates a workOrderAssignment.
     *
     * @param workOrderAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<WorkOrderAssignmentDTO> partialUpdate(WorkOrderAssignmentDTO workOrderAssignmentDTO);

    /**
     * Get all the workOrderAssignments.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderAssignmentDTO> findAll();

    /**
     * Get all the workOrderAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderAssignmentDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of workOrderAssignments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" workOrderAssignment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<WorkOrderAssignmentDTO> findOne(Long id);

    /**
     * Delete the "id" workOrderAssignment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
