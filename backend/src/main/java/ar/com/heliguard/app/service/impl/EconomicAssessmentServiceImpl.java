package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EconomicAssessmentRepository;
import ar.com.heliguard.app.service.EconomicAssessmentService;
import ar.com.heliguard.app.service.dto.EconomicAssessmentDTO;
import ar.com.heliguard.app.service.mapper.EconomicAssessmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EconomicAssessment}.
 */
@Service
@Transactional
public class EconomicAssessmentServiceImpl implements EconomicAssessmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EconomicAssessmentServiceImpl.class);

    private final EconomicAssessmentRepository economicAssessmentRepository;

    private final EconomicAssessmentMapper economicAssessmentMapper;

    public EconomicAssessmentServiceImpl(
        EconomicAssessmentRepository economicAssessmentRepository,
        EconomicAssessmentMapper economicAssessmentMapper
    ) {
        this.economicAssessmentRepository = economicAssessmentRepository;
        this.economicAssessmentMapper = economicAssessmentMapper;
    }

    @Override
    public Mono<EconomicAssessmentDTO> save(EconomicAssessmentDTO economicAssessmentDTO) {
        LOG.debug("Request to save EconomicAssessment : {}", economicAssessmentDTO);
        return economicAssessmentRepository
            .save(economicAssessmentMapper.toEntity(economicAssessmentDTO))
            .map(economicAssessmentMapper::toDto);
    }

    @Override
    public Mono<EconomicAssessmentDTO> update(EconomicAssessmentDTO economicAssessmentDTO) {
        LOG.debug("Request to update EconomicAssessment : {}", economicAssessmentDTO);
        return economicAssessmentRepository
            .save(economicAssessmentMapper.toEntity(economicAssessmentDTO))
            .map(economicAssessmentMapper::toDto);
    }

    @Override
    public Mono<EconomicAssessmentDTO> partialUpdate(EconomicAssessmentDTO economicAssessmentDTO) {
        LOG.debug("Request to partially update EconomicAssessment : {}", economicAssessmentDTO);

        return economicAssessmentRepository
            .findById(economicAssessmentDTO.getId())
            .map(existingEconomicAssessment -> {
                economicAssessmentMapper.partialUpdate(existingEconomicAssessment, economicAssessmentDTO);

                return existingEconomicAssessment;
            })
            .flatMap(economicAssessmentRepository::save)
            .map(economicAssessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EconomicAssessmentDTO> findAll() {
        LOG.debug("Request to get all EconomicAssessments");
        return economicAssessmentRepository.findAll().map(economicAssessmentMapper::toDto);
    }

    public Flux<EconomicAssessmentDTO> findAllWithEagerRelationships() {
        return economicAssessmentRepository.findAllWithEagerRelationships().map(economicAssessmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return economicAssessmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EconomicAssessmentDTO> findOne(Long id) {
        LOG.debug("Request to get EconomicAssessment : {}", id);
        return economicAssessmentRepository.findOneWithEagerRelationships(id).map(economicAssessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EconomicAssessment : {}", id);
        return economicAssessmentRepository.deleteById(id);
    }
}
