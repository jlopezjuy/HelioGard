package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.service.OrganizationMembershipService;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.OrganizationMembership}.
 */
@RestController
@RequestMapping("/api/organization-memberships")
public class OrganizationMembershipResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationMembershipResource.class);

    private static final String ENTITY_NAME = "organizationMembership";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final OrganizationMembershipService organizationMembershipService;

    private final OrganizationMembershipRepository organizationMembershipRepository;

    public OrganizationMembershipResource(
        OrganizationMembershipService organizationMembershipService,
        OrganizationMembershipRepository organizationMembershipRepository
    ) {
        this.organizationMembershipService = organizationMembershipService;
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    /**
     * {@code POST  /organization-memberships} : Create a new organizationMembership.
     *
     * @param organizationMembershipDTO the organizationMembershipDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organizationMembershipDTO, or with status {@code 400 (Bad Request)} if the organizationMembership already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OrganizationMembershipDTO>> createOrganizationMembership(
        @Valid @RequestBody OrganizationMembershipDTO organizationMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save OrganizationMembership : {}", organizationMembershipDTO);
        if (organizationMembershipDTO.getId() != null) {
            throw new BadRequestAlertException("A new organizationMembership cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return organizationMembershipService.save(organizationMembershipDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/organization-memberships/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /organization-memberships/:id} : Updates an existing organizationMembership.
     *
     * @param id the id of the organizationMembershipDTO to save.
     * @param organizationMembershipDTO the organizationMembershipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizationMembershipDTO,
     * or with status {@code 400 (Bad Request)} if the organizationMembershipDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organizationMembershipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OrganizationMembershipDTO>> updateOrganizationMembership(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrganizationMembershipDTO organizationMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrganizationMembership : {}, {}", id, organizationMembershipDTO);
        if (organizationMembershipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizationMembershipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return organizationMembershipRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return organizationMembershipService
                .update(organizationMembershipDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /organization-memberships/:id} : Partial updates given fields of an existing organizationMembership, field will ignore if it is null
     *
     * @param id the id of the organizationMembershipDTO to save.
     * @param organizationMembershipDTO the organizationMembershipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizationMembershipDTO,
     * or with status {@code 400 (Bad Request)} if the organizationMembershipDTO is not valid,
     * or with status {@code 404 (Not Found)} if the organizationMembershipDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the organizationMembershipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrganizationMembershipDTO>> partialUpdateOrganizationMembership(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrganizationMembershipDTO organizationMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update OrganizationMembership : {}, {}", id, organizationMembershipDTO);
        if (organizationMembershipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizationMembershipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return organizationMembershipRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<OrganizationMembershipDTO> result = organizationMembershipService.partialUpdate(organizationMembershipDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /organization-memberships} : get all the Organization Memberships.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Organization Memberships in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<OrganizationMembershipDTO>> getAllOrganizationMemberships(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all OrganizationMemberships");
        if (eagerload) {
            return organizationMembershipService.findAllWithEagerRelationships().collectList();
        } else {
            return organizationMembershipService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /organization-memberships} : get all the Organization Memberships as a stream.
     * @return the {@link Flux} of Organization Memberships.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrganizationMembershipDTO> getAllOrganizationMembershipsAsStream() {
        LOG.debug("REST request to get all OrganizationMemberships as a stream");
        return organizationMembershipService.findAll();
    }

    /**
     * {@code GET  /organization-memberships/:id} : get the "id" organizationMembership.
     *
     * @param id the id of the organizationMembershipDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organizationMembershipDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrganizationMembershipDTO>> getOrganizationMembership(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrganizationMembership : {}", id);
        Mono<OrganizationMembershipDTO> organizationMembershipDTO = organizationMembershipService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organizationMembershipDTO);
    }

    /**
     * {@code DELETE  /organization-memberships/:id} : delete the "id" organizationMembership.
     *
     * @param id the id of the organizationMembershipDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrganizationMembership(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrganizationMembership : {}", id);
        return organizationMembershipService
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
