/**
 * Simulador de intervención y retorno económico (ampliación posterior documentada en
 * `ideas/demos/helioguard_flow.html`, no presente en `Idea Original`).
 */

export type InterventionRouteId = 'drone' | 'technician' | 'owner';
export type InterventionWeather = 'clear' | 'wind' | 'frost';
export type InterventionAccess = 'ground' | 'roof';

export interface InterventionRouteSpec {
  id: InterventionRouteId;
  title: string;
  name: string;
  expectedKwhDay: number;
  costArs: number;
  recoveryRatio: number;
  person: string;
  note: string;
}

export interface InterventionMetrics {
  expectedKwh: number;
  lossKwh: number;
  beforeKwh: number;
  recoveredKwh: number;
  afterKwh: number;
  lossArs: number;
  recoveredArs: number;
  costArs: number;
  paybackDays: number;
}

export interface InterventionSafetyResult {
  ok: boolean;
  reason: string;
}

export const INTERVENTION_ROUTES: Record<InterventionRouteId, InterventionRouteSpec> = {
  drone: {
    id: 'drone',
    title: 'Planta solar · limpieza con dron',
    name: 'Planta solar (Dron)',
    expectedKwhDay: 240,
    costArs: 45_000,
    recoveryRatio: 0.9,
    person: 'Piloto y operador especializados',
    note: 'Vuelo y limpieza solo bajo autorización, personal habilitado y compatibilidad del equipo y módulos.',
  },
  technician: {
    id: 'technician',
    title: 'Comercio · visita de técnico',
    name: 'Comercio (Técnico)',
    expectedKwhDay: 40,
    costArs: 22_000,
    recoveryRatio: 0.9,
    person: 'Técnico solar asignado',
    note: 'La visita exige evaluación de seguridad eléctrica, acceso y condiciones de trabajo.',
  },
  owner: {
    id: 'owner',
    title: 'Hogar · limpieza del propietario',
    name: 'Hogar (Propietario)',
    expectedKwhDay: 14,
    costArs: 2_500,
    recoveryRatio: 0.8,
    person: 'Propietario',
    note: 'Solo para paneles accesibles desde el suelo y con condiciones seguras; nunca subir a un techo.',
  },
};

export const INTERVENTION_ROUTE_LIST: readonly InterventionRouteSpec[] = Object.values(INTERVENTION_ROUTES);

function round2(v: number): number {
  return Math.round(v * 100) / 100;
}

export function computeInterventionMetrics(
  routeId: InterventionRouteId,
  dustPct: number,
  tariffArsPerKwh: number,
): InterventionMetrics {
  const spec = INTERVENTION_ROUTES[routeId];
  const clampedDust = Math.max(0, Math.min(80, dustPct));
  const clampedTariff = Math.max(0, tariffArsPerKwh);
  const lossKwh = (spec.expectedKwhDay * clampedDust) / 100;
  const beforeKwh = spec.expectedKwhDay - lossKwh;
  const recoveredKwh = lossKwh * spec.recoveryRatio;
  const afterKwh = beforeKwh + recoveredKwh;
  const lossArs = lossKwh * clampedTariff;
  const recoveredArs = recoveredKwh * clampedTariff;
  const paybackDays = recoveredArs > 0 ? spec.costArs / recoveredArs : Infinity;

  return {
    expectedKwh: round2(spec.expectedKwhDay),
    lossKwh: round2(lossKwh),
    beforeKwh: round2(beforeKwh),
    recoveredKwh: round2(recoveredKwh),
    afterKwh: round2(afterKwh),
    lossArs: Math.round(lossArs),
    recoveredArs: Math.round(recoveredArs),
    costArs: spec.costArs,
    paybackDays: Number.isFinite(paybackDays) ? round2(paybackDays) : Infinity,
  };
}

export function evaluateInterventionSafety(
  routeId: InterventionRouteId,
  weather: InterventionWeather,
  access: InterventionAccess,
): InterventionSafetyResult {
  if (weather === 'frost') {
    return {
      ok: false,
      reason:
        'Riesgo de helada. No se permite iniciar una limpieza húmeda: esperá condiciones seguras y verificá temperatura del módulo y secado.',
    };
  }
  if (routeId === 'drone' && weather === 'wind') {
    return {
      ok: false,
      reason:
        'Viento fuerte. La operación del dron queda bloqueada. Seleccioná condiciones aptas o evaluá una visita técnica autorizada.',
    };
  }
  if (routeId === 'owner' && access === 'roof') {
    return {
      ok: false,
      reason:
        'Los paneles están en un techo. No se habilita la limpieza por el propietario; solicitá un técnico con medios de acceso y protección apropiados.',
    };
  }
  if (routeId === 'owner' && weather === 'wind') {
    return {
      ok: false,
      reason: 'Condiciones de viento: se pospone la limpieza del propietario hasta que el área sea segura.',
    };
  }
  return { ok: true, reason: '' };
}
