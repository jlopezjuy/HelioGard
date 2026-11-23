import { describe, expect, it } from 'vitest';
import { installationScope, roleAllows, visibleInstallations } from './access';
import { buildSeed } from '../../infra/mock/seed';

describe('aislamiento de la demo', () => {
  it('la propietaria solo ve Casa Andina', () => {
    const state = buildSeed();
    state.activeUserId = 'usr-home';
    state.activeOrganizationId = 'org-prop';
    const names = visibleInstallations(state).map((installation) => installation.name);
    expect(names).toEqual(['Casa Andina']);
    const salta = state.installations.find((installation) => installation.id === 'ins-salta');
    expect(installationScope(state, salta!)).toBe('NONE');
  });

  it('el prestador ve Puna y solo mantenimiento de la casa', () => {
    const state = buildSeed();
    const casa = state.installations.find((installation) => installation.id === 'ins-casa');
    expect(visibleInstallations(state).map((installation) => installation.id).sort()).toEqual(['ins-casa', 'ins-puna']);
    expect(installationScope(state, casa!)).toBe('MAINTENANCE');
  });

  it('un auditor no entra a economía', () => {
    expect(roleAllows('/economia', 'AUDITOR')).toBe(false);
    expect(roleAllows('/auditorias', 'AUDITOR')).toBe(true);
  });
});
