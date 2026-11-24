package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.VerificationResultDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.VerificationResult}.
 */
public interface VerificationResultService {
    /**
     * Save a verificationResult.
     *
     * @param verificationResultDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<VerificationResultDTO> save(VerificationResultDTO verificationResultDTO);

    /**
     * Updates a verificationResult.
     *
     * @param verificationResultDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<VerificationResultDTO> update(VerificationResultDTO verificationResultDTO);

    /**
     * Partially updates a verificationResult.
     *
     * @param verificationResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<VerificationResultDTO> partialUpdate(VerificationResultDTO verificationResultDTO);

    /**
     * Get all the verificationResults.
     *
     * @return the list of entities.
     */
    Flux<VerificationResultDTO> findAll();

    /**
     * Get all the verificationResults with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<VerificationResultDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of verificationResults available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" verificationResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<VerificationResultDTO> findOne(Long id);

    /**
     * Delete the "id" verificationResult.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
