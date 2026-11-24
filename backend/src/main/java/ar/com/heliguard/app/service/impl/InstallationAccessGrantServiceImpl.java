package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.service.InstallationAccessGrantService;
import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
import ar.com.heliguard.app.service.mapper.InstallationAccessGrantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.InstallationAccessGrant}.
 */
@Service
@Transactional
public class InstallationAccessGrantServiceImpl implements InstallationAccessGrantService {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationAccessGrantServiceImpl.class);

    private final InstallationAccessGrantRepository installationAccessGrantRepository;

    private final InstallationAccessGrantMapper installationAccessGrantMapper;

    public InstallationAccessGrantServiceImpl(
        InstallationAccessGrantRepository installationAccessGrantRepository,
        InstallationAccessGrantMapper installationAccessGrantMapper
    ) {
        this.installationAccessGrantRepository = installationAccessGrantRepository;
        this.installationAccessGrantMapper = installationAccessGrantMapper;
    }

    @Override
    public Mono<InstallationAccessGrantDTO> save(InstallationAccessGrantDTO installationAccessGrantDTO) {
        LOG.debug("Request to save InstallationAccessGrant : {}", installationAccessGrantDTO);
        return installationAccessGrantRepository
            .save(installationAccessGrantMapper.toEntity(installationAccessGrantDTO))
            .map(installationAccessGrantMapper::toDto);
    }

    @Override
    public Mono<InstallationAccessGrantDTO> update(InstallationAccessGrantDTO installationAccessGrantDTO) {
        LOG.debug("Request to update InstallationAccessGrant : {}", installationAccessGrantDTO);
        return installationAccessGrantRepository
            .save(installationAccessGrantMapper.toEntity(installationAccessGrantDTO))
            .map(installationAccessGrantMapper::toDto);
    }

    @Override
    public Mono<InstallationAccessGrantDTO> partialUpdate(InstallationAccessGrantDTO installationAccessGrantDTO) {
        LOG.debug("Request to partially update InstallationAccessGrant : {}", installationAccessGrantDTO);

        return installationAccessGrantRepository
            .findById(installationAccessGrantDTO.getId())
            .map(existingInstallationAccessGrant -> {
                installationAccessGrantMapper.partialUpdate(existingInstallationAccessGrant, installationAccessGrantDTO);

                return existingInstallationAccessGrant;
            })
            .flatMap(installationAccessGrantRepository::save)
            .map(installationAccessGrantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationAccessGrantDTO> findAll() {
        LOG.debug("Request to get all InstallationAccessGrants");
        return installationAccessGrantRepository.findAll().map(installationAccessGrantMapper::toDto);
    }

    public Flux<InstallationAccessGrantDTO> findAllWithEagerRelationships() {
        return installationAccessGrantRepository.findAllWithEagerRelationships().map(installationAccessGrantMapper::toDto);
    }

    public Mono<Long> countAll() {
        return installationAccessGrantRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstallationAccessGrantDTO> findOne(Long id) {
        LOG.debug("Request to get InstallationAccessGrant : {}", id);
        return installationAccessGrantRepository.findOneWithEagerRelationships(id).map(installationAccessGrantMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InstallationAccessGrant : {}", id);
        return installationAccessGrantRepository.deleteById(id);
    }
}
