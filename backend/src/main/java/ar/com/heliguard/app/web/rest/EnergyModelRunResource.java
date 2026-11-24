package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.service.EnergyModelRunService;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.EnergyModelRun}.
 */
@RestController
@RequestMapping("/api/energy-model-runs")
public class EnergyModelRunResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnergyModelRunResource.class);

    private static final String ENTITY_NAME = "energyModelRun";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final EnergyModelRunService energyModelRunService;

    private final EnergyModelRunRepository energyModelRunRepository;

    public EnergyModelRunResource(EnergyModelRunService energyModelRunService, EnergyModelRunRepository energyModelRunRepository) {
        this.energyModelRunService = energyModelRunService;
        this.energyModelRunRepository = energyModelRunRepository;
    }

    /**
     * {@code POST  /energy-model-runs} : Create a new energyModelRun.
     *
     * @param energyModelRunDTO the energyModelRunDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new energyModelRunDTO, or with status {@code 400 (Bad Request)} if the energyModelRun already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EnergyModelRunDTO>> createEnergyModelRun(@Valid @RequestBody EnergyModelRunDTO energyModelRunDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EnergyModelRun : {}", energyModelRunDTO);
        if (energyModelRunDTO.getId() != null) {
            throw new BadRequestAlertException("A new energyModelRun cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return energyModelRunService.save(energyModelRunDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/energy-model-runs/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /energy-model-runs/:id} : Updates an existing energyModelRun.
     *
     * @param id the id of the energyModelRunDTO to save.
     * @param energyModelRunDTO the energyModelRunDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated energyModelRunDTO,
     * or with status {@code 400 (Bad Request)} if the energyModelRunDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the energyModelRunDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EnergyModelRunDTO>> updateEnergyModelRun(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnergyModelRunDTO energyModelRunDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EnergyModelRun : {}, {}", id, energyModelRunDTO);
        if (energyModelRunDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, energyModelRunDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return energyModelRunRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return energyModelRunService
                .update(energyModelRunDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /energy-model-runs/:id} : Partial updates given fields of an existing energyModelRun, field will ignore if it is null
     *
     * @param id the id of the energyModelRunDTO to save.
     * @param energyModelRunDTO the energyModelRunDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated energyModelRunDTO,
     * or with status {@code 400 (Bad Request)} if the energyModelRunDTO is not valid,
     * or with status {@code 404 (Not Found)} if the energyModelRunDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the energyModelRunDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EnergyModelRunDTO>> partialUpdateEnergyModelRun(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnergyModelRunDTO energyModelRunDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update EnergyModelRun : {}, {}", id, energyModelRunDTO);
        if (energyModelRunDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, energyModelRunDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return energyModelRunRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<EnergyModelRunDTO> result = energyModelRunService.partialUpdate(energyModelRunDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /energy-model-runs} : get all the Energy Model Runs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Energy Model Runs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<EnergyModelRunDTO>> getAllEnergyModelRuns(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EnergyModelRuns");
        if (eagerload) {
            return energyModelRunService.findAllWithEagerRelationships().collectList();
        } else {
            return energyModelRunService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /energy-model-runs} : get all the Energy Model Runs as a stream.
     * @return the {@link Flux} of Energy Model Runs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EnergyModelRunDTO> getAllEnergyModelRunsAsStream() {
        LOG.debug("REST request to get all EnergyModelRuns as a stream");
        return energyModelRunService.findAll();
    }

    /**
     * {@code GET  /energy-model-runs/:id} : get the "id" energyModelRun.
     *
     * @param id the id of the energyModelRunDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the energyModelRunDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EnergyModelRunDTO>> getEnergyModelRun(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EnergyModelRun : {}", id);
        Mono<EnergyModelRunDTO> energyModelRunDTO = energyModelRunService.findOne(id);
        return ResponseUtil.wrapOrNotFound(energyModelRunDTO);
    }

    /**
     * {@code DELETE  /energy-model-runs/:id} : delete the "id" energyModelRun.
     *
     * @param id the id of the energyModelRunDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEnergyModelRun(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EnergyModelRun : {}", id);
        return energyModelRunService
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
