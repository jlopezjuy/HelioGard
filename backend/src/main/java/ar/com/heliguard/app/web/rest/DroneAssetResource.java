package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.service.DroneAssetService;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.DroneAsset}.
 */
@RestController
@RequestMapping("/api/drone-assets")
public class DroneAssetResource {

    private static final Logger LOG = LoggerFactory.getLogger(DroneAssetResource.class);

    private static final String ENTITY_NAME = "droneAsset";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final DroneAssetService droneAssetService;

    private final DroneAssetRepository droneAssetRepository;

    public DroneAssetResource(DroneAssetService droneAssetService, DroneAssetRepository droneAssetRepository) {
        this.droneAssetService = droneAssetService;
        this.droneAssetRepository = droneAssetRepository;
    }

    /**
     * {@code POST  /drone-assets} : Create a new droneAsset.
     *
     * @param droneAssetDTO the droneAssetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new droneAssetDTO, or with status {@code 400 (Bad Request)} if the droneAsset already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DroneAssetDTO>> createDroneAsset(@Valid @RequestBody DroneAssetDTO droneAssetDTO) throws URISyntaxException {
        LOG.debug("REST request to save DroneAsset : {}", droneAssetDTO);
        if (droneAssetDTO.getId() != null) {
            throw new BadRequestAlertException("A new droneAsset cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return droneAssetService.save(droneAssetDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/drone-assets/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /drone-assets/:id} : Updates an existing droneAsset.
     *
     * @param id the id of the droneAssetDTO to save.
     * @param droneAssetDTO the droneAssetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneAssetDTO,
     * or with status {@code 400 (Bad Request)} if the droneAssetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the droneAssetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DroneAssetDTO>> updateDroneAsset(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DroneAssetDTO droneAssetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DroneAsset : {}, {}", id, droneAssetDTO);
        if (droneAssetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneAssetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneAssetRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return droneAssetService
                .update(droneAssetDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /drone-assets/:id} : Partial updates given fields of an existing droneAsset, field will ignore if it is null
     *
     * @param id the id of the droneAssetDTO to save.
     * @param droneAssetDTO the droneAssetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droneAssetDTO,
     * or with status {@code 400 (Bad Request)} if the droneAssetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the droneAssetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the droneAssetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DroneAssetDTO>> partialUpdateDroneAsset(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DroneAssetDTO droneAssetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update DroneAsset : {}, {}", id, droneAssetDTO);
        if (droneAssetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, droneAssetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return droneAssetRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DroneAssetDTO> result = droneAssetService.partialUpdate(droneAssetDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /drone-assets} : get all the Drone Assets.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Drone Assets in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DroneAssetDTO>> getAllDroneAssets(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all DroneAssets");
        if (eagerload) {
            return droneAssetService.findAllWithEagerRelationships().collectList();
        } else {
            return droneAssetService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /drone-assets} : get all the Drone Assets as a stream.
     * @return the {@link Flux} of Drone Assets.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DroneAssetDTO> getAllDroneAssetsAsStream() {
        LOG.debug("REST request to get all DroneAssets as a stream");
        return droneAssetService.findAll();
    }

    /**
     * {@code GET  /drone-assets/:id} : get the "id" droneAsset.
     *
     * @param id the id of the droneAssetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the droneAssetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DroneAssetDTO>> getDroneAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DroneAsset : {}", id);
        Mono<DroneAssetDTO> droneAssetDTO = droneAssetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(droneAssetDTO);
    }

    /**
     * {@code DELETE  /drone-assets/:id} : delete the "id" droneAsset.
     *
     * @param id the id of the droneAssetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDroneAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DroneAsset : {}", id);
        return droneAssetService
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
