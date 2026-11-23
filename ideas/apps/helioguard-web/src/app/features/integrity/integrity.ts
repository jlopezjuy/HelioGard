import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-integrity',
  imports: [RouterLink],
  templateUrl: './integrity.html',
})
export class Integrity {
  protected readonly store = inject(DemoStore);
  protected readonly events = computed(() => {
    const allowed = new Set(this.store.installations().filter((item) => this.store.state().organizations.some((org) => org.id === this.store.state().activeOrganizationId)).map((item) => item.organizationId === this.store.state().activeOrganizationId ? item.id : ''));
    return this.store.state().integrityEvents.filter((event) => event.organizationId === this.store.state().activeOrganizationId);
  });
  protected assessment(eventId: string) {
    return this.store.state().integrityAssessments.find((item) => item.integrityEventId === eventId) ?? null;
  }
  protected cases = computed(() => this.store.state().securityIncidents.filter((item) => item.organizationId === this.store.state().activeOrganizationId));
  protected device = computed(() => this.store.state().devices.find((item) => item.organizationId === this.store.state().activeOrganizationId) ?? null);
}
