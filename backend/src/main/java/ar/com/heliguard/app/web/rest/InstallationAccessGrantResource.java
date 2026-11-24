package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.service.InstallationAccessGrantService;
import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.InstallationAccessGrant}.
 */
@RestController
@RequestMapping("/api/installation-access-grants")
public class InstallationAccessGrantResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationAccessGrantResource.class);

    private static final String ENTITY_NAME = "installationAccessGrant";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final InstallationAccessGrantService installationAccessGrantService;

    private final InstallationAccessGrantRepository installationAccessGrantRepository;

    public InstallationAccessGrantResource(
        InstallationAccessGrantService installationAccessGrantService,
        InstallationAccessGrantRepository installationAccessGrantRepository
    ) {
        this.installationAccessGrantService = installationAccessGrantService;
        this.installationAccessGrantRepository = installationAccessGrantRepository;
    }

    /**
     * {@code POST  /installation-access-grants} : Create a new installationAccessGrant.
     *
     * @param installationAccessGrantDTO the installationAccessGrantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new installationAccessGrantDTO, or with status {@code 400 (Bad Request)} if the installationAccessGrant already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InstallationAccessGrantDTO>> createInstallationAccessGrant(
        @Valid @RequestBody InstallationAccessGrantDTO installationAccessGrantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save InstallationAccessGrant : {}", installationAccessGrantDTO);
        if (installationAccessGrantDTO.getId() != null) {
            throw new BadRequestAlertException("A new installationAccessGrant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return installationAccessGrantService.save(installationAccessGrantDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/installation-access-grants/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /installation-access-grants/:id} : Updates an existing installationAccessGrant.
     *
     * @param id the id of the installationAccessGrantDTO to save.
     * @param installationAccessGrantDTO the installationAccessGrantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated installationAccessGrantDTO,
     * or with status {@code 400 (Bad Request)} if the installationAccessGrantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the installationAccessGrantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InstallationAccessGrantDTO>> updateInstallationAccessGrant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstallationAccessGrantDTO installationAccessGrantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InstallationAccessGrant : {}, {}", id, installationAccessGrantDTO);
        if (installationAccessGrantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, installationAccessGrantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return installationAccessGrantRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return installationAccessGrantService
                .update(installationAccessGrantDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /installation-access-grants/:id} : Partial updates given fields of an existing installationAccessGrant, field will ignore if it is null
     *
     * @param id the id of the installationAccessGrantDTO to save.
     * @param installationAccessGrantDTO the installationAccessGrantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated installationAccessGrantDTO,
     * or with status {@code 400 (Bad Request)} if the installationAccessGrantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the installationAccessGrantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the installationAccessGrantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InstallationAccessGrantDTO>> partialUpdateInstallationAccessGrant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstallationAccessGrantDTO installationAccessGrantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update InstallationAccessGrant : {}, {}", id, installationAccessGrantDTO);
        if (installationAccessGrantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, installationAccessGrantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return installationAccessGrantRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<InstallationAccessGrantDTO> result = installationAccessGrantService.partialUpdate(installationAccessGrantDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /installation-access-grants} : get all the Installation Access Grants.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Installation Access Grants in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<InstallationAccessGrantDTO>> getAllInstallationAccessGrants(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InstallationAccessGrants");
        if (eagerload) {
            return installationAccessGrantService.findAllWithEagerRelationships().collectList();
        } else {
            return installationAccessGrantService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /installation-access-grants} : get all the Installation Access Grants as a stream.
     * @return the {@link Flux} of Installation Access Grants.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<InstallationAccessGrantDTO> getAllInstallationAccessGrantsAsStream() {
        LOG.debug("REST request to get all InstallationAccessGrants as a stream");
        return installationAccessGrantService.findAll();
    }

    /**
     * {@code GET  /installation-access-grants/:id} : get the "id" installationAccessGrant.
     *
     * @param id the id of the installationAccessGrantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the installationAccessGrantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InstallationAccessGrantDTO>> getInstallationAccessGrant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InstallationAccessGrant : {}", id);
        Mono<InstallationAccessGrantDTO> installationAccessGrantDTO = installationAccessGrantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(installationAccessGrantDTO);
    }

    /**
     * {@code DELETE  /installation-access-grants/:id} : delete the "id" installationAccessGrant.
     *
     * @param id the id of the installationAccessGrantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInstallationAccessGrant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InstallationAccessGrant : {}", id);
        return installationAccessGrantService
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
