import { describe, expect, it } from 'vitest';
import { contrastRatio } from './contrast';

const cream = '#f7f4ee';

describe('contraste de los tokens', () => {
  it('el texto de interfaz supera AA', () => {
    expect(contrastRatio('#24302d', cream)).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#4b5a57', cream)).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#315d57', cream)).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#6b443d', cream)).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#a53232', cream)).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio(cream, '#8f432e')).toBeGreaterThanOrEqual(4.5);
  });

  it('el ámbar no se usa como texto chico sobre marfil', () => {
    expect(contrastRatio('#9a6e2c', cream)).toBeLessThan(4.5);
  });

  it('el anillo de foco supera el contraste no textual', () => {
    expect(contrastRatio('#365d65', cream)).toBeGreaterThanOrEqual(3);
  });
});
