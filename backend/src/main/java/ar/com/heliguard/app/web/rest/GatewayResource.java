package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.service.GatewayService;
import ar.com.heliguard.app.service.dto.GatewayDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.Gateway}.
 */
@RestController
@RequestMapping("/api/gateways")
public class GatewayResource {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayResource.class);

    private static final String ENTITY_NAME = "gateway";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final GatewayService gatewayService;

    private final GatewayRepository gatewayRepository;

    public GatewayResource(GatewayService gatewayService, GatewayRepository gatewayRepository) {
        this.gatewayService = gatewayService;
        this.gatewayRepository = gatewayRepository;
    }

    /**
     * {@code POST  /gateways} : Create a new gateway.
     *
     * @param gatewayDTO the gatewayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gatewayDTO, or with status {@code 400 (Bad Request)} if the gateway already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<GatewayDTO>> createGateway(@Valid @RequestBody GatewayDTO gatewayDTO) throws URISyntaxException {
        LOG.debug("REST request to save Gateway : {}", gatewayDTO);
        if (gatewayDTO.getId() != null) {
            throw new BadRequestAlertException("A new gateway cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return gatewayService.save(gatewayDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/gateways/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /gateways/:id} : Updates an existing gateway.
     *
     * @param id the id of the gatewayDTO to save.
     * @param gatewayDTO the gatewayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gatewayDTO,
     * or with status {@code 400 (Bad Request)} if the gatewayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gatewayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<GatewayDTO>> updateGateway(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GatewayDTO gatewayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Gateway : {}, {}", id, gatewayDTO);
        if (gatewayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gatewayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gatewayRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return gatewayService
                .update(gatewayDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /gateways/:id} : Partial updates given fields of an existing gateway, field will ignore if it is null
     *
     * @param id the id of the gatewayDTO to save.
     * @param gatewayDTO the gatewayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gatewayDTO,
     * or with status {@code 400 (Bad Request)} if the gatewayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gatewayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gatewayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<GatewayDTO>> partialUpdateGateway(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GatewayDTO gatewayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Gateway : {}, {}", id, gatewayDTO);
        if (gatewayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gatewayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gatewayRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<GatewayDTO> result = gatewayService.partialUpdate(gatewayDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /gateways} : get all the Gateways.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Gateways in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<GatewayDTO>>> getAllGateways(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Gateways");
        return gatewayService
            .countAll()
            .zipWith(gatewayService.findAll(pageable).collectList())
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
     * {@code GET  /gateways/:id} : get the "id" gateway.
     *
     * @param id the id of the gatewayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gatewayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<GatewayDTO>> getGateway(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Gateway : {}", id);
        Mono<GatewayDTO> gatewayDTO = gatewayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gatewayDTO);
    }

    /**
     * {@code DELETE  /gateways/:id} : delete the "id" gateway.
     *
     * @param id the id of the gatewayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteGateway(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Gateway : {}", id);
        return gatewayService
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
