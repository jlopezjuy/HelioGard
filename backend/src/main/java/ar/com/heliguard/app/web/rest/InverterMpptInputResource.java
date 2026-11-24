package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.InverterMpptInputRepository;
import ar.com.heliguard.app.service.InverterMpptInputService;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.InverterMpptInput}.
 */
@RestController
@RequestMapping("/api/inverter-mppt-inputs")
public class InverterMpptInputResource {

    private static final Logger LOG = LoggerFactory.getLogger(InverterMpptInputResource.class);

    private static final String ENTITY_NAME = "inverterMpptInput";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final InverterMpptInputService inverterMpptInputService;

    private final InverterMpptInputRepository inverterMpptInputRepository;

    public InverterMpptInputResource(
        InverterMpptInputService inverterMpptInputService,
        InverterMpptInputRepository inverterMpptInputRepository
    ) {
        this.inverterMpptInputService = inverterMpptInputService;
        this.inverterMpptInputRepository = inverterMpptInputRepository;
    }

    /**
     * {@code POST  /inverter-mppt-inputs} : Create a new inverterMpptInput.
     *
     * @param inverterMpptInputDTO the inverterMpptInputDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inverterMpptInputDTO, or with status {@code 400 (Bad Request)} if the inverterMpptInput already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InverterMpptInputDTO>> createInverterMpptInput(@Valid @RequestBody InverterMpptInputDTO inverterMpptInputDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InverterMpptInput : {}", inverterMpptInputDTO);
        if (inverterMpptInputDTO.getId() != null) {
            throw new BadRequestAlertException("A new inverterMpptInput cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return inverterMpptInputService.save(inverterMpptInputDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/inverter-mppt-inputs/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /inverter-mppt-inputs/:id} : Updates an existing inverterMpptInput.
     *
     * @param id the id of the inverterMpptInputDTO to save.
     * @param inverterMpptInputDTO the inverterMpptInputDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inverterMpptInputDTO,
     * or with status {@code 400 (Bad Request)} if the inverterMpptInputDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inverterMpptInputDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InverterMpptInputDTO>> updateInverterMpptInput(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InverterMpptInputDTO inverterMpptInputDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InverterMpptInput : {}, {}", id, inverterMpptInputDTO);
        if (inverterMpptInputDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inverterMpptInputDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return inverterMpptInputRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return inverterMpptInputService
                .update(inverterMpptInputDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /inverter-mppt-inputs/:id} : Partial updates given fields of an existing inverterMpptInput, field will ignore if it is null
     *
     * @param id the id of the inverterMpptInputDTO to save.
     * @param inverterMpptInputDTO the inverterMpptInputDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inverterMpptInputDTO,
     * or with status {@code 400 (Bad Request)} if the inverterMpptInputDTO is not valid,
     * or with status {@code 404 (Not Found)} if the inverterMpptInputDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the inverterMpptInputDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InverterMpptInputDTO>> partialUpdateInverterMpptInput(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InverterMpptInputDTO inverterMpptInputDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update InverterMpptInput : {}, {}", id, inverterMpptInputDTO);
        if (inverterMpptInputDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inverterMpptInputDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return inverterMpptInputRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<InverterMpptInputDTO> result = inverterMpptInputService.partialUpdate(inverterMpptInputDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /inverter-mppt-inputs} : get all the Inverter Mppt Inputs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Inverter Mppt Inputs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<InverterMpptInputDTO>> getAllInverterMpptInputs(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InverterMpptInputs");
        if (eagerload) {
            return inverterMpptInputService.findAllWithEagerRelationships().collectList();
        } else {
            return inverterMpptInputService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /inverter-mppt-inputs} : get all the Inverter Mppt Inputs as a stream.
     * @return the {@link Flux} of Inverter Mppt Inputs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<InverterMpptInputDTO> getAllInverterMpptInputsAsStream() {
        LOG.debug("REST request to get all InverterMpptInputs as a stream");
        return inverterMpptInputService.findAll();
    }

    /**
     * {@code GET  /inverter-mppt-inputs/:id} : get the "id" inverterMpptInput.
     *
     * @param id the id of the inverterMpptInputDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inverterMpptInputDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InverterMpptInputDTO>> getInverterMpptInput(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InverterMpptInput : {}", id);
        Mono<InverterMpptInputDTO> inverterMpptInputDTO = inverterMpptInputService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inverterMpptInputDTO);
    }

    /**
     * {@code DELETE  /inverter-mppt-inputs/:id} : delete the "id" inverterMpptInput.
     *
     * @param id the id of the inverterMpptInputDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInverterMpptInput(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InverterMpptInput : {}", id);
        return inverterMpptInputService
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
