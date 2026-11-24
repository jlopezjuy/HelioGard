package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.RecoveryAssessmentRepository;
import ar.com.heliguard.app.service.RecoveryAssessmentService;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.RecoveryAssessment}.
 */
@RestController
@RequestMapping("/api/recovery-assessments")
public class RecoveryAssessmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryAssessmentResource.class);

    private static final String ENTITY_NAME = "recoveryAssessment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final RecoveryAssessmentService recoveryAssessmentService;

    private final RecoveryAssessmentRepository recoveryAssessmentRepository;

    public RecoveryAssessmentResource(
        RecoveryAssessmentService recoveryAssessmentService,
        RecoveryAssessmentRepository recoveryAssessmentRepository
    ) {
        this.recoveryAssessmentService = recoveryAssessmentService;
        this.recoveryAssessmentRepository = recoveryAssessmentRepository;
    }

    /**
     * {@code POST  /recovery-assessments} : Create a new recoveryAssessment.
     *
     * @param recoveryAssessmentDTO the recoveryAssessmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recoveryAssessmentDTO, or with status {@code 400 (Bad Request)} if the recoveryAssessment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RecoveryAssessmentDTO>> createRecoveryAssessment(
        @Valid @RequestBody RecoveryAssessmentDTO recoveryAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save RecoveryAssessment : {}", recoveryAssessmentDTO);
        if (recoveryAssessmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new recoveryAssessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return recoveryAssessmentService.save(recoveryAssessmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/recovery-assessments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /recovery-assessments/:id} : Updates an existing recoveryAssessment.
     *
     * @param id the id of the recoveryAssessmentDTO to save.
     * @param recoveryAssessmentDTO the recoveryAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recoveryAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the recoveryAssessmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recoveryAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RecoveryAssessmentDTO>> updateRecoveryAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RecoveryAssessmentDTO recoveryAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RecoveryAssessment : {}, {}", id, recoveryAssessmentDTO);
        if (recoveryAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recoveryAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return recoveryAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return recoveryAssessmentService
                .update(recoveryAssessmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /recovery-assessments/:id} : Partial updates given fields of an existing recoveryAssessment, field will ignore if it is null
     *
     * @param id the id of the recoveryAssessmentDTO to save.
     * @param recoveryAssessmentDTO the recoveryAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recoveryAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the recoveryAssessmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recoveryAssessmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recoveryAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RecoveryAssessmentDTO>> partialUpdateRecoveryAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RecoveryAssessmentDTO recoveryAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update RecoveryAssessment : {}, {}", id, recoveryAssessmentDTO);
        if (recoveryAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recoveryAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return recoveryAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<RecoveryAssessmentDTO> result = recoveryAssessmentService.partialUpdate(recoveryAssessmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /recovery-assessments} : get all the Recovery Assessments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Recovery Assessments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<RecoveryAssessmentDTO>> getAllRecoveryAssessments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RecoveryAssessments");
        if (eagerload) {
            return recoveryAssessmentService.findAllWithEagerRelationships().collectList();
        } else {
            return recoveryAssessmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /recovery-assessments} : get all the Recovery Assessments as a stream.
     * @return the {@link Flux} of Recovery Assessments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RecoveryAssessmentDTO> getAllRecoveryAssessmentsAsStream() {
        LOG.debug("REST request to get all RecoveryAssessments as a stream");
        return recoveryAssessmentService.findAll();
    }

    /**
     * {@code GET  /recovery-assessments/:id} : get the "id" recoveryAssessment.
     *
     * @param id the id of the recoveryAssessmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recoveryAssessmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RecoveryAssessmentDTO>> getRecoveryAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RecoveryAssessment : {}", id);
        Mono<RecoveryAssessmentDTO> recoveryAssessmentDTO = recoveryAssessmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recoveryAssessmentDTO);
    }

    /**
     * {@code DELETE  /recovery-assessments/:id} : delete the "id" recoveryAssessment.
     *
     * @param id the id of the recoveryAssessmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRecoveryAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RecoveryAssessment : {}", id);
        return recoveryAssessmentService
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
