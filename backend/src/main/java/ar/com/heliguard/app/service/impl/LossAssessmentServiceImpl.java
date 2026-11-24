package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.service.LossAssessmentService;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.mapper.LossAssessmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.LossAssessment}.
 */
@Service
@Transactional
public class LossAssessmentServiceImpl implements LossAssessmentService {

    private static final Logger LOG = LoggerFactory.getLogger(LossAssessmentServiceImpl.class);

    private final LossAssessmentRepository lossAssessmentRepository;

    private final LossAssessmentMapper lossAssessmentMapper;

    public LossAssessmentServiceImpl(LossAssessmentRepository lossAssessmentRepository, LossAssessmentMapper lossAssessmentMapper) {
        this.lossAssessmentRepository = lossAssessmentRepository;
        this.lossAssessmentMapper = lossAssessmentMapper;
    }

    @Override
    public Mono<LossAssessmentDTO> save(LossAssessmentDTO lossAssessmentDTO) {
        LOG.debug("Request to save LossAssessment : {}", lossAssessmentDTO);
        return lossAssessmentRepository.save(lossAssessmentMapper.toEntity(lossAssessmentDTO)).map(lossAssessmentMapper::toDto);
    }

    @Override
    public Mono<LossAssessmentDTO> update(LossAssessmentDTO lossAssessmentDTO) {
        LOG.debug("Request to update LossAssessment : {}", lossAssessmentDTO);
        return lossAssessmentRepository.save(lossAssessmentMapper.toEntity(lossAssessmentDTO)).map(lossAssessmentMapper::toDto);
    }

    @Override
    public Mono<LossAssessmentDTO> partialUpdate(LossAssessmentDTO lossAssessmentDTO) {
        LOG.debug("Request to partially update LossAssessment : {}", lossAssessmentDTO);

        return lossAssessmentRepository
            .findById(lossAssessmentDTO.getId())
            .map(existingLossAssessment -> {
                lossAssessmentMapper.partialUpdate(existingLossAssessment, lossAssessmentDTO);

                return existingLossAssessment;
            })
            .flatMap(lossAssessmentRepository::save)
            .map(lossAssessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossAssessmentDTO> findAll() {
        LOG.debug("Request to get all LossAssessments");
        return lossAssessmentRepository.findAll().map(lossAssessmentMapper::toDto);
    }

    public Flux<LossAssessmentDTO> findAllWithEagerRelationships() {
        return lossAssessmentRepository.findAllWithEagerRelationships().map(lossAssessmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return lossAssessmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LossAssessmentDTO> findOne(Long id) {
        LOG.debug("Request to get LossAssessment : {}", id);
        return lossAssessmentRepository.findOneWithEagerRelationships(id).map(lossAssessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete LossAssessment : {}", id);
        return lossAssessmentRepository.deleteById(id);
    }
}
