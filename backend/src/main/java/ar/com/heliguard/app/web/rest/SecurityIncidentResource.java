package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityIncidentService;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SecurityIncident}.
 */
@RestController
@RequestMapping("/api/security-incidents")
public class SecurityIncidentResource {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityIncidentResource.class);

    private static final String ENTITY_NAME = "securityIncident";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SecurityIncidentService securityIncidentService;

    private final SecurityIncidentRepository securityIncidentRepository;

    public SecurityIncidentResource(
        SecurityIncidentService securityIncidentService,
        SecurityIncidentRepository securityIncidentRepository
    ) {
        this.securityIncidentService = securityIncidentService;
        this.securityIncidentRepository = securityIncidentRepository;
    }

    /**
     * {@code POST  /security-incidents} : Create a new securityIncident.
     *
     * @param securityIncidentDTO the securityIncidentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new securityIncidentDTO, or with status {@code 400 (Bad Request)} if the securityIncident already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SecurityIncidentDTO>> createSecurityIncident(@Valid @RequestBody SecurityIncidentDTO securityIncidentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SecurityIncident : {}", securityIncidentDTO);
        if (securityIncidentDTO.getId() != null) {
            throw new BadRequestAlertException("A new securityIncident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return securityIncidentService.save(securityIncidentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/security-incidents/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /security-incidents/:id} : Updates an existing securityIncident.
     *
     * @param id the id of the securityIncidentDTO to save.
     * @param securityIncidentDTO the securityIncidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityIncidentDTO,
     * or with status {@code 400 (Bad Request)} if the securityIncidentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the securityIncidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SecurityIncidentDTO>> updateSecurityIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SecurityIncidentDTO securityIncidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SecurityIncident : {}, {}", id, securityIncidentDTO);
        if (securityIncidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityIncidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityIncidentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return securityIncidentService
                .update(securityIncidentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /security-incidents/:id} : Partial updates given fields of an existing securityIncident, field will ignore if it is null
     *
     * @param id the id of the securityIncidentDTO to save.
     * @param securityIncidentDTO the securityIncidentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityIncidentDTO,
     * or with status {@code 400 (Bad Request)} if the securityIncidentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the securityIncidentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the securityIncidentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SecurityIncidentDTO>> partialUpdateSecurityIncident(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SecurityIncidentDTO securityIncidentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SecurityIncident : {}, {}", id, securityIncidentDTO);
        if (securityIncidentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityIncidentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityIncidentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SecurityIncidentDTO> result = securityIncidentService.partialUpdate(securityIncidentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /security-incidents} : get all the Security Incidents.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Security Incidents in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SecurityIncidentDTO>>> getAllSecurityIncidents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SecurityIncidents");
        return securityIncidentService
            .countAll()
            .zipWith(securityIncidentService.findAll(pageable).collectList())
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
     * {@code GET  /security-incidents/:id} : get the "id" securityIncident.
     *
     * @param id the id of the securityIncidentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the securityIncidentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SecurityIncidentDTO>> getSecurityIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SecurityIncident : {}", id);
        Mono<SecurityIncidentDTO> securityIncidentDTO = securityIncidentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(securityIncidentDTO);
    }

    /**
     * {@code DELETE  /security-incidents/:id} : delete the "id" securityIncident.
     *
     * @param id the id of the securityIncidentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSecurityIncident(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SecurityIncident : {}", id);
        return securityIncidentService
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
