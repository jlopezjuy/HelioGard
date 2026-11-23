import { Component, computed, inject } from '@angular/core';
import { visibleInstallations } from '../../core/domain/access';
import { LIVE } from '../../core/domain/model';
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
  protected readonly steps = [
    { code: 'DRAFT', label: 'Borrador' },
    { code: 'SAFETY_REVIEW', label: 'Seguridad' },
    { code: 'AWAITING_AUTHORIZATION', label: 'Espera permiso' },
    { code: 'AUTHORIZED', label: 'Autorizada' },
    { code: 'DISPATCHED', label: 'Despachada' },
    { code: 'IN_FLIGHT', label: 'En recorrido' },
    { code: 'COMPLETED', label: 'Cerrada' },
  ] as const;
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    return {
      drones: snapshot.drones.filter((item) => item.organizationId === snapshot.session.organizationId),
      missions: snapshot.missions.filter((item) => ids.has(item.installationId)),
      live: snapshot.missions.find((item) => item.id === LIVE.missionId) ?? null,
      authorizations: snapshot.authorizations,
      verifications: snapshot.verifications.filter((item) => ids.has(item.installationId)),
      blocks: safetyBlockReasons(snapshot.scenario),
      scenario: snapshot.scenario,
    };
  });
}
