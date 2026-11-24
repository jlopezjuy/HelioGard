package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.RecoveryAssessmentRepository;
import ar.com.heliguard.app.service.RecoveryAssessmentService;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import ar.com.heliguard.app.service.mapper.RecoveryAssessmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.RecoveryAssessment}.
 */
@Service
@Transactional
public class RecoveryAssessmentServiceImpl implements RecoveryAssessmentService {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryAssessmentServiceImpl.class);

    private final RecoveryAssessmentRepository recoveryAssessmentRepository;

    private final RecoveryAssessmentMapper recoveryAssessmentMapper;

    public RecoveryAssessmentServiceImpl(
        RecoveryAssessmentRepository recoveryAssessmentRepository,
        RecoveryAssessmentMapper recoveryAssessmentMapper
    ) {
        this.recoveryAssessmentRepository = recoveryAssessmentRepository;
        this.recoveryAssessmentMapper = recoveryAssessmentMapper;
    }

    @Override
    public Mono<RecoveryAssessmentDTO> save(RecoveryAssessmentDTO recoveryAssessmentDTO) {
        LOG.debug("Request to save RecoveryAssessment : {}", recoveryAssessmentDTO);
        return recoveryAssessmentRepository
            .save(recoveryAssessmentMapper.toEntity(recoveryAssessmentDTO))
            .map(recoveryAssessmentMapper::toDto);
    }

    @Override
    public Mono<RecoveryAssessmentDTO> update(RecoveryAssessmentDTO recoveryAssessmentDTO) {
        LOG.debug("Request to update RecoveryAssessment : {}", recoveryAssessmentDTO);
        return recoveryAssessmentRepository
            .save(recoveryAssessmentMapper.toEntity(recoveryAssessmentDTO))
            .map(recoveryAssessmentMapper::toDto);
    }

    @Override
    public Mono<RecoveryAssessmentDTO> partialUpdate(RecoveryAssessmentDTO recoveryAssessmentDTO) {
        LOG.debug("Request to partially update RecoveryAssessment : {}", recoveryAssessmentDTO);

        return recoveryAssessmentRepository
            .findById(recoveryAssessmentDTO.getId())
            .map(existingRecoveryAssessment -> {
                recoveryAssessmentMapper.partialUpdate(existingRecoveryAssessment, recoveryAssessmentDTO);

                return existingRecoveryAssessment;
            })
            .flatMap(recoveryAssessmentRepository::save)
            .map(recoveryAssessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RecoveryAssessmentDTO> findAll() {
        LOG.debug("Request to get all RecoveryAssessments");
        return recoveryAssessmentRepository.findAll().map(recoveryAssessmentMapper::toDto);
    }

    public Flux<RecoveryAssessmentDTO> findAllWithEagerRelationships() {
        return recoveryAssessmentRepository.findAllWithEagerRelationships().map(recoveryAssessmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return recoveryAssessmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RecoveryAssessmentDTO> findOne(Long id) {
        LOG.debug("Request to get RecoveryAssessment : {}", id);
        return recoveryAssessmentRepository.findOneWithEagerRelationships(id).map(recoveryAssessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete RecoveryAssessment : {}", id);
        return recoveryAssessmentRepository.deleteById(id);
    }
}
