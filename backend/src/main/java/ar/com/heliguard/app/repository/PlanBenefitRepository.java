package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.PlanBenefit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PlanBenefit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanBenefitRepository extends ReactiveCrudRepository<PlanBenefit, Long>, PlanBenefitRepositoryInternal {
    @Override
    Mono<PlanBenefit> findOneWithEagerRelationships(Long id);

    @Override
    Flux<PlanBenefit> findAllWithEagerRelationships();

    @Override
    Flux<PlanBenefit> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM plan_benefit entity WHERE entity.organization_id = :id")
    Flux<PlanBenefit> findByOrganization(Long id);

    @Query("SELECT * FROM plan_benefit entity WHERE entity.organization_id IS NULL")
    Flux<PlanBenefit> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM plan_benefit entity WHERE entity.plan_version_id = :id")
    Flux<PlanBenefit> findByPlanVersion(Long id);

    @Query("SELECT * FROM plan_benefit entity WHERE entity.plan_version_id IS NULL")
    Flux<PlanBenefit> findAllWherePlanVersionIsNull();

    @Override
    <S extends PlanBenefit> Mono<S> save(S entity);

    @Override
    Flux<PlanBenefit> findAll();

    @Override
    Mono<PlanBenefit> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlanBenefitRepositoryInternal {
    Flux<PlanBenefit> findAllBy(Pageable pageable);

    Flux<PlanBenefit> findAll();

    Mono<PlanBenefit> findById(Long id);

    Mono<PlanBenefit> findOneWithEagerRelationships(Long id);

    Flux<PlanBenefit> findAllWithEagerRelationships();

    Flux<PlanBenefit> findAllWithEagerRelationships(Pageable page);
}
