package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.InstallationAccessGrant}.
 */
public interface InstallationAccessGrantService {
    /**
     * Save a installationAccessGrant.
     *
     * @param installationAccessGrantDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InstallationAccessGrantDTO> save(InstallationAccessGrantDTO installationAccessGrantDTO);

    /**
     * Updates a installationAccessGrant.
     *
     * @param installationAccessGrantDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InstallationAccessGrantDTO> update(InstallationAccessGrantDTO installationAccessGrantDTO);

    /**
     * Partially updates a installationAccessGrant.
     *
     * @param installationAccessGrantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InstallationAccessGrantDTO> partialUpdate(InstallationAccessGrantDTO installationAccessGrantDTO);

    /**
     * Get all the installationAccessGrants.
     *
     * @return the list of entities.
     */
    Flux<InstallationAccessGrantDTO> findAll();

    /**
     * Get all the installationAccessGrants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<InstallationAccessGrantDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of installationAccessGrants available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" installationAccessGrant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InstallationAccessGrantDTO> findOne(Long id);

    /**
     * Delete the "id" installationAccessGrant.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
