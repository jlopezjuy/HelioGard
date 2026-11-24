package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.service.AlertRuleVersionService;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AlertRuleVersion}.
 */
@RestController
@RequestMapping("/api/alert-rule-versions")
public class AlertRuleVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlertRuleVersionResource.class);

    private static final String ENTITY_NAME = "alertRuleVersion";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AlertRuleVersionService alertRuleVersionService;

    private final AlertRuleVersionRepository alertRuleVersionRepository;

    public AlertRuleVersionResource(
        AlertRuleVersionService alertRuleVersionService,
        AlertRuleVersionRepository alertRuleVersionRepository
    ) {
        this.alertRuleVersionService = alertRuleVersionService;
        this.alertRuleVersionRepository = alertRuleVersionRepository;
    }

    /**
     * {@code POST  /alert-rule-versions} : Create a new alertRuleVersion.
     *
     * @param alertRuleVersionDTO the alertRuleVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alertRuleVersionDTO, or with status {@code 400 (Bad Request)} if the alertRuleVersion already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AlertRuleVersionDTO>> createAlertRuleVersion(@Valid @RequestBody AlertRuleVersionDTO alertRuleVersionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AlertRuleVersion : {}", alertRuleVersionDTO);
        if (alertRuleVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new alertRuleVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return alertRuleVersionService.save(alertRuleVersionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/alert-rule-versions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /alert-rule-versions/:id} : Updates an existing alertRuleVersion.
     *
     * @param id the id of the alertRuleVersionDTO to save.
     * @param alertRuleVersionDTO the alertRuleVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertRuleVersionDTO,
     * or with status {@code 400 (Bad Request)} if the alertRuleVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alertRuleVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AlertRuleVersionDTO>> updateAlertRuleVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlertRuleVersionDTO alertRuleVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AlertRuleVersion : {}, {}", id, alertRuleVersionDTO);
        if (alertRuleVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertRuleVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return alertRuleVersionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return alertRuleVersionService
                .update(alertRuleVersionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /alert-rule-versions/:id} : Partial updates given fields of an existing alertRuleVersion, field will ignore if it is null
     *
     * @param id the id of the alertRuleVersionDTO to save.
     * @param alertRuleVersionDTO the alertRuleVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertRuleVersionDTO,
     * or with status {@code 400 (Bad Request)} if the alertRuleVersionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the alertRuleVersionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the alertRuleVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AlertRuleVersionDTO>> partialUpdateAlertRuleVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlertRuleVersionDTO alertRuleVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AlertRuleVersion : {}, {}", id, alertRuleVersionDTO);
        if (alertRuleVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertRuleVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return alertRuleVersionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AlertRuleVersionDTO> result = alertRuleVersionService.partialUpdate(alertRuleVersionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /alert-rule-versions} : get all the Alert Rule Versions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Alert Rule Versions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AlertRuleVersionDTO>> getAllAlertRuleVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AlertRuleVersions");
        if (eagerload) {
            return alertRuleVersionService.findAllWithEagerRelationships().collectList();
        } else {
            return alertRuleVersionService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /alert-rule-versions} : get all the Alert Rule Versions as a stream.
     * @return the {@link Flux} of Alert Rule Versions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AlertRuleVersionDTO> getAllAlertRuleVersionsAsStream() {
        LOG.debug("REST request to get all AlertRuleVersions as a stream");
        return alertRuleVersionService.findAll();
    }

    /**
     * {@code GET  /alert-rule-versions/:id} : get the "id" alertRuleVersion.
     *
     * @param id the id of the alertRuleVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alertRuleVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AlertRuleVersionDTO>> getAlertRuleVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AlertRuleVersion : {}", id);
        Mono<AlertRuleVersionDTO> alertRuleVersionDTO = alertRuleVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(alertRuleVersionDTO);
    }

    /**
     * {@code DELETE  /alert-rule-versions/:id} : delete the "id" alertRuleVersion.
     *
     * @param id the id of the alertRuleVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAlertRuleVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AlertRuleVersion : {}", id);
        return alertRuleVersionService
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
