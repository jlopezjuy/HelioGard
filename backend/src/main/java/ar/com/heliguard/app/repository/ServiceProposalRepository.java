package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.ServiceProposal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ServiceProposal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceProposalRepository extends ReactiveCrudRepository<ServiceProposal, Long>, ServiceProposalRepositoryInternal {
    Flux<ServiceProposal> findAllBy(Pageable pageable);

    @Override
    Mono<ServiceProposal> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ServiceProposal> findAllWithEagerRelationships();

    @Override
    Flux<ServiceProposal> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM service_proposal entity WHERE entity.organization_id = :id")
    Flux<ServiceProposal> findByOrganization(Long id);

    @Query("SELECT * FROM service_proposal entity WHERE entity.organization_id = :orgId ORDER BY entity.id DESC LIMIT :limit OFFSET :offset")
    Flux<ServiceProposal> findPageByOrganization(Long orgId, int limit, long offset);

    @Query("SELECT COUNT(*) FROM service_proposal entity WHERE entity.organization_id = :orgId")
    Mono<Long> countByOrganization(Long orgId);

    @Query("SELECT * FROM service_proposal entity WHERE entity.organization_id IS NULL")
    Flux<ServiceProposal> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM service_proposal entity WHERE entity.installation_id = :id")
    Flux<ServiceProposal> findByInstallation(Long id);

    @Query("SELECT * FROM service_proposal entity WHERE entity.installation_id IS NULL")
    Flux<ServiceProposal> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM service_proposal entity WHERE entity.plan_version_id = :id")
    Flux<ServiceProposal> findByPlanVersion(Long id);

    @Query("SELECT * FROM service_proposal entity WHERE entity.plan_version_id IS NULL")
    Flux<ServiceProposal> findAllWherePlanVersionIsNull();

    @Override
    <S extends ServiceProposal> Mono<S> save(S entity);

    @Override
    Flux<ServiceProposal> findAll();

    @Override
    Mono<ServiceProposal> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ServiceProposalRepositoryInternal {
    Flux<ServiceProposal> findAllBy(Pageable pageable);

    Flux<ServiceProposal> findAll();

    Mono<ServiceProposal> findById(Long id);

    Mono<ServiceProposal> findOneWithEagerRelationships(Long id);

    Flux<ServiceProposal> findAllWithEagerRelationships();

    Flux<ServiceProposal> findAllWithEagerRelationships(Pageable page);
}
