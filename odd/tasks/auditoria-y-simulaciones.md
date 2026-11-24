# Auditoría de Idea Original, Matriz de Trazabilidad y Simulaciones HelioGuard

Feature: `auditoria-y-simulaciones` | Target: `ideas/docs/` + `frontend/helioguard-web`

## Specs

- **S1**: `ideas/docs/AUDITORIA_IDEA_ORIGINAL.md` — Investigación exhaustiva de `Idea Original`, comparación con estado actual, clasificación por estado, brechas y separación de ampliaciones SDD.
- **S2**: `ideas/docs/MATRIZ_TRAZABILIDAD.md` — Matriz de trazabilidad de requisitos originales (`ORIG-*`) vinculados a fuentes, JDL/SDD, código Angular y pruebas.
- **S3**: `ideas/docs/ESCENARIOS_SIMULACION.md` — Análisis de procesos simulables (condiciones, entradas, acciones, reglas/fórmulas, cambios de estado, eventos/alertas, resultado esperado, verificación).
- **S4**: `ideas/docs/PLAN_SIMULACIONES_SPRINTS.md` — Plan priorizado por sprints para implementar las simulaciones faltantes/incompletas en Angular.
- **S5**: Implementación Angular P0 (`diurnal-sim.ts`, mejoras en `rules.ts`/`actions.ts`, `DiurnalSimulatorService`, integración Stitch en `/energia` y `/telemetria` sin romper funciones existentes).
- **S6**: Validación completa (`diurnal-sim.spec.ts`, `ng test`, `ng build`, Playwright desktop/tablet/mobile, `e2e/demo.spec.ts`) y entregable `ideas/docs/VALIDACION_SIMULACIONES.md`.

## Tasks

- [x] **T1** (`S1`): Crear `ideas/docs/AUDITORIA_IDEA_ORIGINAL.md` — route: inline
- [x] **T2** (`S2`): Crear `ideas/docs/MATRIZ_TRAZABILIDAD.md` — route: inline
- [x] **T3** (`S3`): Crear `ideas/docs/ESCENARIOS_SIMULACION.md` — route: inline
- [x] **T4** (`S4`): Crear `ideas/docs/PLAN_SIMULACIONES_SPRINTS.md` — route: inline
- [x] **T5** (`S5`): Implementar motor `diurnal-sim.ts`, reglas `V×I` / replay congelado en `rules.ts`, servicio `DiurnalSimulatorService` e integración UI en `/energia` y `/telemetria` — route: inline
- [x] **T6** (`S6`): Crear tests unitarios, ejecutar `ng test`, `ng build`, validación responsive en navegador (1440/768/390) y generar `ideas/docs/VALIDACION_SIMULACIONES.md` — route: inline

## Log

- **L1**: Usuario solicitó auditoría completa de `Idea Original` vs proyecto actual (`ideas`, `frontend/helioguard-web`, `ideas/maquetas`, `ideas/demos`), generación de los 5 entregables en `ideas/docs/`, implementación prioritaria en Angular de las simulaciones faltantes/incompletas con requisitos claros, y validación completa sin modificar `Idea Original`.
