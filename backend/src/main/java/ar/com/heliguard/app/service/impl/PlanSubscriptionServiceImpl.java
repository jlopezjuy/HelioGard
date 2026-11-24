package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.PlanSubscriptionRepository;
import ar.com.heliguard.app.service.PlanSubscriptionService;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.mapper.PlanSubscriptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.PlanSubscription}.
 */
@Service
@Transactional
public class PlanSubscriptionServiceImpl implements PlanSubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanSubscriptionServiceImpl.class);

    private final PlanSubscriptionRepository planSubscriptionRepository;

    private final PlanSubscriptionMapper planSubscriptionMapper;

    public PlanSubscriptionServiceImpl(
        PlanSubscriptionRepository planSubscriptionRepository,
        PlanSubscriptionMapper planSubscriptionMapper
    ) {
        this.planSubscriptionRepository = planSubscriptionRepository;
        this.planSubscriptionMapper = planSubscriptionMapper;
    }

    @Override
    public Mono<PlanSubscriptionDTO> save(PlanSubscriptionDTO planSubscriptionDTO) {
        LOG.debug("Request to save PlanSubscription : {}", planSubscriptionDTO);
        return planSubscriptionRepository.save(planSubscriptionMapper.toEntity(planSubscriptionDTO)).map(planSubscriptionMapper::toDto);
    }

    @Override
    public Mono<PlanSubscriptionDTO> update(PlanSubscriptionDTO planSubscriptionDTO) {
        LOG.debug("Request to update PlanSubscription : {}", planSubscriptionDTO);
        return planSubscriptionRepository.save(planSubscriptionMapper.toEntity(planSubscriptionDTO)).map(planSubscriptionMapper::toDto);
    }

    @Override
    public Mono<PlanSubscriptionDTO> partialUpdate(PlanSubscriptionDTO planSubscriptionDTO) {
        LOG.debug("Request to partially update PlanSubscription : {}", planSubscriptionDTO);

        return planSubscriptionRepository
            .findById(planSubscriptionDTO.getId())
            .map(existingPlanSubscription -> {
                planSubscriptionMapper.partialUpdate(existingPlanSubscription, planSubscriptionDTO);

                return existingPlanSubscription;
            })
            .flatMap(planSubscriptionRepository::save)
            .map(planSubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanSubscriptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PlanSubscriptions");
        return planSubscriptionRepository.findAllBy(pageable).map(planSubscriptionMapper::toDto);
    }

    public Flux<PlanSubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return planSubscriptionRepository.findAllWithEagerRelationships(pageable).map(planSubscriptionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return planSubscriptionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanSubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get PlanSubscription : {}", id);
        return planSubscriptionRepository.findOneWithEagerRelationships(id).map(planSubscriptionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete PlanSubscription : {}", id);
        return planSubscriptionRepository.deleteById(id);
    }
}
