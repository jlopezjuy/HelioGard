package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.repository.PlanSubscriptionRepository;
import ar.com.heliguard.app.service.MaintenancePlanVersionService;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.MaintenancePlanVersionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link MaintenancePlanVersion} enforcing published/subscribed immutability (RB-PLAN-002).
 */
@Service
@Transactional
public class MaintenancePlanVersionServiceImpl implements MaintenancePlanVersionService {

    private final MaintenancePlanVersionRepository repository;
    private final MaintenancePlanRepository planRepository;
    private final PlanSubscriptionRepository subscriptionRepository;
    private final MaintenancePlanVersionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public MaintenancePlanVersionServiceImpl(
        MaintenancePlanVersionRepository repository,
        MaintenancePlanRepository planRepository,
        PlanSubscriptionRepository subscriptionRepository,
        MaintenancePlanVersionMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<MaintenancePlanVersionDTO> save(MaintenancePlanVersionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                checkPlan(dto.getMaintenancePlan() != null ? dto.getMaintenancePlan().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            MaintenancePlanVersion entity = mapper.toEntity(dto);
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
    public Mono<MaintenancePlanVersionDTO> update(MaintenancePlanVersionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing))
                            .then(
                                Mono.defer(() -> {
                                    MaintenancePlanVersion entity = mapper.toEntity(dto);
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
    public Mono<MaintenancePlanVersionDTO> partialUpdate(MaintenancePlanVersionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing))
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
    public Flux<MaintenancePlanVersionDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MaintenancePlanVersionDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MaintenancePlanVersionDTO> findOne(Long id) {
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
            .flatMap(v -> assertMutable(v).then(repository.deleteById(v.getId())));
    }

    private Mono<Void> assertMutable(MaintenancePlanVersion version) {
        if (version.getPublishedAt() != null) {
            return Mono.error(
                new DomainRuleException(HttpStatus.CONFLICT, "PLAN_VERSION_IMMUTABLE", "Published MaintenancePlanVersion is immutable")
            );
        }
        return subscriptionRepository
            .findByPlanVersion(version.getId())
            .hasElements()
            .flatMap(used ->
                used
                    ? Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "PLAN_VERSION_IMMUTABLE",
                            "MaintenancePlanVersion is immutable once subscribed"
                        )
                    )
                    : Mono.empty()
            );
    }

    private Mono<Void> checkPlan(Long planId, Long orgId) {
        if (planId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "MAINTENANCE_PLAN_REQUIRED", "maintenancePlan.id is required"));
        }
        return planRepository
            .findById(planId)
            .filter(p -> orgId.equals(p.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "MAINTENANCE_PLAN_NOT_IN_TENANT", "Not found in tenant")))
            .then();
    }

    private Mono<MaintenancePlanVersion> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(v -> orgId.equals(v.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.NOT_FOUND, "PLAN_VERSION_NOT_FOUND", "MaintenancePlanVersion not found")
                )
            );
    }
}
