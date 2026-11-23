import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-incidents',
  imports: [RouterLink],
  templateUrl: './incidents.html',
})
export class Incidents {
  protected readonly store = inject(DemoStore);
  protected readonly rows = computed(() => {
    const ids = new Set(this.store.installations().map((item) => item.id));
    return this.store.state().incidents.filter((item) => ids.has(item.installationId));
  });
  protected events(incidentId: string) {
    return this.store.state().incidentEvents.filter((item) => item.incidentId === incidentId);
  }
}
