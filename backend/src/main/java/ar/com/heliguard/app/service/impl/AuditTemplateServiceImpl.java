package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditTemplateService;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AuditTemplateMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AuditTemplate} enforcing immutability once published (RB-AUD-002).
 */
@Service
@Transactional
public class AuditTemplateServiceImpl implements AuditTemplateService {

    private final AuditTemplateRepository repository;
    private final AuditReportRepository reportRepository;
    private final AuditTemplateMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AuditTemplateServiceImpl(
        AuditTemplateRepository repository,
        AuditReportRepository reportRepository,
        AuditTemplateMapper mapper,
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
    public Mono<AuditTemplateDTO> save(AuditTemplateDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant -> {
                AuditTemplate entity = mapper.toEntity(dto);
                entity.setId(null);
                entity.setOrganizationId(tenant.organizationId());
                entity.setOrganization(new Organization().id(tenant.organizationId()));
                return repository.save(entity);
            })
            .map(mapper::toDto);
    }

    @Override
    public Mono<AuditTemplateDTO> update(AuditTemplateDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing))
                            .then(
                                Mono.defer(() -> {
                                    AuditTemplate entity = mapper.toEntity(dto);
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
    public Mono<AuditTemplateDTO> partialUpdate(AuditTemplateDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(assertMutable(existing))
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
    public Flux<AuditTemplateDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditTemplateDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditTemplateDTO> findOne(Long id) {
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
            .flatMap(t -> assertMutable(t).then(repository.deleteById(t.getId())));
    }

    private Mono<Void> assertMutable(AuditTemplate template) {
        if (template.getPublishedAt() != null) {
            return Mono.error(
                new DomainRuleException(HttpStatus.CONFLICT, "AUDIT_TEMPLATE_IMMUTABLE", "Published AuditTemplate is immutable")
            );
        }
        return reportRepository
            .findByAuditTemplate(template.getId())
            .hasElements()
            .flatMap(used ->
                used
                    ? Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "AUDIT_TEMPLATE_IMMUTABLE",
                            "AuditTemplate is immutable once used by an AuditReport"
                        )
                    )
                    : Mono.empty()
            );
    }

    private Mono<AuditTemplate> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(t -> orgId.equals(t.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_TEMPLATE_NOT_FOUND", "AuditTemplate not found")));
    }
}
