package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.IncidentLocalization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.IncidentLocalizationRepository;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.IncidentLocalizationService;
import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.IncidentLocalizationMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link IncidentLocalization}.
 */
@Service
@Transactional
public class IncidentLocalizationServiceImpl implements IncidentLocalizationService {

    private final IncidentLocalizationRepository repository;
    private final IncidentRepository incidentRepository;
    private final IncidentLocalizationMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public IncidentLocalizationServiceImpl(
        IncidentLocalizationRepository repository,
        IncidentRepository incidentRepository,
        IncidentLocalizationMapper mapper,
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
    public Mono<IncidentLocalizationDTO> save(IncidentLocalizationDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                checkIncident(dto.getIncident() != null ? dto.getIncident().getId() : null, tenant.organizationId())
                    .then(
                        Mono.defer(() -> {
                            IncidentLocalization entity = mapper.toEntity(dto);
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
    public Mono<IncidentLocalizationDTO> update(IncidentLocalizationDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    IncidentLocalization entity = mapper.toEntity(dto);
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
    public Mono<IncidentLocalizationDTO> partialUpdate(IncidentLocalizationDTO dto) {
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
    public Flux<IncidentLocalizationDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IncidentLocalizationDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IncidentLocalizationDTO> findOne(Long id) {
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
            .flatMap(l -> repository.deleteById(l.getId()));
    }

    private Mono<Void> checkIncident(Long incidentId, Long orgId) {
        if (incidentId == null) {
            return Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INCIDENT_REQUIRED", "incident.id is required"));
        }
        return incidentRepository
            .findById(incidentId)
            .filter(i -> orgId.equals(i.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INCIDENT_NOT_IN_TENANT", "Incident not found in tenant")))
            .then();
    }

    private Mono<IncidentLocalization> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(l -> orgId.equals(l.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INCIDENT_LOCALIZATION_NOT_FOUND", "IncidentLocalization not found"))
            );
    }
}
