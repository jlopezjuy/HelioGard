package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditTemplateService;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditTemplate}.
 */
@RestController
@RequestMapping("/api/audit-templates")
public class AuditTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTemplateResource.class);

    private static final String ENTITY_NAME = "auditTemplate";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditTemplateService auditTemplateService;

    private final AuditTemplateRepository auditTemplateRepository;

    public AuditTemplateResource(AuditTemplateService auditTemplateService, AuditTemplateRepository auditTemplateRepository) {
        this.auditTemplateService = auditTemplateService;
        this.auditTemplateRepository = auditTemplateRepository;
    }

    /**
     * {@code POST  /audit-templates} : Create a new auditTemplate.
     *
     * @param auditTemplateDTO the auditTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditTemplateDTO, or with status {@code 400 (Bad Request)} if the auditTemplate already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditTemplateDTO>> createAuditTemplate(@Valid @RequestBody AuditTemplateDTO auditTemplateDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditTemplate : {}", auditTemplateDTO);
        if (auditTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditTemplateService.save(auditTemplateDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-templates/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-templates/:id} : Updates an existing auditTemplate.
     *
     * @param id the id of the auditTemplateDTO to save.
     * @param auditTemplateDTO the auditTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the auditTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditTemplateDTO>> updateAuditTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditTemplateDTO auditTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditTemplate : {}, {}", id, auditTemplateDTO);
        if (auditTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditTemplateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditTemplateService
                .update(auditTemplateDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-templates/:id} : Partial updates given fields of an existing auditTemplate, field will ignore if it is null
     *
     * @param id the id of the auditTemplateDTO to save.
     * @param auditTemplateDTO the auditTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the auditTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditTemplateDTO>> partialUpdateAuditTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditTemplateDTO auditTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditTemplate : {}, {}", id, auditTemplateDTO);
        if (auditTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditTemplateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditTemplateDTO> result = auditTemplateService.partialUpdate(auditTemplateDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-templates} : get all the Audit Templates.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Templates in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AuditTemplateDTO>> getAllAuditTemplates(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AuditTemplates");
        if (eagerload) {
            return auditTemplateService.findAllWithEagerRelationships().collectList();
        } else {
            return auditTemplateService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /audit-templates} : get all the Audit Templates as a stream.
     * @return the {@link Flux} of Audit Templates.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuditTemplateDTO> getAllAuditTemplatesAsStream() {
        LOG.debug("REST request to get all AuditTemplates as a stream");
        return auditTemplateService.findAll();
    }

    /**
     * {@code GET  /audit-templates/:id} : get the "id" auditTemplate.
     *
     * @param id the id of the auditTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditTemplateDTO>> getAuditTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditTemplate : {}", id);
        Mono<AuditTemplateDTO> auditTemplateDTO = auditTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditTemplateDTO);
    }

    /**
     * {@code DELETE  /audit-templates/:id} : delete the "id" auditTemplate.
     *
     * @param id the id of the auditTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditTemplate : {}", id);
        return auditTemplateService
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
