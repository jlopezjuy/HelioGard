package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.DataProvenanceRepository;
import ar.com.heliguard.app.service.DataProvenanceService;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.DataProvenance}.
 */
@RestController
@RequestMapping("/api/data-provenances")
public class DataProvenanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(DataProvenanceResource.class);

    private static final String ENTITY_NAME = "dataProvenance";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final DataProvenanceService dataProvenanceService;

    private final DataProvenanceRepository dataProvenanceRepository;

    public DataProvenanceResource(DataProvenanceService dataProvenanceService, DataProvenanceRepository dataProvenanceRepository) {
        this.dataProvenanceService = dataProvenanceService;
        this.dataProvenanceRepository = dataProvenanceRepository;
    }

    /**
     * {@code POST  /data-provenances} : Create a new dataProvenance.
     *
     * @param dataProvenanceDTO the dataProvenanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataProvenanceDTO, or with status {@code 400 (Bad Request)} if the dataProvenance already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DataProvenanceDTO>> createDataProvenance(@Valid @RequestBody DataProvenanceDTO dataProvenanceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DataProvenance : {}", dataProvenanceDTO);
        if (dataProvenanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new dataProvenance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return dataProvenanceService.save(dataProvenanceDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/data-provenances/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /data-provenances/:id} : Updates an existing dataProvenance.
     *
     * @param id the id of the dataProvenanceDTO to save.
     * @param dataProvenanceDTO the dataProvenanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataProvenanceDTO,
     * or with status {@code 400 (Bad Request)} if the dataProvenanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dataProvenanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DataProvenanceDTO>> updateDataProvenance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DataProvenanceDTO dataProvenanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DataProvenance : {}, {}", id, dataProvenanceDTO);
        if (dataProvenanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dataProvenanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dataProvenanceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return dataProvenanceService
                .update(dataProvenanceDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /data-provenances/:id} : Partial updates given fields of an existing dataProvenance, field will ignore if it is null
     *
     * @param id the id of the dataProvenanceDTO to save.
     * @param dataProvenanceDTO the dataProvenanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataProvenanceDTO,
     * or with status {@code 400 (Bad Request)} if the dataProvenanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dataProvenanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dataProvenanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DataProvenanceDTO>> partialUpdateDataProvenance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DataProvenanceDTO dataProvenanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DataProvenance : {}, {}", id, dataProvenanceDTO);
        if (dataProvenanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dataProvenanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dataProvenanceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DataProvenanceDTO> result = dataProvenanceService.partialUpdate(dataProvenanceDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /data-provenances} : get all the Data Provenances.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Data Provenances in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DataProvenanceDTO>> getAllDataProvenances(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all DataProvenances");
        if (eagerload) {
            return dataProvenanceService.findAllWithEagerRelationships().collectList();
        } else {
            return dataProvenanceService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /data-provenances} : get all the Data Provenances as a stream.
     * @return the {@link Flux} of Data Provenances.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DataProvenanceDTO> getAllDataProvenancesAsStream() {
        LOG.debug("REST request to get all DataProvenances as a stream");
        return dataProvenanceService.findAll();
    }

    /**
     * {@code GET  /data-provenances/:id} : get the "id" dataProvenance.
     *
     * @param id the id of the dataProvenanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataProvenanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DataProvenanceDTO>> getDataProvenance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DataProvenance : {}", id);
        Mono<DataProvenanceDTO> dataProvenanceDTO = dataProvenanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dataProvenanceDTO);
    }

    /**
     * {@code DELETE  /data-provenances/:id} : delete the "id" dataProvenance.
     *
     * @param id the id of the dataProvenanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDataProvenance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DataProvenance : {}", id);
        return dataProvenanceService
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
