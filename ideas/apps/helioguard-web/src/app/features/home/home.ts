import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { installationScope } from '../../core/domain/access';
import { installationSeries } from '../../core/domain/rules';
import { DemoStore } from '../../infra/mock/demo-store';
import { arsLabel, powerLabel, whLabel } from '../../shared/format';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
})
export class Home {
  protected readonly store = inject(DemoStore);
  protected readonly whLabel = whLabel;
  protected readonly arsLabel = arsLabel;
  protected readonly incident = computed(() => {
    const selected = this.store.selected();
    if (!selected) {
      return null;
    }
    return (
      this.store
        .state()
        .incidents.find(
          (item) => item.installationId === selected.id && item.status !== 'CLOSED' && item.status !== 'RESOLVED',
        ) ?? null
    );
  });
  protected readonly live = computed(() => this.store.state().economicAssessments.find((item) => item.id === 'eco-puna-live') ?? null);
  protected readonly scope = computed(() => {
    const selected = this.store.selected();
    return selected ? installationScope(this.store.state(), selected) : 'NONE';
  });
  protected readonly headline = computed(() => {
    const selected = this.store.selected();
    const scenario = this.store.state().scenario;
    if (!selected) {
      return 'No hay instalaciones en esta sesión';
    }
    if (selected.id !== 'ins-puna') {
      return this.incident()?.title ?? `${selected.name} está en seguimiento`;
    }
    if (scenario === 'soiling' && !this.store.state().recoveryApplied) {
      return 'El bloque A2 entrega menos de lo que el modelo espera';
    }
    if (scenario === 'inverter') {
      return 'INV-03 dejó de entregar. No es un caso de limpieza';
    }
    if (scenario === 'cloud') {
      return 'Una nube simulada recorta todo el parque por un rato';
    }
    if (scenario === 'offline-gateway') {
      return 'Sin telemetría fresca no hay déficit confiable';
    }
    if (this.store.state().recoveryApplied) {
      return 'La comparación simulada muestra recuperación parcial en A2';
    }
    return 'Puna Norte está dentro de la banda simulada';
  });

  protected series() {
    const state = this.store.state();
    return installationSeries(state.scenario, state.recoveryApplied);
  }

  protected power(value: number): string {
    return powerLabel(value);
  }

  protected bar(value: number): number {
    const peak = Math.max(...this.series().map((point) => point.expectedW));
    return peak === 0 ? 0 : Math.max(4, (value / peak) * 100);
  }

  protected deficitLabel(): string {
    const deficit = this.store.rollup().deficit;
    return typeof deficit === 'number' ? whLabel(deficit) : 'sin estimación confiable';
  }
}
