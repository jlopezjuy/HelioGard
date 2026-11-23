import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { installationScope } from '../../core/domain/access';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-installation-detail',
  imports: [RouterLink],
  templateUrl: './installation-detail.html',
})
export class InstallationDetail {
  private readonly route = inject(ActivatedRoute);
  protected readonly store = inject(DemoStore);
  private readonly id = toSignal(this.route.paramMap.pipe(map((params) => params.get('id'))), { initialValue: null });
  protected readonly view = computed(() => {
    const id = this.id();
    const installation = this.store.state().installations.find((item) => item.id === id) ?? null;
    if (!installation) {
      return { kind: 'missing' as const };
    }
    const scope = installationScope(this.store.state(), installation);
    if (scope === 'NONE') {
      return { kind: 'denied' as const, installation };
    }
    const state = this.store.state();
    return {
      kind: 'ok' as const,
      installation,
      scope,
      site: state.sites.find((item) => item.id === installation.siteId) ?? null,
      arrays: state.arrays.filter((item) => item.installationId === installation.id),
      equipment: state.equipment.filter((item) => item.installationId === installation.id),
      gateways: state.gateways.filter((item) => item.installationId === installation.id),
      strings: state.strings.filter((item) => state.arrays.some((array) => array.id === item.solarArrayId && array.installationId === installation.id)),
      channels: state.channels.filter((channel) => state.equipment.some((item) => item.id === channel.equipmentId && item.installationId === installation.id)),
    };
  });
}
