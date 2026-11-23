import { Component, computed, inject } from '@angular/core';
import { visibleInstallations } from '../../core/domain/access';
import { safetyBlockReasons } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-missions',
  templateUrl: './missions.html',
})
export class MissionsPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    return {
      drones: snapshot.drones.filter((item) => item.organizationId === snapshot.session.organizationId),
      missions: snapshot.missions.filter((item) => ids.has(item.installationId)),
      authorizations: snapshot.authorizations,
      verifications: snapshot.verifications.filter((item) => ids.has(item.installationId)),
      blocks: safetyBlockReasons(snapshot.scenario),
      scenario: snapshot.scenario,
    };
  });
}
