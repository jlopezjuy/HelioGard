package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.VerificationResult;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.VerificationResultRepository;
import ar.com.heliguard.app.service.VerificationResultService;
import ar.com.heliguard.app.service.dto.VerificationResultDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.VerificationResultMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant-isolated service for {@link VerificationResult}.
 */
@Service
@Transactional
public class VerificationResultServiceImpl implements VerificationResultService {

    private final VerificationResultRepository repository;
    private final VerificationResultMapper mapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public VerificationResultServiceImpl(
        VerificationResultRepository repository,
        VerificationResultMapper mapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<VerificationResultDTO> save(VerificationResultDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                permissionService
                    .requireOwnedInstallation(dto.getInstallation().getId(), tenant.organizationId())
                    .flatMap(inst -> {
                        VerificationResult entity = mapper.toEntity(dto);
                        entity.setId(null);
                        entity.setOrganizationId(tenant.organizationId());
                        entity.setOrganization(new Organization().id(tenant.organizationId()));
                        return repository.save(entity);
                    })
            )
            .map(mapper::toDto);
    }

    @Override
    public Mono<VerificationResultDTO> update(VerificationResultDTO dto) {
        return permissionService
            .requireRoleForScope(PermissionScope.AUDIT)
            .flatMap(tenant ->
                findOwned(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    VerificationResult entity = mapper.toEntity(dto);
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
    public Mono<VerificationResultDTO> partialUpdate(VerificationResultDTO dto) {
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
    public Flux<VerificationResultDTO> findAll() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> repository.findByOrganization(tenant.organizationId()))
            .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VerificationResultDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<VerificationResultDTO> findOne(Long id) {
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

    private Mono<VerificationResult> findOwned(Long id, Long orgId) {
        return repository
            .findById(id)
            .filter(r -> orgId.equals(r.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "VERIFICATION_RESULT_NOT_FOUND", "Not found")));
    }
}
