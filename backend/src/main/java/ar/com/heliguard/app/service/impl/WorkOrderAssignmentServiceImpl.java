package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.service.WorkOrderAssignmentService;
import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderAssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.WorkOrderAssignment}.
 */
@Service
@Transactional
public class WorkOrderAssignmentServiceImpl implements WorkOrderAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderAssignmentServiceImpl.class);

    private final WorkOrderAssignmentRepository workOrderAssignmentRepository;

    private final WorkOrderAssignmentMapper workOrderAssignmentMapper;

    public WorkOrderAssignmentServiceImpl(
        WorkOrderAssignmentRepository workOrderAssignmentRepository,
        WorkOrderAssignmentMapper workOrderAssignmentMapper
    ) {
        this.workOrderAssignmentRepository = workOrderAssignmentRepository;
        this.workOrderAssignmentMapper = workOrderAssignmentMapper;
    }

    @Override
    public Mono<WorkOrderAssignmentDTO> save(WorkOrderAssignmentDTO workOrderAssignmentDTO) {
        LOG.debug("Request to save WorkOrderAssignment : {}", workOrderAssignmentDTO);
        return workOrderAssignmentRepository
            .save(workOrderAssignmentMapper.toEntity(workOrderAssignmentDTO))
            .map(workOrderAssignmentMapper::toDto);
    }

    @Override
    public Mono<WorkOrderAssignmentDTO> update(WorkOrderAssignmentDTO workOrderAssignmentDTO) {
        LOG.debug("Request to update WorkOrderAssignment : {}", workOrderAssignmentDTO);
        return workOrderAssignmentRepository
            .save(workOrderAssignmentMapper.toEntity(workOrderAssignmentDTO))
            .map(workOrderAssignmentMapper::toDto);
    }

    @Override
    public Mono<WorkOrderAssignmentDTO> partialUpdate(WorkOrderAssignmentDTO workOrderAssignmentDTO) {
        LOG.debug("Request to partially update WorkOrderAssignment : {}", workOrderAssignmentDTO);

        return workOrderAssignmentRepository
            .findById(workOrderAssignmentDTO.getId())
            .map(existingWorkOrderAssignment -> {
                workOrderAssignmentMapper.partialUpdate(existingWorkOrderAssignment, workOrderAssignmentDTO);

                return existingWorkOrderAssignment;
            })
            .flatMap(workOrderAssignmentRepository::save)
            .map(workOrderAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderAssignmentDTO> findAll() {
        LOG.debug("Request to get all WorkOrderAssignments");
        return workOrderAssignmentRepository.findAll().map(workOrderAssignmentMapper::toDto);
    }

    public Flux<WorkOrderAssignmentDTO> findAllWithEagerRelationships() {
        return workOrderAssignmentRepository.findAllWithEagerRelationships().map(workOrderAssignmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return workOrderAssignmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get WorkOrderAssignment : {}", id);
        return workOrderAssignmentRepository.findOneWithEagerRelationships(id).map(workOrderAssignmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete WorkOrderAssignment : {}", id);
        return workOrderAssignmentRepository.deleteById(id);
    }
}
