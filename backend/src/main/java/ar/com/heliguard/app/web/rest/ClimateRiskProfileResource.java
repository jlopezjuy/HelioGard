package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.ClimateRiskProfileRepository;
import ar.com.heliguard.app.service.ClimateRiskProfileService;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.ClimateRiskProfile}.
 */
@RestController
@RequestMapping("/api/climate-risk-profiles")
public class ClimateRiskProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClimateRiskProfileResource.class);

    private static final String ENTITY_NAME = "climateRiskProfile";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final ClimateRiskProfileService climateRiskProfileService;

    private final ClimateRiskProfileRepository climateRiskProfileRepository;

    public ClimateRiskProfileResource(
        ClimateRiskProfileService climateRiskProfileService,
        ClimateRiskProfileRepository climateRiskProfileRepository
    ) {
        this.climateRiskProfileService = climateRiskProfileService;
        this.climateRiskProfileRepository = climateRiskProfileRepository;
    }

    /**
     * {@code POST  /climate-risk-profiles} : Create a new climateRiskProfile.
     *
     * @param climateRiskProfileDTO the climateRiskProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new climateRiskProfileDTO, or with status {@code 400 (Bad Request)} if the climateRiskProfile already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ClimateRiskProfileDTO>> createClimateRiskProfile(
        @Valid @RequestBody ClimateRiskProfileDTO climateRiskProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ClimateRiskProfile : {}", climateRiskProfileDTO);
        if (climateRiskProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new climateRiskProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return climateRiskProfileService.save(climateRiskProfileDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/climate-risk-profiles/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /climate-risk-profiles/:id} : Updates an existing climateRiskProfile.
     *
     * @param id the id of the climateRiskProfileDTO to save.
     * @param climateRiskProfileDTO the climateRiskProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated climateRiskProfileDTO,
     * or with status {@code 400 (Bad Request)} if the climateRiskProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the climateRiskProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClimateRiskProfileDTO>> updateClimateRiskProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClimateRiskProfileDTO climateRiskProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClimateRiskProfile : {}, {}", id, climateRiskProfileDTO);
        if (climateRiskProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, climateRiskProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return climateRiskProfileRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return climateRiskProfileService
                .update(climateRiskProfileDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /climate-risk-profiles/:id} : Partial updates given fields of an existing climateRiskProfile, field will ignore if it is null
     *
     * @param id the id of the climateRiskProfileDTO to save.
     * @param climateRiskProfileDTO the climateRiskProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated climateRiskProfileDTO,
     * or with status {@code 400 (Bad Request)} if the climateRiskProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the climateRiskProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the climateRiskProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClimateRiskProfileDTO>> partialUpdateClimateRiskProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClimateRiskProfileDTO climateRiskProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ClimateRiskProfile : {}, {}", id, climateRiskProfileDTO);
        if (climateRiskProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, climateRiskProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return climateRiskProfileRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<ClimateRiskProfileDTO> result = climateRiskProfileService.partialUpdate(climateRiskProfileDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /climate-risk-profiles} : get all the Climate Risk Profiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Climate Risk Profiles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ClimateRiskProfileDTO>> getAllClimateRiskProfiles() {
        LOG.debug("REST request to get all ClimateRiskProfiles");
        return climateRiskProfileService.findAll().collectList();
    }

    /**
     * {@code GET  /climate-risk-profiles} : get all the Climate Risk Profiles as a stream.
     * @return the {@link Flux} of Climate Risk Profiles.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ClimateRiskProfileDTO> getAllClimateRiskProfilesAsStream() {
        LOG.debug("REST request to get all ClimateRiskProfiles as a stream");
        return climateRiskProfileService.findAll();
    }

    /**
     * {@code GET  /climate-risk-profiles/:id} : get the "id" climateRiskProfile.
     *
     * @param id the id of the climateRiskProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the climateRiskProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClimateRiskProfileDTO>> getClimateRiskProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClimateRiskProfile : {}", id);
        Mono<ClimateRiskProfileDTO> climateRiskProfileDTO = climateRiskProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(climateRiskProfileDTO);
    }

    /**
     * {@code DELETE  /climate-risk-profiles/:id} : delete the "id" climateRiskProfile.
     *
     * @param id the id of the climateRiskProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClimateRiskProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClimateRiskProfile : {}", id);
        return climateRiskProfileService
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
