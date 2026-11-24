package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DeviceSequenceCheckpoint;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for {@link DeviceSequenceCheckpoint}.
 */
@Repository
public interface DeviceSequenceCheckpointRepository extends ReactiveCrudRepository<DeviceSequenceCheckpoint, Long> {
    Mono<DeviceSequenceCheckpoint> findByGatewayIdAndDeviceIdentityId(Long gatewayId, Long deviceIdentityId);
}
