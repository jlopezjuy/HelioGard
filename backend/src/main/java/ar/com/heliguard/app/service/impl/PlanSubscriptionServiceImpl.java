package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.repository.PlanSubscriptionRepository;
import ar.com.heliguard.app.service.PlanSubscriptionService;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.PlanSubscriptionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link PlanSubscription} freezing price/currency from {@link ar.com.heliguard.app.domain.MaintenancePlanVersion} (RB-PLAN-002).
 */
@Service
@Transactional
public class PlanSubscriptionServiceImpl implements PlanSubscriptionService {

    private final PlanSubscriptionRepository repository;
    private final MaintenancePlanVersionRepository versionRepository;
    private final PlanSubscriptionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public PlanSubscriptionServiceImpl(
        PlanSubscriptionRepository repository,
        MaintenancePlanVersionRepository versionRepository,
        PlanSubscriptionMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.versionRepository = versionRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<PlanSubscriptionDTO> save(PlanSubscriptionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .then(
                        versionRepository
                            .findById(dto.getPlanVersion().getId())
                            .filter(v -> tenant.organizationId().equals(v.getOrganizationId()))
                            .switchIfEmpty(
                                Mono.error(
                                    new DomainRuleException(
                                        HttpStatus.BAD_REQUEST,
                                        "PLAN_VERSION_NOT_IN_TENANT",
                                        "MaintenancePlanVersion not found in tenant"
                                    )
                                )
                            )
                    )
                    .flatMap(version -> {
                        PlanSubscription entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        // RB-PLAN-002: Freeze price & currency from plan version
                        entity.setAgreedAmount(version.getRecurringAmount());
                        entity.setCurrencyCode(version.getCurrencyCode());
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<PlanSubscriptionDTO> update(PlanSubscriptionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    PlanSubscription entity = mapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    entity.setAgreedAmount(existing.getAgreedAmount());
                                    entity.setCurrencyCode(existing.getCurrencyCode());
                                    return repository.save(entity);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<PlanSubscriptionDTO> partialUpdate(PlanSubscriptionDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    var frozenAmount = existing.getAgreedAmount();
                                    var frozenCurrency = existing.getCurrencyCode();
                                    mapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    existing.setAgreedAmount(frozenAmount);
                                    existing.setCurrencyCode(frozenCurrency);
                                    return repository.save(existing);
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanSubscriptionDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanSubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().flatMap(tenant -> repository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanSubscriptionDTO> findOne(Long id) {
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
            .flatMap(s -> repository.deleteById(s.getId()));
    }

    private Mono<PlanSubscription> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(s -> orgId.equals(s.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "PLAN_SUBSCRIPTION_NOT_FOUND", "PlanSubscription not found")));
    }
}
