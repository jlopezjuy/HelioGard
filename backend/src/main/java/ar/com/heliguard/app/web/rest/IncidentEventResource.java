package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.IncidentEventRepository;
import ar.com.heliguard.app.service.IncidentEventService;
import ar.com.heliguard.app.service.dto.IncidentEventDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.IncidentEvent}.
 */
@RestController
@RequestMapping("/api/incident-events")
public class IncidentEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventResource.class);

    private static final String ENTITY_NAME = "incidentEvent";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final IncidentEventService incidentEventService;

    private final IncidentEventRepository incidentEventRepository;

    public IncidentEventResource(IncidentEventService incidentEventService, IncidentEventRepository incidentEventRepository) {
        this.incidentEventService = incidentEventService;
        this.incidentEventRepository = incidentEventRepository;
    }

    /**
     * {@code POST  /incident-events} : Create a new incidentEvent.
     *
     * @param incidentEventDTO the incidentEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incidentEventDTO, or with status {@code 400 (Bad Request)} if the incidentEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<IncidentEventDTO>> createIncidentEvent(@Valid @RequestBody IncidentEventDTO incidentEventDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save IncidentEvent : {}", incidentEventDTO);
        if (incidentEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new incidentEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return incidentEventService.save(incidentEventDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/incident-events/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /incident-events/:id} : Updates an existing incidentEvent.
     *
     * @param id the id of the incidentEventDTO to save.
     * @param incidentEventDTO the incidentEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentEventDTO,
     * or with status {@code 400 (Bad Request)} if the incidentEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incidentEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<IncidentEventDTO>> updateIncidentEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IncidentEventDTO incidentEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IncidentEvent : {}, {}", id, incidentEventDTO);
        if (incidentEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return incidentEventService
                .update(incidentEventDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /incident-events/:id} : Partial updates given fields of an existing incidentEvent, field will ignore if it is null
     *
     * @param id the id of the incidentEventDTO to save.
     * @param incidentEventDTO the incidentEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentEventDTO,
     * or with status {@code 400 (Bad Request)} if the incidentEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the incidentEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the incidentEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<IncidentEventDTO>> partialUpdateIncidentEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IncidentEventDTO incidentEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update IncidentEvent : {}, {}", id, incidentEventDTO);
        if (incidentEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<IncidentEventDTO> result = incidentEventService.partialUpdate(incidentEventDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /incident-events} : get all the Incident Events.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Incident Events in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<IncidentEventDTO>> getAllIncidentEvents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all IncidentEvents");
        if (eagerload) {
            return incidentEventService.findAllWithEagerRelationships().collectList();
        } else {
            return incidentEventService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /incident-events} : get all the Incident Events as a stream.
     * @return the {@link Flux} of Incident Events.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<IncidentEventDTO> getAllIncidentEventsAsStream() {
        LOG.debug("REST request to get all IncidentEvents as a stream");
        return incidentEventService.findAll();
    }

    /**
     * {@code GET  /incident-events/:id} : get the "id" incidentEvent.
     *
     * @param id the id of the incidentEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incidentEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<IncidentEventDTO>> getIncidentEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IncidentEvent : {}", id);
        Mono<IncidentEventDTO> incidentEventDTO = incidentEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incidentEventDTO);
    }

    /**
     * {@code DELETE  /incident-events/:id} : delete the "id" incidentEvent.
     *
     * @param id the id of the incidentEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteIncidentEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IncidentEvent : {}", id);
        return incidentEventService
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
