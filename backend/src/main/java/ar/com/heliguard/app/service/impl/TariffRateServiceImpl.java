package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.TariffRateService;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
import ar.com.heliguard.app.service.mapper.TariffRateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TariffRate}.
 */
@Service
@Transactional
public class TariffRateServiceImpl implements TariffRateService {

    private static final Logger LOG = LoggerFactory.getLogger(TariffRateServiceImpl.class);

    private final TariffRateRepository tariffRateRepository;

    private final TariffRateMapper tariffRateMapper;

    public TariffRateServiceImpl(TariffRateRepository tariffRateRepository, TariffRateMapper tariffRateMapper) {
        this.tariffRateRepository = tariffRateRepository;
        this.tariffRateMapper = tariffRateMapper;
    }

    @Override
    public Mono<TariffRateDTO> save(TariffRateDTO tariffRateDTO) {
        LOG.debug("Request to save TariffRate : {}", tariffRateDTO);
        return tariffRateRepository.save(tariffRateMapper.toEntity(tariffRateDTO)).map(tariffRateMapper::toDto);
    }

    @Override
    public Mono<TariffRateDTO> update(TariffRateDTO tariffRateDTO) {
        LOG.debug("Request to update TariffRate : {}", tariffRateDTO);
        return tariffRateRepository.save(tariffRateMapper.toEntity(tariffRateDTO)).map(tariffRateMapper::toDto);
    }

    @Override
    public Mono<TariffRateDTO> partialUpdate(TariffRateDTO tariffRateDTO) {
        LOG.debug("Request to partially update TariffRate : {}", tariffRateDTO);

        return tariffRateRepository
            .findById(tariffRateDTO.getId())
            .map(existingTariffRate -> {
                tariffRateMapper.partialUpdate(existingTariffRate, tariffRateDTO);

                return existingTariffRate;
            })
            .flatMap(tariffRateRepository::save)
            .map(tariffRateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffRateDTO> findAll() {
        LOG.debug("Request to get all TariffRates");
        return tariffRateRepository.findAll().map(tariffRateMapper::toDto);
    }

    public Flux<TariffRateDTO> findAllWithEagerRelationships() {
        return tariffRateRepository.findAllWithEagerRelationships().map(tariffRateMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tariffRateRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TariffRateDTO> findOne(Long id) {
        LOG.debug("Request to get TariffRate : {}", id);
        return tariffRateRepository.findOneWithEagerRelationships(id).map(tariffRateMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TariffRate : {}", id);
        return tariffRateRepository.deleteById(id);
    }
}
