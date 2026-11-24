package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvidence;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.SecurityCaseEvidenceRepository;
import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityCaseEvidenceService;
import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.SecurityCaseEvidenceMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link SecurityCaseEvidence}.
 */
@Service
@Transactional
public class SecurityCaseEvidenceServiceImpl implements SecurityCaseEvidenceService {

    private final SecurityCaseEvidenceRepository repository;
    private final SecurityIncidentRepository incidentRepository;
    private final SecurityCaseEvidenceMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public SecurityCaseEvidenceServiceImpl(
        SecurityCaseEvidenceRepository repository,
        SecurityIncidentRepository incidentRepository,
        SecurityCaseEvidenceMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.incidentRepository = incidentRepository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<SecurityCaseEvidenceDTO> save(SecurityCaseEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                checkSecurityIncident(dto.getSecurityIncident() != null ? dto.getSecurityIncident().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            SecurityCaseEvidence entity = mapper.toEntity(dto);
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
    public Mono<SecurityCaseEvidenceDTO> update(SecurityCaseEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    SecurityCaseEvidence entity = mapper.toEntity(dto);
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
    public Mono<SecurityCaseEvidenceDTO> partialUpdate(SecurityCaseEvidenceDTO dto) {
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
    public Flux<SecurityCaseEvidenceDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SecurityCaseEvidenceDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SecurityCaseEvidenceDTO> findOne(Long id) {
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

    private Mono<Void> checkSecurityIncident(Long secIncidentId, Long orgId) {
        if (secIncidentId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SECURITY_INCIDENT_REQUIRED", "securityIncident.id is required"));
        }
        return incidentRepository
            .findById(secIncidentId)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.BAD_REQUEST, "SECURITY_INCIDENT_NOT_IN_TENANT", "SecurityIncident not found in tenant")
                )
            )
            .then();
    }

    private Mono<SecurityCaseEvidence> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "SECURITY_CASE_EVIDENCE_NOT_FOUND", "SecurityCaseEvidence not found"))
            );
    }
}
