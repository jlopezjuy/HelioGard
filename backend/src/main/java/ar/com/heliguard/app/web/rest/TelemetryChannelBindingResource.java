package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.TelemetryChannelBindingRepository;
import ar.com.heliguard.app.service.TelemetryChannelBindingService;
import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.TelemetryChannelBinding}.
 */
@RestController
@RequestMapping("/api/telemetry-channel-bindings")
public class TelemetryChannelBindingResource {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelBindingResource.class);

    private static final String ENTITY_NAME = "telemetryChannelBinding";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final TelemetryChannelBindingService telemetryChannelBindingService;

    private final TelemetryChannelBindingRepository telemetryChannelBindingRepository;

    public TelemetryChannelBindingResource(
        TelemetryChannelBindingService telemetryChannelBindingService,
        TelemetryChannelBindingRepository telemetryChannelBindingRepository
    ) {
        this.telemetryChannelBindingService = telemetryChannelBindingService;
        this.telemetryChannelBindingRepository = telemetryChannelBindingRepository;
    }

    /**
     * {@code POST  /telemetry-channel-bindings} : Create a new telemetryChannelBinding.
     *
     * @param telemetryChannelBindingDTO the telemetryChannelBindingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telemetryChannelBindingDTO, or with status {@code 400 (Bad Request)} if the telemetryChannelBinding already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TelemetryChannelBindingDTO>> createTelemetryChannelBinding(
        @Valid @RequestBody TelemetryChannelBindingDTO telemetryChannelBindingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save TelemetryChannelBinding : {}", telemetryChannelBindingDTO);
        if (telemetryChannelBindingDTO.getId() != null) {
            throw new BadRequestAlertException("A new telemetryChannelBinding cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return telemetryChannelBindingService.save(telemetryChannelBindingDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/telemetry-channel-bindings/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /telemetry-channel-bindings/:id} : Updates an existing telemetryChannelBinding.
     *
     * @param id the id of the telemetryChannelBindingDTO to save.
     * @param telemetryChannelBindingDTO the telemetryChannelBindingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryChannelBindingDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryChannelBindingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telemetryChannelBindingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TelemetryChannelBindingDTO>> updateTelemetryChannelBinding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TelemetryChannelBindingDTO telemetryChannelBindingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TelemetryChannelBinding : {}, {}", id, telemetryChannelBindingDTO);
        if (telemetryChannelBindingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryChannelBindingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryChannelBindingRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return telemetryChannelBindingService
                .update(telemetryChannelBindingDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /telemetry-channel-bindings/:id} : Partial updates given fields of an existing telemetryChannelBinding, field will ignore if it is null
     *
     * @param id the id of the telemetryChannelBindingDTO to save.
     * @param telemetryChannelBindingDTO the telemetryChannelBindingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryChannelBindingDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryChannelBindingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the telemetryChannelBindingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the telemetryChannelBindingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TelemetryChannelBindingDTO>> partialUpdateTelemetryChannelBinding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TelemetryChannelBindingDTO telemetryChannelBindingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TelemetryChannelBinding : {}, {}", id, telemetryChannelBindingDTO);
        if (telemetryChannelBindingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryChannelBindingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryChannelBindingRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<TelemetryChannelBindingDTO> result = telemetryChannelBindingService.partialUpdate(telemetryChannelBindingDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /telemetry-channel-bindings} : get all the Telemetry Channel Bindings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Telemetry Channel Bindings in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TelemetryChannelBindingDTO>> getAllTelemetryChannelBindings(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TelemetryChannelBindings");
        if (eagerload) {
            return telemetryChannelBindingService.findAllWithEagerRelationships().collectList();
        } else {
            return telemetryChannelBindingService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /telemetry-channel-bindings} : get all the Telemetry Channel Bindings as a stream.
     * @return the {@link Flux} of Telemetry Channel Bindings.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TelemetryChannelBindingDTO> getAllTelemetryChannelBindingsAsStream() {
        LOG.debug("REST request to get all TelemetryChannelBindings as a stream");
        return telemetryChannelBindingService.findAll();
    }

    /**
     * {@code GET  /telemetry-channel-bindings/:id} : get the "id" telemetryChannelBinding.
     *
     * @param id the id of the telemetryChannelBindingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telemetryChannelBindingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TelemetryChannelBindingDTO>> getTelemetryChannelBinding(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TelemetryChannelBinding : {}", id);
        Mono<TelemetryChannelBindingDTO> telemetryChannelBindingDTO = telemetryChannelBindingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(telemetryChannelBindingDTO);
    }

    /**
     * {@code DELETE  /telemetry-channel-bindings/:id} : delete the "id" telemetryChannelBinding.
     *
     * @param id the id of the telemetryChannelBindingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTelemetryChannelBinding(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TelemetryChannelBinding : {}", id);
        return telemetryChannelBindingService
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
