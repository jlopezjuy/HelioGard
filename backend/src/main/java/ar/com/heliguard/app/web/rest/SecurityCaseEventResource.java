package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.SecurityCaseEventRepository;
import ar.com.heliguard.app.service.SecurityCaseEventService;
import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvent}.
 */
@RestController
@RequestMapping("/api/security-case-events")
public class SecurityCaseEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityCaseEventResource.class);

    private static final String ENTITY_NAME = "securityCaseEvent";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final SecurityCaseEventService securityCaseEventService;

    private final SecurityCaseEventRepository securityCaseEventRepository;

    public SecurityCaseEventResource(
        SecurityCaseEventService securityCaseEventService,
        SecurityCaseEventRepository securityCaseEventRepository
    ) {
        this.securityCaseEventService = securityCaseEventService;
        this.securityCaseEventRepository = securityCaseEventRepository;
    }

    /**
     * {@code POST  /security-case-events} : Create a new securityCaseEvent.
     *
     * @param securityCaseEventDTO the securityCaseEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new securityCaseEventDTO, or with status {@code 400 (Bad Request)} if the securityCaseEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SecurityCaseEventDTO>> createSecurityCaseEvent(@Valid @RequestBody SecurityCaseEventDTO securityCaseEventDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SecurityCaseEvent : {}", securityCaseEventDTO);
        if (securityCaseEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new securityCaseEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return securityCaseEventService.save(securityCaseEventDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/security-case-events/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /security-case-events/:id} : Updates an existing securityCaseEvent.
     *
     * @param id the id of the securityCaseEventDTO to save.
     * @param securityCaseEventDTO the securityCaseEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityCaseEventDTO,
     * or with status {@code 400 (Bad Request)} if the securityCaseEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the securityCaseEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SecurityCaseEventDTO>> updateSecurityCaseEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SecurityCaseEventDTO securityCaseEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SecurityCaseEvent : {}, {}", id, securityCaseEventDTO);
        if (securityCaseEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityCaseEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityCaseEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return securityCaseEventService
                .update(securityCaseEventDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /security-case-events/:id} : Partial updates given fields of an existing securityCaseEvent, field will ignore if it is null
     *
     * @param id the id of the securityCaseEventDTO to save.
     * @param securityCaseEventDTO the securityCaseEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityCaseEventDTO,
     * or with status {@code 400 (Bad Request)} if the securityCaseEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the securityCaseEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the securityCaseEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SecurityCaseEventDTO>> partialUpdateSecurityCaseEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SecurityCaseEventDTO securityCaseEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SecurityCaseEvent : {}, {}", id, securityCaseEventDTO);
        if (securityCaseEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, securityCaseEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return securityCaseEventRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<SecurityCaseEventDTO> result = securityCaseEventService.partialUpdate(securityCaseEventDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /security-case-events} : get all the Security Case Events.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Security Case Events in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SecurityCaseEventDTO>> getAllSecurityCaseEvents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SecurityCaseEvents");
        if (eagerload) {
            return securityCaseEventService.findAllWithEagerRelationships().collectList();
        } else {
            return securityCaseEventService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /security-case-events} : get all the Security Case Events as a stream.
     * @return the {@link Flux} of Security Case Events.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SecurityCaseEventDTO> getAllSecurityCaseEventsAsStream() {
        LOG.debug("REST request to get all SecurityCaseEvents as a stream");
        return securityCaseEventService.findAll();
    }

    /**
     * {@code GET  /security-case-events/:id} : get the "id" securityCaseEvent.
     *
     * @param id the id of the securityCaseEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the securityCaseEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SecurityCaseEventDTO>> getSecurityCaseEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SecurityCaseEvent : {}", id);
        Mono<SecurityCaseEventDTO> securityCaseEventDTO = securityCaseEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(securityCaseEventDTO);
    }

    /**
     * {@code DELETE  /security-case-events/:id} : delete the "id" securityCaseEvent.
     *
     * @param id the id of the securityCaseEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSecurityCaseEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SecurityCaseEvent : {}", id);
        return securityCaseEventService
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
