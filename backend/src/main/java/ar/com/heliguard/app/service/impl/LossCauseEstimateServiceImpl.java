package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.LossCauseEstimate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.LossCause;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.repository.LossCauseEstimateRepository;
import ar.com.heliguard.app.service.LossCauseEstimateService;
import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.LossCauseEstimateMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link LossCauseEstimate} preventing double-counting (RB-ECO-001, T14).
 */
@Service
@Transactional
public class LossCauseEstimateServiceImpl implements LossCauseEstimateService {

    private final LossCauseEstimateRepository repository;
    private final LossAssessmentRepository assessmentRepository;
    private final LossCauseEstimateMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public LossCauseEstimateServiceImpl(
        LossCauseEstimateRepository repository,
        LossAssessmentRepository assessmentRepository,
        LossCauseEstimateMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.assessmentRepository = assessmentRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<LossCauseEstimateDTO> save(LossCauseEstimateDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                validateNoDoubleCounting(
                    null,
                    dto.getLossAssessment() != null ? dto.getLossAssessment().getId() : null,
                    dto.getCause(),
                    dto.getEstimatedWh(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            LossCauseEstimate entity = mapper.toEntity(dto);
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
    public Mono<LossCauseEstimateDTO> update(LossCauseEstimateDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateNoDoubleCounting(
                                    existing.getId(),
                                    dto.getLossAssessment() != null ? dto.getLossAssessment().getId() : null,
                                    dto.getCause(),
                                    dto.getEstimatedWh(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    LossCauseEstimate entity = mapper.toEntity(dto);
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
    public Mono<LossCauseEstimateDTO> partialUpdate(LossCauseEstimateDTO dto) {
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
                                    return validateNoDoubleCounting(
                                        existing.getId(),
                                        existing.getLossAssessmentId(),
                                        existing.getCause(),
                                        existing.getEstimatedWh(),
                                        tenant.organizationId()
                                    )
                                        .then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossCauseEstimateDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LossCauseEstimateDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LossCauseEstimateDTO> findOne(Long id) {
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

    private Mono<Void> validateNoDoubleCounting(
        Long currentId,
        Long assessmentId,
        LossCause cause,
        BigDecimal estimatedWh,
        Long orgId
    ) {
        if (estimatedWh == null || estimatedWh.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_ESTIMATED_WH", "estimatedWh must be >= 0"));
        }
        if (assessmentId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "LOSS_ASSESSMENT_REQUIRED", "lossAssessment.id is required"));
        }
        return assessmentRepository
            .findById(assessmentId)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "LOSS_ASSESSMENT_NOT_IN_TENANT", "LossAssessment not found in tenant"))
            )
            .flatMap(assessment ->
                repository
                    .findByLossAssessment(assessmentId)
                    .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                    .filter(existing -> existing.getCause() != LossCause.UNKNOWN)
                    .map(LossCauseEstimate::getEstimatedWh)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .flatMap(existingSum -> {
                        BigDecimal newTotal = cause == LossCause.UNKNOWN ? existingSum : existingSum.add(estimatedWh);
                        if (newTotal.compareTo(assessment.getTotalLossWh()) > 0) {
                            return Mono.error(
                                new DomainRuleException(
                                    HttpStatus.CONFLICT,
                                    "LOSS_DOUBLE_COUNTING_FORBIDDEN",
                                    "Attributed loss causes exceed totalLossWh"
                                )
                            );
                        }
                        return Mono.empty();
                    })
            );
    }

    private Mono<LossCauseEstimate> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "LOSS_CAUSE_ESTIMATE_NOT_FOUND", "LossCauseEstimate not found"))
            );
    }
}
