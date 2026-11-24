package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.TelemetryIntegrityEvent}.
 */
public interface TelemetryIntegrityEventService {
    /**
     * Save a telemetryIntegrityEvent.
     *
     * @param telemetryIntegrityEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TelemetryIntegrityEventDTO> save(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO);

    /**
     * Updates a telemetryIntegrityEvent.
     *
     * @param telemetryIntegrityEventDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TelemetryIntegrityEventDTO> update(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO);

    /**
     * Partially updates a telemetryIntegrityEvent.
     *
     * @param telemetryIntegrityEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TelemetryIntegrityEventDTO> partialUpdate(TelemetryIntegrityEventDTO telemetryIntegrityEventDTO);

    /**
     * Get all the telemetryIntegrityEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TelemetryIntegrityEventDTO> findAll(Pageable pageable);

    /**
     * Get all the telemetryIntegrityEvents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TelemetryIntegrityEventDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of telemetryIntegrityEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" telemetryIntegrityEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TelemetryIntegrityEventDTO> findOne(Long id);

    /**
     * Delete the "id" telemetryIntegrityEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
