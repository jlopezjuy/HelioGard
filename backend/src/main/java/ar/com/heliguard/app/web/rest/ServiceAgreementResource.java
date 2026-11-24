package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.ServiceAgreementRepository;
import ar.com.heliguard.app.service.ServiceAgreementService;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.ServiceAgreement}.
 */
@RestController
@RequestMapping("/api/service-agreements")
public class ServiceAgreementResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceAgreementResource.class);

    private static final String ENTITY_NAME = "serviceAgreement";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final ServiceAgreementService serviceAgreementService;

    private final ServiceAgreementRepository serviceAgreementRepository;

    public ServiceAgreementResource(
        ServiceAgreementService serviceAgreementService,
        ServiceAgreementRepository serviceAgreementRepository
    ) {
        this.serviceAgreementService = serviceAgreementService;
        this.serviceAgreementRepository = serviceAgreementRepository;
    }

    /**
     * {@code POST  /service-agreements} : Create a new serviceAgreement.
     *
     * @param serviceAgreementDTO the serviceAgreementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceAgreementDTO, or with status {@code 400 (Bad Request)} if the serviceAgreement already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ServiceAgreementDTO>> createServiceAgreement(@Valid @RequestBody ServiceAgreementDTO serviceAgreementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceAgreement : {}", serviceAgreementDTO);
        if (serviceAgreementDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceAgreement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return serviceAgreementService.save(serviceAgreementDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/service-agreements/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /service-agreements/:id} : Updates an existing serviceAgreement.
     *
     * @param id the id of the serviceAgreementDTO to save.
     * @param serviceAgreementDTO the serviceAgreementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceAgreementDTO,
     * or with status {@code 400 (Bad Request)} if the serviceAgreementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceAgreementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ServiceAgreementDTO>> updateServiceAgreement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceAgreementDTO serviceAgreementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceAgreement : {}, {}", id, serviceAgreementDTO);
        if (serviceAgreementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceAgreementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return serviceAgreementRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return serviceAgreementService
                .update(serviceAgreementDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /service-agreements/:id} : Partial updates given fields of an existing serviceAgreement, field will ignore if it is null
     *
     * @param id the id of the serviceAgreementDTO to save.
     * @param serviceAgreementDTO the serviceAgreementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceAgreementDTO,
     * or with status {@code 400 (Bad Request)} if the serviceAgreementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceAgreementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceAgreementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ServiceAgreementDTO>> partialUpdateServiceAgreement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceAgreementDTO serviceAgreementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ServiceAgreement : {}, {}", id, serviceAgreementDTO);
        if (serviceAgreementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceAgreementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return serviceAgreementRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<ServiceAgreementDTO> result = serviceAgreementService.partialUpdate(serviceAgreementDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /service-agreements} : get all the Service Agreements.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Service Agreements in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ServiceAgreementDTO>> getAllServiceAgreements(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ServiceAgreements");
        if (eagerload) {
            return serviceAgreementService.findAllWithEagerRelationships().collectList();
        } else {
            return serviceAgreementService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /service-agreements} : get all the Service Agreements as a stream.
     * @return the {@link Flux} of Service Agreements.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ServiceAgreementDTO> getAllServiceAgreementsAsStream() {
        LOG.debug("REST request to get all ServiceAgreements as a stream");
        return serviceAgreementService.findAll();
    }

    /**
     * {@code GET  /service-agreements/:id} : get the "id" serviceAgreement.
     *
     * @param id the id of the serviceAgreementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceAgreementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ServiceAgreementDTO>> getServiceAgreement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceAgreement : {}", id);
        Mono<ServiceAgreementDTO> serviceAgreementDTO = serviceAgreementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceAgreementDTO);
    }

    /**
     * {@code DELETE  /service-agreements/:id} : delete the "id" serviceAgreement.
     *
     * @param id the id of the serviceAgreementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteServiceAgreement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceAgreement : {}", id);
        return serviceAgreementService
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
