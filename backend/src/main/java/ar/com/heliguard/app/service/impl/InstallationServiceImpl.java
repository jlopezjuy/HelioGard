package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.service.InstallationService;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.mapper.InstallationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Installation}.
 */
@Service
@Transactional
public class InstallationServiceImpl implements InstallationService {

    private static final Logger LOG = LoggerFactory.getLogger(InstallationServiceImpl.class);

    private final InstallationRepository installationRepository;

    private final InstallationMapper installationMapper;

    public InstallationServiceImpl(InstallationRepository installationRepository, InstallationMapper installationMapper) {
        this.installationRepository = installationRepository;
        this.installationMapper = installationMapper;
    }

    @Override
    public Mono<InstallationDTO> save(InstallationDTO installationDTO) {
        LOG.debug("Request to save Installation : {}", installationDTO);
        return installationRepository.save(installationMapper.toEntity(installationDTO)).map(installationMapper::toDto);
    }

    @Override
    public Mono<InstallationDTO> update(InstallationDTO installationDTO) {
        LOG.debug("Request to update Installation : {}", installationDTO);
        return installationRepository.save(installationMapper.toEntity(installationDTO)).map(installationMapper::toDto);
    }

    @Override
    public Mono<InstallationDTO> partialUpdate(InstallationDTO installationDTO) {
        LOG.debug("Request to partially update Installation : {}", installationDTO);

        return installationRepository
            .findById(installationDTO.getId())
            .map(existingInstallation -> {
                installationMapper.partialUpdate(existingInstallation, installationDTO);

                return existingInstallation;
            })
            .flatMap(installationRepository::save)
            .map(installationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstallationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Installations");
        return installationRepository.findAllBy(pageable).map(installationMapper::toDto);
    }

    public Flux<InstallationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return installationRepository.findAllWithEagerRelationships(pageable).map(installationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return installationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstallationDTO> findOne(Long id) {
        LOG.debug("Request to get Installation : {}", id);
        return installationRepository.findOneWithEagerRelationships(id).map(installationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Installation : {}", id);
        return installationRepository.deleteById(id);
    }
}
