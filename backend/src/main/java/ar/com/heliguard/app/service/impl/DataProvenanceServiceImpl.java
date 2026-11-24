package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.DataProvenanceRepository;
import ar.com.heliguard.app.service.DataProvenanceService;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.DataProvenanceMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link DataProvenance} enforcing Sentinel/GOES non-thermography guard (RB-ENE-001).
 */
@Service
@Transactional
public class DataProvenanceServiceImpl implements DataProvenanceService {

    private final DataProvenanceRepository repository;
    private final DataProvenanceMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public DataProvenanceServiceImpl(
        DataProvenanceRepository repository,
        DataProvenanceMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<DataProvenanceDTO> save(DataProvenanceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                validateProvenance(dto)
                    .then(
                        dto.getInstallation() == null || dto.getInstallation().getId() == null
                            ? Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "INSTALLATION_REQUIRED", "installation.id is required"))
                            : permissionService.requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    )
                    .flatMap(inst -> {
                        DataProvenance entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<DataProvenanceDTO> update(DataProvenanceDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(validateProvenance(dto))
                            .then(
                                Mono.defer(() -> {
                                    DataProvenance entity = mapper.toEntity(dto);
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
    public Mono<DataProvenanceDTO> partialUpdate(DataProvenanceDTO dto) {
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
                                    return validateProvenanceEntity(existing).then(repository.save(existing));
                                })
                            )
                    )
            )
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DataProvenanceDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DataProvenanceDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DataProvenanceDTO> findOne(Long id) {
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
            .flatMap(p -> repository.deleteById(p.getId()));
    }

    private Mono<Void> validateProvenance(DataProvenanceDTO dto) {
        return checkSatelliteThermographyClaim(dto.getSourceProductCode(), dto.getQualityFlags(), dto.getSourceRecordKey());
    }

    private Mono<Void> validateProvenanceEntity(DataProvenance entity) {
        return checkSatelliteThermographyClaim(entity.getSourceProductCode(), entity.getQualityFlags(), entity.getSourceRecordKey());
    }

    private Mono<Void> checkSatelliteThermographyClaim(String productCode, String qualityFlags, String recordKey) {
        String combined = ((productCode != null ? productCode : "") + " " + (qualityFlags != null ? qualityFlags : "") + " " + (recordKey != null ? recordKey : ""))
            .toUpperCase(Locale.ROOT);
        boolean isSatellite = combined.contains("SENTINEL") || combined.contains("GOES");
        boolean claimsModuleThermography = combined.contains("THERMOGRAPHY") || combined.contains("MAPA_TERMICO_MODULO");
        if (isSatellite && claimsModuleThermography) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_SATELLITE_THERMOGRAPHY_CLAIM",
                    "Sentinel/GOES satellite products do not provide module-level thermography"
                )
            );
        }
        return Mono.empty();
    }

    private Mono<DataProvenance> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(p -> orgId.equals(p.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "DATA_PROVENANCE_NOT_FOUND", "DataProvenance not found")));
    }
}
