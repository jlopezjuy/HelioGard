package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.service.MaintenancePlanVersionService;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MaintenancePlanVersion}.
 */
@RestController
@RequestMapping("/api/maintenance-plan-versions")
public class MaintenancePlanVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenancePlanVersionResource.class);

    private static final String ENTITY_NAME = "maintenancePlanVersion";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MaintenancePlanVersionService maintenancePlanVersionService;

    private final MaintenancePlanVersionRepository maintenancePlanVersionRepository;

    public MaintenancePlanVersionResource(
        MaintenancePlanVersionService maintenancePlanVersionService,
        MaintenancePlanVersionRepository maintenancePlanVersionRepository
    ) {
        this.maintenancePlanVersionService = maintenancePlanVersionService;
        this.maintenancePlanVersionRepository = maintenancePlanVersionRepository;
    }

    /**
     * {@code POST  /maintenance-plan-versions} : Create a new maintenancePlanVersion.
     *
     * @param maintenancePlanVersionDTO the maintenancePlanVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maintenancePlanVersionDTO, or with status {@code 400 (Bad Request)} if the maintenancePlanVersion already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MaintenancePlanVersionDTO>> createMaintenancePlanVersion(
        @Valid @RequestBody MaintenancePlanVersionDTO maintenancePlanVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save MaintenancePlanVersion : {}", maintenancePlanVersionDTO);
        if (maintenancePlanVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new maintenancePlanVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return maintenancePlanVersionService.save(maintenancePlanVersionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/maintenance-plan-versions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /maintenance-plan-versions/:id} : Updates an existing maintenancePlanVersion.
     *
     * @param id the id of the maintenancePlanVersionDTO to save.
     * @param maintenancePlanVersionDTO the maintenancePlanVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenancePlanVersionDTO,
     * or with status {@code 400 (Bad Request)} if the maintenancePlanVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maintenancePlanVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MaintenancePlanVersionDTO>> updateMaintenancePlanVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MaintenancePlanVersionDTO maintenancePlanVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MaintenancePlanVersion : {}, {}", id, maintenancePlanVersionDTO);
        if (maintenancePlanVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenancePlanVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenancePlanVersionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return maintenancePlanVersionService
                .update(maintenancePlanVersionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /maintenance-plan-versions/:id} : Partial updates given fields of an existing maintenancePlanVersion, field will ignore if it is null
     *
     * @param id the id of the maintenancePlanVersionDTO to save.
     * @param maintenancePlanVersionDTO the maintenancePlanVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenancePlanVersionDTO,
     * or with status {@code 400 (Bad Request)} if the maintenancePlanVersionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the maintenancePlanVersionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the maintenancePlanVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MaintenancePlanVersionDTO>> partialUpdateMaintenancePlanVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MaintenancePlanVersionDTO maintenancePlanVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MaintenancePlanVersion : {}, {}", id, maintenancePlanVersionDTO);
        if (maintenancePlanVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenancePlanVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maintenancePlanVersionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MaintenancePlanVersionDTO> result = maintenancePlanVersionService.partialUpdate(maintenancePlanVersionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /maintenance-plan-versions} : get all the Maintenance Plan Versions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Maintenance Plan Versions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MaintenancePlanVersionDTO>> getAllMaintenancePlanVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MaintenancePlanVersions");
        if (eagerload) {
            return maintenancePlanVersionService.findAllWithEagerRelationships().collectList();
        } else {
            return maintenancePlanVersionService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /maintenance-plan-versions} : get all the Maintenance Plan Versions as a stream.
     * @return the {@link Flux} of Maintenance Plan Versions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MaintenancePlanVersionDTO> getAllMaintenancePlanVersionsAsStream() {
        LOG.debug("REST request to get all MaintenancePlanVersions as a stream");
        return maintenancePlanVersionService.findAll();
    }

    /**
     * {@code GET  /maintenance-plan-versions/:id} : get the "id" maintenancePlanVersion.
     *
     * @param id the id of the maintenancePlanVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maintenancePlanVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MaintenancePlanVersionDTO>> getMaintenancePlanVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MaintenancePlanVersion : {}", id);
        Mono<MaintenancePlanVersionDTO> maintenancePlanVersionDTO = maintenancePlanVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maintenancePlanVersionDTO);
    }

    /**
     * {@code DELETE  /maintenance-plan-versions/:id} : delete the "id" maintenancePlanVersion.
     *
     * @param id the id of the maintenancePlanVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMaintenancePlanVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MaintenancePlanVersion : {}", id);
        return maintenancePlanVersionService
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
