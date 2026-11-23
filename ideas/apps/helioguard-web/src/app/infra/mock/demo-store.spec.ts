import { describe, expect, it } from 'vitest';
import { DemoStore, STORAGE_KEY } from './demo-store';

describe('DemoStore', () => {
  it('persiste el replay y no aumenta las mediciones válidas', () => {
    localStorage.removeItem(STORAGE_KEY);
    const store = new DemoStore();
    store.reset();
    store.replay();
    expect(store.acceptedCount()).toBe(1);
    const events = store.state().integrityEvents.filter((event) => event.eventType === 'REPLAY');
    expect(events.length).toBe(1);
    expect(events[0]?.status).toBe('QUARANTINED');
    store.replay();
    expect(store.acceptedCount()).toBe(1);
    expect(store.state().integrityEvents.filter((event) => event.eventType === 'REPLAY').length).toBe(2);
  });

  it('la inyección queda en revisión y el reinicio se acepta', () => {
    localStorage.removeItem(STORAGE_KEY);
    const store = new DemoStore();
    store.reset();
    store.injectFalse();
    const security = store.state().securityIncidents[0];
    expect(security?.title).toMatch(/inconsistente/);
    expect(security?.resolutionNote).toMatch(/No hay prueba de intrusión/);
    expect(store.acceptedCount()).toBe(0);
    store.legitimateReboot();
    expect(store.acceptedCount()).toBe(1);
    expect(store.state().samples[0]?.legitimateReboot).toBe(true);
  });

  it('no despacha una limpieza con helada ni sin autorización', () => {
    localStorage.removeItem(STORAGE_KEY);
    const store = new DemoStore();
    store.reset();
    store.activateScenario('soiling');
    const incident = store.state().incidents.find((item) => item.dedupKey === 'soiling-puna-a2');
    const orderId = store.openWorkOrder(incident!.id);
    const missionId = store.proposeMission(orderId!, 'CLEANING');
    store.dispatch(missionId!);
    expect(store.state().missions[0]?.status).toBe('DRAFT');
    store.activateScenario('frost');
    store.reviewSafety(missionId!);
    expect(store.state().missions[0]?.status).toBe('SAFETY_REVIEW');
    expect(store.state().notice).toMatch(/Helada/);
  });

  it('cambia el monto si cambia la tarifa', () => {
    localStorage.removeItem(STORAGE_KEY);
    const store = new DemoStore();
    store.reset();
    store.activateScenario('soiling');
    const before = store.state().economicAssessments.find((item) => item.id === 'eco-puna-live')?.estimatedLossAmount;
    store.updateTariff('rate-puna', 400, 80);
    const after = store.state().economicAssessments.find((item) => item.id === 'eco-puna-live')?.estimatedLossAmount;
    expect(after).toBeGreaterThan(before ?? 0);
  });
});
