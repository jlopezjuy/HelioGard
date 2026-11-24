package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.IntegrityAssessmentRepository;
import ar.com.heliguard.app.service.IntegrityAssessmentService;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.IntegrityAssessment}.
 */
@RestController
@RequestMapping("/api/integrity-assessments")
public class IntegrityAssessmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrityAssessmentResource.class);

    private static final String ENTITY_NAME = "integrityAssessment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final IntegrityAssessmentService integrityAssessmentService;

    private final IntegrityAssessmentRepository integrityAssessmentRepository;

    public IntegrityAssessmentResource(
        IntegrityAssessmentService integrityAssessmentService,
        IntegrityAssessmentRepository integrityAssessmentRepository
    ) {
        this.integrityAssessmentService = integrityAssessmentService;
        this.integrityAssessmentRepository = integrityAssessmentRepository;
    }

    /**
     * {@code POST  /integrity-assessments} : Create a new integrityAssessment.
     *
     * @param integrityAssessmentDTO the integrityAssessmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new integrityAssessmentDTO, or with status {@code 400 (Bad Request)} if the integrityAssessment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<IntegrityAssessmentDTO>> createIntegrityAssessment(
        @Valid @RequestBody IntegrityAssessmentDTO integrityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save IntegrityAssessment : {}", integrityAssessmentDTO);
        if (integrityAssessmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new integrityAssessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return integrityAssessmentService.save(integrityAssessmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/integrity-assessments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /integrity-assessments/:id} : Updates an existing integrityAssessment.
     *
     * @param id the id of the integrityAssessmentDTO to save.
     * @param integrityAssessmentDTO the integrityAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated integrityAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the integrityAssessmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the integrityAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<IntegrityAssessmentDTO>> updateIntegrityAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntegrityAssessmentDTO integrityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntegrityAssessment : {}, {}", id, integrityAssessmentDTO);
        if (integrityAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrityAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return integrityAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return integrityAssessmentService
                .update(integrityAssessmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /integrity-assessments/:id} : Partial updates given fields of an existing integrityAssessment, field will ignore if it is null
     *
     * @param id the id of the integrityAssessmentDTO to save.
     * @param integrityAssessmentDTO the integrityAssessmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated integrityAssessmentDTO,
     * or with status {@code 400 (Bad Request)} if the integrityAssessmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the integrityAssessmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the integrityAssessmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<IntegrityAssessmentDTO>> partialUpdateIntegrityAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntegrityAssessmentDTO integrityAssessmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update IntegrityAssessment : {}, {}", id, integrityAssessmentDTO);
        if (integrityAssessmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrityAssessmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return integrityAssessmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<IntegrityAssessmentDTO> result = integrityAssessmentService.partialUpdate(integrityAssessmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /integrity-assessments} : get all the Integrity Assessments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Integrity Assessments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<IntegrityAssessmentDTO>> getAllIntegrityAssessments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all IntegrityAssessments");
        if (eagerload) {
            return integrityAssessmentService.findAllWithEagerRelationships().collectList();
        } else {
            return integrityAssessmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /integrity-assessments} : get all the Integrity Assessments as a stream.
     * @return the {@link Flux} of Integrity Assessments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<IntegrityAssessmentDTO> getAllIntegrityAssessmentsAsStream() {
        LOG.debug("REST request to get all IntegrityAssessments as a stream");
        return integrityAssessmentService.findAll();
    }

    /**
     * {@code GET  /integrity-assessments/:id} : get the "id" integrityAssessment.
     *
     * @param id the id of the integrityAssessmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the integrityAssessmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<IntegrityAssessmentDTO>> getIntegrityAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntegrityAssessment : {}", id);
        Mono<IntegrityAssessmentDTO> integrityAssessmentDTO = integrityAssessmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integrityAssessmentDTO);
    }

    /**
     * {@code DELETE  /integrity-assessments/:id} : delete the "id" integrityAssessment.
     *
     * @param id the id of the integrityAssessmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteIntegrityAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntegrityAssessment : {}", id);
        return integrityAssessmentService
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
