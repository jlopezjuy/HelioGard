import { Component, computed, inject } from '@angular/core';
import { visibleInstallations } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-maintenance',
  templateUrl: './maintenance.html',
})
export class MaintenancePage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const orders = snapshot.workOrders.filter((item) => ids.has(item.installationId));
    const orderIds = new Set(orders.map((item) => item.id));
    const interventions = snapshot.interventions.filter((item) => orderIds.has(item.workOrderId));
    const interventionIds = new Set(interventions.map((item) => item.id));
    return {
      orders,
      tasks: snapshot.tasks.filter((item) => orderIds.has(item.workOrderId)),
      interventions,
      evidence: snapshot.interventionEvidence.filter((item) => interventionIds.has(item.interventionId)),
      recoveries: snapshot.recoveries.filter((item) => interventionIds.has(item.interventionId)),
    };
  });
}
