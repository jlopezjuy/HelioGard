package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.EnergyModelConfiguration}.
 */
public interface EnergyModelConfigurationService {
    /**
     * Save a energyModelConfiguration.
     *
     * @param energyModelConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EnergyModelConfigurationDTO> save(EnergyModelConfigurationDTO energyModelConfigurationDTO);

    /**
     * Updates a energyModelConfiguration.
     *
     * @param energyModelConfigurationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EnergyModelConfigurationDTO> update(EnergyModelConfigurationDTO energyModelConfigurationDTO);

    /**
     * Partially updates a energyModelConfiguration.
     *
     * @param energyModelConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EnergyModelConfigurationDTO> partialUpdate(EnergyModelConfigurationDTO energyModelConfigurationDTO);

    /**
     * Get all the energyModelConfigurations.
     *
     * @return the list of entities.
     */
    Flux<EnergyModelConfigurationDTO> findAll();

    /**
     * Get all the energyModelConfigurations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<EnergyModelConfigurationDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of energyModelConfigurations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" energyModelConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EnergyModelConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" energyModelConfiguration.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
