package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.LossAssessment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the LossAssessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LossAssessmentRepository extends ReactiveCrudRepository<LossAssessment, Long>, LossAssessmentRepositoryInternal {
    @Override
    Mono<LossAssessment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<LossAssessment> findAllWithEagerRelationships();

    @Override
    Flux<LossAssessment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM loss_assessment entity WHERE entity.organization_id = :id")
    Flux<LossAssessment> findByOrganization(Long id);

    @Query("SELECT * FROM loss_assessment entity WHERE entity.organization_id IS NULL")
    Flux<LossAssessment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM loss_assessment entity WHERE entity.model_run_id = :id")
    Flux<LossAssessment> findByModelRun(Long id);

    @Query("SELECT * FROM loss_assessment entity WHERE entity.model_run_id IS NULL")
    Flux<LossAssessment> findAllWhereModelRunIsNull();

    @Override
    <S extends LossAssessment> Mono<S> save(S entity);

    @Override
    Flux<LossAssessment> findAll();

    @Override
    Mono<LossAssessment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface LossAssessmentRepositoryInternal {
    Flux<LossAssessment> findAllBy(Pageable pageable);

    Flux<LossAssessment> findAll();

    Mono<LossAssessment> findById(Long id);

    Mono<LossAssessment> findOneWithEagerRelationships(Long id);

    Flux<LossAssessment> findAllWithEagerRelationships();

    Flux<LossAssessment> findAllWithEagerRelationships(Pageable page);
}
