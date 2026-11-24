package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Site;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Site entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteRepository extends ReactiveCrudRepository<Site, Long>, SiteRepositoryInternal {
    @Override
    Mono<Site> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Site> findAllWithEagerRelationships();

    @Override
    Flux<Site> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM site entity WHERE entity.organization_id = :id")
    Flux<Site> findByOrganization(Long id);

    @Query("SELECT * FROM site entity WHERE entity.organization_id IS NULL")
    Flux<Site> findAllWhereOrganizationIsNull();

    @Override
    <S extends Site> Mono<S> save(S entity);

    @Override
    Flux<Site> findAll();

    @Override
    Mono<Site> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SiteRepositoryInternal {
    Flux<Site> findAllBy(Pageable pageable);

    Flux<Site> findAll();

    Mono<Site> findById(Long id);

    Mono<Site> findOneWithEagerRelationships(Long id);

    Flux<Site> findAllWithEagerRelationships();

    Flux<Site> findAllWithEagerRelationships(Pageable page);
}
