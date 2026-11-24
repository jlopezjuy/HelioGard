package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.InterventionEvidence;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.InterventionEvidenceRepository;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.service.InterventionEvidenceService;
import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.InterventionEvidenceMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link InterventionEvidence}.
 */
@Service
@Transactional
public class InterventionEvidenceServiceImpl implements InterventionEvidenceService {

    private final InterventionEvidenceRepository repository;
    private final MaintenanceInterventionRepository interventionRepository;
    private final InterventionEvidenceMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public InterventionEvidenceServiceImpl(
        InterventionEvidenceRepository repository,
        MaintenanceInterventionRepository interventionRepository,
        InterventionEvidenceMapper mapper,
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
    public Mono<InterventionEvidenceDTO> save(InterventionEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                interventionRepository
                    .findById(dto.getIntervention().getId())
                    .filter(i -> tenant.organizationId().equals(i.getOrganizationId()))
                    .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INTERVENTION_NOT_FOUND", "Not found")))
                    .flatMap(i -> {
                        InterventionEvidence entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<InterventionEvidenceDTO> update(InterventionEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    InterventionEvidence entity = mapper.toEntity(dto);
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
    public Mono<InterventionEvidenceDTO> partialUpdate(InterventionEvidenceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
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
    public Flux<InterventionEvidenceDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InterventionEvidenceDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InterventionEvidenceDTO> findOne(Long id) {
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

    private Mono<InterventionEvidence> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(e -> orgId.equals(e.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INTERVENTION_EVIDENCE_NOT_FOUND", "Not found")));
    }
}
