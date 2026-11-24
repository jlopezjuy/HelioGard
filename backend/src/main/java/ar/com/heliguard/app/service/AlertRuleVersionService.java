package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AlertRuleVersion}.
 */
public interface AlertRuleVersionService {
    /**
     * Save a alertRuleVersion.
     *
     * @param alertRuleVersionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AlertRuleVersionDTO> save(AlertRuleVersionDTO alertRuleVersionDTO);

    /**
     * Updates a alertRuleVersion.
     *
     * @param alertRuleVersionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AlertRuleVersionDTO> update(AlertRuleVersionDTO alertRuleVersionDTO);

    /**
     * Partially updates a alertRuleVersion.
     *
     * @param alertRuleVersionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AlertRuleVersionDTO> partialUpdate(AlertRuleVersionDTO alertRuleVersionDTO);

    /**
     * Get all the alertRuleVersions.
     *
     * @return the list of entities.
     */
    Flux<AlertRuleVersionDTO> findAll();

    /**
     * Get all the alertRuleVersions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AlertRuleVersionDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of alertRuleVersions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" alertRuleVersion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AlertRuleVersionDTO> findOne(Long id);

    /**
     * Delete the "id" alertRuleVersion.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
