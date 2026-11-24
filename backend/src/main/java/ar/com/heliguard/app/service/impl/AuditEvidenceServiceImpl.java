package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.AuditEvidence;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.service.AuditEvidenceService;
import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.AuditEvidenceMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link AuditEvidence} (RB-AUD-002).
 */
@Service
@Transactional
public class AuditEvidenceServiceImpl implements AuditEvidenceService {

    private final AuditEvidenceRepository repository;
    private final AuditFindingRepository findingRepository;
    private final AuditEvidenceMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public AuditEvidenceServiceImpl(
        AuditEvidenceRepository repository,
        AuditFindingRepository findingRepository,
        AuditEvidenceMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.findingRepository = findingRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<AuditEvidenceDTO> save(AuditEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkFinding(dto.getFinding() != null ? dto.getFinding().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            AuditEvidence entity = mapper.toEntity(dto);
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
    public Mono<AuditEvidenceDTO> update(AuditEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    AuditEvidence entity = mapper.toEntity(dto);
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
    public Mono<AuditEvidenceDTO> partialUpdate(AuditEvidenceDTO dto) {
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
    public Flux<AuditEvidenceDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditEvidenceDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditEvidenceDTO> findOne(Long id) {
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

    private Mono<Void> checkFinding(Long findingId, Long orgId) {
        if (findingId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_FINDING_REQUIRED", "finding.id is required"));
        }
        return findingRepository
            .findById(findingId)
            .filter(f -> orgId.equals(f.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "AUDIT_FINDING_NOT_IN_TENANT", "Not found in tenant")))
            .then();
    }

    private Mono<AuditEvidence> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "AUDIT_EVIDENCE_NOT_FOUND", "AuditEvidence not found")));
    }
}
