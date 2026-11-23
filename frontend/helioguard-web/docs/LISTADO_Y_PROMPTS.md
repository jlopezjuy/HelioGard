# Listado del frontend y prompts de migración

App: `frontend/helioguard-web`. Maquetas: `ideas/maquetas/`. Los prompts son para pantallas. Los componentes `hg-*` ya existen y no se rehacen como ruta.

## Globales

| Pieza | Archivo | Rol |
|---|---|---|
| Arranque | `src/main.ts`, `src/app/app.ts`, `src/app/app.config.ts` | Zona sin Zone.js, router, `DemoStore` |
| Rutas y guard | `src/app/app.routes.ts` | `roleGuard`, redirect `''` → `inicio` |
| Tokens y CSS | `src/styles.css` | `q-*` (demo clara) y `hg-*` (Stitch, clase `.hg`) |
| Documento | `src/index.html` | `lang="es"`, título de la demo |
| Shell | `src/app/features/shell/` | Barra Stitch, píldora de rutas, tabs móviles y cajón. Envuelve todas las rutas menos `/sistema` |
| Estado | `src/app/infra/mock/demo-store.ts`, `snapshot-io.ts`, `seed.ts` | Mock. No es UI |
| Dominio | `src/app/core/domain/` | Reglas, acceso, acciones. No es UI |
| Carga | `src/app/shared/load.ts` | Estado de lectura de pantallas |

Clases sueltas que las pantallas todavía usan y el catálogo no reemplazó: `.sheet`, `.fact`, `.kicker`, `.lede`, `.mono`, `.hatch`.

## Componentes reutilizables

`src/app/shared/ui/`

`hg-brand`, `hg-nav-pill`, `hg-button`, `hg-icon-button`, `hg-badge`, `hg-tooltip`, `hg-field`, `hg-control`, `hg-card`, `hg-kpi`, `hg-table`, `hg-search`, `hg-pagination`, `hg-tabs`, `hg-modal`, `hg-drawer`, `hg-alert`, `hg-sensor`, `hg-empty`, `hg-error`, `hg-skeleton`, `hg-chart-frame`, `hg-equalizer`, `hg-gauge`, `hg-progress`, `hg-flow`, `hg-map-frame`.

Contraste: `tokens.ts` y `tokens.spec.ts`.

## Pantallas

| Ruta | Clase | Plantilla | Maqueta a usar | Estado |
|---|---|---|---|---|
| `/sistema` | `CatalogPage` | `features/catalog/` | `helioguard_ui_component_storybook`, `helioguard_storybook_showcase_49_componentes` | Hecho. Es el catálogo, no una pantalla operativa |
| shell | `Shell` | `features/shell/` | `helioguard_dashboard_principal` (navbar) y `helioguard_mobile_dashboard` (cabecera) | Hecho |
| `/inicio` | `HomePage` | `features/home/` | `helioguard_dashboard_principal`, `helioguard_mobile_dashboard` | Pendiente |
| `/parque` | `ParkPage` | `features/park/` | `helioguard_monitoreo_satelital_telemetr_a_cenital` | Pendiente |
| `/telemetria` | `TelemetryPage` | `features/telemetry/` | Misma maqueta cenital, panel de estado. Sin shader ni Sentinel | Pendiente |
| `/energia` | `EnergyPage` | `features/energy/` | `helioguard_dashboard_principal` (generación y flujo) | Pendiente |
| `/incidentes` | `IncidentsPage` | `features/incidents/` | No hay pantalla Stitch. Usar el catálogo `/sistema` | Pendiente |
| `/integridad` | `IntegrityPage` | `features/integrity/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/misiones` | `MissionsPage` | `features/missions/` | Marco de `helioguard_monitoreo_satelital_telemetr_a_cenital` | Pendiente |
| `/mantenimiento` | `MaintenancePage` | `features/maintenance/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/instalaciones` | `InstallationsPage` | `features/installations/installations.*` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/instalaciones/:id` | `InstallationDetailPage` | `features/installations/installation-detail.*` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/fuentes` | `SourcesPage` | `features/sources/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/auditorias` | `AuditsPage` | `features/audits/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/economia` | `EconomyPage` | `features/economy/` | Widget Earning del dashboard, en ARS, no en AUD | Pendiente |
| `/planes` | `PlansPage` | `features/plans/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/organizaciones` | `OrganizationsPage` | `features/organizations/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |
| `/configuracion` | `SettingsPage` | `features/settings/` | No hay pantalla Stitch. Usar el catálogo | Pendiente |

`''` redirige a `/inicio`. Un rol sin permiso vuelve a `/inicio?denegado=`.

## Prompts

Cada bloque se pega solo. No migrar varias rutas en el mismo paso.

### Shell

Diseña y refactoriza el layout Angular del **shell** (`src/app/features/shell/`, envuelve todas las rutas salvo `/sistema`) tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_dashboard_principal` y `ideas/maquetas/helioguard_mobile_dashboard`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/inicio`

Diseña y refactoriza la pantalla Angular **`/inicio`** tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_dashboard_principal` y `ideas/maquetas/helioguard_mobile_dashboard`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/parque`

Diseña y refactoriza la pantalla Angular **`/parque`** tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/telemetria`

Diseña y refactoriza la pantalla Angular **`/telemetria`** tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/energia`

Diseña y refactoriza la pantalla Angular **`/energia`** tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_dashboard_principal`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/incidentes`

Diseña y refactoriza la pantalla Angular **`/incidentes`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/integridad`

Diseña y refactoriza la pantalla Angular **`/integridad`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/misiones`

Diseña y refactoriza la pantalla Angular **`/misiones`** tomando como referencia fiel su maqueta en `ideas/maquetas/helioguard_monitoreo_satelital_telemetr_a_cenital`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/mantenimiento`

Diseña y refactoriza la pantalla Angular **`/mantenimiento`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/instalaciones`

Diseña y refactoriza la pantalla Angular **`/instalaciones`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/instalaciones/:id`

Diseña y refactoriza la pantalla Angular **`/instalaciones/:id`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/fuentes`

Diseña y refactoriza la pantalla Angular **`/fuentes`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/auditorias`

Diseña y refactoriza la pantalla Angular **`/auditorias`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/economia`

Diseña y refactoriza la pantalla Angular **`/economia`** tomando como referencia el widget Earning de `ideas/maquetas/helioguard_dashboard_principal`. La moneda de la demo es ARS, no AUD.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/planes`

Diseña y refactoriza la pantalla Angular **`/planes`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/organizaciones`

Diseña y refactoriza la pantalla Angular **`/organizaciones`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.

### `/configuracion`

Diseña y refactoriza la pantalla Angular **`/configuracion`**. No hay maqueta de esa ruta: tomá como referencia `ideas/maquetas/helioguard_storybook_showcase_49_componentes` y el catálogo `/sistema`.

Usá exclusivamente el Design System y los componentes reutilizables de HelioGuard. Conservá toda la lógica, mocks, navegación e interacciones existentes.

Lográ fidelidad visual con Stitch, excelente UX/UI y responsive. Compará capturas reales en escritorio y móvil, corregí diferencias y verificá el build antes de terminar.

Actualizá el avance en `docs/PLAN_MIGRACION_PANTALLAS.md`.
