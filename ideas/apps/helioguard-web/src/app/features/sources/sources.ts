import { Component, inject } from '@angular/core';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-sources',
  templateUrl: './sources.html',
})
export class Sources {
  protected readonly store = inject(DemoStore);
  protected source(id: string) {
    return this.store.state().evidenceSources.find((item) => item.id === id) ?? null;
  }
  protected rows() {
    return this.store.state().provenance.filter((item) => item.organizationId === this.store.state().activeOrganizationId);
  }
}
