import { describe, expect, it } from 'vitest';
import { installationAccess } from './access';
import {
  approveMission,
  createResponseChain,
  cutWan,
  dispatchMission,
  messageExactReplay,
  messageImplausible,
  messageNewBoot,
  messageOriginal,
  messageSameSequence,
  publishExactReplay,
  publishImplausible,
  publishNewBoot,
  publishOriginal,
  publishSameSequence,
  restoreWan,
  reviewSafety,
  setScenarioFlag,
  setLossScenario,
  setSoiling,
} from './actions';
import { MODEL_COVERAGE } from './coverage';
import { SECTOR, type DemoSnapshot } from './model';
import {
  assertCauseAllocation,
  canTransition,
  deficitWh,
  evaluateIngest,
  lossAmountArs,
  wattHoursFromCounter,
  wattHoursFromPower,
} from './rules';
import { createSeed } from '../../infra/mock/seed';
import { parseSnapshot } from '../../infra/mock/snapshot-io';

function fresh(): DemoSnapshot {
  return createSeed();
}

describe('energía', () => {
  it('integra potencia en el tiempo y no trata la resta de kW como kWh', () => {
    expect(wattHoursFromPower(48_000, 900)).toBe(12_000);
    expect(wattHoursFromPower(31_000, 900)).toBe(7_750);
    expect(wattHoursFromPower(48_000, 900) - wattHoursFromPower(31_000, 900)).toBe(4_250);
    expect(48_000 - 31_000).not.toBe(4_250);
    expect(wattHoursFromCounter(1_000, 5_250)).toBe(4_250);
    expect(wattHoursFromCounter(5, 4)).toBe('invalid');
  });

  it('niega el déficit si el intervalo no es comparable', () => {
    expect(deficitWh(12_000, 7_750, true)).toEqual({ kind: 'value', deficitWh: 4_250 });
    expect(deficitWh(12_000, null, true).kind).toBe('unavailable');
    expect(deficitWh(12_000, 7_750, false)).toEqual({ kind: 'unavailable', label: 'sin estimación confiable' });
  });

  it('monetiza en ARS y no asigna toda la pérdida a suciedad', () => {
    expect(lossAmountArs(4_250, 0.7, 200, 80)).toBe(697);
    expect(assertCauseAllocation(4_250, [
      { cause: 'SOILING', estimatedWh: 2_550 },
      { cause: 'UNKNOWN', estimatedWh: 1_700 },
    ]).ok).toBe(true);
    expect(assertCauseAllocation(4_250, [{ cause: 'SOILING', estimatedWh: 4_250 }]).ok).toBe(false);
    expect(assertCauseAllocation(4_250, [
      { cause: 'SOILING', estimatedWh: 3_000 },
      { cause: 'UNKNOWN', estimatedWh: 2_000 },
    ]).ok).toBe(false);
    const soiled = setSoiling(fresh(), true);
    const loss = soiled.lossAssessments.find((item) => item.id === SECTOR.lossId);
    expect(loss?.totalLossWh).toBe(4_250);
    expect(soiled.economicAssessments.find((item) => item.id === SECTOR.economicId)?.estimatedLossAmount).toBe(697);
  });

  it('reparte inversor, nube, helada, granizo y humo con un residual desconocido', () => {
    for (const scenario of ['INVERTER', 'CLOUD', 'FROST', 'HAIL', 'SMOKE'] as const) {
      const next = setLossScenario(fresh(), scenario);
      const loss = next.lossAssessments.find((item) => item.id === SECTOR.lossId);
      const causes = next.lossCauses.filter((item) => item.lossAssessmentId === loss?.id);
      const sum = causes.reduce((total, item) => total + item.estimatedWh, 0);
      expect(sum).toBe(loss?.totalLossWh);
      expect(causes.some((item) => item.cause === 'UNKNOWN')).toBe(true);
      const soilingWh = causes.find((item) => item.cause === 'SOILING')?.estimatedWh ?? 0;
      expect(soilingWh).toBeLessThan(loss?.totalLossWh ?? 0);
      expect(next.scenario.soiling).toBe(false);
    }
  });
});

describe('integridad', () => {
  it('acepta el original, retiene el replay y el eventId repetido, y acepta un boot nuevo', () => {
    const seed = fresh();
    const limits = { maxPowerW: SECTOR.limitW, uncertaintyW: SECTOR.uncertaintyW };
    const original = messageOriginal(seed);
    expect(evaluateIngest([], original, limits).action).toBe('ACCEPT');
    const withOriginal = publishOriginal(seed);
    expect(withOriginal.scenario.ledger).toHaveLength(1);
    expect(withOriginal.scenario.ledger[0]?.acceptedForEnergy).toBe(true);
    const replay = publishExactReplay(withOriginal);
    expect(replay.scenario.lastProof?.eventType).toBe('DUPLICATE_EVENT');
    expect(replay.scenario.lastProof?.useInEnergy).toBe(false);
    expect(replay.scenario.ledger.filter((item) => item.acceptedForEnergy)).toHaveLength(1);
    const sequenced = publishSameSequence(replay);
    expect(sequenced.scenario.lastProof?.eventType).toBe('REPLAY');
    expect(sequenced.scenario.lastProof?.matchedKeys).toEqual(['deviceId', 'bootId', 'sequenceNumber']);
    const restarted = publishNewBoot(sequenced);
    expect(restarted.scenario.lastProof?.action).toBe('ACCEPT');
    expect(restarted.scenario.lastProof?.message.bootId).toBe('boot-8');
  });

  it('aísla un valor físicamente improbable sin declararlo ataque', () => {
    const result = publishImplausible(fresh());
    expect(result.scenario.lastProof?.eventType).toBe('PHYSICAL_INCONSISTENCY');
    expect(result.scenario.lastProof?.status).toBe('UNDER_REVIEW');
    expect(result.scenario.lastProof?.useInEnergy).toBe(false);
    expect(result.scenario.lastProof?.cryptographicallyVerified).toBe(false);
    expect(result.scenario.lastProof?.reason.toLowerCase()).not.toContain('ataque confirmado');
    const security = result.securityIncidents.find((item) => item.id === 'sec-evt-b2-spike');
    expect(security?.status).toBe('INVESTIGATING');
    expect(security?.resolutionNote).toContain('No se afirma intrusión');
  });

  it('encola sin WAN y sincroniza una sola vez', () => {
    const offline = cutWan(fresh());
    const buffered = publishOriginal(offline);
    expect(buffered.scenario.edgeBuffer).toHaveLength(1);
    expect(buffered.scenario.ledger).toHaveLength(0);
    const synced = restoreWan(buffered);
    expect(synced.scenario.edgeBuffer).toHaveLength(0);
    expect(synced.scenario.ledger.filter((item) => item.eventId === 'evt-b2-0007')).toHaveLength(1);
    const again = restoreWan(synced);
    expect(again.scenario.ledger.filter((item) => item.eventId === 'evt-b2-0007')).toHaveLength(1);
  });
});

describe('misión y tenant', () => {
  it('no despacha desde DRAFT y bloquea con viento', () => {
    expect(canTransition('DRAFT', 'IN_FLIGHT')).toBe(false);
    const draft = createResponseChain(fresh());
    expect(draft.missions.find((item) => item.id === 'msn-live-b2')?.status).toBe('DRAFT');
    expect(dispatchMission(draft).scenario.notice).toContain('No se puede pasar');
    const windy = setScenarioFlag(reviewSafety(draft), 'windBlocked', true);
    expect(approveMission(windy).scenario.notice).toContain('Autorización retenida');
    expect(windy.missions.find((item) => item.id === 'msn-live-b2')?.status).not.toBe('IN_FLIGHT');
  });

  it('autoriza y deja constancia de que la aprobación no es un permiso real', () => {
    const ready = reviewSafety(createResponseChain(fresh()));
    expect(ready.missions.find((item) => item.id === 'msn-live-b2')?.status).toBe('AWAITING_AUTHORIZATION');
    const approved = approveMission(ready);
    expect(approved.authorizations.find((item) => item.id === 'auth-live-b2')?.status).toBe('APPROVED');
    expect(approved.scenario.notice).toContain('no representa un permiso operativo real');
    const flying = dispatchMission(approved);
    expect(flying.missions.find((item) => item.id === 'msn-live-b2')?.status).toBe('IN_FLIGHT');
  });

  it('no mezcla instalaciones entre tenants', () => {
    const seed = fresh();
    expect(installationAccess(seed, 'ins-puna')).toBe('FULL');
    expect(installationAccess(seed, 'ins-casa')).toBe('MAINTENANCE');
    expect(installationAccess(seed, 'ins-salta')).toBe('NONE');
    const home = structuredClone(seed);
    home.session = { userId: 'usr-home', organizationId: 'org-prop' };
    expect(installationAccess(home, 'ins-puna')).toBe('NONE');
    expect(installationAccess(home, 'ins-casa')).toBe('FULL');
    const salta = structuredClone(seed);
    salta.session = { userId: 'usr-salta', organizationId: 'org-salta' };
    const visibleToSalta = salta.incidents.filter((item) => installationAccess(salta, item.installationId) !== 'NONE');
    expect(visibleToSalta.every((item) => item.organizationId === 'org-salta')).toBe(true);
    expect(installationAccess(salta, 'ins-salta')).toBe('FULL');
  });
});

describe('cobertura', () => {
  it('cubre las 47 entidades con fixture', () => {
    expect(MODEL_COVERAGE).toHaveLength(47);
    const seed = fresh();
    for (const row of MODEL_COVERAGE) {
      const list = seed[row.key];
      expect(Array.isArray(list), row.entity).toBe(true);
      expect(list.length, row.entity).toBeGreaterThan(0);
    }
    expect(parseSnapshot('{"meta":{"version":"v3"}}').ok).toBe(false);
    expect(parseSnapshot(JSON.stringify(seed)).ok).toBe(true);
  });

  it('usa los mensajes de secuencia previstos', () => {
    const seed = fresh();
    expect(messageExactReplay(seed).eventId).toBe(messageOriginal(seed).eventId);
    expect(messageSameSequence(seed).sequenceNumber).toBe(messageOriginal(seed).sequenceNumber);
    expect(messageSameSequence(seed).eventId).not.toBe(messageOriginal(seed).eventId);
    expect(messageNewBoot(seed).bootId).toBe('boot-8');
    expect(messageImplausible(seed).powerW).toBe(8_000_000);
  });
});
