package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.WorkOrder}.
 */
public interface WorkOrderService {
    /**
     * Save a workOrder.
     *
     * @param workOrderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<WorkOrderDTO> save(WorkOrderDTO workOrderDTO);

    /**
     * Updates a workOrder.
     *
     * @param workOrderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<WorkOrderDTO> update(WorkOrderDTO workOrderDTO);

    /**
     * Partially updates a workOrder.
     *
     * @param workOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<WorkOrderDTO> partialUpdate(WorkOrderDTO workOrderDTO);

    /**
     * Get all the workOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<WorkOrderDTO> findAll(Pageable pageable);

    /**
     * Get all the workOrders with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<WorkOrderDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of workOrders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" workOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<WorkOrderDTO> findOne(Long id);

    /**
     * Delete the "id" workOrder.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
