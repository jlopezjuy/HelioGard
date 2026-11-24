package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.OrganizationMembership}.
 */
public interface OrganizationMembershipService {
    /**
     * Save a organizationMembership.
     *
     * @param organizationMembershipDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrganizationMembershipDTO> save(OrganizationMembershipDTO organizationMembershipDTO);

    /**
     * Updates a organizationMembership.
     *
     * @param organizationMembershipDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrganizationMembershipDTO> update(OrganizationMembershipDTO organizationMembershipDTO);

    /**
     * Partially updates a organizationMembership.
     *
     * @param organizationMembershipDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrganizationMembershipDTO> partialUpdate(OrganizationMembershipDTO organizationMembershipDTO);

    /**
     * Get all the organizationMemberships.
     *
     * @return the list of entities.
     */
    Flux<OrganizationMembershipDTO> findAll();

    /**
     * Get all the organizationMemberships with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<OrganizationMembershipDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of organizationMemberships available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" organizationMembership.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrganizationMembershipDTO> findOne(Long id);

    /**
     * Delete the "id" organizationMembership.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
