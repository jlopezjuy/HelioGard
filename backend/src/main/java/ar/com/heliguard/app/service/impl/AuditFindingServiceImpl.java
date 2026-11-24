package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.service.AuditFindingService;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import ar.com.heliguard.app.service.mapper.AuditFindingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditFinding}.
 */
@Service
@Transactional
public class AuditFindingServiceImpl implements AuditFindingService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditFindingServiceImpl.class);

    private final AuditFindingRepository auditFindingRepository;

    private final AuditFindingMapper auditFindingMapper;

    public AuditFindingServiceImpl(AuditFindingRepository auditFindingRepository, AuditFindingMapper auditFindingMapper) {
        this.auditFindingRepository = auditFindingRepository;
        this.auditFindingMapper = auditFindingMapper;
    }

    @Override
    public Mono<AuditFindingDTO> save(AuditFindingDTO auditFindingDTO) {
        LOG.debug("Request to save AuditFinding : {}", auditFindingDTO);
        return auditFindingRepository.save(auditFindingMapper.toEntity(auditFindingDTO)).map(auditFindingMapper::toDto);
    }

    @Override
    public Mono<AuditFindingDTO> update(AuditFindingDTO auditFindingDTO) {
        LOG.debug("Request to update AuditFinding : {}", auditFindingDTO);
        return auditFindingRepository.save(auditFindingMapper.toEntity(auditFindingDTO)).map(auditFindingMapper::toDto);
    }

    @Override
    public Mono<AuditFindingDTO> partialUpdate(AuditFindingDTO auditFindingDTO) {
        LOG.debug("Request to partially update AuditFinding : {}", auditFindingDTO);

        return auditFindingRepository
            .findById(auditFindingDTO.getId())
            .map(existingAuditFinding -> {
                auditFindingMapper.partialUpdate(existingAuditFinding, auditFindingDTO);

                return existingAuditFinding;
            })
            .flatMap(auditFindingRepository::save)
            .map(auditFindingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditFindingDTO> findAll() {
        LOG.debug("Request to get all AuditFindings");
        return auditFindingRepository.findAll().map(auditFindingMapper::toDto);
    }

    public Flux<AuditFindingDTO> findAllWithEagerRelationships() {
        return auditFindingRepository.findAllWithEagerRelationships().map(auditFindingMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditFindingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditFindingDTO> findOne(Long id) {
        LOG.debug("Request to get AuditFinding : {}", id);
        return auditFindingRepository.findOneWithEagerRelationships(id).map(auditFindingMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditFinding : {}", id);
        return auditFindingRepository.deleteById(id);
    }
}
