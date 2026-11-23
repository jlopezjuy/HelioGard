import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import type { BlockCode } from '../../core/domain/model';
import { rollupBlock } from '../../core/domain/rules';
import { DemoStore } from '../../infra/mock/demo-store';
import { powerLabel, whLabel } from '../../shared/format';

@Component({
  selector: 'app-park',
  imports: [RouterLink],
  templateUrl: './park.html',
})
export class Park {
  protected readonly store = inject(DemoStore);
  protected readonly whLabel = whLabel;
  protected readonly powerLabel = powerLabel;
  protected readonly selectedBlock = signal<BlockCode>('A2');
  protected readonly visible = computed(() => this.store.installations().some((item) => item.id === 'ins-puna'));
  protected readonly flying = computed(() => this.store.state().missions.some((mission) => mission.status === 'IN_FLIGHT'));
  protected readonly blocks = computed(() => {
    const state = this.store.state();
    return (['A1', 'A2', 'A3', 'A4'] as const).map((code) => {
      const rollup = rollupBlock(state.scenario, code, state.recoveryApplied);
      const deficit = rollup.deficit;
      const ratio = typeof deficit === 'number' && rollup.expectedWh > 0 ? deficit / rollup.expectedWh : 0;
      const label = !rollup.comparable ? 'Sin medición' : ratio > 0.15 ? 'Atención' : 'En banda';
      return {
        code,
        name: state.arrays.find((array) => array.blockCode === code)?.name ?? code,
        rollup,
        label,
      };
    });
  });
  protected readonly current = computed(() => this.blocks().find((block) => block.code === this.selectedBlock()) ?? this.blocks()[0]);

  protected choose(code: BlockCode): void {
    this.selectedBlock.set(code);
    this.store.selectInstallation('ins-puna');
  }

  protected deficitText(deficit: number | 'SIN_ESTIMACION'): string {
    return typeof deficit === 'number' ? `${whLabel(deficit)} de déficit` : 'sin estimación';
  }
}
