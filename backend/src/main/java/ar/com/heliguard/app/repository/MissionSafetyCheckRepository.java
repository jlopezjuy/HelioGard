package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MissionSafetyCheck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MissionSafetyCheck entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionSafetyCheckRepository
    extends ReactiveCrudRepository<MissionSafetyCheck, Long>, MissionSafetyCheckRepositoryInternal
{
    @Override
    Mono<MissionSafetyCheck> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MissionSafetyCheck> findAllWithEagerRelationships();

    @Override
    Flux<MissionSafetyCheck> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.organization_id = :id")
    Flux<MissionSafetyCheck> findByOrganization(Long id);

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.organization_id IS NULL")
    Flux<MissionSafetyCheck> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.mission_id = :id")
    Flux<MissionSafetyCheck> findByMission(Long id);

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.mission_id IS NULL")
    Flux<MissionSafetyCheck> findAllWhereMissionIsNull();

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.checked_by_membership_id = :id")
    Flux<MissionSafetyCheck> findByCheckedByMembership(Long id);

    @Query("SELECT * FROM mission_safety_check entity WHERE entity.checked_by_membership_id IS NULL")
    Flux<MissionSafetyCheck> findAllWhereCheckedByMembershipIsNull();

    @Override
    <S extends MissionSafetyCheck> Mono<S> save(S entity);

    @Override
    Flux<MissionSafetyCheck> findAll();

    @Override
    Mono<MissionSafetyCheck> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MissionSafetyCheckRepositoryInternal {
    Flux<MissionSafetyCheck> findAllBy(Pageable pageable);

    Flux<MissionSafetyCheck> findAll();

    Mono<MissionSafetyCheck> findById(Long id);

    Mono<MissionSafetyCheck> findOneWithEagerRelationships(Long id);

    Flux<MissionSafetyCheck> findAllWithEagerRelationships();

    Flux<MissionSafetyCheck> findAllWithEagerRelationships(Pageable page);
}
