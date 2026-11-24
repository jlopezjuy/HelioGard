package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.GatewayDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.Gateway}.
 */
public interface GatewayService {
    /**
     * Save a gateway.
     *
     * @param gatewayDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<GatewayDTO> save(GatewayDTO gatewayDTO);

    /**
     * Updates a gateway.
     *
     * @param gatewayDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<GatewayDTO> update(GatewayDTO gatewayDTO);

    /**
     * Partially updates a gateway.
     *
     * @param gatewayDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<GatewayDTO> partialUpdate(GatewayDTO gatewayDTO);

    /**
     * Get all the gateways.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<GatewayDTO> findAll(Pageable pageable);

    /**
     * Get all the gateways with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<GatewayDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of gateways available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" gateway.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<GatewayDTO> findOne(Long id);

    /**
     * Delete the "id" gateway.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
