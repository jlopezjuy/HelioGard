package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityIncidentService;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import ar.com.heliguard.app.service.mapper.SecurityIncidentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SecurityIncident}.
 */
@Service
@Transactional
public class SecurityIncidentServiceImpl implements SecurityIncidentService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityIncidentServiceImpl.class);

    private final SecurityIncidentRepository securityIncidentRepository;

    private final SecurityIncidentMapper securityIncidentMapper;

    public SecurityIncidentServiceImpl(
        SecurityIncidentRepository securityIncidentRepository,
        SecurityIncidentMapper securityIncidentMapper
    ) {
        this.securityIncidentRepository = securityIncidentRepository;
        this.securityIncidentMapper = securityIncidentMapper;
    }

    @Override
    public Mono<SecurityIncidentDTO> save(SecurityIncidentDTO securityIncidentDTO) {
        LOG.debug("Request to save SecurityIncident : {}", securityIncidentDTO);
        return securityIncidentRepository.save(securityIncidentMapper.toEntity(securityIncidentDTO)).map(securityIncidentMapper::toDto);
    }

    @Override
    public Mono<SecurityIncidentDTO> update(SecurityIncidentDTO securityIncidentDTO) {
        LOG.debug("Request to update SecurityIncident : {}", securityIncidentDTO);
        return securityIncidentRepository.save(securityIncidentMapper.toEntity(securityIncidentDTO)).map(securityIncidentMapper::toDto);
    }

    @Override
    public Mono<SecurityIncidentDTO> partialUpdate(SecurityIncidentDTO securityIncidentDTO) {
        LOG.debug("Request to partially update SecurityIncident : {}", securityIncidentDTO);

        return securityIncidentRepository
            .findById(securityIncidentDTO.getId())
            .map(existingSecurityIncident -> {
                securityIncidentMapper.partialUpdate(existingSecurityIncident, securityIncidentDTO);

                return existingSecurityIncident;
            })
            .flatMap(securityIncidentRepository::save)
            .map(securityIncidentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityIncidentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SecurityIncidents");
        return securityIncidentRepository.findAllBy(pageable).map(securityIncidentMapper::toDto);
    }

    public Flux<SecurityIncidentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return securityIncidentRepository.findAllWithEagerRelationships(pageable).map(securityIncidentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return securityIncidentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityIncidentDTO> findOne(Long id) {
        LOG.debug("Request to get SecurityIncident : {}", id);
        return securityIncidentRepository.findOneWithEagerRelationships(id).map(securityIncidentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SecurityIncident : {}", id);
        return securityIncidentRepository.deleteById(id);
    }
}
