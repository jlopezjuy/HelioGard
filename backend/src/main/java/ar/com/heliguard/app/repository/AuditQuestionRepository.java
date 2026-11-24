package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditQuestion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditQuestionRepository extends ReactiveCrudRepository<AuditQuestion, Long>, AuditQuestionRepositoryInternal {
    @Override
    Mono<AuditQuestion> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditQuestion> findAllWithEagerRelationships();

    @Override
    Flux<AuditQuestion> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_question entity WHERE entity.organization_id = :id")
    Flux<AuditQuestion> findByOrganization(Long id);

    @Query("SELECT * FROM audit_question entity WHERE entity.organization_id IS NULL")
    Flux<AuditQuestion> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM audit_question entity WHERE entity.audit_template_id = :id")
    Flux<AuditQuestion> findByAuditTemplate(Long id);

    @Query("SELECT * FROM audit_question entity WHERE entity.audit_template_id IS NULL")
    Flux<AuditQuestion> findAllWhereAuditTemplateIsNull();

    @Override
    <S extends AuditQuestion> Mono<S> save(S entity);

    @Override
    Flux<AuditQuestion> findAll();

    @Override
    Mono<AuditQuestion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditQuestionRepositoryInternal {
    Flux<AuditQuestion> findAllBy(Pageable pageable);

    Flux<AuditQuestion> findAll();

    Mono<AuditQuestion> findById(Long id);

    Mono<AuditQuestion> findOneWithEagerRelationships(Long id);

    Flux<AuditQuestion> findAllWithEagerRelationships();

    Flux<AuditQuestion> findAllWithEagerRelationships(Pageable page);
}
