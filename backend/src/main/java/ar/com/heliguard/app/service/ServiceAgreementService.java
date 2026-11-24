package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.ServiceAgreement}.
 */
public interface ServiceAgreementService {
    /**
     * Save a serviceAgreement.
     *
     * @param serviceAgreementDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ServiceAgreementDTO> save(ServiceAgreementDTO serviceAgreementDTO);

    /**
     * Updates a serviceAgreement.
     *
     * @param serviceAgreementDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ServiceAgreementDTO> update(ServiceAgreementDTO serviceAgreementDTO);

    /**
     * Partially updates a serviceAgreement.
     *
     * @param serviceAgreementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ServiceAgreementDTO> partialUpdate(ServiceAgreementDTO serviceAgreementDTO);

    /**
     * Get all the serviceAgreements.
     *
     * @return the list of entities.
     */
    Flux<ServiceAgreementDTO> findAll();

    /**
     * Get all the serviceAgreements with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Flux<ServiceAgreementDTO> findAllWithEagerRelationships();

    /**
     * Returns the number of serviceAgreements available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" serviceAgreement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ServiceAgreementDTO> findOne(Long id);

    /**
     * Delete the "id" serviceAgreement.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
