package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.service.SolarArrayService;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SolarArray}.
 */
@RestController
@RequestMapping("/api/solar-arrays")
public class SolarArrayResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolarArrayResource.class);

    private static final String ENTITY_NAME = "solarArray";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SolarArrayService solarArrayService;

    private final SolarArrayRepository solarArrayRepository;

    public SolarArrayResource(SolarArrayService solarArrayService, SolarArrayRepository solarArrayRepository) {
        this.solarArrayService = solarArrayService;
        this.solarArrayRepository = solarArrayRepository;
    }

    /**
     * {@code POST  /solar-arrays} : Create a new solarArray.
     *
     * @param solarArrayDTO the solarArrayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solarArrayDTO, or with status {@code 400 (Bad Request)} if the solarArray already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SolarArrayDTO>> createSolarArray(@Valid @RequestBody SolarArrayDTO solarArrayDTO) throws URISyntaxException {
        LOG.debug("REST request to save SolarArray : {}", solarArrayDTO);
        if (solarArrayDTO.getId() != null) {
            throw new BadRequestAlertException("A new solarArray cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return solarArrayService.save(solarArrayDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/solar-arrays/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /solar-arrays/:id} : Updates an existing solarArray.
     *
     * @param id the id of the solarArrayDTO to save.
     * @param solarArrayDTO the solarArrayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarArrayDTO,
     * or with status {@code 400 (Bad Request)} if the solarArrayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solarArrayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SolarArrayDTO>> updateSolarArray(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SolarArrayDTO solarArrayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolarArray : {}, {}", id, solarArrayDTO);
        if (solarArrayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarArrayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarArrayRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return solarArrayService
                .update(solarArrayDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /solar-arrays/:id} : Partial updates given fields of an existing solarArray, field will ignore if it is null
     *
     * @param id the id of the solarArrayDTO to save.
     * @param solarArrayDTO the solarArrayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarArrayDTO,
     * or with status {@code 400 (Bad Request)} if the solarArrayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the solarArrayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the solarArrayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SolarArrayDTO>> partialUpdateSolarArray(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SolarArrayDTO solarArrayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SolarArray : {}, {}", id, solarArrayDTO);
        if (solarArrayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarArrayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarArrayRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SolarArrayDTO> result = solarArrayService.partialUpdate(solarArrayDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /solar-arrays} : get all the Solar Arrays.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solar Arrays in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SolarArrayDTO>> getAllSolarArrays(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SolarArrays");
        if (eagerload) {
            return solarArrayService.findAllWithEagerRelationships().collectList();
        } else {
            return solarArrayService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /solar-arrays} : get all the Solar Arrays as a stream.
     * @return the {@link Flux} of Solar Arrays.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SolarArrayDTO> getAllSolarArraysAsStream() {
        LOG.debug("REST request to get all SolarArrays as a stream");
        return solarArrayService.findAll();
    }

    /**
     * {@code GET  /solar-arrays/:id} : get the "id" solarArray.
     *
     * @param id the id of the solarArrayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solarArrayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SolarArrayDTO>> getSolarArray(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolarArray : {}", id);
        Mono<SolarArrayDTO> solarArrayDTO = solarArrayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solarArrayDTO);
    }

    /**
     * {@code DELETE  /solar-arrays/:id} : delete the "id" solarArray.
     *
     * @param id the id of the solarArrayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSolarArray(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SolarArray : {}", id);
        return solarArrayService
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
