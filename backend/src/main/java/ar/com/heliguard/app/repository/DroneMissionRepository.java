package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DroneMission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroneMissionRepository extends ReactiveCrudRepository<DroneMission, Long>, DroneMissionRepositoryInternal {
    Flux<DroneMission> findAllBy(Pageable pageable);

    @Override
    Mono<DroneMission> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DroneMission> findAllWithEagerRelationships();

    @Override
    Flux<DroneMission> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM drone_mission entity WHERE entity.organization_id = :id")
    Flux<DroneMission> findByOrganization(Long id);

    @Query("SELECT * FROM drone_mission entity WHERE entity.organization_id IS NULL")
    Flux<DroneMission> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM drone_mission entity WHERE entity.installation_id = :id")
    Flux<DroneMission> findByInstallation(Long id);

    @Query("SELECT * FROM drone_mission entity WHERE entity.installation_id IS NULL")
    Flux<DroneMission> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM drone_mission entity WHERE entity.work_order_id = :id")
    Flux<DroneMission> findByWorkOrder(Long id);

    @Query("SELECT * FROM drone_mission entity WHERE entity.work_order_id IS NULL")
    Flux<DroneMission> findAllWhereWorkOrderIsNull();

    @Query("SELECT * FROM drone_mission entity WHERE entity.drone_asset_id = :id")
    Flux<DroneMission> findByDroneAsset(Long id);

    @Query("SELECT * FROM drone_mission entity WHERE entity.drone_asset_id IS NULL")
    Flux<DroneMission> findAllWhereDroneAssetIsNull();

    @Query("SELECT * FROM drone_mission entity WHERE entity.provider_organization_id = :id")
    Flux<DroneMission> findByProviderOrganization(Long id);

    @Query("SELECT * FROM drone_mission entity WHERE entity.provider_organization_id IS NULL")
    Flux<DroneMission> findAllWhereProviderOrganizationIsNull();

    @Override
    <S extends DroneMission> Mono<S> save(S entity);

    @Override
    Flux<DroneMission> findAll();

    @Override
    Mono<DroneMission> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DroneMissionRepositoryInternal {
    Flux<DroneMission> findAllBy(Pageable pageable);

    Flux<DroneMission> findAll();

    Mono<DroneMission> findById(Long id);

    Mono<DroneMission> findOneWithEagerRelationships(Long id);

    Flux<DroneMission> findAllWithEagerRelationships();

    Flux<DroneMission> findAllWithEagerRelationships(Pageable page);
}
