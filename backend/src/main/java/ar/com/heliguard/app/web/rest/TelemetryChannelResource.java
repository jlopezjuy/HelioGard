package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.TelemetryChannelRepository;
import ar.com.heliguard.app.service.TelemetryChannelService;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.TelemetryChannel}.
 */
@RestController
@RequestMapping("/api/telemetry-channels")
public class TelemetryChannelResource {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryChannelResource.class);

    private static final String ENTITY_NAME = "telemetryChannel";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final TelemetryChannelService telemetryChannelService;

    private final TelemetryChannelRepository telemetryChannelRepository;

    public TelemetryChannelResource(
        TelemetryChannelService telemetryChannelService,
        TelemetryChannelRepository telemetryChannelRepository
    ) {
        this.telemetryChannelService = telemetryChannelService;
        this.telemetryChannelRepository = telemetryChannelRepository;
    }

    /**
     * {@code POST  /telemetry-channels} : Create a new telemetryChannel.
     *
     * @param telemetryChannelDTO the telemetryChannelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telemetryChannelDTO, or with status {@code 400 (Bad Request)} if the telemetryChannel already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TelemetryChannelDTO>> createTelemetryChannel(@Valid @RequestBody TelemetryChannelDTO telemetryChannelDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TelemetryChannel : {}", telemetryChannelDTO);
        if (telemetryChannelDTO.getId() != null) {
            throw new BadRequestAlertException("A new telemetryChannel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return telemetryChannelService.save(telemetryChannelDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/telemetry-channels/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /telemetry-channels/:id} : Updates an existing telemetryChannel.
     *
     * @param id the id of the telemetryChannelDTO to save.
     * @param telemetryChannelDTO the telemetryChannelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryChannelDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryChannelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telemetryChannelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TelemetryChannelDTO>> updateTelemetryChannel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TelemetryChannelDTO telemetryChannelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TelemetryChannel : {}, {}", id, telemetryChannelDTO);
        if (telemetryChannelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryChannelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryChannelRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return telemetryChannelService
                .update(telemetryChannelDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /telemetry-channels/:id} : Partial updates given fields of an existing telemetryChannel, field will ignore if it is null
     *
     * @param id the id of the telemetryChannelDTO to save.
     * @param telemetryChannelDTO the telemetryChannelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryChannelDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryChannelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the telemetryChannelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the telemetryChannelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TelemetryChannelDTO>> partialUpdateTelemetryChannel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TelemetryChannelDTO telemetryChannelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TelemetryChannel : {}, {}", id, telemetryChannelDTO);
        if (telemetryChannelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryChannelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryChannelRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<TelemetryChannelDTO> result = telemetryChannelService.partialUpdate(telemetryChannelDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /telemetry-channels} : get all the Telemetry Channels.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Telemetry Channels in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TelemetryChannelDTO>> getAllTelemetryChannels(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TelemetryChannels");
        if (eagerload) {
            return telemetryChannelService.findAllWithEagerRelationships().collectList();
        } else {
            return telemetryChannelService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /telemetry-channels} : get all the Telemetry Channels as a stream.
     * @return the {@link Flux} of Telemetry Channels.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TelemetryChannelDTO> getAllTelemetryChannelsAsStream() {
        LOG.debug("REST request to get all TelemetryChannels as a stream");
        return telemetryChannelService.findAll();
    }

    /**
     * {@code GET  /telemetry-channels/:id} : get the "id" telemetryChannel.
     *
     * @param id the id of the telemetryChannelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telemetryChannelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TelemetryChannelDTO>> getTelemetryChannel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TelemetryChannel : {}", id);
        Mono<TelemetryChannelDTO> telemetryChannelDTO = telemetryChannelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(telemetryChannelDTO);
    }

    /**
     * {@code DELETE  /telemetry-channels/:id} : delete the "id" telemetryChannel.
     *
     * @param id the id of the telemetryChannelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTelemetryChannel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TelemetryChannel : {}", id);
        return telemetryChannelService
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
