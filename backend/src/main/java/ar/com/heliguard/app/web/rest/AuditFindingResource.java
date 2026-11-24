package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.service.AuditFindingService;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditFinding}.
 */
@RestController
@RequestMapping("/api/audit-findings")
public class AuditFindingResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditFindingResource.class);

    private static final String ENTITY_NAME = "auditFinding";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditFindingService auditFindingService;

    private final AuditFindingRepository auditFindingRepository;

    public AuditFindingResource(AuditFindingService auditFindingService, AuditFindingRepository auditFindingRepository) {
        this.auditFindingService = auditFindingService;
        this.auditFindingRepository = auditFindingRepository;
    }

    /**
     * {@code POST  /audit-findings} : Create a new auditFinding.
     *
     * @param auditFindingDTO the auditFindingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditFindingDTO, or with status {@code 400 (Bad Request)} if the auditFinding already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditFindingDTO>> createAuditFinding(@Valid @RequestBody AuditFindingDTO auditFindingDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditFinding : {}", auditFindingDTO);
        if (auditFindingDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditFinding cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditFindingService.save(auditFindingDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-findings/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-findings/:id} : Updates an existing auditFinding.
     *
     * @param id the id of the auditFindingDTO to save.
     * @param auditFindingDTO the auditFindingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditFindingDTO,
     * or with status {@code 400 (Bad Request)} if the auditFindingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditFindingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditFindingDTO>> updateAuditFinding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditFindingDTO auditFindingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditFinding : {}, {}", id, auditFindingDTO);
        if (auditFindingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditFindingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditFindingRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditFindingService
                .update(auditFindingDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-findings/:id} : Partial updates given fields of an existing auditFinding, field will ignore if it is null
     *
     * @param id the id of the auditFindingDTO to save.
     * @param auditFindingDTO the auditFindingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditFindingDTO,
     * or with status {@code 400 (Bad Request)} if the auditFindingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditFindingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditFindingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditFindingDTO>> partialUpdateAuditFinding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditFindingDTO auditFindingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditFinding : {}, {}", id, auditFindingDTO);
        if (auditFindingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditFindingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditFindingRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditFindingDTO> result = auditFindingService.partialUpdate(auditFindingDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-findings} : get all the Audit Findings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Findings in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditFindingDTO>> getAllAuditFindings(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditFindings");
        if (eagerload) {
            return auditFindingService.findAllWithEagerRelationships().collectList();
        } else {
            return auditFindingService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-findings} : get all the Audit Findings as a stream.
     * @return the {@link Flux} of Audit Findings.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditFindingDTO> getAllAuditFindingsAsStream() {
        LOG.debug("REST request to get all AuditFindings as a stream");
        return auditFindingService.findAll();
    }

    /**
     * {@code GET  /audit-findings/:id} : get the "id" auditFinding.
     *
     * @param id the id of the auditFindingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditFindingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditFindingDTO>> getAuditFinding(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditFinding : {}", id);
        Mono<AuditFindingDTO> auditFindingDTO = auditFindingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditFindingDTO);
    }

    /**
     * {@code DELETE  /audit-findings/:id} : delete the "id" auditFinding.
     *
     * @param id the id of the auditFindingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditFinding(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditFinding : {}", id);
        return auditFindingService
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
