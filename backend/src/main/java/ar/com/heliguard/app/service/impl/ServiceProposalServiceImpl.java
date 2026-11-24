package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.ServiceProposalRepository;
import ar.com.heliguard.app.service.ServiceProposalService;
import ar.com.heliguard.app.service.dto.ServiceProposalDTO;
import ar.com.heliguard.app.service.mapper.ServiceProposalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.ServiceProposal}.
 */
@Service
@Transactional
public class ServiceProposalServiceImpl implements ServiceProposalService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceProposalServiceImpl.class);

    private final ServiceProposalRepository serviceProposalRepository;

    private final ServiceProposalMapper serviceProposalMapper;

    public ServiceProposalServiceImpl(ServiceProposalRepository serviceProposalRepository, ServiceProposalMapper serviceProposalMapper) {
        this.serviceProposalRepository = serviceProposalRepository;
        this.serviceProposalMapper = serviceProposalMapper;
    }

    @Override
    public Mono<ServiceProposalDTO> save(ServiceProposalDTO serviceProposalDTO) {
        LOG.debug("Request to save ServiceProposal : {}", serviceProposalDTO);
        return serviceProposalRepository.save(serviceProposalMapper.toEntity(serviceProposalDTO)).map(serviceProposalMapper::toDto);
    }

    @Override
    public Mono<ServiceProposalDTO> update(ServiceProposalDTO serviceProposalDTO) {
        LOG.debug("Request to update ServiceProposal : {}", serviceProposalDTO);
        return serviceProposalRepository.save(serviceProposalMapper.toEntity(serviceProposalDTO)).map(serviceProposalMapper::toDto);
    }

    @Override
    public Mono<ServiceProposalDTO> partialUpdate(ServiceProposalDTO serviceProposalDTO) {
        LOG.debug("Request to partially update ServiceProposal : {}", serviceProposalDTO);

        return serviceProposalRepository
            .findById(serviceProposalDTO.getId())
            .map(existingServiceProposal -> {
                serviceProposalMapper.partialUpdate(existingServiceProposal, serviceProposalDTO);

                return existingServiceProposal;
            })
            .flatMap(serviceProposalRepository::save)
            .map(serviceProposalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ServiceProposalDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ServiceProposals");
        return serviceProposalRepository.findAllBy(pageable).map(serviceProposalMapper::toDto);
    }

    public Flux<ServiceProposalDTO> findAllWithEagerRelationships(Pageable pageable) {
        return serviceProposalRepository.findAllWithEagerRelationships(pageable).map(serviceProposalMapper::toDto);
    }

    public Mono<Long> countAll() {
        return serviceProposalRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ServiceProposalDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceProposal : {}", id);
        return serviceProposalRepository.findOneWithEagerRelationships(id).map(serviceProposalMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ServiceProposal : {}", id);
        return serviceProposalRepository.deleteById(id);
    }
}
