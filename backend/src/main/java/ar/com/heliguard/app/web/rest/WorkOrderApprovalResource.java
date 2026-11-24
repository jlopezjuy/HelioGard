package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.WorkOrderApprovalRepository;
import ar.com.heliguard.app.service.WorkOrderApprovalService;
import ar.com.heliguard.app.service.dto.WorkOrderApprovalDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.WorkOrderApproval}.
 */
@RestController
@RequestMapping("/api/work-order-approvals")
public class WorkOrderApprovalResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderApprovalResource.class);

    private static final String ENTITY_NAME = "workOrderApproval";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final WorkOrderApprovalService workOrderApprovalService;

    private final WorkOrderApprovalRepository workOrderApprovalRepository;

    public WorkOrderApprovalResource(
        WorkOrderApprovalService workOrderApprovalService,
        WorkOrderApprovalRepository workOrderApprovalRepository
    ) {
        this.workOrderApprovalService = workOrderApprovalService;
        this.workOrderApprovalRepository = workOrderApprovalRepository;
    }

    /**
     * {@code POST  /work-order-approvals} : Create a new workOrderApproval.
     *
     * @param workOrderApprovalDTO the workOrderApprovalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workOrderApprovalDTO, or with status {@code 400 (Bad Request)} if the workOrderApproval already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<WorkOrderApprovalDTO>> createWorkOrderApproval(@Valid @RequestBody WorkOrderApprovalDTO workOrderApprovalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save WorkOrderApproval : {}", workOrderApprovalDTO);
        if (workOrderApprovalDTO.getId() != null) {
            throw new BadRequestAlertException("A new workOrderApproval cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return workOrderApprovalService.save(workOrderApprovalDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/work-order-approvals/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /work-order-approvals/:id} : Updates an existing workOrderApproval.
     *
     * @param id the id of the workOrderApprovalDTO to save.
     * @param workOrderApprovalDTO the workOrderApprovalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderApprovalDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderApprovalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workOrderApprovalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderApprovalDTO>> updateWorkOrderApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkOrderApprovalDTO workOrderApprovalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WorkOrderApproval : {}, {}", id, workOrderApprovalDTO);
        if (workOrderApprovalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderApprovalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderApprovalRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return workOrderApprovalService
                .update(workOrderApprovalDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /work-order-approvals/:id} : Partial updates given fields of an existing workOrderApproval, field will ignore if it is null
     *
     * @param id the id of the workOrderApprovalDTO to save.
     * @param workOrderApprovalDTO the workOrderApprovalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderApprovalDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderApprovalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workOrderApprovalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workOrderApprovalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<WorkOrderApprovalDTO>> partialUpdateWorkOrderApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkOrderApprovalDTO workOrderApprovalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WorkOrderApproval : {}, {}", id, workOrderApprovalDTO);
        if (workOrderApprovalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderApprovalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderApprovalRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<WorkOrderApprovalDTO> result = workOrderApprovalService.partialUpdate(workOrderApprovalDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /work-order-approvals} : get all the Work Order Approvals.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Work Order Approvals in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<WorkOrderApprovalDTO>> getAllWorkOrderApprovals(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all WorkOrderApprovals");
        if (eagerload) {
            return workOrderApprovalService.findAllWithEagerRelationships().collectList();
        } else {
            return workOrderApprovalService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /work-order-approvals} : get all the Work Order Approvals as a stream.
     * @return the {@link Flux} of Work Order Approvals.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<WorkOrderApprovalDTO> getAllWorkOrderApprovalsAsStream() {
        LOG.debug("REST request to get all WorkOrderApprovals as a stream");
        return workOrderApprovalService.findAll();
    }

    /**
     * {@code GET  /work-order-approvals/:id} : get the "id" workOrderApproval.
     *
     * @param id the id of the workOrderApprovalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workOrderApprovalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderApprovalDTO>> getWorkOrderApproval(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WorkOrderApproval : {}", id);
        Mono<WorkOrderApprovalDTO> workOrderApprovalDTO = workOrderApprovalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workOrderApprovalDTO);
    }

    /**
     * {@code DELETE  /work-order-approvals/:id} : delete the "id" workOrderApproval.
     *
     * @param id the id of the workOrderApprovalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteWorkOrderApproval(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WorkOrderApproval : {}", id);
        return workOrderApprovalService
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
