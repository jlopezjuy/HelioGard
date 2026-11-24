package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.EvidenceSourceRepository;
import ar.com.heliguard.app.service.EvidenceSourceService;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.EvidenceSource}.
 */
@RestController
@RequestMapping("/api/evidence-sources")
public class EvidenceSourceResource {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceSourceResource.class);

    private static final String ENTITY_NAME = "evidenceSource";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final EvidenceSourceService evidenceSourceService;

    private final EvidenceSourceRepository evidenceSourceRepository;

    public EvidenceSourceResource(EvidenceSourceService evidenceSourceService, EvidenceSourceRepository evidenceSourceRepository) {
        this.evidenceSourceService = evidenceSourceService;
        this.evidenceSourceRepository = evidenceSourceRepository;
    }

    /**
     * {@code POST  /evidence-sources} : Create a new evidenceSource.
     *
     * @param evidenceSourceDTO the evidenceSourceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evidenceSourceDTO, or with status {@code 400 (Bad Request)} if the evidenceSource already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EvidenceSourceDTO>> createEvidenceSource(@Valid @RequestBody EvidenceSourceDTO evidenceSourceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EvidenceSource : {}", evidenceSourceDTO);
        if (evidenceSourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new evidenceSource cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return evidenceSourceService.save(evidenceSourceDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/evidence-sources/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /evidence-sources/:id} : Updates an existing evidenceSource.
     *
     * @param id the id of the evidenceSourceDTO to save.
     * @param evidenceSourceDTO the evidenceSourceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evidenceSourceDTO,
     * or with status {@code 400 (Bad Request)} if the evidenceSourceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evidenceSourceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EvidenceSourceDTO>> updateEvidenceSource(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EvidenceSourceDTO evidenceSourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EvidenceSource : {}, {}", id, evidenceSourceDTO);
        if (evidenceSourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evidenceSourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return evidenceSourceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return evidenceSourceService
                .update(evidenceSourceDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /evidence-sources/:id} : Partial updates given fields of an existing evidenceSource, field will ignore if it is null
     *
     * @param id the id of the evidenceSourceDTO to save.
     * @param evidenceSourceDTO the evidenceSourceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evidenceSourceDTO,
     * or with status {@code 400 (Bad Request)} if the evidenceSourceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the evidenceSourceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the evidenceSourceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EvidenceSourceDTO>> partialUpdateEvidenceSource(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EvidenceSourceDTO evidenceSourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update EvidenceSource : {}, {}", id, evidenceSourceDTO);
        if (evidenceSourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evidenceSourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return evidenceSourceRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<EvidenceSourceDTO> result = evidenceSourceService.partialUpdate(evidenceSourceDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /evidence-sources} : get all the Evidence Sources.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Evidence Sources in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<EvidenceSourceDTO>> getAllEvidenceSources() {
        LOG.debug("REST request to get all EvidenceSources");
        return evidenceSourceService.findAll().collectList();
    }

    /**
     * {@code GET  /evidence-sources} : get all the Evidence Sources as a stream.
     * @return the {@link Flux} of Evidence Sources.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EvidenceSourceDTO> getAllEvidenceSourcesAsStream() {
        LOG.debug("REST request to get all EvidenceSources as a stream");
        return evidenceSourceService.findAll();
    }

    /**
     * {@code GET  /evidence-sources/:id} : get the "id" evidenceSource.
     *
     * @param id the id of the evidenceSourceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evidenceSourceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EvidenceSourceDTO>> getEvidenceSource(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EvidenceSource : {}", id);
        Mono<EvidenceSourceDTO> evidenceSourceDTO = evidenceSourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(evidenceSourceDTO);
    }

    /**
     * {@code DELETE  /evidence-sources/:id} : delete the "id" evidenceSource.
     *
     * @param id the id of the evidenceSourceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEvidenceSource(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EvidenceSource : {}", id);
        return evidenceSourceService
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
