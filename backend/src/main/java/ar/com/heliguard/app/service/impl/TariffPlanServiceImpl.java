package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.service.TariffPlanService;
import ar.com.heliguard.app.service.dto.TariffPlanDTO;
import ar.com.heliguard.app.service.mapper.TariffPlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TariffPlan}.
 */
@Service
@Transactional
public class TariffPlanServiceImpl implements TariffPlanService {

    private static final Logger LOG = LoggerFactory.getLogger(TariffPlanServiceImpl.class);

    private final TariffPlanRepository tariffPlanRepository;

    private final TariffPlanMapper tariffPlanMapper;

    public TariffPlanServiceImpl(TariffPlanRepository tariffPlanRepository, TariffPlanMapper tariffPlanMapper) {
        this.tariffPlanRepository = tariffPlanRepository;
        this.tariffPlanMapper = tariffPlanMapper;
    }

    @Override
    public Mono<TariffPlanDTO> save(TariffPlanDTO tariffPlanDTO) {
        LOG.debug("Request to save TariffPlan : {}", tariffPlanDTO);
        return tariffPlanRepository.save(tariffPlanMapper.toEntity(tariffPlanDTO)).map(tariffPlanMapper::toDto);
    }

    @Override
    public Mono<TariffPlanDTO> update(TariffPlanDTO tariffPlanDTO) {
        LOG.debug("Request to update TariffPlan : {}", tariffPlanDTO);
        return tariffPlanRepository.save(tariffPlanMapper.toEntity(tariffPlanDTO)).map(tariffPlanMapper::toDto);
    }

    @Override
    public Mono<TariffPlanDTO> partialUpdate(TariffPlanDTO tariffPlanDTO) {
        LOG.debug("Request to partially update TariffPlan : {}", tariffPlanDTO);

        return tariffPlanRepository
            .findById(tariffPlanDTO.getId())
            .map(existingTariffPlan -> {
                tariffPlanMapper.partialUpdate(existingTariffPlan, tariffPlanDTO);

                return existingTariffPlan;
            })
            .flatMap(tariffPlanRepository::save)
            .map(tariffPlanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffPlanDTO> findAll() {
        LOG.debug("Request to get all TariffPlans");
        return tariffPlanRepository.findAll().map(tariffPlanMapper::toDto);
    }

    public Flux<TariffPlanDTO> findAllWithEagerRelationships() {
        return tariffPlanRepository.findAllWithEagerRelationships().map(tariffPlanMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tariffPlanRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TariffPlanDTO> findOne(Long id) {
        LOG.debug("Request to get TariffPlan : {}", id);
        return tariffPlanRepository.findOneWithEagerRelationships(id).map(tariffPlanMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TariffPlan : {}", id);
        return tariffPlanRepository.deleteById(id);
    }
}
