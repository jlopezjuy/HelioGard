import { Component, computed, inject } from '@angular/core';
import { installationAccess } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import { HgAlert, HgBadge, HgButton, HgCard, HgSkeleton, HgTable } from '../../shared/ui';

@Component({
  selector: 'app-organizations',
  imports: [HgAlert, HgBadge, HgButton, HgCard, HgSkeleton, HgTable],
  templateUrl: './organizations.html',
})
export class OrganizationsPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly denied = computed(() => {
    const snapshot = this.store.snapshot();
    return installationAccess(snapshot, 'ins-salta') === 'NONE';
  });
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    return {
      organizations: snapshot.organizations,
      memberships: snapshot.memberships.filter((item) => item.organizationId === snapshot.session.organizationId),
      users: snapshot.users,
      grants: snapshot.grants.filter((item) => item.granteeOrganizationId === snapshot.session.organizationId || item.organizationId === snapshot.session.organizationId),
      current: snapshot.session.organizationId,
    };
  });

  protected nameOf(userId: string): string {
    return this.store.snapshot().users.find((user) => user.id === userId)?.name ?? userId;
  }

  protected probe(): void {
    const level = installationAccess(this.store.snapshot(), 'ins-salta');
    this.store.note(
      level === 'NONE'
        ? 'Lectura de Calchaquí Oeste denegada. La sesión no tiene membresía ni grant sobre Sol del Norte Salta.'
        : 'La sesión sí alcanza Calchaquí Oeste.',
    );
  }
}
