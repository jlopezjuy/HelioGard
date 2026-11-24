package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.SiteDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.Site}.
 */
public interface SiteService {
    /**
     * Save a site.
     *
     * @param siteDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SiteDTO> save(SiteDTO siteDTO);

    /**
     * Updates a site.
     *
     * @param siteDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SiteDTO> update(SiteDTO siteDTO);

    /**
     * Partially updates a site.
     *
     * @param siteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SiteDTO> partialUpdate(SiteDTO siteDTO);

    /**
     * Get all the sites.
     *
     * @return the list of entities.
     */
    Flux<SiteDTO> findAll();

    /**
     * Get all the sites with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<SiteDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of sites available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" site.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SiteDTO> findOne(Long id);

    /**
     * Delete the "id" site.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
