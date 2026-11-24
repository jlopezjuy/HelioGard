package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.PlanEligibilityAssessmentRepository;
import ar.com.heliguard.app.service.PlanEligibilityAssessmentService;
import ar.com.heliguard.app.service.dto.PlanEligibilityAssessmentDTO;
import ar.com.heliguard.app.service.mapper.PlanEligibilityAssessmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.PlanEligibilityAssessment}.
 */
@Service
@Transactional
public class PlanEligibilityAssessmentServiceImpl implements PlanEligibilityAssessmentService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanEligibilityAssessmentServiceImpl.class);

    private final PlanEligibilityAssessmentRepository planEligibilityAssessmentRepository;

    private final PlanEligibilityAssessmentMapper planEligibilityAssessmentMapper;

    public PlanEligibilityAssessmentServiceImpl(
        PlanEligibilityAssessmentRepository planEligibilityAssessmentRepository,
        PlanEligibilityAssessmentMapper planEligibilityAssessmentMapper
    ) {
        this.planEligibilityAssessmentRepository = planEligibilityAssessmentRepository;
        this.planEligibilityAssessmentMapper = planEligibilityAssessmentMapper;
    }

    @Override
    public Mono<PlanEligibilityAssessmentDTO> save(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO) {
        LOG.debug("Request to save PlanEligibilityAssessment : {}", planEligibilityAssessmentDTO);
        return planEligibilityAssessmentRepository
            .save(planEligibilityAssessmentMapper.toEntity(planEligibilityAssessmentDTO))
            .map(planEligibilityAssessmentMapper::toDto);
    }

    @Override
    public Mono<PlanEligibilityAssessmentDTO> update(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO) {
        LOG.debug("Request to update PlanEligibilityAssessment : {}", planEligibilityAssessmentDTO);
        return planEligibilityAssessmentRepository
            .save(planEligibilityAssessmentMapper.toEntity(planEligibilityAssessmentDTO))
            .map(planEligibilityAssessmentMapper::toDto);
    }

    @Override
    public Mono<PlanEligibilityAssessmentDTO> partialUpdate(PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO) {
        LOG.debug("Request to partially update PlanEligibilityAssessment : {}", planEligibilityAssessmentDTO);

        return planEligibilityAssessmentRepository
            .findById(planEligibilityAssessmentDTO.getId())
            .map(existingPlanEligibilityAssessment -> {
                planEligibilityAssessmentMapper.partialUpdate(existingPlanEligibilityAssessment, planEligibilityAssessmentDTO);

                return existingPlanEligibilityAssessment;
            })
            .flatMap(planEligibilityAssessmentRepository::save)
            .map(planEligibilityAssessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanEligibilityAssessmentDTO> findAll() {
        LOG.debug("Request to get all PlanEligibilityAssessments");
        return planEligibilityAssessmentRepository.findAll().map(planEligibilityAssessmentMapper::toDto);
    }

    public Flux<PlanEligibilityAssessmentDTO> findAllWithEagerRelationships() {
        return planEligibilityAssessmentRepository.findAllWithEagerRelationships().map(planEligibilityAssessmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return planEligibilityAssessmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanEligibilityAssessmentDTO> findOne(Long id) {
        LOG.debug("Request to get PlanEligibilityAssessment : {}", id);
        return planEligibilityAssessmentRepository.findOneWithEagerRelationships(id).map(planEligibilityAssessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete PlanEligibilityAssessment : {}", id);
        return planEligibilityAssessmentRepository.deleteById(id);
    }
}
