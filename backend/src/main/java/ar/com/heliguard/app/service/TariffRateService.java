package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.TariffRateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.TariffRate}.
 */
public interface TariffRateService {
    /**
     * Save a tariffRate.
     *
     * @param tariffRateDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TariffRateDTO> save(TariffRateDTO tariffRateDTO);

    /**
     * Updates a tariffRate.
     *
     * @param tariffRateDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TariffRateDTO> update(TariffRateDTO tariffRateDTO);

    /**
     * Partially updates a tariffRate.
     *
     * @param tariffRateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TariffRateDTO> partialUpdate(TariffRateDTO tariffRateDTO);

    /**
     * Get all the tariffRates.
     *
     * @return the list of entities.
     */
    Flux<TariffRateDTO> findAll();

    /**
     * Get all the tariffRates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<TariffRateDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of tariffRates available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tariffRate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TariffRateDTO> findOne(Long id);

    /**
     * Delete the "id" tariffRate.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
