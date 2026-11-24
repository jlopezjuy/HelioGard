package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanBenefit;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.repository.PlanBenefitRepository;
import ar.com.heliguard.app.service.PlanBenefitService;
import ar.com.heliguard.app.service.dto.PlanBenefitDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.PlanBenefitMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link PlanBenefit} enforcing published plan version immutability (RB-PLAN-002).
 */
@Service
@Transactional
public class PlanBenefitServiceImpl implements PlanBenefitService {

    private final PlanBenefitRepository repository;
    private final MaintenancePlanVersionRepository versionRepository;
    private final PlanBenefitMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public PlanBenefitServiceImpl(
        PlanBenefitRepository repository,
        MaintenancePlanVersionRepository versionRepository,
        PlanBenefitMapper mapper,
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
    public Mono<PlanBenefitDTO> save(PlanBenefitDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                checkVersionMutable(dto.getPlanVersion() != null ? dto.getPlanVersion().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            PlanBenefit entity = mapper.toEntity(dto);
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
    public Mono<PlanBenefitDTO> update(PlanBenefitDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(checkVersionMutable(existing.getPlanVersionId(), tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    PlanBenefit entity = mapper.toEntity(dto);
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
    public Mono<PlanBenefitDTO> partialUpdate(PlanBenefitDTO dto) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(checkVersionMutable(existing.getPlanVersionId(), tenant.organizationId()))
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
    public Flux<PlanBenefitDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanBenefitDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanBenefitDTO> findOne(Long id) {
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
            .flatMap(b -> checkVersionMutable(b.getPlanVersionId(), b.getOrganizationId()).then(repository.deleteById(b.getId())));
    }

    private Mono<Void> checkVersionMutable(Long versionId, Long orgId) {
        if (versionId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "PLAN_VERSION_REQUIRED", "planVersion.id is required"));
        }
        return versionRepository
            .findById(versionId)
            .filter(v -> orgId.equals(v.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "PLAN_VERSION_NOT_IN_TENANT", "Not found in tenant")))
            .flatMap(v ->
                v.getPublishedAt() != null
                    ? Mono.error(
                        new DomainRuleException(HttpStatus.CONFLICT, "PLAN_VERSION_IMMUTABLE", "Published MaintenancePlanVersion is immutable")
                    )
                    : Mono.empty()
            );
    }

    private Mono<PlanBenefit> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(b -> orgId.equals(b.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "PLAN_BENEFIT_NOT_FOUND", "PlanBenefit not found")));
    }
}
