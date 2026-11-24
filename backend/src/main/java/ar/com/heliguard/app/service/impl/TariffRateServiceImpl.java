package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.TariffRateService;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.TariffRateMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link TariffRate} enforcing validity window and non-negative prices.
 */
@Service
@Transactional
public class TariffRateServiceImpl implements TariffRateService {

    private final TariffRateRepository repository;
    private final TariffPlanRepository planRepository;
    private final TariffRateMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public TariffRateServiceImpl(
        TariffRateRepository repository,
        TariffPlanRepository planRepository,
        TariffRateMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<TariffRateDTO> save(TariffRateDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                permissionService
                    .validateTemporalWindow(dto.getValidFrom(), dto.getValidTo())
                    .then(checkPlanOwnership(dto.getTariffPlan() != null ? dto.getTariffPlan().getId() : null, tenant.organizationId()))
                    .then(
                        Mono.defer(() -> {
                            TariffRate entity = mapper.toEntity(dto);
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
    public Mono<TariffRateDTO> update(TariffRateDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(permissionService.validateTemporalWindow(dto.getValidFrom(), dto.getValidTo()))
                            .then(checkPlanOwnership(dto.getTariffPlan() != null ? dto.getTariffPlan().getId() : null, tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    TariffRate entity = mapper.toEntity(dto);
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
    public Mono<TariffRateDTO> partialUpdate(TariffRateDTO dto) {
        return permissionService
            .requireManageRole()
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
                                    return permissionService
                                        .validateTemporalWindow(existing.getValidFrom(), existing.getValidTo())
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffRateDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TariffRateDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TariffRateDTO> findOne(Long id) {
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

    private Mono<Void> checkPlanOwnership(Long planId, Long orgId) {
        if (planId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "TARIFF_PLAN_REQUIRED", "tariffPlan.id is required"));
        }
        return planRepository
            .findById(planId)
            .filter(p -> orgId.equals(p.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "TARIFF_PLAN_NOT_IN_TENANT", "TariffPlan not found in tenant")))
            .then();
    }

    private Mono<TariffRate> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "TARIFF_RATE_NOT_FOUND", "TariffRate not found")));
    }
}
