package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.domain.enumeration.SecurityCaseStatus;
import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityIncidentService;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SecurityIncidentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.SecurityIncident}
 * enforcing tenant isolation and explicit status transition rules (RB-SEC-004).
 */
@Service
@Transactional
public class SecurityIncidentServiceImpl implements SecurityIncidentService {

    private static final Map<SecurityCaseStatus, Set<SecurityCaseStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(
        SecurityCaseStatus.class
    );

    static {
        ALLOWED_TRANSITIONS.put(SecurityCaseStatus.OPEN, EnumSet.of(SecurityCaseStatus.OPEN, SecurityCaseStatus.TRIAGE));
        ALLOWED_TRANSITIONS.put(
            SecurityCaseStatus.TRIAGE,
            EnumSet.of(SecurityCaseStatus.TRIAGE, SecurityCaseStatus.INVESTIGATING, SecurityCaseStatus.CLOSED)
        );
        ALLOWED_TRANSITIONS.put(
            SecurityCaseStatus.INVESTIGATING,
            EnumSet.of(SecurityCaseStatus.INVESTIGATING, SecurityCaseStatus.MITIGATED, SecurityCaseStatus.CLOSED)
        );
        ALLOWED_TRANSITIONS.put(
            SecurityCaseStatus.MITIGATED,
            EnumSet.of(SecurityCaseStatus.MITIGATED, SecurityCaseStatus.CLOSED, SecurityCaseStatus.INVESTIGATING)
        );
        ALLOWED_TRANSITIONS.put(SecurityCaseStatus.CLOSED, EnumSet.of(SecurityCaseStatus.CLOSED));
    }

    private final SecurityIncidentRepository incidentRepository;
    private final SecurityIncidentMapper incidentMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SecurityIncidentServiceImpl(
        SecurityIncidentRepository incidentRepository,
        SecurityIncidentMapper incidentMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SecurityIncidentDTO> save(SecurityIncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        SecurityIncident entity = incidentMapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        entity.setStatus(SecurityCaseStatus.OPEN);
                        if (entity.getOpenedAt() == null) {
                            entity.setOpenedAt(Instant.now());
                        }
                        return incidentRepository.save(entity);
                    })
            )
            .map(incidentMapper::toDto);
    }

    @Override
    public Mono<SecurityIncidentDTO> update(SecurityIncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(validateTransition(existing.getStatus(), dto.getStatus()))
                            .then(
                                Mono.defer(() -> {
                                    SecurityIncident entity = incidentMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    if (entity.getStatus() == SecurityCaseStatus.CLOSED && entity.getClosedAt() == null) {
                                        entity.setClosedAt(Instant.now());
                                    }
                                    return incidentRepository.save(entity);
                                })
                            )
                    )
            )
            .map(incidentMapper::toDto);
    }

    @Override
    public Mono<SecurityIncidentDTO> partialUpdate(SecurityIncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getStatus() != null ? validateTransition(existing.getStatus(), dto.getStatus()) : Mono.empty()
                            )
                            .then(
                                Mono.defer(() -> {
                                    incidentMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    if (existing.getStatus() == SecurityCaseStatus.CLOSED && existing.getClosedAt() == null) {
                                        existing.setClosedAt(Instant.now());
                                    }
                                    return incidentRepository.save(existing);
                                })
                            )
                    )
            )
            .map(incidentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityIncidentDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> incidentRepository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(incidentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityIncidentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> incidentRepository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityIncidentDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(incidentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(i -> incidentRepository.deleteById(i.getId()));
    }

    private Mono<Void> validateTransition(SecurityCaseStatus from, SecurityCaseStatus to) {
        if (to == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "STATUS_REQUIRED", "status is required"));
        }
        Set<SecurityCaseStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(SecurityCaseStatus.class));
        if (!allowed.contains(to)) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "INVALID_INCIDENT_STATUS_TRANSITION",
                    "Transition from " + from + " to " + to + " is not allowed"
                )
            );
        }
        return Mono.empty();
    }

    private Mono<SecurityIncident> findOwned(Long id, Long orgId) {
        return incidentRepository
            .findById(id)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SECURITY_INCIDENT_NOT_FOUND", "SecurityIncident not found")));
    }
}
