package ar.com.heliguard.app.service.tenant;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.error.DomainRuleException;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Enforces tenant isolation, organization role checks, and delegated installation access grants.
 */
@Service
@Transactional(readOnly = true)
public class PermissionService {

    private static final Set<MembershipRole> MANAGE_ROLES = EnumSet.of(MembershipRole.OWNER, MembershipRole.ADMIN);
    private static final Set<MembershipRole> WRITE_OPERATIONAL_ROLES = EnumSet.of(
        MembershipRole.OWNER,
        MembershipRole.ADMIN,
        MembershipRole.OPERATOR,
        MembershipRole.TECHNICIAN
    );
    private static final Set<MembershipRole> AUDIT_ROLES = EnumSet.of(
        MembershipRole.OWNER,
        MembershipRole.ADMIN,
        MembershipRole.AUDITOR
    );

    private final TenantContextService tenantContextService;
    private final InstallationRepository installationRepository;
    private final InstallationAccessGrantRepository grantRepository;

    public PermissionService(
        TenantContextService tenantContextService,
        InstallationRepository installationRepository,
        InstallationAccessGrantRepository grantRepository
    ) {
        this.tenantContextService = tenantContextService;
        this.installationRepository = installationRepository;
        this.grantRepository = grantRepository;
    }

    public Mono<CurrentTenant> requireManageRole() {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                MANAGE_ROLES.contains(tenant.role())
                    ? Mono.just(tenant)
                    : Mono.error(new DomainRuleException(HttpStatus.FORBIDDEN, "INSUFFICIENT_ROLE", "Owner or admin role required"))
            );
    }

    public Mono<CurrentTenant> requireRoleForScope(PermissionScope scope) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                roleSatisfiesScope(tenant.role(), scope)
                    ? Mono.just(tenant)
                    : Mono.error(
                        new DomainRuleException(
                            HttpStatus.FORBIDDEN,
                            "INSUFFICIENT_ROLE",
                            "Current organization role does not allow scope " + scope
                        )
                    )
            );
    }

    /**
     * Authorizes access to an installation for the current tenant (own org or active non-revoked grant).
     * Uses uniform 404 on missing or unauthorized cross-tenant installations to prevent IDOR enumeration.
     */
    public Mono<Installation> requireAccessToInstallation(Long installationId, PermissionScope requiredScope) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                installationRepository
                    .findById(installationId)
                    .switchIfEmpty(
                        Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INSTALLATION_NOT_FOUND", "Installation not found"))
                    )
                    .flatMap(installation -> checkInstallationAccess(tenant, installation, requiredScope))
            );
    }

    public Mono<Installation> requireOwnedInstallation(Long installationId, Long organizationId) {
        return installationRepository
            .findById(installationId)
            .filter(inst -> organizationId.equals(inst.getOrganizationId()))
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INSTALLATION_NOT_FOUND", "Installation not found")));
    }

    public Mono<Void> validateTemporalWindow(Instant validFrom, Instant validUntil) {
        if (validFrom == null || (validUntil != null && !validUntil.isAfter(validFrom))) {
            return Mono.error(
                new DomainRuleException(HttpStatus.BAD_REQUEST, "INVALID_TEMPORAL_INTERVAL", "validUntil must be strictly after validFrom")
            );
        }
        return Mono.empty();
    }

    public Mono<Void> assertNoOrgReassignment(OrganizationDTO dtoOrg, Long currentOrgId) {
        if (dtoOrg != null && dtoOrg.getId() != null && !currentOrgId.equals(dtoOrg.getId())) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.FORBIDDEN,
                    "ORGANIZATION_REASSIGNMENT_FORBIDDEN",
                    "Reassigning organization is forbidden"
                )
            );
        }
        return Mono.empty();
    }

    public Mono<Boolean> hasValidGrant(Long installationId, Long granteeOrgId, PermissionScope requiredScope, Instant now) {
        return grantRepository
            .findByInstallation(installationId)
            .filter(grant -> isGrantActiveAndSufficient(grant, granteeOrgId, requiredScope, now))
            .hasElements();
    }

    private Mono<Installation> checkInstallationAccess(
        CurrentTenant tenant,
        Installation installation,
        PermissionScope requiredScope
    ) {
        if (!roleSatisfiesScope(tenant.role(), requiredScope)) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.FORBIDDEN,
                    "INSUFFICIENT_ROLE",
                    "Current organization role does not allow scope " + requiredScope
                )
            );
        }
        if (tenant.organizationId().equals(installation.getOrganizationId())) {
            return Mono.just(installation);
        }
        return hasValidGrant(installation.getId(), tenant.organizationId(), requiredScope, Instant.now())
            .flatMap(granted ->
                granted
                    ? Mono.just(installation)
                    : Mono.error(new DomainRuleException(HttpStatus.NOT_FOUND, "INSTALLATION_NOT_FOUND", "Installation not found"))
            );
    }

    public static boolean isGrantActiveAndSufficient(
        InstallationAccessGrant grant,
        Long granteeOrgId,
        PermissionScope requiredScope,
        Instant now
    ) {
        if (grant.getGranteeOrganizationId() == null || !grant.getGranteeOrganizationId().equals(granteeOrgId)) {
            return false;
        }
        if (grant.getRevokedAt() != null) {
            return false;
        }
        if (grant.getValidFrom() == null || grant.getValidFrom().isAfter(now)) {
            return false;
        }
        if (grant.getValidUntil() != null && !grant.getValidUntil().isAfter(now)) {
            return false;
        }
        return scopeSatisfies(grant.getScope(), requiredScope);
    }

    public static boolean scopeSatisfies(PermissionScope granted, PermissionScope required) {
        if (granted == null || required == null) {
            return false;
        }
        return scopeRank(granted) >= scopeRank(required);
    }

    public static boolean roleSatisfiesScope(MembershipRole role, PermissionScope scope) {
        if (role == null || scope == null) {
            return false;
        }
        return switch (scope) {
            case READ_ONLY -> true;
            case AUDIT -> AUDIT_ROLES.contains(role);
            case MAINTENANCE -> WRITE_OPERATIONAL_ROLES.contains(role);
            case MANAGE -> MANAGE_ROLES.contains(role);
        };
    }

    private static int scopeRank(PermissionScope scope) {
        return switch (scope) {
            case READ_ONLY -> 1;
            case AUDIT -> 2;
            case MAINTENANCE -> 3;
            case MANAGE -> 4;
        };
    }
}
