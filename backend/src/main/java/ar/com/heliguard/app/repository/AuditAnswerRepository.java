package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditAnswer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditAnswer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditAnswerRepository extends ReactiveCrudRepository<AuditAnswer, Long>, AuditAnswerRepositoryInternal {
    @Override
    Mono<AuditAnswer> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditAnswer> findAllWithEagerRelationships();

    @Override
    Flux<AuditAnswer> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_answer entity WHERE entity.organization_id = :id")
    Flux<AuditAnswer> findByOrganization(Long id);

    @Query("SELECT * FROM audit_answer entity WHERE entity.organization_id IS NULL")
    Flux<AuditAnswer> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_answer entity WHERE entity.audit_report_id = :id")
    Flux<AuditAnswer> findByAuditReport(Long id);

    @Query("SELECT * FROM audit_answer entity WHERE entity.audit_report_id IS NULL")
    Flux<AuditAnswer> findAllWhereAuditReportIsNull();

    @Query("SELECT * FROM audit_answer entity WHERE entity.audit_question_id = :id")
    Flux<AuditAnswer> findByAuditQuestion(Long id);

    @Query("SELECT * FROM audit_answer entity WHERE entity.audit_question_id IS NULL")
    Flux<AuditAnswer> findAllWhereAuditQuestionIsNull();

    @Override
    <S extends AuditAnswer> Mono<S> save(S entity);

    @Override
    Flux<AuditAnswer> findAll();

    @Override
    Mono<AuditAnswer> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditAnswerRepositoryInternal {
    Flux<AuditAnswer> findAllBy(Pageable pageable);

    Flux<AuditAnswer> findAll();

    Mono<AuditAnswer> findById(Long id);

    Mono<AuditAnswer> findOneWithEagerRelationships(Long id);

    Flux<AuditAnswer> findAllWithEagerRelationships();

    Flux<AuditAnswer> findAllWithEagerRelationships(Pageable page);
}
