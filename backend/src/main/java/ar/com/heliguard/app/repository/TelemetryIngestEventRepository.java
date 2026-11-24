package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.TelemetryIngestEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for {@link TelemetryIngestEvent}.
 */
@Repository
public interface TelemetryIngestEventRepository extends ReactiveCrudRepository<TelemetryIngestEvent, Long> {
    Mono<TelemetryIngestEvent> findByGatewayIdAndEventId(Long gatewayId, String eventId);

    Flux<TelemetryIngestEvent> findByOrganizationId(Long organizationId);

    Mono<Long> countByGatewayId(Long gatewayId);
}
