import { Injectable, InjectionToken, Signal, WritableSignal, computed, inject, signal } from '@angular/core';
import {
  acceptProposal,
  acceptWork,
  acknowledgeIncident,
  approveMission,
  createResponseChain,
  cutWan,
  dispatchMission,
  publishExactReplay,
  setLossScenario,
  publishImplausible,
  publishNewBoot,
  publishOriginal,
  publishSameSequence,
  reopenAudit,
  restoreWan,
  reviewSafety,
  saveAudit,
  selectArray,
  setIntervalComparable,
  setScenarioFlag,
  setSession,
  setSoiling,
  tickDrone,
  toggleString,
  updateTariff,
  verifyMission,
} from '../../core/domain/actions';
import type { AuditStatus, DemoSnapshot, LossScenario } from '../../core/domain/model';
import { createSeed } from './seed';
import { parseSnapshot } from './snapshot-io';

export const STORAGE_KEY = 'helioguard.demo.v4';

export interface KeyValueStorage {
  getItem(key: string): string | null;
  setItem(key: string, value: string): void;
  removeItem(key: string): void;
}

export const KV_STORAGE = new InjectionToken<KeyValueStorage>('KV_STORAGE', {
  providedIn: 'root',
  factory: () => localStorage,
});

export interface DemoRepository {
  readonly snapshot: WritableSignal<DemoSnapshot>;
  readonly notice: Signal<string>;
  reset(): void;
  exportJson(): string;
  importJson(raw: string): string;
  soiling(enabled: boolean): void;
  loss(scenario: LossScenario): void;
  useUser(userId: string): void;
  chain(): void;
  safety(): void;
  approve(): void;
  dispatch(): void;
  tick(): void;
  verify(): void;
  original(): void;
  replay(): void;
  sameSequence(): void;
  newBoot(): void;
  implausible(): void;
  cut(): void;
  restore(): void;
  acknowledge(incidentId: string): void;
  toggleString(stringId: string): void;
  saveAudit(answers: Record<string, string>, status: AuditStatus): void;
  reopenAudit(): void;
  tariff(purchase: number, exported: number): void;
  acceptProposal(): void;
  acceptWork(): void;
  flag(
    flag: 'windBlocked' | 'frostBlocked' | 'roofWorkBlocked' | 'manufacturerOk' | 'operatorOk' | 'siteOk' | 'readFailure',
    value: boolean,
  ): void;
  select(arrayId: string): void;
  note(message: string): void;
  comparable(intervalId: string, value: boolean): void;
}

export const DEMO_REPOSITORY = new InjectionToken<DemoRepository>('DEMO_REPOSITORY');

@Injectable({ providedIn: 'root' })
export class DemoStore implements DemoRepository {
  private readonly storage = inject(KV_STORAGE);
  readonly snapshot = signal<DemoSnapshot>(this.read());
  readonly notice = computed(() => this.snapshot().scenario.notice);

  private read(): DemoSnapshot {
    const raw = this.storage.getItem(STORAGE_KEY);
    if (!raw) {
      return createSeed();
    }
    const parsed = parseSnapshot(raw);
    return parsed.ok ? parsed.snapshot : createSeed();
  }

  private commit(next: DemoSnapshot): void {
    this.snapshot.set(next);
    this.storage.setItem(STORAGE_KEY, JSON.stringify(next));
  }

  reset(): void {
    this.storage.removeItem(STORAGE_KEY);
    this.snapshot.set(createSeed());
  }

  exportJson(): string {
    return JSON.stringify(this.snapshot(), null, 2);
  }

  importJson(raw: string): string {
    const parsed = parseSnapshot(raw);
    if (!parsed.ok) {
      return parsed.reason;
    }
    this.commit(parsed.snapshot);
    return 'Copia importada. Sigue siendo una demo local.';
  }

  soiling(enabled: boolean): void {
    this.commit(setSoiling(this.snapshot(), enabled));
  }

  loss(scenario: LossScenario): void {
    this.commit(setLossScenario(this.snapshot(), scenario));
  }

  useUser(userId: string): void {
    this.commit(setSession(this.snapshot(), userId));
  }

  chain(): void {
    this.commit(createResponseChain(this.snapshot()));
  }

  safety(): void {
    this.commit(reviewSafety(this.snapshot()));
  }

  approve(): void {
    this.commit(approveMission(this.snapshot()));
  }

  dispatch(): void {
    this.commit(dispatchMission(this.snapshot()));
  }

  tick(): void {
    this.commit(tickDrone(this.snapshot()));
  }

  verify(): void {
    this.commit(verifyMission(this.snapshot()));
  }

  original(): void {
    this.commit(publishOriginal(this.snapshot()));
  }

  replay(): void {
    this.commit(publishExactReplay(this.snapshot()));
  }

  sameSequence(): void {
    this.commit(publishSameSequence(this.snapshot()));
  }

  newBoot(): void {
    this.commit(publishNewBoot(this.snapshot()));
  }

  implausible(): void {
    this.commit(publishImplausible(this.snapshot()));
  }

  cut(): void {
    this.commit(cutWan(this.snapshot()));
  }

  restore(): void {
    this.commit(restoreWan(this.snapshot()));
  }

  acknowledge(incidentId: string): void {
    this.commit(acknowledgeIncident(this.snapshot(), incidentId));
  }

  toggleString(stringId: string): void {
    this.commit(toggleString(this.snapshot(), stringId));
  }

  saveAudit(answers: Record<string, string>, status: AuditStatus): void {
    this.commit(saveAudit(this.snapshot(), answers, status));
  }

  reopenAudit(): void {
    this.commit(reopenAudit(this.snapshot()));
  }

  tariff(purchase: number, exported: number): void {
    this.commit(updateTariff(this.snapshot(), purchase, exported));
  }

  acceptProposal(): void {
    this.commit(acceptProposal(this.snapshot()));
  }

  acceptWork(): void {
    this.commit(acceptWork(this.snapshot()));
  }

  flag(
    flag: 'windBlocked' | 'frostBlocked' | 'roofWorkBlocked' | 'manufacturerOk' | 'operatorOk' | 'siteOk' | 'readFailure',
    value: boolean,
  ): void {
    this.commit(setScenarioFlag(this.snapshot(), flag, value));
  }

  select(arrayId: string): void {
    this.commit(selectArray(this.snapshot(), arrayId));
  }

  note(message: string): void {
    const next = structuredClone(this.snapshot());
    next.scenario.notice = message;
    this.commit(next);
  }

  comparable(intervalId: string, value: boolean): void {
    this.commit(setIntervalComparable(this.snapshot(), intervalId, value));
  }
}
