package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.service.EnergyModelConfigurationService;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.EnergyModelConfiguration}.
 */
@RestController
@RequestMapping("/api/energy-model-configurations")
public class EnergyModelConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnergyModelConfigurationResource.class);

    private static final String ENTITY_NAME = "energyModelConfiguration";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final EnergyModelConfigurationService energyModelConfigurationService;

    private final EnergyModelConfigurationRepository energyModelConfigurationRepository;

    public EnergyModelConfigurationResource(
        EnergyModelConfigurationService energyModelConfigurationService,
        EnergyModelConfigurationRepository energyModelConfigurationRepository
    ) {
        this.energyModelConfigurationService = energyModelConfigurationService;
        this.energyModelConfigurationRepository = energyModelConfigurationRepository;
    }

    /**
     * {@code POST  /energy-model-configurations} : Create a new energyModelConfiguration.
     *
     * @param energyModelConfigurationDTO the energyModelConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new energyModelConfigurationDTO, or with status {@code 400 (Bad Request)} if the energyModelConfiguration already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EnergyModelConfigurationDTO>> createEnergyModelConfiguration(
        @Valid @RequestBody EnergyModelConfigurationDTO energyModelConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save EnergyModelConfiguration : {}", energyModelConfigurationDTO);
        if (energyModelConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new energyModelConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return energyModelConfigurationService.save(energyModelConfigurationDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/energy-model-configurations/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /energy-model-configurations/:id} : Updates an existing energyModelConfiguration.
     *
     * @param id the id of the energyModelConfigurationDTO to save.
     * @param energyModelConfigurationDTO the energyModelConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated energyModelConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the energyModelConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the energyModelConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EnergyModelConfigurationDTO>> updateEnergyModelConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnergyModelConfigurationDTO energyModelConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EnergyModelConfiguration : {}, {}", id, energyModelConfigurationDTO);
        if (energyModelConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, energyModelConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return energyModelConfigurationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return energyModelConfigurationService
                .update(energyModelConfigurationDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /energy-model-configurations/:id} : Partial updates given fields of an existing energyModelConfiguration, field will ignore if it is null
     *
     * @param id the id of the energyModelConfigurationDTO to save.
     * @param energyModelConfigurationDTO the energyModelConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated energyModelConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the energyModelConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the energyModelConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the energyModelConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EnergyModelConfigurationDTO>> partialUpdateEnergyModelConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnergyModelConfigurationDTO energyModelConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update EnergyModelConfiguration : {}, {}", id, energyModelConfigurationDTO);
        if (energyModelConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, energyModelConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return energyModelConfigurationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<EnergyModelConfigurationDTO> result = energyModelConfigurationService.partialUpdate(energyModelConfigurationDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /energy-model-configurations} : get all the Energy Model Configurations.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Energy Model Configurations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<EnergyModelConfigurationDTO>> getAllEnergyModelConfigurations(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EnergyModelConfigurations");
        if (eagerload) {
            return energyModelConfigurationService.findAllWithEagerRelationships().collectList();
        } else {
            return energyModelConfigurationService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /energy-model-configurations} : get all the Energy Model Configurations as a stream.
     * @return the {@link Flux} of Energy Model Configurations.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EnergyModelConfigurationDTO> getAllEnergyModelConfigurationsAsStream() {
        LOG.debug("REST request to get all EnergyModelConfigurations as a stream");
        return energyModelConfigurationService.findAll();
    }

    /**
     * {@code GET  /energy-model-configurations/:id} : get the "id" energyModelConfiguration.
     *
     * @param id the id of the energyModelConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the energyModelConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EnergyModelConfigurationDTO>> getEnergyModelConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EnergyModelConfiguration : {}", id);
        Mono<EnergyModelConfigurationDTO> energyModelConfigurationDTO = energyModelConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(energyModelConfigurationDTO);
    }

    /**
     * {@code DELETE  /energy-model-configurations/:id} : delete the "id" energyModelConfiguration.
     *
     * @param id the id of the energyModelConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEnergyModelConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EnergyModelConfiguration : {}", id);
        return energyModelConfigurationService
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
