package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.TelemetryIntegrityEventService;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.TelemetryIntegrityEventMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.TelemetryIntegrityEvent}
 * with tenant isolation.
 */
@Service
@Transactional
public class TelemetryIntegrityEventServiceImpl implements TelemetryIntegrityEventService {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryIntegrityEventServiceImpl.class);

    private final TelemetryIntegrityEventRepository eventRepository;
    private final TelemetryIntegrityEventMapper eventMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public TelemetryIntegrityEventServiceImpl(
        TelemetryIntegrityEventRepository eventRepository,
        TelemetryIntegrityEventMapper eventMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> save(TelemetryIntegrityEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        TelemetryIntegrityEvent entity = eventMapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return eventRepository.save(entity);
                    })
            )
            .map(eventMapper::toDto);
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> update(TelemetryIntegrityEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    TelemetryIntegrityEvent entity = eventMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return eventRepository.save(entity);
                                })
                            )
                    )
            )
            .map(eventMapper::toDto);
    }

    @Override
    public Mono<TelemetryIntegrityEventDTO> partialUpdate(TelemetryIntegrityEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    eventMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return eventRepository.save(existing);
                                })
                            )
                    )
            )
            .map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryIntegrityEventDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> eventRepository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TelemetryIntegrityEventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> eventRepository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TelemetryIntegrityEventDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(eventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(e -> eventRepository.deleteById(e.getId()));
    }

    private Mono<TelemetryIntegrityEvent> findOwned(Long id, Long orgId) {
        return eventRepository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.NOT_FOUND, "INTEGRITY_EVENT_NOT_FOUND", "TelemetryIntegrityEvent not found")
                )
            );
    }
}
