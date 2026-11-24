package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvent;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.SecurityCaseEventRepository;
import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityCaseEventService;
import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SecurityCaseEventMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link SecurityCaseEvent}.
 */
@Service
@Transactional
public class SecurityCaseEventServiceImpl implements SecurityCaseEventService {

    private final SecurityCaseEventRepository repository;
    private final SecurityIncidentRepository incidentRepository;
    private final SecurityCaseEventMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SecurityCaseEventServiceImpl(
        SecurityCaseEventRepository repository,
        SecurityIncidentRepository incidentRepository,
        SecurityCaseEventMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.incidentRepository = incidentRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SecurityCaseEventDTO> save(SecurityCaseEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkSecurityIncident(dto.getSecurityIncident() != null ? dto.getSecurityIncident().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            SecurityCaseEvent entity = mapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return repository.save(entity);
                        })
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEventDTO> update(SecurityCaseEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    SecurityCaseEvent entity = mapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return repository.save(entity);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<SecurityCaseEventDTO> partialUpdate(SecurityCaseEventDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    mapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return repository.save(existing);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityCaseEventDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityCaseEventDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityCaseEventDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(e -> repository.deleteById(e.getId()));
    }

    private Mono<Void> checkSecurityIncident(Long secIncidentId, Long orgId) {
        if (secIncidentId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SECURITY_INCIDENT_REQUIRED", "securityIncident.id is required"));
        }
        return incidentRepository
            .findById(secIncidentId)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.BAD_REQUEST, "SECURITY_INCIDENT_NOT_IN_TENANT", "SecurityIncident not found in tenant")
                )
            )
            .then();
    }

    private Mono<SecurityCaseEvent> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SECURITY_CASE_EVENT_NOT_FOUND", "SecurityCaseEvent not found"))
            );
    }
}
