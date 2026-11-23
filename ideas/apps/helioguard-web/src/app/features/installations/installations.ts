import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { installationScope } from '../../core/domain/access';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-installations',
  imports: [RouterLink],
  templateUrl: './installations.html',
})
export class Installations {
  protected readonly store = inject(DemoStore);
  protected readonly rows = computed(() =>
    this.store.installations().map((installation) => ({
      installation,
      site: this.store.state().sites.find((site) => site.id === installation.siteId),
      scope: installationScope(this.store.state(), installation),
    })),
  );
}
