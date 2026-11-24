package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.WorkOrderTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WorkOrderTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkOrderTaskRepository extends ReactiveCrudRepository<WorkOrderTask, Long>, WorkOrderTaskRepositoryInternal {
    @Override
    Mono<WorkOrderTask> findOneWithEagerRelationships(Long id);

    @Override
    Flux<WorkOrderTask> findAllWithEagerRelationships();

    @Override
    Flux<WorkOrderTask> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM work_order_task entity WHERE entity.organization_id = :id")
    Flux<WorkOrderTask> findByOrganization(Long id);

    @Query("SELECT * FROM work_order_task entity WHERE entity.organization_id IS NULL")
    Flux<WorkOrderTask> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM work_order_task entity WHERE entity.work_order_id = :id")
    Flux<WorkOrderTask> findByWorkOrder(Long id);

    @Query("SELECT * FROM work_order_task entity WHERE entity.work_order_id IS NULL")
    Flux<WorkOrderTask> findAllWhereWorkOrderIsNull();

    @Override
    <S extends WorkOrderTask> Mono<S> save(S entity);

    @Override
    Flux<WorkOrderTask> findAll();

    @Override
    Mono<WorkOrderTask> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WorkOrderTaskRepositoryInternal {
    Flux<WorkOrderTask> findAllBy(Pageable pageable);

    Flux<WorkOrderTask> findAll();

    Mono<WorkOrderTask> findById(Long id);

    Mono<WorkOrderTask> findOneWithEagerRelationships(Long id);

    Flux<WorkOrderTask> findAllWithEagerRelationships();

    Flux<WorkOrderTask> findAllWithEagerRelationships(Pageable page);
}
