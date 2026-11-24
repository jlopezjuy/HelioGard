package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.IntegrityAssessmentRepository;
import ar.com.heliguard.app.service.IntegrityAssessmentService;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.mapper.IntegrityAssessmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.IntegrityAssessment}.
 */
@Service
@Transactional
public class IntegrityAssessmentServiceImpl implements IntegrityAssessmentService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrityAssessmentServiceImpl.class);

    private final IntegrityAssessmentRepository integrityAssessmentRepository;

    private final IntegrityAssessmentMapper integrityAssessmentMapper;

    public IntegrityAssessmentServiceImpl(
        IntegrityAssessmentRepository integrityAssessmentRepository,
        IntegrityAssessmentMapper integrityAssessmentMapper
    ) {
        this.integrityAssessmentRepository = integrityAssessmentRepository;
        this.integrityAssessmentMapper = integrityAssessmentMapper;
    }

    @Override
    public Mono<IntegrityAssessmentDTO> save(IntegrityAssessmentDTO integrityAssessmentDTO) {
        LOG.debug("Request to save IntegrityAssessment : {}", integrityAssessmentDTO);
        return integrityAssessmentRepository
            .save(integrityAssessmentMapper.toEntity(integrityAssessmentDTO))
            .map(integrityAssessmentMapper::toDto);
    }

    @Override
    public Mono<IntegrityAssessmentDTO> update(IntegrityAssessmentDTO integrityAssessmentDTO) {
        LOG.debug("Request to update IntegrityAssessment : {}", integrityAssessmentDTO);
        return integrityAssessmentRepository
            .save(integrityAssessmentMapper.toEntity(integrityAssessmentDTO))
            .map(integrityAssessmentMapper::toDto);
    }

    @Override
    public Mono<IntegrityAssessmentDTO> partialUpdate(IntegrityAssessmentDTO integrityAssessmentDTO) {
        LOG.debug("Request to partially update IntegrityAssessment : {}", integrityAssessmentDTO);

        return integrityAssessmentRepository
            .findById(integrityAssessmentDTO.getId())
            .map(existingIntegrityAssessment -> {
                integrityAssessmentMapper.partialUpdate(existingIntegrityAssessment, integrityAssessmentDTO);

                return existingIntegrityAssessment;
            })
            .flatMap(integrityAssessmentRepository::save)
            .map(integrityAssessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IntegrityAssessmentDTO> findAll() {
        LOG.debug("Request to get all IntegrityAssessments");
        return integrityAssessmentRepository.findAll().map(integrityAssessmentMapper::toDto);
    }

    public Flux<IntegrityAssessmentDTO> findAllWithEagerRelationships() {
        return integrityAssessmentRepository.findAllWithEagerRelationships().map(integrityAssessmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return integrityAssessmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IntegrityAssessmentDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrityAssessment : {}", id);
        return integrityAssessmentRepository.findOneWithEagerRelationships(id).map(integrityAssessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete IntegrityAssessment : {}", id);
        return integrityAssessmentRepository.deleteById(id);
    }
}
