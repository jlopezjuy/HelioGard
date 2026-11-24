package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.ClimateRiskProfileRepository;
import ar.com.heliguard.app.service.ClimateRiskProfileService;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import ar.com.heliguard.app.service.mapper.ClimateRiskProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.ClimateRiskProfile}.
 */
@Service
@Transactional
public class ClimateRiskProfileServiceImpl implements ClimateRiskProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(ClimateRiskProfileServiceImpl.class);

    private final ClimateRiskProfileRepository climateRiskProfileRepository;

    private final ClimateRiskProfileMapper climateRiskProfileMapper;

    public ClimateRiskProfileServiceImpl(
        ClimateRiskProfileRepository climateRiskProfileRepository,
        ClimateRiskProfileMapper climateRiskProfileMapper
    ) {
        this.climateRiskProfileRepository = climateRiskProfileRepository;
        this.climateRiskProfileMapper = climateRiskProfileMapper;
    }

    @Override
    public Mono<ClimateRiskProfileDTO> save(ClimateRiskProfileDTO climateRiskProfileDTO) {
        LOG.debug("Request to save ClimateRiskProfile : {}", climateRiskProfileDTO);
        return climateRiskProfileRepository
            .save(climateRiskProfileMapper.toEntity(climateRiskProfileDTO))
            .map(climateRiskProfileMapper::toDto);
    }

    @Override
    public Mono<ClimateRiskProfileDTO> update(ClimateRiskProfileDTO climateRiskProfileDTO) {
        LOG.debug("Request to update ClimateRiskProfile : {}", climateRiskProfileDTO);
        return climateRiskProfileRepository
            .save(climateRiskProfileMapper.toEntity(climateRiskProfileDTO))
            .map(climateRiskProfileMapper::toDto);
    }

    @Override
    public Mono<ClimateRiskProfileDTO> partialUpdate(ClimateRiskProfileDTO climateRiskProfileDTO) {
        LOG.debug("Request to partially update ClimateRiskProfile : {}", climateRiskProfileDTO);

        return climateRiskProfileRepository
            .findById(climateRiskProfileDTO.getId())
            .map(existingClimateRiskProfile -> {
                climateRiskProfileMapper.partialUpdate(existingClimateRiskProfile, climateRiskProfileDTO);

                return existingClimateRiskProfile;
            })
            .flatMap(climateRiskProfileRepository::save)
            .map(climateRiskProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClimateRiskProfileDTO> findAll() {
        LOG.debug("Request to get all ClimateRiskProfiles");
        return climateRiskProfileRepository.findAll().map(climateRiskProfileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return climateRiskProfileRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ClimateRiskProfileDTO> findOne(Long id) {
        LOG.debug("Request to get ClimateRiskProfile : {}", id);
        return climateRiskProfileRepository.findById(id).map(climateRiskProfileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ClimateRiskProfile : {}", id);
        return climateRiskProfileRepository.deleteById(id);
    }
}
