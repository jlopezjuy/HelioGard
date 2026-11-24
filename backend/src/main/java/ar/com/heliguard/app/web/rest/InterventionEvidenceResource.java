package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.InterventionEvidenceRepository;
import ar.com.heliguard.app.service.InterventionEvidenceService;
import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.InterventionEvidence}.
 */
@RestController
@RequestMapping("/api/intervention-evidences")
public class InterventionEvidenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(InterventionEvidenceResource.class);

    private static final String ENTITY_NAME = "interventionEvidence";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final InterventionEvidenceService interventionEvidenceService;

    private final InterventionEvidenceRepository interventionEvidenceRepository;

    public InterventionEvidenceResource(
        InterventionEvidenceService interventionEvidenceService,
        InterventionEvidenceRepository interventionEvidenceRepository
    ) {
        this.interventionEvidenceService = interventionEvidenceService;
        this.interventionEvidenceRepository = interventionEvidenceRepository;
    }

    /**
     * {@code POST  /intervention-evidences} : Create a new interventionEvidence.
     *
     * @param interventionEvidenceDTO the interventionEvidenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interventionEvidenceDTO, or with status {@code 400 (Bad Request)} if the interventionEvidence already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InterventionEvidenceDTO>> createInterventionEvidence(
        @Valid @RequestBody InterventionEvidenceDTO interventionEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save InterventionEvidence : {}", interventionEvidenceDTO);
        if (interventionEvidenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new interventionEvidence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return interventionEvidenceService.save(interventionEvidenceDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/intervention-evidences/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /intervention-evidences/:id} : Updates an existing interventionEvidence.
     *
     * @param id the id of the interventionEvidenceDTO to save.
     * @param interventionEvidenceDTO the interventionEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interventionEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the interventionEvidenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interventionEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InterventionEvidenceDTO>> updateInterventionEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InterventionEvidenceDTO interventionEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InterventionEvidence : {}, {}", id, interventionEvidenceDTO);
        if (interventionEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interventionEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interventionEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return interventionEvidenceService
                .update(interventionEvidenceDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /intervention-evidences/:id} : Partial updates given fields of an existing interventionEvidence, field will ignore if it is null
     *
     * @param id the id of the interventionEvidenceDTO to save.
     * @param interventionEvidenceDTO the interventionEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interventionEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the interventionEvidenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the interventionEvidenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the interventionEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InterventionEvidenceDTO>> partialUpdateInterventionEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InterventionEvidenceDTO interventionEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update InterventionEvidence : {}, {}", id, interventionEvidenceDTO);
        if (interventionEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interventionEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interventionEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<InterventionEvidenceDTO> result = interventionEvidenceService.partialUpdate(interventionEvidenceDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /intervention-evidences} : get all the Intervention Evidences.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Intervention Evidences in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<InterventionEvidenceDTO>> getAllInterventionEvidences(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InterventionEvidences");
        if (eagerload) {
            return interventionEvidenceService.findAllWithEagerRelationships().collectList();
        } else {
            return interventionEvidenceService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /intervention-evidences} : get all the Intervention Evidences as a stream.
     * @return the {@link Flux} of Intervention Evidences.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<InterventionEvidenceDTO> getAllInterventionEvidencesAsStream() {
        LOG.debug("REST request to get all InterventionEvidences as a stream");
        return interventionEvidenceService.findAll();
    }

    /**
     * {@code GET  /intervention-evidences/:id} : get the "id" interventionEvidence.
     *
     * @param id the id of the interventionEvidenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interventionEvidenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InterventionEvidenceDTO>> getInterventionEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InterventionEvidence : {}", id);
        Mono<InterventionEvidenceDTO> interventionEvidenceDTO = interventionEvidenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(interventionEvidenceDTO);
    }

    /**
     * {@code DELETE  /intervention-evidences/:id} : delete the "id" interventionEvidence.
     *
     * @param id the id of the interventionEvidenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInterventionEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InterventionEvidence : {}", id);
        return interventionEvidenceService
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
