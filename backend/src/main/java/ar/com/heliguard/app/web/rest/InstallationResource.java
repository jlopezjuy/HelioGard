package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.service.InstallationService;
import ar.com.heliguard.app.service.dto.InstallationDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.Installation}.
 */
@RestController
@RequestMapping("/api/installations")
public class InstallationResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationResource.class);

    private static final String ENTITY_NAME = "installation";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final InstallationService installationService;

    private final InstallationRepository installationRepository;

    public InstallationResource(InstallationService installationService, InstallationRepository installationRepository) {
        this.installationService = installationService;
        this.installationRepository = installationRepository;
    }

    /**
     * {@code POST  /installations} : Create a new installation.
     *
     * @param installationDTO the installationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new installationDTO, or with status {@code 400 (Bad Request)} if the installation already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InstallationDTO>> createInstallation(@Valid @RequestBody InstallationDTO installationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Installation : {}", installationDTO);
        if (installationDTO.getId() != null) {
            throw new BadRequestAlertException("A new installation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return installationService.save(installationDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/installations/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /installations/:id} : Updates an existing installation.
     *
     * @param id the id of the installationDTO to save.
     * @param installationDTO the installationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated installationDTO,
     * or with status {@code 400 (Bad Request)} if the installationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the installationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InstallationDTO>> updateInstallation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstallationDTO installationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Installation : {}, {}", id, installationDTO);
        if (installationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, installationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return installationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return installationService
                .update(installationDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /installations/:id} : Partial updates given fields of an existing installation, field will ignore if it is null
     *
     * @param id the id of the installationDTO to save.
     * @param installationDTO the installationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated installationDTO,
     * or with status {@code 400 (Bad Request)} if the installationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the installationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the installationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InstallationDTO>> partialUpdateInstallation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstallationDTO installationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Installation : {}, {}", id, installationDTO);
        if (installationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, installationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return installationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<InstallationDTO> result = installationService.partialUpdate(installationDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /installations} : get all the Installations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Installations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<InstallationDTO>>> getAllInstallations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Installations");
        return installationService
            .countAll()
            .zipWith(installationService.findAll(pageable).collectList())
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
     * {@code GET  /installations/:id} : get the "id" installation.
     *
     * @param id the id of the installationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the installationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InstallationDTO>> getInstallation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Installation : {}", id);
        Mono<InstallationDTO> installationDTO = installationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(installationDTO);
    }

    /**
     * {@code DELETE  /installations/:id} : delete the "id" installation.
     *
     * @param id the id of the installationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInstallation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Installation : {}", id);
        return installationService
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
