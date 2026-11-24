package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.WorkOrderTaskRepository;
import ar.com.heliguard.app.service.WorkOrderTaskService;
import ar.com.heliguard.app.service.dto.WorkOrderTaskDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.WorkOrderTask}.
 */
@Service
@Transactional
public class WorkOrderTaskServiceImpl implements WorkOrderTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderTaskServiceImpl.class);

    private final WorkOrderTaskRepository workOrderTaskRepository;

    private final WorkOrderTaskMapper workOrderTaskMapper;

    public WorkOrderTaskServiceImpl(WorkOrderTaskRepository workOrderTaskRepository, WorkOrderTaskMapper workOrderTaskMapper) {
        this.workOrderTaskRepository = workOrderTaskRepository;
        this.workOrderTaskMapper = workOrderTaskMapper;
    }

    @Override
    public Mono<WorkOrderTaskDTO> save(WorkOrderTaskDTO workOrderTaskDTO) {
        LOG.debug("Request to save WorkOrderTask : {}", workOrderTaskDTO);
        return workOrderTaskRepository.save(workOrderTaskMapper.toEntity(workOrderTaskDTO)).map(workOrderTaskMapper::toDto);
    }

    @Override
    public Mono<WorkOrderTaskDTO> update(WorkOrderTaskDTO workOrderTaskDTO) {
        LOG.debug("Request to update WorkOrderTask : {}", workOrderTaskDTO);
        return workOrderTaskRepository.save(workOrderTaskMapper.toEntity(workOrderTaskDTO)).map(workOrderTaskMapper::toDto);
    }

    @Override
    public Mono<WorkOrderTaskDTO> partialUpdate(WorkOrderTaskDTO workOrderTaskDTO) {
        LOG.debug("Request to partially update WorkOrderTask : {}", workOrderTaskDTO);

        return workOrderTaskRepository
            .findById(workOrderTaskDTO.getId())
            .map(existingWorkOrderTask -> {
                workOrderTaskMapper.partialUpdate(existingWorkOrderTask, workOrderTaskDTO);

                return existingWorkOrderTask;
            })
            .flatMap(workOrderTaskRepository::save)
            .map(workOrderTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderTaskDTO> findAll() {
        LOG.debug("Request to get all WorkOrderTasks");
        return workOrderTaskRepository.findAll().map(workOrderTaskMapper::toDto);
    }

    public Flux<WorkOrderTaskDTO> findAllWithEagerRelationships() {
        return workOrderTaskRepository.findAllWithEagerRelationships().map(workOrderTaskMapper::toDto);
    }

    public Mono<Long> countAll() {
        return workOrderTaskRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderTaskDTO> findOne(Long id) {
        LOG.debug("Request to get WorkOrderTask : {}", id);
        return workOrderTaskRepository.findOneWithEagerRelationships(id).map(workOrderTaskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete WorkOrderTask : {}", id);
        return workOrderTaskRepository.deleteById(id);
    }
}
