package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.RecoveryAssessment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RecoveryAssessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecoveryAssessmentRepository
    extends ReactiveCrudRepository<RecoveryAssessment, Long>, RecoveryAssessmentRepositoryInternal
{
    @Override
    Mono<RecoveryAssessment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<RecoveryAssessment> findAllWithEagerRelationships();

    @Override
    Flux<RecoveryAssessment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.organization_id = :id")
    Flux<RecoveryAssessment> findByOrganization(Long id);

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.organization_id IS NULL")
    Flux<RecoveryAssessment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.intervention_id = :id")
    Flux<RecoveryAssessment> findByIntervention(Long id);

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.intervention_id IS NULL")
    Flux<RecoveryAssessment> findAllWhereInterventionIsNull();

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.model_run_id = :id")
    Flux<RecoveryAssessment> findByModelRun(Long id);

    @Query("SELECT * FROM recovery_assessment entity WHERE entity.model_run_id IS NULL")
    Flux<RecoveryAssessment> findAllWhereModelRunIsNull();

    @Override
    <S extends RecoveryAssessment> Mono<S> save(S entity);

    @Override
    Flux<RecoveryAssessment> findAll();

    @Override
    Mono<RecoveryAssessment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RecoveryAssessmentRepositoryInternal {
    Flux<RecoveryAssessment> findAllBy(Pageable pageable);

    Flux<RecoveryAssessment> findAll();

    Mono<RecoveryAssessment> findById(Long id);

    Mono<RecoveryAssessment> findOneWithEagerRelationships(Long id);

    Flux<RecoveryAssessment> findAllWithEagerRelationships();

    Flux<RecoveryAssessment> findAllWithEagerRelationships(Pageable page);
}
