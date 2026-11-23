import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { canSeeEconomics, installationAccess, visibleInstallations } from '../../core/domain/access';
import { SECTOR } from '../../core/domain/model';
import { deficitWh, formatArs, formatWh, wattHoursFromPower } from '../../core/domain/rules';
import { readyPhase } from '../../shared/load';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
})
export class HomePage {
  private readonly store = inject(DEMO_REPOSITORY);
  private readonly route = inject(ActivatedRoute);
  protected readonly phase = readyPhase();
  private readonly params = toSignal(this.route.queryParamMap);
  protected readonly denied = computed(() => this.params()?.get('denegado'));
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const installations = visibleInstallations(snapshot);
    const focus = installations.find((item) => item.id === 'ins-puna') ?? installations[0] ?? null;
    const site = snapshot.sites.find((item) => item.id === focus?.siteId);
    const interval = snapshot.intervals.find((item) => item.id === SECTOR.intervalId);
    const economy = snapshot.economicAssessments.find((item) => item.id === SECTOR.economicId);
    const expectedWh = interval ? wattHoursFromPower(interval.expectedPowerW, interval.durationSeconds) : 0;
    const actualWh = interval?.actualPowerW == null ? null : wattHoursFromPower(interval.actualPowerW, interval.durationSeconds);
    const deficit = deficitWh(expectedWh, actualWh, interval?.comparable ?? false);
    const level = focus ? installationAccess(snapshot, focus.id) : 'NONE';
    const urgent = snapshot.incidents.find((item) => item.installationId === focus?.id && item.status !== 'CLOSED' && item.status !== 'RESOLVED');
    return {
      focus,
      site,
      deficit,
      economy,
      level,
      urgent,
      scenario: snapshot.scenario.lossScenario,
      scenarioLabel: {
        NONE: 'régimen',
        SOILING: 'polvo',
        INVERTER: 'inversor',
        CLOUD: 'nube',
        FROST: 'helada',
        HAIL: 'granizo',
        SMOKE: 'humo',
      }[snapshot.scenario.lossScenario],
      showMoney: focus ? canSeeEconomics(level) : false,
    };
  });
  protected readonly formatWh = formatWh;
  protected readonly formatArs = formatArs;
}
