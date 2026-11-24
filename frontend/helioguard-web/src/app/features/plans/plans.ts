import { Component, computed, inject } from '@angular/core';
import { formatArs } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import { HgBadge, HgButton, HgCard, HgSkeleton } from '../../shared/ui';

@Component({
  selector: 'app-plans',
  imports: [HgBadge, HgButton, HgCard, HgSkeleton],
  templateUrl: './plans.html',
})
export class PlansPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatArs = formatArs;
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const org = snapshot.session.organizationId;
    return {
      plans: snapshot.maintenancePlans.filter((item) => item.organizationId === org),
      versions: snapshot.planVersions.filter((item) => item.organizationId === org),
      agreements: snapshot.agreements.filter((item) => item.organizationId === org),
      subscriptions: snapshot.subscriptions.filter((item) => item.organizationId === org),
      proposals: snapshot.proposals.filter((item) => item.organizationId === org),
    };
  });
}
