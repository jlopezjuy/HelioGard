import { describe, expect, it } from 'vitest';
import {
  ORIGINAL_DEFAULTS,
  createDiurnalState,
  detectDiurnalAnomalies,
  expectedPowerKw,
  formatSimClock,
  irradianceAtHour,
  panelTempC,
  setDiurnalConfig,
  setDiurnalMode,
  stepDiurnalSimulation,
} from './diurnal-sim';

const MID_RNG = () => 0.5;

describe('Motor diurno fiel a Idea Original', () => {
  it('calcula curva solar, temperatura de panel y potencia esperada (-0.4 %/°C)', () => {
    expect(irradianceAtHour(6.0)).toBe(0);
    expect(irradianceAtHour(19.0)).toBe(0);
    const noonIrr = irradianceAtHour(12.5);
    expect(noonIrr).toBeCloseTo(1000, 5);
    const noonTemp = panelTempC(noonIrr, 0);
    expect(noonTemp).toBeCloseTo(45, 5);
    // 5 kW * 1.0 * (1 - 0.004 * (45 - 25)) = 4.60 kW
    expect(expectedPowerKw(5, noonIrr, noonTemp)).toBeCloseTo(4.6, 5);
    expect(formatSimClock(7)).toBe('07:00');
    expect(formatSimClock(12 + 35 / 60)).toBe('12:35');
  });

  it('opera en régimen normal sin falsas alarmas durante el día', () => {
    let state = createDiurnalState({ startHour: 11.0 });
    for (let i = 0; i < 12; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
    }
    expect(state.estado).toBe('verde');
    expect(state.alertas).toHaveLength(0);
    expect(state.suciedad).toBe(1.0);
    expect(state.perdidaKwh).toBe(0);
    expect(state.serie).toHaveLength(12);
  });

  it('degrada por suciedad (-0.012/paso hasta 0.55), dispara Rendimiento bajo (< 0.93 en >= 6 muestras) y se recupera en normal', () => {
    let state = createDiurnalState({ startHour: 11.0 });
    state = setDiurnalMode(state, 'suciedad');

    for (let i = 0; i < 14; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
    }
    expect(state.suciedad).toBeLessThan(0.9);
    expect(state.estado).toBe('amarillo');
    expect(state.alertas.some((a) => a.titulo === 'Rendimiento bajo')).toBe(true);
    expect(state.perdidaKwh).toBeGreaterThan(0);
    expect(state.perdidaPesos).toBe(Math.round(state.perdidaKwh * ORIGINAL_DEFAULTS.tariffArsPerKwh));

    // Piso físico 0.55 (0.832 -> 0.55 requiere <= 38 pasos desde el inicio)
    for (let i = 0; i < 30; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
    }
    expect(state.suciedad).toBe(0.55);

    // Recuperación en modo normal (+0.05/paso hasta 1.0 y limpieza de la ventana de 12 muestras)
    state = setDiurnalMode(state, 'normal');
    for (let i = 0; i < 20; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
    }
    expect(state.suciedad).toBe(1.0);
    expect(state.estado).toBe('verde');
  });

  it('detecta ataque replay exactamente en la 5ta lectura idéntica y congela la pérdida económica', () => {
    let state = createDiurnalState({ startHour: 11.0 });
    state = setDiurnalMode(state, 'suciedad');
    for (let i = 0; i < 8; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
    }
    const lossBeforeReplay = state.perdidaKwh;

    state = setDiurnalMode(state, 'replay');
    for (let i = 0; i < 4; i += 1) {
      state = stepDiurnalSimulation(state, MID_RNG);
      expect(state.alertas.some((a) => a.titulo === 'Posible replay')).toBe(false);
    }

    // 5to tick congelado: dispara Posible replay y congela pérdida
    state = stepDiurnalSimulation(state, MID_RNG);
    expect(state.estado).toBe('rojo');
    expect(state.alertas.some((a) => a.titulo === 'Posible replay')).toBe(true);
    const lossAtRed = state.perdidaKwh;
    state = stepDiurnalSimulation(state, MID_RNG);
    expect(state.perdidaKwh).toBe(lossAtRed);
    expect(lossAtRed).toBeGreaterThanOrEqual(lossBeforeReplay);
  });

  it('detecta inyección falsa inmediatamente (Dato imposible) y congela pérdidas en rojo', () => {
    let state = createDiurnalState({ startHour: 12.0 });
    state = stepDiurnalSimulation(state, MID_RNG);
    const lossBefore = state.perdidaKwh;

    state = setDiurnalMode(state, 'inyeccion');
    state = stepDiurnalSimulation(state, MID_RNG);
    expect(state.estado).toBe('rojo');
    expect(state.alertas.some((a) => a.titulo === 'Dato imposible')).toBe(true);
    expect(state.perdidaKwh).toBe(lossBefore);
  });

  it('cubre condiciones extremas: reinicio de jornada 19:00 -> 07:00, piso 0.1 kW en V×I y cambio de tarifa', () => {
    let state = createDiurnalState({ startHour: 18.85, peakKw: 10, tariffArsPerKwh: 200 });
    state = stepDiurnalSimulation(state, MID_RNG);
    state = stepDiurnalSimulation(state, MID_RNG);
    expect(state.dayRollovers).toBe(1);
    expect(state.hora).toBe(7.0);
    expect(state.serie).toHaveLength(0);

    // Prueba directa del piso 0.1 kW en V×I: 0.25 kW con V*I = 0 kW dispara Dato imposible
    const alerts = detectDiurnalAnomalies([], {
      hora: 12,
      reloj: '12:00',
      modo: 'normal',
      potenciaKw: 0.25,
      tensionV: 380,
      corrienteA: 0,
      irradianciaWm2: 500,
      temperaturaC: 30,
      esperadaKw: 2.5,
    });
    expect(alerts.some((a) => a.titulo === 'Dato imposible')).toBe(true);

    const updated = setDiurnalConfig({ ...state, perdidaKwh: 2.5 }, { tariffArsPerKwh: 150 });
    expect(updated.perdidaPesos).toBe(375);
  });
});
