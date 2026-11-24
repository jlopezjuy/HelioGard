package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.VerificationResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the VerificationResult entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VerificationResultRepository
    extends ReactiveCrudRepository<VerificationResult, Long>, VerificationResultRepositoryInternal
{
    @Override
    Mono<VerificationResult> findOneWithEagerRelationships(Long id);

    @Override
    Flux<VerificationResult> findAllWithEagerRelationships();

    @Override
    Flux<VerificationResult> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM verification_result entity WHERE entity.organization_id = :id")
    Flux<VerificationResult> findByOrganization(Long id);

    @Query("SELECT * FROM verification_result entity WHERE entity.organization_id IS NULL")
    Flux<VerificationResult> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM verification_result entity WHERE entity.installation_id = :id")
    Flux<VerificationResult> findByInstallation(Long id);

    @Query("SELECT * FROM verification_result entity WHERE entity.installation_id IS NULL")
    Flux<VerificationResult> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM verification_result entity WHERE entity.drone_mission_id = :id")
    Flux<VerificationResult> findByDroneMission(Long id);

    @Query("SELECT * FROM verification_result entity WHERE entity.drone_mission_id IS NULL")
    Flux<VerificationResult> findAllWhereDroneMissionIsNull();

    @Query("SELECT * FROM verification_result entity WHERE entity.recovery_assessment_id = :id")
    Flux<VerificationResult> findByRecoveryAssessment(Long id);

    @Query("SELECT * FROM verification_result entity WHERE entity.recovery_assessment_id IS NULL")
    Flux<VerificationResult> findAllWhereRecoveryAssessmentIsNull();

    @Override
    <S extends VerificationResult> Mono<S> save(S entity);

    @Override
    Flux<VerificationResult> findAll();

    @Override
    Mono<VerificationResult> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VerificationResultRepositoryInternal {
    Flux<VerificationResult> findAllBy(Pageable pageable);

    Flux<VerificationResult> findAll();

    Mono<VerificationResult> findById(Long id);

    Mono<VerificationResult> findOneWithEagerRelationships(Long id);

    Flux<VerificationResult> findAllWithEagerRelationships();

    Flux<VerificationResult> findAllWithEagerRelationships(Pageable page);
}
