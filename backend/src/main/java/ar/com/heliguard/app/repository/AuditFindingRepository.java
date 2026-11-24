package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditFinding;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditFinding entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditFindingRepository extends ReactiveCrudRepository<AuditFinding, Long>, AuditFindingRepositoryInternal {
    @Override
    Mono<AuditFinding> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditFinding> findAllWithEagerRelationships();

    @Override
    Flux<AuditFinding> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_finding entity WHERE entity.organization_id = :id")
    Flux<AuditFinding> findByOrganization(Long id);

    @Query("SELECT * FROM audit_finding entity WHERE entity.organization_id IS NULL")
    Flux<AuditFinding> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_finding entity WHERE entity.audit_report_id = :id")
    Flux<AuditFinding> findByAuditReport(Long id);

    @Query("SELECT * FROM audit_finding entity WHERE entity.audit_report_id IS NULL")
    Flux<AuditFinding> findAllWhereAuditReportIsNull();

    @Query("SELECT * FROM audit_finding entity WHERE entity.equipment_id = :id")
    Flux<AuditFinding> findByEquipment(Long id);

    @Query("SELECT * FROM audit_finding entity WHERE entity.equipment_id IS NULL")
    Flux<AuditFinding> findAllWhereEquipmentIsNull();

    @Override
    <S extends AuditFinding> Mono<S> save(S entity);

    @Override
    Flux<AuditFinding> findAll();

    @Override
    Mono<AuditFinding> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditFindingRepositoryInternal {
    Flux<AuditFinding> findAllBy(Pageable pageable);

    Flux<AuditFinding> findAll();

    Mono<AuditFinding> findById(Long id);

    Mono<AuditFinding> findOneWithEagerRelationships(Long id);

    Flux<AuditFinding> findAllWithEagerRelationships();

    Flux<AuditFinding> findAllWithEagerRelationships(Pageable page);
}
