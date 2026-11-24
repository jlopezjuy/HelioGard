package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DataProvenanceRepository;
import ar.com.heliguard.app.service.DataProvenanceService;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.mapper.DataProvenanceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DataProvenance}.
 */
@Service
@Transactional
public class DataProvenanceServiceImpl implements DataProvenanceService {

    private static final Logger LOG = LoggerFactory.getLogger(DataProvenanceServiceImpl.class);

    private final DataProvenanceRepository dataProvenanceRepository;

    private final DataProvenanceMapper dataProvenanceMapper;

    public DataProvenanceServiceImpl(DataProvenanceRepository dataProvenanceRepository, DataProvenanceMapper dataProvenanceMapper) {
        this.dataProvenanceRepository = dataProvenanceRepository;
        this.dataProvenanceMapper = dataProvenanceMapper;
    }

    @Override
    public Mono<DataProvenanceDTO> save(DataProvenanceDTO dataProvenanceDTO) {
        LOG.debug("Request to save DataProvenance : {}", dataProvenanceDTO);
        return dataProvenanceRepository.save(dataProvenanceMapper.toEntity(dataProvenanceDTO)).map(dataProvenanceMapper::toDto);
    }

    @Override
    public Mono<DataProvenanceDTO> update(DataProvenanceDTO dataProvenanceDTO) {
        LOG.debug("Request to update DataProvenance : {}", dataProvenanceDTO);
        return dataProvenanceRepository.save(dataProvenanceMapper.toEntity(dataProvenanceDTO)).map(dataProvenanceMapper::toDto);
    }

    @Override
    public Mono<DataProvenanceDTO> partialUpdate(DataProvenanceDTO dataProvenanceDTO) {
        LOG.debug("Request to partially update DataProvenance : {}", dataProvenanceDTO);

        return dataProvenanceRepository
            .findById(dataProvenanceDTO.getId())
            .map(existingDataProvenance -> {
                dataProvenanceMapper.partialUpdate(existingDataProvenance, dataProvenanceDTO);

                return existingDataProvenance;
            })
            .flatMap(dataProvenanceRepository::save)
            .map(dataProvenanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DataProvenanceDTO> findAll() {
        LOG.debug("Request to get all DataProvenances");
        return dataProvenanceRepository.findAll().map(dataProvenanceMapper::toDto);
    }

    public Flux<DataProvenanceDTO> findAllWithEagerRelationships() {
        return dataProvenanceRepository.findAllWithEagerRelationships().map(dataProvenanceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return dataProvenanceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DataProvenanceDTO> findOne(Long id) {
        LOG.debug("Request to get DataProvenance : {}", id);
        return dataProvenanceRepository.findOneWithEagerRelationships(id).map(dataProvenanceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DataProvenance : {}", id);
        return dataProvenanceRepository.deleteById(id);
    }
}
