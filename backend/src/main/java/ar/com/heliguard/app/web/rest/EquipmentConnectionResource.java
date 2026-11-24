package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.EquipmentConnectionRepository;
import ar.com.heliguard.app.service.EquipmentConnectionService;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link ar.com.heliguard.app.domain.EquipmentConnection}.
 */
@RestController
@RequestMapping("/api/equipment-connections")
public class EquipmentConnectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentConnectionResource.class);

    private static final String ENTITY_NAME = "equipmentConnection";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final EquipmentConnectionService equipmentConnectionService;

    private final EquipmentConnectionRepository equipmentConnectionRepository;

    public EquipmentConnectionResource(
        EquipmentConnectionService equipmentConnectionService,
        EquipmentConnectionRepository equipmentConnectionRepository
    ) {
        this.equipmentConnectionService = equipmentConnectionService;
        this.equipmentConnectionRepository = equipmentConnectionRepository;
    }

    /**
     * {@code POST  /equipment-connections} : Create a new equipmentConnection.
     *
     * @param equipmentConnectionDTO the equipmentConnectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentConnectionDTO, or with status {@code 400 (Bad Request)} if the equipmentConnection already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EquipmentConnectionDTO>> createEquipmentConnection(
        @Valid @RequestBody EquipmentConnectionDTO equipmentConnectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save EquipmentConnection : {}", equipmentConnectionDTO);
        if (equipmentConnectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentConnection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return equipmentConnectionService.save(equipmentConnectionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/equipment-connections/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /equipment-connections/:id} : Updates an existing equipmentConnection.
     *
     * @param id the id of the equipmentConnectionDTO to save.
     * @param equipmentConnectionDTO the equipmentConnectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentConnectionDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentConnectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentConnectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EquipmentConnectionDTO>> updateEquipmentConnection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EquipmentConnectionDTO equipmentConnectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EquipmentConnection : {}, {}", id, equipmentConnectionDTO);
        if (equipmentConnectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentConnectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipmentConnectionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return equipmentConnectionService
                .update(equipmentConnectionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /equipment-connections/:id} : Partial updates given fields of an existing equipmentConnection, field will ignore if it is null
     *
     * @param id the id of the equipmentConnectionDTO to save.
     * @param equipmentConnectionDTO the equipmentConnectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentConnectionDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentConnectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the equipmentConnectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the equipmentConnectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EquipmentConnectionDTO>> partialUpdateEquipmentConnection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EquipmentConnectionDTO equipmentConnectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update EquipmentConnection : {}, {}", id, equipmentConnectionDTO);
        if (equipmentConnectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentConnectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipmentConnectionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<EquipmentConnectionDTO> result = equipmentConnectionService.partialUpdate(equipmentConnectionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /equipment-connections} : get all the Equipment Connections.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Equipment Connections in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<EquipmentConnectionDTO>> getAllEquipmentConnections(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EquipmentConnections");
        if (eagerload) {
            return equipmentConnectionService.findAllWithEagerRelationships().collectList();
        } else {
            return equipmentConnectionService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /equipment-connections} : get all the Equipment Connections as a stream.
     * @return the {@link Flux} of Equipment Connections.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EquipmentConnectionDTO> getAllEquipmentConnectionsAsStream() {
        LOG.debug("REST request to get all EquipmentConnections as a stream");
        return equipmentConnectionService.findAll();
    }

    /**
     * {@code GET  /equipment-connections/:id} : get the "id" equipmentConnection.
     *
     * @param id the id of the equipmentConnectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentConnectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EquipmentConnectionDTO>> getEquipmentConnection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EquipmentConnection : {}", id);
        Mono<EquipmentConnectionDTO> equipmentConnectionDTO = equipmentConnectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentConnectionDTO);
    }

    /**
     * {@code DELETE  /equipment-connections/:id} : delete the "id" equipmentConnection.
     *
     * @param id the id of the equipmentConnectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEquipmentConnection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EquipmentConnection : {}", id);
        return equipmentConnectionService
            .delete(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
