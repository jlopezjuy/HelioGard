package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditReportService;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AuditReportMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AuditReport} enforcing DRAFT start and explicit approval workflow (RB-AUD-001).
 */
@Service
@Transactional
public class AuditReportServiceImpl implements AuditReportService {

    private final AuditReportRepository repository;
    private final AuditTemplateRepository templateRepository;
    private final AuditReportMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AuditReportServiceImpl(
        AuditReportRepository repository,
        AuditTemplateRepository templateRepository,
        AuditReportMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.templateRepository = templateRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<AuditReportDTO> save(AuditReportDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .then(
                        templateRepository
                            .findById(dto.getAuditTemplate().getId())
                            .filter(t -> tenant.organizationId().equals(t.getOrganizationId()))
                            .switchIfEmpty(
                                Mono.error(
                                    new DomainRuleException(
                                        HttpStatus.BAD_REQUEST,
                                        "AUDIT_TEMPLATE_NOT_IN_TENANT",
                                        "AuditTemplate not found in tenant"
                                    )
                                )
                            )
                    )
                    .flatMap(template -> {
                        AuditReport entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        entity.setStatus(AuditStatus.DRAFT);
                        if (entity.getStartedAt() == null) {
                            entity.setStartedAt(Instant.now());
                        }
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<AuditReportDTO> update(AuditReportDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getStatus() == AuditStatus.APPROVED && existing.getStatus() != AuditStatus.APPROVED
                                    ? Mono.error(
                                        new DomainRuleException(
                                            HttpStatus.CONFLICT,
                                            "USE_AUDIT_APPROVAL_ENDPOINT",
                                            "Approve AuditReport via POST /api/audit-reports/{id}/approve"
                                        )
                                    )
                                    : Mono.empty()
                            )
                            .then(
                                Mono.defer(() -> {
                                    AuditReport entity = mapper.toEntity(dto);
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
    public Mono<AuditReportDTO> partialUpdate(AuditReportDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                dto.getStatus() == AuditStatus.APPROVED && existing.getStatus() != AuditStatus.APPROVED
                                    ? Mono.error(
                                        new DomainRuleException(
                                            HttpStatus.CONFLICT,
                                            "USE_AUDIT_APPROVAL_ENDPOINT",
                                            "Approve AuditReport via POST /api/audit-reports/{id}/approve"
                                        )
                                    )
                                    : Mono.empty()
                            )
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
    public Flux<AuditReportDTO> findAll(Pageable pageable) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findPageByOrganization(tenant.organizationId(), pageable.getPageSize(), pageable.getOffset()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditReportDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return tenantContextService.requireCurrentTenant().flatMap(tenant -> repository.countByOrganization(tenant.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditReportDTO> findOne(Long id) {
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

    private Mono<AuditReport> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_REPORT_NOT_FOUND", "AuditReport not found")));
    }
}
