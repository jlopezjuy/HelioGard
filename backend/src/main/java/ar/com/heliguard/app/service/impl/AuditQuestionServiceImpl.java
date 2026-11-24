package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.service.AuditQuestionService;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import ar.com.heliguard.app.service.mapper.AuditQuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditQuestion}.
 */
@Service
@Transactional
public class AuditQuestionServiceImpl implements AuditQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditQuestionServiceImpl.class);

    private final AuditQuestionRepository auditQuestionRepository;

    private final AuditQuestionMapper auditQuestionMapper;

    public AuditQuestionServiceImpl(AuditQuestionRepository auditQuestionRepository, AuditQuestionMapper auditQuestionMapper) {
        this.auditQuestionRepository = auditQuestionRepository;
        this.auditQuestionMapper = auditQuestionMapper;
    }

    @Override
    public Mono<AuditQuestionDTO> save(AuditQuestionDTO auditQuestionDTO) {
        LOG.debug("Request to save AuditQuestion : {}", auditQuestionDTO);
        return auditQuestionRepository.save(auditQuestionMapper.toEntity(auditQuestionDTO)).map(auditQuestionMapper::toDto);
    }

    @Override
    public Mono<AuditQuestionDTO> update(AuditQuestionDTO auditQuestionDTO) {
        LOG.debug("Request to update AuditQuestion : {}", auditQuestionDTO);
        return auditQuestionRepository.save(auditQuestionMapper.toEntity(auditQuestionDTO)).map(auditQuestionMapper::toDto);
    }

    @Override
    public Mono<AuditQuestionDTO> partialUpdate(AuditQuestionDTO auditQuestionDTO) {
        LOG.debug("Request to partially update AuditQuestion : {}", auditQuestionDTO);

        return auditQuestionRepository
            .findById(auditQuestionDTO.getId())
            .map(existingAuditQuestion -> {
                auditQuestionMapper.partialUpdate(existingAuditQuestion, auditQuestionDTO);

                return existingAuditQuestion;
            })
            .flatMap(auditQuestionRepository::save)
            .map(auditQuestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditQuestionDTO> findAll() {
        LOG.debug("Request to get all AuditQuestions");
        return auditQuestionRepository.findAll().map(auditQuestionMapper::toDto);
    }

    public Flux<AuditQuestionDTO> findAllWithEagerRelationships() {
        return auditQuestionRepository.findAllWithEagerRelationships().map(auditQuestionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditQuestionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get AuditQuestion : {}", id);
        return auditQuestionRepository.findOneWithEagerRelationships(id).map(auditQuestionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditQuestion : {}", id);
        return auditQuestionRepository.deleteById(id);
    }
}
