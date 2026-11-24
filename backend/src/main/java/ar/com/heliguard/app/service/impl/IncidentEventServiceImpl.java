package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.IncidentEventRepository;
import ar.com.heliguard.app.service.IncidentEventService;
import ar.com.heliguard.app.service.dto.IncidentEventDTO;
import ar.com.heliguard.app.service.mapper.IncidentEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.IncidentEvent}.
 */
@Service
@Transactional
public class IncidentEventServiceImpl implements IncidentEventService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventServiceImpl.class);

    private final IncidentEventRepository incidentEventRepository;

    private final IncidentEventMapper incidentEventMapper;

    public IncidentEventServiceImpl(IncidentEventRepository incidentEventRepository, IncidentEventMapper incidentEventMapper) {
        this.incidentEventRepository = incidentEventRepository;
        this.incidentEventMapper = incidentEventMapper;
    }

    @Override
    public Mono<IncidentEventDTO> save(IncidentEventDTO incidentEventDTO) {
        LOG.debug("Request to save IncidentEvent : {}", incidentEventDTO);
        return incidentEventRepository.save(incidentEventMapper.toEntity(incidentEventDTO)).map(incidentEventMapper::toDto);
    }

    @Override
    public Mono<IncidentEventDTO> update(IncidentEventDTO incidentEventDTO) {
        LOG.debug("Request to update IncidentEvent : {}", incidentEventDTO);
        return incidentEventRepository.save(incidentEventMapper.toEntity(incidentEventDTO)).map(incidentEventMapper::toDto);
    }

    @Override
    public Mono<IncidentEventDTO> partialUpdate(IncidentEventDTO incidentEventDTO) {
        LOG.debug("Request to partially update IncidentEvent : {}", incidentEventDTO);

        return incidentEventRepository
            .findById(incidentEventDTO.getId())
            .map(existingIncidentEvent -> {
                incidentEventMapper.partialUpdate(existingIncidentEvent, incidentEventDTO);

                return existingIncidentEvent;
            })
            .flatMap(incidentEventRepository::save)
            .map(incidentEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentEventDTO> findAll() {
        LOG.debug("Request to get all IncidentEvents");
        return incidentEventRepository.findAll().map(incidentEventMapper::toDto);
    }

    public Flux<IncidentEventDTO> findAllWithEagerRelationships() {
        return incidentEventRepository.findAllWithEagerRelationships().map(incidentEventMapper::toDto);
    }

    public Mono<Long> countAll() {
        return incidentEventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IncidentEventDTO> findOne(Long id) {
        LOG.debug("Request to get IncidentEvent : {}", id);
        return incidentEventRepository.findOneWithEagerRelationships(id).map(incidentEventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete IncidentEvent : {}", id);
        return incidentEventRepository.deleteById(id);
    }
}
