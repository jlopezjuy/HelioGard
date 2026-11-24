package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.service.AuditEvidenceService;
import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditEvidence}.
 */
@RestController
@RequestMapping("/api/audit-evidences")
public class AuditEvidenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditEvidenceResource.class);

    private static final String ENTITY_NAME = "auditEvidence";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditEvidenceService auditEvidenceService;

    private final AuditEvidenceRepository auditEvidenceRepository;

    public AuditEvidenceResource(AuditEvidenceService auditEvidenceService, AuditEvidenceRepository auditEvidenceRepository) {
        this.auditEvidenceService = auditEvidenceService;
        this.auditEvidenceRepository = auditEvidenceRepository;
    }

    /**
     * {@code POST  /audit-evidences} : Create a new auditEvidence.
     *
     * @param auditEvidenceDTO the auditEvidenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditEvidenceDTO, or with status {@code 400 (Bad Request)} if the auditEvidence already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditEvidenceDTO>> createAuditEvidence(@Valid @RequestBody AuditEvidenceDTO auditEvidenceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditEvidence : {}", auditEvidenceDTO);
        if (auditEvidenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditEvidence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditEvidenceService.save(auditEvidenceDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-evidences/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-evidences/:id} : Updates an existing auditEvidence.
     *
     * @param id the id of the auditEvidenceDTO to save.
     * @param auditEvidenceDTO the auditEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the auditEvidenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditEvidenceDTO>> updateAuditEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditEvidenceDTO auditEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditEvidence : {}, {}", id, auditEvidenceDTO);
        if (auditEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditEvidenceService
                .update(auditEvidenceDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-evidences/:id} : Partial updates given fields of an existing auditEvidence, field will ignore if it is null
     *
     * @param id the id of the auditEvidenceDTO to save.
     * @param auditEvidenceDTO the auditEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the auditEvidenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditEvidenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditEvidenceDTO>> partialUpdateAuditEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditEvidenceDTO auditEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditEvidence : {}, {}", id, auditEvidenceDTO);
        if (auditEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditEvidenceDTO> result = auditEvidenceService.partialUpdate(auditEvidenceDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-evidences} : get all the Audit Evidences.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Evidences in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditEvidenceDTO>> getAllAuditEvidences(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditEvidences");
        if (eagerload) {
            return auditEvidenceService.findAllWithEagerRelationships().collectList();
        } else {
            return auditEvidenceService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-evidences} : get all the Audit Evidences as a stream.
     * @return the {@link Flux} of Audit Evidences.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditEvidenceDTO> getAllAuditEvidencesAsStream() {
        LOG.debug("REST request to get all AuditEvidences as a stream");
        return auditEvidenceService.findAll();
    }

    /**
     * {@code GET  /audit-evidences/:id} : get the "id" auditEvidence.
     *
     * @param id the id of the auditEvidenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditEvidenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditEvidenceDTO>> getAuditEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditEvidence : {}", id);
        Mono<AuditEvidenceDTO> auditEvidenceDTO = auditEvidenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditEvidenceDTO);
    }

    /**
     * {@code DELETE  /audit-evidences/:id} : delete the "id" auditEvidence.
     *
     * @param id the id of the auditEvidenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditEvidence : {}", id);
        return auditEvidenceService
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
