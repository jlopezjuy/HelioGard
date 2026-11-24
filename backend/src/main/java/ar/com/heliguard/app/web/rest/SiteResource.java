package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.service.SiteService;
import ar.com.heliguard.app.service.dto.SiteDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.Site}.
 */
@RestController
@RequestMapping("/api/sites")
public class SiteResource {

    private static final Logger LOG = LoggerFactory.getLogger(SiteResource.class);

    private static final String ENTITY_NAME = "site";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SiteService siteService;

    private final SiteRepository siteRepository;

    public SiteResource(SiteService siteService, SiteRepository siteRepository) {
        this.siteService = siteService;
        this.siteRepository = siteRepository;
    }

    /**
     * {@code POST  /sites} : Create a new site.
     *
     * @param siteDTO the siteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new siteDTO, or with status {@code 400 (Bad Request)} if the site already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SiteDTO>> createSite(@Valid @RequestBody SiteDTO siteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Site : {}", siteDTO);
        if (siteDTO.getId() != null) {
            throw new BadRequestAlertException("A new site cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return siteService.save(siteDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/sites/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /sites/:id} : Updates an existing site.
     *
     * @param id the id of the siteDTO to save.
     * @param siteDTO the siteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteDTO,
     * or with status {@code 400 (Bad Request)} if the siteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the siteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SiteDTO>> updateSite(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SiteDTO siteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Site : {}, {}", id, siteDTO);
        if (siteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return siteRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return siteService
                .update(siteDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /sites/:id} : Partial updates given fields of an existing site, field will ignore if it is null
     *
     * @param id the id of the siteDTO to save.
     * @param siteDTO the siteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteDTO,
     * or with status {@code 400 (Bad Request)} if the siteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the siteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the siteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SiteDTO>> partialUpdateSite(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SiteDTO siteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Site : {}, {}", id, siteDTO);
        if (siteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return siteRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SiteDTO> result = siteService.partialUpdate(siteDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /sites} : get all the Sites.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Sites in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SiteDTO>> getAllSites(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Sites");
        if (eagerload) {
            return siteService.findAllWithEagerRelationships().collectList();
        } else {
            return siteService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /sites} : get all the Sites as a stream.
     * @return the {@link Flux} of Sites.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SiteDTO> getAllSitesAsStream() {
        LOG.debug("REST request to get all Sites as a stream");
        return siteService.findAll();
    }

    /**
     * {@code GET  /sites/:id} : get the "id" site.
     *
     * @param id the id of the siteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the siteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SiteDTO>> getSite(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Site : {}", id);
        Mono<SiteDTO> siteDTO = siteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(siteDTO);
    }

    /**
     * {@code DELETE  /sites/:id} : delete the "id" site.
     *
     * @param id the id of the siteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSite(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Site : {}", id);
        return siteService
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
