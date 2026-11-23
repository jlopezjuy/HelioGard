import { Component, computed, inject } from '@angular/core';
import { DemoStore } from '../../infra/mock/demo-store';
import { whLabel } from '../../shared/format';

@Component({
  selector: 'app-energy',
  imports: [],
  templateUrl: './energy.html',
})
export class Energy {
  protected readonly store = inject(DemoStore);
  protected readonly whLabel = whLabel;
  protected readonly runs = computed(() => {
    const allowed = new Set(this.store.installations().map((item) => item.id));
    return this.store.state().modelRuns.filter((run) => allowed.has(run.installationId));
  });
  protected readonly causes = computed(() => this.store.state().lossCauses);
  protected causeOf(lossId: string) {
    return this.store.state().lossCauses.filter((cause) => cause.lossAssessmentId === lossId);
  }
  protected lossOf(runId: string) {
    return this.store.state().lossAssessments.find((loss) => loss.modelRunId === runId) ?? null;
  }
}
