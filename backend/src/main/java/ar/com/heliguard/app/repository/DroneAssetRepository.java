package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneAsset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DroneAsset entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroneAssetRepository extends ReactiveCrudRepository<DroneAsset, Long>, DroneAssetRepositoryInternal {
    @Override
    Mono<DroneAsset> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DroneAsset> findAllWithEagerRelationships();

    @Override
    Flux<DroneAsset> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM drone_asset entity WHERE entity.organization_id = :id")
    Flux<DroneAsset> findByOrganization(Long id);

    @Query("SELECT * FROM drone_asset entity WHERE entity.organization_id IS NULL")
    Flux<DroneAsset> findAllWhereOrganizationIsNull();

    @Override
    <S extends DroneAsset> Mono<S> save(S entity);

    @Override
    Flux<DroneAsset> findAll();

    @Override
    Mono<DroneAsset> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DroneAssetRepositoryInternal {
    Flux<DroneAsset> findAllBy(Pageable pageable);

    Flux<DroneAsset> findAll();

    Mono<DroneAsset> findById(Long id);

    Mono<DroneAsset> findOneWithEagerRelationships(Long id);

    Flux<DroneAsset> findAllWithEagerRelationships();

    Flux<DroneAsset> findAllWithEagerRelationships(Pageable page);
}
