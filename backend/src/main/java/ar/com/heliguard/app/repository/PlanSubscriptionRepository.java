package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.PlanSubscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PlanSubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanSubscriptionRepository extends ReactiveCrudRepository<PlanSubscription, Long>, PlanSubscriptionRepositoryInternal {
    Flux<PlanSubscription> findAllBy(Pageable pageable);

    @Override
    Mono<PlanSubscription> findOneWithEagerRelationships(Long id);

    @Override
    Flux<PlanSubscription> findAllWithEagerRelationships();

    @Override
    Flux<PlanSubscription> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM plan_subscription entity WHERE entity.organization_id = :id")
    Flux<PlanSubscription> findByOrganization(Long id);

    @Query("SELECT * FROM plan_subscription entity WHERE entity.organization_id IS NULL")
    Flux<PlanSubscription> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM plan_subscription entity WHERE entity.installation_id = :id")
    Flux<PlanSubscription> findByInstallation(Long id);

    @Query("SELECT * FROM plan_subscription entity WHERE entity.installation_id IS NULL")
    Flux<PlanSubscription> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM plan_subscription entity WHERE entity.plan_version_id = :id")
    Flux<PlanSubscription> findByPlanVersion(Long id);

    @Query("SELECT * FROM plan_subscription entity WHERE entity.plan_version_id IS NULL")
    Flux<PlanSubscription> findAllWherePlanVersionIsNull();

    @Query("SELECT * FROM plan_subscription entity WHERE entity.service_agreement_id = :id")
    Flux<PlanSubscription> findByServiceAgreement(Long id);

    @Query("SELECT * FROM plan_subscription entity WHERE entity.service_agreement_id IS NULL")
    Flux<PlanSubscription> findAllWhereServiceAgreementIsNull();

    @Override
    <S extends PlanSubscription> Mono<S> save(S entity);

    @Override
    Flux<PlanSubscription> findAll();

    @Override
    Mono<PlanSubscription> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlanSubscriptionRepositoryInternal {
    Flux<PlanSubscription> findAllBy(Pageable pageable);

    Flux<PlanSubscription> findAll();

    Mono<PlanSubscription> findById(Long id);

    Mono<PlanSubscription> findOneWithEagerRelationships(Long id);

    Flux<PlanSubscription> findAllWithEagerRelationships();

    Flux<PlanSubscription> findAllWithEagerRelationships(Pageable page);
}
