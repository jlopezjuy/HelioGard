package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.WorkOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WorkOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkOrderRepository extends ReactiveCrudRepository<WorkOrder, Long>, WorkOrderRepositoryInternal {
    Flux<WorkOrder> findAllBy(Pageable pageable);

    @Override
    Mono<WorkOrder> findOneWithEagerRelationships(Long id);

    @Override
    Flux<WorkOrder> findAllWithEagerRelationships();

    @Override
    Flux<WorkOrder> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM work_order entity WHERE entity.organization_id = :id")
    Flux<WorkOrder> findByOrganization(Long id);

    @Query("SELECT * FROM work_order entity WHERE entity.organization_id = :orgId ORDER BY entity.id DESC LIMIT :limit OFFSET :offset")
    Flux<WorkOrder> findPageByOrganization(Long orgId, int limit, long offset);

    @Query("SELECT COUNT(*) FROM work_order entity WHERE entity.organization_id = :orgId")
    Mono<Long> countByOrganization(Long orgId);

    @Query("SELECT * FROM work_order entity WHERE entity.organization_id IS NULL")
    Flux<WorkOrder> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM work_order entity WHERE entity.installation_id = :id")
    Flux<WorkOrder> findByInstallation(Long id);

    @Query("SELECT * FROM work_order entity WHERE entity.installation_id IS NULL")
    Flux<WorkOrder> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM work_order entity WHERE entity.incident_id = :id")
    Flux<WorkOrder> findByIncident(Long id);

    @Query("SELECT * FROM work_order entity WHERE entity.incident_id IS NULL")
    Flux<WorkOrder> findAllWhereIncidentIsNull();

    @Query("SELECT * FROM work_order entity WHERE entity.subscription_id = :id")
    Flux<WorkOrder> findBySubscription(Long id);

    @Query("SELECT * FROM work_order entity WHERE entity.subscription_id IS NULL")
    Flux<WorkOrder> findAllWhereSubscriptionIsNull();

    @Override
    <S extends WorkOrder> Mono<S> save(S entity);

    @Override
    Flux<WorkOrder> findAll();

    @Override
    Mono<WorkOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WorkOrderRepositoryInternal {
    Flux<WorkOrder> findAllBy(Pageable pageable);

    Flux<WorkOrder> findAll();

    Mono<WorkOrder> findById(Long id);

    Mono<WorkOrder> findOneWithEagerRelationships(Long id);

    Flux<WorkOrder> findAllWithEagerRelationships();

    Flux<WorkOrder> findAllWithEagerRelationships(Pageable page);
}
