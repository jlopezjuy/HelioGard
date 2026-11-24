package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Gateway;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Gateway entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GatewayRepository extends ReactiveCrudRepository<Gateway, Long>, GatewayRepositoryInternal {
    Flux<Gateway> findAllBy(Pageable pageable);

    @Override
    Mono<Gateway> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Gateway> findAllWithEagerRelationships();

    @Override
    Flux<Gateway> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM gateway entity WHERE entity.organization_id = :id")
    Flux<Gateway> findByOrganization(Long id);

    @Query("SELECT * FROM gateway entity WHERE entity.organization_id = :orgId ORDER BY entity.id ASC LIMIT :limit OFFSET :offset")
    Flux<Gateway> findPageByOrganization(Long orgId, int limit, long offset);

    @Query("SELECT COUNT(*) FROM gateway entity WHERE entity.organization_id = :orgId")
    Mono<Long> countByOrganization(Long orgId);

    @Query("SELECT * FROM gateway entity WHERE entity.organization_id IS NULL")
    Flux<Gateway> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM gateway entity WHERE entity.installation_id = :id")
    Flux<Gateway> findByInstallation(Long id);

    @Query("SELECT * FROM gateway entity WHERE entity.installation_id IS NULL")
    Flux<Gateway> findAllWhereInstallationIsNull();

    @Override
    <S extends Gateway> Mono<S> save(S entity);

    @Override
    Flux<Gateway> findAll();

    @Override
    Mono<Gateway> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface GatewayRepositoryInternal {
    Flux<Gateway> findAllBy(Pageable pageable);

    Flux<Gateway> findAll();

    Mono<Gateway> findById(Long id);

    Mono<Gateway> findOneWithEagerRelationships(Long id);

    Flux<Gateway> findAllWithEagerRelationships();

    Flux<Gateway> findAllWithEagerRelationships(Pageable page);
}
