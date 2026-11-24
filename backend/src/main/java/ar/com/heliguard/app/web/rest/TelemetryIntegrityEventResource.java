package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.TelemetryIntegrityEventService;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link ar.com.heliguard.app.domain.TelemetryIntegrityEvent}.
 */
@RestController
@RequestMapping("/api/telemetry-integrity-events")
public class TelemetryIntegrityEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryIntegrityEventResource.class);

    private static final String ENTITY_NAME = "telemetryIntegrityEvent";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final TelemetryIntegrityEventService telemetryIntegrityEventService;

    private final TelemetryIntegrityEventRepository telemetryIntegrityEventRepository;

    public TelemetryIntegrityEventResource(
        TelemetryIntegrityEventService telemetryIntegrityEventService,
        TelemetryIntegrityEventRepository telemetryIntegrityEventRepository
    ) {
        this.telemetryIntegrityEventService = telemetryIntegrityEventService;
        this.telemetryIntegrityEventRepository = telemetryIntegrityEventRepository;
    }

    /**
     * {@code POST  /telemetry-integrity-events} : Create a new telemetryIntegrityEvent.
     *
     * @param telemetryIntegrityEventDTO the telemetryIntegrityEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telemetryIntegrityEventDTO, or with status {@code 400 (Bad Request)} if the telemetryIntegrityEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TelemetryIntegrityEventDTO>> createTelemetryIntegrityEvent(
        @Valid @RequestBody TelemetryIntegrityEventDTO telemetryIntegrityEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save TelemetryIntegrityEvent : {}", telemetryIntegrityEventDTO);
        if (telemetryIntegrityEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new telemetryIntegrityEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return telemetryIntegrityEventService.save(telemetryIntegrityEventDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/telemetry-integrity-events/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /telemetry-integrity-events/:id} : Updates an existing telemetryIntegrityEvent.
     *
     * @param id the id of the telemetryIntegrityEventDTO to save.
     * @param telemetryIntegrityEventDTO the telemetryIntegrityEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryIntegrityEventDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryIntegrityEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telemetryIntegrityEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TelemetryIntegrityEventDTO>> updateTelemetryIntegrityEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TelemetryIntegrityEventDTO telemetryIntegrityEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TelemetryIntegrityEvent : {}, {}", id, telemetryIntegrityEventDTO);
        if (telemetryIntegrityEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryIntegrityEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryIntegrityEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return telemetryIntegrityEventService
                .update(telemetryIntegrityEventDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /telemetry-integrity-events/:id} : Partial updates given fields of an existing telemetryIntegrityEvent, field will ignore if it is null
     *
     * @param id the id of the telemetryIntegrityEventDTO to save.
     * @param telemetryIntegrityEventDTO the telemetryIntegrityEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telemetryIntegrityEventDTO,
     * or with status {@code 400 (Bad Request)} if the telemetryIntegrityEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the telemetryIntegrityEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the telemetryIntegrityEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TelemetryIntegrityEventDTO>> partialUpdateTelemetryIntegrityEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TelemetryIntegrityEventDTO telemetryIntegrityEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TelemetryIntegrityEvent : {}, {}", id, telemetryIntegrityEventDTO);
        if (telemetryIntegrityEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telemetryIntegrityEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telemetryIntegrityEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<TelemetryIntegrityEventDTO> result = telemetryIntegrityEventService.partialUpdate(telemetryIntegrityEventDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /telemetry-integrity-events} : get all the Telemetry Integrity Events.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Telemetry Integrity Events in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TelemetryIntegrityEventDTO>>> getAllTelemetryIntegrityEvents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TelemetryIntegrityEvents");
        return telemetryIntegrityEventService
            .countAll()
            .zipWith(telemetryIntegrityEventService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /telemetry-integrity-events/:id} : get the "id" telemetryIntegrityEvent.
     *
     * @param id the id of the telemetryIntegrityEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telemetryIntegrityEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TelemetryIntegrityEventDTO>> getTelemetryIntegrityEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TelemetryIntegrityEvent : {}", id);
        Mono<TelemetryIntegrityEventDTO> telemetryIntegrityEventDTO = telemetryIntegrityEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(telemetryIntegrityEventDTO);
    }

    /**
     * {@code DELETE  /telemetry-integrity-events/:id} : delete the "id" telemetryIntegrityEvent.
     *
     * @param id the id of the telemetryIntegrityEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTelemetryIntegrityEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TelemetryIntegrityEvent : {}", id);
        return telemetryIntegrityEventService
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
