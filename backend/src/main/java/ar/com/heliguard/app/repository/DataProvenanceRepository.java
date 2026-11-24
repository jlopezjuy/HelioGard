package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DataProvenance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DataProvenance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataProvenanceRepository extends ReactiveCrudRepository<DataProvenance, Long>, DataProvenanceRepositoryInternal {
    @Override
    Mono<DataProvenance> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DataProvenance> findAllWithEagerRelationships();

    @Override
    Flux<DataProvenance> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM data_provenance entity WHERE entity.organization_id = :id")
    Flux<DataProvenance> findByOrganization(Long id);

    @Query("SELECT * FROM data_provenance entity WHERE entity.organization_id IS NULL")
    Flux<DataProvenance> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM data_provenance entity WHERE entity.installation_id = :id")
    Flux<DataProvenance> findByInstallation(Long id);

    @Query("SELECT * FROM data_provenance entity WHERE entity.installation_id IS NULL")
    Flux<DataProvenance> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM data_provenance entity WHERE entity.evidence_source_id = :id")
    Flux<DataProvenance> findByEvidenceSource(Long id);

    @Query("SELECT * FROM data_provenance entity WHERE entity.evidence_source_id IS NULL")
    Flux<DataProvenance> findAllWhereEvidenceSourceIsNull();

    @Query("SELECT * FROM data_provenance entity WHERE entity.model_run_id = :id")
    Flux<DataProvenance> findByModelRun(Long id);

    @Query("SELECT * FROM data_provenance entity WHERE entity.model_run_id IS NULL")
    Flux<DataProvenance> findAllWhereModelRunIsNull();

    @Override
    <S extends DataProvenance> Mono<S> save(S entity);

    @Override
    Flux<DataProvenance> findAll();

    @Override
    Mono<DataProvenance> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DataProvenanceRepositoryInternal {
    Flux<DataProvenance> findAllBy(Pageable pageable);

    Flux<DataProvenance> findAll();

    Mono<DataProvenance> findById(Long id);

    Mono<DataProvenance> findOneWithEagerRelationships(Long id);

    Flux<DataProvenance> findAllWithEagerRelationships();

    Flux<DataProvenance> findAllWithEagerRelationships(Pageable page);
}
