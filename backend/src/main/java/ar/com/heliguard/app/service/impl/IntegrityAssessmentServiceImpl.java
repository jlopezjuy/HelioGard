package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.IntegrityAssessmentRepository;
import ar.com.heliguard.app.service.IntegrityAssessmentService;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.IntegrityAssessmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.IntegrityAssessment}
 * with tenant isolation.
 */
@Service
@Transactional
public class IntegrityAssessmentServiceImpl implements IntegrityAssessmentService {

    private final IntegrityAssessmentRepository assessmentRepository;
    private final IntegrityAssessmentMapper assessmentMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public IntegrityAssessmentServiceImpl(
        IntegrityAssessmentRepository assessmentRepository,
        IntegrityAssessmentMapper assessmentMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentMapper = assessmentMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<IntegrityAssessmentDTO> save(IntegrityAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        IntegrityAssessment entity = assessmentMapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return assessmentRepository.save(entity);
                    })
            )
            .map(assessmentMapper::toDto);
    }

    @Override
    public Mono<IntegrityAssessmentDTO> update(IntegrityAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    IntegrityAssessment entity = assessmentMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return assessmentRepository.save(entity);
                                })
                            )
                    )
            )
            .map(assessmentMapper::toDto);
    }

    @Override
    public Mono<IntegrityAssessmentDTO> partialUpdate(IntegrityAssessmentDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    assessmentMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return assessmentRepository.save(existing);
                                })
                            )
                    )
            )
            .map(assessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IntegrityAssessmentDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> assessmentRepository.findByOrganization(tenant.organizationId()))
            .map(assessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IntegrityAssessmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IntegrityAssessmentDTO> findOne(Long id) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .map(assessmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return permissionService
            .requireManageRole()
            .flatMap(tenant -> findOwned(id, tenant.organizationId()))
            .flatMap(a -> assessmentRepository.deleteById(a.getId()));
    }

    private Mono<IntegrityAssessment> findOwned(Long id, Long orgId) {
        return assessmentRepository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INTEGRITY_ASSESSMENT_NOT_FOUND", "IntegrityAssessment not found"))
            );
    }
}
