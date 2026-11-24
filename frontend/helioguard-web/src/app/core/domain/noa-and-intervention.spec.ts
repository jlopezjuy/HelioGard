import { describe, expect, it } from 'vitest';
import { computeInterventionMetrics, evaluateInterventionSafety } from './intervention-sim';
import { NOA_REGION_LIST, simulateNoaDay } from './noa-regional';

describe('Modelo regional NOA (Sprint S2)', () => {
  it('incluye las 6 zonas climáticas del NOA y calcula descomposición secuencial en Abra Pampa', () => {
    expect(NOA_REGION_LIST).toHaveLength(6);
    const res = simulateNoaDay({
      regionId: 'abra',
      kwp: 6.6,
      soilingPct: 20,
      cloudPct: 8,
      windKmH: 32,
      frost: true,
      failure: true,
      storm: false,
    });
    expect(res.frostIgnored).toBe(false);
    expect(res.totals.expectedKwh).toBeGreaterThan(res.totals.actualKwh);
    expect(res.totals.dustKwh).toBeGreaterThan(0);
    expect(res.totals.frostKwh).toBeGreaterThan(0);
    expect(res.totals.faultKwh).toBeGreaterThan(0);
    expect(
      res.totals.actualKwh + res.totals.dustKwh + res.totals.frostKwh + res.totals.faultKwh,
    ).toBeCloseTo(res.totals.expectedKwh, 0);
  });

  it('ignora escarcha en regiones con mínima > 0 °C (Orán)', () => {
    const res = simulateNoaDay({
      regionId: 'oran',
      kwp: 6.6,
      soilingPct: 0,
      cloudPct: 10,
      windKmH: 15,
      frost: true,
      failure: false,
      storm: false,
    });
    expect(res.frostIgnored).toBe(true);
    expect(res.totals.frostKwh).toBe(0);
  });
});

describe('Simulador de intervención y payback (Sprint S3)', () => {
  it('calcula pérdida, recuperación y días de payback según ruta, suciedad y tarifa', () => {
    // Dron: 240 kWh/d * 20% = 48 kWh/d pérdida; 90% rec = 43.2 kWh/d; * 180 ARS = 7776 ARS/d; 45000 / 7776 = 5.79 días
    const m = computeInterventionMetrics('drone', 20, 180);
    expect(m.lossKwh).toBe(48);
    expect(m.recoveredKwh).toBe(43.2);
    expect(m.recoveredArs).toBe(7776);
    expect(m.paybackDays).toBeCloseTo(5.79, 2);

    const zero = computeInterventionMetrics('owner', 0, 180);
    expect(zero.paybackDays).toBe(Infinity);
  });

  it('aplica compuertas de seguridad para helada, viento fuerte y acceso en techo', () => {
    expect(evaluateInterventionSafety('drone', 'frost', 'ground').ok).toBe(false);
    expect(evaluateInterventionSafety('drone', 'wind', 'ground').ok).toBe(false);
    expect(evaluateInterventionSafety('technician', 'wind', 'roof').ok).toBe(true);
    expect(evaluateInterventionSafety('owner', 'clear', 'roof').ok).toBe(false);
    expect(evaluateInterventionSafety('owner', 'wind', 'ground').ok).toBe(false);
    expect(evaluateInterventionSafety('owner', 'clear', 'ground').ok).toBe(true);
  });
});
