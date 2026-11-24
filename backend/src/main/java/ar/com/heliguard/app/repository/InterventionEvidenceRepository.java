package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.InterventionEvidence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the InterventionEvidence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InterventionEvidenceRepository
    extends ReactiveCrudRepository<InterventionEvidence, Long>, InterventionEvidenceRepositoryInternal
{
    @Override
    Mono<InterventionEvidence> findOneWithEagerRelationships(Long id);

    @Override
    Flux<InterventionEvidence> findAllWithEagerRelationships();

    @Override
    Flux<InterventionEvidence> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM intervention_evidence entity WHERE entity.organization_id = :id")
    Flux<InterventionEvidence> findByOrganization(Long id);

    @Query("SELECT * FROM intervention_evidence entity WHERE entity.organization_id IS NULL")
    Flux<InterventionEvidence> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM intervention_evidence entity WHERE entity.intervention_id = :id")
    Flux<InterventionEvidence> findByIntervention(Long id);

    @Query("SELECT * FROM intervention_evidence entity WHERE entity.intervention_id IS NULL")
    Flux<InterventionEvidence> findAllWhereInterventionIsNull();

    @Override
    <S extends InterventionEvidence> Mono<S> save(S entity);

    @Override
    Flux<InterventionEvidence> findAll();

    @Override
    Mono<InterventionEvidence> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InterventionEvidenceRepositoryInternal {
    Flux<InterventionEvidence> findAllBy(Pageable pageable);

    Flux<InterventionEvidence> findAll();

    Mono<InterventionEvidence> findById(Long id);

    Mono<InterventionEvidence> findOneWithEagerRelationships(Long id);

    Flux<InterventionEvidence> findAllWithEagerRelationships();

    Flux<InterventionEvidence> findAllWithEagerRelationships(Pageable page);
}
