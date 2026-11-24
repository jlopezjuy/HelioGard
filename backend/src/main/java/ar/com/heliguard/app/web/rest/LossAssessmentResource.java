package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.service.LossAssessmentService;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.LossAssessment}.
 */
@RestController
@RequestMapping("/api/loss-assessments")
public class LossAssessmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(LossAssessmentResource.class);

    private static final String ENTITY_NAME = "lossAssessment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final LossAssessmentService lossAssessmentService;

    private final LossAssessmentRepository lossAssessmentRepository;

    public LossAssessmentResource(LossAssessmentService lossAssessmentService, LossAssessmentRepository lossAssessmentRepository) {
        this.lossAssessmentService = lossAssessmentService;
        this.lossAssessmentRepository = lossAssessmentRepository;
    }

    /**
     * {@code POST  /loss-assessments} : Create a new lossAssessment.
     *
     * @param lossAssessmentDTO the lossAssessmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lossAssessmentDTO, or with status {@code 400 (Bad Request)} if the lossAssessment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<LossAssessmentDTO>> createLossAssessment(@Valid @RequestBody LossAssessmentDTO lossAssessmentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LossAssessment : {}", lossAssessmentDTO);
        if (lossAssessmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new lossAssessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return lossAssessmentService.save(lossAssessmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/loss-assessments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /loss-assessments/:id} : Updates an existing lossAssessment.
     *
     * @param id the id of the lossAssessmentDTO to save.
     * @param lossAssessmentDTO the lossAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lossAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the lossAssessmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lossAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<LossAssessmentDTO>> updateLossAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LossAssessmentDTO lossAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LossAssessment : {}, {}", id, lossAssessmentDTO);
        if (lossAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lossAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return lossAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return lossAssessmentService
                .update(lossAssessmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /loss-assessments/:id} : Partial updates given fields of an existing lossAssessment, field will ignore if it is null
     *
     * @param id the id of the lossAssessmentDTO to save.
     * @param lossAssessmentDTO the lossAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lossAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the lossAssessmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lossAssessmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lossAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<LossAssessmentDTO>> partialUpdateLossAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LossAssessmentDTO lossAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update LossAssessment : {}, {}", id, lossAssessmentDTO);
        if (lossAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lossAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return lossAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<LossAssessmentDTO> result = lossAssessmentService.partialUpdate(lossAssessmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /loss-assessments} : get all the Loss Assessments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Loss Assessments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<LossAssessmentDTO>> getAllLossAssessments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all LossAssessments");
        if (eagerload) {
            return lossAssessmentService.findAllWithEagerRelationships().collectList();
        } else {
            return lossAssessmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /loss-assessments} : get all the Loss Assessments as a stream.
     * @return the {@link Flux} of Loss Assessments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<LossAssessmentDTO> getAllLossAssessmentsAsStream() {
        LOG.debug("REST request to get all LossAssessments as a stream");
        return lossAssessmentService.findAll();
    }

    /**
     * {@code GET  /loss-assessments/:id} : get the "id" lossAssessment.
     *
     * @param id the id of the lossAssessmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lossAssessmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LossAssessmentDTO>> getLossAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LossAssessment : {}", id);
        Mono<LossAssessmentDTO> lossAssessmentDTO = lossAssessmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lossAssessmentDTO);
    }

    /**
     * {@code DELETE  /loss-assessments/:id} : delete the "id" lossAssessment.
     *
     * @param id the id of the lossAssessmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLossAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LossAssessment : {}", id);
        return lossAssessmentService
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
