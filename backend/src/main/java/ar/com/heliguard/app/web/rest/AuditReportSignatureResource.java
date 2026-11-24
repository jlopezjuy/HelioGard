package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditReportSignatureRepository;
import ar.com.heliguard.app.service.AuditReportSignatureService;
import ar.com.heliguard.app.service.dto.AuditReportSignatureDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditReportSignature}.
 */
@RestController
@RequestMapping("/api/audit-report-signatures")
public class AuditReportSignatureResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditReportSignatureResource.class);

    private static final String ENTITY_NAME = "auditReportSignature";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditReportSignatureService auditReportSignatureService;

    private final AuditReportSignatureRepository auditReportSignatureRepository;

    public AuditReportSignatureResource(
        AuditReportSignatureService auditReportSignatureService,
        AuditReportSignatureRepository auditReportSignatureRepository
    ) {
        this.auditReportSignatureService = auditReportSignatureService;
        this.auditReportSignatureRepository = auditReportSignatureRepository;
    }

    /**
     * {@code POST  /audit-report-signatures} : Create a new auditReportSignature.
     *
     * @param auditReportSignatureDTO the auditReportSignatureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditReportSignatureDTO, or with status {@code 400 (Bad Request)} if the auditReportSignature already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditReportSignatureDTO>> createAuditReportSignature(
        @Valid @RequestBody AuditReportSignatureDTO auditReportSignatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AuditReportSignature : {}", auditReportSignatureDTO);
        if (auditReportSignatureDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditReportSignature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditReportSignatureService.save(auditReportSignatureDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-report-signatures/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-report-signatures/:id} : Updates an existing auditReportSignature.
     *
     * @param id the id of the auditReportSignatureDTO to save.
     * @param auditReportSignatureDTO the auditReportSignatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReportSignatureDTO,
     * or with status {@code 400 (Bad Request)} if the auditReportSignatureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditReportSignatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditReportSignatureDTO>> updateAuditReportSignature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditReportSignatureDTO auditReportSignatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditReportSignature : {}, {}", id, auditReportSignatureDTO);
        if (auditReportSignatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReportSignatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditReportSignatureRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditReportSignatureService
                .update(auditReportSignatureDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-report-signatures/:id} : Partial updates given fields of an existing auditReportSignature, field will ignore if it is null
     *
     * @param id the id of the auditReportSignatureDTO to save.
     * @param auditReportSignatureDTO the auditReportSignatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReportSignatureDTO,
     * or with status {@code 400 (Bad Request)} if the auditReportSignatureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditReportSignatureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditReportSignatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditReportSignatureDTO>> partialUpdateAuditReportSignature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditReportSignatureDTO auditReportSignatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditReportSignature : {}, {}", id, auditReportSignatureDTO);
        if (auditReportSignatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReportSignatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditReportSignatureRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditReportSignatureDTO> result = auditReportSignatureService.partialUpdate(auditReportSignatureDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-report-signatures} : get all the Audit Report Signatures.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Report Signatures in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditReportSignatureDTO>> getAllAuditReportSignatures(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditReportSignatures");
        if (eagerload) {
            return auditReportSignatureService.findAllWithEagerRelationships().collectList();
        } else {
            return auditReportSignatureService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-report-signatures} : get all the Audit Report Signatures as a stream.
     * @return the {@link Flux} of Audit Report Signatures.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditReportSignatureDTO> getAllAuditReportSignaturesAsStream() {
        LOG.debug("REST request to get all AuditReportSignatures as a stream");
        return auditReportSignatureService.findAll();
    }

    /**
     * {@code GET  /audit-report-signatures/:id} : get the "id" auditReportSignature.
     *
     * @param id the id of the auditReportSignatureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditReportSignatureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditReportSignatureDTO>> getAuditReportSignature(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditReportSignature : {}", id);
        Mono<AuditReportSignatureDTO> auditReportSignatureDTO = auditReportSignatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditReportSignatureDTO);
    }

    /**
     * {@code DELETE  /audit-report-signatures/:id} : delete the "id" auditReportSignature.
     *
     * @param id the id of the auditReportSignatureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditReportSignature(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditReportSignature : {}", id);
        return auditReportSignatureService
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
