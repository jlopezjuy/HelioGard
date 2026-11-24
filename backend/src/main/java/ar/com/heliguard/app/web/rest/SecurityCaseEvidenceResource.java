package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SecurityCaseEvidenceRepository;
import ar.com.heliguard.app.service.SecurityCaseEvidenceService;
import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvidence}.
 */
@RestController
@RequestMapping("/api/security-case-evidences")
public class SecurityCaseEvidenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityCaseEvidenceResource.class);

    private static final String ENTITY_NAME = "securityCaseEvidence";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SecurityCaseEvidenceService securityCaseEvidenceService;

    private final SecurityCaseEvidenceRepository securityCaseEvidenceRepository;

    public SecurityCaseEvidenceResource(
        SecurityCaseEvidenceService securityCaseEvidenceService,
        SecurityCaseEvidenceRepository securityCaseEvidenceRepository
    ) {
        this.securityCaseEvidenceService = securityCaseEvidenceService;
        this.securityCaseEvidenceRepository = securityCaseEvidenceRepository;
    }

    /**
     * {@code POST  /security-case-evidences} : Create a new securityCaseEvidence.
     *
     * @param securityCaseEvidenceDTO the securityCaseEvidenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new securityCaseEvidenceDTO, or with status {@code 400 (Bad Request)} if the securityCaseEvidence already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SecurityCaseEvidenceDTO>> createSecurityCaseEvidence(
        @Valid @RequestBody SecurityCaseEvidenceDTO securityCaseEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SecurityCaseEvidence : {}", securityCaseEvidenceDTO);
        if (securityCaseEvidenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new securityCaseEvidence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return securityCaseEvidenceService.save(securityCaseEvidenceDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/security-case-evidences/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /security-case-evidences/:id} : Updates an existing securityCaseEvidence.
     *
     * @param id the id of the securityCaseEvidenceDTO to save.
     * @param securityCaseEvidenceDTO the securityCaseEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityCaseEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the securityCaseEvidenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the securityCaseEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SecurityCaseEvidenceDTO>> updateSecurityCaseEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SecurityCaseEvidenceDTO securityCaseEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SecurityCaseEvidence : {}, {}", id, securityCaseEvidenceDTO);
        if (securityCaseEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityCaseEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityCaseEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return securityCaseEvidenceService
                .update(securityCaseEvidenceDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /security-case-evidences/:id} : Partial updates given fields of an existing securityCaseEvidence, field will ignore if it is null
     *
     * @param id the id of the securityCaseEvidenceDTO to save.
     * @param securityCaseEvidenceDTO the securityCaseEvidenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityCaseEvidenceDTO,
     * or with status {@code 400 (Bad Request)} if the securityCaseEvidenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the securityCaseEvidenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the securityCaseEvidenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SecurityCaseEvidenceDTO>> partialUpdateSecurityCaseEvidence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SecurityCaseEvidenceDTO securityCaseEvidenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SecurityCaseEvidence : {}, {}", id, securityCaseEvidenceDTO);
        if (securityCaseEvidenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityCaseEvidenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityCaseEvidenceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SecurityCaseEvidenceDTO> result = securityCaseEvidenceService.partialUpdate(securityCaseEvidenceDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /security-case-evidences} : get all the Security Case Evidences.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Security Case Evidences in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SecurityCaseEvidenceDTO>> getAllSecurityCaseEvidences(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SecurityCaseEvidences");
        if (eagerload) {
            return securityCaseEvidenceService.findAllWithEagerRelationships().collectList();
        } else {
            return securityCaseEvidenceService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /security-case-evidences} : get all the Security Case Evidences as a stream.
     * @return the {@link Flux} of Security Case Evidences.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SecurityCaseEvidenceDTO> getAllSecurityCaseEvidencesAsStream() {
        LOG.debug("REST request to get all SecurityCaseEvidences as a stream");
        return securityCaseEvidenceService.findAll();
    }

    /**
     * {@code GET  /security-case-evidences/:id} : get the "id" securityCaseEvidence.
     *
     * @param id the id of the securityCaseEvidenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the securityCaseEvidenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SecurityCaseEvidenceDTO>> getSecurityCaseEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SecurityCaseEvidence : {}", id);
        Mono<SecurityCaseEvidenceDTO> securityCaseEvidenceDTO = securityCaseEvidenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(securityCaseEvidenceDTO);
    }

    /**
     * {@code DELETE  /security-case-evidences/:id} : delete the "id" securityCaseEvidence.
     *
     * @param id the id of the securityCaseEvidenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSecurityCaseEvidence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SecurityCaseEvidence : {}", id);
        return securityCaseEvidenceService
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
