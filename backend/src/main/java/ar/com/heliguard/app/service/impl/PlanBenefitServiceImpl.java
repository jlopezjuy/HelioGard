package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.PlanBenefitRepository;
import ar.com.heliguard.app.service.PlanBenefitService;
import ar.com.heliguard.app.service.dto.PlanBenefitDTO;
import ar.com.heliguard.app.service.mapper.PlanBenefitMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.PlanBenefit}.
 */
@Service
@Transactional
public class PlanBenefitServiceImpl implements PlanBenefitService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanBenefitServiceImpl.class);

    private final PlanBenefitRepository planBenefitRepository;

    private final PlanBenefitMapper planBenefitMapper;

    public PlanBenefitServiceImpl(PlanBenefitRepository planBenefitRepository, PlanBenefitMapper planBenefitMapper) {
        this.planBenefitRepository = planBenefitRepository;
        this.planBenefitMapper = planBenefitMapper;
    }

    @Override
    public Mono<PlanBenefitDTO> save(PlanBenefitDTO planBenefitDTO) {
        LOG.debug("Request to save PlanBenefit : {}", planBenefitDTO);
        return planBenefitRepository.save(planBenefitMapper.toEntity(planBenefitDTO)).map(planBenefitMapper::toDto);
    }

    @Override
    public Mono<PlanBenefitDTO> update(PlanBenefitDTO planBenefitDTO) {
        LOG.debug("Request to update PlanBenefit : {}", planBenefitDTO);
        return planBenefitRepository.save(planBenefitMapper.toEntity(planBenefitDTO)).map(planBenefitMapper::toDto);
    }

    @Override
    public Mono<PlanBenefitDTO> partialUpdate(PlanBenefitDTO planBenefitDTO) {
        LOG.debug("Request to partially update PlanBenefit : {}", planBenefitDTO);

        return planBenefitRepository
            .findById(planBenefitDTO.getId())
            .map(existingPlanBenefit -> {
                planBenefitMapper.partialUpdate(existingPlanBenefit, planBenefitDTO);

                return existingPlanBenefit;
            })
            .flatMap(planBenefitRepository::save)
            .map(planBenefitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanBenefitDTO> findAll() {
        LOG.debug("Request to get all PlanBenefits");
        return planBenefitRepository.findAll().map(planBenefitMapper::toDto);
    }

    public Flux<PlanBenefitDTO> findAllWithEagerRelationships() {
        return planBenefitRepository.findAllWithEagerRelationships().map(planBenefitMapper::toDto);
    }

    public Mono<Long> countAll() {
        return planBenefitRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanBenefitDTO> findOne(Long id) {
        LOG.debug("Request to get PlanBenefit : {}", id);
        return planBenefitRepository.findOneWithEagerRelationships(id).map(planBenefitMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete PlanBenefit : {}", id);
        return planBenefitRepository.deleteById(id);
    }
}
