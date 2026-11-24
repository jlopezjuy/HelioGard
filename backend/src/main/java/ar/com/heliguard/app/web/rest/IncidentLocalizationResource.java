package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.IncidentLocalizationRepository;
import ar.com.heliguard.app.service.IncidentLocalizationService;
import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.IncidentLocalization}.
 */
@RestController
@RequestMapping("/api/incident-localizations")
public class IncidentLocalizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentLocalizationResource.class);

    private static final String ENTITY_NAME = "incidentLocalization";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final IncidentLocalizationService incidentLocalizationService;

    private final IncidentLocalizationRepository incidentLocalizationRepository;

    public IncidentLocalizationResource(
        IncidentLocalizationService incidentLocalizationService,
        IncidentLocalizationRepository incidentLocalizationRepository
    ) {
        this.incidentLocalizationService = incidentLocalizationService;
        this.incidentLocalizationRepository = incidentLocalizationRepository;
    }

    /**
     * {@code POST  /incident-localizations} : Create a new incidentLocalization.
     *
     * @param incidentLocalizationDTO the incidentLocalizationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new incidentLocalizationDTO, or with status {@code 400 (Bad Request)} if the incidentLocalization already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<IncidentLocalizationDTO>> createIncidentLocalization(
        @Valid @RequestBody IncidentLocalizationDTO incidentLocalizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save IncidentLocalization : {}", incidentLocalizationDTO);
        if (incidentLocalizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new incidentLocalization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return incidentLocalizationService.save(incidentLocalizationDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/incident-localizations/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /incident-localizations/:id} : Updates an existing incidentLocalization.
     *
     * @param id the id of the incidentLocalizationDTO to save.
     * @param incidentLocalizationDTO the incidentLocalizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentLocalizationDTO,
     * or with status {@code 400 (Bad Request)} if the incidentLocalizationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the incidentLocalizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<IncidentLocalizationDTO>> updateIncidentLocalization(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IncidentLocalizationDTO incidentLocalizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IncidentLocalization : {}, {}", id, incidentLocalizationDTO);
        if (incidentLocalizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentLocalizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentLocalizationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return incidentLocalizationService
                .update(incidentLocalizationDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /incident-localizations/:id} : Partial updates given fields of an existing incidentLocalization, field will ignore if it is null
     *
     * @param id the id of the incidentLocalizationDTO to save.
     * @param incidentLocalizationDTO the incidentLocalizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated incidentLocalizationDTO,
     * or with status {@code 400 (Bad Request)} if the incidentLocalizationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the incidentLocalizationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the incidentLocalizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<IncidentLocalizationDTO>> partialUpdateIncidentLocalization(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IncidentLocalizationDTO incidentLocalizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update IncidentLocalization : {}, {}", id, incidentLocalizationDTO);
        if (incidentLocalizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, incidentLocalizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return incidentLocalizationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<IncidentLocalizationDTO> result = incidentLocalizationService.partialUpdate(incidentLocalizationDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /incident-localizations} : get all the Incident Localizations.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Incident Localizations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<IncidentLocalizationDTO>> getAllIncidentLocalizations(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all IncidentLocalizations");
        if (eagerload) {
            return incidentLocalizationService.findAllWithEagerRelationships().collectList();
        } else {
            return incidentLocalizationService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /incident-localizations} : get all the Incident Localizations as a stream.
     * @return the {@link Flux} of Incident Localizations.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<IncidentLocalizationDTO> getAllIncidentLocalizationsAsStream() {
        LOG.debug("REST request to get all IncidentLocalizations as a stream");
        return incidentLocalizationService.findAll();
    }

    /**
     * {@code GET  /incident-localizations/:id} : get the "id" incidentLocalization.
     *
     * @param id the id of the incidentLocalizationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the incidentLocalizationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<IncidentLocalizationDTO>> getIncidentLocalization(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IncidentLocalization : {}", id);
        Mono<IncidentLocalizationDTO> incidentLocalizationDTO = incidentLocalizationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(incidentLocalizationDTO);
    }

    /**
     * {@code DELETE  /incident-localizations/:id} : delete the "id" incidentLocalization.
     *
     * @param id the id of the incidentLocalizationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteIncidentLocalization(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IncidentLocalization : {}", id);
        return incidentLocalizationService
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
