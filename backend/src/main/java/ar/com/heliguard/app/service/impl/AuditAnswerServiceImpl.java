package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditAnswerRepository;
import ar.com.heliguard.app.service.AuditAnswerService;
import ar.com.heliguard.app.service.dto.AuditAnswerDTO;
import ar.com.heliguard.app.service.mapper.AuditAnswerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditAnswer}.
 */
@Service
@Transactional
public class AuditAnswerServiceImpl implements AuditAnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditAnswerServiceImpl.class);

    private final AuditAnswerRepository auditAnswerRepository;

    private final AuditAnswerMapper auditAnswerMapper;

    public AuditAnswerServiceImpl(AuditAnswerRepository auditAnswerRepository, AuditAnswerMapper auditAnswerMapper) {
        this.auditAnswerRepository = auditAnswerRepository;
        this.auditAnswerMapper = auditAnswerMapper;
    }

    @Override
    public Mono<AuditAnswerDTO> save(AuditAnswerDTO auditAnswerDTO) {
        LOG.debug("Request to save AuditAnswer : {}", auditAnswerDTO);
        return auditAnswerRepository.save(auditAnswerMapper.toEntity(auditAnswerDTO)).map(auditAnswerMapper::toDto);
    }

    @Override
    public Mono<AuditAnswerDTO> update(AuditAnswerDTO auditAnswerDTO) {
        LOG.debug("Request to update AuditAnswer : {}", auditAnswerDTO);
        return auditAnswerRepository.save(auditAnswerMapper.toEntity(auditAnswerDTO)).map(auditAnswerMapper::toDto);
    }

    @Override
    public Mono<AuditAnswerDTO> partialUpdate(AuditAnswerDTO auditAnswerDTO) {
        LOG.debug("Request to partially update AuditAnswer : {}", auditAnswerDTO);

        return auditAnswerRepository
            .findById(auditAnswerDTO.getId())
            .map(existingAuditAnswer -> {
                auditAnswerMapper.partialUpdate(existingAuditAnswer, auditAnswerDTO);

                return existingAuditAnswer;
            })
            .flatMap(auditAnswerRepository::save)
            .map(auditAnswerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditAnswerDTO> findAll() {
        LOG.debug("Request to get all AuditAnswers");
        return auditAnswerRepository.findAll().map(auditAnswerMapper::toDto);
    }

    public Flux<AuditAnswerDTO> findAllWithEagerRelationships() {
        return auditAnswerRepository.findAllWithEagerRelationships().map(auditAnswerMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditAnswerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditAnswerDTO> findOne(Long id) {
        LOG.debug("Request to get AuditAnswer : {}", id);
        return auditAnswerRepository.findOneWithEagerRelationships(id).map(auditAnswerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditAnswer : {}", id);
        return auditAnswerRepository.deleteById(id);
    }
}
