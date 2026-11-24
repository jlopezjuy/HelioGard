package ar.com.heliguard.app.service;

import ar.com.heliguard.app.service.dto.AuditReportDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ar.com.heliguard.app.domain.AuditReport}.
 */
public interface AuditReportService {
    /**
     * Save a auditReport.
     *
     * @param auditReportDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuditReportDTO> save(AuditReportDTO auditReportDTO);

    /**
     * Updates a auditReport.
     *
     * @param auditReportDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuditReportDTO> update(AuditReportDTO auditReportDTO);

    /**
     * Partially updates a auditReport.
     *
     * @param auditReportDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuditReportDTO> partialUpdate(AuditReportDTO auditReportDTO);

    /**
     * Get all the auditReports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AuditReportDTO> findAll(Pageable pageable);

    /**
     * Get all the auditReports with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AuditReportDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of auditReports available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" auditReport.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuditReportDTO> findOne(Long id);

    /**
     * Delete the "id" auditReport.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
