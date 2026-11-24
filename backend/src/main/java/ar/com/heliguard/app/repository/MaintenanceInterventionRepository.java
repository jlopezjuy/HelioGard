package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MaintenanceIntervention;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MaintenanceIntervention entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaintenanceInterventionRepository
    extends ReactiveCrudRepository<MaintenanceIntervention, Long>, MaintenanceInterventionRepositoryInternal
{
    @Override
    Mono<MaintenanceIntervention> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MaintenanceIntervention> findAllWithEagerRelationships();

    @Override
    Flux<MaintenanceIntervention> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.organization_id = :id")
    Flux<MaintenanceIntervention> findByOrganization(Long id);

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.organization_id IS NULL")
    Flux<MaintenanceIntervention> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.work_order_id = :id")
    Flux<MaintenanceIntervention> findByWorkOrder(Long id);

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.work_order_id IS NULL")
    Flux<MaintenanceIntervention> findAllWhereWorkOrderIsNull();

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.performed_by_membership_id = :id")
    Flux<MaintenanceIntervention> findByPerformedByMembership(Long id);

    @Query("SELECT * FROM maintenance_intervention entity WHERE entity.performed_by_membership_id IS NULL")
    Flux<MaintenanceIntervention> findAllWherePerformedByMembershipIsNull();

    @Override
    <S extends MaintenanceIntervention> Mono<S> save(S entity);

    @Override
    Flux<MaintenanceIntervention> findAll();

    @Override
    Mono<MaintenanceIntervention> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MaintenanceInterventionRepositoryInternal {
    Flux<MaintenanceIntervention> findAllBy(Pageable pageable);

    Flux<MaintenanceIntervention> findAll();

    Mono<MaintenanceIntervention> findById(Long id);

    Mono<MaintenanceIntervention> findOneWithEagerRelationships(Long id);

    Flux<MaintenanceIntervention> findAllWithEagerRelationships();

    Flux<MaintenanceIntervention> findAllWithEagerRelationships(Pageable page);
}
