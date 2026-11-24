package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.service.EquipmentModelService;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.EquipmentModel}.
 */
@RestController
@RequestMapping("/api/equipment-models")
public class EquipmentModelResource {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentModelResource.class);

    private static final String ENTITY_NAME = "equipmentModel";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final EquipmentModelService equipmentModelService;

    private final EquipmentModelRepository equipmentModelRepository;

    public EquipmentModelResource(EquipmentModelService equipmentModelService, EquipmentModelRepository equipmentModelRepository) {
        this.equipmentModelService = equipmentModelService;
        this.equipmentModelRepository = equipmentModelRepository;
    }

    /**
     * {@code POST  /equipment-models} : Create a new equipmentModel.
     *
     * @param equipmentModelDTO the equipmentModelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentModelDTO, or with status {@code 400 (Bad Request)} if the equipmentModel already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EquipmentModelDTO>> createEquipmentModel(@Valid @RequestBody EquipmentModelDTO equipmentModelDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EquipmentModel : {}", equipmentModelDTO);
        if (equipmentModelDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return equipmentModelService.save(equipmentModelDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/equipment-models/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /equipment-models/:id} : Updates an existing equipmentModel.
     *
     * @param id the id of the equipmentModelDTO to save.
     * @param equipmentModelDTO the equipmentModelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentModelDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentModelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentModelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EquipmentModelDTO>> updateEquipmentModel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EquipmentModelDTO equipmentModelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EquipmentModel : {}, {}", id, equipmentModelDTO);
        if (equipmentModelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentModelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipmentModelRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return equipmentModelService
                .update(equipmentModelDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /equipment-models/:id} : Partial updates given fields of an existing equipmentModel, field will ignore if it is null
     *
     * @param id the id of the equipmentModelDTO to save.
     * @param equipmentModelDTO the equipmentModelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentModelDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentModelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the equipmentModelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the equipmentModelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EquipmentModelDTO>> partialUpdateEquipmentModel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EquipmentModelDTO equipmentModelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update EquipmentModel : {}, {}", id, equipmentModelDTO);
        if (equipmentModelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentModelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return equipmentModelRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<EquipmentModelDTO> result = equipmentModelService.partialUpdate(equipmentModelDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /equipment-models} : get all the Equipment Models.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Equipment Models in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<EquipmentModelDTO>> getAllEquipmentModels() {
        LOG.debug("REST request to get all EquipmentModels");
        return equipmentModelService.findAll().collectList();
    }

    /**
     * {@code GET  /equipment-models} : get all the Equipment Models as a stream.
     * @return the {@link Flux} of Equipment Models.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EquipmentModelDTO> getAllEquipmentModelsAsStream() {
        LOG.debug("REST request to get all EquipmentModels as a stream");
        return equipmentModelService.findAll();
    }

    /**
     * {@code GET  /equipment-models/:id} : get the "id" equipmentModel.
     *
     * @param id the id of the equipmentModelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentModelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EquipmentModelDTO>> getEquipmentModel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EquipmentModel : {}", id);
        Mono<EquipmentModelDTO> equipmentModelDTO = equipmentModelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentModelDTO);
    }

    /**
     * {@code DELETE  /equipment-models/:id} : delete the "id" equipmentModel.
     *
     * @param id the id of the equipmentModelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEquipmentModel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EquipmentModel : {}", id);
        return equipmentModelService
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
