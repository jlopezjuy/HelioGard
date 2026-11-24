package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.IncidentLocalizationRepository;
import ar.com.heliguard.app.service.IncidentLocalizationService;
import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
import ar.com.heliguard.app.service.mapper.IncidentLocalizationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.IncidentLocalization}.
 */
@Service
@Transactional
public class IncidentLocalizationServiceImpl implements IncidentLocalizationService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentLocalizationServiceImpl.class);

    private final IncidentLocalizationRepository incidentLocalizationRepository;

    private final IncidentLocalizationMapper incidentLocalizationMapper;

    public IncidentLocalizationServiceImpl(
        IncidentLocalizationRepository incidentLocalizationRepository,
        IncidentLocalizationMapper incidentLocalizationMapper
    ) {
        this.incidentLocalizationRepository = incidentLocalizationRepository;
        this.incidentLocalizationMapper = incidentLocalizationMapper;
    }

    @Override
    public Mono<IncidentLocalizationDTO> save(IncidentLocalizationDTO incidentLocalizationDTO) {
        LOG.debug("Request to save IncidentLocalization : {}", incidentLocalizationDTO);
        return incidentLocalizationRepository
            .save(incidentLocalizationMapper.toEntity(incidentLocalizationDTO))
            .map(incidentLocalizationMapper::toDto);
    }

    @Override
    public Mono<IncidentLocalizationDTO> update(IncidentLocalizationDTO incidentLocalizationDTO) {
        LOG.debug("Request to update IncidentLocalization : {}", incidentLocalizationDTO);
        return incidentLocalizationRepository
            .save(incidentLocalizationMapper.toEntity(incidentLocalizationDTO))
            .map(incidentLocalizationMapper::toDto);
    }

    @Override
    public Mono<IncidentLocalizationDTO> partialUpdate(IncidentLocalizationDTO incidentLocalizationDTO) {
        LOG.debug("Request to partially update IncidentLocalization : {}", incidentLocalizationDTO);

        return incidentLocalizationRepository
            .findById(incidentLocalizationDTO.getId())
            .map(existingIncidentLocalization -> {
                incidentLocalizationMapper.partialUpdate(existingIncidentLocalization, incidentLocalizationDTO);

                return existingIncidentLocalization;
            })
            .flatMap(incidentLocalizationRepository::save)
            .map(incidentLocalizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentLocalizationDTO> findAll() {
        LOG.debug("Request to get all IncidentLocalizations");
        return incidentLocalizationRepository.findAll().map(incidentLocalizationMapper::toDto);
    }

    public Flux<IncidentLocalizationDTO> findAllWithEagerRelationships() {
        return incidentLocalizationRepository.findAllWithEagerRelationships().map(incidentLocalizationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return incidentLocalizationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IncidentLocalizationDTO> findOne(Long id) {
        LOG.debug("Request to get IncidentLocalization : {}", id);
        return incidentLocalizationRepository.findOneWithEagerRelationships(id).map(incidentLocalizationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete IncidentLocalization : {}", id);
        return incidentLocalizationRepository.deleteById(id);
    }
}
