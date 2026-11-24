package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Incident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Incident entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncidentRepository extends ReactiveCrudRepository<Incident, Long>, IncidentRepositoryInternal {
    Flux<Incident> findAllBy(Pageable pageable);

    @Override
    Mono<Incident> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Incident> findAllWithEagerRelationships();

    @Override
    Flux<Incident> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM incident entity WHERE entity.organization_id = :id")
    Flux<Incident> findByOrganization(Long id);

    @Query("SELECT * FROM incident entity WHERE entity.organization_id = :orgId ORDER BY entity.id DESC LIMIT :limit OFFSET :offset")
    Flux<Incident> findPageByOrganization(Long orgId, int limit, long offset);

    @Query("SELECT COUNT(*) FROM incident entity WHERE entity.organization_id = :orgId")
    Mono<Long> countByOrganization(Long orgId);

    @Query(
        "SELECT * FROM incident entity WHERE entity.organization_id = :orgId AND entity.dedup_key = :dedupKey " +
        "AND entity.status IN ('OPEN', 'ACKNOWLEDGED', 'IN_PROGRESS')"
    )
    Mono<Incident> findActiveByOrgAndDedupKey(Long orgId, String dedupKey);

    @Query("SELECT * FROM incident entity WHERE entity.organization_id IS NULL")
    Flux<Incident> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM incident entity WHERE entity.installation_id = :id")
    Flux<Incident> findByInstallation(Long id);

    @Query("SELECT * FROM incident entity WHERE entity.installation_id IS NULL")
    Flux<Incident> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM incident entity WHERE entity.alert_rule_version_id = :id")
    Flux<Incident> findByAlertRuleVersion(Long id);

    @Query("SELECT * FROM incident entity WHERE entity.alert_rule_version_id IS NULL")
    Flux<Incident> findAllWhereAlertRuleVersionIsNull();

    @Override
    <S extends Incident> Mono<S> save(S entity);

    @Override
    Flux<Incident> findAll();

    @Override
    Mono<Incident> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IncidentRepositoryInternal {
    Flux<Incident> findAllBy(Pageable pageable);

    Flux<Incident> findAll();

    Mono<Incident> findById(Long id);

    Mono<Incident> findOneWithEagerRelationships(Long id);

    Flux<Incident> findAllWithEagerRelationships();

    Flux<Incident> findAllWithEagerRelationships(Pageable page);
}
