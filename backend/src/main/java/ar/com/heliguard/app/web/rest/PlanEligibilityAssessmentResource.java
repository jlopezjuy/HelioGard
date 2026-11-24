package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.PlanEligibilityAssessmentRepository;
import ar.com.heliguard.app.service.PlanEligibilityAssessmentService;
import ar.com.heliguard.app.service.dto.PlanEligibilityAssessmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.PlanEligibilityAssessment}.
 */
@RestController
@RequestMapping("/api/plan-eligibility-assessments")
public class PlanEligibilityAssessmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlanEligibilityAssessmentResource.class);

    private static final String ENTITY_NAME = "planEligibilityAssessment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final PlanEligibilityAssessmentService planEligibilityAssessmentService;

    private final PlanEligibilityAssessmentRepository planEligibilityAssessmentRepository;

    public PlanEligibilityAssessmentResource(
        PlanEligibilityAssessmentService planEligibilityAssessmentService,
        PlanEligibilityAssessmentRepository planEligibilityAssessmentRepository
    ) {
        this.planEligibilityAssessmentService = planEligibilityAssessmentService;
        this.planEligibilityAssessmentRepository = planEligibilityAssessmentRepository;
    }

    /**
     * {@code POST  /plan-eligibility-assessments} : Create a new planEligibilityAssessment.
     *
     * @param planEligibilityAssessmentDTO the planEligibilityAssessmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new planEligibilityAssessmentDTO, or with status {@code 400 (Bad Request)} if the planEligibilityAssessment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PlanEligibilityAssessmentDTO>> createPlanEligibilityAssessment(
        @Valid @RequestBody PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save PlanEligibilityAssessment : {}", planEligibilityAssessmentDTO);
        if (planEligibilityAssessmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new planEligibilityAssessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return planEligibilityAssessmentService.save(planEligibilityAssessmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/plan-eligibility-assessments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /plan-eligibility-assessments/:id} : Updates an existing planEligibilityAssessment.
     *
     * @param id the id of the planEligibilityAssessmentDTO to save.
     * @param planEligibilityAssessmentDTO the planEligibilityAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planEligibilityAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the planEligibilityAssessmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the planEligibilityAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PlanEligibilityAssessmentDTO>> updatePlanEligibilityAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PlanEligibilityAssessment : {}, {}", id, planEligibilityAssessmentDTO);
        if (planEligibilityAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planEligibilityAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return planEligibilityAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return planEligibilityAssessmentService
                .update(planEligibilityAssessmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /plan-eligibility-assessments/:id} : Partial updates given fields of an existing planEligibilityAssessment, field will ignore if it is null
     *
     * @param id the id of the planEligibilityAssessmentDTO to save.
     * @param planEligibilityAssessmentDTO the planEligibilityAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planEligibilityAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the planEligibilityAssessmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the planEligibilityAssessmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the planEligibilityAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PlanEligibilityAssessmentDTO>> partialUpdatePlanEligibilityAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update PlanEligibilityAssessment : {}, {}", id, planEligibilityAssessmentDTO);
        if (planEligibilityAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planEligibilityAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return planEligibilityAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<PlanEligibilityAssessmentDTO> result = planEligibilityAssessmentService.partialUpdate(planEligibilityAssessmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /plan-eligibility-assessments} : get all the Plan Eligibility Assessments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Plan Eligibility Assessments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<PlanEligibilityAssessmentDTO>> getAllPlanEligibilityAssessments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PlanEligibilityAssessments");
        if (eagerload) {
            return planEligibilityAssessmentService.findAllWithEagerRelationships().collectList();
        } else {
            return planEligibilityAssessmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /plan-eligibility-assessments} : get all the Plan Eligibility Assessments as a stream.
     * @return the {@link Flux} of Plan Eligibility Assessments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PlanEligibilityAssessmentDTO> getAllPlanEligibilityAssessmentsAsStream() {
        LOG.debug("REST request to get all PlanEligibilityAssessments as a stream");
        return planEligibilityAssessmentService.findAll();
    }

    /**
     * {@code GET  /plan-eligibility-assessments/:id} : get the "id" planEligibilityAssessment.
     *
     * @param id the id of the planEligibilityAssessmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the planEligibilityAssessmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlanEligibilityAssessmentDTO>> getPlanEligibilityAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PlanEligibilityAssessment : {}", id);
        Mono<PlanEligibilityAssessmentDTO> planEligibilityAssessmentDTO = planEligibilityAssessmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(planEligibilityAssessmentDTO);
    }

    /**
     * {@code DELETE  /plan-eligibility-assessments/:id} : delete the "id" planEligibilityAssessment.
     *
     * @param id the id of the planEligibilityAssessmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlanEligibilityAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PlanEligibilityAssessment : {}", id);
        return planEligibilityAssessmentService
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
