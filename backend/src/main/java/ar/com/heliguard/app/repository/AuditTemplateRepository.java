package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditTemplateRepository extends ReactiveCrudRepository<AuditTemplate, Long>, AuditTemplateRepositoryInternal {
    @Override
    Mono<AuditTemplate> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AuditTemplate> findAllWithEagerRelationships();

    @Override
    Flux<AuditTemplate> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM audit_template entity WHERE entity.organization_id = :id")
    Flux<AuditTemplate> findByOrganization(Long id);

    @Query("SELECT * FROM audit_template entity WHERE entity.organization_id IS NULL")
    Flux<AuditTemplate> findAllWhereOrganizationIsNull();

    @Override
    <S extends AuditTemplate> Mono<S> save(S entity);

    @Override
    Flux<AuditTemplate> findAll();

    @Override
    Mono<AuditTemplate> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditTemplateRepositoryInternal {
    Flux<AuditTemplate> findAllBy(Pageable pageable);

    Flux<AuditTemplate> findAll();

    Mono<AuditTemplate> findById(Long id);

    Mono<AuditTemplate> findOneWithEagerRelationships(Long id);

    Flux<AuditTemplate> findAllWithEagerRelationships();

    Flux<AuditTemplate> findAllWithEagerRelationships(Pageable page);
}
