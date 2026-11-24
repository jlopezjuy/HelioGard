package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.TelemetryChannelBinding}.
 */
public interface TelemetryChannelBindingService {
    /**
     * Save a telemetryChannelBinding.
     *
     * @param telemetryChannelBindingDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelBindingDTO> save(TelemetryChannelBindingDTO telemetryChannelBindingDTO);

    /**
     * Updates a telemetryChannelBinding.
     *
     * @param telemetryChannelBindingDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelBindingDTO> update(TelemetryChannelBindingDTO telemetryChannelBindingDTO);

    /**
     * Partially updates a telemetryChannelBinding.
     *
     * @param telemetryChannelBindingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TelemetryChannelBindingDTO> partialUpdate(TelemetryChannelBindingDTO telemetryChannelBindingDTO);

    /**
     * Get all the telemetryChannelBindings.
     *
     * @return the list of entities.
     */
    Flux<TelemetryChannelBindingDTO> findAll();

    /**
     * Get all the telemetryChannelBindings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<TelemetryChannelBindingDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of telemetryChannelBindings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" telemetryChannelBinding.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TelemetryChannelBindingDTO> findOne(Long id);

    /**
     * Delete the "id" telemetryChannelBinding.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
