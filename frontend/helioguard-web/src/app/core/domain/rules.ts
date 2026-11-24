import type {
  DataTrustLevel,
  DroneMissionStatus,
  IngestLedgerEntry,
  IntegrityEventStatus,
  IntegrityEventType,
  LossCause,
  TelemetryMessage,
} from './model';

export interface PowerSample {
  powerW: number;
  durationSeconds: number;
}

export interface DeficitResult {
  kind: 'value';
  deficitWh: number;
}

export interface UnavailableDeficit {
  kind: 'unavailable';
  label: 'sin estimación confiable';
}

export interface IngestOutcome {
  action: 'ACCEPT' | 'RETAIN' | 'REVIEW';
  eventType: IntegrityEventType | null;
  status: IntegrityEventStatus | null;
  trustLevel: DataTrustLevel;
  reason: string;
  matchedKeys: string[];
  useInEnergy: boolean;
  cryptographicallyVerified: false;
}

const MISSION_NEXT: Record<DroneMissionStatus, readonly DroneMissionStatus[]> = {
  DRAFT: ['SAFETY_REVIEW', 'ABORTED'],
  SAFETY_REVIEW: ['AWAITING_AUTHORIZATION', 'ABORTED'],
  AWAITING_AUTHORIZATION: ['AUTHORIZED', 'ABORTED'],
  AUTHORIZED: ['DISPATCHED', 'ABORTED'],
  DISPATCHED: ['IN_FLIGHT', 'ABORTED'],
  IN_FLIGHT: ['COMPLETED', 'ABORTED'],
  COMPLETED: [],
  ABORTED: [],
};

export function wattHoursFromPower(powerW: number, durationSeconds: number): number {
  return (powerW * durationSeconds) / 3600;
}

export function wattHoursFromSamples(samples: readonly PowerSample[]): number {
  return samples.reduce((sum, sample) => sum + wattHoursFromPower(sample.powerW, sample.durationSeconds), 0);
}

export function wattHoursFromCounter(startWh: number, endWh: number): number | 'invalid' {
  if (endWh < startWh) {
    return 'invalid';
  }
  return endWh - startWh;
}

export function deficitWh(
  expectedWh: number,
  actualWh: number | null,
  comparable: boolean,
): DeficitResult | UnavailableDeficit {
  if (!comparable || actualWh === null) {
    return { kind: 'unavailable', label: 'sin estimación confiable' };
  }
  return { kind: 'value', deficitWh: Math.max(0, expectedWh - actualWh) };
}

export function lossAmountArs(
  deficitWhValue: number,
  selfConsumptionRatio: number,
  purchasePricePerKwh: number,
  exportPricePerKwh: number,
): number {
  const kwh = deficitWhValue / 1000;
  const selfKwh = kwh * selfConsumptionRatio;
  const exportKwh = kwh - selfKwh;
  const amount = selfKwh * purchasePricePerKwh + exportKwh * exportPricePerKwh;
  return Math.round(amount * 100) / 100;
}

export function assertCauseAllocation(
  totalDeficitWh: number,
  parts: readonly { cause: LossCause; estimatedWh: number }[],
): { ok: true } | { ok: false; reason: string } {
  const sum = parts.reduce((total, part) => total + part.estimatedWh, 0);
  if (sum - totalDeficitWh > 0.001) {
    return { ok: false, reason: 'La suma de causas supera el déficit.' };
  }
  const soiling = parts.find((part) => part.cause === 'SOILING');
  if (soiling && totalDeficitWh > 0 && soiling.estimatedWh >= totalDeficitWh - 0.001) {
    return { ok: false, reason: 'No se asigna el 100% a suciedad sin medición directa de ensuciamiento.' };
  }
  return { ok: true };
}

export function canTransition(from: DroneMissionStatus, to: DroneMissionStatus): boolean {
  return MISSION_NEXT[from].includes(to);
}

export function safetyBlockReasons(input: {
  windBlocked: boolean;
  frostBlocked: boolean;
  roofWorkBlocked: boolean;
  manufacturerOk: boolean;
  operatorOk: boolean;
  siteOk: boolean;
}): string[] {
  const reasons: string[] = [];
  if (input.windBlocked) {
    reasons.push('Viento por encima del máximo declarado para el dron simulado.');
  }
  if (input.frostBlocked) {
    reasons.push('Helada: la demo no simula agua sobre hielo.');
  }
  if (input.roofWorkBlocked) {
    reasons.push('Trabajo en techo bloqueado: no se simula limpieza casera de techos.');
  }
  if (!input.manufacturerOk) {
    reasons.push('Falta la conformidad de fabricante del equipo.');
  }
  if (!input.operatorOk) {
    reasons.push('Falta un operador identificado para la misión simulada.');
  }
  if (!input.siteOk) {
    reasons.push('El sitio no está habilitado para esta misión simulada.');
  }
  return reasons;
}

export function evaluateIngest(
  ledger: readonly IngestLedgerEntry[],
  message: TelemetryMessage,
  limits: { maxPowerW: number; uncertaintyW: number },
): IngestOutcome {
  const duplicate = ledger.find(
    (entry) => entry.gatewayId === message.gatewayId && entry.eventId === message.eventId,
  );
  if (duplicate) {
    return {
      action: 'RETAIN',
      eventType: 'DUPLICATE_EVENT',
      status: 'QUARANTINED',
      trustLevel: 'SUSPECT',
      reason:
        'Idempotencia por gatewayId+eventId. La retransmisión no se inserta otra vez y no suma energía.',
      matchedKeys: ['gatewayId', 'eventId'],
      useInEnergy: false,
      cryptographicallyVerified: false,
    };
  }

  const replay = ledger.find(
    (entry) =>
      entry.deviceId === message.deviceId &&
      entry.bootId === message.bootId &&
      entry.sequenceNumber === message.sequenceNumber,
  );
  if (replay) {
    return {
      action: 'RETAIN',
      eventType: 'REPLAY',
      status: 'UNDER_REVIEW',
      trustLevel: 'SUSPECT',
      reason:
        'Otro eventId reutiliza deviceId+bootId+sequenceNumber. Se retiene. No es, por sí solo, un ataque confirmado.',
      matchedKeys: ['deviceId', 'bootId', 'sequenceNumber'],
      useInEnergy: false,
      cryptographicallyVerified: false,
    };
  }

  if (message.voltageV !== undefined) {
    const recentForDevice = ledger
      .filter((entry) => entry.deviceId === message.deviceId)
      .slice(-4);
    if (
      recentForDevice.length === 4 &&
      recentForDevice.every(
        (entry) => entry.powerW === message.powerW && entry.voltageV === message.voltageV,
      )
    ) {
      return {
        action: 'RETAIN',
        eventType: 'REPLAY',
        status: 'UNDER_REVIEW',
        trustLevel: 'SUSPECT',
        reason:
          'Cinco lecturas seguidas con idéntico par potencia/tensión sin ruido físico (stream congelado). No demuestra intrusión por sí solo.',
        matchedKeys: ['deviceId', 'powerW', 'voltageV'],
        useInEnergy: false,
        cryptographicallyVerified: false,
      };
    }
  }

  const exceedsAbsolute = message.powerW > limits.maxPowerW + limits.uncertaintyW;
  const pviW =
    message.voltageV !== undefined && message.currentA !== undefined
      ? message.voltageV * message.currentA
      : null;
  const failsVxI = pviW !== null && Math.abs(message.powerW - pviW) > 0.08 * Math.max(pviW, 100);
  const exceedsExpectedCeiling =
    message.expectedPowerW !== undefined && message.powerW > 1.25 * message.expectedPowerW + 300;

  if (exceedsAbsolute || failsVxI || exceedsExpectedCeiling) {
    const matchedKeys: string[] = [];
    if (failsVxI) matchedKeys.push('V*I');
    if (exceedsExpectedCeiling) matchedKeys.push('expectedPowerW');
    if (exceedsAbsolute) matchedKeys.push('maxPowerW');
    return {
      action: 'REVIEW',
      eventType: 'PHYSICAL_INCONSISTENCY',
      status: 'UNDER_REVIEW',
      trustLevel: 'SUSPECT',
      reason: failsVxI
        ? 'La potencia informada no coincide con V×I (> 8 % de desvío). Puede ser sensor o dato inconsistente. No demuestra intrusión.'
        : 'La potencia supera el límite físico o el 125 % de la esperada. Puede ser sensor o dato inconsistente. No demuestra intrusión.',
      matchedKeys,
      useInEnergy: false,
      cryptographicallyVerified: false,
    };
  }

  const signed = message.authenticSignature;
  return {
    action: 'ACCEPT',
    eventType: null,
    status: null,
    trustLevel: 'ESTIMATED',
    reason: signed
      ? 'Lectura aceptada en la simulación. El indicador de firma del fixture no equivale a una verificación criptográfica real.'
      : 'Lectura aceptada como estimación simulada. Sin firma auténtica: no está criptográficamente verificada.',
    matchedKeys: [],
    useInEnergy: true,
    cryptographicallyVerified: false,
  };
}

export function formatLocal(iso: string, timeZone: string): string {
  return new Intl.DateTimeFormat('es-AR', {
    timeZone,
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hourCycle: 'h23',
  }).format(new Date(iso));
}

export function formatArs(amount: number): string {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    maximumFractionDigits: 2,
  }).format(amount);
}

export function formatWh(value: number): string {
  if (Math.abs(value) >= 1000) {
    return `${new Intl.NumberFormat('es-AR', { maximumFractionDigits: 2 }).format(value / 1000)} kWh`;
  }
  return `${new Intl.NumberFormat('es-AR', { maximumFractionDigits: 0 }).format(value)} Wh`;
}
