package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.AlertRuleVersionService;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AlertRuleVersionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AlertRuleVersion} enforcing immutability once referenced by an Incident (RB-INC-001).
 */
@Service
@Transactional
public class AlertRuleVersionServiceImpl implements AlertRuleVersionService {

    private final AlertRuleVersionRepository repository;
    private final AlertRuleRepository alertRuleRepository;
    private final IncidentRepository incidentRepository;
    private final AlertRuleVersionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AlertRuleVersionServiceImpl(
        AlertRuleVersionRepository repository,
        AlertRuleRepository alertRuleRepository,
        IncidentRepository incidentRepository,
        AlertRuleVersionMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.alertRuleRepository = alertRuleRepository;
        this.incidentRepository = incidentRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<AlertRuleVersionDTO> save(AlertRuleVersionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getValidFrom(), dto.getValidUntil())
                    .then(checkRuleOwnership(dto.getAlertRule() != null ? dto.getAlertRule().getId() : null, tenant.organizationId()))
                    .then(
                        Mono.defer(() -> {
                            AlertRuleVersion entity = mapper.toEntity(dto);
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
    public Mono<AlertRuleVersionDTO> update(AlertRuleVersionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing.getId()))
                            .then(permissionService.validateTemporalWindow(dto.getValidFrom(), dto.getValidUntil()))
                            .then(checkRuleOwnership(dto.getAlertRule() != null ? dto.getAlertRule().getId() : null, tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    AlertRuleVersion entity = mapper.toEntity(dto);
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
    public Mono<AlertRuleVersionDTO> partialUpdate(AlertRuleVersionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing.getId()))
                            .then(
                                Mono.defer(() -> {
                                    mapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return permissionService
                                        .validateTemporalWindow(existing.getValidFrom(), existing.getValidUntil())
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AlertRuleVersionDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AlertRuleVersionDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AlertRuleVersionDTO> findOne(Long id) {
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
            .flatMap(v -> assertMutable(v.getId()).then(repository.deleteById(v.getId())));
    }

    private Mono<Void> assertMutable(Long versionId) {
        return incidentRepository
            .findByAlertRuleVersion(versionId)
            .hasElements()
            .flatMap(used ->
                used
                    ? Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "RULE_VERSION_IMMUTABLE",
                            "AlertRuleVersion is immutable once referenced by an Incident"
                        )
                    )
                    : Mono.empty()
            );
    }

    private Mono<Void> checkRuleOwnership(Long alertRuleId, Long orgId) {
        if (alertRuleId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "ALERT_RULE_REQUIRED", "alertRule.id is required"));
        }
        return alertRuleRepository
            .findById(alertRuleId)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "ALERT_RULE_NOT_IN_TENANT", "AlertRule not found in tenant")))
            .then();
    }

    private Mono<AlertRuleVersion> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(v -> orgId.equals(v.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ALERT_RULE_VERSION_NOT_FOUND", "AlertRuleVersion not found"))
            );
    }
}
