package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SecurityIncident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SecurityIncident entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityIncidentRepository extends ReactiveCrudRepository<SecurityIncident, Long>, SecurityIncidentRepositoryInternal {
    Flux<SecurityIncident> findAllBy(Pageable pageable);

    @Override
    Mono<SecurityIncident> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SecurityIncident> findAllWithEagerRelationships();

    @Override
    Flux<SecurityIncident> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM security_incident entity WHERE entity.organization_id = :id")
    Flux<SecurityIncident> findByOrganization(Long id);

    @Query("SELECT * FROM security_incident entity WHERE entity.organization_id IS NULL")
    Flux<SecurityIncident> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM security_incident entity WHERE entity.installation_id = :id")
    Flux<SecurityIncident> findByInstallation(Long id);

    @Query("SELECT * FROM security_incident entity WHERE entity.installation_id IS NULL")
    Flux<SecurityIncident> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM security_incident entity WHERE entity.integrity_assessment_id = :id")
    Flux<SecurityIncident> findByIntegrityAssessment(Long id);

    @Query("SELECT * FROM security_incident entity WHERE entity.integrity_assessment_id IS NULL")
    Flux<SecurityIncident> findAllWhereIntegrityAssessmentIsNull();

    @Override
    <S extends SecurityIncident> Mono<S> save(S entity);

    @Override
    Flux<SecurityIncident> findAll();

    @Override
    Mono<SecurityIncident> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SecurityIncidentRepositoryInternal {
    Flux<SecurityIncident> findAllBy(Pageable pageable);

    Flux<SecurityIncident> findAll();

    Mono<SecurityIncident> findById(Long id);

    Mono<SecurityIncident> findOneWithEagerRelationships(Long id);

    Flux<SecurityIncident> findAllWithEagerRelationships();

    Flux<SecurityIncident> findAllWithEagerRelationships(Pageable page);
}
