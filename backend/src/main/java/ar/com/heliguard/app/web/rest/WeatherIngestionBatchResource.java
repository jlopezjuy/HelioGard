package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.WeatherIngestionBatchService;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.WeatherIngestionBatch}.
 */
@RestController
@RequestMapping("/api/weather-ingestion-batches")
public class WeatherIngestionBatchResource {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherIngestionBatchResource.class);

    private static final String ENTITY_NAME = "weatherIngestionBatch";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final WeatherIngestionBatchService weatherIngestionBatchService;

    private final WeatherIngestionBatchRepository weatherIngestionBatchRepository;

    public WeatherIngestionBatchResource(
        WeatherIngestionBatchService weatherIngestionBatchService,
        WeatherIngestionBatchRepository weatherIngestionBatchRepository
    ) {
        this.weatherIngestionBatchService = weatherIngestionBatchService;
        this.weatherIngestionBatchRepository = weatherIngestionBatchRepository;
    }

    /**
     * {@code POST  /weather-ingestion-batches} : Create a new weatherIngestionBatch.
     *
     * @param weatherIngestionBatchDTO the weatherIngestionBatchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new weatherIngestionBatchDTO, or with status {@code 400 (Bad Request)} if the weatherIngestionBatch already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<WeatherIngestionBatchDTO>> createWeatherIngestionBatch(
        @Valid @RequestBody WeatherIngestionBatchDTO weatherIngestionBatchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save WeatherIngestionBatch : {}", weatherIngestionBatchDTO);
        if (weatherIngestionBatchDTO.getId() != null) {
            throw new BadRequestAlertException("A new weatherIngestionBatch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return weatherIngestionBatchService.save(weatherIngestionBatchDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/weather-ingestion-batches/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /weather-ingestion-batches/:id} : Updates an existing weatherIngestionBatch.
     *
     * @param id the id of the weatherIngestionBatchDTO to save.
     * @param weatherIngestionBatchDTO the weatherIngestionBatchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated weatherIngestionBatchDTO,
     * or with status {@code 400 (Bad Request)} if the weatherIngestionBatchDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the weatherIngestionBatchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<WeatherIngestionBatchDTO>> updateWeatherIngestionBatch(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WeatherIngestionBatchDTO weatherIngestionBatchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WeatherIngestionBatch : {}, {}", id, weatherIngestionBatchDTO);
        if (weatherIngestionBatchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, weatherIngestionBatchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return weatherIngestionBatchRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return weatherIngestionBatchService
                .update(weatherIngestionBatchDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /weather-ingestion-batches/:id} : Partial updates given fields of an existing weatherIngestionBatch, field will ignore if it is null
     *
     * @param id the id of the weatherIngestionBatchDTO to save.
     * @param weatherIngestionBatchDTO the weatherIngestionBatchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated weatherIngestionBatchDTO,
     * or with status {@code 400 (Bad Request)} if the weatherIngestionBatchDTO is not valid,
     * or with status {@code 404 (Not Found)} if the weatherIngestionBatchDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the weatherIngestionBatchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<WeatherIngestionBatchDTO>> partialUpdateWeatherIngestionBatch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WeatherIngestionBatchDTO weatherIngestionBatchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WeatherIngestionBatch : {}, {}", id, weatherIngestionBatchDTO);
        if (weatherIngestionBatchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, weatherIngestionBatchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return weatherIngestionBatchRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<WeatherIngestionBatchDTO> result = weatherIngestionBatchService.partialUpdate(weatherIngestionBatchDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /weather-ingestion-batches} : get all the Weather Ingestion Batches.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Weather Ingestion Batches in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<WeatherIngestionBatchDTO>> getAllWeatherIngestionBatches(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all WeatherIngestionBatches");
        if (eagerload) {
            return weatherIngestionBatchService.findAllWithEagerRelationships().collectList();
        } else {
            return weatherIngestionBatchService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /weather-ingestion-batches} : get all the Weather Ingestion Batches as a stream.
     * @return the {@link Flux} of Weather Ingestion Batches.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<WeatherIngestionBatchDTO> getAllWeatherIngestionBatchesAsStream() {
        LOG.debug("REST request to get all WeatherIngestionBatches as a stream");
        return weatherIngestionBatchService.findAll();
    }

    /**
     * {@code GET  /weather-ingestion-batches/:id} : get the "id" weatherIngestionBatch.
     *
     * @param id the id of the weatherIngestionBatchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the weatherIngestionBatchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<WeatherIngestionBatchDTO>> getWeatherIngestionBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WeatherIngestionBatch : {}", id);
        Mono<WeatherIngestionBatchDTO> weatherIngestionBatchDTO = weatherIngestionBatchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(weatherIngestionBatchDTO);
    }

    /**
     * {@code DELETE  /weather-ingestion-batches/:id} : delete the "id" weatherIngestionBatch.
     *
     * @param id the id of the weatherIngestionBatchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteWeatherIngestionBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WeatherIngestionBatch : {}", id);
        return weatherIngestionBatchService
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
