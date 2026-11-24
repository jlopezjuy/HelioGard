package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.TelemetryChannel}.
 */
public interface TelemetryChannelService {
    /**
     * Save a telemetryChannel.
     *
     * @param telemetryChannelDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelDTO> save(TelemetryChannelDTO telemetryChannelDTO);

    /**
     * Updates a telemetryChannel.
     *
     * @param telemetryChannelDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelDTO> update(TelemetryChannelDTO telemetryChannelDTO);

    /**
     * Partially updates a telemetryChannel.
     *
     * @param telemetryChannelDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelDTO> partialUpdate(TelemetryChannelDTO telemetryChannelDTO);

    /**
     * Get all the telemetryChannels.
     *
     * @return the list of entities.
     */
    Flux<TelemetryChannelDTO> findAll();

    /**
     * Get all the telemetryChannels with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<TelemetryChannelDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of telemetryChannels available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" telemetryChannel.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TelemetryChannelDTO> findOne(Long id);

    /**
     * Delete the "id" telemetryChannel.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
