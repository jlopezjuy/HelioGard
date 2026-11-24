package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MissionSafetyCheckRepository;
import ar.com.heliguard.app.service.MissionSafetyCheckService;
import ar.com.heliguard.app.service.dto.MissionSafetyCheckDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MissionSafetyCheck}.
 */
@RestController
@RequestMapping("/api/mission-safety-checks")
public class MissionSafetyCheckResource {

    private static final Logger LOG = LoggerFactory.getLogger(MissionSafetyCheckResource.class);

    private static final String ENTITY_NAME = "missionSafetyCheck";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MissionSafetyCheckService missionSafetyCheckService;

    private final MissionSafetyCheckRepository missionSafetyCheckRepository;

    public MissionSafetyCheckResource(
        MissionSafetyCheckService missionSafetyCheckService,
        MissionSafetyCheckRepository missionSafetyCheckRepository
    ) {
        this.missionSafetyCheckService = missionSafetyCheckService;
        this.missionSafetyCheckRepository = missionSafetyCheckRepository;
    }

    /**
     * {@code POST  /mission-safety-checks} : Create a new missionSafetyCheck.
     *
     * @param missionSafetyCheckDTO the missionSafetyCheckDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new missionSafetyCheckDTO, or with status {@code 400 (Bad Request)} if the missionSafetyCheck already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MissionSafetyCheckDTO>> createMissionSafetyCheck(
        @Valid @RequestBody MissionSafetyCheckDTO missionSafetyCheckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save MissionSafetyCheck : {}", missionSafetyCheckDTO);
        if (missionSafetyCheckDTO.getId() != null) {
            throw new BadRequestAlertException("A new missionSafetyCheck cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return missionSafetyCheckService.save(missionSafetyCheckDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/mission-safety-checks/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /mission-safety-checks/:id} : Updates an existing missionSafetyCheck.
     *
     * @param id the id of the missionSafetyCheckDTO to save.
     * @param missionSafetyCheckDTO the missionSafetyCheckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionSafetyCheckDTO,
     * or with status {@code 400 (Bad Request)} if the missionSafetyCheckDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the missionSafetyCheckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MissionSafetyCheckDTO>> updateMissionSafetyCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MissionSafetyCheckDTO missionSafetyCheckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MissionSafetyCheck : {}, {}", id, missionSafetyCheckDTO);
        if (missionSafetyCheckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionSafetyCheckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return missionSafetyCheckRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return missionSafetyCheckService
                .update(missionSafetyCheckDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /mission-safety-checks/:id} : Partial updates given fields of an existing missionSafetyCheck, field will ignore if it is null
     *
     * @param id the id of the missionSafetyCheckDTO to save.
     * @param missionSafetyCheckDTO the missionSafetyCheckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionSafetyCheckDTO,
     * or with status {@code 400 (Bad Request)} if the missionSafetyCheckDTO is not valid,
     * or with status {@code 404 (Not Found)} if the missionSafetyCheckDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the missionSafetyCheckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MissionSafetyCheckDTO>> partialUpdateMissionSafetyCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MissionSafetyCheckDTO missionSafetyCheckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MissionSafetyCheck : {}, {}", id, missionSafetyCheckDTO);
        if (missionSafetyCheckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionSafetyCheckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return missionSafetyCheckRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MissionSafetyCheckDTO> result = missionSafetyCheckService.partialUpdate(missionSafetyCheckDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /mission-safety-checks} : get all the Mission Safety Checks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mission Safety Checks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MissionSafetyCheckDTO>> getAllMissionSafetyChecks(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MissionSafetyChecks");
        if (eagerload) {
            return missionSafetyCheckService.findAllWithEagerRelationships().collectList();
        } else {
            return missionSafetyCheckService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /mission-safety-checks} : get all the Mission Safety Checks as a stream.
     * @return the {@link Flux} of Mission Safety Checks.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MissionSafetyCheckDTO> getAllMissionSafetyChecksAsStream() {
        LOG.debug("REST request to get all MissionSafetyChecks as a stream");
        return missionSafetyCheckService.findAll();
    }

    /**
     * {@code GET  /mission-safety-checks/:id} : get the "id" missionSafetyCheck.
     *
     * @param id the id of the missionSafetyCheckDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the missionSafetyCheckDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MissionSafetyCheckDTO>> getMissionSafetyCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MissionSafetyCheck : {}", id);
        Mono<MissionSafetyCheckDTO> missionSafetyCheckDTO = missionSafetyCheckService.findOne(id);
        return ResponseUtil.wrapOrNotFound(missionSafetyCheckDTO);
    }

    /**
     * {@code DELETE  /mission-safety-checks/:id} : delete the "id" missionSafetyCheck.
     *
     * @param id the id of the missionSafetyCheckDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMissionSafetyCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MissionSafetyCheck : {}", id);
        return missionSafetyCheckService
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
