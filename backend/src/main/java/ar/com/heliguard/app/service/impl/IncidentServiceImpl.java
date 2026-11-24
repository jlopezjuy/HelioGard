package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.IncidentService;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.mapper.IncidentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Incident}.
 */
@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentServiceImpl.class);

    private final IncidentRepository incidentRepository;

    private final IncidentMapper incidentMapper;

    public IncidentServiceImpl(IncidentRepository incidentRepository, IncidentMapper incidentMapper) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
    }

    @Override
    public Mono<IncidentDTO> save(IncidentDTO incidentDTO) {
        LOG.debug("Request to save Incident : {}", incidentDTO);
        return incidentRepository.save(incidentMapper.toEntity(incidentDTO)).map(incidentMapper::toDto);
    }

    @Override
    public Mono<IncidentDTO> update(IncidentDTO incidentDTO) {
        LOG.debug("Request to update Incident : {}", incidentDTO);
        return incidentRepository.save(incidentMapper.toEntity(incidentDTO)).map(incidentMapper::toDto);
    }

    @Override
    public Mono<IncidentDTO> partialUpdate(IncidentDTO incidentDTO) {
        LOG.debug("Request to partially update Incident : {}", incidentDTO);

        return incidentRepository
            .findById(incidentDTO.getId())
            .map(existingIncident -> {
                incidentMapper.partialUpdate(existingIncident, incidentDTO);

                return existingIncident;
            })
            .flatMap(incidentRepository::save)
            .map(incidentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Incidents");
        return incidentRepository.findAllBy(pageable).map(incidentMapper::toDto);
    }

    public Flux<IncidentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return incidentRepository.findAllWithEagerRelationships(pageable).map(incidentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return incidentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IncidentDTO> findOne(Long id) {
        LOG.debug("Request to get Incident : {}", id);
        return incidentRepository.findOneWithEagerRelationships(id).map(incidentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Incident : {}", id);
        return incidentRepository.deleteById(id);
    }
}
