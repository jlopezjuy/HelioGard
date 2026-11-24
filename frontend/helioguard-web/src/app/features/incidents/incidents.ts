import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { visibleInstallations } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import { HgAlert, HgBadge, HgButton, HgCard, HgEmpty, HgSkeleton } from '../../shared/ui';

@Component({
  selector: 'app-incidents',
  imports: [RouterLink, HgAlert, HgBadge, HgButton, HgCard, HgEmpty, HgSkeleton],
  templateUrl: './incidents.html',
})
export class IncidentsPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly onlyOpen = signal(true);
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const energy = snapshot.incidents.filter((item) => item.domain === 'ENERGY' && ids.has(item.installationId) && (!this.onlyOpen() || (item.status !== 'CLOSED' && item.status !== 'RESOLVED')));
    const security = snapshot.securityIncidents.filter((item) => ids.has(item.installationId));
    const rules = snapshot.alertRules.filter((item) => !item.installationId || ids.has(item.installationId));
    return { energy, security, rules };
  });

  protected acknowledge(id: string): void {
    this.store.acknowledge(id);
  }
}
