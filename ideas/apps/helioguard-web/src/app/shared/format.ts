export function whLabel(value: number | null): string {
  if (value === null || Number.isNaN(value)) {
    return 'sin medición';
  }
  const absolute = Math.abs(value);
  if (absolute >= 1_000_000) {
    return `${(value / 1_000_000).toLocaleString('es-AR', { maximumFractionDigits: 2 })} MWh`;
  }
  if (absolute >= 1000) {
    return `${(value / 1000).toLocaleString('es-AR', { maximumFractionDigits: 1 })} kWh`;
  }
  return `${Math.round(value).toLocaleString('es-AR')} Wh`;
}

export function powerLabel(watts: number): string {
  const absolute = Math.abs(watts);
  if (absolute >= 1_000_000) {
    return `${(watts / 1_000_000).toLocaleString('es-AR', { maximumFractionDigits: 2 })} MW`;
  }
  if (absolute >= 1000) {
    return `${(watts / 1000).toLocaleString('es-AR', { maximumFractionDigits: 1 })} kW`;
  }
  return `${Math.round(watts).toLocaleString('es-AR')} W`;
}

export function arsLabel(amount: number): string {
  return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', maximumFractionDigits: 0 }).format(amount);
}
