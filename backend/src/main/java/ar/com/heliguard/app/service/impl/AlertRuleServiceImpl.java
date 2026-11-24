package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.service.AlertRuleService;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AlertRuleMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AlertRule}.
 */
@Service
@Transactional
public class AlertRuleServiceImpl implements AlertRuleService {

    private final AlertRuleRepository repository;
    private final AlertRuleMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AlertRuleServiceImpl(
        AlertRuleRepository repository,
        AlertRuleMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<AlertRuleDTO> save(AlertRuleDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                (dto.getInstallation() != null && dto.getInstallation().getId() != null
                    ? permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()).then()
                    : Mono.empty())
                    .then(
                        Mono.defer(() -> {
                            AlertRule entity = mapper.toEntity(dto);
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
    public Mono<AlertRuleDTO> update(AlertRuleDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getInstallation() != null && dto.getInstallation().getId() != null
                                    ? permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId()).then()
                                    : Mono.empty()
                            )
                            .then(
                                Mono.defer(() -> {
                                    AlertRule entity = mapper.toEntity(dto);
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
    public Mono<AlertRuleDTO> partialUpdate(AlertRuleDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
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
    public Flux<AlertRuleDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AlertRuleDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AlertRuleDTO> findOne(Long id) {
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
            .flatMap(r -> repository.deleteById(r.getId()));
    }

    private Mono<AlertRule> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ALERT_RULE_NOT_FOUND", "AlertRule not found")));
    }
}
