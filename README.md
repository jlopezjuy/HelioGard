<p align="center">
  <img src="frontend/helioguard-web/public/logo/Logo01.png" alt="HelioGuard" width="140" />
</p>

<h1 align="center">HelioGuard</h1>

<p align="center">
  Plataforma de monitoreo fotovoltaico, detección de pérdidas e integridad de telemetría con arquitectura multi-tenant y simuladores interactivos en Angular.
</p>

> [!NOTE]
> **Alcance de la demo:** La aplicación web (`frontend/helioguard-web`) opera con datos simulados y persistencia local (`localStorage`). No controla inversores, gateways ni aeronaves reales, y las firmas de telemetría del fixture no constituyen verificación criptográfica real.

---

## Inicio rápido (Frontend Angular)

Requisitos: **Node.js 20+** y **npm 11+**.

```bash
cd frontend/helioguard-web
npm install
npm start
```

Abrí `http://localhost:4200/` en el navegador.

### Comandos principales (`frontend/helioguard-web`)

| Acción | Comando | Resultado esperado |
|---|---|---|
| Servidor de desarrollo | `npm start` | App disponible en `http://localhost:4200/` |
| Pruebas unitarias (Vitest) | `npx ng test --watch=false` | 4 archivos, 31 pruebas en verde |
| Build de producción | `npm run build` | Bundle en `dist/helioguard-web` |
| Pruebas E2E (Playwright) | `npm run e2e` | Requiere binario de Chromium (`npx playwright install`) |

---

## Qué hace HelioGuard

HelioGuard compara la potencia informada por una planta solar contra la generación esperada por irradiación y temperatura, detecta degradación física o anomalías en los datos y coordina la respuesta operativa sin atribuir intrusiones sin evidencia.

1. **Detecta pérdida energética (`kWh` / `Wh` / `ARS`):** calcula potencia esperada con derating térmico (`-0,4 %/°C`), identifica déficit por suciedad, nube, helada, granizo, humo o falla de inversor, y evita asignar 100 % a suciedad sin medición directa.
2. **Protege la integridad de la telemetría:** separa incidentes energéticos de casos de seguridad; retiene eventos duplicados (`gatewayId+eventId`), replays de secuencia (`deviceId+bootId+sequenceNumber`), streams congelados (`5` pares `(P, V)` idénticos) e inconsistencias eléctricas (`|P - V·I| > 8 %` o exceso sobre potencia esperada) sin contaminar el cálculo de pérdidas.
3. **Resiliencia Edge Local-First:** simula el camino `Inversor / ESP32 → Raspberry Pi → Outbox local → WAN` con cola offline y sincronización idempotente al reconectar.
4. **Operación segura y multi-tenant:** separa organizaciones (`Andes Solar Jujuy`, `Casa Andina`, `Sol del Norte Salta`) con grants acotados en tiempo y una máquina de estados para misiones de dron con checklist de viento, helada, techo y aprobación explícita.

---

## Estructura del repositorio

| Ruta | Contenido | Estado |
|---|---|---|
| `frontend/helioguard-web/` | Aplicación Angular 21 + Tailwind CSS v4 + Design System Stitch (`hg-*`) | **Activo y migrado** |
| `ideas/docs/` | Especificación SDD v4, auditoría de `Idea Original`, matriz de trazabilidad, escenarios y validación | **Documentación maestra** |
| `ideas/specs/` y `ideas/contracts/` | Modelo JDL (`domain.v4.draft.jdl`, 47 entidades), `REQUIREMENTS.md`, OpenAPI y esquemas JSON | **Especificación** |
| `ideas/maquetas/` | Maquetas visuales de Google Stitch y tokens de diseño | **Referencia UI** |
| `ideas/demos/` | Prototipos HTML exploratorios (`helioguard_lab.html`, `helioguard_orbit.html`, `helioguard_flow.html`) | **Referencia de simulaciones** |
| `backend/` | Spring Boot 4.1.1 / JHipster 9.4.0 reactivo (WebFlux + R2DBC + PostgreSQL + Liquibase), lógica SDD S0.5–S8 | **Implementado (S1–S8 verificados)** |
| `Idea Original/` | Pitch y prototipo inicial en Flask (`app.py`, `index.html`, `pichoriginal.md`) | **Solo lectura (histórico)** |
| `docker/` y `movil/` | Espacios reservados para despliegue y cliente móvil | **Vacíos / reserva** |

---

## Pantallas y simulaciones interactivas

Todas las rutas de `frontend/helioguard-web` usan los componentes reutilizables `hg-*` en `src/app/shared/ui/` y pueden probarse desde el navegador usando el **Guion de la demo** y el selector de **Usuario demo** en la barra superior:

| Ruta | Qué muestra y qué podés simular |
|---|---|
| `/inicio` | Resumen operativo del régimen actual, déficit del intervalo (`450 Wh`), esperado vs. real y acceso al siguiente paso. |
| `/parque` | Vista cenital simulada por bloques (`B1–B4`), selección de bloque, escenarios de pérdida, pin de dron simulado y disclaimer satelital. |
| `/energia` | Gráfico `Wh` vs. esperado, tabla de intervalos (`marcar no comparable`), **Simulación diurna en vivo (`07:00–19:00`)** fiel a `Idea Original` (Reproducir/Pausar, `Ensuciar panel`, **`Enviar limpieza`** con recuperación al 100 %, `Ataque replay`, `Inyección falsa`) y **Clima NOA** (6 zonas regionales). |
| `/telemetria` | Camino del dato (`INV-03` → `Raspberry` → `Outbox` → `WAN`), publicación original, replay exacto, misma secuencia, nuevo boot, **`Inyección V×I inconsistente`**, **`Stream congelado 5×`**, fallo de lectura y ledger local. |
| `/integridad` | Buscador de eventos de integridad, dispositivos, valoraciones (`criptográficamente verificada: no`) y casos de investigación sin falsa atribución. |
| `/incidentes` | Bandeja separada de incidentes de **Energía** (con botón `Reconocer`) y **Seguridad** + reglas asociadas. |
| `/misiones` | Máquina de estados (`DRAFT → COMPLETED`), checklist de seguridad (viento, helada, techo, fabricante, operador, sitio), aprobación demo y despacho. |
| `/mantenimiento` | Órdenes de trabajo, esquemas antes/después, verificación de recuperación y **Simulador de intervención y retorno (payback)** (`Dron`, `Técnico`, `Propietario`) con compuertas de seguridad. |
| `/instalaciones` y `/instalaciones/:id` | Listado filtrado por tenant/grants, toggle de strings, detalle de equipos en `hg-drawer` y bloqueo cross-tenant (p. ej. Salta). |
| `/economia` | Valorización en **ARS** (compra evitada e inyección separadas), edición de tarifa demo y bloqueo por rol técnico. |
| `/auditorias` y `/fuentes` | Formulario por secciones con persistencia local (`Guardar`, `Cerrar`, `Reabrir`) y procedencia meteorológica/algorítmica. |
| `/planes`, `/organizaciones`, `/configuracion`, `/sistema` | Cobertura comercial sin seguro, prueba de aislamiento multi-tenant, exportar/importar/restablecer JSON y catálogo de componentes `hg-*`. |

---

## Flujo recomendado de prueba rápida (2 minutos)

1. Entrá a **`/energia`** → pestaña **`Simulación diurna (07:00–19:00)`** → pulsá **`Ensuciar panel`** y avanzá **`+30 min`** dos veces hasta ver **`Rendimiento bajo`**.
2. Pulsá **`Enviar limpieza`** dentro de la alerta y observá cómo la curva real vuelve a subir hasta recuperar **`Suciedad 100 %`** y **`Paneles limpios · producción recuperada`**.
3. Abrí el **`Guion de la demo`** arriba y recorré: `Sector sucio` → `Crear misión` → `Revisar seguridad` → `Aprobar` → `Despachar` → `Animar dron` (×8) → `Verificar`.
4. En **`/telemetria`**, probá **`Inyección V×I inconsistente`** y **`Stream congelado 5×`** para ver el aislamiento automático en el ledger (`retenida`) sin contaminar el cálculo de energía.

---

## Backend (`backend/`)

Backend reactivo sobre **Spring Boot 4.1.1**, **JHipster 9.4.0**, **Spring WebFlux**, **Spring Data R2DBC**, **PostgreSQL 18** y **Liquibase**, con lógica de negocio SDD implementada de punta a punta (**Sprints S0.5–S8**) y verificada contra PostgreSQL en Testcontainers (`T01`–`T20`):

- **Aislamiento multi-tenant por JWT (`active_org`):** nunca confía en `X-Organization-Id` ni permite `organizationId` editable en DTOs de creación; el servidor resuelve `organization` desde el JWT validado y `OrganizationMembership` `ACTIVE` (o exige selección explícita cuando el usuario pertenece a múltiples empresas).
- **Colaboración delegada sin IDOR:** los colaboradores externos operan solo sobre instalaciones con `InstallationAccessGrant` vigente y dentro del scope otorgado; los accesos cruzados responden `404` uniforme.
- **Autenticación M2M para Edge / ESP32:** los gateways no heredan JWT humano; autentican con `X-Device-Uid` + `X-Device-Secret` contra `DeviceIdentity.keyFingerprint` (SHA-256 en tiempo constante) y resuelven su tenant/instalación desde DB.

### Capacidades y Endpoints de Dominio (`S1`–`S8`)

| Sprint | Dominio | Endpoints / Componentes clave | Reglas de negocio destacadas |
|---|---|---|---|
| **S1** | Multi-tenancy y acceso | `POST /api/authenticate`, `POST /api/auth/active-organization`, `GET /api/me/context`, `GET/POST /api/installations`, `GET /api/installations/{id}` | Selección obligatoria de empresa en multi-org (`409` sin `active_org`), grants temporales (`InstallationAccessGrant`), aislamiento cross-tenant. |
| **S2** | Topología eléctrica y binding Edge | Servicios de `Site`, `Installation`, `SolarBlock`, `Inverter`, `SolarString`, `Gateway`, `DeviceIdentity`, `GatewayInstallationBinding` | Exclusión temporal (`btree_gist`) para bindings activos, jerarquía consistente dentro del mismo tenant, restricciones `CHECK` de capacidad/potencia. |
| **S3** | Telemetría Edge e idempotencia | `POST /api/ingest/telemetry` (`X-Device-Uid` + `X-Device-Secret`) | Idempotencia por `(gatewayId, eventId)`, detección de replay (`deviceId+bootId+sequenceNumber`), stream congelado y retención en `TelemetryIntegrityEvent`. |
| **S4** | Modelado energético y clima | `POST /api/energy-runs/execute`, `DataProvenanceServiceImpl` | Cálculo esperado AC con banda de incertidumbre (±8 %) usando `EnergyModelConfiguration` y `WeatherIngestionBatch`; rechaza (`400`) falsos reclamos de termografía de módulo sobre Sentinel/GOES. |
| **S5** | Alertas, diagnóstico e integridad | `POST /api/incidents/evaluate`, `POST /api/integrity/evaluate` | Diferencia noche (`NO_PRODUCTION_NIGHT`), offline (`TELEMETRY_OFFLINE`) y clima (`WEATHER_LIMITED`) sin falsas fallas; deduplica incidentes activos; anomalías físicas generan `SUSPECT` con `cyberAttackConfirmed = false`. |
| **S6** | Pérdidas económicas y tarifas | `POST /api/economic-assessments/evaluate` | Selección de `TariffRate` vigente; cálculo exacto con curva de autoconsumo o 3 escenarios con supuestos explícitos (`ALL_EXPORTED`, `MIXED_50_50`, `ALL_SELF_CONSUMED`). |
| **S7** | Auditorías y elegibilidad de planes | `POST /api/audit-reports/{id}/approve`, `POST /api/plan-eligibility-assessments/evaluate` | `AuditTemplate` publicado inmutable (`409`); aprobación exige preguntas obligatorias, evidencia en hallazgos `CRITICAL` y firma; elegibilidad bloquea auto-contratación ante críticos abiertos. |
| **S8** | Mantenimiento, recuperación y drones | `POST /api/work-orders/{id}/accept`, `POST /api/drone-missions/{id}/dispatch`, `POST /api/recovery-assessments/verify` | Aceptación por grant (`WORK_ORDER_EXECUTION`); **drones físicos deshabilitados en MVP** (`simulatedOnly = true`, `physicalHardwareInvoked = false`) con guardas de autorización, safety check y viento; recuperación `INCONCLUSIVE` sin muestras suficientes. |

### Ejecución y pruebas (`backend/`)

```bash
# Levantar backend en desarrollo
cd backend
./mvnw

# Ejecutar suite SDD completa (T01–T20) + ArchUnit + Auth contra PostgreSQL Testcontainers
./mvnw test -Dtest="MaintenanceDroneSddIT,AuditAndPlanSddIT,EconomicLossSddIT,IncidentDiagnosticSddIT,EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado verificado:** código de salida `0` contra PostgreSQL 18.6 en Testcontainers (`T01`–`T20`, reglas ArchUnit y autenticación).
- **Nota sobre `*ResourceIT` autogenerados:** la suite completa de JHipster (`./mvnw verify`) incluye los `*ResourceIT` autogenerados de las 67 entidades, que fallan porque usan `@WithMockUser` sin JWT `active_org` y un `slug` estático en `OrganizationResourceIT.createEntity()`.

---

## Documentación de referencia

- **Implementación Backend SDD (S0.5–S8):** `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`
- **Auditoría Idea Original vs. actual:** `ideas/docs/AUDITORIA_IDEA_ORIGINAL.md`
- **Matriz de trazabilidad:** `ideas/docs/MATRIZ_TRAZABILIDAD.md`
- **Escenarios y fórmulas de simulación:** `ideas/docs/ESCENARIOS_SIMULACION.md`
- **Plan de simulaciones y validación:** `ideas/docs/PLAN_SIMULACIONES_SPRINTS.md` y `ideas/docs/VALIDACION_SIMULACIONES.md`
- **Migración UI Stitch:** `frontend/helioguard-web/docs/PLAN_MIGRACION_PANTALLAS.md`
- **Arquitectura SDD, seguridad y límites científicos:** `ideas/docs/00_ARQUITECTURA_MAESTRA_SDD.md`, `ideas/docs/06_SEGURIDAD_TELEMETRIA_ATAQUES.md`, `ideas/docs/07_SATELITES_DRONES_LIMITACIONES.md`
