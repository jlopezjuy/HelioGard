package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MaintenancePlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MaintenancePlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaintenancePlanRepository extends ReactiveCrudRepository<MaintenancePlan, Long>, MaintenancePlanRepositoryInternal {
    @Override
    Mono<MaintenancePlan> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MaintenancePlan> findAllWithEagerRelationships();

    @Override
    Flux<MaintenancePlan> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM maintenance_plan entity WHERE entity.organization_id = :id")
    Flux<MaintenancePlan> findByOrganization(Long id);

    @Query("SELECT * FROM maintenance_plan entity WHERE entity.organization_id IS NULL")
    Flux<MaintenancePlan> findAllWhereOrganizationIsNull();

    @Override
    <S extends MaintenancePlan> Mono<S> save(S entity);

    @Override
    Flux<MaintenancePlan> findAll();

    @Override
    Mono<MaintenancePlan> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MaintenancePlanRepositoryInternal {
    Flux<MaintenancePlan> findAllBy(Pageable pageable);

    Flux<MaintenancePlan> findAll();

    Mono<MaintenancePlan> findById(Long id);

    Mono<MaintenancePlan> findOneWithEagerRelationships(Long id);

    Flux<MaintenancePlan> findAllWithEagerRelationships();

    Flux<MaintenancePlan> findAllWithEagerRelationships(Pageable page);
}
