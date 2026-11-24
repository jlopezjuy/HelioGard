package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EvidenceSource}.
 */
public interface EvidenceSourceService {
    /**
     * Save a evidenceSource.
     *
     * @param evidenceSourceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EvidenceSourceDTO> save(EvidenceSourceDTO evidenceSourceDTO);

    /**
     * Updates a evidenceSource.
     *
     * @param evidenceSourceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EvidenceSourceDTO> update(EvidenceSourceDTO evidenceSourceDTO);

    /**
     * Partially updates a evidenceSource.
     *
     * @param evidenceSourceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EvidenceSourceDTO> partialUpdate(EvidenceSourceDTO evidenceSourceDTO);

    /**
     * Get all the evidenceSources.
     *
     * @return the list of entities.
     */
    Flux<EvidenceSourceDTO> findAll();

    /**
     * Returns the number of evidenceSources available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" evidenceSource.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EvidenceSourceDTO> findOne(Long id);

    /**
     * Delete the "id" evidenceSource.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
