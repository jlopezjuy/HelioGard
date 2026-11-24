package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.VerificationResultRepository;
import ar.com.heliguard.app.service.VerificationResultService;
import ar.com.heliguard.app.service.dto.VerificationResultDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.VerificationResult}.
 */
@RestController
@RequestMapping("/api/verification-results")
public class VerificationResultResource {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationResultResource.class);

    private static final String ENTITY_NAME = "verificationResult";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final VerificationResultService verificationResultService;

    private final VerificationResultRepository verificationResultRepository;

    public VerificationResultResource(
        VerificationResultService verificationResultService,
        VerificationResultRepository verificationResultRepository
    ) {
        this.verificationResultService = verificationResultService;
        this.verificationResultRepository = verificationResultRepository;
    }

    /**
     * {@code POST  /verification-results} : Create a new verificationResult.
     *
     * @param verificationResultDTO the verificationResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new verificationResultDTO, or with status {@code 400 (Bad Request)} if the verificationResult already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<VerificationResultDTO>> createVerificationResult(
        @Valid @RequestBody VerificationResultDTO verificationResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save VerificationResult : {}", verificationResultDTO);
        if (verificationResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new verificationResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return verificationResultService.save(verificationResultDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/verification-results/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /verification-results/:id} : Updates an existing verificationResult.
     *
     * @param id the id of the verificationResultDTO to save.
     * @param verificationResultDTO the verificationResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated verificationResultDTO,
     * or with status {@code 400 (Bad Request)} if the verificationResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the verificationResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<VerificationResultDTO>> updateVerificationResult(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VerificationResultDTO verificationResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update VerificationResult : {}, {}", id, verificationResultDTO);
        if (verificationResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, verificationResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return verificationResultRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return verificationResultService
                .update(verificationResultDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /verification-results/:id} : Partial updates given fields of an existing verificationResult, field will ignore if it is null
     *
     * @param id the id of the verificationResultDTO to save.
     * @param verificationResultDTO the verificationResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated verificationResultDTO,
     * or with status {@code 400 (Bad Request)} if the verificationResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the verificationResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the verificationResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<VerificationResultDTO>> partialUpdateVerificationResult(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VerificationResultDTO verificationResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update VerificationResult : {}, {}", id, verificationResultDTO);
        if (verificationResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, verificationResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return verificationResultRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<VerificationResultDTO> result = verificationResultService.partialUpdate(verificationResultDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /verification-results} : get all the Verification Results.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Verification Results in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<VerificationResultDTO>> getAllVerificationResults(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all VerificationResults");
        if (eagerload) {
            return verificationResultService.findAllWithEagerRelationships().collectList();
        } else {
            return verificationResultService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /verification-results} : get all the Verification Results as a stream.
     * @return the {@link Flux} of Verification Results.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<VerificationResultDTO> getAllVerificationResultsAsStream() {
        LOG.debug("REST request to get all VerificationResults as a stream");
        return verificationResultService.findAll();
    }

    /**
     * {@code GET  /verification-results/:id} : get the "id" verificationResult.
     *
     * @param id the id of the verificationResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the verificationResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<VerificationResultDTO>> getVerificationResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VerificationResult : {}", id);
        Mono<VerificationResultDTO> verificationResultDTO = verificationResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(verificationResultDTO);
    }

    /**
     * {@code DELETE  /verification-results/:id} : delete the "id" verificationResult.
     *
     * @param id the id of the verificationResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteVerificationResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VerificationResult : {}", id);
        return verificationResultService
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
