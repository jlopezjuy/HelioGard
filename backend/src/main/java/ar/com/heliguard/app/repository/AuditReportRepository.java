package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditReport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditReport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditReportRepository extends ReactiveCrudRepository<AuditReport, Long>, AuditReportRepositoryInternal {
    Flux<AuditReport> findAllBy(Pageable pageable);

    @Override
    Mono<AuditReport> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditReport> findAllWithEagerRelationships();

    @Override
    Flux<AuditReport> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_report entity WHERE entity.organization_id = :id")
    Flux<AuditReport> findByOrganization(Long id);

    @Query("SELECT * FROM audit_report entity WHERE entity.organization_id IS NULL")
    Flux<AuditReport> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_report entity WHERE entity.installation_id = :id")
    Flux<AuditReport> findByInstallation(Long id);

    @Query("SELECT * FROM audit_report entity WHERE entity.installation_id IS NULL")
    Flux<AuditReport> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM audit_report entity WHERE entity.audit_template_id = :id")
    Flux<AuditReport> findByAuditTemplate(Long id);

    @Query("SELECT * FROM audit_report entity WHERE entity.audit_template_id IS NULL")
    Flux<AuditReport> findAllWhereAuditTemplateIsNull();

    @Query("SELECT * FROM audit_report entity WHERE entity.auditor_membership_id = :id")
    Flux<AuditReport> findByAuditorMembership(Long id);

    @Query("SELECT * FROM audit_report entity WHERE entity.auditor_membership_id IS NULL")
    Flux<AuditReport> findAllWhereAuditorMembershipIsNull();

    @Override
    <S extends AuditReport> Mono<S> save(S entity);

    @Override
    Flux<AuditReport> findAll();

    @Override
    Mono<AuditReport> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditReportRepositoryInternal {
    Flux<AuditReport> findAllBy(Pageable pageable);

    Flux<AuditReport> findAll();

    Mono<AuditReport> findById(Long id);

    Mono<AuditReport> findOneWithEagerRelationships(Long id);

    Flux<AuditReport> findAllWithEagerRelationships();

    Flux<AuditReport> findAllWithEagerRelationships(Pageable page);
}
