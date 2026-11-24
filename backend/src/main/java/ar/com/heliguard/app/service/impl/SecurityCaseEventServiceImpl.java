package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SecurityCaseEventRepository;
import ar.com.heliguard.app.service.SecurityCaseEventService;
import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
import ar.com.heliguard.app.service.mapper.SecurityCaseEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SecurityCaseEvent}.
 */
@Service
@Transactional
public class SecurityCaseEventServiceImpl implements SecurityCaseEventService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityCaseEventServiceImpl.class);

    private final SecurityCaseEventRepository securityCaseEventRepository;

    private final SecurityCaseEventMapper securityCaseEventMapper;

    public SecurityCaseEventServiceImpl(
        SecurityCaseEventRepository securityCaseEventRepository,
        SecurityCaseEventMapper securityCaseEventMapper
    ) {
        this.securityCaseEventRepository = securityCaseEventRepository;
        this.securityCaseEventMapper = securityCaseEventMapper;
    }

    @Override
    public Mono<SecurityCaseEventDTO> save(SecurityCaseEventDTO securityCaseEventDTO) {
        LOG.debug("Request to save SecurityCaseEvent : {}", securityCaseEventDTO);
        return securityCaseEventRepository.save(securityCaseEventMapper.toEntity(securityCaseEventDTO)).map(securityCaseEventMapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEventDTO> update(SecurityCaseEventDTO securityCaseEventDTO) {
        LOG.debug("Request to update SecurityCaseEvent : {}", securityCaseEventDTO);
        return securityCaseEventRepository.save(securityCaseEventMapper.toEntity(securityCaseEventDTO)).map(securityCaseEventMapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEventDTO> partialUpdate(SecurityCaseEventDTO securityCaseEventDTO) {
        LOG.debug("Request to partially update SecurityCaseEvent : {}", securityCaseEventDTO);

        return securityCaseEventRepository
            .findById(securityCaseEventDTO.getId())
            .map(existingSecurityCaseEvent -> {
                securityCaseEventMapper.partialUpdate(existingSecurityCaseEvent, securityCaseEventDTO);

                return existingSecurityCaseEvent;
            })
            .flatMap(securityCaseEventRepository::save)
            .map(securityCaseEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityCaseEventDTO> findAll() {
        LOG.debug("Request to get all SecurityCaseEvents");
        return securityCaseEventRepository.findAll().map(securityCaseEventMapper::toDto);
    }

    public Flux<SecurityCaseEventDTO> findAllWithEagerRelationships() {
        return securityCaseEventRepository.findAllWithEagerRelationships().map(securityCaseEventMapper::toDto);
    }

    public Mono<Long> countAll() {
        return securityCaseEventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityCaseEventDTO> findOne(Long id) {
        LOG.debug("Request to get SecurityCaseEvent : {}", id);
        return securityCaseEventRepository.findOneWithEagerRelationships(id).map(securityCaseEventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SecurityCaseEvent : {}", id);
        return securityCaseEventRepository.deleteById(id);
    }
}
