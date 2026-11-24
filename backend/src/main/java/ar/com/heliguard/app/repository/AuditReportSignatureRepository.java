package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditReportSignature;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditReportSignature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditReportSignatureRepository
    extends ReactiveCrudRepository<AuditReportSignature, Long>, AuditReportSignatureRepositoryInternal
{
    @Override
    Mono<AuditReportSignature> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditReportSignature> findAllWithEagerRelationships();

    @Override
    Flux<AuditReportSignature> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.organization_id = :id")
    Flux<AuditReportSignature> findByOrganization(Long id);

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.organization_id IS NULL")
    Flux<AuditReportSignature> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.audit_report_id = :id")
    Flux<AuditReportSignature> findByAuditReport(Long id);

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.audit_report_id IS NULL")
    Flux<AuditReportSignature> findAllWhereAuditReportIsNull();

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.signer_membership_id = :id")
    Flux<AuditReportSignature> findBySignerMembership(Long id);

    @Query("SELECT * FROM audit_report_signature entity WHERE entity.signer_membership_id IS NULL")
    Flux<AuditReportSignature> findAllWhereSignerMembershipIsNull();

    @Override
    <S extends AuditReportSignature> Mono<S> save(S entity);

    @Override
    Flux<AuditReportSignature> findAll();

    @Override
    Mono<AuditReportSignature> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditReportSignatureRepositoryInternal {
    Flux<AuditReportSignature> findAllBy(Pageable pageable);

    Flux<AuditReportSignature> findAll();

    Mono<AuditReportSignature> findById(Long id);

    Mono<AuditReportSignature> findOneWithEagerRelationships(Long id);

    Flux<AuditReportSignature> findAllWithEagerRelationships();

    Flux<AuditReportSignature> findAllWithEagerRelationships(Pageable page);
}
