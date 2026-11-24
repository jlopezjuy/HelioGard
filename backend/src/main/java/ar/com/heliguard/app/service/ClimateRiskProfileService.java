package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.ClimateRiskProfile}.
 */
public interface ClimateRiskProfileService {
    /**
     * Save a climateRiskProfile.
     *
     * @param climateRiskProfileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ClimateRiskProfileDTO> save(ClimateRiskProfileDTO climateRiskProfileDTO);

    /**
     * Updates a climateRiskProfile.
     *
     * @param climateRiskProfileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ClimateRiskProfileDTO> update(ClimateRiskProfileDTO climateRiskProfileDTO);

    /**
     * Partially updates a climateRiskProfile.
     *
     * @param climateRiskProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ClimateRiskProfileDTO> partialUpdate(ClimateRiskProfileDTO climateRiskProfileDTO);

    /**
     * Get all the climateRiskProfiles.
     *
     * @return the list of entities.
     */
    Flux<ClimateRiskProfileDTO> findAll();

    /**
     * Returns the number of climateRiskProfiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" climateRiskProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ClimateRiskProfileDTO> findOne(Long id);

    /**
     * Delete the "id" climateRiskProfile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
