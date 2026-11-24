package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.service.MaintenancePlanService;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MaintenancePlan}.
 */
@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanResource {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenancePlanResource.class);

    private static final String ENTITY_NAME = "maintenancePlan";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MaintenancePlanService maintenancePlanService;

    private final MaintenancePlanRepository maintenancePlanRepository;

    public MaintenancePlanResource(MaintenancePlanService maintenancePlanService, MaintenancePlanRepository maintenancePlanRepository) {
        this.maintenancePlanService = maintenancePlanService;
        this.maintenancePlanRepository = maintenancePlanRepository;
    }

    /**
     * {@code POST  /maintenance-plans} : Create a new maintenancePlan.
     *
     * @param maintenancePlanDTO the maintenancePlanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maintenancePlanDTO, or with status {@code 400 (Bad Request)} if the maintenancePlan already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MaintenancePlanDTO>> createMaintenancePlan(@Valid @RequestBody MaintenancePlanDTO maintenancePlanDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MaintenancePlan : {}", maintenancePlanDTO);
        if (maintenancePlanDTO.getId() != null) {
            throw new BadRequestAlertException("A new maintenancePlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return maintenancePlanService.save(maintenancePlanDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/maintenance-plans/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /maintenance-plans/:id} : Updates an existing maintenancePlan.
     *
     * @param id the id of the maintenancePlanDTO to save.
     * @param maintenancePlanDTO the maintenancePlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenancePlanDTO,
     * or with status {@code 400 (Bad Request)} if the maintenancePlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maintenancePlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MaintenancePlanDTO>> updateMaintenancePlan(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MaintenancePlanDTO maintenancePlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MaintenancePlan : {}, {}", id, maintenancePlanDTO);
        if (maintenancePlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenancePlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenancePlanRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return maintenancePlanService
                .update(maintenancePlanDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /maintenance-plans/:id} : Partial updates given fields of an existing maintenancePlan, field will ignore if it is null
     *
     * @param id the id of the maintenancePlanDTO to save.
     * @param maintenancePlanDTO the maintenancePlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenancePlanDTO,
     * or with status {@code 400 (Bad Request)} if the maintenancePlanDTO is not valid,
     * or with status {@code 404 (Not Found)} if the maintenancePlanDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the maintenancePlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MaintenancePlanDTO>> partialUpdateMaintenancePlan(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MaintenancePlanDTO maintenancePlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MaintenancePlan : {}, {}", id, maintenancePlanDTO);
        if (maintenancePlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenancePlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenancePlanRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MaintenancePlanDTO> result = maintenancePlanService.partialUpdate(maintenancePlanDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /maintenance-plans} : get all the Maintenance Plans.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Maintenance Plans in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MaintenancePlanDTO>> getAllMaintenancePlans(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MaintenancePlans");
        if (eagerload) {
            return maintenancePlanService.findAllWithEagerRelationships().collectList();
        } else {
            return maintenancePlanService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /maintenance-plans} : get all the Maintenance Plans as a stream.
     * @return the {@link Flux} of Maintenance Plans.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MaintenancePlanDTO> getAllMaintenancePlansAsStream() {
        LOG.debug("REST request to get all MaintenancePlans as a stream");
        return maintenancePlanService.findAll();
    }

    /**
     * {@code GET  /maintenance-plans/:id} : get the "id" maintenancePlan.
     *
     * @param id the id of the maintenancePlanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maintenancePlanDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MaintenancePlanDTO>> getMaintenancePlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MaintenancePlan : {}", id);
        Mono<MaintenancePlanDTO> maintenancePlanDTO = maintenancePlanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maintenancePlanDTO);
    }

    /**
     * {@code DELETE  /maintenance-plans/:id} : delete the "id" maintenancePlan.
     *
     * @param id the id of the maintenancePlanDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMaintenancePlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MaintenancePlan : {}", id);
        return maintenancePlanService
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
