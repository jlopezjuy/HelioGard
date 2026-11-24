import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { visibleInstallations } from '../../core/domain/access';
import { formatLocal } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import { HgAlert, HgBadge, HgCard, HgEmpty, HgSearch, HgSkeleton, HgTable } from '../../shared/ui';

@Component({
  selector: 'app-integrity',
  imports: [RouterLink, HgAlert, HgBadge, HgCard, HgEmpty, HgSearch, HgSkeleton, HgTable],
  templateUrl: './integrity.html',
})
export class IntegrityPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatLocal = formatLocal;
  protected readonly query = signal('');
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const q = this.query().trim().toLowerCase();
    const allEvents = snapshot.integrityEvents.filter((item) => ids.has(item.installationId));
    const events = q
      ? allEvents.filter(
          (item) =>
            item.eventType.toLowerCase().includes(q) ||
            item.status.toLowerCase().includes(q) ||
            item.deviceId.toLowerCase().includes(q) ||
            item.detail.toLowerCase().includes(q)
        )
      : allEvents;
    return {
      devices: snapshot.devices.filter((item) => ids.has(snapshot.equipment.find((equipment) => equipment.id === item.equipmentId)?.installationId ?? '') || item.organizationId === snapshot.session.organizationId),
      events,
      assessments: snapshot.integrityAssessments.filter((item) => ids.has(item.installationId)),
      security: snapshot.securityIncidents.filter((item) => ids.has(item.installationId)),
      proof: snapshot.scenario.lastProof,
    };
  });
}
