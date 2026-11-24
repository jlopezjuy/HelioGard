package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.IncidentService;
import ar.com.heliguard.app.service.dto.IncidentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.Incident}.
 */
@RestController
@RequestMapping("/api/incidents")
public class IncidentResource {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentResource.class);

    private static final String ENTITY_NAME = "incident";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final IncidentService incidentService;

    private final IncidentRepository incidentRepository;

    public IncidentResource(IncidentService incidentService, IncidentRepository incidentRepository) {
        this.incidentService = incidentService;
        this.incidentRepository = incidentRepository;
    }

    /**
     * {@code POST  /incidents} : Create a new incident.
     *
     * @param incidentDTO the incidentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incidentDTO, or with status {@code 400 (Bad Request)} if the incident already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<IncidentDTO>> createIncident(@Valid @RequestBody IncidentDTO incidentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Incident : {}", incidentDTO);
        if (incidentDTO.getId() != null) {
            throw new BadRequestAlertException("A new incident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return incidentService.save(incidentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/incidents/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /incidents/:id} : Updates an existing incident.
     *
     * @param id the id of the incidentDTO to save.
     * @param incidentDTO the incidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentDTO,
     * or with status {@code 400 (Bad Request)} if the incidentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<IncidentDTO>> updateIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IncidentDTO incidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Incident : {}, {}", id, incidentDTO);
        if (incidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return incidentService
                .update(incidentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /incidents/:id} : Partial updates given fields of an existing incident, field will ignore if it is null
     *
     * @param id the id of the incidentDTO to save.
     * @param incidentDTO the incidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentDTO,
     * or with status {@code 400 (Bad Request)} if the incidentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the incidentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the incidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<IncidentDTO>> partialUpdateIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IncidentDTO incidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Incident : {}, {}", id, incidentDTO);
        if (incidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<IncidentDTO> result = incidentService.partialUpdate(incidentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /incidents} : get all the Incidents.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Incidents in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<IncidentDTO>>> getAllIncidents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Incidents");
        return incidentService
            .countAll()
            .zipWith(incidentService.findAll(pageable).collectList())
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
     * {@code GET  /incidents/:id} : get the "id" incident.
     *
     * @param id the id of the incidentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incidentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<IncidentDTO>> getIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Incident : {}", id);
        Mono<IncidentDTO> incidentDTO = incidentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incidentDTO);
    }

    /**
     * {@code DELETE  /incidents/:id} : delete the "id" incident.
     *
     * @param id the id of the incidentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Incident : {}", id);
        return incidentService
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
