package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Installation;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Installation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstallationRepository extends ReactiveCrudRepository<Installation, Long>, InstallationRepositoryInternal {
    Flux<Installation> findAllBy(Pageable pageable);

    @Override
    Mono<Installation> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Installation> findAllWithEagerRelationships();

    @Override
    Flux<Installation> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM installation entity WHERE entity.organization_id = :id")
    Flux<Installation> findByOrganization(Long id);

    @Query("SELECT * FROM installation entity WHERE entity.organization_id = :organizationId AND entity.code = :code")
    Mono<Installation> findByOrganizationIdAndCode(Long organizationId, String code);

    @Query(
        "SELECT DISTINCT i.* FROM installation i " +
        "LEFT JOIN installation_access_grant g ON g.installation_id = i.id " +
        "AND g.grantee_organization_id = :orgId AND g.revoked_at IS NULL " +
        "AND g.valid_from <= :now AND (g.valid_until IS NULL OR g.valid_until > :now) " +
        "WHERE i.organization_id = :orgId OR g.id IS NOT NULL " +
        "ORDER BY i.id ASC LIMIT :limit OFFSET :offset"
    )
    Flux<Installation> findAllAccessibleByOrganization(Long orgId, Instant now, int limit, long offset);

    @Query(
        "SELECT COUNT(DISTINCT i.id) FROM installation i " +
        "LEFT JOIN installation_access_grant g ON g.installation_id = i.id " +
        "AND g.grantee_organization_id = :orgId AND g.revoked_at IS NULL " +
        "AND g.valid_from <= :now AND (g.valid_until IS NULL OR g.valid_until > :now) " +
        "WHERE i.organization_id = :orgId OR g.id IS NOT NULL"
    )
    Mono<Long> countAccessibleByOrganization(Long orgId, Instant now);

    @Query("SELECT * FROM installation entity WHERE entity.organization_id IS NULL")
    Flux<Installation> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM installation entity WHERE entity.site_id = :id")
    Flux<Installation> findBySite(Long id);

    @Query("SELECT * FROM installation entity WHERE entity.site_id IS NULL")
    Flux<Installation> findAllWhereSiteIsNull();

    @Query("SELECT * FROM installation entity WHERE entity.climate_risk_profile_id = :id")
    Flux<Installation> findByClimateRiskProfile(Long id);

    @Query("SELECT * FROM installation entity WHERE entity.climate_risk_profile_id IS NULL")
    Flux<Installation> findAllWhereClimateRiskProfileIsNull();

    @Override
    <S extends Installation> Mono<S> save(S entity);

    @Override
    Flux<Installation> findAll();

    @Override
    Mono<Installation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InstallationRepositoryInternal {
    Flux<Installation> findAllBy(Pageable pageable);

    Flux<Installation> findAll();

    Mono<Installation> findById(Long id);

    Mono<Installation> findOneWithEagerRelationships(Long id);

    Flux<Installation> findAllWithEagerRelationships();

    Flux<Installation> findAllWithEagerRelationships(Pageable page);
}
