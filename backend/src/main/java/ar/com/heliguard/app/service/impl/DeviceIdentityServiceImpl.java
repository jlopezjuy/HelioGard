package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.DeviceIdentityRepository;
import ar.com.heliguard.app.service.DeviceIdentityService;
import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import ar.com.heliguard.app.service.mapper.DeviceIdentityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.DeviceIdentity}.
 */
@Service
@Transactional
public class DeviceIdentityServiceImpl implements DeviceIdentityService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceIdentityServiceImpl.class);

    private final DeviceIdentityRepository deviceIdentityRepository;

    private final DeviceIdentityMapper deviceIdentityMapper;

    public DeviceIdentityServiceImpl(DeviceIdentityRepository deviceIdentityRepository, DeviceIdentityMapper deviceIdentityMapper) {
        this.deviceIdentityRepository = deviceIdentityRepository;
        this.deviceIdentityMapper = deviceIdentityMapper;
    }

    @Override
    public Mono<DeviceIdentityDTO> save(DeviceIdentityDTO deviceIdentityDTO) {
        LOG.debug("Request to save DeviceIdentity : {}", deviceIdentityDTO);
        return deviceIdentityRepository.save(deviceIdentityMapper.toEntity(deviceIdentityDTO)).map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<DeviceIdentityDTO> update(DeviceIdentityDTO deviceIdentityDTO) {
        LOG.debug("Request to update DeviceIdentity : {}", deviceIdentityDTO);
        return deviceIdentityRepository.save(deviceIdentityMapper.toEntity(deviceIdentityDTO)).map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<DeviceIdentityDTO> partialUpdate(DeviceIdentityDTO deviceIdentityDTO) {
        LOG.debug("Request to partially update DeviceIdentity : {}", deviceIdentityDTO);

        return deviceIdentityRepository
            .findById(deviceIdentityDTO.getId())
            .map(existingDeviceIdentity -> {
                deviceIdentityMapper.partialUpdate(existingDeviceIdentity, deviceIdentityDTO);

                return existingDeviceIdentity;
            })
            .flatMap(deviceIdentityRepository::save)
            .map(deviceIdentityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DeviceIdentityDTO> findAll() {
        LOG.debug("Request to get all DeviceIdentities");
        return deviceIdentityRepository.findAll().map(deviceIdentityMapper::toDto);
    }

    public Flux<DeviceIdentityDTO> findAllWithEagerRelationships() {
        return deviceIdentityRepository.findAllWithEagerRelationships().map(deviceIdentityMapper::toDto);
    }

    public Mono<Long> countAll() {
        return deviceIdentityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DeviceIdentityDTO> findOne(Long id) {
        LOG.debug("Request to get DeviceIdentity : {}", id);
        return deviceIdentityRepository.findOneWithEagerRelationships(id).map(deviceIdentityMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DeviceIdentity : {}", id);
        return deviceIdentityRepository.deleteById(id);
    }
}
