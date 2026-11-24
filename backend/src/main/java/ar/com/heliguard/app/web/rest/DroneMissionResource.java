package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.service.DroneMissionService;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.DroneMission}.
 */
@RestController
@RequestMapping("/api/drone-missions")
public class DroneMissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DroneMissionResource.class);

    private static final String ENTITY_NAME = "droneMission";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final DroneMissionService droneMissionService;

    private final DroneMissionRepository droneMissionRepository;

    public DroneMissionResource(DroneMissionService droneMissionService, DroneMissionRepository droneMissionRepository) {
        this.droneMissionService = droneMissionService;
        this.droneMissionRepository = droneMissionRepository;
    }

    /**
     * {@code POST  /drone-missions} : Create a new droneMission.
     *
     * @param droneMissionDTO the droneMissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new droneMissionDTO, or with status {@code 400 (Bad Request)} if the droneMission already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DroneMissionDTO>> createDroneMission(@Valid @RequestBody DroneMissionDTO droneMissionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DroneMission : {}", droneMissionDTO);
        if (droneMissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new droneMission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return droneMissionService.save(droneMissionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/drone-missions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /drone-missions/:id} : Updates an existing droneMission.
     *
     * @param id the id of the droneMissionDTO to save.
     * @param droneMissionDTO the droneMissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionDTO>> updateDroneMission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DroneMissionDTO droneMissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DroneMission : {}, {}", id, droneMissionDTO);
        if (droneMissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return droneMissionService
                .update(droneMissionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /drone-missions/:id} : Partial updates given fields of an existing droneMission, field will ignore if it is null
     *
     * @param id the id of the droneMissionDTO to save.
     * @param droneMissionDTO the droneMissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneMissionDTO,
     * or with status {@code 400 (Bad Request)} if the droneMissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the droneMissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the droneMissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DroneMissionDTO>> partialUpdateDroneMission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DroneMissionDTO droneMissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DroneMission : {}, {}", id, droneMissionDTO);
        if (droneMissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneMissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneMissionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DroneMissionDTO> result = droneMissionService.partialUpdate(droneMissionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /drone-missions} : get all the Drone Missions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Drone Missions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DroneMissionDTO>>> getAllDroneMissions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of DroneMissions");
        return droneMissionService
            .countAll()
            .zipWith(droneMissionService.findAll(pageable).collectList())
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
     * {@code GET  /drone-missions/:id} : get the "id" droneMission.
     *
     * @param id the id of the droneMissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the droneMissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DroneMissionDTO>> getDroneMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DroneMission : {}", id);
        Mono<DroneMissionDTO> droneMissionDTO = droneMissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(droneMissionDTO);
    }

    /**
     * {@code DELETE  /drone-missions/:id} : delete the "id" droneMission.
     *
     * @param id the id of the droneMissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDroneMission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DroneMission : {}", id);
        return droneMissionService
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
