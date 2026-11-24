package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.service.AlertRuleService;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import ar.com.heliguard.app.service.mapper.AlertRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.AlertRule}.
 */
@Service
@Transactional
public class AlertRuleServiceImpl implements AlertRuleService {

    private static final Logger LOG = LoggerFactory.getLogger(AlertRuleServiceImpl.class);

    private final AlertRuleRepository alertRuleRepository;

    private final AlertRuleMapper alertRuleMapper;

    public AlertRuleServiceImpl(AlertRuleRepository alertRuleRepository, AlertRuleMapper alertRuleMapper) {
        this.alertRuleRepository = alertRuleRepository;
        this.alertRuleMapper = alertRuleMapper;
    }

    @Override
    public Mono<AlertRuleDTO> save(AlertRuleDTO alertRuleDTO) {
        LOG.debug("Request to save AlertRule : {}", alertRuleDTO);
        return alertRuleRepository.save(alertRuleMapper.toEntity(alertRuleDTO)).map(alertRuleMapper::toDto);
    }

    @Override
    public Mono<AlertRuleDTO> update(AlertRuleDTO alertRuleDTO) {
        LOG.debug("Request to update AlertRule : {}", alertRuleDTO);
        return alertRuleRepository.save(alertRuleMapper.toEntity(alertRuleDTO)).map(alertRuleMapper::toDto);
    }

    @Override
    public Mono<AlertRuleDTO> partialUpdate(AlertRuleDTO alertRuleDTO) {
        LOG.debug("Request to partially update AlertRule : {}", alertRuleDTO);

        return alertRuleRepository
            .findById(alertRuleDTO.getId())
            .map(existingAlertRule -> {
                alertRuleMapper.partialUpdate(existingAlertRule, alertRuleDTO);

                return existingAlertRule;
            })
            .flatMap(alertRuleRepository::save)
            .map(alertRuleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AlertRuleDTO> findAll() {
        LOG.debug("Request to get all AlertRules");
        return alertRuleRepository.findAll().map(alertRuleMapper::toDto);
    }

    public Flux<AlertRuleDTO> findAllWithEagerRelationships() {
        return alertRuleRepository.findAllWithEagerRelationships().map(alertRuleMapper::toDto);
    }

    public Mono<Long> countAll() {
        return alertRuleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AlertRuleDTO> findOne(Long id) {
        LOG.debug("Request to get AlertRule : {}", id);
        return alertRuleRepository.findOneWithEagerRelationships(id).map(alertRuleMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AlertRule : {}", id);
        return alertRuleRepository.deleteById(id);
    }
}
