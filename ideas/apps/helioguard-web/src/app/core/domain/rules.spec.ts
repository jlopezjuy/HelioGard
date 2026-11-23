import { describe, expect, it } from 'vitest';
import {
  INVERTER_MAX_POWER_W,
  canTransitionMission,
  capCauses,
  classifySample,
  cleaningBlockReason,
  deficitWh,
  energyWhFromCounter,
  lossAmountArs,
  rollupBlock,
  rollupInstallation,
  splitLostEnergy,
  wattHours,
} from './rules';

const base = {
  gatewayId: 'gw-1',
  deviceId: 'inverter-03',
  eventId: 'evt-1',
  bootId: 'boot-A',
  sequenceNumber: 1542,
  powerW: 180_000,
};

describe('energía', () => {
  it('integra vatios en el tiempo y no trata kW como kWh', () => {
    expect(wattHours([{ powerW: 2000, durationSeconds: 3600 }])).toBe(2000);
    expect(wattHours([{ powerW: 2000, durationSeconds: 1800 }])).toBe(1000);
    expect(energyWhFromCounter(10, 10.5)).toBe(0.5);
    expect(energyWhFromCounter(10, 9)).toBe('SIN_ESTIMACION');
  });

  it('omite el déficit si los intervalos no son comparables', () => {
    expect(deficitWh(1000, 400, true)).toBe(600);
    expect(deficitWh(1000, null, false)).toBe('SIN_ESTIMACION');
  });

  it('no atribuye toda la pérdida a suciedad ni deja que las causas superen el déficit', () => {
    const rollup = rollupInstallation('soiling', false);
    expect(typeof rollup.deficit).toBe('number');
    const deficit = rollup.deficit as number;
    const soiling = rollup.causes.find((cause) => cause.cause === 'SOILING');
    const sum = rollup.causes.reduce((total, cause) => total + cause.estimatedWh, 0);
    expect(soiling).toBeTruthy();
    expect(soiling!.estimatedWh).toBeLessThan(deficit);
    expect(sum).toBeLessThanOrEqual(deficit);
    expect(rollup.causes.some((cause) => cause.cause === 'UNKNOWN')).toBe(true);
  });

  it('una nube no genera causa de suciedad', () => {
    const rollup = rollupInstallation('cloud', false);
    expect(rollup.causes.every((cause) => cause.cause !== 'SOILING')).toBe(true);
  });

  it('el bloque A2 concentra la suciedad y el resto casi no', () => {
    const dirty = rollupBlock('soiling', 'A2', false);
    const clean = rollupBlock('soiling', 'A1', false);
    expect(dirty.deficit).toBeGreaterThan(clean.deficit as number);
  });

  it('recorta causas que se pasan del déficit', () => {
    const capped = capCauses(100, [
      { cause: 'SOILING', estimatedWh: 80, method: 'm', evidenceNote: 'a' },
      { cause: 'UNKNOWN', estimatedWh: 80, method: 'm', evidenceNote: 'b' },
    ]);
    expect(capped.reduce((total, part) => total + part.estimatedWh, 0)).toBeLessThanOrEqual(100);
  });

  it('valoriza autoconsumo e inyección por separado en ARS', () => {
    const split = splitLostEnergy(10_000);
    const amount = lossAmountArs({
      selfConsumptionLostWh: split.selfConsumptionLostWh,
      exportedLostWh: split.exportedLostWh,
      purchasePricePerKwh: 200,
      exportPricePerKwh: 80,
    });
    expect(split.selfConsumptionLostWh).toBe(7000);
    expect(amount).toBe(1640);
  });
});

describe('integridad', () => {
  it('acepta el original, retiene la misma secuencia y acepta un bootId nuevo', () => {
    const first = classifySample([], base);
    expect(first.decision).toBe('ACCEPTED');
    const replay = classifySample([base], { ...base, eventId: 'evt-2' });
    expect(replay.decision).toBe('REPLAY');
    const duplicate = classifySample([base], base);
    expect(duplicate.decision).toBe('DUPLICATE_EVENT');
    const reboot = classifySample([base], {
      ...base,
      eventId: 'evt-3',
      bootId: 'boot-B',
      sequenceNumber: 0,
      powerW: 175_000,
    });
    expect(reboot).toEqual({ decision: 'ACCEPTED', legitimateReboot: true });
  });

  it('marca un valor imposible como inconsistencia física y no como ataque', () => {
    const injected = classifySample([], { ...base, eventId: 'evt-bad', sequenceNumber: 1600, powerW: 8_000_000 });
    expect(injected.decision).toBe('PHYSICAL_INCONSISTENCY');
    expect(8_000_000).toBeGreaterThan(INVERTER_MAX_POWER_W * 1.08);
  });
});

describe('misiones', () => {
  it('no permite saltar de DRAFT a IN_FLIGHT', () => {
    expect(canTransitionMission('DRAFT', 'IN_FLIGHT')).toBe(false);
    expect(canTransitionMission('DRAFT', 'SAFETY_REVIEW')).toBe(true);
    expect(canTransitionMission('AWAITING_AUTHORIZATION', 'AUTHORIZED')).toBe(true);
  });

  it('bloquea agua con helada, viento, granizo, techo y falta de autorización', () => {
    expect(
      cleaningBlockReason({
        kind: 'CLEANING',
        frost: true,
        windMs: 4,
        maxWindMs: 12,
        hail: false,
        roof: false,
        authorization: 'APPROVED',
      }),
    ).toMatch(/Helada/);
    expect(
      cleaningBlockReason({
        kind: 'CLEANING',
        frost: false,
        windMs: 18,
        maxWindMs: 12,
        hail: false,
        roof: false,
        authorization: 'APPROVED',
      }),
    ).toMatch(/Viento/);
    expect(
      cleaningBlockReason({
        kind: 'VISUAL_INSPECTION',
        frost: false,
        windMs: 4,
        maxWindMs: 12,
        hail: false,
        roof: false,
        authorization: 'PENDING',
      }),
    ).toMatch(/autorización/);
    expect(
      cleaningBlockReason({
        kind: 'CLEANING',
        frost: false,
        windMs: 4,
        maxWindMs: 12,
        hail: false,
        roof: true,
        authorization: 'APPROVED',
      }),
    ).toMatch(/Cubierta/);
  });
});
