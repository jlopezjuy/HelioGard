package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.InstallationDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.Installation}.
 */
public interface InstallationService {
    /**
     * Save a installation.
     *
     * @param installationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InstallationDTO> save(InstallationDTO installationDTO);

    /**
     * Updates a installation.
     *
     * @param installationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InstallationDTO> update(InstallationDTO installationDTO);

    /**
     * Partially updates a installation.
     *
     * @param installationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InstallationDTO> partialUpdate(InstallationDTO installationDTO);

    /**
     * Get all the installations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstallationDTO> findAll(Pageable pageable);

    /**
     * Get all the installations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstallationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of installations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" installation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InstallationDTO> findOne(Long id);

    /**
     * Delete the "id" installation.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
