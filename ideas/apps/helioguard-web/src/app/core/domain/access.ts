import type { DemoState, Installation, InstallationAccessGrant, MembershipRole, PermissionScope } from './model';

export type AccessScope = PermissionScope | 'FULL' | 'NONE';

const ROLE_ROUTES: Record<string, MembershipRole[]> = {
  '/inicio': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'],
  '/parque': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'],
  '/instalaciones': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'],
  '/telemetria': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'],
  '/energia': ['OWNER', 'ADMIN', 'OPERATOR'],
  '/integridad': ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'],
  '/fuentes': ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'],
  '/incidentes': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR'],
  '/misiones': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'],
  '/auditorias': ['OWNER', 'ADMIN', 'AUDITOR', 'TECHNICIAN'],
  '/mantenimiento': ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'],
  '/economia': ['OWNER', 'ADMIN', 'OPERATOR'],
  '/planes': ['OWNER', 'ADMIN'],
  '/organizaciones': ['OWNER', 'ADMIN'],
  '/configuracion': ['OWNER', 'ADMIN', 'OPERATOR'],
};

export function routeRoles(path: string): MembershipRole[] {
  const base = '/' + path.split('/').filter(Boolean)[0];
  return ROLE_ROUTES[base] ?? [];
}

export function roleAllows(path: string, role: MembershipRole | null): boolean {
  if (!role) {
    return false;
  }
  return routeRoles(path).includes(role);
}

export function activeMembership(state: DemoState) {
  return (
    state.memberships.find(
      (membership) =>
        membership.userId === state.activeUserId &&
        membership.organizationId === state.activeOrganizationId &&
        membership.status === 'ACTIVE',
    ) ?? null
  );
}

export function grantIsActive(grant: InstallationAccessGrant, nowIso: string): boolean {
  if (grant.revokedAt) {
    return false;
  }
  if (grant.validFrom > nowIso) {
    return false;
  }
  if (grant.validUntil && grant.validUntil < nowIso) {
    return false;
  }
  return true;
}

export function installationScope(state: DemoState, installation: Installation): AccessScope {
  if (installation.organizationId === state.activeOrganizationId) {
    return 'FULL';
  }
  const grant = state.grants.find(
    (item) =>
      item.granteeOrganizationId === state.activeOrganizationId &&
      item.installationId === installation.id &&
      grantIsActive(item, state.clockIso),
  );
  return grant ? grant.scope : 'NONE';
}

export function visibleInstallations(state: DemoState): Installation[] {
  return state.installations.filter((installation) => installationScope(state, installation) !== 'NONE');
}
