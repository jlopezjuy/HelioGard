package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.IntegrityAssessment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the IntegrityAssessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntegrityAssessmentRepository
    extends ReactiveCrudRepository<IntegrityAssessment, Long>, IntegrityAssessmentRepositoryInternal
{
    @Override
    Mono<IntegrityAssessment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<IntegrityAssessment> findAllWithEagerRelationships();

    @Override
    Flux<IntegrityAssessment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.organization_id = :id")
    Flux<IntegrityAssessment> findByOrganization(Long id);

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.organization_id IS NULL")
    Flux<IntegrityAssessment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.installation_id = :id")
    Flux<IntegrityAssessment> findByInstallation(Long id);

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.installation_id IS NULL")
    Flux<IntegrityAssessment> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.integrity_event_id = :id")
    Flux<IntegrityAssessment> findByIntegrityEvent(Long id);

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.integrity_event_id IS NULL")
    Flux<IntegrityAssessment> findAllWhereIntegrityEventIsNull();

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.model_run_id = :id")
    Flux<IntegrityAssessment> findByModelRun(Long id);

    @Query("SELECT * FROM integrity_assessment entity WHERE entity.model_run_id IS NULL")
    Flux<IntegrityAssessment> findAllWhereModelRunIsNull();

    @Override
    <S extends IntegrityAssessment> Mono<S> save(S entity);

    @Override
    Flux<IntegrityAssessment> findAll();

    @Override
    Mono<IntegrityAssessment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IntegrityAssessmentRepositoryInternal {
    Flux<IntegrityAssessment> findAllBy(Pageable pageable);

    Flux<IntegrityAssessment> findAll();

    Mono<IntegrityAssessment> findById(Long id);

    Mono<IntegrityAssessment> findOneWithEagerRelationships(Long id);

    Flux<IntegrityAssessment> findAllWithEagerRelationships();

    Flux<IntegrityAssessment> findAllWithEagerRelationships(Pageable page);
}
