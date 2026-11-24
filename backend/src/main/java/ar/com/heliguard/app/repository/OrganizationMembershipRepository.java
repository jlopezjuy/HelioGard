package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.OrganizationMembership;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OrganizationMembership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationMembershipRepository
    extends ReactiveCrudRepository<OrganizationMembership, Long>, OrganizationMembershipRepositoryInternal
{
    @Override
    Mono<OrganizationMembership> findOneWithEagerRelationships(Long id);

    @Override
    Flux<OrganizationMembership> findAllWithEagerRelationships();

    @Override
    Flux<OrganizationMembership> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM organization_membership entity WHERE entity.organization_id = :id")
    Flux<OrganizationMembership> findByOrganization(Long id);

    @Query("SELECT * FROM organization_membership entity WHERE entity.organization_id IS NULL")
    Flux<OrganizationMembership> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM organization_membership entity WHERE entity.member_id = :id")
    Flux<OrganizationMembership> findByMember(Long id);

    @Query("SELECT * FROM organization_membership entity WHERE entity.member_id IS NULL")
    Flux<OrganizationMembership> findAllWhereMemberIsNull();

    @Override
    <S extends OrganizationMembership> Mono<S> save(S entity);

    @Override
    Flux<OrganizationMembership> findAll();

    @Override
    Mono<OrganizationMembership> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrganizationMembershipRepositoryInternal {
    Flux<OrganizationMembership> findAllBy(Pageable pageable);

    Flux<OrganizationMembership> findAll();

    Mono<OrganizationMembership> findById(Long id);

    Mono<OrganizationMembership> findOneWithEagerRelationships(Long id);

    Flux<OrganizationMembership> findAllWithEagerRelationships();

    Flux<OrganizationMembership> findAllWithEagerRelationships(Pageable page);
}
