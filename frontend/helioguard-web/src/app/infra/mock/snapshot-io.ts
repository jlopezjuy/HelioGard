import { SNAPSHOT_LIST_KEYS, type DemoSnapshot } from '../../core/domain/model';

export function parseSnapshot(raw: string): { ok: true; snapshot: DemoSnapshot } | { ok: false; reason: string } {
  let value: unknown;
  try {
    value = JSON.parse(raw);
  } catch {
    return { ok: false, reason: 'El archivo no es JSON.' };
  }
  if (!value || typeof value !== 'object') {
    return { ok: false, reason: 'El JSON no es un objeto de demo.' };
  }
  const record = value as Partial<DemoSnapshot>;
  if (record.meta?.version !== 'v4' || record.meta.seed !== 'helioguard-hackaton-v4') {
    return { ok: false, reason: 'La versión o la semilla no coinciden con helioguard-hackaton-v4.' };
  }
  for (const key of SNAPSHOT_LIST_KEYS) {
    if (!Array.isArray(record[key])) {
      return { ok: false, reason: `Falta la lista ${key}.` };
    }
  }
  if (!record.session?.userId || !record.session.organizationId || !record.clockIso || !record.scenario) {
    return { ok: false, reason: 'Faltan sesión, reloj o escenario.' };
  }
  const snapshot = record as DemoSnapshot;
  if (!snapshot.scenario.lossScenario) {
    snapshot.scenario.lossScenario = snapshot.scenario.soiling ? 'SOILING' : 'NONE';
  }
  return { ok: true, snapshot };
}
