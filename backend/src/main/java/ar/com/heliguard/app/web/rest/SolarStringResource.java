package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.service.SolarStringService;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SolarString}.
 */
@RestController
@RequestMapping("/api/solar-strings")
public class SolarStringResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolarStringResource.class);

    private static final String ENTITY_NAME = "solarString";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SolarStringService solarStringService;

    private final SolarStringRepository solarStringRepository;

    public SolarStringResource(SolarStringService solarStringService, SolarStringRepository solarStringRepository) {
        this.solarStringService = solarStringService;
        this.solarStringRepository = solarStringRepository;
    }

    /**
     * {@code POST  /solar-strings} : Create a new solarString.
     *
     * @param solarStringDTO the solarStringDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solarStringDTO, or with status {@code 400 (Bad Request)} if the solarString already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SolarStringDTO>> createSolarString(@Valid @RequestBody SolarStringDTO solarStringDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SolarString : {}", solarStringDTO);
        if (solarStringDTO.getId() != null) {
            throw new BadRequestAlertException("A new solarString cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return solarStringService.save(solarStringDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/solar-strings/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /solar-strings/:id} : Updates an existing solarString.
     *
     * @param id the id of the solarStringDTO to save.
     * @param solarStringDTO the solarStringDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarStringDTO,
     * or with status {@code 400 (Bad Request)} if the solarStringDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solarStringDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SolarStringDTO>> updateSolarString(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SolarStringDTO solarStringDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolarString : {}, {}", id, solarStringDTO);
        if (solarStringDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarStringDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarStringRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return solarStringService
                .update(solarStringDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /solar-strings/:id} : Partial updates given fields of an existing solarString, field will ignore if it is null
     *
     * @param id the id of the solarStringDTO to save.
     * @param solarStringDTO the solarStringDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarStringDTO,
     * or with status {@code 400 (Bad Request)} if the solarStringDTO is not valid,
     * or with status {@code 404 (Not Found)} if the solarStringDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the solarStringDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SolarStringDTO>> partialUpdateSolarString(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SolarStringDTO solarStringDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SolarString : {}, {}", id, solarStringDTO);
        if (solarStringDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarStringDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarStringRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SolarStringDTO> result = solarStringService.partialUpdate(solarStringDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /solar-strings} : get all the Solar Strings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solar Strings in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SolarStringDTO>> getAllSolarStrings(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SolarStrings");
        if (eagerload) {
            return solarStringService.findAllWithEagerRelationships().collectList();
        } else {
            return solarStringService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /solar-strings} : get all the Solar Strings as a stream.
     * @return the {@link Flux} of Solar Strings.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SolarStringDTO> getAllSolarStringsAsStream() {
        LOG.debug("REST request to get all SolarStrings as a stream");
        return solarStringService.findAll();
    }

    /**
     * {@code GET  /solar-strings/:id} : get the "id" solarString.
     *
     * @param id the id of the solarStringDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solarStringDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SolarStringDTO>> getSolarString(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolarString : {}", id);
        Mono<SolarStringDTO> solarStringDTO = solarStringService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solarStringDTO);
    }

    /**
     * {@code DELETE  /solar-strings/:id} : delete the "id" solarString.
     *
     * @param id the id of the solarStringDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSolarString(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SolarString : {}", id);
        return solarStringService
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
