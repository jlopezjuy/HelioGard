import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DemoStore } from '../../infra/mock/demo-store';
import type { WorkOrderStatus } from '../../core/domain/model';
import { arsLabel } from '../../shared/format';

@Component({
  selector: 'app-maintenance',
  imports: [FormsModule],
  templateUrl: './maintenance.html',
})
export class Maintenance {
  protected readonly store = inject(DemoStore);
  protected readonly arsLabel = arsLabel;
  protected readonly note = signal('');
  protected readonly orders = computed(() => {
    const ids = new Set(this.store.installations().map((item) => item.id));
    return this.store.state().workOrders.filter((order) => ids.has(order.installationId));
  });
  protected tasks(orderId: string) {
    return this.store.state().tasks.filter((task) => task.workOrderId === orderId);
  }
  protected next(status: WorkOrderStatus): WorkOrderStatus | null {
    const map: Partial<Record<WorkOrderStatus, WorkOrderStatus>> = {
      CREATED: 'ASSIGNED',
      ASSIGNED: 'ACCEPTED',
      ACCEPTED: 'IN_PROGRESS',
      IN_PROGRESS: 'DONE',
      DONE: 'VERIFIED',
    };
    return map[status] ?? null;
  }
}
