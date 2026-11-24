package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.ServiceProposalDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.ServiceProposal}.
 */
public interface ServiceProposalService {
    /**
     * Save a serviceProposal.
     *
     * @param serviceProposalDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ServiceProposalDTO> save(ServiceProposalDTO serviceProposalDTO);

    /**
     * Updates a serviceProposal.
     *
     * @param serviceProposalDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ServiceProposalDTO> update(ServiceProposalDTO serviceProposalDTO);

    /**
     * Partially updates a serviceProposal.
     *
     * @param serviceProposalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ServiceProposalDTO> partialUpdate(ServiceProposalDTO serviceProposalDTO);

    /**
     * Get all the serviceProposals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ServiceProposalDTO> findAll(Pageable pageable);

    /**
     * Get all the serviceProposals with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ServiceProposalDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of serviceProposals available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" serviceProposal.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ServiceProposalDTO> findOne(Long id);

    /**
     * Delete the "id" serviceProposal.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
