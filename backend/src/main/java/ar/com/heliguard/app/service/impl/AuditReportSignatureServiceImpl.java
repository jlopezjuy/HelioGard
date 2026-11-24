package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditReportSignatureRepository;
import ar.com.heliguard.app.service.AuditReportSignatureService;
import ar.com.heliguard.app.service.dto.AuditReportSignatureDTO;
import ar.com.heliguard.app.service.mapper.AuditReportSignatureMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditReportSignature}.
 */
@Service
@Transactional
public class AuditReportSignatureServiceImpl implements AuditReportSignatureService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditReportSignatureServiceImpl.class);

    private final AuditReportSignatureRepository auditReportSignatureRepository;

    private final AuditReportSignatureMapper auditReportSignatureMapper;

    public AuditReportSignatureServiceImpl(
        AuditReportSignatureRepository auditReportSignatureRepository,
        AuditReportSignatureMapper auditReportSignatureMapper
    ) {
        this.auditReportSignatureRepository = auditReportSignatureRepository;
        this.auditReportSignatureMapper = auditReportSignatureMapper;
    }

    @Override
    public Mono<AuditReportSignatureDTO> save(AuditReportSignatureDTO auditReportSignatureDTO) {
        LOG.debug("Request to save AuditReportSignature : {}", auditReportSignatureDTO);
        return auditReportSignatureRepository
            .save(auditReportSignatureMapper.toEntity(auditReportSignatureDTO))
            .map(auditReportSignatureMapper::toDto);
    }

    @Override
    public Mono<AuditReportSignatureDTO> update(AuditReportSignatureDTO auditReportSignatureDTO) {
        LOG.debug("Request to update AuditReportSignature : {}", auditReportSignatureDTO);
        return auditReportSignatureRepository
            .save(auditReportSignatureMapper.toEntity(auditReportSignatureDTO))
            .map(auditReportSignatureMapper::toDto);
    }

    @Override
    public Mono<AuditReportSignatureDTO> partialUpdate(AuditReportSignatureDTO auditReportSignatureDTO) {
        LOG.debug("Request to partially update AuditReportSignature : {}", auditReportSignatureDTO);

        return auditReportSignatureRepository
            .findById(auditReportSignatureDTO.getId())
            .map(existingAuditReportSignature -> {
                auditReportSignatureMapper.partialUpdate(existingAuditReportSignature, auditReportSignatureDTO);

                return existingAuditReportSignature;
            })
            .flatMap(auditReportSignatureRepository::save)
            .map(auditReportSignatureMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditReportSignatureDTO> findAll() {
        LOG.debug("Request to get all AuditReportSignatures");
        return auditReportSignatureRepository.findAll().map(auditReportSignatureMapper::toDto);
    }

    public Flux<AuditReportSignatureDTO> findAllWithEagerRelationships() {
        return auditReportSignatureRepository.findAllWithEagerRelationships().map(auditReportSignatureMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditReportSignatureRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditReportSignatureDTO> findOne(Long id) {
        LOG.debug("Request to get AuditReportSignature : {}", id);
        return auditReportSignatureRepository.findOneWithEagerRelationships(id).map(auditReportSignatureMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditReportSignature : {}", id);
        return auditReportSignatureRepository.deleteById(id);
    }
}
