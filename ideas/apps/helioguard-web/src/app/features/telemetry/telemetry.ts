import { Component, computed, inject } from '@angular/core';
import { DemoStore } from '../../infra/mock/demo-store';
import { powerLabel } from '../../shared/format';

@Component({
  selector: 'app-telemetry',
  imports: [],
  templateUrl: './telemetry.html',
})
export class Telemetry {
  protected readonly store = inject(DemoStore);
  protected readonly powerLabel = powerLabel;
  protected readonly pending = computed(() => this.store.state().outbox.filter((item) => item.syncedAt === null));
  protected readonly gateway = computed(() => this.store.state().gateways.find((item) => item.id === 'gw-puna') ?? null);
}
