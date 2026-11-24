package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.ServiceProposalRepository;
import ar.com.heliguard.app.service.ServiceProposalService;
import ar.com.heliguard.app.service.dto.ServiceProposalDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.ServiceProposal}.
 */
@RestController
@RequestMapping("/api/service-proposals")
public class ServiceProposalResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceProposalResource.class);

    private static final String ENTITY_NAME = "serviceProposal";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final ServiceProposalService serviceProposalService;

    private final ServiceProposalRepository serviceProposalRepository;

    public ServiceProposalResource(ServiceProposalService serviceProposalService, ServiceProposalRepository serviceProposalRepository) {
        this.serviceProposalService = serviceProposalService;
        this.serviceProposalRepository = serviceProposalRepository;
    }

    /**
     * {@code POST  /service-proposals} : Create a new serviceProposal.
     *
     * @param serviceProposalDTO the serviceProposalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceProposalDTO, or with status {@code 400 (Bad Request)} if the serviceProposal already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ServiceProposalDTO>> createServiceProposal(@Valid @RequestBody ServiceProposalDTO serviceProposalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceProposal : {}", serviceProposalDTO);
        if (serviceProposalDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceProposal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return serviceProposalService.save(serviceProposalDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/service-proposals/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /service-proposals/:id} : Updates an existing serviceProposal.
     *
     * @param id the id of the serviceProposalDTO to save.
     * @param serviceProposalDTO the serviceProposalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceProposalDTO,
     * or with status {@code 400 (Bad Request)} if the serviceProposalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceProposalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ServiceProposalDTO>> updateServiceProposal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceProposalDTO serviceProposalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceProposal : {}, {}", id, serviceProposalDTO);
        if (serviceProposalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceProposalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return serviceProposalRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return serviceProposalService
                .update(serviceProposalDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /service-proposals/:id} : Partial updates given fields of an existing serviceProposal, field will ignore if it is null
     *
     * @param id the id of the serviceProposalDTO to save.
     * @param serviceProposalDTO the serviceProposalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceProposalDTO,
     * or with status {@code 400 (Bad Request)} if the serviceProposalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceProposalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceProposalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ServiceProposalDTO>> partialUpdateServiceProposal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceProposalDTO serviceProposalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ServiceProposal : {}, {}", id, serviceProposalDTO);
        if (serviceProposalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceProposalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return serviceProposalRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<ServiceProposalDTO> result = serviceProposalService.partialUpdate(serviceProposalDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /service-proposals} : get all the Service Proposals.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Service Proposals in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ServiceProposalDTO>>> getAllServiceProposals(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ServiceProposals");
        return serviceProposalService
            .countAll()
            .zipWith(serviceProposalService.findAll(pageable).collectList())
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
     * {@code GET  /service-proposals/:id} : get the "id" serviceProposal.
     *
     * @param id the id of the serviceProposalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceProposalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ServiceProposalDTO>> getServiceProposal(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceProposal : {}", id);
        Mono<ServiceProposalDTO> serviceProposalDTO = serviceProposalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceProposalDTO);
    }

    /**
     * {@code DELETE  /service-proposals/:id} : delete the "id" serviceProposal.
     *
     * @param id the id of the serviceProposalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteServiceProposal(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceProposal : {}", id);
        return serviceProposalService
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
