package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.service.MaintenanceInterventionService;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MaintenanceIntervention}.
 */
@RestController
@RequestMapping("/api/maintenance-interventions")
public class MaintenanceInterventionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenanceInterventionResource.class);

    private static final String ENTITY_NAME = "maintenanceIntervention";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MaintenanceInterventionService maintenanceInterventionService;

    private final MaintenanceInterventionRepository maintenanceInterventionRepository;

    public MaintenanceInterventionResource(
        MaintenanceInterventionService maintenanceInterventionService,
        MaintenanceInterventionRepository maintenanceInterventionRepository
    ) {
        this.maintenanceInterventionService = maintenanceInterventionService;
        this.maintenanceInterventionRepository = maintenanceInterventionRepository;
    }

    /**
     * {@code POST  /maintenance-interventions} : Create a new maintenanceIntervention.
     *
     * @param maintenanceInterventionDTO the maintenanceInterventionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maintenanceInterventionDTO, or with status {@code 400 (Bad Request)} if the maintenanceIntervention already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MaintenanceInterventionDTO>> createMaintenanceIntervention(
        @Valid @RequestBody MaintenanceInterventionDTO maintenanceInterventionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save MaintenanceIntervention : {}", maintenanceInterventionDTO);
        if (maintenanceInterventionDTO.getId() != null) {
            throw new BadRequestAlertException("A new maintenanceIntervention cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return maintenanceInterventionService.save(maintenanceInterventionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/maintenance-interventions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /maintenance-interventions/:id} : Updates an existing maintenanceIntervention.
     *
     * @param id the id of the maintenanceInterventionDTO to save.
     * @param maintenanceInterventionDTO the maintenanceInterventionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenanceInterventionDTO,
     * or with status {@code 400 (Bad Request)} if the maintenanceInterventionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maintenanceInterventionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MaintenanceInterventionDTO>> updateMaintenanceIntervention(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MaintenanceInterventionDTO maintenanceInterventionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MaintenanceIntervention : {}, {}", id, maintenanceInterventionDTO);
        if (maintenanceInterventionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenanceInterventionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenanceInterventionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return maintenanceInterventionService
                .update(maintenanceInterventionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /maintenance-interventions/:id} : Partial updates given fields of an existing maintenanceIntervention, field will ignore if it is null
     *
     * @param id the id of the maintenanceInterventionDTO to save.
     * @param maintenanceInterventionDTO the maintenanceInterventionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenanceInterventionDTO,
     * or with status {@code 400 (Bad Request)} if the maintenanceInterventionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the maintenanceInterventionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the maintenanceInterventionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MaintenanceInterventionDTO>> partialUpdateMaintenanceIntervention(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MaintenanceInterventionDTO maintenanceInterventionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MaintenanceIntervention : {}, {}", id, maintenanceInterventionDTO);
        if (maintenanceInterventionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenanceInterventionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenanceInterventionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MaintenanceInterventionDTO> result = maintenanceInterventionService.partialUpdate(maintenanceInterventionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /maintenance-interventions} : get all the Maintenance Interventions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Maintenance Interventions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MaintenanceInterventionDTO>> getAllMaintenanceInterventions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MaintenanceInterventions");
        if (eagerload) {
            return maintenanceInterventionService.findAllWithEagerRelationships().collectList();
        } else {
            return maintenanceInterventionService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /maintenance-interventions} : get all the Maintenance Interventions as a stream.
     * @return the {@link Flux} of Maintenance Interventions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MaintenanceInterventionDTO> getAllMaintenanceInterventionsAsStream() {
        LOG.debug("REST request to get all MaintenanceInterventions as a stream");
        return maintenanceInterventionService.findAll();
    }

    /**
     * {@code GET  /maintenance-interventions/:id} : get the "id" maintenanceIntervention.
     *
     * @param id the id of the maintenanceInterventionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maintenanceInterventionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MaintenanceInterventionDTO>> getMaintenanceIntervention(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MaintenanceIntervention : {}", id);
        Mono<MaintenanceInterventionDTO> maintenanceInterventionDTO = maintenanceInterventionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maintenanceInterventionDTO);
    }

    /**
     * {@code DELETE  /maintenance-interventions/:id} : delete the "id" maintenanceIntervention.
     *
     * @param id the id of the maintenanceInterventionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMaintenanceIntervention(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MaintenanceIntervention : {}", id);
        return maintenanceInterventionService
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
