import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';
import {
  DiurnalMode,
  DiurnalSimState,
  createDiurnalState,
  setDiurnalConfig,
  setDiurnalMode,
  stepDiurnalSimulation,
} from '../../core/domain/diurnal-sim';

@Injectable({ providedIn: 'root' })
export class DiurnalSimulatorService {
  private readonly destroyRef = inject(DestroyRef);
  private timerId: ReturnType<typeof setInterval> | null = null;

  readonly state = signal<DiurnalSimState>(this.initialSeededState());
  readonly running = signal(false);
  readonly speedMs = signal<number>(1000);
  readonly cleaningState = signal<'idle' | 'cleaning' | 'recovered'>('idle');

  readonly progressPct = computed(() => {
    const hora = this.state().hora;
    return Math.min(100, Math.max(0, Math.round(((hora - 7) / 12) * 100)));
  });

  constructor() {
    this.destroyRef.onDestroy(() => this.pause());
  }

  private initialSeededState(): DiurnalSimState {
    let current = createDiurnalState({ peakKw: 5.0, tariffArsPerKwh: 120 });
    // Pre-populate 12 morning ticks (07:00 -> 08:00) so the chart renders immediately with real curve data
    for (let i = 0; i < 12; i += 1) {
      current = stepDiurnalSimulation(current);
    }
    return current;
  }

  play(): void {
    if (this.running()) {
      return;
    }
    this.running.set(true);
    this.timerId = setInterval(() => this.step(), this.speedMs());
  }

  pause(): void {
    if (this.timerId !== null) {
      clearInterval(this.timerId);
      this.timerId = null;
    }
    this.running.set(false);
  }

  togglePlay(): void {
    if (this.running()) {
      this.pause();
    } else {
      this.play();
    }
  }

  step(count = 1): void {
    this.state.update((prev) => {
      let next = prev;
      for (let i = 0; i < count; i += 1) {
        next = stepDiurnalSimulation(next);
      }
      return next;
    });
    const current = this.state();
    if (this.cleaningState() === 'cleaning' && current.suciedad >= 1 && current.estado === 'verde') {
      this.cleaningState.set('recovered');
    }
  }

  setMode(mode: DiurnalMode): void {
    if (mode !== 'normal') {
      this.cleaningState.set('idle');
    }
    this.state.update((prev) => setDiurnalMode(prev, mode));
  }

  sendCleaning(): void {
    this.state.update((prev) => setDiurnalMode(prev, 'normal'));
    this.cleaningState.set('cleaning');
    if (!this.running()) {
      this.play();
    }
  }

  setSpeed(ms: number): void {
    this.speedMs.set(ms);
    if (this.running()) {
      this.pause();
      this.play();
    }
  }

  updateConfig(config: { peakKw?: number; tariffArsPerKwh?: number }): void {
    this.state.update((prev) => setDiurnalConfig(prev, config));
  }

  reset(): void {
    this.pause();
    this.cleaningState.set('idle');
    const { peakKw, tariffArsPerKwh } = this.state();
    this.state.set(createDiurnalState({ peakKw, tariffArsPerKwh }));
  }
}
