package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMissionTarget;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DroneMissionTarget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroneMissionTargetRepository
    extends ReactiveCrudRepository<DroneMissionTarget, Long>, DroneMissionTargetRepositoryInternal
{
    @Override
    Mono<DroneMissionTarget> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DroneMissionTarget> findAllWithEagerRelationships();

    @Override
    Flux<DroneMissionTarget> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.organization_id = :id")
    Flux<DroneMissionTarget> findByOrganization(Long id);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.organization_id IS NULL")
    Flux<DroneMissionTarget> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.mission_id = :id")
    Flux<DroneMissionTarget> findByMission(Long id);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.mission_id IS NULL")
    Flux<DroneMissionTarget> findAllWhereMissionIsNull();

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.solar_sector_id = :id")
    Flux<DroneMissionTarget> findBySolarSector(Long id);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.solar_sector_id IS NULL")
    Flux<DroneMissionTarget> findAllWhereSolarSectorIsNull();

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.solar_string_id = :id")
    Flux<DroneMissionTarget> findBySolarString(Long id);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.solar_string_id IS NULL")
    Flux<DroneMissionTarget> findAllWhereSolarStringIsNull();

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.equipment_id = :id")
    Flux<DroneMissionTarget> findByEquipment(Long id);

    @Query("SELECT * FROM drone_mission_target entity WHERE entity.equipment_id IS NULL")
    Flux<DroneMissionTarget> findAllWhereEquipmentIsNull();

    @Override
    <S extends DroneMissionTarget> Mono<S> save(S entity);

    @Override
    Flux<DroneMissionTarget> findAll();

    @Override
    Mono<DroneMissionTarget> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DroneMissionTargetRepositoryInternal {
    Flux<DroneMissionTarget> findAllBy(Pageable pageable);

    Flux<DroneMissionTarget> findAll();

    Mono<DroneMissionTarget> findById(Long id);

    Mono<DroneMissionTarget> findOneWithEagerRelationships(Long id);

    Flux<DroneMissionTarget> findAllWithEagerRelationships();

    Flux<DroneMissionTarget> findAllWithEagerRelationships(Pageable page);
}
