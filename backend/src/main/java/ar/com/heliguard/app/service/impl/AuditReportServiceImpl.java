package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.service.AuditReportService;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.mapper.AuditReportMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditReport}.
 */
@Service
@Transactional
public class AuditReportServiceImpl implements AuditReportService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditReportServiceImpl.class);

    private final AuditReportRepository auditReportRepository;

    private final AuditReportMapper auditReportMapper;

    public AuditReportServiceImpl(AuditReportRepository auditReportRepository, AuditReportMapper auditReportMapper) {
        this.auditReportRepository = auditReportRepository;
        this.auditReportMapper = auditReportMapper;
    }

    @Override
    public Mono<AuditReportDTO> save(AuditReportDTO auditReportDTO) {
        LOG.debug("Request to save AuditReport : {}", auditReportDTO);
        return auditReportRepository.save(auditReportMapper.toEntity(auditReportDTO)).map(auditReportMapper::toDto);
    }

    @Override
    public Mono<AuditReportDTO> update(AuditReportDTO auditReportDTO) {
        LOG.debug("Request to update AuditReport : {}", auditReportDTO);
        return auditReportRepository.save(auditReportMapper.toEntity(auditReportDTO)).map(auditReportMapper::toDto);
    }

    @Override
    public Mono<AuditReportDTO> partialUpdate(AuditReportDTO auditReportDTO) {
        LOG.debug("Request to partially update AuditReport : {}", auditReportDTO);

        return auditReportRepository
            .findById(auditReportDTO.getId())
            .map(existingAuditReport -> {
                auditReportMapper.partialUpdate(existingAuditReport, auditReportDTO);

                return existingAuditReport;
            })
            .flatMap(auditReportRepository::save)
            .map(auditReportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditReportDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AuditReports");
        return auditReportRepository.findAllBy(pageable).map(auditReportMapper::toDto);
    }

    public Flux<AuditReportDTO> findAllWithEagerRelationships(Pageable pageable) {
        return auditReportRepository.findAllWithEagerRelationships(pageable).map(auditReportMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditReportRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditReportDTO> findOne(Long id) {
        LOG.debug("Request to get AuditReport : {}", id);
        return auditReportRepository.findOneWithEagerRelationships(id).map(auditReportMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditReport : {}", id);
        return auditReportRepository.deleteById(id);
    }
}
