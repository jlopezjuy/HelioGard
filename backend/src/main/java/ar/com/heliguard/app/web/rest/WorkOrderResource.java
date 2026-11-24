package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderService;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.WorkOrder}.
 */
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderResource.class);

    private static final String ENTITY_NAME = "workOrder";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final WorkOrderService workOrderService;

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderResource(WorkOrderService workOrderService, WorkOrderRepository workOrderRepository) {
        this.workOrderService = workOrderService;
        this.workOrderRepository = workOrderRepository;
    }

    /**
     * {@code POST  /work-orders} : Create a new workOrder.
     *
     * @param workOrderDTO the workOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workOrderDTO, or with status {@code 400 (Bad Request)} if the workOrder already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<WorkOrderDTO>> createWorkOrder(@Valid @RequestBody WorkOrderDTO workOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to save WorkOrder : {}", workOrderDTO);
        if (workOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new workOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return workOrderService.save(workOrderDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/work-orders/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /work-orders/:id} : Updates an existing workOrder.
     *
     * @param id the id of the workOrderDTO to save.
     * @param workOrderDTO the workOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderDTO>> updateWorkOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkOrderDTO workOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WorkOrder : {}, {}", id, workOrderDTO);
        if (workOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return workOrderService
                .update(workOrderDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /work-orders/:id} : Partial updates given fields of an existing workOrder, field will ignore if it is null
     *
     * @param id the id of the workOrderDTO to save.
     * @param workOrderDTO the workOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workOrderDTO,
     * or with status {@code 400 (Bad Request)} if the workOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<WorkOrderDTO>> partialUpdateWorkOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkOrderDTO workOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update WorkOrder : {}, {}", id, workOrderDTO);
        if (workOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return workOrderRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<WorkOrderDTO> result = workOrderService.partialUpdate(workOrderDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /work-orders} : get all the Work Orders.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Work Orders in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<WorkOrderDTO>>> getAllWorkOrders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of WorkOrders");
        return workOrderService
            .countAll()
            .zipWith(workOrderService.findAll(pageable).collectList())
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
     * {@code GET  /work-orders/:id} : get the "id" workOrder.
     *
     * @param id the id of the workOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<WorkOrderDTO>> getWorkOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WorkOrder : {}", id);
        Mono<WorkOrderDTO> workOrderDTO = workOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workOrderDTO);
    }

    /**
     * {@code DELETE  /work-orders/:id} : delete the "id" workOrder.
     *
     * @param id the id of the workOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteWorkOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WorkOrder : {}", id);
        return workOrderService
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
