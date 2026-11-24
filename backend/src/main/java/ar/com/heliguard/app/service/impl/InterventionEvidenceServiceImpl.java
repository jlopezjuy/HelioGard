package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.InterventionEvidenceRepository;
import ar.com.heliguard.app.service.InterventionEvidenceService;
import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
import ar.com.heliguard.app.service.mapper.InterventionEvidenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.InterventionEvidence}.
 */
@Service
@Transactional
public class InterventionEvidenceServiceImpl implements InterventionEvidenceService {

    private static final Logger LOG = LoggerFactory.getLogger(InterventionEvidenceServiceImpl.class);

    private final InterventionEvidenceRepository interventionEvidenceRepository;

    private final InterventionEvidenceMapper interventionEvidenceMapper;

    public InterventionEvidenceServiceImpl(
        InterventionEvidenceRepository interventionEvidenceRepository,
        InterventionEvidenceMapper interventionEvidenceMapper
    ) {
        this.interventionEvidenceRepository = interventionEvidenceRepository;
        this.interventionEvidenceMapper = interventionEvidenceMapper;
    }

    @Override
    public Mono<InterventionEvidenceDTO> save(InterventionEvidenceDTO interventionEvidenceDTO) {
        LOG.debug("Request to save InterventionEvidence : {}", interventionEvidenceDTO);
        return interventionEvidenceRepository
            .save(interventionEvidenceMapper.toEntity(interventionEvidenceDTO))
            .map(interventionEvidenceMapper::toDto);
    }

    @Override
    public Mono<InterventionEvidenceDTO> update(InterventionEvidenceDTO interventionEvidenceDTO) {
        LOG.debug("Request to update InterventionEvidence : {}", interventionEvidenceDTO);
        return interventionEvidenceRepository
            .save(interventionEvidenceMapper.toEntity(interventionEvidenceDTO))
            .map(interventionEvidenceMapper::toDto);
    }

    @Override
    public Mono<InterventionEvidenceDTO> partialUpdate(InterventionEvidenceDTO interventionEvidenceDTO) {
        LOG.debug("Request to partially update InterventionEvidence : {}", interventionEvidenceDTO);

        return interventionEvidenceRepository
            .findById(interventionEvidenceDTO.getId())
            .map(existingInterventionEvidence -> {
                interventionEvidenceMapper.partialUpdate(existingInterventionEvidence, interventionEvidenceDTO);

                return existingInterventionEvidence;
            })
            .flatMap(interventionEvidenceRepository::save)
            .map(interventionEvidenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InterventionEvidenceDTO> findAll() {
        LOG.debug("Request to get all InterventionEvidences");
        return interventionEvidenceRepository.findAll().map(interventionEvidenceMapper::toDto);
    }

    public Flux<InterventionEvidenceDTO> findAllWithEagerRelationships() {
        return interventionEvidenceRepository.findAllWithEagerRelationships().map(interventionEvidenceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return interventionEvidenceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InterventionEvidenceDTO> findOne(Long id) {
        LOG.debug("Request to get InterventionEvidence : {}", id);
        return interventionEvidenceRepository.findOneWithEagerRelationships(id).map(interventionEvidenceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InterventionEvidence : {}", id);
        return interventionEvidenceRepository.deleteById(id);
    }
}
