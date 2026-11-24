package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.service.MissionAuthorizationService;
import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.MissionAuthorization}.
 */
@RestController
@RequestMapping("/api/mission-authorizations")
public class MissionAuthorizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(MissionAuthorizationResource.class);

    private static final String ENTITY_NAME = "missionAuthorization";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final MissionAuthorizationService missionAuthorizationService;

    private final MissionAuthorizationRepository missionAuthorizationRepository;

    public MissionAuthorizationResource(
        MissionAuthorizationService missionAuthorizationService,
        MissionAuthorizationRepository missionAuthorizationRepository
    ) {
        this.missionAuthorizationService = missionAuthorizationService;
        this.missionAuthorizationRepository = missionAuthorizationRepository;
    }

    /**
     * {@code POST  /mission-authorizations} : Create a new missionAuthorization.
     *
     * @param missionAuthorizationDTO the missionAuthorizationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new missionAuthorizationDTO, or with status {@code 400 (Bad Request)} if the missionAuthorization already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MissionAuthorizationDTO>> createMissionAuthorization(
        @Valid @RequestBody MissionAuthorizationDTO missionAuthorizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save MissionAuthorization : {}", missionAuthorizationDTO);
        if (missionAuthorizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new missionAuthorization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return missionAuthorizationService.save(missionAuthorizationDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/mission-authorizations/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /mission-authorizations/:id} : Updates an existing missionAuthorization.
     *
     * @param id the id of the missionAuthorizationDTO to save.
     * @param missionAuthorizationDTO the missionAuthorizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionAuthorizationDTO,
     * or with status {@code 400 (Bad Request)} if the missionAuthorizationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the missionAuthorizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MissionAuthorizationDTO>> updateMissionAuthorization(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MissionAuthorizationDTO missionAuthorizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MissionAuthorization : {}, {}", id, missionAuthorizationDTO);
        if (missionAuthorizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionAuthorizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return missionAuthorizationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return missionAuthorizationService
                .update(missionAuthorizationDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /mission-authorizations/:id} : Partial updates given fields of an existing missionAuthorization, field will ignore if it is null
     *
     * @param id the id of the missionAuthorizationDTO to save.
     * @param missionAuthorizationDTO the missionAuthorizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated missionAuthorizationDTO,
     * or with status {@code 400 (Bad Request)} if the missionAuthorizationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the missionAuthorizationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the missionAuthorizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MissionAuthorizationDTO>> partialUpdateMissionAuthorization(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MissionAuthorizationDTO missionAuthorizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MissionAuthorization : {}, {}", id, missionAuthorizationDTO);
        if (missionAuthorizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, missionAuthorizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return missionAuthorizationRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<MissionAuthorizationDTO> result = missionAuthorizationService.partialUpdate(missionAuthorizationDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /mission-authorizations} : get all the Mission Authorizations.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mission Authorizations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MissionAuthorizationDTO>> getAllMissionAuthorizations(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MissionAuthorizations");
        if (eagerload) {
            return missionAuthorizationService.findAllWithEagerRelationships().collectList();
        } else {
            return missionAuthorizationService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /mission-authorizations} : get all the Mission Authorizations as a stream.
     * @return the {@link Flux} of Mission Authorizations.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MissionAuthorizationDTO> getAllMissionAuthorizationsAsStream() {
        LOG.debug("REST request to get all MissionAuthorizations as a stream");
        return missionAuthorizationService.findAll();
    }

    /**
     * {@code GET  /mission-authorizations/:id} : get the "id" missionAuthorization.
     *
     * @param id the id of the missionAuthorizationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the missionAuthorizationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MissionAuthorizationDTO>> getMissionAuthorization(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MissionAuthorization : {}", id);
        Mono<MissionAuthorizationDTO> missionAuthorizationDTO = missionAuthorizationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(missionAuthorizationDTO);
    }

    /**
     * {@code DELETE  /mission-authorizations/:id} : delete the "id" missionAuthorization.
     *
     * @param id the id of the missionAuthorizationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMissionAuthorization(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MissionAuthorization : {}", id);
        return missionAuthorizationService
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
