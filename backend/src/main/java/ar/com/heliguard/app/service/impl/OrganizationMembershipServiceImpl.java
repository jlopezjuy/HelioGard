package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.service.OrganizationMembershipService;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.mapper.OrganizationMembershipMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.OrganizationMembership}.
 */
@Service
@Transactional
public class OrganizationMembershipServiceImpl implements OrganizationMembershipService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationMembershipServiceImpl.class);

    private final OrganizationMembershipRepository organizationMembershipRepository;

    private final OrganizationMembershipMapper organizationMembershipMapper;

    public OrganizationMembershipServiceImpl(
        OrganizationMembershipRepository organizationMembershipRepository,
        OrganizationMembershipMapper organizationMembershipMapper
    ) {
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.organizationMembershipMapper = organizationMembershipMapper;
    }

    @Override
    public Mono<OrganizationMembershipDTO> save(OrganizationMembershipDTO organizationMembershipDTO) {
        LOG.debug("Request to save OrganizationMembership : {}", organizationMembershipDTO);
        return organizationMembershipRepository
            .save(organizationMembershipMapper.toEntity(organizationMembershipDTO))
            .map(organizationMembershipMapper::toDto);
    }

    @Override
    public Mono<OrganizationMembershipDTO> update(OrganizationMembershipDTO organizationMembershipDTO) {
        LOG.debug("Request to update OrganizationMembership : {}", organizationMembershipDTO);
        return organizationMembershipRepository
            .save(organizationMembershipMapper.toEntity(organizationMembershipDTO))
            .map(organizationMembershipMapper::toDto);
    }

    @Override
    public Mono<OrganizationMembershipDTO> partialUpdate(OrganizationMembershipDTO organizationMembershipDTO) {
        LOG.debug("Request to partially update OrganizationMembership : {}", organizationMembershipDTO);

        return organizationMembershipRepository
            .findById(organizationMembershipDTO.getId())
            .map(existingOrganizationMembership -> {
                organizationMembershipMapper.partialUpdate(existingOrganizationMembership, organizationMembershipDTO);

                return existingOrganizationMembership;
            })
            .flatMap(organizationMembershipRepository::save)
            .map(organizationMembershipMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrganizationMembershipDTO> findAll() {
        LOG.debug("Request to get all OrganizationMemberships");
        return organizationMembershipRepository.findAll().map(organizationMembershipMapper::toDto);
    }

    public Flux<OrganizationMembershipDTO> findAllWithEagerRelationships() {
        return organizationMembershipRepository.findAllWithEagerRelationships().map(organizationMembershipMapper::toDto);
    }

    public Mono<Long> countAll() {
        return organizationMembershipRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrganizationMembershipDTO> findOne(Long id) {
        LOG.debug("Request to get OrganizationMembership : {}", id);
        return organizationMembershipRepository.findOneWithEagerRelationships(id).map(organizationMembershipMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OrganizationMembership : {}", id);
        return organizationMembershipRepository.deleteById(id);
    }
}
