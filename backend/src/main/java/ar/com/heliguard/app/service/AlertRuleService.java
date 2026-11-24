package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AlertRule}.
 */
public interface AlertRuleService {
    /**
     * Save a alertRule.
     *
     * @param alertRuleDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AlertRuleDTO> save(AlertRuleDTO alertRuleDTO);

    /**
     * Updates a alertRule.
     *
     * @param alertRuleDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AlertRuleDTO> update(AlertRuleDTO alertRuleDTO);

    /**
     * Partially updates a alertRule.
     *
     * @param alertRuleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AlertRuleDTO> partialUpdate(AlertRuleDTO alertRuleDTO);

    /**
     * Get all the alertRules.
     *
     * @return the list of entities.
     */
    Flux<AlertRuleDTO> findAll();

    /**
     * Get all the alertRules with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<AlertRuleDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of alertRules available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" alertRule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AlertRuleDTO> findOne(Long id);

    /**
     * Delete the "id" alertRule.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
