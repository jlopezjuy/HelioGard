package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.InstallationAccessGrant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the InstallationAccessGrant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstallationAccessGrantRepository
    extends ReactiveCrudRepository<InstallationAccessGrant, Long>, InstallationAccessGrantRepositoryInternal
{
    @Override
    Mono<InstallationAccessGrant> findOneWithEagerRelationships(Long id);

    @Override
    Flux<InstallationAccessGrant> findAllWithEagerRelationships();

    @Override
    Flux<InstallationAccessGrant> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.organization_id = :id")
    Flux<InstallationAccessGrant> findByOrganization(Long id);

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.organization_id IS NULL")
    Flux<InstallationAccessGrant> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.installation_id = :id")
    Flux<InstallationAccessGrant> findByInstallation(Long id);

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.installation_id IS NULL")
    Flux<InstallationAccessGrant> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.grantee_organization_id = :id")
    Flux<InstallationAccessGrant> findByGranteeOrganization(Long id);

    @Query("SELECT * FROM installation_access_grant entity WHERE entity.grantee_organization_id IS NULL")
    Flux<InstallationAccessGrant> findAllWhereGranteeOrganizationIsNull();

    @Override
    <S extends InstallationAccessGrant> Mono<S> save(S entity);

    @Override
    Flux<InstallationAccessGrant> findAll();

    @Override
    Mono<InstallationAccessGrant> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InstallationAccessGrantRepositoryInternal {
    Flux<InstallationAccessGrant> findAllBy(Pageable pageable);

    Flux<InstallationAccessGrant> findAll();

    Mono<InstallationAccessGrant> findById(Long id);

    Mono<InstallationAccessGrant> findOneWithEagerRelationships(Long id);

    Flux<InstallationAccessGrant> findAllWithEagerRelationships();

    Flux<InstallationAccessGrant> findAllWithEagerRelationships(Pageable page);
}
