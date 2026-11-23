import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DemoStore } from '../../infra/mock/demo-store';
import { arsLabel, whLabel } from '../../shared/format';

@Component({
  selector: 'app-economy',
  imports: [FormsModule],
  templateUrl: './economy.html',
})
export class Economy {
  protected readonly store = inject(DemoStore);
  protected readonly arsLabel = arsLabel;
  protected readonly whLabel = whLabel;
  protected readonly purchase = signal(200);
  protected readonly exported = signal(80);
  protected readonly rates = computed(() =>
    this.store.state().tariffRates.filter((rate) => rate.organizationId === this.store.state().activeOrganizationId),
  );
  protected readonly assessments = computed(() =>
    this.store.state().economicAssessments.filter((item) => item.organizationId === this.store.state().activeOrganizationId),
  );
  protected save(rateId: string): void {
    this.store.updateTariff(rateId, this.purchase(), this.exported());
  }
}
