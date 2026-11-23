import { Component, computed, inject } from '@angular/core';
import type { DroneMissionKind } from '../../core/domain/model';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-missions',
  templateUrl: './missions.html',
})
export class Missions {
  protected readonly store = inject(DemoStore);
  protected readonly missions = computed(() =>
    this.store.state().missions.filter((mission) => mission.organizationId === this.store.state().activeOrganizationId),
  );
  protected readonly drone = computed(() => this.store.state().drones.find((item) => item.organizationId === this.store.state().activeOrganizationId) ?? null);

  protected auth(missionId: string) {
    return this.store.state().authorizations.find((item) => item.missionId === missionId) ?? null;
  }

  protected verification(missionId: string) {
    return this.store.state().verifications.find((item) => item.droneMissionId === missionId) ?? null;
  }

  protected propose(kind: DroneMissionKind): void {
    const incident =
      this.store.state().incidents.find((item) => item.dedupKey === 'soiling-puna-a2') ??
      this.store.state().incidents.find((item) => item.organizationId === this.store.state().activeOrganizationId && item.status !== 'RESOLVED');
    if (!incident) {
      this.store.activateScenario('soiling');
    }
    const current =
      this.store.state().incidents.find((item) => item.dedupKey === 'soiling-puna-a2') ??
      this.store.state().incidents.find((item) => item.organizationId === this.store.state().activeOrganizationId);
    if (!current) {
      return;
    }
    const orderId = this.store.openWorkOrder(current.id);
    if (orderId) {
      this.store.proposeMission(orderId, kind);
    }
  }
}
