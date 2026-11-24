package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMissionEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DroneMissionEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroneMissionEventRepository extends ReactiveCrudRepository<DroneMissionEvent, Long>, DroneMissionEventRepositoryInternal {
    @Override
    Mono<DroneMissionEvent> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DroneMissionEvent> findAllWithEagerRelationships();

    @Override
    Flux<DroneMissionEvent> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.organization_id = :id")
    Flux<DroneMissionEvent> findByOrganization(Long id);

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.organization_id IS NULL")
    Flux<DroneMissionEvent> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.mission_id = :id")
    Flux<DroneMissionEvent> findByMission(Long id);

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.mission_id IS NULL")
    Flux<DroneMissionEvent> findAllWhereMissionIsNull();

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.operator_membership_id = :id")
    Flux<DroneMissionEvent> findByOperatorMembership(Long id);

    @Query("SELECT * FROM drone_mission_event entity WHERE entity.operator_membership_id IS NULL")
    Flux<DroneMissionEvent> findAllWhereOperatorMembershipIsNull();

    @Override
    <S extends DroneMissionEvent> Mono<S> save(S entity);

    @Override
    Flux<DroneMissionEvent> findAll();

    @Override
    Mono<DroneMissionEvent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DroneMissionEventRepositoryInternal {
    Flux<DroneMissionEvent> findAllBy(Pageable pageable);

    Flux<DroneMissionEvent> findAll();

    Mono<DroneMissionEvent> findById(Long id);

    Mono<DroneMissionEvent> findOneWithEagerRelationships(Long id);

    Flux<DroneMissionEvent> findAllWithEagerRelationships();

    Flux<DroneMissionEvent> findAllWithEagerRelationships(Pageable page);
}
