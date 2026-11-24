package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TelemetryChannelBinding;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TelemetryChannelBinding entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelemetryChannelBindingRepository
    extends ReactiveCrudRepository<TelemetryChannelBinding, Long>, TelemetryChannelBindingRepositoryInternal
{
    @Override
    Mono<TelemetryChannelBinding> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TelemetryChannelBinding> findAllWithEagerRelationships();

    @Override
    Flux<TelemetryChannelBinding> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.organization_id = :id")
    Flux<TelemetryChannelBinding> findByOrganization(Long id);

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.organization_id IS NULL")
    Flux<TelemetryChannelBinding> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.telemetry_channel_id = :id")
    Flux<TelemetryChannelBinding> findByTelemetryChannel(Long id);

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.telemetry_channel_id IS NULL")
    Flux<TelemetryChannelBinding> findAllWhereTelemetryChannelIsNull();

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.gateway_id = :id")
    Flux<TelemetryChannelBinding> findByGateway(Long id);

    @Query("SELECT * FROM telemetry_channel_binding entity WHERE entity.gateway_id IS NULL")
    Flux<TelemetryChannelBinding> findAllWhereGatewayIsNull();

    @Override
    <S extends TelemetryChannelBinding> Mono<S> save(S entity);

    @Override
    Flux<TelemetryChannelBinding> findAll();

    @Override
    Mono<TelemetryChannelBinding> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TelemetryChannelBindingRepositoryInternal {
    Flux<TelemetryChannelBinding> findAllBy(Pageable pageable);

    Flux<TelemetryChannelBinding> findAll();

    Mono<TelemetryChannelBinding> findById(Long id);

    Mono<TelemetryChannelBinding> findOneWithEagerRelationships(Long id);

    Flux<TelemetryChannelBinding> findAllWithEagerRelationships();

    Flux<TelemetryChannelBinding> findAllWithEagerRelationships(Pageable page);
}
