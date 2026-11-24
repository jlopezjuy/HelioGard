package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.repository.StringInputAssignmentRepository;
import ar.com.heliguard.app.service.StringInputAssignmentService;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
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
 * REST controller for managing {@link ar.com.heliguard.app.domain.StringInputAssignment}.
 */
@RestController
@RequestMapping("/api/string-input-assignments")
public class StringInputAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(StringInputAssignmentResource.class);

    private static final String ENTITY_NAME = "stringInputAssignment";

    @Value("${jhipster.clientApp.name:helioguard}")
    private String applicationName;

    private final StringInputAssignmentService stringInputAssignmentService;

    private final StringInputAssignmentRepository stringInputAssignmentRepository;

    public StringInputAssignmentResource(
        StringInputAssignmentService stringInputAssignmentService,
        StringInputAssignmentRepository stringInputAssignmentRepository
    ) {
        this.stringInputAssignmentService = stringInputAssignmentService;
        this.stringInputAssignmentRepository = stringInputAssignmentRepository;
    }

    /**
     * {@code POST  /string-input-assignments} : Create a new stringInputAssignment.
     *
     * @param stringInputAssignmentDTO the stringInputAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stringInputAssignmentDTO, or with status {@code 400 (Bad Request)} if the stringInputAssignment already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<StringInputAssignmentDTO>> createStringInputAssignment(
        @Valid @RequestBody StringInputAssignmentDTO stringInputAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save StringInputAssignment : {}", stringInputAssignmentDTO);
        if (stringInputAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new stringInputAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stringInputAssignmentService.save(stringInputAssignmentDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/string-input-assignments/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /string-input-assignments/:id} : Updates an existing stringInputAssignment.
     *
     * @param id the id of the stringInputAssignmentDTO to save.
     * @param stringInputAssignmentDTO the stringInputAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stringInputAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the stringInputAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stringInputAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<StringInputAssignmentDTO>> updateStringInputAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StringInputAssignmentDTO stringInputAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StringInputAssignment : {}, {}", id, stringInputAssignmentDTO);
        if (stringInputAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stringInputAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stringInputAssignmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return stringInputAssignmentService
                .update(stringInputAssignmentDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /string-input-assignments/:id} : Partial updates given fields of an existing stringInputAssignment, field will ignore if it is null
     *
     * @param id the id of the stringInputAssignmentDTO to save.
     * @param stringInputAssignmentDTO the stringInputAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stringInputAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the stringInputAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stringInputAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stringInputAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StringInputAssignmentDTO>> partialUpdateStringInputAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StringInputAssignmentDTO stringInputAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StringInputAssignment : {}, {}", id, stringInputAssignmentDTO);
        if (stringInputAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stringInputAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stringInputAssignmentRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<StringInputAssignmentDTO> result = stringInputAssignmentService.partialUpdate(stringInputAssignmentDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /string-input-assignments} : get all the String Input Assignments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of String Input Assignments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<StringInputAssignmentDTO>> getAllStringInputAssignments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StringInputAssignments");
        if (eagerload) {
            return stringInputAssignmentService.findAllWithEagerRelationships().collectList();
        } else {
            return stringInputAssignmentService.findAll().collectList();
        }
    }

    /**
     * {@code GET  /string-input-assignments} : get all the String Input Assignments as a stream.
     * @return the {@link Flux} of String Input Assignments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StringInputAssignmentDTO> getAllStringInputAssignmentsAsStream() {
        LOG.debug("REST request to get all StringInputAssignments as a stream");
        return stringInputAssignmentService.findAll();
    }

    /**
     * {@code GET  /string-input-assignments/:id} : get the "id" stringInputAssignment.
     *
     * @param id the id of the stringInputAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stringInputAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<StringInputAssignmentDTO>> getStringInputAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StringInputAssignment : {}", id);
        Mono<StringInputAssignmentDTO> stringInputAssignmentDTO = stringInputAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stringInputAssignmentDTO);
    }

    /**
     * {@code DELETE  /string-input-assignments/:id} : delete the "id" stringInputAssignment.
     *
     * @param id the id of the stringInputAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStringInputAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StringInputAssignment : {}", id);
        return stringInputAssignmentService
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
