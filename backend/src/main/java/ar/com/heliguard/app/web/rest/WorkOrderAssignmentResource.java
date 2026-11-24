package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.service.WorkOrderAssignmentService;
import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.WorkOrderAssignment}.
 */
@RestController
@RequestMapping("/api/work-order-assignments")
public class WorkOrderAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderAssignmentResource.class);

    private static final String ENTITY_NAME = "workOrderAssignment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final WorkOrderAssignmentService workOrderAssignmentService;

    private final WorkOrderAssignmentRepository workOrderAssignmentRepository;

    public WorkOrderAssignmentResource(
        WorkOrderAssignmentService workOrderAssignmentService,
        WorkOrderAssignmentRepository workOrderAssignmentRepository
    ) {
        this.workOrderAssignmentService = workOrderAssignmentService;
        this.workOrderAssignmentRepository = workOrderAssignmentRepository;
    }

    /**
     * {@code POST  /work-order-assignments} : Create a new workOrderAssignment.
     *
     * @param workOrderAssignmentDTO the workOrderAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workOrderAssignmentDTO, or with status {@code 400 (Bad Request)} if the workOrderAssignment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<WorkOrderAssignmentDTO>> createWorkOrderAssignment(
        @Valid @RequestBody WorkOrderAssignmentDTO workOrderAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save WorkOrderAssignment : {}", workOrderAssignmentDTO);
        if (workOrderAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new workOrderAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return workOrderAssignmentService.save(workOrderAssignmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/work-order-assignments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /work-order-assignments/:id} : Updates an existing workOrderAssignment.
     *
     * @param id the id of the workOrderAssignmentDTO to save.
     * @param workOrderAssignmentDTO the workOrderAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workOrderAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderAssignmentDTO>> updateWorkOrderAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkOrderAssignmentDTO workOrderAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WorkOrderAssignment : {}, {}", id, workOrderAssignmentDTO);
        if (workOrderAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderAssignmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return workOrderAssignmentService
                .update(workOrderAssignmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /work-order-assignments/:id} : Partial updates given fields of an existing workOrderAssignment, field will ignore if it is null
     *
     * @param id the id of the workOrderAssignmentDTO to save.
     * @param workOrderAssignmentDTO the workOrderAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workOrderAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workOrderAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<WorkOrderAssignmentDTO>> partialUpdateWorkOrderAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkOrderAssignmentDTO workOrderAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WorkOrderAssignment : {}, {}", id, workOrderAssignmentDTO);
        if (workOrderAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderAssignmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<WorkOrderAssignmentDTO> result = workOrderAssignmentService.partialUpdate(workOrderAssignmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /work-order-assignments} : get all the Work Order Assignments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Work Order Assignments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<WorkOrderAssignmentDTO>> getAllWorkOrderAssignments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all WorkOrderAssignments");
        if (eagerload) {
            return workOrderAssignmentService.findAllWithEagerRelationships().collectList();
        } else {
            return workOrderAssignmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /work-order-assignments} : get all the Work Order Assignments as a stream.
     * @return the {@link Flux} of Work Order Assignments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<WorkOrderAssignmentDTO> getAllWorkOrderAssignmentsAsStream() {
        LOG.debug("REST request to get all WorkOrderAssignments as a stream");
        return workOrderAssignmentService.findAll();
    }

    /**
     * {@code GET  /work-order-assignments/:id} : get the "id" workOrderAssignment.
     *
     * @param id the id of the workOrderAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workOrderAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderAssignmentDTO>> getWorkOrderAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WorkOrderAssignment : {}", id);
        Mono<WorkOrderAssignmentDTO> workOrderAssignmentDTO = workOrderAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workOrderAssignmentDTO);
    }

    /**
     * {@code DELETE  /work-order-assignments/:id} : delete the "id" workOrderAssignment.
     *
     * @param id the id of the workOrderAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteWorkOrderAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WorkOrderAssignment : {}", id);
        return workOrderAssignmentService
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
