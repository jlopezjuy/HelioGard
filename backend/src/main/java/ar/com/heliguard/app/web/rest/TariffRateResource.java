package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.TariffRateService;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.TariffRate}.
 */
@RestController
@RequestMapping("/api/tariff-rates")
public class TariffRateResource {

    private static final Logger LOG = LoggerFactory.getLogger(TariffRateResource.class);

    private static final String ENTITY_NAME = "tariffRate";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final TariffRateService tariffRateService;

    private final TariffRateRepository tariffRateRepository;

    public TariffRateResource(TariffRateService tariffRateService, TariffRateRepository tariffRateRepository) {
        this.tariffRateService = tariffRateService;
        this.tariffRateRepository = tariffRateRepository;
    }

    /**
     * {@code POST  /tariff-rates} : Create a new tariffRate.
     *
     * @param tariffRateDTO the tariffRateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tariffRateDTO, or with status {@code 400 (Bad Request)} if the tariffRate already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TariffRateDTO>> createTariffRate(@Valid @RequestBody TariffRateDTO tariffRateDTO) throws URISyntaxException {
        LOG.debug("REST request to save TariffRate : {}", tariffRateDTO);
        if (tariffRateDTO.getId() != null) {
            throw new BadRequestAlertException("A new tariffRate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tariffRateService.save(tariffRateDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/tariff-rates/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /tariff-rates/:id} : Updates an existing tariffRate.
     *
     * @param id the id of the tariffRateDTO to save.
     * @param tariffRateDTO the tariffRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tariffRateDTO,
     * or with status {@code 400 (Bad Request)} if the tariffRateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tariffRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TariffRateDTO>> updateTariffRate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TariffRateDTO tariffRateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TariffRate : {}, {}", id, tariffRateDTO);
        if (tariffRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tariffRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tariffRateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return tariffRateService
                .update(tariffRateDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /tariff-rates/:id} : Partial updates given fields of an existing tariffRate, field will ignore if it is null
     *
     * @param id the id of the tariffRateDTO to save.
     * @param tariffRateDTO the tariffRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tariffRateDTO,
     * or with status {@code 400 (Bad Request)} if the tariffRateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tariffRateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tariffRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TariffRateDTO>> partialUpdateTariffRate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TariffRateDTO tariffRateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TariffRate : {}, {}", id, tariffRateDTO);
        if (tariffRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tariffRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tariffRateRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<TariffRateDTO> result = tariffRateService.partialUpdate(tariffRateDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /tariff-rates} : get all the Tariff Rates.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tariff Rates in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TariffRateDTO>> getAllTariffRates(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TariffRates");
        if (eagerload) {
            return tariffRateService.findAllWithEagerRelationships().collectList();
        } else {
            return tariffRateService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /tariff-rates} : get all the Tariff Rates as a stream.
     * @return the {@link Flux} of Tariff Rates.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TariffRateDTO> getAllTariffRatesAsStream() {
        LOG.debug("REST request to get all TariffRates as a stream");
        return tariffRateService.findAll();
    }

    /**
     * {@code GET  /tariff-rates/:id} : get the "id" tariffRate.
     *
     * @param id the id of the tariffRateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tariffRateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TariffRateDTO>> getTariffRate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TariffRate : {}", id);
        Mono<TariffRateDTO> tariffRateDTO = tariffRateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tariffRateDTO);
    }

    /**
     * {@code DELETE  /tariff-rates/:id} : delete the "id" tariffRate.
     *
     * @param id the id of the tariffRateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTariffRate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TariffRate : {}", id);
        return tariffRateService
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
