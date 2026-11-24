# Migración de pantallas HelioGuard a Design System Stitch (`hg-*`)

Feature: `migracion-pantallas-stitch` | App: `frontend/helioguard-web` | Plan: `frontend/helioguard-web/docs/PLAN_MIGRACION_PANTALLAS.md`
Base commit: `1ef4833`

## Specs

- **S1 (`/parque`)**: Refactor `/parque` referencing `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital` using `hg-*` components (`hg-map-frame`, `hg-sensor`, `hg-button`, etc.), preserving block selection, scenarios, drone animation, and "no es una aeronave real" legend. Dark `.hg` canvas, responsive 1440/768/390.
- **S2 (`/telemetria`)**: Refactor `/telemetria` referencing `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital` using `hg-*` (`hg-sensor`, `hg-chart-frame`, `hg-alert`, `hg-error`, `hg-button`, `hg-flow`). Preserve inverter -> ESP32 -> Raspberry -> outbox -> WAN, publish original, replay, same sequence, new boot, read failure, retry, ingestion test.
- **S3 (`/energia`)**: Refactor `/energia` referencing `ideas/maquetas/helioguard_dashboard_principal` using `hg-*` (`hg-tabs`, `hg-chart-frame`, `hg-progress`, `hg-badge`). Preserve Wh unit, local time, loss scenarios, and rule not to close cause alone.
- **S4 (`/integridad`)**: Refactor `/integridad` referencing `ideas/maquetas/helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-table`, `hg-search`, `hg-badge`, `hg-alert`, `hg-empty`). Preserve events, devices, assessments, "criptográficamente verificada: no", never "ataque confirmado".
- **S5 (`/incidentes`)**: Refactor `/incidentes` referencing `helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-alert`, `hg-badge`, `hg-table`, `hg-button`, `hg-empty`). Preserve acknowledge, create work order, link to park, visible row actions without hover.
- **S6 (`/misiones`)**: Refactor `/misiones` referencing `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital` using `hg-*` (`hg-map-frame`, `hg-field`, `hg-sensor`, `hg-button`). Preserve propose, checklist, demo approve, dispatch, traverse, verify, wind/frost/ceiling/manufacturer/operator/site guards.
- **S7 (`/mantenimiento`)**: Refactor `/mantenimiento` referencing `helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-table`, `hg-badge`, `hg-field`, `hg-button`). Preserve accept order, notes, steps, cancel, separate buttons for note vs state change.
- **S8 (`/instalaciones`, `/instalaciones/:id`)**: Refactor `/instalaciones` and `/instalaciones/:id` referencing `helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-card`, `hg-drawer`, `hg-sensor`, `hg-badge`, `hg-button`). Preserve scope list, strings, cross-tenant denial, timeline.
- **S9 (`/auditorias`, `/fuentes`)**: Refactor `/auditorias` and `/fuentes` referencing `helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-field`, `hg-button`, `hg-table`, `hg-badge`, `hg-alert`). Preserve yes/no/note persistence, submit/close/reopen/offline; provenance and "not thermography" text.
- **S10 (`/economia`)**: Refactor `/economia` referencing Earning widget in `ideas/maquetas/helioguard_dashboard_principal` with currency `ARS` (never AUD), using `hg-*` (`hg-kpi`, `hg-field`, `hg-button`, `hg-card`). Preserve tariff, demo loss, role access guard.
- **S11 (`/planes`, `/organizaciones`, `/configuracion`)**: Refactor `/planes`, `/organizaciones`, `/configuracion` referencing `helioguard_storybook_showcase_49_componentes` and `/sistema` using `hg-*` (`hg-card`, `hg-badge`, `hg-button`, `hg-table`, `hg-alert`, `hg-field`). Preserve one-time proposal accept, persona switch & Salta isolation, export/import/reset.
- **S12 (Verification & Plan Update)**: Verify `ng test`, `ng build`, `npm run e2e`, capture & visually compare desktop (1440) and mobile (390) + tablet (768) screenshots, fix any visual/responsive differences, and update `frontend/helioguard-web/docs/PLAN_MIGRACION_PANTALLAS.md`.

## Tasks

- [x] **T1** (`S1`): Migrate `/parque` (`features/park/`) — route: inline — verified
- [x] **T2** (`S2`): Migrate `/telemetria` (`features/telemetry/`) — route: inline — verified
- [x] **T3** (`S3`): Migrate `/energia` (`features/energy/`) — route: inline — verified
- [x] **T4** (`S4`): Migrate `/integridad` (`features/integrity/`) — route: inline — verified
- [x] **T5** (`S5`): Migrate `/incidentes` (`features/incidents/`) — route: inline — verified
- [x] **T6** (`S6`): Migrate `/misiones` (`features/missions/`) — route: inline — verified
- [x] **T7** (`S7`): Migrate `/mantenimiento` (`features/maintenance/`) — route: inline — verified
- [x] **T8** (`S8`): Migrate `/instalaciones` and `/instalaciones/:id` (`features/installations/`) — route: inline — verified
- [x] **T9** (`S9`): Migrate `/auditorias` and `/fuentes` (`features/audits/`, `features/sources/`) — route: inline — verified
- [x] **T10** (`S10`): Migrate `/economia` (`features/economy/`) — route: inline — verified
- [x] **T11** (`S11`): Migrate `/planes`, `/organizaciones`, `/configuracion` (`features/plans/`, `features/organizations/`, `features/settings/`) — route: inline — verified
- [x] **T12** (`S12`): Capture & compare desktop/mobile screenshots, run `ng test` + `ng build` + `e2e`, update `PLAN_MIGRACION_PANTALLAS.md` — route: inline — verified

## Log

- **L1**: User requested designing and refactoring `/parque`, `/telemetria`, `/energia`, `/misiones`, `/economia`, and the remaining screens without their own maqueta (`/incidentes`, `/integridad`, `/mantenimiento`, `/instalaciones`, `/instalaciones/:id`, `/fuentes`, `/auditorias`, `/planes`, `/organizaciones`, `/configuracion`) using exclusively HelioGuard's Design System and reusable components, preserving all logic/mocks/navigation/interactions, comparing real desktop & mobile screenshots, fixing differences, verifying the build, and updating `docs/PLAN_MIGRACION_PANTALLAS.md`.
