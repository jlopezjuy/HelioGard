package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderService;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.WorkOrder}.
 */
@Service
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    private final WorkOrderRepository workOrderRepository;

    private final WorkOrderMapper workOrderMapper;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository, WorkOrderMapper workOrderMapper) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderMapper = workOrderMapper;
    }

    @Override
    public Mono<WorkOrderDTO> save(WorkOrderDTO workOrderDTO) {
        LOG.debug("Request to save WorkOrder : {}", workOrderDTO);
        return workOrderRepository.save(workOrderMapper.toEntity(workOrderDTO)).map(workOrderMapper::toDto);
    }

    @Override
    public Mono<WorkOrderDTO> update(WorkOrderDTO workOrderDTO) {
        LOG.debug("Request to update WorkOrder : {}", workOrderDTO);
        return workOrderRepository.save(workOrderMapper.toEntity(workOrderDTO)).map(workOrderMapper::toDto);
    }

    @Override
    public Mono<WorkOrderDTO> partialUpdate(WorkOrderDTO workOrderDTO) {
        LOG.debug("Request to partially update WorkOrder : {}", workOrderDTO);

        return workOrderRepository
            .findById(workOrderDTO.getId())
            .map(existingWorkOrder -> {
                workOrderMapper.partialUpdate(existingWorkOrder, workOrderDTO);

                return existingWorkOrder;
            })
            .flatMap(workOrderRepository::save)
            .map(workOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WorkOrders");
        return workOrderRepository.findAllBy(pageable).map(workOrderMapper::toDto);
    }

    public Flux<WorkOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return workOrderRepository.findAllWithEagerRelationships(pageable).map(workOrderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return workOrderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkOrderDTO> findOne(Long id) {
        LOG.debug("Request to get WorkOrder : {}", id);
        return workOrderRepository.findOneWithEagerRelationships(id).map(workOrderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete WorkOrder : {}", id);
        return workOrderRepository.deleteById(id);
    }
}
