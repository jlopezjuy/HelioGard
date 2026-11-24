# Plan de implementación de simulaciones por sprints (`PLAN_SIMULACIONES_SPRINTS.md`)

Priorización basada en la auditoría de cobertura (`AUDITORIA_IDEA_ORIGINAL.md`) y el nivel de definición documental en `Idea Original` e `ideas/demos/`.

## Criterios de priorización
- **Prioridad P0 (implementación inmediata en este ciclo):** Brechas directas respecto de `Idea Original` cuyas fórmulas y umbrales están 100 % documentados en `Idea Original/app.py` e `index.html` y que corrigen huecos reales de detección en Angular.
- **Prioridad P1 (siguiente ciclo):** Ampliaciones de `ideas/demos/helioguard_lab.html` (perfiles regionales del NOA, descomposición multiplicativa de pérdidas).
- **Prioridad P2 (futuro / requiere backend real):** Persistencia JPA de las 47 entidades JDL en `backend/`, ingestión MQTT/Modbus real y firmas criptográficas Ed25519/HMAC.

---

## Sprint S1 (P0 — Ejecutado ahora): Fidelidad física e integridad de `Idea Original` en Angular

| Ítem | Alcance técnico | Archivos objetivo (`frontend/helioguard-web`) | Criterio de aceptación |
|---|---|---|---|
| **S1.1 — Motor diurno fiel (`07:00–19:00`)** | Crear `diurnal-sim.ts` con curva solar `06:30–18:30`, `temp`, derating `-0.4 %/°C`, ensuciamiento `-0.012`/paso (piso `0.55`), recuperación `+0.05`/paso, 3 detectores (`Dato imposible`, `Posible replay`, `Rendimiento bajo` `ratio < 0.93`) y congelamiento de pérdida en `rojo` con banda muerta `3 %` | `src/app/core/domain/diurnal-sim.ts`, `src/app/core/domain/diurnal-sim.spec.ts` | Pruebas unitarias deterministas cubriendo normal, suciedad, replay, inyección, valores extremos (noche/cambio de día) y recuperación |
| **S1.2 — Refuerzo de `evaluateIngest` (`V × I` y stream congelado)** | Agregar verificación opcional `voltageV`/`currentA` (`|P - V·I| > 8 %` y techo relativo `1.25·esperada + 0.3 kW`) y `evaluateFrozenStream` (5 muestras idénticas de `(powerW, voltageV)`) sin romper contratos actuales de `DemoStore` ni `e2e/demo.spec.ts` | `src/app/core/domain/rules.ts`, `src/app/core/domain/actions.ts`, `src/app/infra/mock/demo-store.ts`, `src/app/core/domain/rules.spec.ts` | Todos los tests previos siguen en verde + nuevos tests para inyección `V × I` moderada y replay congelado de 5 muestras |
| **S1.3 — Servicio desacoplado e integración UI Stitch** | Crear `DiurnalSimulatorService` (Signals + Play/Pausa/Paso/Reinicio/Velocidad/Tarifa/kWp) e integrarlo en `/energia` (nueva pestaña `Simulación diurna`) y `/telemetria` (controles `Inyección V×I inconsistente` y `Stream congelado 5×`) usando solo componentes `hg-*` | `src/app/infra/mock/diurnal-simulator.service.ts`, `src/app/features/energy/*`, `src/app/features/telemetry/*`, `src/styles.css` | `ng test`, `ng build`, `e2e/demo.spec.ts` en verde y revisión visual en 1440px, 768px y 390px sin desbordes |

---

## Sprint S2 (P1 — Ejecutado): Perfiles regionales NOA (`ideas/demos/helioguard_lab.html`)

- Implementado en [noa-regional.ts](file:///Users/juanlopez/Proyectos/Solar/frontend/helioguard-web/src/app/core/domain/noa-regional.ts) e integrado como pestaña `Clima NOA (ampliación)` en `/energia` (`energy.html`) con las 6 zonas (`Abra Pampa`, `Tilcara`, `San Salvador de Jujuy`, `Orán`, `Cafayate`, `Tartagal`), descomposición secuencial de pérdidas (`Polvo`, `Escarcha`, `Falla inversor`) y etiqueta explícita de ampliación regional.

---

## Sprint S3 (P1 — Ejecutado): Simulador integrado de intervención y payback (`/mantenimiento`)

- Implementado en [intervention-sim.ts](file:///Users/juanlopez/Proyectos/Solar/frontend/helioguard-web/src/app/core/domain/intervention-sim.ts) e integrado en `/mantenimiento` (`maintenance.html`) con comparación de rutas (`Dron`, `Técnico`, `Propietario`), cálculo de `paybackDays` y compuertas de seguridad por helada, viento fuerte y acceso en techo.

---

## Sprint S4 (P2 — Dependiente de backend real): Persistencia JPA y telemetría real

- Implementar en `backend/` las entidades de `ideas/specs/domain.v4.draft.jdl`, endpoints `/api/telemetria`, verificación criptográfica real de payloads en gateway Raspberry Pi y broker MQTT TLS (fuera del alcance frontend-mock actual).
