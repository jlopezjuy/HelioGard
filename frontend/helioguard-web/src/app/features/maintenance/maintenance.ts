import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { visibleInstallations } from '../../core/domain/access';
import {
  INTERVENTION_ROUTES,
  INTERVENTION_ROUTE_LIST,
  InterventionAccess,
  InterventionRouteId,
  InterventionWeather,
  computeInterventionMetrics,
  evaluateInterventionSafety,
} from '../../core/domain/intervention-sim';
import { formatArs } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';
import {
  HgAlert,
  HgBadge,
  HgButton,
  HgCard,
  HgControl,
  HgEmpty,
  HgField,
  HgKpi,
  HgSkeleton,
} from '../../shared/ui';

@Component({
  selector: 'app-maintenance',
  imports: [DecimalPipe, HgAlert, HgBadge, HgButton, HgCard, HgControl, HgEmpty, HgField, HgKpi, HgSkeleton],
  templateUrl: './maintenance.html',
})
export class MaintenancePage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatArs = formatArs;

  protected readonly routes = INTERVENTION_ROUTE_LIST;
  protected readonly routeId = signal<InterventionRouteId>('drone');
  protected readonly weather = signal<InterventionWeather>('clear');
  protected readonly access = signal<InterventionAccess>('ground');
  protected readonly dustPct = signal(20);
  protected readonly tariffArs = signal(180);

  protected readonly interventionSpec = computed(() => INTERVENTION_ROUTES[this.routeId()]);
  protected readonly interventionMetrics = computed(() =>
    computeInterventionMetrics(this.routeId(), this.dustPct(), this.tariffArs()),
  );
  protected readonly interventionSafety = computed(() =>
    evaluateInterventionSafety(this.routeId(), this.weather(), this.access()),
  );

  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const orders = snapshot.workOrders.filter((item) => ids.has(item.installationId));
    const orderIds = new Set(orders.map((item) => item.id));
    const interventions = snapshot.interventions.filter((item) => orderIds.has(item.workOrderId));
    const interventionIds = new Set(interventions.map((item) => item.id));
    return {
      orders,
      tasks: snapshot.tasks.filter((item) => orderIds.has(item.workOrderId)),
      interventions,
      evidence: snapshot.interventionEvidence.filter((item) => interventionIds.has(item.interventionId)),
      recoveries: snapshot.recoveries.filter((item) => interventionIds.has(item.interventionId)),
    };
  });
}
