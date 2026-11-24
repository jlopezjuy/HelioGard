package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.LossCauseEstimateRepository;
import ar.com.heliguard.app.service.LossCauseEstimateService;
import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.LossCauseEstimate}.
 */
@RestController
@RequestMapping("/api/loss-cause-estimates")
public class LossCauseEstimateResource {

    private static final Logger LOG = LoggerFactory.getLogger(LossCauseEstimateResource.class);

    private static final String ENTITY_NAME = "lossCauseEstimate";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final LossCauseEstimateService lossCauseEstimateService;

    private final LossCauseEstimateRepository lossCauseEstimateRepository;

    public LossCauseEstimateResource(
        LossCauseEstimateService lossCauseEstimateService,
        LossCauseEstimateRepository lossCauseEstimateRepository
    ) {
        this.lossCauseEstimateService = lossCauseEstimateService;
        this.lossCauseEstimateRepository = lossCauseEstimateRepository;
    }

    /**
     * {@code POST  /loss-cause-estimates} : Create a new lossCauseEstimate.
     *
     * @param lossCauseEstimateDTO the lossCauseEstimateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lossCauseEstimateDTO, or with status {@code 400 (Bad Request)} if the lossCauseEstimate already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<LossCauseEstimateDTO>> createLossCauseEstimate(@Valid @RequestBody LossCauseEstimateDTO lossCauseEstimateDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LossCauseEstimate : {}", lossCauseEstimateDTO);
        if (lossCauseEstimateDTO.getId() != null) {
            throw new BadRequestAlertException("A new lossCauseEstimate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return lossCauseEstimateService.save(lossCauseEstimateDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/loss-cause-estimates/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /loss-cause-estimates/:id} : Updates an existing lossCauseEstimate.
     *
     * @param id the id of the lossCauseEstimateDTO to save.
     * @param lossCauseEstimateDTO the lossCauseEstimateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lossCauseEstimateDTO,
     * or with status {@code 400 (Bad Request)} if the lossCauseEstimateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lossCauseEstimateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<LossCauseEstimateDTO>> updateLossCauseEstimate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LossCauseEstimateDTO lossCauseEstimateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LossCauseEstimate : {}, {}", id, lossCauseEstimateDTO);
        if (lossCauseEstimateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lossCauseEstimateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return lossCauseEstimateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return lossCauseEstimateService
                .update(lossCauseEstimateDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /loss-cause-estimates/:id} : Partial updates given fields of an existing lossCauseEstimate, field will ignore if it is null
     *
     * @param id the id of the lossCauseEstimateDTO to save.
     * @param lossCauseEstimateDTO the lossCauseEstimateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lossCauseEstimateDTO,
     * or with status {@code 400 (Bad Request)} if the lossCauseEstimateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lossCauseEstimateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lossCauseEstimateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<LossCauseEstimateDTO>> partialUpdateLossCauseEstimate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LossCauseEstimateDTO lossCauseEstimateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update LossCauseEstimate : {}, {}", id, lossCauseEstimateDTO);
        if (lossCauseEstimateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lossCauseEstimateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return lossCauseEstimateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<LossCauseEstimateDTO> result = lossCauseEstimateService.partialUpdate(lossCauseEstimateDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /loss-cause-estimates} : get all the Loss Cause Estimates.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Loss Cause Estimates in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<LossCauseEstimateDTO>> getAllLossCauseEstimates(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all LossCauseEstimates");
        if (eagerload) {
            return lossCauseEstimateService.findAllWithEagerRelationships().collectList();
        } else {
            return lossCauseEstimateService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /loss-cause-estimates} : get all the Loss Cause Estimates as a stream.
     * @return the {@link Flux} of Loss Cause Estimates.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<LossCauseEstimateDTO> getAllLossCauseEstimatesAsStream() {
        LOG.debug("REST request to get all LossCauseEstimates as a stream");
        return lossCauseEstimateService.findAll();
    }

    /**
     * {@code GET  /loss-cause-estimates/:id} : get the "id" lossCauseEstimate.
     *
     * @param id the id of the lossCauseEstimateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lossCauseEstimateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LossCauseEstimateDTO>> getLossCauseEstimate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LossCauseEstimate : {}", id);
        Mono<LossCauseEstimateDTO> lossCauseEstimateDTO = lossCauseEstimateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lossCauseEstimateDTO);
    }

    /**
     * {@code DELETE  /loss-cause-estimates/:id} : delete the "id" lossCauseEstimate.
     *
     * @param id the id of the lossCauseEstimateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLossCauseEstimate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LossCauseEstimate : {}", id);
        return lossCauseEstimateService
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
