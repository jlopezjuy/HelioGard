package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.DroneMissionEventRepository;
import ar.com.heliguard.app.service.DroneMissionEventService;
import ar.com.heliguard.app.service.dto.DroneMissionEventDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.DroneMissionEvent}.
 */
@RestController
@RequestMapping("/api/drone-mission-events")
public class DroneMissionEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionEventResource.class);

    private static final String ENTITY_NAME = "droneMissionEvent";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final DroneMissionEventService droneMissionEventService;

    private final DroneMissionEventRepository droneMissionEventRepository;

    public DroneMissionEventResource(
        DroneMissionEventService droneMissionEventService,
        DroneMissionEventRepository droneMissionEventRepository
    ) {
        this.droneMissionEventService = droneMissionEventService;
        this.droneMissionEventRepository = droneMissionEventRepository;
    }

    /**
     * {@code POST  /drone-mission-events} : Create a new droneMissionEvent.
     *
     * @param droneMissionEventDTO the droneMissionEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new droneMissionEventDTO, or with status {@code 400 (Bad Request)} if the droneMissionEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DroneMissionEventDTO>> createDroneMissionEvent(@Valid @RequestBody DroneMissionEventDTO droneMissionEventDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DroneMissionEvent : {}", droneMissionEventDTO);
        if (droneMissionEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new droneMissionEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return droneMissionEventService.save(droneMissionEventDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/drone-mission-events/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /drone-mission-events/:id} : Updates an existing droneMissionEvent.
     *
     * @param id the id of the droneMissionEventDTO to save.
     * @param droneMissionEventDTO the droneMissionEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionEventDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionEventDTO>> updateDroneMissionEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DroneMissionEventDTO droneMissionEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DroneMissionEvent : {}, {}", id, droneMissionEventDTO);
        if (droneMissionEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return droneMissionEventService
                .update(droneMissionEventDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /drone-mission-events/:id} : Partial updates given fields of an existing droneMissionEvent, field will ignore if it is null
     *
     * @param id the id of the droneMissionEventDTO to save.
     * @param droneMissionEventDTO the droneMissionEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionEventDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the droneMissionEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DroneMissionEventDTO>> partialUpdateDroneMissionEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DroneMissionEventDTO droneMissionEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DroneMissionEvent : {}, {}", id, droneMissionEventDTO);
        if (droneMissionEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DroneMissionEventDTO> result = droneMissionEventService.partialUpdate(droneMissionEventDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /drone-mission-events} : get all the Drone Mission Events.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Drone Mission Events in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DroneMissionEventDTO>> getAllDroneMissionEvents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all DroneMissionEvents");
        if (eagerload) {
            return droneMissionEventService.findAllWithEagerRelationships().collectList();
        } else {
            return droneMissionEventService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /drone-mission-events} : get all the Drone Mission Events as a stream.
     * @return the {@link Flux} of Drone Mission Events.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DroneMissionEventDTO> getAllDroneMissionEventsAsStream() {
        LOG.debug("REST request to get all DroneMissionEvents as a stream");
        return droneMissionEventService.findAll();
    }

    /**
     * {@code GET  /drone-mission-events/:id} : get the "id" droneMissionEvent.
     *
     * @param id the id of the droneMissionEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the droneMissionEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionEventDTO>> getDroneMissionEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DroneMissionEvent : {}", id);
        Mono<DroneMissionEventDTO> droneMissionEventDTO = droneMissionEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(droneMissionEventDTO);
    }

    /**
     * {@code DELETE  /drone-mission-events/:id} : delete the "id" droneMissionEvent.
     *
     * @param id the id of the droneMissionEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDroneMissionEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DroneMissionEvent : {}", id);
        return droneMissionEventService
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
