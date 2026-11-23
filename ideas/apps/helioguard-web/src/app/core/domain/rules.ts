import type {
  BlockCode,
  DroneMissionKind,
  DroneMissionStatus,
  LossCause,
  MissionAuthorizationStatus,
  ScenarioId,
  WorkOrderStatus,
} from './model';

export interface PowerInterval {
  powerW: number;
  durationSeconds: number;
}

export interface IntervalPoint {
  label: string;
  durationSeconds: number;
  expectedW: number;
  actualW: number | null;
  comparable: boolean;
}

export interface CauseDraft {
  cause: LossCause;
  estimatedWh: number;
  method: string;
  evidenceNote: string;
}

export interface SampleIdentity {
  gatewayId: string;
  deviceId: string;
  eventId: string;
  bootId: string;
  sequenceNumber: number;
  powerW: number;
}

export interface ClassifyResult {
  decision: 'ACCEPTED' | 'DUPLICATE_EVENT' | 'REPLAY' | 'PHYSICAL_INCONSISTENCY';
  legitimateReboot: boolean;
}

export const INVERTER_MAX_POWER_W = 250_000;
export const INVERTER_UNCERTAINTY_RATIO = 0.08;
export const SELF_CONSUMPTION_RATIO = 0.7;

const BLOCK_EXPECTED_W: Record<BlockCode, number> = {
  A1: 2_100_000,
  A2: 2_100_000,
  A3: 2_000_000,
  A4: 1_900_000,
};

const INTERVAL_LABELS = ['10:00', '10:15', '10:30', '10:45', '11:00', '11:15', '11:30', '11:45'];

/** Integra potencia en el tiempo. 2000 W durante 3600 s = 2000 Wh, no 2000 kWh. */
export function wattHours(intervals: PowerInterval[]): number {
  return intervals.reduce((sum, interval) => sum + (interval.powerW * interval.durationSeconds) / 3600, 0);
}

export function energyWhFromCounter(startWh: number, endWh: number): number | 'SIN_ESTIMACION' {
  if (!Number.isFinite(startWh) || !Number.isFinite(endWh) || endWh < startWh) {
    return 'SIN_ESTIMACION';
  }
  return endWh - startWh;
}

export function deficitWh(
  expectedWh: number,
  actualWh: number | null,
  comparable: boolean,
): number | 'SIN_ESTIMACION' {
  if (!comparable || actualWh === null || !Number.isFinite(expectedWh) || !Number.isFinite(actualWh)) {
    return 'SIN_ESTIMACION';
  }
  return Math.max(0, expectedWh - actualWh);
}

export function capCauses(deficit: number, parts: CauseDraft[]): CauseDraft[] {
  if (deficit <= 0) {
    return parts.map((part) => ({ ...part, estimatedWh: 0 }));
  }
  const sum = parts.reduce((total, part) => total + part.estimatedWh, 0);
  if (sum <= deficit) {
    return parts;
  }
  const scale = deficit / sum;
  return parts.map((part) => ({ ...part, estimatedWh: Math.round(part.estimatedWh * scale) }));
}

export function lossAmountArs(input: {
  selfConsumptionLostWh: number;
  exportedLostWh: number;
  purchasePricePerKwh: number;
  exportPricePerKwh: number;
}): number {
  const self = (input.selfConsumptionLostWh / 1000) * input.purchasePricePerKwh;
  const exported = (input.exportedLostWh / 1000) * input.exportPricePerKwh;
  return Math.round((self + exported) * 100) / 100;
}

export function splitLostEnergy(deficit: number): { selfConsumptionLostWh: number; exportedLostWh: number } {
  const self = Math.round(deficit * SELF_CONSUMPTION_RATIO);
  return { selfConsumptionLostWh: self, exportedLostWh: deficit - self };
}

export function classifySample(
  priorAccepted: SampleIdentity[],
  incoming: SampleIdentity,
  maxPowerW = INVERTER_MAX_POWER_W,
  uncertaintyRatio = INVERTER_UNCERTAINTY_RATIO,
): ClassifyResult {
  const duplicate = priorAccepted.some(
    (sample) => sample.gatewayId === incoming.gatewayId && sample.eventId === incoming.eventId,
  );
  if (duplicate) {
    return { decision: 'DUPLICATE_EVENT', legitimateReboot: false };
  }
  const replay = priorAccepted.some(
    (sample) =>
      sample.deviceId === incoming.deviceId &&
      sample.bootId === incoming.bootId &&
      sample.sequenceNumber === incoming.sequenceNumber,
  );
  if (replay) {
    return { decision: 'REPLAY', legitimateReboot: false };
  }
  const ceiling = maxPowerW * (1 + uncertaintyRatio);
  if (incoming.powerW < 0 || incoming.powerW > ceiling) {
    return { decision: 'PHYSICAL_INCONSISTENCY', legitimateReboot: false };
  }
  const knownBoot = priorAccepted.some(
    (sample) => sample.deviceId === incoming.deviceId && sample.bootId === incoming.bootId,
  );
  return { decision: 'ACCEPTED', legitimateReboot: !knownBoot && incoming.sequenceNumber === 0 };
}

export function seriesForBlock(scenario: ScenarioId, block: BlockCode, recovered: boolean): IntervalPoint[] {
  return INTERVAL_LABELS.map((label, index) => {
    const expectedW = BLOCK_EXPECTED_W[block];
    const factor = actualFactor(scenario, block, index, recovered);
    const comparable = scenario !== 'offline-gateway';
    return {
      label,
      durationSeconds: 900,
      expectedW,
      actualW: comparable ? Math.round(expectedW * factor) : null,
      comparable,
    };
  });
}

export function installationSeries(scenario: ScenarioId, recovered: boolean): IntervalPoint[] {
  const blocks: BlockCode[] = ['A1', 'A2', 'A3', 'A4'];
  return INTERVAL_LABELS.map((label, index) => {
    let expectedW = 0;
    let actualW = 0;
    for (const block of blocks) {
      const point = seriesForBlock(scenario, block, recovered)[index];
      expectedW += point.expectedW;
      actualW += point.actualW ?? 0;
    }
    return {
      label,
      durationSeconds: 900,
      expectedW,
      actualW: scenario === 'offline-gateway' ? null : actualW,
      comparable: scenario !== 'offline-gateway',
    };
  });
}

export interface EnergyRollup {
  expectedWh: number;
  actualWh: number | null;
  deficit: number | 'SIN_ESTIMACION';
  comparable: boolean;
  causes: CauseDraft[];
}

export function rollupInstallation(scenario: ScenarioId, recovered: boolean): EnergyRollup {
  const series = installationSeries(scenario, recovered);
  const expectedWh = wattHours(series.map((point) => ({ powerW: point.expectedW, durationSeconds: point.durationSeconds })));
  const comparable = series.every((point) => point.comparable && point.actualW !== null);
  const actualWh = comparable
    ? wattHours(series.map((point) => ({ powerW: point.actualW ?? 0, durationSeconds: point.durationSeconds })))
    : null;
  const deficit = deficitWh(expectedWh, actualWh, comparable);
  return {
    expectedWh,
    actualWh,
    deficit,
    comparable,
    causes: comparable && typeof deficit === 'number' ? causesFor(scenario, deficit, recovered) : [],
  };
}

export function rollupBlock(scenario: ScenarioId, block: BlockCode, recovered: boolean): EnergyRollup {
  const series = seriesForBlock(scenario, block, recovered);
  const expectedWh = wattHours(series.map((point) => ({ powerW: point.expectedW, durationSeconds: point.durationSeconds })));
  const comparable = series.every((point) => point.comparable && point.actualW !== null);
  const actualWh = comparable
    ? wattHours(series.map((point) => ({ powerW: point.actualW ?? 0, durationSeconds: point.durationSeconds })))
    : null;
  const deficit = deficitWh(expectedWh, actualWh, comparable);
  return { expectedWh, actualWh, deficit, comparable, causes: [] };
}

function causesFor(scenario: ScenarioId, deficit: number, recovered: boolean): CauseDraft[] {
  if (deficit === 0) {
    return [];
  }
  if (scenario === 'soiling' && !recovered) {
    return capCauses(deficit, [
      {
        cause: 'SOILING',
        estimatedWh: Math.round(deficit * 0.62),
        method: 'desvio-sector-vs-hermanos',
        evidenceNote: 'El bloque A2 cae y los bloques vecinos no. Hipótesis de polvo, no medición de suciedad.',
      },
      {
        cause: 'UNKNOWN',
        estimatedWh: deficit - Math.round(deficit * 0.62),
        method: 'residual',
        evidenceNote: 'El residual no se asigna a suciedad sin inspección.',
      },
    ]);
  }
  if (scenario === 'inverter') {
    return capCauses(deficit, [
      {
        cause: 'HARDWARE',
        estimatedWh: Math.round(deficit * 0.85),
        method: 'inversor-sin-potencia',
        evidenceNote: 'INV-03 en cero mientras el recurso solar del modelo sigue presente. No es un caso de limpieza.',
      },
      {
        cause: 'UNKNOWN',
        estimatedWh: deficit - Math.round(deficit * 0.85),
        method: 'residual',
        evidenceNote: 'Puede haber strings sanos en el mismo bloque.',
      },
    ]);
  }
  if (scenario === 'cloud' || scenario === 'hail' || scenario === 'smoke') {
    return capCauses(deficit, [
      {
        cause: 'WEATHER',
        estimatedWh: deficit,
        method: 'recurso-meteorologico-simulado',
        evidenceNote: 'El recorte coincide con el recurso externo simulado. No se abre limpieza.',
      },
    ]);
  }
  return capCauses(deficit, [
    {
      cause: 'UNKNOWN',
      estimatedWh: deficit,
      method: 'banda-operativa',
      evidenceNote: 'Diferencia pequeña dentro de incertidumbre del modelo. Sin causa única.',
    },
  ]);
}

function actualFactor(scenario: ScenarioId, block: BlockCode, index: number, recovered: boolean): number {
  if (scenario === 'soiling') {
    if (block === 'A2') {
      return recovered ? 0.96 : 0.62;
    }
    return 0.98;
  }
  if (scenario === 'inverter') {
    return block === 'A2' ? 0 : 0.98;
  }
  if (scenario === 'cloud') {
    return index === 3 || index === 4 ? 0.55 : 0.98;
  }
  if (scenario === 'hail') {
    return index >= 5 ? 0.42 : 0.97;
  }
  if (scenario === 'smoke') {
    return 0.74;
  }
  return 0.98;
}

export function weatherFor(scenario: ScenarioId, roof: boolean): {
  frost: boolean;
  windMs: number;
  hail: boolean;
  roof: boolean;
} {
  if (scenario === 'frost') {
    return { frost: true, windMs: 3, hail: false, roof };
  }
  if (scenario === 'hail') {
    return { frost: false, windMs: 16, hail: true, roof };
  }
  if (scenario === 'smoke') {
    return { frost: false, windMs: 9, hail: false, roof };
  }
  return { frost: false, windMs: 4, hail: false, roof };
}

export function canTransitionMission(from: DroneMissionStatus, to: DroneMissionStatus): boolean {
  const allowed: Partial<Record<DroneMissionStatus, DroneMissionStatus[]>> = {
    DRAFT: ['SAFETY_REVIEW', 'ABORTED'],
    SAFETY_REVIEW: ['AWAITING_AUTHORIZATION', 'ABORTED'],
    AWAITING_AUTHORIZATION: ['AUTHORIZED', 'ABORTED'],
    AUTHORIZED: ['DISPATCHED', 'ABORTED'],
    DISPATCHED: ['IN_FLIGHT', 'ABORTED'],
    IN_FLIGHT: ['COMPLETED', 'ABORTED'],
  };
  return (allowed[from] ?? []).includes(to);
}

export function cleaningBlockReason(input: {
  kind: DroneMissionKind;
  frost: boolean;
  windMs: number;
  maxWindMs: number;
  hail: boolean;
  roof: boolean;
  authorization: MissionAuthorizationStatus | null;
}): string | null {
  if (input.authorization !== 'APPROVED') {
    return 'Falta una autorización APPROVED. La animación no representa un permiso de vuelo real.';
  }
  if (input.windMs > input.maxWindMs) {
    return `Viento ${input.windMs} m/s por encima del límite del equipo (${input.maxWindMs} m/s).`;
  }
  if (input.hail) {
    return 'Granizo simulado: el despacho queda bloqueado.';
  }
  if (input.kind === 'CLEANING' && input.frost) {
    return 'Helada: no se simula agua ni aspersión sobre módulos fríos.';
  }
  if (input.kind === 'CLEANING' && input.roof) {
    return 'Cubierta residencial: la demo no simula limpieza aérea sobre un techo.';
  }
  return null;
}

export function canTransitionWorkOrder(from: WorkOrderStatus, to: WorkOrderStatus): boolean {
  const allowed: Partial<Record<WorkOrderStatus, WorkOrderStatus[]>> = {
    CREATED: ['ASSIGNED', 'CANCELLED'],
    ASSIGNED: ['ACCEPTED', 'CANCELLED'],
    ACCEPTED: ['IN_PROGRESS', 'CANCELLED'],
    IN_PROGRESS: ['DONE', 'CANCELLED'],
    DONE: ['VERIFIED'],
    VERIFIED: [],
    CANCELLED: [],
  };
  return (allowed[from] ?? []).includes(to);
}

export function localIso(utcIso: string, timeZone: string): string {
  return new Intl.DateTimeFormat('es-AR', {
    timeZone,
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(utcIso));
}
