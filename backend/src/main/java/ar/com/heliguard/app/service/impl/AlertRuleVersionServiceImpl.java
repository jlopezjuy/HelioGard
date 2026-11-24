package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.service.AlertRuleVersionService;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.mapper.AlertRuleVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AlertRuleVersion}.
 */
@Service
@Transactional
public class AlertRuleVersionServiceImpl implements AlertRuleVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(AlertRuleVersionServiceImpl.class);

    private final AlertRuleVersionRepository alertRuleVersionRepository;

    private final AlertRuleVersionMapper alertRuleVersionMapper;

    public AlertRuleVersionServiceImpl(
        AlertRuleVersionRepository alertRuleVersionRepository,
        AlertRuleVersionMapper alertRuleVersionMapper
    ) {
        this.alertRuleVersionRepository = alertRuleVersionRepository;
        this.alertRuleVersionMapper = alertRuleVersionMapper;
    }

    @Override
    public Mono<AlertRuleVersionDTO> save(AlertRuleVersionDTO alertRuleVersionDTO) {
        LOG.debug("Request to save AlertRuleVersion : {}", alertRuleVersionDTO);
        return alertRuleVersionRepository.save(alertRuleVersionMapper.toEntity(alertRuleVersionDTO)).map(alertRuleVersionMapper::toDto);
    }

    @Override
    public Mono<AlertRuleVersionDTO> update(AlertRuleVersionDTO alertRuleVersionDTO) {
        LOG.debug("Request to update AlertRuleVersion : {}", alertRuleVersionDTO);
        return alertRuleVersionRepository.save(alertRuleVersionMapper.toEntity(alertRuleVersionDTO)).map(alertRuleVersionMapper::toDto);
    }

    @Override
    public Mono<AlertRuleVersionDTO> partialUpdate(AlertRuleVersionDTO alertRuleVersionDTO) {
        LOG.debug("Request to partially update AlertRuleVersion : {}", alertRuleVersionDTO);

        return alertRuleVersionRepository
            .findById(alertRuleVersionDTO.getId())
            .map(existingAlertRuleVersion -> {
                alertRuleVersionMapper.partialUpdate(existingAlertRuleVersion, alertRuleVersionDTO);

                return existingAlertRuleVersion;
            })
            .flatMap(alertRuleVersionRepository::save)
            .map(alertRuleVersionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AlertRuleVersionDTO> findAll() {
        LOG.debug("Request to get all AlertRuleVersions");
        return alertRuleVersionRepository.findAll().map(alertRuleVersionMapper::toDto);
    }

    public Flux<AlertRuleVersionDTO> findAllWithEagerRelationships() {
        return alertRuleVersionRepository.findAllWithEagerRelationships().map(alertRuleVersionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return alertRuleVersionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AlertRuleVersionDTO> findOne(Long id) {
        LOG.debug("Request to get AlertRuleVersion : {}", id);
        return alertRuleVersionRepository.findOneWithEagerRelationships(id).map(alertRuleVersionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AlertRuleVersion : {}", id);
        return alertRuleVersionRepository.deleteById(id);
    }
}
