package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MissionAuthorization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MissionAuthorization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionAuthorizationRepository
    extends ReactiveCrudRepository<MissionAuthorization, Long>, MissionAuthorizationRepositoryInternal
{
    @Override
    Mono<MissionAuthorization> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MissionAuthorization> findAllWithEagerRelationships();

    @Override
    Flux<MissionAuthorization> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mission_authorization entity WHERE entity.organization_id = :id")
    Flux<MissionAuthorization> findByOrganization(Long id);

    @Query("SELECT * FROM mission_authorization entity WHERE entity.organization_id IS NULL")
    Flux<MissionAuthorization> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM mission_authorization entity WHERE entity.mission_id = :id")
    Flux<MissionAuthorization> findByMission(Long id);

    @Query("SELECT * FROM mission_authorization entity WHERE entity.mission_id IS NULL")
    Flux<MissionAuthorization> findAllWhereMissionIsNull();

    @Query("SELECT * FROM mission_authorization entity WHERE entity.authorized_by_membership_id = :id")
    Flux<MissionAuthorization> findByAuthorizedByMembership(Long id);

    @Query("SELECT * FROM mission_authorization entity WHERE entity.authorized_by_membership_id IS NULL")
    Flux<MissionAuthorization> findAllWhereAuthorizedByMembershipIsNull();

    @Override
    <S extends MissionAuthorization> Mono<S> save(S entity);

    @Override
    Flux<MissionAuthorization> findAll();

    @Override
    Mono<MissionAuthorization> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MissionAuthorizationRepositoryInternal {
    Flux<MissionAuthorization> findAllBy(Pageable pageable);

    Flux<MissionAuthorization> findAll();

    Mono<MissionAuthorization> findById(Long id);

    Mono<MissionAuthorization> findOneWithEagerRelationships(Long id);

    Flux<MissionAuthorization> findAllWithEagerRelationships();

    Flux<MissionAuthorization> findAllWithEagerRelationships(Pageable page);
}
