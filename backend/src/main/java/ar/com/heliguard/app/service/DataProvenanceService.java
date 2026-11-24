package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.DataProvenance}.
 */
public interface DataProvenanceService {
    /**
     * Save a dataProvenance.
     *
     * @param dataProvenanceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DataProvenanceDTO> save(DataProvenanceDTO dataProvenanceDTO);

    /**
     * Updates a dataProvenance.
     *
     * @param dataProvenanceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DataProvenanceDTO> update(DataProvenanceDTO dataProvenanceDTO);

    /**
     * Partially updates a dataProvenance.
     *
     * @param dataProvenanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DataProvenanceDTO> partialUpdate(DataProvenanceDTO dataProvenanceDTO);

    /**
     * Get all the dataProvenances.
     *
     * @return the list of entities.
     */
    Flux<DataProvenanceDTO> findAll();

    /**
     * Get all the dataProvenances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<DataProvenanceDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of dataProvenances available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" dataProvenance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DataProvenanceDTO> findOne(Long id);

    /**
     * Delete the "id" dataProvenance.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
