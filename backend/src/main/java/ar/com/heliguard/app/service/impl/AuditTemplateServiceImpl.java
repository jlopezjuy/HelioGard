package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditTemplateService;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.mapper.AuditTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AuditTemplate}.
 */
@Service
@Transactional
public class AuditTemplateServiceImpl implements AuditTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTemplateServiceImpl.class);

    private final AuditTemplateRepository auditTemplateRepository;

    private final AuditTemplateMapper auditTemplateMapper;

    public AuditTemplateServiceImpl(AuditTemplateRepository auditTemplateRepository, AuditTemplateMapper auditTemplateMapper) {
        this.auditTemplateRepository = auditTemplateRepository;
        this.auditTemplateMapper = auditTemplateMapper;
    }

    @Override
    public Mono<AuditTemplateDTO> save(AuditTemplateDTO auditTemplateDTO) {
        LOG.debug("Request to save AuditTemplate : {}", auditTemplateDTO);
        return auditTemplateRepository.save(auditTemplateMapper.toEntity(auditTemplateDTO)).map(auditTemplateMapper::toDto);
    }

    @Override
    public Mono<AuditTemplateDTO> update(AuditTemplateDTO auditTemplateDTO) {
        LOG.debug("Request to update AuditTemplate : {}", auditTemplateDTO);
        return auditTemplateRepository.save(auditTemplateMapper.toEntity(auditTemplateDTO)).map(auditTemplateMapper::toDto);
    }

    @Override
    public Mono<AuditTemplateDTO> partialUpdate(AuditTemplateDTO auditTemplateDTO) {
        LOG.debug("Request to partially update AuditTemplate : {}", auditTemplateDTO);

        return auditTemplateRepository
            .findById(auditTemplateDTO.getId())
            .map(existingAuditTemplate -> {
                auditTemplateMapper.partialUpdate(existingAuditTemplate, auditTemplateDTO);

                return existingAuditTemplate;
            })
            .flatMap(auditTemplateRepository::save)
            .map(auditTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditTemplateDTO> findAll() {
        LOG.debug("Request to get all AuditTemplates");
        return auditTemplateRepository.findAll().map(auditTemplateMapper::toDto);
    }

    public Flux<AuditTemplateDTO> findAllWithEagerRelationships() {
        return auditTemplateRepository.findAllWithEagerRelationships().map(auditTemplateMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditTemplateRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get AuditTemplate : {}", id);
        return auditTemplateRepository.findOneWithEagerRelationships(id).map(auditTemplateMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditTemplate : {}", id);
        return auditTemplateRepository.deleteById(id);
    }
}
