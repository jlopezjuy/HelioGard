package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvent}.
 */
public interface SecurityCaseEventService {
    /**
     * Save a securityCaseEvent.
     *
     * @param securityCaseEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEventDTO> save(SecurityCaseEventDTO securityCaseEventDTO);

    /**
     * Updates a securityCaseEvent.
     *
     * @param securityCaseEventDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEventDTO> update(SecurityCaseEventDTO securityCaseEventDTO);

    /**
     * Partially updates a securityCaseEvent.
     *
     * @param securityCaseEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SecurityCaseEventDTO> partialUpdate(SecurityCaseEventDTO securityCaseEventDTO);

    /**
     * Get all the securityCaseEvents.
     *
     * @return the list of entities.
     */
    Flux<SecurityCaseEventDTO> findAll();

    /**
     * Get all the securityCaseEvents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SecurityCaseEventDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of securityCaseEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" securityCaseEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SecurityCaseEventDTO> findOne(Long id);

    /**
     * Delete the "id" securityCaseEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
