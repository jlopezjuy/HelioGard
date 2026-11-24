/**
 * Motor de simulación diurna fiel a `Idea Original/app.py` (prototipo Hackatón Tren Solar).
 *
 * Modela el ciclo solar 07:00–19:00 en pasos de 5 minutos, ensuciamiento gradual,
 * recuperación en modo normal, inyección falsa, replay congelado, los 3 detectores
 * originales (`Dato imposible`, `Posible replay`, `Rendimiento bajo`) y el
 * congelamiento de pérdidas cuando el estado es `rojo`.
 */

export type DiurnalMode = 'normal' | 'suciedad' | 'replay' | 'inyeccion';
export type DiurnalAlertLevel = 'amarillo' | 'rojo';
export type DiurnalStatus = 'verde' | 'amarillo' | 'rojo';

export interface DiurnalAlert {
  nivel: DiurnalAlertLevel;
  titulo: string;
  detalle: string;
}

export interface DiurnalReading {
  hora: number;
  reloj: string;
  modo: DiurnalMode;
  potenciaKw: number;
  tensionV: number;
  corrienteA: number;
  irradianciaWm2: number;
  temperaturaC: number;
  esperadaKw: number;
}

export interface DiurnalSimState {
  hora: number;
  modo: DiurnalMode;
  suciedad: number;
  congelado: DiurnalReading | null;
  hist: DiurnalReading[];
  serie: DiurnalReading[];
  perdidaKwh: number;
  perdidaPesos: number;
  estado: DiurnalStatus;
  alertas: DiurnalAlert[];
  lectura: DiurnalReading | null;
  peakKw: number;
  tariffArsPerKwh: number;
  dayRollovers: number;
}

export const ORIGINAL_DEFAULTS = {
  peakKw: 5.0,
  tariffArsPerKwh: 120.0,
  stepMinutes: 5,
  stepHours: 5 / 60,
  startHour: 7.0,
  endHour: 19.0,
  maxHistory: 12,
  minSoilingFactor: 0.55,
  soilingStepDrop: 0.012,
  cleaningStepGain: 0.05,
  electricalMismatchRatio: 0.08,
  electricalPowerFloorKw: 0.1,
  expectedCeilingRatio: 1.25,
  expectedCeilingMarginKw: 0.3,
  replayConsecutiveCount: 5,
  lowYieldMinUsefulSamples: 6,
  lowYieldUsefulPowerKw: 0.5,
  lowYieldRatioThreshold: 0.93,
  lossDeadbandFactor: 0.97,
} as const;

function roundTo(value: number, decimals: number): number {
  const factor = 10 ** decimals;
  return Math.round(value * factor) / factor;
}

function uniform(min: number, max: number, rng: () => number): number {
  return min + (max - min) * rng();
}

export function formatSimClock(hora: number): string {
  const hh = Math.floor(hora);
  const mm = Math.round((hora - hh) * 60) % 60;
  return `${String(hh).padStart(2, '0')}:${String(mm).padStart(2, '0')}`;
}

export function irradianceAtHour(hora: number): number {
  if (hora < 6.5 || hora > 18.5) {
    return 0;
  }
  return Math.max(0, Math.sin((Math.PI * (hora - 6.5)) / 12)) * 1000;
}

export function panelTempC(irradianceWm2: number, noiseC = 0): number {
  return 20 + irradianceWm2 * 0.025 + noiseC;
}

export function expectedPowerKw(peakKw: number, irradianceWm2: number, tempC: number): number {
  return peakKw * (irradianceWm2 / 1000) * (1 - 0.004 * (tempC - 25));
}

export function createDiurnalState(options?: {
  peakKw?: number;
  tariffArsPerKwh?: number;
  startHour?: number;
}): DiurnalSimState {
  return {
    hora: options?.startHour ?? ORIGINAL_DEFAULTS.startHour,
    modo: 'normal',
    suciedad: 1.0,
    congelado: null,
    hist: [],
    serie: [],
    perdidaKwh: 0,
    perdidaPesos: 0,
    estado: 'verde',
    alertas: [],
    lectura: null,
    peakKw: options?.peakKw ?? ORIGINAL_DEFAULTS.peakKw,
    tariffArsPerKwh: options?.tariffArsPerKwh ?? ORIGINAL_DEFAULTS.tariffArsPerKwh,
    dayRollovers: 0,
  };
}

export function setDiurnalMode(state: DiurnalSimState, modo: DiurnalMode): DiurnalSimState {
  return {
    ...state,
    modo,
    congelado: modo === 'replay' ? state.congelado : null,
  };
}

export function setDiurnalConfig(
  state: DiurnalSimState,
  config: { peakKw?: number; tariffArsPerKwh?: number },
): DiurnalSimState {
  const peakKw = config.peakKw !== undefined ? Math.max(0.5, config.peakKw) : state.peakKw;
  const tariffArsPerKwh =
    config.tariffArsPerKwh !== undefined ? Math.max(0, config.tariffArsPerKwh) : state.tariffArsPerKwh;
  return {
    ...state,
    peakKw,
    tariffArsPerKwh,
    perdidaPesos: Math.round(state.perdidaKwh * tariffArsPerKwh),
  };
}

export function detectDiurnalAnomalies(
  hist: readonly DiurnalReading[],
  current: DiurnalReading,
): DiurnalAlert[] {
  const alertas: DiurnalAlert[] = [];

  const pvi = (current.tensionV * current.corrienteA) / 1000;
  if (
    Math.abs(current.potenciaKw - pvi) >
      ORIGINAL_DEFAULTS.electricalMismatchRatio * Math.max(pvi, ORIGINAL_DEFAULTS.electricalPowerFloorKw) ||
    current.potenciaKw >
      ORIGINAL_DEFAULTS.expectedCeilingRatio * current.esperadaKw + ORIGINAL_DEFAULTS.expectedCeilingMarginKw
  ) {
    alertas.push({
      nivel: 'rojo',
      titulo: 'Dato imposible',
      detalle: 'Potencia no coincide con V×I ni con la radiación. Sensor roto o dato inyectado.',
    });
  }

  const ult = hist.slice(-ORIGINAL_DEFAULTS.replayConsecutiveCount);
  if (
    ult.length === ORIGINAL_DEFAULTS.replayConsecutiveCount &&
    ult.every((item) => item.potenciaKw === ult[0].potenciaKw && item.tensionV === ult[0].tensionV)
  ) {
    alertas.push({
      nivel: 'rojo',
      titulo: 'Posible replay',
      detalle: 'Cinco lecturas idénticas seguidas. Los sensores reales siempre tienen ruido.',
    });
  }

  const util = hist.filter((item) => item.esperadaKw > ORIGINAL_DEFAULTS.lowYieldUsefulPowerKw);
  if (
    util.length >= ORIGINAL_DEFAULTS.lowYieldMinUsefulSamples &&
    current.esperadaKw > ORIGINAL_DEFAULTS.lowYieldUsefulPowerKw
  ) {
    const sumPot = util.reduce((sum, item) => sum + item.potenciaKw, 0);
    const sumEsp = util.reduce((sum, item) => sum + item.esperadaKw, 0);
    const ratio = sumEsp > 0 ? sumPot / sumEsp : 1;
    if (ratio < ORIGINAL_DEFAULTS.lowYieldRatioThreshold) {
      alertas.push({
        nivel: 'amarillo',
        titulo: 'Rendimiento bajo',
        detalle: `Producción al ${Math.round(ratio * 100)} % de lo esperado. Programar limpieza.`,
      });
    }
  }

  return alertas;
}

export function stepDiurnalSimulation(
  state: DiurnalSimState,
  rng: () => number = Math.random,
): DiurnalSimState {
  let suciedad = state.suciedad;
  let congelado = state.congelado;
  let reading: DiurnalReading;

  if (state.modo === 'replay') {
    if (congelado) {
      reading = {
        ...congelado,
        hora: roundTo(state.hora, 4),
        reloj: formatSimClock(state.hora),
        modo: 'replay',
      };
    } else {
      reading = generateReading(state, suciedad, rng);
      congelado = reading;
    }
  } else {
    congelado = null;
    if (state.modo === 'suciedad') {
      suciedad = Math.max(ORIGINAL_DEFAULTS.minSoilingFactor, suciedad - ORIGINAL_DEFAULTS.soilingStepDrop);
    } else if (state.modo === 'normal') {
      suciedad = Math.min(1.0, suciedad + ORIGINAL_DEFAULTS.cleaningStepGain);
    }
    reading = generateReading(state, suciedad, rng);
    if (state.modo === 'inyeccion') {
      reading = {
        ...reading,
        modo: 'inyeccion',
        potenciaKw: roundTo(reading.esperadaKw * 1.7 + 0.8, 3),
      };
    }
  }

  const hist = [...state.hist, reading].slice(-ORIGINAL_DEFAULTS.maxHistory);
  const alertas = detectDiurnalAnomalies(hist, reading);
  const estado: DiurnalStatus = alertas.some((a) => a.nivel === 'rojo')
    ? 'rojo'
    : alertas.some((a) => a.nivel === 'amarillo')
      ? 'amarillo'
      : 'verde';

  let perdidaKwh = state.perdidaKwh;
  if (estado !== 'rojo' && reading.potenciaKw < ORIGINAL_DEFAULTS.lossDeadbandFactor * reading.esperadaKw) {
    perdidaKwh += Math.max(0, reading.esperadaKw - reading.potenciaKw) * ORIGINAL_DEFAULTS.stepHours;
  }

  const nextHora = state.hora + ORIGINAL_DEFAULTS.stepHours;
  const rolledOver = nextHora >= ORIGINAL_DEFAULTS.endHour;
  const hora = rolledOver ? ORIGINAL_DEFAULTS.startHour : nextHora;
  const serie = rolledOver ? [] : [...state.serie, reading];

  return {
    ...state,
    hora,
    suciedad: roundTo(suciedad, 4),
    congelado,
    hist,
    serie,
    perdidaKwh: roundTo(perdidaKwh, 4),
    perdidaPesos: Math.round(perdidaKwh * state.tariffArsPerKwh),
    estado,
    alertas,
    lectura: reading,
    dayRollovers: state.dayRollovers + (rolledOver ? 1 : 0),
  };
}

function generateReading(
  state: DiurnalSimState,
  suciedad: number,
  rng: () => number,
): DiurnalReading {
  const irr = irradianceAtHour(state.hora);
  const temp = panelTempC(irr, uniform(-0.4, 0.4, rng));
  const esperada = expectedPowerKw(state.peakKw, irr, temp);
  const pot = esperada * suciedad * uniform(0.985, 1.015, rng);
  const tension = 380 + uniform(-3, 3, rng);
  const corriente = tension > 0 ? (pot * 1000) / tension : 0;
  const irrMeasured = irr * uniform(0.98, 1.02, rng);

  return {
    hora: roundTo(state.hora, 4),
    reloj: formatSimClock(state.hora),
    modo: state.modo,
    potenciaKw: roundTo(pot, 3),
    tensionV: roundTo(tension, 1),
    corrienteA: roundTo(corriente, 2),
    irradianciaWm2: roundTo(irrMeasured, 0),
    temperaturaC: roundTo(temp, 1),
    esperadaKw: roundTo(esperada, 3),
  };
}
