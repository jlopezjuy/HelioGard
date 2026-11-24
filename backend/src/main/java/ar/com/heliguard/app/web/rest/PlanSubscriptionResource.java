package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.PlanSubscriptionRepository;
import ar.com.heliguard.app.service.PlanSubscriptionService;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.PlanSubscription}.
 */
@RestController
@RequestMapping("/api/plan-subscriptions")
public class PlanSubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlanSubscriptionResource.class);

    private static final String ENTITY_NAME = "planSubscription";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final PlanSubscriptionService planSubscriptionService;

    private final PlanSubscriptionRepository planSubscriptionRepository;

    public PlanSubscriptionResource(
        PlanSubscriptionService planSubscriptionService,
        PlanSubscriptionRepository planSubscriptionRepository
    ) {
        this.planSubscriptionService = planSubscriptionService;
        this.planSubscriptionRepository = planSubscriptionRepository;
    }

    /**
     * {@code POST  /plan-subscriptions} : Create a new planSubscription.
     *
     * @param planSubscriptionDTO the planSubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new planSubscriptionDTO, or with status {@code 400 (Bad Request)} if the planSubscription already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PlanSubscriptionDTO>> createPlanSubscription(@Valid @RequestBody PlanSubscriptionDTO planSubscriptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PlanSubscription : {}", planSubscriptionDTO);
        if (planSubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new planSubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return planSubscriptionService.save(planSubscriptionDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/plan-subscriptions/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /plan-subscriptions/:id} : Updates an existing planSubscription.
     *
     * @param id the id of the planSubscriptionDTO to save.
     * @param planSubscriptionDTO the planSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the planSubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the planSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PlanSubscriptionDTO>> updatePlanSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlanSubscriptionDTO planSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PlanSubscription : {}, {}", id, planSubscriptionDTO);
        if (planSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return planSubscriptionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return planSubscriptionService
                .update(planSubscriptionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /plan-subscriptions/:id} : Partial updates given fields of an existing planSubscription, field will ignore if it is null
     *
     * @param id the id of the planSubscriptionDTO to save.
     * @param planSubscriptionDTO the planSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the planSubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the planSubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the planSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PlanSubscriptionDTO>> partialUpdatePlanSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlanSubscriptionDTO planSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update PlanSubscription : {}, {}", id, planSubscriptionDTO);
        if (planSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return planSubscriptionRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<PlanSubscriptionDTO> result = planSubscriptionService.partialUpdate(planSubscriptionDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /plan-subscriptions} : get all the Plan Subscriptions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Plan Subscriptions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PlanSubscriptionDTO>>> getAllPlanSubscriptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of PlanSubscriptions");
        return planSubscriptionService
            .countAll()
            .zipWith(planSubscriptionService.findAll(pageable).collectList())
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
     * {@code GET  /plan-subscriptions/:id} : get the "id" planSubscription.
     *
     * @param id the id of the planSubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the planSubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlanSubscriptionDTO>> getPlanSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PlanSubscription : {}", id);
        Mono<PlanSubscriptionDTO> planSubscriptionDTO = planSubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(planSubscriptionDTO);
    }

    /**
     * {@code DELETE  /plan-subscriptions/:id} : delete the "id" planSubscription.
     *
     * @param id the id of the planSubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlanSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PlanSubscription : {}", id);
        return planSubscriptionService
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
