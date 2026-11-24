import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { visibleInstallations } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import { HgBadge, HgCard, HgEmpty, HgSensor, HgSkeleton } from '../../shared/ui';

@Component({
  selector: 'app-installations',
  imports: [RouterLink, HgBadge, HgCard, HgEmpty, HgSensor, HgSkeleton],
  templateUrl: './installations.html',
})
export class InstallationsPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly rows = computed(() => {
    const snapshot = this.store.snapshot();
    return visibleInstallations(snapshot).map((installation) => ({
      installation,
      site: snapshot.sites.find((site) => site.id === installation.siteId),
      gateways: snapshot.gateways.filter((gateway) => gateway.installationId === installation.id),
    }));
  });
}
