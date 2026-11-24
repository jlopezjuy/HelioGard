package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.service.AuditReportService;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link ar.com.heliguard.app.domain.AuditReport}.
 */
@RestController
@RequestMapping("/api/audit-reports")
public class AuditReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditReportResource.class);

    private static final String ENTITY_NAME = "auditReport";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AuditReportService auditReportService;

    private final AuditReportRepository auditReportRepository;

    public AuditReportResource(AuditReportService auditReportService, AuditReportRepository auditReportRepository) {
        this.auditReportService = auditReportService;
        this.auditReportRepository = auditReportRepository;
    }

    /**
     * {@code POST  /audit-reports} : Create a new auditReport.
     *
     * @param auditReportDTO the auditReportDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditReportDTO, or with status {@code 400 (Bad Request)} if the auditReport already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditReportDTO>> createAuditReport(@Valid @RequestBody AuditReportDTO auditReportDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AuditReport : {}", auditReportDTO);
        if (auditReportDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditReport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditReportService.save(auditReportDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/audit-reports/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /audit-reports/:id} : Updates an existing auditReport.
     *
     * @param id the id of the auditReportDTO to save.
     * @param auditReportDTO the auditReportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReportDTO,
     * or with status {@code 400 (Bad Request)} if the auditReportDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditReportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditReportDTO>> updateAuditReport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditReportDTO auditReportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditReport : {}, {}", id, auditReportDTO);
        if (auditReportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditReportRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return auditReportService
                .update(auditReportDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /audit-reports/:id} : Partial updates given fields of an existing auditReport, field will ignore if it is null
     *
     * @param id the id of the auditReportDTO to save.
     * @param auditReportDTO the auditReportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReportDTO,
     * or with status {@code 400 (Bad Request)} if the auditReportDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditReportDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditReportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditReportDTO>> partialUpdateAuditReport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditReportDTO auditReportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuditReport : {}, {}", id, auditReportDTO);
        if (auditReportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditReportRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AuditReportDTO> result = auditReportService.partialUpdate(auditReportDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /audit-reports} : get all the Audit Reports.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Audit Reports in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AuditReportDTO>>> getAllAuditReports(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of AuditReports");
        return auditReportService
            .countAll()
            .zipWith(auditReportService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /audit-reports/:id} : get the "id" auditReport.
     *
     * @param id the id of the auditReportDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditReportDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditReportDTO>> getAuditReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditReport : {}", id);
        Mono<AuditReportDTO> auditReportDTO = auditReportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditReportDTO);
    }

    /**
     * {@code DELETE  /audit-reports/:id} : delete the "id" auditReport.
     *
     * @param id the id of the auditReportDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditReport : {}", id);
        return auditReportService
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
