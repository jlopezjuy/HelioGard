package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.repository.RecoveryAssessmentRepository;
import ar.com.heliguard.app.service.RecoveryAssessmentService;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.RecoveryAssessmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link RecoveryAssessment}.
 */
@Service
@Transactional
public class RecoveryAssessmentServiceImpl implements RecoveryAssessmentService {

    private final RecoveryAssessmentRepository repository;
    private final MaintenanceInterventionRepository interventionRepository;
    private final RecoveryAssessmentMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public RecoveryAssessmentServiceImpl(
        RecoveryAssessmentRepository repository,
        MaintenanceInterventionRepository interventionRepository,
        RecoveryAssessmentMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.interventionRepository = interventionRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<RecoveryAssessmentDTO> save(RecoveryAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                interventionRepository
                    .findById(dto.getIntervention().getId())
                    .filter(i -> tenant.organizationId().equals(i.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INTERVENTION_NOT_FOUND", "Not found")))
                    .flatMap(i -> {
                        RecoveryAssessment entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<RecoveryAssessmentDTO> update(RecoveryAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    RecoveryAssessment entity = mapper.toEntity(dto);
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
    public Mono<RecoveryAssessmentDTO> partialUpdate(RecoveryAssessmentDTO dto) {
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
    public Flux<RecoveryAssessmentDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RecoveryAssessmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RecoveryAssessmentDTO> findOne(Long id) {
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

    private Mono<RecoveryAssessment> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "RECOVERY_ASSESSMENT_NOT_FOUND", "Not found")));
    }
}
