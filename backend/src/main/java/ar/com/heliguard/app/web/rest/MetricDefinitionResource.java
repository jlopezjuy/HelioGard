package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MetricDefinitionRepository;
import ar.com.heliguard.app.service.MetricDefinitionService;
import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MetricDefinition}.
 */
@RestController
@RequestMapping("/api/metric-definitions")
public class MetricDefinitionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MetricDefinitionResource.class);

    private static final String ENTITY_NAME = "metricDefinition";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MetricDefinitionService metricDefinitionService;

    private final MetricDefinitionRepository metricDefinitionRepository;

    public MetricDefinitionResource(
        MetricDefinitionService metricDefinitionService,
        MetricDefinitionRepository metricDefinitionRepository
    ) {
        this.metricDefinitionService = metricDefinitionService;
        this.metricDefinitionRepository = metricDefinitionRepository;
    }

    /**
     * {@code POST  /metric-definitions} : Create a new metricDefinition.
     *
     * @param metricDefinitionDTO the metricDefinitionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metricDefinitionDTO, or with status {@code 400 (Bad Request)} if the metricDefinition already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MetricDefinitionDTO>> createMetricDefinition(@Valid @RequestBody MetricDefinitionDTO metricDefinitionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MetricDefinition : {}", metricDefinitionDTO);
        if (metricDefinitionDTO.getId() != null) {
            throw new BadRequestAlertException("A new metricDefinition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return metricDefinitionService.save(metricDefinitionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/metric-definitions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /metric-definitions/:id} : Updates an existing metricDefinition.
     *
     * @param id the id of the metricDefinitionDTO to save.
     * @param metricDefinitionDTO the metricDefinitionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricDefinitionDTO,
     * or with status {@code 400 (Bad Request)} if the metricDefinitionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metricDefinitionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MetricDefinitionDTO>> updateMetricDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MetricDefinitionDTO metricDefinitionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MetricDefinition : {}, {}", id, metricDefinitionDTO);
        if (metricDefinitionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricDefinitionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return metricDefinitionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return metricDefinitionService
                .update(metricDefinitionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /metric-definitions/:id} : Partial updates given fields of an existing metricDefinition, field will ignore if it is null
     *
     * @param id the id of the metricDefinitionDTO to save.
     * @param metricDefinitionDTO the metricDefinitionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metricDefinitionDTO,
     * or with status {@code 400 (Bad Request)} if the metricDefinitionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the metricDefinitionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the metricDefinitionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MetricDefinitionDTO>> partialUpdateMetricDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MetricDefinitionDTO metricDefinitionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MetricDefinition : {}, {}", id, metricDefinitionDTO);
        if (metricDefinitionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, metricDefinitionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return metricDefinitionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MetricDefinitionDTO> result = metricDefinitionService.partialUpdate(metricDefinitionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /metric-definitions} : get all the Metric Definitions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Metric Definitions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MetricDefinitionDTO>> getAllMetricDefinitions() {
        LOG.debug("REST request to get all MetricDefinitions");
        return metricDefinitionService.findAll().collectList();
    }

    /**
     * {@code GET  /metric-definitions} : get all the Metric Definitions as a stream.
     * @return the {@link Flux} of Metric Definitions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MetricDefinitionDTO> getAllMetricDefinitionsAsStream() {
        LOG.debug("REST request to get all MetricDefinitions as a stream");
        return metricDefinitionService.findAll();
    }

    /**
     * {@code GET  /metric-definitions/:id} : get the "id" metricDefinition.
     *
     * @param id the id of the metricDefinitionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metricDefinitionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MetricDefinitionDTO>> getMetricDefinition(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MetricDefinition : {}", id);
        Mono<MetricDefinitionDTO> metricDefinitionDTO = metricDefinitionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(metricDefinitionDTO);
    }

    /**
     * {@code DELETE  /metric-definitions/:id} : delete the "id" metricDefinition.
     *
     * @param id the id of the metricDefinitionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMetricDefinition(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MetricDefinition : {}", id);
        return metricDefinitionService
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
