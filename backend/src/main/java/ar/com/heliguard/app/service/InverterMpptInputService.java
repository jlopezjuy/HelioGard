package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.InverterMpptInput}.
 */
public interface InverterMpptInputService {
    /**
     * Save a inverterMpptInput.
     *
     * @param inverterMpptInputDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InverterMpptInputDTO> save(InverterMpptInputDTO inverterMpptInputDTO);

    /**
     * Updates a inverterMpptInput.
     *
     * @param inverterMpptInputDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InverterMpptInputDTO> update(InverterMpptInputDTO inverterMpptInputDTO);

    /**
     * Partially updates a inverterMpptInput.
     *
     * @param inverterMpptInputDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InverterMpptInputDTO> partialUpdate(InverterMpptInputDTO inverterMpptInputDTO);

    /**
     * Get all the inverterMpptInputs.
     *
     * @return the list of entities.
     */
    Flux<InverterMpptInputDTO> findAll();

    /**
     * Get all the inverterMpptInputs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<InverterMpptInputDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of inverterMpptInputs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" inverterMpptInput.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InverterMpptInputDTO> findOne(Long id);

    /**
     * Delete the "id" inverterMpptInput.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
