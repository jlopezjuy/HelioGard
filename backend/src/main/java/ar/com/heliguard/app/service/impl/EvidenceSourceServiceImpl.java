package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.EvidenceSourceRepository;
import ar.com.heliguard.app.service.EvidenceSourceService;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import ar.com.heliguard.app.service.mapper.EvidenceSourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.EvidenceSource}.
 */
@Service
@Transactional
public class EvidenceSourceServiceImpl implements EvidenceSourceService {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceSourceServiceImpl.class);

    private final EvidenceSourceRepository evidenceSourceRepository;

    private final EvidenceSourceMapper evidenceSourceMapper;

    public EvidenceSourceServiceImpl(EvidenceSourceRepository evidenceSourceRepository, EvidenceSourceMapper evidenceSourceMapper) {
        this.evidenceSourceRepository = evidenceSourceRepository;
        this.evidenceSourceMapper = evidenceSourceMapper;
    }

    @Override
    public Mono<EvidenceSourceDTO> save(EvidenceSourceDTO evidenceSourceDTO) {
        LOG.debug("Request to save EvidenceSource : {}", evidenceSourceDTO);
        return evidenceSourceRepository.save(evidenceSourceMapper.toEntity(evidenceSourceDTO)).map(evidenceSourceMapper::toDto);
    }

    @Override
    public Mono<EvidenceSourceDTO> update(EvidenceSourceDTO evidenceSourceDTO) {
        LOG.debug("Request to update EvidenceSource : {}", evidenceSourceDTO);
        return evidenceSourceRepository.save(evidenceSourceMapper.toEntity(evidenceSourceDTO)).map(evidenceSourceMapper::toDto);
    }

    @Override
    public Mono<EvidenceSourceDTO> partialUpdate(EvidenceSourceDTO evidenceSourceDTO) {
        LOG.debug("Request to partially update EvidenceSource : {}", evidenceSourceDTO);

        return evidenceSourceRepository
            .findById(evidenceSourceDTO.getId())
            .map(existingEvidenceSource -> {
                evidenceSourceMapper.partialUpdate(existingEvidenceSource, evidenceSourceDTO);

                return existingEvidenceSource;
            })
            .flatMap(evidenceSourceRepository::save)
            .map(evidenceSourceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EvidenceSourceDTO> findAll() {
        LOG.debug("Request to get all EvidenceSources");
        return evidenceSourceRepository.findAll().map(evidenceSourceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return evidenceSourceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EvidenceSourceDTO> findOne(Long id) {
        LOG.debug("Request to get EvidenceSource : {}", id);
        return evidenceSourceRepository.findById(id).map(evidenceSourceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EvidenceSource : {}", id);
        return evidenceSourceRepository.deleteById(id);
    }
}
