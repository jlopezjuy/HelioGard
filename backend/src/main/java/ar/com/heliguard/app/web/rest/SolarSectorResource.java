package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SolarSectorRepository;
import ar.com.heliguard.app.service.SolarSectorService;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SolarSector}.
 */
@RestController
@RequestMapping("/api/solar-sectors")
public class SolarSectorResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolarSectorResource.class);

    private static final String ENTITY_NAME = "solarSector";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SolarSectorService solarSectorService;

    private final SolarSectorRepository solarSectorRepository;

    public SolarSectorResource(SolarSectorService solarSectorService, SolarSectorRepository solarSectorRepository) {
        this.solarSectorService = solarSectorService;
        this.solarSectorRepository = solarSectorRepository;
    }

    /**
     * {@code POST  /solar-sectors} : Create a new solarSector.
     *
     * @param solarSectorDTO the solarSectorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solarSectorDTO, or with status {@code 400 (Bad Request)} if the solarSector already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SolarSectorDTO>> createSolarSector(@Valid @RequestBody SolarSectorDTO solarSectorDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SolarSector : {}", solarSectorDTO);
        if (solarSectorDTO.getId() != null) {
            throw new BadRequestAlertException("A new solarSector cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return solarSectorService.save(solarSectorDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/solar-sectors/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /solar-sectors/:id} : Updates an existing solarSector.
     *
     * @param id the id of the solarSectorDTO to save.
     * @param solarSectorDTO the solarSectorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarSectorDTO,
     * or with status {@code 400 (Bad Request)} if the solarSectorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solarSectorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SolarSectorDTO>> updateSolarSector(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SolarSectorDTO solarSectorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolarSector : {}, {}", id, solarSectorDTO);
        if (solarSectorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarSectorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarSectorRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return solarSectorService
                .update(solarSectorDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /solar-sectors/:id} : Partial updates given fields of an existing solarSector, field will ignore if it is null
     *
     * @param id the id of the solarSectorDTO to save.
     * @param solarSectorDTO the solarSectorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solarSectorDTO,
     * or with status {@code 400 (Bad Request)} if the solarSectorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the solarSectorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the solarSectorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SolarSectorDTO>> partialUpdateSolarSector(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SolarSectorDTO solarSectorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SolarSector : {}, {}", id, solarSectorDTO);
        if (solarSectorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solarSectorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return solarSectorRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SolarSectorDTO> result = solarSectorService.partialUpdate(solarSectorDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /solar-sectors} : get all the Solar Sectors.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solar Sectors in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SolarSectorDTO>> getAllSolarSectors(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SolarSectors");
        if (eagerload) {
            return solarSectorService.findAllWithEagerRelationships().collectList();
        } else {
            return solarSectorService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /solar-sectors} : get all the Solar Sectors as a stream.
     * @return the {@link Flux} of Solar Sectors.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SolarSectorDTO> getAllSolarSectorsAsStream() {
        LOG.debug("REST request to get all SolarSectors as a stream");
        return solarSectorService.findAll();
    }

    /**
     * {@code GET  /solar-sectors/:id} : get the "id" solarSector.
     *
     * @param id the id of the solarSectorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solarSectorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SolarSectorDTO>> getSolarSector(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolarSector : {}", id);
        Mono<SolarSectorDTO> solarSectorDTO = solarSectorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solarSectorDTO);
    }

    /**
     * {@code DELETE  /solar-sectors/:id} : delete the "id" solarSector.
     *
     * @param id the id of the solarSectorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSolarSector(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SolarSector : {}", id);
        return solarSectorService
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
