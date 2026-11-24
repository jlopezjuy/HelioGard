package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WeatherIngestionBatch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeatherIngestionBatchRepository
    extends ReactiveCrudRepository<WeatherIngestionBatch, Long>, WeatherIngestionBatchRepositoryInternal
{
    @Override
    Mono<WeatherIngestionBatch> findOneWithEagerRelationships(Long id);

    @Override
    Flux<WeatherIngestionBatch> findAllWithEagerRelationships();

    @Override
    Flux<WeatherIngestionBatch> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.organization_id = :id")
    Flux<WeatherIngestionBatch> findByOrganization(Long id);

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.organization_id IS NULL")
    Flux<WeatherIngestionBatch> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.installation_id = :id")
    Flux<WeatherIngestionBatch> findByInstallation(Long id);

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.installation_id IS NULL")
    Flux<WeatherIngestionBatch> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.evidence_source_id = :id")
    Flux<WeatherIngestionBatch> findByEvidenceSource(Long id);

    @Query("SELECT * FROM weather_ingestion_batch entity WHERE entity.evidence_source_id IS NULL")
    Flux<WeatherIngestionBatch> findAllWhereEvidenceSourceIsNull();

    @Override
    <S extends WeatherIngestionBatch> Mono<S> save(S entity);

    @Override
    Flux<WeatherIngestionBatch> findAll();

    @Override
    Mono<WeatherIngestionBatch> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WeatherIngestionBatchRepositoryInternal {
    Flux<WeatherIngestionBatch> findAllBy(Pageable pageable);

    Flux<WeatherIngestionBatch> findAll();

    Mono<WeatherIngestionBatch> findById(Long id);

    Mono<WeatherIngestionBatch> findOneWithEagerRelationships(Long id);

    Flux<WeatherIngestionBatch> findAllWithEagerRelationships();

    Flux<WeatherIngestionBatch> findAllWithEagerRelationships(Pageable page);
}
