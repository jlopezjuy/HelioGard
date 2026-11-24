package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.VerificationResultRepository;
import ar.com.heliguard.app.service.VerificationResultService;
import ar.com.heliguard.app.service.dto.VerificationResultDTO;
import ar.com.heliguard.app.service.mapper.VerificationResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.VerificationResult}.
 */
@Service
@Transactional
public class VerificationResultServiceImpl implements VerificationResultService {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationResultServiceImpl.class);

    private final VerificationResultRepository verificationResultRepository;

    private final VerificationResultMapper verificationResultMapper;

    public VerificationResultServiceImpl(
        VerificationResultRepository verificationResultRepository,
        VerificationResultMapper verificationResultMapper
    ) {
        this.verificationResultRepository = verificationResultRepository;
        this.verificationResultMapper = verificationResultMapper;
    }

    @Override
    public Mono<VerificationResultDTO> save(VerificationResultDTO verificationResultDTO) {
        LOG.debug("Request to save VerificationResult : {}", verificationResultDTO);
        return verificationResultRepository
            .save(verificationResultMapper.toEntity(verificationResultDTO))
            .map(verificationResultMapper::toDto);
    }

    @Override
    public Mono<VerificationResultDTO> update(VerificationResultDTO verificationResultDTO) {
        LOG.debug("Request to update VerificationResult : {}", verificationResultDTO);
        return verificationResultRepository
            .save(verificationResultMapper.toEntity(verificationResultDTO))
            .map(verificationResultMapper::toDto);
    }

    @Override
    public Mono<VerificationResultDTO> partialUpdate(VerificationResultDTO verificationResultDTO) {
        LOG.debug("Request to partially update VerificationResult : {}", verificationResultDTO);

        return verificationResultRepository
            .findById(verificationResultDTO.getId())
            .map(existingVerificationResult -> {
                verificationResultMapper.partialUpdate(existingVerificationResult, verificationResultDTO);

                return existingVerificationResult;
            })
            .flatMap(verificationResultRepository::save)
            .map(verificationResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VerificationResultDTO> findAll() {
        LOG.debug("Request to get all VerificationResults");
        return verificationResultRepository.findAll().map(verificationResultMapper::toDto);
    }

    public Flux<VerificationResultDTO> findAllWithEagerRelationships() {
        return verificationResultRepository.findAllWithEagerRelationships().map(verificationResultMapper::toDto);
    }

    public Mono<Long> countAll() {
        return verificationResultRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<VerificationResultDTO> findOne(Long id) {
        LOG.debug("Request to get VerificationResult : {}", id);
        return verificationResultRepository.findOneWithEagerRelationships(id).map(verificationResultMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete VerificationResult : {}", id);
        return verificationResultRepository.deleteById(id);
    }
}
