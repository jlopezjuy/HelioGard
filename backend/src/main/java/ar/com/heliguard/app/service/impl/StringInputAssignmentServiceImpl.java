package ar.com.heliguard.app.service.impl;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.StringInputAssignment;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.repository.InverterMpptInputRepository;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.repository.StringInputAssignmentRepository;
import ar.com.heliguard.app.service.StringInputAssignmentService;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import ar.com.heliguard.app.service.mapper.StringInputAssignmentMapper;
import ar.com.heliguard.app.service.tenant.PermissionService;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ar.com.heliguard.app.domain.StringInputAssignment}
 * enforcing same-tenant, same-installation, and non-overlapping temporal MPPT assignments (RB-ASSET-002).
 */
@Service
@Transactional
public class StringInputAssignmentServiceImpl implements StringInputAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(StringInputAssignmentServiceImpl.class);

    private final StringInputAssignmentRepository assignmentRepository;
    private final SolarStringRepository solarStringRepository;
    private final SolarArrayRepository solarArrayRepository;
    private final InverterMpptInputRepository mpptInputRepository;
    private final EquipmentRepository equipmentRepository;
    private final StringInputAssignmentMapper assignmentMapper;
    private final TenantContextService tenantContextService;
    private final PermissionService permissionService;

    public StringInputAssignmentServiceImpl(
        StringInputAssignmentRepository assignmentRepository,
        SolarStringRepository solarStringRepository,
        SolarArrayRepository solarArrayRepository,
        InverterMpptInputRepository mpptInputRepository,
        EquipmentRepository equipmentRepository,
        StringInputAssignmentMapper assignmentMapper,
        TenantContextService tenantContextService,
        PermissionService permissionService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.solarStringRepository = solarStringRepository;
        this.solarArrayRepository = solarArrayRepository;
        this.mpptInputRepository = mpptInputRepository;
        this.equipmentRepository = equipmentRepository;
        this.assignmentMapper = assignmentMapper;
        this.tenantContextService = tenantContextService;
        this.permissionService = permissionService;
    }

    @Override
    public Mono<StringInputAssignmentDTO> save(StringInputAssignmentDTO dto) {
        LOG.debug("Request to save StringInputAssignment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                validateAssignment(
                    null,
                    dto.getSolarString() != null ? dto.getSolarString().getId() : null,
                    dto.getMpptInput() != null ? dto.getMpptInput().getId() : null,
                    dto.getValidFrom(),
                    dto.getValidUntil(),
                    tenant.organizationId()
                )
                    .then(
                        Mono.defer(() -> {
                            StringInputAssignment entity = assignmentMapper.toEntity(dto);
                            entity.setId(null);
                            entity.setOrganizationId(tenant.organizationId());
                            entity.setOrganization(new Organization().id(tenant.organizationId()));
                            return saveWithOverlapMapping(entity);
                        })
                    )
            )
            .map(assignmentMapper::toDto);
    }

    @Override
    public Mono<StringInputAssignmentDTO> update(StringInputAssignmentDTO dto) {
        LOG.debug("Request to update StringInputAssignment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedAssignment(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                validateAssignment(
                                    existing.getId(),
                                    dto.getSolarString() != null ? dto.getSolarString().getId() : null,
                                    dto.getMpptInput() != null ? dto.getMpptInput().getId() : null,
                                    dto.getValidFrom(),
                                    dto.getValidUntil(),
                                    tenant.organizationId()
                                )
                            )
                            .then(
                                Mono.defer(() -> {
                                    StringInputAssignment entity = assignmentMapper.toEntity(dto);
                                    entity.setOrganizationId(existing.getOrganizationId());
                                    entity.setOrganization(new Organization().id(existing.getOrganizationId()));
                                    return saveWithOverlapMapping(entity);
                                })
                            )
                    )
            )
            .map(assignmentMapper::toDto);
    }

    @Override
    public Mono<StringInputAssignmentDTO> partialUpdate(StringInputAssignmentDTO dto) {
        LOG.debug("Request to partially update StringInputAssignment : {}", dto);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant ->
                findOwnedAssignment(dto.getId(), tenant.organizationId())
                    .flatMap(existing ->
                        permissionService
                            .assertNoOrgReassignment(dto.getOrganization(), tenant.organizationId())
                            .then(
                                Mono.defer(() -> {
                                    assignmentMapper.partialUpdate(existing, dto);
                                    existing.setOrganizationId(tenant.organizationId());
                                    existing.setOrganization(new Organization().id(tenant.organizationId()));
                                    return validateAssignment(
                                        existing.getId(),
                                        existing.getSolarStringId(),
                                        existing.getMpptInputId(),
                                        existing.getValidFrom(),
                                        existing.getValidUntil(),
                                        tenant.organizationId()
                                    )
                                        .then(saveWithOverlapMapping(existing));
                                })
                            )
                    )
            )
            .map(assignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StringInputAssignmentDTO> findAll() {
        LOG.debug("Request to get all StringInputAssignments for current tenant");
        return tenantContextService
            .requireCurrentTenant()
            .flatMapMany(tenant -> assignmentRepository.findByOrganization(tenant.organizationId()))
            .map(assignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StringInputAssignmentDTO> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return findAll().count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StringInputAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get StringInputAssignment : {}", id);
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant -> findOwnedAssignment(id, tenant.organizationId()))
            .map(assignmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete StringInputAssignment : {}", id);
        return permissionService
            .requireRoleForScope(PermissionScope.MAINTENANCE)
            .flatMap(tenant -> findOwnedAssignment(id, tenant.organizationId()))
            .flatMap(a -> assignmentRepository.deleteById(a.getId()));
    }

    private Mono<StringInputAssignment> saveWithOverlapMapping(StringInputAssignment entity) {
        return assignmentRepository
            .save(entity)
            .onErrorMap(
                DataIntegrityViolationException.class,
                ex ->
                    new DomainRuleException(
                        HttpStatus.CONFLICT,
                        "OVERLAPPING_STRING_ASSIGNMENT",
                        "SolarString already has an overlapping MPPT input assignment"
                    )
            );
    }

    private Mono<Void> validateAssignment(
        Long currentId,
        Long solarStringId,
        Long mpptInputId,
        Instant validFrom,
        Instant validUntil,
        Long orgId
    ) {
        if (solarStringId == null || mpptInputId == null) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "ASSIGNMENT_TARGETS_REQUIRED", "solarString.id and mpptInput.id are required")
            );
        }
        return permissionService
            .validateTemporalWindow(validFrom, validUntil)
            .then(
                Mono.zip(
                    resolveStringInstallationId(solarStringId, orgId),
                    resolveMpptInstallationId(mpptInputId, orgId)
                )
            )
            .flatMap(tuple -> {
                if (!tuple.getT1().equals(tuple.getT2())) {
                    return Mono.error(
                        new DomainRuleException(
                            HttpStatus.BAD_REQUEST,
                            "CROSS_INSTALLATION_WIRING_FORBIDDEN",
                            "SolarString and InverterMpptInput must belong to the same installation"
                        )
                    );
                }
                return checkOverlap(currentId, solarStringId, validFrom, validUntil);
            });
    }

    private Mono<Long> resolveStringInstallationId(Long solarStringId, Long orgId) {
        return solarStringRepository
            .findById(solarStringId)
            .filter(s -> orgId.equals(s.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.BAD_REQUEST, "SOLAR_STRING_NOT_IN_TENANT", "SolarString not found in tenant"))
            )
            .flatMap(s -> solarArrayRepository.findById(s.getSolarArrayId()))
            .filter(arr -> orgId.equals(arr.getOrganizationId()))
            .map(arr -> arr.getInstallationId());
    }

    private Mono<Long> resolveMpptInstallationId(Long mpptInputId, Long orgId) {
        return mpptInputRepository
            .findById(mpptInputId)
            .filter(m -> orgId.equals(m.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.BAD_REQUEST, "MPPT_INPUT_NOT_IN_TENANT", "InverterMpptInput not found in tenant")
                )
            )
            .flatMap(m -> equipmentRepository.findById(m.getInverterId()))
            .filter(eq -> orgId.equals(eq.getOrganizationId()))
            .map(eq -> eq.getInstallationId());
    }

    private Mono<Void> checkOverlap(Long currentId, Long solarStringId, Instant validFrom, Instant validUntil) {
        return assignmentRepository
            .findBySolarString(solarStringId)
            .filter(existing -> currentId == null || !existing.getId().equals(currentId))
            .filter(existing -> intervalsOverlap(existing.getValidFrom(), existing.getValidUntil(), validFrom, validUntil))
            .hasElements()
            .flatMap(overlaps ->
                overlaps
                    ? Mono.error(
                        new DomainRuleException(
                            HttpStatus.CONFLICT,
                            "OVERLAPPING_STRING_ASSIGNMENT",
                            "SolarString already has an overlapping MPPT input assignment"
                        )
                    )
                    : Mono.empty()
            );
    }

    private static boolean intervalsOverlap(Instant startA, Instant endA, Instant startB, Instant endB) {
        boolean aEndsBeforeBStarts = endA != null && !endA.isAfter(startB);
        boolean bEndsBeforeAStarts = endB != null && !endB.isAfter(startA);
        return !aEndsBeforeBStarts && !bEndsBeforeAStarts;
    }

    private Mono<StringInputAssignment> findOwnedAssignment(Long id, Long orgId) {
        return assignmentRepository
            .findById(id)
            .filter(a -> orgId.equals(a.getOrganizationId()))
            .switchIfEmpty(
                Mono.error(
                    new DomainRuleException(HttpStatus.NOT_FOUND, "STRING_ASSIGNMENT_NOT_FOUND", "StringInputAssignment not found")
                )
            );
    }
}
