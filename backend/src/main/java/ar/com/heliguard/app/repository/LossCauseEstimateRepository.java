package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.LossCauseEstimate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the LossCauseEstimate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LossCauseEstimateRepository extends ReactiveCrudRepository<LossCauseEstimate, Long>, LossCauseEstimateRepositoryInternal {
    @Override
    Mono<LossCauseEstimate> findOneWithEagerRelationships(Long id);

    @Override
    Flux<LossCauseEstimate> findAllWithEagerRelationships();

    @Override
    Flux<LossCauseEstimate> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM loss_cause_estimate entity WHERE entity.organization_id = :id")
    Flux<LossCauseEstimate> findByOrganization(Long id);

    @Query("SELECT * FROM loss_cause_estimate entity WHERE entity.organization_id IS NULL")
    Flux<LossCauseEstimate> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM loss_cause_estimate entity WHERE entity.loss_assessment_id = :id")
    Flux<LossCauseEstimate> findByLossAssessment(Long id);

    @Query("SELECT * FROM loss_cause_estimate entity WHERE entity.loss_assessment_id IS NULL")
    Flux<LossCauseEstimate> findAllWhereLossAssessmentIsNull();

    @Override
    <S extends LossCauseEstimate> Mono<S> save(S entity);

    @Override
    Flux<LossCauseEstimate> findAll();

    @Override
    Mono<LossCauseEstimate> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface LossCauseEstimateRepositoryInternal {
    Flux<LossCauseEstimate> findAllBy(Pageable pageable);

    Flux<LossCauseEstimate> findAll();

    Mono<LossCauseEstimate> findById(Long id);

    Mono<LossCauseEstimate> findOneWithEagerRelationships(Long id);

    Flux<LossCauseEstimate> findAllWithEagerRelationships();

    Flux<LossCauseEstimate> findAllWithEagerRelationships(Pageable page);
}
