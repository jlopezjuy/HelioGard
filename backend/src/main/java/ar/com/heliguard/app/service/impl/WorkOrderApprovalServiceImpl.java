package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.WorkOrderApprovalRepository;
import ar.com.heliguard.app.service.WorkOrderApprovalService;
import ar.com.heliguard.app.service.dto.WorkOrderApprovalDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderApprovalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.WorkOrderApproval}.
 */
@Service
@Transactional
public class WorkOrderApprovalServiceImpl implements WorkOrderApprovalService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderApprovalServiceImpl.class);

    private final WorkOrderApprovalRepository workOrderApprovalRepository;

    private final WorkOrderApprovalMapper workOrderApprovalMapper;

    public WorkOrderApprovalServiceImpl(
        WorkOrderApprovalRepository workOrderApprovalRepository,
        WorkOrderApprovalMapper workOrderApprovalMapper
    ) {
        this.workOrderApprovalRepository = workOrderApprovalRepository;
        this.workOrderApprovalMapper = workOrderApprovalMapper;
    }

    @Override
    public Mono<WorkOrderApprovalDTO> save(WorkOrderApprovalDTO workOrderApprovalDTO) {
        LOG.debug("Request to save WorkOrderApproval : {}", workOrderApprovalDTO);
        return workOrderApprovalRepository.save(workOrderApprovalMapper.toEntity(workOrderApprovalDTO)).map(workOrderApprovalMapper::toDto);
    }

    @Override
    public Mono<WorkOrderApprovalDTO> update(WorkOrderApprovalDTO workOrderApprovalDTO) {
        LOG.debug("Request to update WorkOrderApproval : {}", workOrderApprovalDTO);
        return workOrderApprovalRepository.save(workOrderApprovalMapper.toEntity(workOrderApprovalDTO)).map(workOrderApprovalMapper::toDto);
    }

    @Override
    public Mono<WorkOrderApprovalDTO> partialUpdate(WorkOrderApprovalDTO workOrderApprovalDTO) {
        LOG.debug("Request to partially update WorkOrderApproval : {}", workOrderApprovalDTO);

        return workOrderApprovalRepository
            .findById(workOrderApprovalDTO.getId())
            .map(existingWorkOrderApproval -> {
                workOrderApprovalMapper.partialUpdate(existingWorkOrderApproval, workOrderApprovalDTO);

                return existingWorkOrderApproval;
            })
            .flatMap(workOrderApprovalRepository::save)
            .map(workOrderApprovalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderApprovalDTO> findAll() {
        LOG.debug("Request to get all WorkOrderApprovals");
        return workOrderApprovalRepository.findAll().map(workOrderApprovalMapper::toDto);
    }

    public Flux<WorkOrderApprovalDTO> findAllWithEagerRelationships() {
        return workOrderApprovalRepository.findAllWithEagerRelationships().map(workOrderApprovalMapper::toDto);
    }

    public Mono<Long> countAll() {
        return workOrderApprovalRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderApprovalDTO> findOne(Long id) {
        LOG.debug("Request to get WorkOrderApproval : {}", id);
        return workOrderApprovalRepository.findOneWithEagerRelationships(id).map(workOrderApprovalMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete WorkOrderApproval : {}", id);
        return workOrderApprovalRepository.deleteById(id);
    }
}
