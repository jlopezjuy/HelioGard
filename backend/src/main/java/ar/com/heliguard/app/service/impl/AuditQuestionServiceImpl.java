package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditQuestionService;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AuditQuestionMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AuditQuestion} enforcing published template immutability (RB-AUD-002).
 */
@Service
@Transactional
public class AuditQuestionServiceImpl implements AuditQuestionService {

    private final AuditQuestionRepository repository;
    private final AuditTemplateRepository templateRepository;
    private final AuditQuestionMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AuditQuestionServiceImpl(
        AuditQuestionRepository repository,
        AuditTemplateRepository templateRepository,
        AuditQuestionMapper mapper,
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
    public Mono<AuditQuestionDTO> save(AuditQuestionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkTemplateMutable(dto.getAuditTemplate() != null ? dto.getAuditTemplate().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            AuditQuestion entity = mapper.toEntity(dto);
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
    public Mono<AuditQuestionDTO> update(AuditQuestionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(checkTemplateMutable(existing.getAuditTemplateId(), tenant.organizationId()))
                            .then(
                                Mono.defer(() -> {
                                    AuditQuestion entity = mapper.toEntity(dto);
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
    public Mono<AuditQuestionDTO> partialUpdate(AuditQuestionDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(checkTemplateMutable(existing.getAuditTemplateId(), tenant.organizationId()))
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
    public Flux<AuditQuestionDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditQuestionDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditQuestionDTO> findOne(Long id) {
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
            .flatMap(q -> checkTemplateMutable(q.getAuditTemplateId(), q.getOrganizationId()).then(repository.deleteById(q.getId())));
    }

    private Mono<Void> checkTemplateMutable(Long templateId, Long orgId) {
        if (templateId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_TEMPLATE_REQUIRED", "auditTemplate.id is required"));
        }
        return templateRepository
            .findById(templateId)
            .filter(t -> orgId.equals(t.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_TEMPLATE_NOT_IN_TENANT", "AuditTemplate not found in tenant"))
            )
            .flatMap(t ->
                t.getPublishedAt() != null
                    ? Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "AUDIT_TEMPLATE_IMMUTABLE",
                            "Cannot modify questions of a published AuditTemplate"
                        )
                    )
                    : Mono.empty()
            );
    }

    private Mono<AuditQuestion> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(q -> orgId.equals(q.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_QUESTION_NOT_FOUND", "AuditQuestion not found")));
    }
}
