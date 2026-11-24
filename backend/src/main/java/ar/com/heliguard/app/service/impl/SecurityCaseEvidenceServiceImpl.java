package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SecurityCaseEvidenceRepository;
import ar.com.heliguard.app.service.SecurityCaseEvidenceService;
import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
import ar.com.heliguard.app.service.mapper.SecurityCaseEvidenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvidence}.
 */
@Service
@Transactional
public class SecurityCaseEvidenceServiceImpl implements SecurityCaseEvidenceService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityCaseEvidenceServiceImpl.class);

    private final SecurityCaseEvidenceRepository securityCaseEvidenceRepository;

    private final SecurityCaseEvidenceMapper securityCaseEvidenceMapper;

    public SecurityCaseEvidenceServiceImpl(
        SecurityCaseEvidenceRepository securityCaseEvidenceRepository,
        SecurityCaseEvidenceMapper securityCaseEvidenceMapper
    ) {
        this.securityCaseEvidenceRepository = securityCaseEvidenceRepository;
        this.securityCaseEvidenceMapper = securityCaseEvidenceMapper;
    }

    @Override
    public Mono<SecurityCaseEvidenceDTO> save(SecurityCaseEvidenceDTO securityCaseEvidenceDTO) {
        LOG.debug("Request to save SecurityCaseEvidence : {}", securityCaseEvidenceDTO);
        return securityCaseEvidenceRepository
            .save(securityCaseEvidenceMapper.toEntity(securityCaseEvidenceDTO))
            .map(securityCaseEvidenceMapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEvidenceDTO> update(SecurityCaseEvidenceDTO securityCaseEvidenceDTO) {
        LOG.debug("Request to update SecurityCaseEvidence : {}", securityCaseEvidenceDTO);
        return securityCaseEvidenceRepository
            .save(securityCaseEvidenceMapper.toEntity(securityCaseEvidenceDTO))
            .map(securityCaseEvidenceMapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEvidenceDTO> partialUpdate(SecurityCaseEvidenceDTO securityCaseEvidenceDTO) {
        LOG.debug("Request to partially update SecurityCaseEvidence : {}", securityCaseEvidenceDTO);

        return securityCaseEvidenceRepository
            .findById(securityCaseEvidenceDTO.getId())
            .map(existingSecurityCaseEvidence -> {
                securityCaseEvidenceMapper.partialUpdate(existingSecurityCaseEvidence, securityCaseEvidenceDTO);

                return existingSecurityCaseEvidence;
            })
            .flatMap(securityCaseEvidenceRepository::save)
            .map(securityCaseEvidenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityCaseEvidenceDTO> findAll() {
        LOG.debug("Request to get all SecurityCaseEvidences");
        return securityCaseEvidenceRepository.findAll().map(securityCaseEvidenceMapper::toDto);
    }

    public Flux<SecurityCaseEvidenceDTO> findAllWithEagerRelationships() {
        return securityCaseEvidenceRepository.findAllWithEagerRelationships().map(securityCaseEvidenceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return securityCaseEvidenceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityCaseEvidenceDTO> findOne(Long id) {
        LOG.debug("Request to get SecurityCaseEvidence : {}", id);
        return securityCaseEvidenceRepository.findOneWithEagerRelationships(id).map(securityCaseEvidenceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SecurityCaseEvidence : {}", id);
        return securityCaseEvidenceRepository.deleteById(id);
    }
}
