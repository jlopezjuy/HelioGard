package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.ServiceAgreementRepository;
import ar.com.heliguard.app.service.ServiceAgreementService;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
import ar.com.heliguard.app.service.mapper.ServiceAgreementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.ServiceAgreement}.
 */
@Service
@Transactional
public class ServiceAgreementServiceImpl implements ServiceAgreementService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceAgreementServiceImpl.class);

    private final ServiceAgreementRepository serviceAgreementRepository;

    private final ServiceAgreementMapper serviceAgreementMapper;

    public ServiceAgreementServiceImpl(
        ServiceAgreementRepository serviceAgreementRepository,
        ServiceAgreementMapper serviceAgreementMapper
    ) {
        this.serviceAgreementRepository = serviceAgreementRepository;
        this.serviceAgreementMapper = serviceAgreementMapper;
    }

    @Override
    public Mono<ServiceAgreementDTO> save(ServiceAgreementDTO serviceAgreementDTO) {
        LOG.debug("Request to save ServiceAgreement : {}", serviceAgreementDTO);
        return serviceAgreementRepository.save(serviceAgreementMapper.toEntity(serviceAgreementDTO)).map(serviceAgreementMapper::toDto);
    }

    @Override
    public Mono<ServiceAgreementDTO> update(ServiceAgreementDTO serviceAgreementDTO) {
        LOG.debug("Request to update ServiceAgreement : {}", serviceAgreementDTO);
        return serviceAgreementRepository.save(serviceAgreementMapper.toEntity(serviceAgreementDTO)).map(serviceAgreementMapper::toDto);
    }

    @Override
    public Mono<ServiceAgreementDTO> partialUpdate(ServiceAgreementDTO serviceAgreementDTO) {
        LOG.debug("Request to partially update ServiceAgreement : {}", serviceAgreementDTO);

        return serviceAgreementRepository
            .findById(serviceAgreementDTO.getId())
            .map(existingServiceAgreement -> {
                serviceAgreementMapper.partialUpdate(existingServiceAgreement, serviceAgreementDTO);

                return existingServiceAgreement;
            })
            .flatMap(serviceAgreementRepository::save)
            .map(serviceAgreementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ServiceAgreementDTO> findAll() {
        LOG.debug("Request to get all ServiceAgreements");
        return serviceAgreementRepository.findAll().map(serviceAgreementMapper::toDto);
    }

    public Flux<ServiceAgreementDTO> findAllWithEagerRelationships() {
        return serviceAgreementRepository.findAllWithEagerRelationships().map(serviceAgreementMapper::toDto);
    }

    public Mono<Long> countAll() {
        return serviceAgreementRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ServiceAgreementDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceAgreement : {}", id);
        return serviceAgreementRepository.findOneWithEagerRelationships(id).map(serviceAgreementMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ServiceAgreement : {}", id);
        return serviceAgreementRepository.deleteById(id);
    }
}
