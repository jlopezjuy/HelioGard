package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SecurityCaseEvidence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SecurityCaseEvidence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityCaseEvidenceRepository
    extends ReactiveCrudRepository<SecurityCaseEvidence, Long>, SecurityCaseEvidenceRepositoryInternal
{
    @Override
    Mono<SecurityCaseEvidence> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SecurityCaseEvidence> findAllWithEagerRelationships();

    @Override
    Flux<SecurityCaseEvidence> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.organization_id = :id")
    Flux<SecurityCaseEvidence> findByOrganization(Long id);

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.organization_id IS NULL")
    Flux<SecurityCaseEvidence> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.security_incident_id = :id")
    Flux<SecurityCaseEvidence> findBySecurityIncident(Long id);

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.security_incident_id IS NULL")
    Flux<SecurityCaseEvidence> findAllWhereSecurityIncidentIsNull();

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.integrity_assessment_id = :id")
    Flux<SecurityCaseEvidence> findByIntegrityAssessment(Long id);

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.integrity_assessment_id IS NULL")
    Flux<SecurityCaseEvidence> findAllWhereIntegrityAssessmentIsNull();

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.data_provenance_id = :id")
    Flux<SecurityCaseEvidence> findByDataProvenance(Long id);

    @Query("SELECT * FROM security_case_evidence entity WHERE entity.data_provenance_id IS NULL")
    Flux<SecurityCaseEvidence> findAllWhereDataProvenanceIsNull();

    @Override
    <S extends SecurityCaseEvidence> Mono<S> save(S entity);

    @Override
    Flux<SecurityCaseEvidence> findAll();

    @Override
    Mono<SecurityCaseEvidence> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SecurityCaseEvidenceRepositoryInternal {
    Flux<SecurityCaseEvidence> findAllBy(Pageable pageable);

    Flux<SecurityCaseEvidence> findAll();

    Mono<SecurityCaseEvidence> findById(Long id);

    Mono<SecurityCaseEvidence> findOneWithEagerRelationships(Long id);

    Flux<SecurityCaseEvidence> findAllWithEagerRelationships();

    Flux<SecurityCaseEvidence> findAllWithEagerRelationships(Pageable page);
}
