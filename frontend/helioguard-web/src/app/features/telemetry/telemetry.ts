import { Component, computed, inject } from '@angular/core';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-telemetry',
  templateUrl: './telemetry.html',
})
export class TelemetryPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    return {
      failure: snapshot.scenario.readFailure,
      offline: snapshot.scenario.wanOffline,
      gateway: snapshot.gateways.find((item) => item.id === 'gw-1'),
      channel: snapshot.channels.find((item) => item.id === 'ch-inv-03'),
      metric: snapshot.metrics.find((item) => item.id === 'm-power'),
      buffer: snapshot.scenario.edgeBuffer,
      ledger: snapshot.scenario.ledger,
      proof: snapshot.scenario.lastProof,
      syncCount: snapshot.scenario.syncCount,
    };
  });

  protected retry(): void {
    this.store.flag('readFailure', false);
  }
}
