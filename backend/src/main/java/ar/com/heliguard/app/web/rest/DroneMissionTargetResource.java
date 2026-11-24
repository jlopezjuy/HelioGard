package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.DroneMissionTargetRepository;
import ar.com.heliguard.app.service.DroneMissionTargetService;
import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.DroneMissionTarget}.
 */
@RestController
@RequestMapping("/api/drone-mission-targets")
public class DroneMissionTargetResource {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionTargetResource.class);

    private static final String ENTITY_NAME = "droneMissionTarget";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final DroneMissionTargetService droneMissionTargetService;

    private final DroneMissionTargetRepository droneMissionTargetRepository;

    public DroneMissionTargetResource(
        DroneMissionTargetService droneMissionTargetService,
        DroneMissionTargetRepository droneMissionTargetRepository
    ) {
        this.droneMissionTargetService = droneMissionTargetService;
        this.droneMissionTargetRepository = droneMissionTargetRepository;
    }

    /**
     * {@code POST  /drone-mission-targets} : Create a new droneMissionTarget.
     *
     * @param droneMissionTargetDTO the droneMissionTargetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new droneMissionTargetDTO, or with status {@code 400 (Bad Request)} if the droneMissionTarget already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DroneMissionTargetDTO>> createDroneMissionTarget(
        @Valid @RequestBody DroneMissionTargetDTO droneMissionTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save DroneMissionTarget : {}", droneMissionTargetDTO);
        if (droneMissionTargetDTO.getId() != null) {
            throw new BadRequestAlertException("A new droneMissionTarget cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return droneMissionTargetService.save(droneMissionTargetDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/drone-mission-targets/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /drone-mission-targets/:id} : Updates an existing droneMissionTarget.
     *
     * @param id the id of the droneMissionTargetDTO to save.
     * @param droneMissionTargetDTO the droneMissionTargetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionTargetDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionTargetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionTargetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionTargetDTO>> updateDroneMissionTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DroneMissionTargetDTO droneMissionTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DroneMissionTarget : {}, {}", id, droneMissionTargetDTO);
        if (droneMissionTargetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionTargetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionTargetRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return droneMissionTargetService
                .update(droneMissionTargetDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /drone-mission-targets/:id} : Partial updates given fields of an existing droneMissionTarget, field will ignore if it is null
     *
     * @param id the id of the droneMissionTargetDTO to save.
     * @param droneMissionTargetDTO the droneMissionTargetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionTargetDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionTargetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the droneMissionTargetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionTargetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DroneMissionTargetDTO>> partialUpdateDroneMissionTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DroneMissionTargetDTO droneMissionTargetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DroneMissionTarget : {}, {}", id, droneMissionTargetDTO);
        if (droneMissionTargetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionTargetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionTargetRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DroneMissionTargetDTO> result = droneMissionTargetService.partialUpdate(droneMissionTargetDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /drone-mission-targets} : get all the Drone Mission Targets.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Drone Mission Targets in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DroneMissionTargetDTO>> getAllDroneMissionTargets(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all DroneMissionTargets");
        if (eagerload) {
            return droneMissionTargetService.findAllWithEagerRelationships().collectList();
        } else {
            return droneMissionTargetService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /drone-mission-targets} : get all the Drone Mission Targets as a stream.
     * @return the {@link Flux} of Drone Mission Targets.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DroneMissionTargetDTO> getAllDroneMissionTargetsAsStream() {
        LOG.debug("REST request to get all DroneMissionTargets as a stream");
        return droneMissionTargetService.findAll();
    }

    /**
     * {@code GET  /drone-mission-targets/:id} : get the "id" droneMissionTarget.
     *
     * @param id the id of the droneMissionTargetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the droneMissionTargetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionTargetDTO>> getDroneMissionTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DroneMissionTarget : {}", id);
        Mono<DroneMissionTargetDTO> droneMissionTargetDTO = droneMissionTargetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(droneMissionTargetDTO);
    }

    /**
     * {@code DELETE  /drone-mission-targets/:id} : delete the "id" droneMissionTarget.
     *
     * @param id the id of the droneMissionTargetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDroneMissionTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DroneMissionTarget : {}", id);
        return droneMissionTargetService
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
