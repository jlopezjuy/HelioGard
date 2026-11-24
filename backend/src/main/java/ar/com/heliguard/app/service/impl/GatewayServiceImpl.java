package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.service.GatewayService;
import ar.com.heliguard.app.service.dto.GatewayDTO;
import ar.com.heliguard.app.service.mapper.GatewayMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Gateway}.
 */
@Service
@Transactional
public class GatewayServiceImpl implements GatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayServiceImpl.class);

    private final GatewayRepository gatewayRepository;

    private final GatewayMapper gatewayMapper;

    public GatewayServiceImpl(GatewayRepository gatewayRepository, GatewayMapper gatewayMapper) {
        this.gatewayRepository = gatewayRepository;
        this.gatewayMapper = gatewayMapper;
    }

    @Override
    public Mono<GatewayDTO> save(GatewayDTO gatewayDTO) {
        LOG.debug("Request to save Gateway : {}", gatewayDTO);
        return gatewayRepository.save(gatewayMapper.toEntity(gatewayDTO)).map(gatewayMapper::toDto);
    }

    @Override
    public Mono<GatewayDTO> update(GatewayDTO gatewayDTO) {
        LOG.debug("Request to update Gateway : {}", gatewayDTO);
        return gatewayRepository.save(gatewayMapper.toEntity(gatewayDTO)).map(gatewayMapper::toDto);
    }

    @Override
    public Mono<GatewayDTO> partialUpdate(GatewayDTO gatewayDTO) {
        LOG.debug("Request to partially update Gateway : {}", gatewayDTO);

        return gatewayRepository
            .findById(gatewayDTO.getId())
            .map(existingGateway -> {
                gatewayMapper.partialUpdate(existingGateway, gatewayDTO);

                return existingGateway;
            })
            .flatMap(gatewayRepository::save)
            .map(gatewayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GatewayDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Gateways");
        return gatewayRepository.findAllBy(pageable).map(gatewayMapper::toDto);
    }

    public Flux<GatewayDTO> findAllWithEagerRelationships(Pageable pageable) {
        return gatewayRepository.findAllWithEagerRelationships(pageable).map(gatewayMapper::toDto);
    }

    public Mono<Long> countAll() {
        return gatewayRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<GatewayDTO> findOne(Long id) {
        LOG.debug("Request to get Gateway : {}", id);
        return gatewayRepository.findOneWithEagerRelationships(id).map(gatewayMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Gateway : {}", id);
        return gatewayRepository.deleteById(id);
    }
}
