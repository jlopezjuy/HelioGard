package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditEvidence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditEvidence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditEvidenceRepository extends ReactiveCrudRepository<AuditEvidence, Long>, AuditEvidenceRepositoryInternal {
    @Override
    Mono<AuditEvidence> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditEvidence> findAllWithEagerRelationships();

    @Override
    Flux<AuditEvidence> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_evidence entity WHERE entity.organization_id = :id")
    Flux<AuditEvidence> findByOrganization(Long id);

    @Query("SELECT * FROM audit_evidence entity WHERE entity.organization_id IS NULL")
    Flux<AuditEvidence> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_evidence entity WHERE entity.finding_id = :id")
    Flux<AuditEvidence> findByFinding(Long id);

    @Query("SELECT * FROM audit_evidence entity WHERE entity.finding_id IS NULL")
    Flux<AuditEvidence> findAllWhereFindingIsNull();

    @Override
    <S extends AuditEvidence> Mono<S> save(S entity);

    @Override
    Flux<AuditEvidence> findAll();

    @Override
    Mono<AuditEvidence> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditEvidenceRepositoryInternal {
    Flux<AuditEvidence> findAllBy(Pageable pageable);

    Flux<AuditEvidence> findAll();

    Mono<AuditEvidence> findById(Long id);

    Mono<AuditEvidence> findOneWithEagerRelationships(Long id);

    Flux<AuditEvidence> findAllWithEagerRelationships();

    Flux<AuditEvidence> findAllWithEagerRelationships(Pageable page);
}
