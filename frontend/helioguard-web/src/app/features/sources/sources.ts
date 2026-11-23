import { Component, computed, inject } from '@angular/core';
import { visibleInstallations } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-sources',
  templateUrl: './sources.html',
})
export class SourcesPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    return {
      sources: snapshot.evidenceSources,
      rows: snapshot.provenance.filter((item) => ids.has(item.installationId)),
    };
  });
}
