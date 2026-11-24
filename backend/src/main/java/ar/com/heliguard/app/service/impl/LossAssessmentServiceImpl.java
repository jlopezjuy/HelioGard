package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.service.LossAssessmentService;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.LossAssessmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link LossAssessment}.
 */
@Service
@Transactional
public class LossAssessmentServiceImpl implements LossAssessmentService {

    private final LossAssessmentRepository repository;
    private final EnergyModelRunRepository modelRunRepository;
    private final LossAssessmentMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public LossAssessmentServiceImpl(
        LossAssessmentRepository repository,
        EnergyModelRunRepository modelRunRepository,
        LossAssessmentMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.modelRunRepository = modelRunRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<LossAssessmentDTO> save(LossAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                validateLoss(dto.getTotalLossWh(), dto.getModelRun() != null ? dto.getModelRun().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            LossAssessment entity = mapper.toEntity(dto);
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
    public Mono<LossAssessmentDTO> update(LossAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateLoss(
                                    dto.getTotalLossWh(),
                                    dto.getModelRun() != null ? dto.getModelRun().getId() : null,
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    LossAssessment entity = mapper.toEntity(dto);
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
    public Mono<LossAssessmentDTO> partialUpdate(LossAssessmentDTO dto) {
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
                                    return validateLoss(existing.getTotalLossWh(), existing.getModelRunId(), tenant.organizationId())
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossAssessmentDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossAssessmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LossAssessmentDTO> findOne(Long id) {
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
            .flatMap(a -> repository.deleteById(a.getId()));
    }

    private Mono<Void> validateLoss(BigDecimal totalLossWh, Long modelRunId, Long orgId) {
        if (totalLossWh == null || totalLossWh.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_LOSS_WH", "totalLossWh must be >= 0"));
        }
        if (modelRunId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "MODEL_RUN_REQUIRED", "modelRun.id is required"));
        }
        return modelRunRepository
            .findById(modelRunId)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "MODEL_RUN_NOT_IN_TENANT", "EnergyModelRun not found in tenant"))
            )
            .then();
    }

    private Mono<LossAssessment> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "LOSS_ASSESSMENT_NOT_FOUND", "LossAssessment not found")));
    }
}
