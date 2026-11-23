import { describe, expect, it } from 'vitest';
import { STITCH_TEXT_PAIRS, contrastRatio } from './tokens';

describe('contraste del catálogo Stitch', () => {
  it.each(STITCH_TEXT_PAIRS)('$name supera el mínimo', (pair) => {
    expect(contrastRatio(pair.foreground, pair.background)).toBeGreaterThanOrEqual(pair.minimum);
  });
});
