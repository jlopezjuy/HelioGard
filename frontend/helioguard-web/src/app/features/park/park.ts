import { DecimalPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { installationAccess, visibleInstallations } from '../../core/domain/access';
import { LIVE } from '../../core/domain/model';
import { formatWh, wattHoursFromPower } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-park',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './park.html',
})
export class ParkPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatWh = formatWh;
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const installations = visibleInstallations(snapshot);
    const focus = installations.find((item) => item.id === 'ins-puna') ?? installations[0] ?? null;
    const blocks = snapshot.arrays.filter((array) => array.installationId === focus?.id);
    const selected = snapshot.arrays.find((array) => array.id === snapshot.scenario.selectedArrayId && array.installationId === focus?.id) ?? blocks[0] ?? null;
    const strings = snapshot.strings.filter((item) => item.solarArrayId === selected?.id);
    const equipment = snapshot.equipment.filter((item) => strings.some((row) => row.id === item.solarStringId));
    const mission = snapshot.missions.find((item) => item.id === LIVE.missionId);
    const interval = snapshot.intervals.find((item) => item.id === 'iv-b2-1030');
    return {
      focus,
      blocks,
      selected,
      strings,
      equipment,
      mission,
      soiling: snapshot.scenario.soiling,
      progress: snapshot.scenario.droneProgress,
      level: focus ? installationAccess(snapshot, focus.id) : 'NONE',
      actualWh: interval?.actualPowerW == null ? null : wattHoursFromPower(interval.actualPowerW, interval.durationSeconds),
      expectedWh: interval ? wattHoursFromPower(interval.expectedPowerW, interval.durationSeconds) : 0,
    };
  });

  protected stateOf(arrayId: string): string {
    const snapshot = this.store.snapshot();
    if (arrayId === 'arr-b2') {
      const labels = {
        NONE: 'Operativo',
        SOILING: 'Suciedad sospechada',
        INVERTER: 'Inversor por debajo',
        CLOUD: 'Nube transitoria',
        FROST: 'Helada en el intervalo',
        HAIL: 'Granizo simulado',
        SMOKE: 'Humo o sombreado',
      } as const;
      return labels[snapshot.scenario.lossScenario];
    }
    const equipment = snapshot.equipment.find((item) => item.installationId === 'ins-salta' && item.status === 'FAILED');
    if (arrayId === 'arr-salta' && equipment) {
      return 'Inversor en falla';
    }
    return 'Operativo';
  }

  protected select(arrayId: string): void {
    this.store.select(arrayId);
  }

  protected isSelected(arrayId: string): boolean {
    return this.view().selected?.id === arrayId;
  }

  protected droneLeft(): number {
    return 8 + this.view().progress * 58;
  }

  protected droneTop(): number {
    const progress = this.view().progress;
    return progress < 0.5 ? 22 : 22 + ((progress - 0.5) / 0.5) * 46;
  }
}
