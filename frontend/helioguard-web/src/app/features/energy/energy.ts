import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { canSeeEconomics, visibleInstallations } from '../../core/domain/access';
import { DiurnalMode, DiurnalStatus } from '../../core/domain/diurnal-sim';
import { NOA_REGIONS, NOA_REGION_LIST, NoaRegionId, simulateNoaDay } from '../../core/domain/noa-regional';
import { SECTOR } from '../../core/domain/model';
import { deficitWh, formatArs, formatWh, wattHoursFromPower } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { DiurnalSimulatorService } from '../../infra/mock/diurnal-simulator.service';
import { readyPhase } from '../../shared/load';
import {
  HgAlert,
  HgBadge,
  HgButton,
  HgCard,
  HgChartFrame,
  HgControl,
  HgField,
  HgKpi,
  HgProgress,
  HgSensor,
  HgSkeleton,
  HgTable,
  HgTabs,
} from '../../shared/ui';

@Component({
  selector: 'app-energy',
  imports: [
    DecimalPipe,
    HgAlert,
    HgBadge,
    HgButton,
    HgCard,
    HgChartFrame,
    HgControl,
    HgField,
    HgKpi,
    HgProgress,
    HgSensor,
    HgSkeleton,
    HgTable,
    HgTabs,
  ],
  templateUrl: './energy.html',
})
export class EnergyPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly sim = inject(DiurnalSimulatorService);
  protected readonly phase = readyPhase();
  protected readonly formatWh = formatWh;
  protected readonly formatArs = formatArs;
  protected readonly zones = ['Puna', 'Quebrada', 'Valles', 'Yungas', 'Valles Calchaquíes'];
  protected readonly tab = signal<'grafico' | 'tabla' | 'diurna' | 'noa'>('grafico');
  protected readonly tabs = [
    { id: 'grafico', label: 'Gráfico Wh' },
    { id: 'tabla', label: 'Tabla de intervalos' },
    { id: 'diurna', label: 'Simulación diurna (07:00–19:00)' },
    { id: 'noa', label: 'Clima NOA (ampliación)' },
  ] as const;

  protected readonly noaRegions = NOA_REGION_LIST;
  protected readonly noaRegionId = signal<NoaRegionId>('abra');
  protected readonly noaKwp = signal(6.6);
  protected readonly noaSoiling = signal(18);
  protected readonly noaCloud = signal(8);
  protected readonly noaFrost = signal(true);
  protected readonly noaStorm = signal(false);
  protected readonly noaFailure = signal(false);

  protected readonly noaResult = computed(() =>
    simulateNoaDay({
      regionId: this.noaRegionId(),
      kwp: this.noaKwp(),
      soilingPct: this.noaSoiling(),
      cloudPct: this.noaCloud(),
      windKmH: NOA_REGIONS[this.noaRegionId()].wind,
      frost: this.noaFrost(),
      failure: this.noaFailure(),
      storm: this.noaStorm(),
    }),
  );

  protected readonly noaChart = computed(() => {
    const res = this.noaResult();
    const yMax = Math.max(this.noaKwp() * 1.1, 1);
    const xOf = (m: number) => 42 + ((m - 390) / 750) * 490;
    const yOf = (kw: number) => 185 - (Math.max(0, kw) / yMax) * 160;
    return {
      yMax,
      expectedLine: res.points.map((p) => `${xOf(p.minute).toFixed(1)},${yOf(p.expectedKw).toFixed(1)}`).join(' '),
      actualLine: res.points.map((p) => `${xOf(p.minute).toFixed(1)},${yOf(p.powerKw).toFixed(1)}`).join(' '),
    };
  });

  protected selectNoaRegion(id: string): void {
    if (id in NOA_REGIONS) {
      const region = NOA_REGIONS[id as NoaRegionId];
      this.noaRegionId.set(region.id);
      this.noaCloud.set(region.cloud);
      this.noaSoiling.set(region.soil);
      this.noaFrost.set(region.frost);
    }
  }

  protected readonly modes: readonly { id: DiurnalMode; label: string }[] = [
    { id: 'normal', label: 'Día normal' },
    { id: 'suciedad', label: 'Ensuciar panel' },
    { id: 'replay', label: 'Ataque replay' },
    { id: 'inyeccion', label: 'Inyección falsa' },
  ];

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

  protected readonly diurnalChart = computed(() => {
    const s = this.sim.state();
    const maxSeriesKw = s.serie.reduce((m, r) => Math.max(m, r.potenciaKw, r.esperadaKw), 0);
    const yMax = Math.max(s.peakKw * 1.15, maxSeriesKw * 1.05, 1);
    const xOf = (hora: number) => 42 + ((Math.max(7, Math.min(19, hora)) - 7) / 12) * 490;
    const yOf = (kw: number) => 185 - (Math.max(0, kw) / yMax) * 160;
    const expectedLine = s.serie.map((r) => `${xOf(r.hora).toFixed(1)},${yOf(r.esperadaKw).toFixed(1)}`).join(' ');
    const actualLine = s.serie.map((r) => `${xOf(r.hora).toFixed(1)},${yOf(r.potenciaKw).toFixed(1)}`).join(' ');
    return { yMax, expectedLine, actualLine };
  });

  protected sensorTone(status: DiurnalStatus): 'ok' | 'watch' | 'alert' {
    if (status === 'rojo') return 'alert';
    if (status === 'amarillo') return 'watch';
    return 'ok';
  }

  protected statusLabel(status: DiurnalStatus): string {
    if (status === 'rojo') return 'Alerta de integridad';
    if (status === 'amarillo') return 'Rendimiento bajo';
    return 'En régimen simulado';
  }

  protected onPeakChange(raw: string): void {
    const val = Number(raw);
    if (Number.isFinite(val) && val > 0) {
      this.sim.updateConfig({ peakKw: val });
    }
  }

  protected onTariffChange(raw: string): void {
    const val = Number(raw);
    if (Number.isFinite(val) && val >= 0) {
      this.sim.updateConfig({ tariffArsPerKwh: val });
    }
  }

  protected toggleComparable(): void {
    const interval = this.store.snapshot().intervals.find((item) => item.id === 'iv-b2-1015');
    this.store.comparable('iv-b2-1015', !(interval?.comparable ?? true));
  }
}
