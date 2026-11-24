package ar.com.heliguard.app.service.tenant;

import ar.com.heliguard.app.domain.enumeration.MembershipRole;

/**
 * Resolved and DB-validated tenant context for the current authenticated request.
 */
public record CurrentTenant(Long organizationId, Long userId, String userLogin, MembershipRole role) {}
