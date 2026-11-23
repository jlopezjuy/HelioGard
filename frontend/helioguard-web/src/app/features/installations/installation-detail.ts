import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { installationAccess } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-installation-detail',
  imports: [RouterLink],
  templateUrl: './installation-detail.html',
})
export class InstallationDetailPage {
  private readonly store = inject(DEMO_REPOSITORY);
  private readonly route = inject(ActivatedRoute);
  protected readonly phase = readyPhase();
  private readonly params = toSignal(this.route.paramMap);
  protected readonly view = computed(() => {
    const id = this.params()?.get('id') ?? '';
    const snapshot = this.store.snapshot();
    const installation = snapshot.installations.find((item) => item.id === id) ?? null;
    const level = installation ? installationAccess(snapshot, installation.id) : 'NONE';
    if (!installation || level === 'NONE') {
      return { missing: true as const };
    }
    const site = snapshot.sites.find((item) => item.id === installation.siteId);
    const arrays = snapshot.arrays.filter((item) => item.installationId === installation.id);
    const arrayIds = new Set(arrays.map((item) => item.id));
    const strings = snapshot.strings.filter((item) => arrayIds.has(item.solarArrayId));
    return {
      missing: false as const,
      installation,
      site,
      level,
      arrays,
      strings,
      equipment: snapshot.equipment.filter((item) => item.installationId === installation.id),
      gateways: snapshot.gateways.filter((item) => item.installationId === installation.id),
      channels: snapshot.channels.filter((item) => snapshot.equipment.some((equipment) => equipment.id === item.equipmentId && equipment.installationId === installation.id)),
      models: snapshot.equipmentModels,
      metrics: snapshot.metrics,
      grants: snapshot.grants.filter((item) => item.installationId === installation.id),
    };
  });

  protected toggle(id: string): void {
    this.store.toggleString(id);
  }
}
