/**
 * Modelo climático regional del NOA (ampliación posterior documentada en
 * `ideas/demos/helioguard_lab.html`, no presente en `Idea Original`).
 */

export type NoaRegionId = 'abra' | 'tilcara' | 'jujuy' | 'oran' | 'cafayate' | 'tartagal';

export interface NoaRegionProfile {
  id: NoaRegionId;
  name: string;
  zone: string;
  detail: string;
  tmin: number;
  tmax: number;
  cloud: number;
  soil: number;
  wind: number;
  frost: boolean;
}

export interface NoaSimInput {
  regionId: NoaRegionId;
  kwp: number;
  soilingPct: number;
  cloudPct: number;
  windKmH: number;
  frost: boolean;
  failure: boolean;
  storm: boolean;
}

export interface NoaPoint {
  minute: number;
  label: string;
  expectedKw: number;
  powerKw: number;
  irrWm2: number;
  tempC: number;
  frostFactor: number;
}

export interface NoaDayResult {
  region: NoaRegionProfile;
  points: NoaPoint[];
  totals: {
    expectedKwh: number;
    actualKwh: number;
    dustKwh: number;
    frostKwh: number;
    faultKwh: number;
  };
  frostIgnored: boolean;
}

export const NOA_REGIONS: Record<NoaRegionId, NoaRegionProfile> = {
  abra: {
    id: 'abra',
    name: 'Abra Pampa',
    zone: 'Puna',
    detail: 'Puna · heladas, viento y polvo · perfil ilustrativo',
    tmin: -13,
    tmax: 17,
    cloud: 8,
    soil: 18,
    wind: 32,
    frost: true,
  },
  tilcara: {
    id: 'tilcara',
    name: 'Tilcara',
    zone: 'Quebrada',
    detail: 'Quebrada · radiación, sombras y polvo · perfil ilustrativo',
    tmin: -3,
    tmax: 25,
    cloud: 15,
    soil: 15,
    wind: 23,
    frost: false,
  },
  jujuy: {
    id: 'jujuy',
    name: 'San Salvador de Jujuy',
    zone: 'Valles',
    detail: 'Valles · tormentas y humedad · perfil ilustrativo',
    tmin: 12,
    tmax: 31,
    cloud: 32,
    soil: 8,
    wind: 15,
    frost: false,
  },
  oran: {
    id: 'oran',
    name: 'Orán',
    zone: 'Yungas',
    detail: 'Yungas · calor y humedad · perfil ilustrativo',
    tmin: 20,
    tmax: 37,
    cloud: 37,
    soil: 9,
    wind: 17,
    frost: false,
  },
  cafayate: {
    id: 'cafayate',
    name: 'Cafayate',
    zone: 'Valles Calchaquíes',
    detail: 'Valles Calchaquíes · viento y polvo · perfil ilustrativo',
    tmin: 5,
    tmax: 32,
    cloud: 13,
    soil: 22,
    wind: 34,
    frost: false,
  },
  tartagal: {
    id: 'tartagal',
    name: 'Tartagal',
    zone: 'Chaco salteño',
    detail: 'Chaco salteño · calor y humo estacional · perfil ilustrativo',
    tmin: 19,
    tmax: 40,
    cloud: 27,
    soil: 15,
    wind: 22,
    frost: false,
  },
};

export const NOA_REGION_LIST: readonly NoaRegionProfile[] = Object.values(NOA_REGIONS);

function clamp(v: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, v));
}

function round2(v: number): number {
  return Math.round(v * 100) / 100;
}

export function noaAmbientTemp(region: NoaRegionProfile, minute: number): number {
  const h = minute / 60;
  let frac: number;
  if (h <= 5) {
    frac = 0.13 + ((5 - h) / 5) * 0.25;
  } else if (h <= 15) {
    frac = (h - 5) / 10;
  } else {
    frac = Math.max(0.13, 1 - ((h - 15) / 12) * 0.88);
  }
  return region.tmin + (region.tmax - region.tmin) * frac;
}

export function simulateNoaDay(input: NoaSimInput): NoaDayResult {
  const region = NOA_REGIONS[input.regionId];
  const minSun = 390;
  const maxSun = 1140;
  const stepMin = 10;
  const points: NoaPoint[] = [];

  let expectedKwh = 0;
  let actualKwh = 0;
  let dustKwh = 0;
  let frostKwh = 0;
  let faultKwh = 0;

  for (let m = minSun; m < maxSun; m += stepMin) {
    const mid = m + stepMin / 2;
    const dayfrac = (mid - minSun) / (maxSun - minSun);
    const day = dayfrac > 0 && dayfrac < 1;
    const sun = day ? Math.sin(Math.PI * dayfrac) ** 1.23 : 0;
    const cloudAtt = 1 - clamp(input.cloudPct, 0, 100) * 0.0074;
    const microCloud = 1 - (clamp(input.cloudPct, 0, 100) / 100) * 0.1 * Math.sin(mid * 0.051) ** 2;
    const stormAtt = input.storm ? 0.56 : 1;
    const irr = clamp(980 * sun * cloudAtt * microCloud * stormAtt, 0, 1200);
    const ambient = noaAmbientTemp(region, mid);
    const cellTemp = ambient + irr * 0.022 * (1 - clamp(input.windKmH, 0, 120) * 0.0013);
    const tempFactor = clamp(1 - 0.0036 * (cellTemp - 25), 0.7, 1.18);
    const expected = clamp(input.kwp * (irr / 1000) * 0.92 * tempFactor, 0, input.kwp * 1.05);

    const dustFactor = 1 - clamp(input.soilingPct, 0, 75) * 0.0046;
    let frostFactor = 1;
    if (input.frost && region.tmin <= 0 && day) {
      const fromSun = mid - minSun;
      const thaw = clamp((fromSun - 35) / 220, 0, 1);
      const frostStrength = clamp((-region.tmin + 2) / 20, 0.2, 0.9);
      frostFactor = 1 - frostStrength * (1 - thaw);
    }
    const faultFactor = input.failure ? 0.48 : 1;
    const power = expected * dustFactor * frostFactor * faultFactor;

    const dtH = stepMin / 60;
    expectedKwh += expected * dtH;
    actualKwh += power * dtH;
    dustKwh += expected * (1 - dustFactor) * dtH;
    frostKwh += expected * dustFactor * (1 - frostFactor) * dtH;
    faultKwh += expected * dustFactor * frostFactor * (1 - faultFactor) * dtH;

    const endMin = m + stepMin;
    points.push({
      minute: endMin,
      label: `${String(Math.floor(endMin / 60)).padStart(2, '0')}:${String(endMin % 60).padStart(2, '0')}`,
      expectedKw: round2(expected),
      powerKw: round2(power),
      irrWm2: Math.round(irr),
      tempC: round2(ambient),
      frostFactor: round2(frostFactor),
    });
  }

  return {
    region,
    points,
    totals: {
      expectedKwh: round2(expectedKwh),
      actualKwh: round2(actualKwh),
      dustKwh: round2(dustKwh),
      frostKwh: round2(frostKwh),
      faultKwh: round2(faultKwh),
    },
    frostIgnored: input.frost && region.tmin > 0,
  };
}
