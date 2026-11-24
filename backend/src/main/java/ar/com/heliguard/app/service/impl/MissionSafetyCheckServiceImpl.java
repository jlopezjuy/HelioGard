package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.MissionSafetyCheckRepository;
import ar.com.heliguard.app.service.MissionSafetyCheckService;
import ar.com.heliguard.app.service.dto.MissionSafetyCheckDTO;
import ar.com.heliguard.app.service.mapper.MissionSafetyCheckMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.MissionSafetyCheck}.
 */
@Service
@Transactional
public class MissionSafetyCheckServiceImpl implements MissionSafetyCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(MissionSafetyCheckServiceImpl.class);

    private final MissionSafetyCheckRepository missionSafetyCheckRepository;

    private final MissionSafetyCheckMapper missionSafetyCheckMapper;

    public MissionSafetyCheckServiceImpl(
        MissionSafetyCheckRepository missionSafetyCheckRepository,
        MissionSafetyCheckMapper missionSafetyCheckMapper
    ) {
        this.missionSafetyCheckRepository = missionSafetyCheckRepository;
        this.missionSafetyCheckMapper = missionSafetyCheckMapper;
    }

    @Override
    public Mono<MissionSafetyCheckDTO> save(MissionSafetyCheckDTO missionSafetyCheckDTO) {
        LOG.debug("Request to save MissionSafetyCheck : {}", missionSafetyCheckDTO);
        return missionSafetyCheckRepository
            .save(missionSafetyCheckMapper.toEntity(missionSafetyCheckDTO))
            .map(missionSafetyCheckMapper::toDto);
    }

    @Override
    public Mono<MissionSafetyCheckDTO> update(MissionSafetyCheckDTO missionSafetyCheckDTO) {
        LOG.debug("Request to update MissionSafetyCheck : {}", missionSafetyCheckDTO);
        return missionSafetyCheckRepository
            .save(missionSafetyCheckMapper.toEntity(missionSafetyCheckDTO))
            .map(missionSafetyCheckMapper::toDto);
    }

    @Override
    public Mono<MissionSafetyCheckDTO> partialUpdate(MissionSafetyCheckDTO missionSafetyCheckDTO) {
        LOG.debug("Request to partially update MissionSafetyCheck : {}", missionSafetyCheckDTO);

        return missionSafetyCheckRepository
            .findById(missionSafetyCheckDTO.getId())
            .map(existingMissionSafetyCheck -> {
                missionSafetyCheckMapper.partialUpdate(existingMissionSafetyCheck, missionSafetyCheckDTO);

                return existingMissionSafetyCheck;
            })
            .flatMap(missionSafetyCheckRepository::save)
            .map(missionSafetyCheckMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MissionSafetyCheckDTO> findAll() {
        LOG.debug("Request to get all MissionSafetyChecks");
        return missionSafetyCheckRepository.findAll().map(missionSafetyCheckMapper::toDto);
    }

    public Flux<MissionSafetyCheckDTO> findAllWithEagerRelationships() {
        return missionSafetyCheckRepository.findAllWithEagerRelationships().map(missionSafetyCheckMapper::toDto);
    }

    public Mono<Long> countAll() {
        return missionSafetyCheckRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MissionSafetyCheckDTO> findOne(Long id) {
        LOG.debug("Request to get MissionSafetyCheck : {}", id);
        return missionSafetyCheckRepository.findOneWithEagerRelationships(id).map(missionSafetyCheckMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete MissionSafetyCheck : {}", id);
        return missionSafetyCheckRepository.deleteById(id);
    }
}
