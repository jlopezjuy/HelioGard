package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.InverterMpptInputRepository;
import ar.com.heliguard.app.service.InverterMpptInputService;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.mapper.InverterMpptInputMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.InverterMpptInput}.
 */
@Service
@Transactional
public class InverterMpptInputServiceImpl implements InverterMpptInputService {

    private static final Logger LOG = LoggerFactory.getLogger(InverterMpptInputServiceImpl.class);

    private final InverterMpptInputRepository inverterMpptInputRepository;

    private final InverterMpptInputMapper inverterMpptInputMapper;

    public InverterMpptInputServiceImpl(
        InverterMpptInputRepository inverterMpptInputRepository,
        InverterMpptInputMapper inverterMpptInputMapper
    ) {
        this.inverterMpptInputRepository = inverterMpptInputRepository;
        this.inverterMpptInputMapper = inverterMpptInputMapper;
    }

    @Override
    public Mono<InverterMpptInputDTO> save(InverterMpptInputDTO inverterMpptInputDTO) {
        LOG.debug("Request to save InverterMpptInput : {}", inverterMpptInputDTO);
        return inverterMpptInputRepository.save(inverterMpptInputMapper.toEntity(inverterMpptInputDTO)).map(inverterMpptInputMapper::toDto);
    }

    @Override
    public Mono<InverterMpptInputDTO> update(InverterMpptInputDTO inverterMpptInputDTO) {
        LOG.debug("Request to update InverterMpptInput : {}", inverterMpptInputDTO);
        return inverterMpptInputRepository.save(inverterMpptInputMapper.toEntity(inverterMpptInputDTO)).map(inverterMpptInputMapper::toDto);
    }

    @Override
    public Mono<InverterMpptInputDTO> partialUpdate(InverterMpptInputDTO inverterMpptInputDTO) {
        LOG.debug("Request to partially update InverterMpptInput : {}", inverterMpptInputDTO);

        return inverterMpptInputRepository
            .findById(inverterMpptInputDTO.getId())
            .map(existingInverterMpptInput -> {
                inverterMpptInputMapper.partialUpdate(existingInverterMpptInput, inverterMpptInputDTO);

                return existingInverterMpptInput;
            })
            .flatMap(inverterMpptInputRepository::save)
            .map(inverterMpptInputMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InverterMpptInputDTO> findAll() {
        LOG.debug("Request to get all InverterMpptInputs");
        return inverterMpptInputRepository.findAll().map(inverterMpptInputMapper::toDto);
    }

    public Flux<InverterMpptInputDTO> findAllWithEagerRelationships() {
        return inverterMpptInputRepository.findAllWithEagerRelationships().map(inverterMpptInputMapper::toDto);
    }

    public Mono<Long> countAll() {
        return inverterMpptInputRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InverterMpptInputDTO> findOne(Long id) {
        LOG.debug("Request to get InverterMpptInput : {}", id);
        return inverterMpptInputRepository.findOneWithEagerRelationships(id).map(inverterMpptInputMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InverterMpptInput : {}", id);
        return inverterMpptInputRepository.deleteById(id);
    }
}
