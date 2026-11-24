package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.WorkOrderAssignment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WorkOrderAssignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkOrderAssignmentRepository
    extends ReactiveCrudRepository<WorkOrderAssignment, Long>, WorkOrderAssignmentRepositoryInternal
{
    @Override
    Mono<WorkOrderAssignment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<WorkOrderAssignment> findAllWithEagerRelationships();

    @Override
    Flux<WorkOrderAssignment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.organization_id = :id")
    Flux<WorkOrderAssignment> findByOrganization(Long id);

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.organization_id IS NULL")
    Flux<WorkOrderAssignment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.work_order_id = :id")
    Flux<WorkOrderAssignment> findByWorkOrder(Long id);

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.work_order_id IS NULL")
    Flux<WorkOrderAssignment> findAllWhereWorkOrderIsNull();

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.provider_organization_id = :id")
    Flux<WorkOrderAssignment> findByProviderOrganization(Long id);

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.provider_organization_id IS NULL")
    Flux<WorkOrderAssignment> findAllWhereProviderOrganizationIsNull();

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.assignee_membership_id = :id")
    Flux<WorkOrderAssignment> findByAssigneeMembership(Long id);

    @Query("SELECT * FROM work_order_assignment entity WHERE entity.assignee_membership_id IS NULL")
    Flux<WorkOrderAssignment> findAllWhereAssigneeMembershipIsNull();

    @Override
    <S extends WorkOrderAssignment> Mono<S> save(S entity);

    @Override
    Flux<WorkOrderAssignment> findAll();

    @Override
    Mono<WorkOrderAssignment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WorkOrderAssignmentRepositoryInternal {
    Flux<WorkOrderAssignment> findAllBy(Pageable pageable);

    Flux<WorkOrderAssignment> findAll();

    Mono<WorkOrderAssignment> findById(Long id);

    Mono<WorkOrderAssignment> findOneWithEagerRelationships(Long id);

    Flux<WorkOrderAssignment> findAllWithEagerRelationships();

    Flux<WorkOrderAssignment> findAllWithEagerRelationships(Pageable page);
}
