package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.EconomicAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EconomicAssessmentRepository;
import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.EconomicAssessmentService;
import ar.com.heliguard.app.service.dto.EconomicAssessmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.EconomicAssessmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link EconomicAssessment}.
 */
@Service
@Transactional
public class EconomicAssessmentServiceImpl implements EconomicAssessmentService {

    private final EconomicAssessmentRepository repository;
    private final LossAssessmentRepository lossRepository;
    private final TariffRateRepository rateRepository;
    private final EconomicAssessmentMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public EconomicAssessmentServiceImpl(
        EconomicAssessmentRepository repository,
        LossAssessmentRepository lossRepository,
        TariffRateRepository rateRepository,
        EconomicAssessmentMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.lossRepository = lossRepository;
        this.rateRepository = rateRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<EconomicAssessmentDTO> save(EconomicAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkRefs(
                    dto.getLossAssessment() != null ? dto.getLossAssessment().getId() : null,
                    dto.getTariffRate() != null ? dto.getTariffRate().getId() : null,
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            EconomicAssessment entity = mapper.toEntity(dto);
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
    public Mono<EconomicAssessmentDTO> update(EconomicAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                checkRefs(
                                    dto.getLossAssessment() != null ? dto.getLossAssessment().getId() : null,
                                    dto.getTariffRate() != null ? dto.getTariffRate().getId() : null,
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    EconomicAssessment entity = mapper.toEntity(dto);
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
    public Mono<EconomicAssessmentDTO> partialUpdate(EconomicAssessmentDTO dto) {
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
    public Flux<EconomicAssessmentDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EconomicAssessmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EconomicAssessmentDTO> findOne(Long id) {
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

    private Mono<Void> checkRefs(Long lossId, Long rateId, Long orgId) {
        if (lossId == null || rateId == null) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "ECONOMIC_REFS_REQUIRED", "lossAssessment.id and tariffRate.id are required")
            );
        }
        Mono<Void> checkLoss = lossRepository
            .findById(lossId)
            .filter(l -> orgId.equals(l.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "LOSS_ASSESSMENT_NOT_IN_TENANT", "Not found in tenant")))
            .then();
        Mono<Void> checkRate = rateRepository
            .findById(rateId)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "TARIFF_RATE_NOT_IN_TENANT", "Not found in tenant")))
            .then();
        return checkLoss.then(checkRate);
    }

    private Mono<EconomicAssessment> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "ECONOMIC_ASSESSMENT_NOT_FOUND", "EconomicAssessment not found"))
            );
    }
}
