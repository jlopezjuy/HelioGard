package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.ServiceAgreement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ServiceAgreement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceAgreementRepository extends ReactiveCrudRepository<ServiceAgreement, Long>, ServiceAgreementRepositoryInternal {
    @Override
    Mono<ServiceAgreement> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ServiceAgreement> findAllWithEagerRelationships();

    @Override
    Flux<ServiceAgreement> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM service_agreement entity WHERE entity.organization_id = :id")
    Flux<ServiceAgreement> findByOrganization(Long id);

    @Query("SELECT * FROM service_agreement entity WHERE entity.organization_id IS NULL")
    Flux<ServiceAgreement> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM service_agreement entity WHERE entity.provider_organization_id = :id")
    Flux<ServiceAgreement> findByProviderOrganization(Long id);

    @Query("SELECT * FROM service_agreement entity WHERE entity.provider_organization_id IS NULL")
    Flux<ServiceAgreement> findAllWhereProviderOrganizationIsNull();

    @Query("SELECT * FROM service_agreement entity WHERE entity.installation_id = :id")
    Flux<ServiceAgreement> findByInstallation(Long id);

    @Query("SELECT * FROM service_agreement entity WHERE entity.installation_id IS NULL")
    Flux<ServiceAgreement> findAllWhereInstallationIsNull();

    @Override
    <S extends ServiceAgreement> Mono<S> save(S entity);

    @Override
    Flux<ServiceAgreement> findAll();

    @Override
    Mono<ServiceAgreement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ServiceAgreementRepositoryInternal {
    Flux<ServiceAgreement> findAllBy(Pageable pageable);

    Flux<ServiceAgreement> findAll();

    Mono<ServiceAgreement> findById(Long id);

    Mono<ServiceAgreement> findOneWithEagerRelationships(Long id);

    Flux<ServiceAgreement> findAllWithEagerRelationships();

    Flux<ServiceAgreement> findAllWithEagerRelationships(Pageable page);
}
