package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.service.AlertRuleService;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.AlertRule}.
 */
@RestController
@RequestMapping("/api/alert-rules")
public class AlertRuleResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlertRuleResource.class);

    private static final String ENTITY_NAME = "alertRule";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final AlertRuleService alertRuleService;

    private final AlertRuleRepository alertRuleRepository;

    public AlertRuleResource(AlertRuleService alertRuleService, AlertRuleRepository alertRuleRepository) {
        this.alertRuleService = alertRuleService;
        this.alertRuleRepository = alertRuleRepository;
    }

    /**
     * {@code POST  /alert-rules} : Create a new alertRule.
     *
     * @param alertRuleDTO the alertRuleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alertRuleDTO, or with status {@code 400 (Bad Request)} if the alertRule already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AlertRuleDTO>> createAlertRule(@Valid @RequestBody AlertRuleDTO alertRuleDTO) throws URISyntaxException {
        LOG.debug("REST request to save AlertRule : {}", alertRuleDTO);
        if (alertRuleDTO.getId() != null) {
            throw new BadRequestAlertException("A new alertRule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return alertRuleService.save(alertRuleDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/alert-rules/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /alert-rules/:id} : Updates an existing alertRule.
     *
     * @param id the id of the alertRuleDTO to save.
     * @param alertRuleDTO the alertRuleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertRuleDTO,
     * or with status {@code 400 (Bad Request)} if the alertRuleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alertRuleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AlertRuleDTO>> updateAlertRule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlertRuleDTO alertRuleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AlertRule : {}, {}", id, alertRuleDTO);
        if (alertRuleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertRuleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return alertRuleRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return alertRuleService
                .update(alertRuleDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /alert-rules/:id} : Partial updates given fields of an existing alertRule, field will ignore if it is null
     *
     * @param id the id of the alertRuleDTO to save.
     * @param alertRuleDTO the alertRuleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alertRuleDTO,
     * or with status {@code 400 (Bad Request)} if the alertRuleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the alertRuleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the alertRuleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AlertRuleDTO>> partialUpdateAlertRule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlertRuleDTO alertRuleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AlertRule : {}, {}", id, alertRuleDTO);
        if (alertRuleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alertRuleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return alertRuleRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<AlertRuleDTO> result = alertRuleService.partialUpdate(alertRuleDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /alert-rules} : get all the Alert Rules.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Alert Rules in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<AlertRuleDTO>> getAllAlertRules(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AlertRules");
        if (eagerload) {
            return alertRuleService.findAllWithEagerRelationships().collectList();
        } else {
            return alertRuleService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /alert-rules} : get all the Alert Rules as a stream.
     * @return the {@link Flux} of Alert Rules.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AlertRuleDTO> getAllAlertRulesAsStream() {
        LOG.debug("REST request to get all AlertRules as a stream");
        return alertRuleService.findAll();
    }

    /**
     * {@code GET  /alert-rules/:id} : get the "id" alertRule.
     *
     * @param id the id of the alertRuleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alertRuleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AlertRuleDTO>> getAlertRule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AlertRule : {}", id);
        Mono<AlertRuleDTO> alertRuleDTO = alertRuleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(alertRuleDTO);
    }

    /**
     * {@code DELETE  /alert-rules/:id} : delete the "id" alertRule.
     *
     * @param id the id of the alertRuleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAlertRule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AlertRule : {}", id);
        return alertRuleService
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
