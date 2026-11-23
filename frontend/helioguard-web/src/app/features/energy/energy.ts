import { Component, computed, inject } from '@angular/core';
import { canSeeEconomics, visibleInstallations } from '../../core/domain/access';
import { SECTOR } from '../../core/domain/model';
import { deficitWh, formatArs, formatWh, wattHoursFromPower } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-energy',
  templateUrl: './energy.html',
})
export class EnergyPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatWh = formatWh;
  protected readonly formatArs = formatArs;
  protected readonly zones = ['Puna', 'Quebrada', 'Valles', 'Yungas', 'Valles Calchaquíes'];
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const visible = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const intervals = snapshot.intervals.filter((item) => visible.has(item.installationId));
    const run = snapshot.modelRuns.find((item) => item.id === SECTOR.modelRunId);
    const loss = snapshot.lossAssessments.find((item) => item.id === SECTOR.lossId);
    const causes = snapshot.lossCauses.filter((item) => item.lossAssessmentId === SECTOR.lossId);
    const economy = snapshot.economicAssessments.find((item) => item.id === SECTOR.economicId);
    const showMoney = canSeeEconomics(visible.has('ins-puna') ? 'FULL' : 'NONE');
    const points = intervals.map((interval, index) => {
      const expected = wattHoursFromPower(interval.expectedPowerW, interval.durationSeconds);
      const actual = interval.actualPowerW == null ? null : wattHoursFromPower(interval.actualPowerW, interval.durationSeconds);
      return { interval, expected, actual, deficit: deficitWh(expected, actual, interval.comparable), x: 48 + index * 120 };
    });
    return { points, run, loss, causes, economy, showMoney, present: snapshot.sites.map((site) => site.climateZone) };
  });

  protected toggleComparable(): void {
    const interval = this.store.snapshot().intervals.find((item) => item.id === 'iv-b2-1015');
    this.store.comparable('iv-b2-1015', !(interval?.comparable ?? true));
  }
}
