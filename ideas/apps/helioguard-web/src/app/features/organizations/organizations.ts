import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-organizations',
  imports: [RouterLink],
  templateUrl: './organizations.html',
})
export class Organizations {
  protected readonly store = inject(DemoStore);
  protected readonly memberships = computed(() =>
    this.store.state().memberships.filter((item) => item.organizationId === this.store.state().activeOrganizationId),
  );
  protected user(id: string) {
    return this.store.state().users.find((item) => item.id === id)?.name ?? id;
  }
  protected readonly grants = computed(() =>
    this.store.state().grants.filter(
      (grant) =>
        grant.organizationId === this.store.state().activeOrganizationId ||
        grant.granteeOrganizationId === this.store.state().activeOrganizationId,
    ),
  );
  protected org(id: string) {
    return this.store.state().organizations.find((item) => item.id === id)?.name ?? id;
  }
}
