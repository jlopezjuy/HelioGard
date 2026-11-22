# HelioGuard — SDD v4 | Hackatón Jujuy

**Entrega consolidada y limpia — 22/11/2026.** Plataforma para monitoreo, auditoría y respuesta ante anomalías de infraestructura solar, con una capa verificable de integridad de datos.

## Qué contiene y qué NO contiene

- Un **plan técnico y funcional** para backend Java/JHipster, dashboard Angular/Tailwind, app Flutter, edge Raspberry Pi/ESP32 y despliegue Docker.
- **JDL v4 BORRADOR** con **47 entidades y 114 relaciones declaradas**. Requiere parser JHipster, generación, revisión de Liquibase, normalización y pruebas de RLS antes de considerarlo implementable.
- Un **prompt maestro** para que Codex, Claude Code u otra IA implemente Angular totalmente navegable **sin backend**, con repositorios mock y escenarios reproducibles; el código Angular no está generado en este ZIP.
- Contratos para telemetría, integridad y misiones, con ejemplos JSON sintéticos.
- Tres simuladores HTML autocontenidos heredados y renombrados (lab, limpieza, vista orbital); son **demos conceptuales**, no están conectados a satélites, sensores ni drones reales. El simulador orbital incluye un módulo de integridad/replay didáctico.
- Infraestructura Compose de referencia. Algunas imágenes `helioguard/*` son placeholders; NO son un despliegue productivo listo.

## Tres afirmaciones obligatorias en toda presentación

1. La irradiancia estimada por satélites ayuda a modelar producción **pero Sentinel-2/GOES no hacen termografía de paneles individuales**. Localizar strings requiere datos por string/inversor y/o inspección por dron.
2. Lecturas duplicadas se pueden rechazar mediante identidad + identificador de evento + `bootId/sequenceNumber`; una inconsistencia con modelo físico **no demuestra intención maliciosa**.
3. El software **propone** misiones de dron; en producción el despegue requiere operador y verificaciones técnicas, meteorológicas y regulatorias. Nadie debe rociar paneles helados con agua.

## Índice de lectura (en orden)

| Archivo | Propósito |
|---|---|
| `docs/00_ARQUITECTURA_MAESTRA_SDD.md` | Arquitectura, alcance, límites, NFR, roadmap |
| `specs/domain.v4.draft.jdl` | Fuente única de verdad para entidades y enums |
| `docs/01_MODELO_ER_3FN_MULTITENANT.md` | ER, normalización, acceso delegado, RLS |
| `docs/02_SPRINTS_INTEGRALES_S0_S10.md` | Plan técnico end-to-end JHipster/Flutter/Edge |
| `docs/03_SPRINTS_FRONTEND_DEMO_F0_F7.md` | Frontend Angular-first para hackatón |
| `docs/04_DESIGN_SYSTEM_QUEBRADA.md` | Identidad visual y criterios anti-slop |
| `docs/05_FRONTEND_JDL_MATRIX.md` | Cobertura 47 entidades con destinos UX |
| `docs/06_SEGURIDAD_TELEMETRIA_ATAQUES.md` | Replay, alteración, cuarentena, procedencia |
| `docs/07_SATELITES_DRONES_LIMITACIONES.md` | Qué pueden hacer satélites/drones y qué no |
| `docs/08_PLAN_QA_EVIDENCIAS.md` | Criterios de aceptación, pruebas y evidencias |
| `docs/09_MODELO_NEGOCIO_AUDITORIA_COBERTURA.md` | Auditoría, planes, seguros externos, métricas |
| `docs/10_DEMO_GUION_PITCH.md` | Pitch y demo de 2 minutos, respuestas a jurado |
| `docs/11_CLI_DOCKER_DEPLOY.md` | CLI oficiales y orquestación por entorno |
| `prompts/PROMPT_MAESTRO_IA_FRONTEND_ANGULAR_MOCK.md` | Instrucción ejecutable para IA del frontend |
| `prompts/PROMPT_SDD_IMPLEMENTACION_GLOBAL.md` | Prompt para implementar roadmap general |

## Orden recomendado para arrancar

1. **Hackatón:** ejecutar prompt de frontend desde la raíz del repositorio, usando JDL v4 + fixtures + design system. Demo solo mocks, cero HTTP.
2. **Sprint 0.5:** verificar JDL con generador, migraciones, políticas tenant y cardinalidades. No generar backend antes de esta validación.
3. **Piloto físico:** leer datos de un inversor real de forma solo lectura; medir calidad y calibrar expectativas con datos reales.
4. **Producción:** hardening, autenticación de dispositivos, auditorías, contratos y permisos reales para mantenimiento.

## Instrucción corta para IA

> Leé `AGENTS.md` y `prompts/PROMPT_MAESTRO_IA_FRONTEND_ANGULAR_MOCK.md`. Inspeccioná repositorio existente; creá Angular + Tailwind con mocks persistentes y cobertura de las 47 entidades del JDL v4, sin levantar backend. Ejecutá F0–F7, corregí visualmente y entregá evidencias verificables.

## Fuente de referencias técnicas

- JHipster JDL: https://www.jhipster.tech/jdl/intro/
- Relaciones JDL: https://www.jhipster.tech/jdl/relationships/
- Angular + Tailwind: https://angular.dev/guide/tailwind
- Sentinel-2, resolución: https://www.esa.int/Applications/Observing_the_Earth/Copernicus/Sentinel-2/Facts_and_figures
- GOES ABI: https://www.nesdis.noaa.gov/our-satellites/currently-flying/goes-east-west/abi-scan-mode-information-and-channel-summary

**Licencias/condiciones reales de datos satelitales, API meteorológica, operación de drones y planes comerciales se validan antes del lanzamiento.**
