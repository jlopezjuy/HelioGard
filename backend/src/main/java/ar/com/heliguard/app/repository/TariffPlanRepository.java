package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TariffPlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TariffPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TariffPlanRepository extends ReactiveCrudRepository<TariffPlan, Long>, TariffPlanRepositoryInternal {
    @Override
    Mono<TariffPlan> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TariffPlan> findAllWithEagerRelationships();

    @Override
    Flux<TariffPlan> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM tariff_plan entity WHERE entity.organization_id = :id")
    Flux<TariffPlan> findByOrganization(Long id);

    @Query("SELECT * FROM tariff_plan entity WHERE entity.organization_id IS NULL")
    Flux<TariffPlan> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM tariff_plan entity WHERE entity.installation_id = :id")
    Flux<TariffPlan> findByInstallation(Long id);

    @Query("SELECT * FROM tariff_plan entity WHERE entity.installation_id IS NULL")
    Flux<TariffPlan> findAllWhereInstallationIsNull();

    @Override
    <S extends TariffPlan> Mono<S> save(S entity);

    @Override
    Flux<TariffPlan> findAll();

    @Override
    Mono<TariffPlan> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TariffPlanRepositoryInternal {
    Flux<TariffPlan> findAllBy(Pageable pageable);

    Flux<TariffPlan> findAll();

    Mono<TariffPlan> findById(Long id);

    Mono<TariffPlan> findOneWithEagerRelationships(Long id);

    Flux<TariffPlan> findAllWithEagerRelationships();

    Flux<TariffPlan> findAllWithEagerRelationships(Pageable page);
}
