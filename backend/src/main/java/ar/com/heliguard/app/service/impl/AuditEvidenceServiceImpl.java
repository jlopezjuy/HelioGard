package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.service.AuditEvidenceService;
import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
import ar.com.heliguard.app.service.mapper.AuditEvidenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditEvidence}.
 */
@Service
@Transactional
public class AuditEvidenceServiceImpl implements AuditEvidenceService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditEvidenceServiceImpl.class);

    private final AuditEvidenceRepository auditEvidenceRepository;

    private final AuditEvidenceMapper auditEvidenceMapper;

    public AuditEvidenceServiceImpl(AuditEvidenceRepository auditEvidenceRepository, AuditEvidenceMapper auditEvidenceMapper) {
        this.auditEvidenceRepository = auditEvidenceRepository;
        this.auditEvidenceMapper = auditEvidenceMapper;
    }

    @Override
    public Mono<AuditEvidenceDTO> save(AuditEvidenceDTO auditEvidenceDTO) {
        LOG.debug("Request to save AuditEvidence : {}", auditEvidenceDTO);
        return auditEvidenceRepository.save(auditEvidenceMapper.toEntity(auditEvidenceDTO)).map(auditEvidenceMapper::toDto);
    }

    @Override
    public Mono<AuditEvidenceDTO> update(AuditEvidenceDTO auditEvidenceDTO) {
        LOG.debug("Request to update AuditEvidence : {}", auditEvidenceDTO);
        return auditEvidenceRepository.save(auditEvidenceMapper.toEntity(auditEvidenceDTO)).map(auditEvidenceMapper::toDto);
    }

    @Override
    public Mono<AuditEvidenceDTO> partialUpdate(AuditEvidenceDTO auditEvidenceDTO) {
        LOG.debug("Request to partially update AuditEvidence : {}", auditEvidenceDTO);

        return auditEvidenceRepository
            .findById(auditEvidenceDTO.getId())
            .map(existingAuditEvidence -> {
                auditEvidenceMapper.partialUpdate(existingAuditEvidence, auditEvidenceDTO);

                return existingAuditEvidence;
            })
            .flatMap(auditEvidenceRepository::save)
            .map(auditEvidenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditEvidenceDTO> findAll() {
        LOG.debug("Request to get all AuditEvidences");
        return auditEvidenceRepository.findAll().map(auditEvidenceMapper::toDto);
    }

    public Flux<AuditEvidenceDTO> findAllWithEagerRelationships() {
        return auditEvidenceRepository.findAllWithEagerRelationships().map(auditEvidenceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditEvidenceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditEvidenceDTO> findOne(Long id) {
        LOG.debug("Request to get AuditEvidence : {}", id);
        return auditEvidenceRepository.findOneWithEagerRelationships(id).map(auditEvidenceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditEvidence : {}", id);
        return auditEvidenceRepository.deleteById(id);
    }
}
