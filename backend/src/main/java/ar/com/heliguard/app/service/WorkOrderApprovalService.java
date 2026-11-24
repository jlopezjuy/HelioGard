package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.WorkOrderApprovalDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.WorkOrderApproval}.
 */
public interface WorkOrderApprovalService {
    /**
     * Save a workOrderApproval.
     *
     * @param workOrderApprovalDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<WorkOrderApprovalDTO> save(WorkOrderApprovalDTO workOrderApprovalDTO);

    /**
     * Updates a workOrderApproval.
     *
     * @param workOrderApprovalDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<WorkOrderApprovalDTO> update(WorkOrderApprovalDTO workOrderApprovalDTO);

    /**
     * Partially updates a workOrderApproval.
     *
     * @param workOrderApprovalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<WorkOrderApprovalDTO> partialUpdate(WorkOrderApprovalDTO workOrderApprovalDTO);

    /**
     * Get all the workOrderApprovals.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderApprovalDTO> findAll();

    /**
     * Get all the workOrderApprovals with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderApprovalDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of workOrderApprovals available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" workOrderApproval.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<WorkOrderApprovalDTO> findOne(Long id);

    /**
     * Delete the "id" workOrderApproval.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
