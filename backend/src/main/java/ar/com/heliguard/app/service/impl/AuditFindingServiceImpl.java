package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.service.AuditFindingService;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AuditFindingMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AuditFinding}.
 */
@Service
@Transactional
public class AuditFindingServiceImpl implements AuditFindingService {

    private final AuditFindingRepository repository;
    private final AuditReportRepository reportRepository;
    private final AuditFindingMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AuditFindingServiceImpl(
        AuditFindingRepository repository,
        AuditReportRepository reportRepository,
        AuditFindingMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.reportRepository = reportRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<AuditFindingDTO> save(AuditFindingDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkReport(dto.getAuditReport() != null ? dto.getAuditReport().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            AuditFinding entity = mapper.toEntity(dto);
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
    public Mono<AuditFindingDTO> update(AuditFindingDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    AuditFinding entity = mapper.toEntity(dto);
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
    public Mono<AuditFindingDTO> partialUpdate(AuditFindingDTO dto) {
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
    public Flux<AuditFindingDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditFindingDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditFindingDTO> findOne(Long id) {
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
            .flatMap(f -> repository.deleteById(f.getId()));
    }

    private Mono<Void> checkReport(Long reportId, Long orgId) {
        if (reportId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_REPORT_REQUIRED", "auditReport.id is required"));
        }
        return reportRepository
            .findById(reportId)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_REPORT_NOT_IN_TENANT", "Not found in tenant")))
            .then();
    }

    private Mono<AuditFinding> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(f -> orgId.equals(f.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_FINDING_NOT_FOUND", "AuditFinding not found")));
    }
}
