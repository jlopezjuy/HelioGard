package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.WorkOrderTaskDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.WorkOrderTask}.
 */
public interface WorkOrderTaskService {
    /**
     * Save a workOrderTask.
     *
     * @param workOrderTaskDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<WorkOrderTaskDTO> save(WorkOrderTaskDTO workOrderTaskDTO);

    /**
     * Updates a workOrderTask.
     *
     * @param workOrderTaskDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<WorkOrderTaskDTO> update(WorkOrderTaskDTO workOrderTaskDTO);

    /**
     * Partially updates a workOrderTask.
     *
     * @param workOrderTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<WorkOrderTaskDTO> partialUpdate(WorkOrderTaskDTO workOrderTaskDTO);

    /**
     * Get all the workOrderTasks.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderTaskDTO> findAll();

    /**
     * Get all the workOrderTasks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<WorkOrderTaskDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of workOrderTasks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" workOrderTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<WorkOrderTaskDTO> findOne(Long id);

    /**
     * Delete the "id" workOrderTask.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
