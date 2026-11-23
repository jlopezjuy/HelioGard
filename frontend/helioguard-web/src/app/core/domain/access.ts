import type { DemoSnapshot, Installation, MembershipRole, PermissionScope } from './model';

export type AccessLevel = 'NONE' | 'READ' | 'MAINTENANCE' | 'FULL';

export function roleInOrganization(
  snapshot: DemoSnapshot,
  userId: string,
  organizationId: string,
): MembershipRole | null {
  const membership = snapshot.memberships.find(
    (item) => item.userId === userId && item.organizationId === organizationId && item.status === 'ACTIVE',
  );
  return membership?.role ?? null;
}

export function currentRole(snapshot: DemoSnapshot): MembershipRole | null {
  return roleInOrganization(snapshot, snapshot.session.userId, snapshot.session.organizationId);
}

function rank(scope: PermissionScope): AccessLevel {
  if (scope === 'MANAGE') {
    return 'FULL';
  }
  if (scope === 'MAINTENANCE') {
    return 'MAINTENANCE';
  }
  return 'READ';
}

export function installationAccess(snapshot: DemoSnapshot, installationId: string): AccessLevel {
  const installation = snapshot.installations.find((item) => item.id === installationId);
  if (!installation) {
    return 'NONE';
  }
  const role = roleInOrganization(snapshot, snapshot.session.userId, installation.organizationId);
  if (role && snapshot.session.organizationId === installation.organizationId) {
    return 'FULL';
  }
  const now = snapshot.clockIso;
  const grant = snapshot.grants.find((item) => {
    if (item.installationId !== installationId || item.revokedAt) {
      return false;
    }
    if (item.granteeOrganizationId !== snapshot.session.organizationId) {
      return false;
    }
    if (item.validFrom > now) {
      return false;
    }
    if (item.validUntil && item.validUntil < now) {
      return false;
    }
    return true;
  });
  return grant ? rank(grant.scope) : 'NONE';
}

export function visibleInstallations(snapshot: DemoSnapshot): Installation[] {
  return snapshot.installations.filter((item) => installationAccess(snapshot, item.id) !== 'NONE');
}

export function canSeeEconomics(level: AccessLevel): boolean {
  return level === 'FULL';
}
