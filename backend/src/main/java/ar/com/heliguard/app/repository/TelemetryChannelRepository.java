package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TelemetryChannel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TelemetryChannel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelemetryChannelRepository extends ReactiveCrudRepository<TelemetryChannel, Long>, TelemetryChannelRepositoryInternal {
    @Override
    Mono<TelemetryChannel> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TelemetryChannel> findAllWithEagerRelationships();

    @Override
    Flux<TelemetryChannel> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.organization_id = :id")
    Flux<TelemetryChannel> findByOrganization(Long id);

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.organization_id IS NULL")
    Flux<TelemetryChannel> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.equipment_id = :id")
    Flux<TelemetryChannel> findByEquipment(Long id);

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.equipment_id IS NULL")
    Flux<TelemetryChannel> findAllWhereEquipmentIsNull();

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.metric_definition_id = :id")
    Flux<TelemetryChannel> findByMetricDefinition(Long id);

    @Query("SELECT * FROM telemetry_channel entity WHERE entity.metric_definition_id IS NULL")
    Flux<TelemetryChannel> findAllWhereMetricDefinitionIsNull();

    @Override
    <S extends TelemetryChannel> Mono<S> save(S entity);

    @Override
    Flux<TelemetryChannel> findAll();

    @Override
    Mono<TelemetryChannel> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TelemetryChannelRepositoryInternal {
    Flux<TelemetryChannel> findAllBy(Pageable pageable);

    Flux<TelemetryChannel> findAll();

    Mono<TelemetryChannel> findById(Long id);

    Mono<TelemetryChannel> findOneWithEagerRelationships(Long id);

    Flux<TelemetryChannel> findAllWithEagerRelationships();

    Flux<TelemetryChannel> findAllWithEagerRelationships(Pageable page);
}
