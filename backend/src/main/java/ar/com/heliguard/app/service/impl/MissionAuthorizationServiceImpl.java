package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.service.MissionAuthorizationService;
import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
import ar.com.heliguard.app.service.mapper.MissionAuthorizationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MissionAuthorization}.
 */
@Service
@Transactional
public class MissionAuthorizationServiceImpl implements MissionAuthorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(MissionAuthorizationServiceImpl.class);

    private final MissionAuthorizationRepository missionAuthorizationRepository;

    private final MissionAuthorizationMapper missionAuthorizationMapper;

    public MissionAuthorizationServiceImpl(
        MissionAuthorizationRepository missionAuthorizationRepository,
        MissionAuthorizationMapper missionAuthorizationMapper
    ) {
        this.missionAuthorizationRepository = missionAuthorizationRepository;
        this.missionAuthorizationMapper = missionAuthorizationMapper;
    }

    @Override
    public Mono<MissionAuthorizationDTO> save(MissionAuthorizationDTO missionAuthorizationDTO) {
        LOG.debug("Request to save MissionAuthorization : {}", missionAuthorizationDTO);
        return missionAuthorizationRepository
            .save(missionAuthorizationMapper.toEntity(missionAuthorizationDTO))
            .map(missionAuthorizationMapper::toDto);
    }

    @Override
    public Mono<MissionAuthorizationDTO> update(MissionAuthorizationDTO missionAuthorizationDTO) {
        LOG.debug("Request to update MissionAuthorization : {}", missionAuthorizationDTO);
        return missionAuthorizationRepository
            .save(missionAuthorizationMapper.toEntity(missionAuthorizationDTO))
            .map(missionAuthorizationMapper::toDto);
    }

    @Override
    public Mono<MissionAuthorizationDTO> partialUpdate(MissionAuthorizationDTO missionAuthorizationDTO) {
        LOG.debug("Request to partially update MissionAuthorization : {}", missionAuthorizationDTO);

        return missionAuthorizationRepository
            .findById(missionAuthorizationDTO.getId())
            .map(existingMissionAuthorization -> {
                missionAuthorizationMapper.partialUpdate(existingMissionAuthorization, missionAuthorizationDTO);

                return existingMissionAuthorization;
            })
            .flatMap(missionAuthorizationRepository::save)
            .map(missionAuthorizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MissionAuthorizationDTO> findAll() {
        LOG.debug("Request to get all MissionAuthorizations");
        return missionAuthorizationRepository.findAll().map(missionAuthorizationMapper::toDto);
    }

    public Flux<MissionAuthorizationDTO> findAllWithEagerRelationships() {
        return missionAuthorizationRepository.findAllWithEagerRelationships().map(missionAuthorizationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return missionAuthorizationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MissionAuthorizationDTO> findOne(Long id) {
        LOG.debug("Request to get MissionAuthorization : {}", id);
        return missionAuthorizationRepository.findOneWithEagerRelationships(id).map(missionAuthorizationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MissionAuthorization : {}", id);
        return missionAuthorizationRepository.deleteById(id);
    }
}
