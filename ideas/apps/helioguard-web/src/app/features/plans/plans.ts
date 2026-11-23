import { Component, computed, inject } from '@angular/core';
import { DemoStore } from '../../infra/mock/demo-store';
import { arsLabel } from '../../shared/format';

@Component({
  selector: 'app-plans',
  templateUrl: './plans.html',
})
export class Plans {
  protected readonly store = inject(DemoStore);
  protected readonly arsLabel = arsLabel;
  protected readonly plans = computed(() =>
    this.store.state().maintenancePlans.filter((plan) => plan.organizationId === this.store.state().activeOrganizationId),
  );
  protected versions(planId: string) {
    return this.store.state().planVersions.filter((version) => version.maintenancePlanId === planId);
  }
  protected proposals() {
    return this.store.state().proposals.filter((item) => item.organizationId === this.store.state().activeOrganizationId);
  }
  protected subscriptions() {
    return this.store.state().subscriptions.filter((item) => item.organizationId === this.store.state().activeOrganizationId);
  }
}
