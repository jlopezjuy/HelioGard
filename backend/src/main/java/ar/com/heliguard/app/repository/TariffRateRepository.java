package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TariffRate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TariffRate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TariffRateRepository extends ReactiveCrudRepository<TariffRate, Long>, TariffRateRepositoryInternal {
    @Override
    Mono<TariffRate> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TariffRate> findAllWithEagerRelationships();

    @Override
    Flux<TariffRate> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM tariff_rate entity WHERE entity.organization_id = :id")
    Flux<TariffRate> findByOrganization(Long id);

    @Query("SELECT * FROM tariff_rate entity WHERE entity.organization_id IS NULL")
    Flux<TariffRate> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM tariff_rate entity WHERE entity.tariff_plan_id = :id")
    Flux<TariffRate> findByTariffPlan(Long id);

    @Query("SELECT * FROM tariff_rate entity WHERE entity.tariff_plan_id IS NULL")
    Flux<TariffRate> findAllWhereTariffPlanIsNull();

    @Override
    <S extends TariffRate> Mono<S> save(S entity);

    @Override
    Flux<TariffRate> findAll();

    @Override
    Mono<TariffRate> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TariffRateRepositoryInternal {
    Flux<TariffRate> findAllBy(Pageable pageable);

    Flux<TariffRate> findAll();

    Mono<TariffRate> findById(Long id);

    Mono<TariffRate> findOneWithEagerRelationships(Long id);

    Flux<TariffRate> findAllWithEagerRelationships();

    Flux<TariffRate> findAllWithEagerRelationships(Pageable page);
}
