package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TelemetryIntegrityEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelemetryIntegrityEventRepository
    extends ReactiveCrudRepository<TelemetryIntegrityEvent, Long>, TelemetryIntegrityEventRepositoryInternal
{
    Flux<TelemetryIntegrityEvent> findAllBy(Pageable pageable);

    @Override
    Mono<TelemetryIntegrityEvent> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TelemetryIntegrityEvent> findAllWithEagerRelationships();

    @Override
    Flux<TelemetryIntegrityEvent> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.organization_id = :id")
    Flux<TelemetryIntegrityEvent> findByOrganization(Long id);

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.organization_id IS NULL")
    Flux<TelemetryIntegrityEvent> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.installation_id = :id")
    Flux<TelemetryIntegrityEvent> findByInstallation(Long id);

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.installation_id IS NULL")
    Flux<TelemetryIntegrityEvent> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.device_identity_id = :id")
    Flux<TelemetryIntegrityEvent> findByDeviceIdentity(Long id);

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.device_identity_id IS NULL")
    Flux<TelemetryIntegrityEvent> findAllWhereDeviceIdentityIsNull();

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.telemetry_channel_id = :id")
    Flux<TelemetryIntegrityEvent> findByTelemetryChannel(Long id);

    @Query("SELECT * FROM telemetry_integrity_event entity WHERE entity.telemetry_channel_id IS NULL")
    Flux<TelemetryIntegrityEvent> findAllWhereTelemetryChannelIsNull();

    @Override
    <S extends TelemetryIntegrityEvent> Mono<S> save(S entity);

    @Override
    Flux<TelemetryIntegrityEvent> findAll();

    @Override
    Mono<TelemetryIntegrityEvent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TelemetryIntegrityEventRepositoryInternal {
    Flux<TelemetryIntegrityEvent> findAllBy(Pageable pageable);

    Flux<TelemetryIntegrityEvent> findAll();

    Mono<TelemetryIntegrityEvent> findById(Long id);

    Mono<TelemetryIntegrityEvent> findOneWithEagerRelationships(Long id);

    Flux<TelemetryIntegrityEvent> findAllWithEagerRelationships();

    Flux<TelemetryIntegrityEvent> findAllWithEagerRelationships(Pageable page);
}
