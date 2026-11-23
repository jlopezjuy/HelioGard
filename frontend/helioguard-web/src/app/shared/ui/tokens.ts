export interface ContrastPair {
  name: string;
  foreground: string;
  background: string;
  minimum: number;
}

/** Pares usados por el catálogo. El ámbar y el coral no se miden como texto chico. */
export const STITCH_TEXT_PAIRS: readonly ContrastPair[] = [
  { name: 'texto sobre canvas', foreground: '#e2e8f0', background: '#070a0d', minimum: 7 },
  { name: 'secundario sobre canvas', foreground: '#94a3b8', background: '#070a0d', minimum: 4.5 },
  { name: 'secundario sobre tarjeta', foreground: '#94a3b8', background: '#10161d', minimum: 4.5 },
  { name: 'texto sobre neón', foreground: '#070a0d', background: '#e6ff00', minimum: 7 },
  { name: 'neón sobre canvas', foreground: '#e6ff00', background: '#070a0d', minimum: 7 },
  { name: 'menta clara sobre tarjeta', foreground: '#6ee7b7', background: '#10161d', minimum: 4.5 },
  { name: 'aviso claro sobre tarjeta', foreground: '#fde68a', background: '#10161d', minimum: 4.5 },
  { name: 'alerta clara sobre tarjeta', foreground: '#fecaca', background: '#10161d', minimum: 4.5 },
];

export function contrastRatio(foreground: string, background: string): number {
  const lighter = Math.max(relativeLuminance(foreground), relativeLuminance(background));
  const darker = Math.min(relativeLuminance(foreground), relativeLuminance(background));
  return (lighter + 0.05) / (darker + 0.05);
}

function relativeLuminance(color: string): number {
  const [red, green, blue] = channels(color).map((channel) => {
    const value = channel / 255;
    return value <= 0.04045 ? value / 12.92 : ((value + 0.055) / 1.055) ** 2.4;
  });
  return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
}

function channels(color: string): [number, number, number] {
  const hex = color.trim().replace('#', '');
  if (!/^[0-9a-f]{6}$/i.test(hex)) {
    throw new Error(`Color no reconocido: ${color}`);
  }
  return [parseInt(hex.slice(0, 2), 16), parseInt(hex.slice(2, 4), 16), parseInt(hex.slice(4, 6), 16)];
}
