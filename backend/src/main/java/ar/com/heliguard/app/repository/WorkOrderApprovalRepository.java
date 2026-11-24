package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.WorkOrderApproval;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WorkOrderApproval entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkOrderApprovalRepository extends ReactiveCrudRepository<WorkOrderApproval, Long>, WorkOrderApprovalRepositoryInternal {
    @Override
    Mono<WorkOrderApproval> findOneWithEagerRelationships(Long id);

    @Override
    Flux<WorkOrderApproval> findAllWithEagerRelationships();

    @Override
    Flux<WorkOrderApproval> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM work_order_approval entity WHERE entity.organization_id = :id")
    Flux<WorkOrderApproval> findByOrganization(Long id);

    @Query("SELECT * FROM work_order_approval entity WHERE entity.organization_id IS NULL")
    Flux<WorkOrderApproval> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM work_order_approval entity WHERE entity.work_order_id = :id")
    Flux<WorkOrderApproval> findByWorkOrder(Long id);

    @Query("SELECT * FROM work_order_approval entity WHERE entity.work_order_id IS NULL")
    Flux<WorkOrderApproval> findAllWhereWorkOrderIsNull();

    @Query("SELECT * FROM work_order_approval entity WHERE entity.decided_by_membership_id = :id")
    Flux<WorkOrderApproval> findByDecidedByMembership(Long id);

    @Query("SELECT * FROM work_order_approval entity WHERE entity.decided_by_membership_id IS NULL")
    Flux<WorkOrderApproval> findAllWhereDecidedByMembershipIsNull();

    @Override
    <S extends WorkOrderApproval> Mono<S> save(S entity);

    @Override
    Flux<WorkOrderApproval> findAll();

    @Override
    Mono<WorkOrderApproval> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WorkOrderApprovalRepositoryInternal {
    Flux<WorkOrderApproval> findAllBy(Pageable pageable);

    Flux<WorkOrderApproval> findAll();

    Mono<WorkOrderApproval> findById(Long id);

    Mono<WorkOrderApproval> findOneWithEagerRelationships(Long id);

    Flux<WorkOrderApproval> findAllWithEagerRelationships();

    Flux<WorkOrderApproval> findAllWithEagerRelationships(Pageable page);
}
