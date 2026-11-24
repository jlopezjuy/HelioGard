package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MaintenancePlanVersion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaintenancePlanVersionRepository
    extends ReactiveCrudRepository<MaintenancePlanVersion, Long>, MaintenancePlanVersionRepositoryInternal
{
    @Override
    Mono<MaintenancePlanVersion> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MaintenancePlanVersion> findAllWithEagerRelationships();

    @Override
    Flux<MaintenancePlanVersion> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM maintenance_plan_version entity WHERE entity.organization_id = :id")
    Flux<MaintenancePlanVersion> findByOrganization(Long id);

    @Query("SELECT * FROM maintenance_plan_version entity WHERE entity.organization_id IS NULL")
    Flux<MaintenancePlanVersion> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM maintenance_plan_version entity WHERE entity.maintenance_plan_id = :id")
    Flux<MaintenancePlanVersion> findByMaintenancePlan(Long id);

    @Query("SELECT * FROM maintenance_plan_version entity WHERE entity.maintenance_plan_id IS NULL")
    Flux<MaintenancePlanVersion> findAllWhereMaintenancePlanIsNull();

    @Override
    <S extends MaintenancePlanVersion> Mono<S> save(S entity);

    @Override
    Flux<MaintenancePlanVersion> findAll();

    @Override
    Mono<MaintenancePlanVersion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MaintenancePlanVersionRepositoryInternal {
    Flux<MaintenancePlanVersion> findAllBy(Pageable pageable);

    Flux<MaintenancePlanVersion> findAll();

    Mono<MaintenancePlanVersion> findById(Long id);

    Mono<MaintenancePlanVersion> findOneWithEagerRelationships(Long id);

    Flux<MaintenancePlanVersion> findAllWithEagerRelationships();

    Flux<MaintenancePlanVersion> findAllWithEagerRelationships(Pageable page);
}
