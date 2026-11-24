package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.service.SiteService;
import ar.com.heliguard.app.service.dto.SiteDTO;
import ar.com.heliguard.app.service.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.Site}.
 */
@Service
@Transactional
public class SiteServiceImpl implements SiteService {

    private static final Logger LOG = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;

    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
    }

    @Override
    public Mono<SiteDTO> save(SiteDTO siteDTO) {
        LOG.debug("Request to save Site : {}", siteDTO);
        return siteRepository.save(siteMapper.toEntity(siteDTO)).map(siteMapper::toDto);
    }

    @Override
    public Mono<SiteDTO> update(SiteDTO siteDTO) {
        LOG.debug("Request to update Site : {}", siteDTO);
        return siteRepository.save(siteMapper.toEntity(siteDTO)).map(siteMapper::toDto);
    }

    @Override
    public Mono<SiteDTO> partialUpdate(SiteDTO siteDTO) {
        LOG.debug("Request to partially update Site : {}", siteDTO);

        return siteRepository
            .findById(siteDTO.getId())
            .map(existingSite -> {
                siteMapper.partialUpdate(existingSite, siteDTO);

                return existingSite;
            })
            .flatMap(siteRepository::save)
            .map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SiteDTO> findAll() {
        LOG.debug("Request to get all Sites");
        return siteRepository.findAll().map(siteMapper::toDto);
    }

    public Flux<SiteDTO> findAllWithEagerRelationships() {
        return siteRepository.findAllWithEagerRelationships().map(siteMapper::toDto);
    }

    public Mono<Long> countAll() {
        return siteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SiteDTO> findOne(Long id) {
        LOG.debug("Request to get Site : {}", id);
        return siteRepository.findOneWithEagerRelationships(id).map(siteMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Site : {}", id);
        return siteRepository.deleteById(id);
    }
}
