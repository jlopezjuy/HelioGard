package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.LossCauseEstimateRepository;
import ar.com.heliguard.app.service.LossCauseEstimateService;
import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
import ar.com.heliguard.app.service.mapper.LossCauseEstimateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.LossCauseEstimate}.
 */
@Service
@Transactional
public class LossCauseEstimateServiceImpl implements LossCauseEstimateService {

    private static final Logger LOG = LoggerFactory.getLogger(LossCauseEstimateServiceImpl.class);

    private final LossCauseEstimateRepository lossCauseEstimateRepository;

    private final LossCauseEstimateMapper lossCauseEstimateMapper;

    public LossCauseEstimateServiceImpl(
        LossCauseEstimateRepository lossCauseEstimateRepository,
        LossCauseEstimateMapper lossCauseEstimateMapper
    ) {
        this.lossCauseEstimateRepository = lossCauseEstimateRepository;
        this.lossCauseEstimateMapper = lossCauseEstimateMapper;
    }

    @Override
    public Mono<LossCauseEstimateDTO> save(LossCauseEstimateDTO lossCauseEstimateDTO) {
        LOG.debug("Request to save LossCauseEstimate : {}", lossCauseEstimateDTO);
        return lossCauseEstimateRepository.save(lossCauseEstimateMapper.toEntity(lossCauseEstimateDTO)).map(lossCauseEstimateMapper::toDto);
    }

    @Override
    public Mono<LossCauseEstimateDTO> update(LossCauseEstimateDTO lossCauseEstimateDTO) {
        LOG.debug("Request to update LossCauseEstimate : {}", lossCauseEstimateDTO);
        return lossCauseEstimateRepository.save(lossCauseEstimateMapper.toEntity(lossCauseEstimateDTO)).map(lossCauseEstimateMapper::toDto);
    }

    @Override
    public Mono<LossCauseEstimateDTO> partialUpdate(LossCauseEstimateDTO lossCauseEstimateDTO) {
        LOG.debug("Request to partially update LossCauseEstimate : {}", lossCauseEstimateDTO);

        return lossCauseEstimateRepository
            .findById(lossCauseEstimateDTO.getId())
            .map(existingLossCauseEstimate -> {
                lossCauseEstimateMapper.partialUpdate(existingLossCauseEstimate, lossCauseEstimateDTO);

                return existingLossCauseEstimate;
            })
            .flatMap(lossCauseEstimateRepository::save)
            .map(lossCauseEstimateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossCauseEstimateDTO> findAll() {
        LOG.debug("Request to get all LossCauseEstimates");
        return lossCauseEstimateRepository.findAll().map(lossCauseEstimateMapper::toDto);
    }

    public Flux<LossCauseEstimateDTO> findAllWithEagerRelationships() {
        return lossCauseEstimateRepository.findAllWithEagerRelationships().map(lossCauseEstimateMapper::toDto);
    }

    public Mono<Long> countAll() {
        return lossCauseEstimateRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LossCauseEstimateDTO> findOne(Long id) {
        LOG.debug("Request to get LossCauseEstimate : {}", id);
        return lossCauseEstimateRepository.findOneWithEagerRelationships(id).map(lossCauseEstimateMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete LossCauseEstimate : {}", id);
        return lossCauseEstimateRepository.deleteById(id);
    }
}
