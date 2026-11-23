import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { visibleInstallations } from '../../core/domain/access';
import { formatLocal } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-integrity',
  imports: [RouterLink],
  templateUrl: './integrity.html',
})
export class IntegrityPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatLocal = formatLocal;
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    return {
      devices: snapshot.devices.filter((item) => ids.has(snapshot.equipment.find((equipment) => equipment.id === item.equipmentId)?.installationId ?? '') || item.organizationId === snapshot.session.organizationId),
      events: snapshot.integrityEvents.filter((item) => ids.has(item.installationId)),
      assessments: snapshot.integrityAssessments.filter((item) => ids.has(item.installationId)),
      security: snapshot.securityIncidents.filter((item) => ids.has(item.installationId)),
      proof: snapshot.scenario.lastProof,
    };
  });
}
