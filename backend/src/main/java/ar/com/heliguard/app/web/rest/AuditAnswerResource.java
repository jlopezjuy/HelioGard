package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditAnswerRepository;
import ar.com.heliguard.app.service.AuditAnswerService;
import ar.com.heliguard.app.service.dto.AuditAnswerDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditAnswer}.
 */
@RestController
@RequestMapping("/api/audit-answers")
public class AuditAnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditAnswerResource.class);

    private static final String ENTITY_NAME = "auditAnswer";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditAnswerService auditAnswerService;

    private final AuditAnswerRepository auditAnswerRepository;

    public AuditAnswerResource(AuditAnswerService auditAnswerService, AuditAnswerRepository auditAnswerRepository) {
        this.auditAnswerService = auditAnswerService;
        this.auditAnswerRepository = auditAnswerRepository;
    }

    /**
     * {@code POST  /audit-answers} : Create a new auditAnswer.
     *
     * @param auditAnswerDTO the auditAnswerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditAnswerDTO, or with status {@code 400 (Bad Request)} if the auditAnswer already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditAnswerDTO>> createAuditAnswer(@Valid @RequestBody AuditAnswerDTO auditAnswerDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditAnswer : {}", auditAnswerDTO);
        if (auditAnswerDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditAnswerService.save(auditAnswerDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-answers/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-answers/:id} : Updates an existing auditAnswer.
     *
     * @param id the id of the auditAnswerDTO to save.
     * @param auditAnswerDTO the auditAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the auditAnswerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditAnswerDTO>> updateAuditAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditAnswerDTO auditAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditAnswer : {}, {}", id, auditAnswerDTO);
        if (auditAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditAnswerRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditAnswerService
                .update(auditAnswerDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-answers/:id} : Partial updates given fields of an existing auditAnswer, field will ignore if it is null
     *
     * @param id the id of the auditAnswerDTO to save.
     * @param auditAnswerDTO the auditAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the auditAnswerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditAnswerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditAnswerDTO>> partialUpdateAuditAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditAnswerDTO auditAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditAnswer : {}, {}", id, auditAnswerDTO);
        if (auditAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditAnswerRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditAnswerDTO> result = auditAnswerService.partialUpdate(auditAnswerDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-answers} : get all the Audit Answers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Answers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditAnswerDTO>> getAllAuditAnswers(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditAnswers");
        if (eagerload) {
            return auditAnswerService.findAllWithEagerRelationships().collectList();
        } else {
            return auditAnswerService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-answers} : get all the Audit Answers as a stream.
     * @return the {@link Flux} of Audit Answers.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditAnswerDTO> getAllAuditAnswersAsStream() {
        LOG.debug("REST request to get all AuditAnswers as a stream");
        return auditAnswerService.findAll();
    }

    /**
     * {@code GET  /audit-answers/:id} : get the "id" auditAnswer.
     *
     * @param id the id of the auditAnswerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditAnswerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditAnswerDTO>> getAuditAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditAnswer : {}", id);
        Mono<AuditAnswerDTO> auditAnswerDTO = auditAnswerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditAnswerDTO);
    }

    /**
     * {@code DELETE  /audit-answers/:id} : delete the "id" auditAnswer.
     *
     * @param id the id of the auditAnswerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditAnswer : {}", id);
        return auditAnswerService
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
