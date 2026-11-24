package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.IncidentService;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.IncidentMapper;
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
 * Tenant-isolated service for {@link Incident} enforcing status lifecycle (RB-INC-003).
 */
@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private static final Map<IncidentStatus, Set<IncidentStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(IncidentStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(IncidentStatus.OPEN, EnumSet.of(IncidentStatus.OPEN, IncidentStatus.ACKNOWLEDGED, IncidentStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(
            IncidentStatus.ACKNOWLEDGED,
            EnumSet.of(IncidentStatus.ACKNOWLEDGED, IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED)
        );
        ALLOWED_TRANSITIONS.put(
            IncidentStatus.IN_PROGRESS,
            EnumSet.of(IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED, IncidentStatus.CLOSED)
        );
        ALLOWED_TRANSITIONS.put(IncidentStatus.RESOLVED, EnumSet.of(IncidentStatus.RESOLVED, IncidentStatus.CLOSED, IncidentStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(IncidentStatus.CLOSED, EnumSet.of(IncidentStatus.CLOSED));
    }

    private final IncidentRepository repository;
    private final IncidentMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public IncidentServiceImpl(
        IncidentRepository repository,
        IncidentMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<IncidentDTO> save(IncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        Instant now = Instant.now();
                        Incident entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        entity.setStatus(IncidentStatus.OPEN);
                        if (entity.getFirstSeenAt() == null) {
                            entity.setFirstSeenAt(now);
                        }
                        if (entity.getLastSeenAt() == null) {
                            entity.setLastSeenAt(now);
                        }
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<IncidentDTO> update(IncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(validateTransition(existing.getStatus(), dto.getStatus()))
                            .then(
                                Mono.defer(() -> {
                                    Incident entity = mapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    stampStatusDates(entity);
                                    return repository.save(entity);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<IncidentDTO> partialUpdate(IncidentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(dto.getStatus() != null ? validateTransition(existing.getStatus(), dto.getStatus()) : Mono.empty())
                            .then(
                                Mono.defer(() -> {
                                    mapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    stampStatusDates(existing);
                                    return repository.save(existing);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().flatMap(tenant -> repository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IncidentDTO> findOne(Long id) {
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
            .flatMap(i -> repository.deleteById(i.getId()));
    }

    private Mono<Void> validateTransition(IncidentStatus from, IncidentStatus to) {
        if (to == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "STATUS_REQUIRED", "status is required"));
        }
        Set<IncidentStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(IncidentStatus.class));
        if (!allowed.contains(to)) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "INVALID_INCIDENT_TRANSITION",
                    "Transition from " + from + " to " + to + " is not allowed"
                )
            );
        }
        return Mono.empty();
    }

    private static void stampStatusDates(Incident entity) {
        Instant now = Instant.now();
        if (entity.getStatus() == IncidentStatus.ACKNOWLEDGED && entity.getAcknowledgedAt() == null) {
            entity.setAcknowledgedAt(now);
        }
        if ((entity.getStatus() == IncidentStatus.RESOLVED || entity.getStatus() == IncidentStatus.CLOSED) && entity.getResolvedAt() == null) {
            entity.setResolvedAt(now);
        }
    }

    private Mono<Incident> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INCIDENT_NOT_FOUND", "Incident not found")));
    }
}
