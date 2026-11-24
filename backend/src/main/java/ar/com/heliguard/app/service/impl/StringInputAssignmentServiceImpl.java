package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.StringInputAssignmentRepository;
import ar.com.heliguard.app.service.StringInputAssignmentService;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import ar.com.heliguard.app.service.mapper.StringInputAssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.StringInputAssignment}.
 */
@Service
@Transactional
public class StringInputAssignmentServiceImpl implements StringInputAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(StringInputAssignmentServiceImpl.class);

    private final StringInputAssignmentRepository stringInputAssignmentRepository;

    private final StringInputAssignmentMapper stringInputAssignmentMapper;

    public StringInputAssignmentServiceImpl(
        StringInputAssignmentRepository stringInputAssignmentRepository,
        StringInputAssignmentMapper stringInputAssignmentMapper
    ) {
        this.stringInputAssignmentRepository = stringInputAssignmentRepository;
        this.stringInputAssignmentMapper = stringInputAssignmentMapper;
    }

    @Override
    public Mono<StringInputAssignmentDTO> save(StringInputAssignmentDTO stringInputAssignmentDTO) {
        LOG.debug("Request to save StringInputAssignment : {}", stringInputAssignmentDTO);
        return stringInputAssignmentRepository
            .save(stringInputAssignmentMapper.toEntity(stringInputAssignmentDTO))
            .map(stringInputAssignmentMapper::toDto);
    }

    @Override
    public Mono<StringInputAssignmentDTO> update(StringInputAssignmentDTO stringInputAssignmentDTO) {
        LOG.debug("Request to update StringInputAssignment : {}", stringInputAssignmentDTO);
        return stringInputAssignmentRepository
            .save(stringInputAssignmentMapper.toEntity(stringInputAssignmentDTO))
            .map(stringInputAssignmentMapper::toDto);
    }

    @Override
    public Mono<StringInputAssignmentDTO> partialUpdate(StringInputAssignmentDTO stringInputAssignmentDTO) {
        LOG.debug("Request to partially update StringInputAssignment : {}", stringInputAssignmentDTO);

        return stringInputAssignmentRepository
            .findById(stringInputAssignmentDTO.getId())
            .map(existingStringInputAssignment -> {
                stringInputAssignmentMapper.partialUpdate(existingStringInputAssignment, stringInputAssignmentDTO);

                return existingStringInputAssignment;
            })
            .flatMap(stringInputAssignmentRepository::save)
            .map(stringInputAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StringInputAssignmentDTO> findAll() {
        LOG.debug("Request to get all StringInputAssignments");
        return stringInputAssignmentRepository.findAll().map(stringInputAssignmentMapper::toDto);
    }

    public Flux<StringInputAssignmentDTO> findAllWithEagerRelationships() {
        return stringInputAssignmentRepository.findAllWithEagerRelationships().map(stringInputAssignmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return stringInputAssignmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StringInputAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get StringInputAssignment : {}", id);
        return stringInputAssignmentRepository.findOneWithEagerRelationships(id).map(stringInputAssignmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete StringInputAssignment : {}", id);
        return stringInputAssignmentRepository.deleteById(id);
    }
}
