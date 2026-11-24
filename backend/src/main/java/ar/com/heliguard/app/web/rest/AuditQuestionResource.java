package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.service.AuditQuestionService;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditQuestion}.
 */
@RestController
@RequestMapping("/api/audit-questions")
public class AuditQuestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditQuestionResource.class);

    private static final String ENTITY_NAME = "auditQuestion";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditQuestionService auditQuestionService;

    private final AuditQuestionRepository auditQuestionRepository;

    public AuditQuestionResource(AuditQuestionService auditQuestionService, AuditQuestionRepository auditQuestionRepository) {
        this.auditQuestionService = auditQuestionService;
        this.auditQuestionRepository = auditQuestionRepository;
    }

    /**
     * {@code POST  /audit-questions} : Create a new auditQuestion.
     *
     * @param auditQuestionDTO the auditQuestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditQuestionDTO, or with status {@code 400 (Bad Request)} if the auditQuestion already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditQuestionDTO>> createAuditQuestion(@Valid @RequestBody AuditQuestionDTO auditQuestionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditQuestion : {}", auditQuestionDTO);
        if (auditQuestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditQuestionService.save(auditQuestionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-questions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-questions/:id} : Updates an existing auditQuestion.
     *
     * @param id the id of the auditQuestionDTO to save.
     * @param auditQuestionDTO the auditQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the auditQuestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditQuestionDTO>> updateAuditQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditQuestionDTO auditQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditQuestion : {}, {}", id, auditQuestionDTO);
        if (auditQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditQuestionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditQuestionService
                .update(auditQuestionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-questions/:id} : Partial updates given fields of an existing auditQuestion, field will ignore if it is null
     *
     * @param id the id of the auditQuestionDTO to save.
     * @param auditQuestionDTO the auditQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the auditQuestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditQuestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditQuestionDTO>> partialUpdateAuditQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditQuestionDTO auditQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditQuestion : {}, {}", id, auditQuestionDTO);
        if (auditQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditQuestionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditQuestionDTO> result = auditQuestionService.partialUpdate(auditQuestionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-questions} : get all the Audit Questions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Questions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditQuestionDTO>> getAllAuditQuestions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditQuestions");
        if (eagerload) {
            return auditQuestionService.findAllWithEagerRelationships().collectList();
        } else {
            return auditQuestionService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-questions} : get all the Audit Questions as a stream.
     * @return the {@link Flux} of Audit Questions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditQuestionDTO> getAllAuditQuestionsAsStream() {
        LOG.debug("REST request to get all AuditQuestions as a stream");
        return auditQuestionService.findAll();
    }

    /**
     * {@code GET  /audit-questions/:id} : get the "id" auditQuestion.
     *
     * @param id the id of the auditQuestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditQuestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditQuestionDTO>> getAuditQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditQuestion : {}", id);
        Mono<AuditQuestionDTO> auditQuestionDTO = auditQuestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditQuestionDTO);
    }

    /**
     * {@code DELETE  /audit-questions/:id} : delete the "id" auditQuestion.
     *
     * @param id the id of the auditQuestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditQuestion : {}", id);
        return auditQuestionService
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
