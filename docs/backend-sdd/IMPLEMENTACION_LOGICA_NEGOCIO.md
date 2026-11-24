# Implementación de Lógica de Negocio Backend (SDD) — HelioGuard v5

> Repositorio backend real: `backend/` (branch `develop`, stack reactivo Spring Boot 4.1.1 / JHipster 9.4.0 / WebFlux / Spring Data R2DBC / PostgreSQL / Liquibase).

## 1. Validación S0.5 (Línea base JDL v5 + Liquibase)

- **Estado:** Validado.
- **Evidencia observada:**
  - `backend/domain.v5.review.jdl`, 67 descriptores en `backend/.jhipster/`, y changelogs Liquibase en `backend/src/main/resources/config/liquibase/master.xml` presentes y consistentes.
  - `./mvnw test-compile -q` compiló con código de salida `0`.
  - `AuthenticateControllerIT` ejecutó contra PostgreSQL 18.6 (Testcontainers) aplicando todos los changelogs Liquibase con código de salida `0`.
- **Nota técnica:** El proyecto es 100% reactivo (`WebFlux` + `R2DBC`), por lo que la resolución de tenant y autorización se implementó de forma reactiva (`Mono`/`Flux`, `ReactiveSecurityContextHolder`), respetando las reglas de capas de `TechnicalStructureTest` (ArchUnit).

---

## 2. Sprint S1 — Contexto de Tenant desde JWT, Autorización y Core de Acceso

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-TEN-001` | Resolución de tenant desde JWT + `OrganizationMembership` ACTIVE + `Organization` ACTIVE (sin `findFirst()`, sin `X-Organization-Id`) | `SecurityUtils.getCurrentActiveOrgId()`<br>`TenantContextService.requireCurrentTenant()`<br>`TenantContextService.resolveSingleActiveOrganizationIdForLogin()`<br>`TenantContextService.validateActiveMembershipForSwitch()`<br>`AuthenticateController.selectActiveOrganization()` | 0 membresías activas → `403 NO_ACTIVE_ORGANIZATION`. 1 membresía activa → autoselección (validando que `active_org`, si existe, coincida). 2+ membresías activas → exige claim firmado `active_org` (`409 TENANT_SELECTION_REQUIRED` si falta; `403 INVALID_ACTIVE_ORGANIZATION` si fue revocado/suspendido/inválido). Revalidación en DB en cada request. |
| `RB-ID-001` | Creación de `Installation` con organización asignada por servidor y validaciones de dominio | `InstallationServiceImpl.save()` | Exige rol `OWNER`/`ADMIN` (`PermissionService.requireManageRole()`). Asigna `organizationId` y `@Transient organization` desde `TenantContextService` ignorando `organization` del cliente. Exige `nominalDcKw > 0`, `gridMode != null`, `Site` perteneciente al tenant activo (`SITE_NOT_IN_TENANT`), código único por tenant (`409 INSTALLATION_CODE_EXISTS`) y estado inicial `COMMISSIONING`. |
| `RB-ID-002` | Listado y detalle filtrados por tenant propio + grants delegados válidos, con denegación uniforme anti-IDOR | `PermissionService.requireAccessToInstallation()`<br>`InstallationRepository.findAllAccessibleByOrganization()`<br>`InstallationRepository.countAccessibleByOrganization()`<br>`InstallationServiceImpl.findAll()` / `findOne()`<br>`InstallationResource.updateInstallation()` / `partialUpdateInstallation()` | Listado paginado y conteo incluyen solo instalaciones propias o con `InstallationAccessGrant` vigente (`revoked_at IS NULL`, `valid_from <= now`, `valid_until IS NULL OR valid_until > now`). Detalle/escritura devuelve `404 INSTALLATION_NOT_FOUND` uniforme tanto si no existe como si pertenece a otro tenant sin grant válido. |
| `RB-ID-003` | Prohibición de reasignación genérica de organización | `InstallationServiceImpl.update()` / `partialUpdate()`<br>`SiteServiceImpl.update()` / `partialUpdate()` | Rechaza con `403 ORGANIZATION_REASSIGNMENT_FORBIDDEN` intentos de cambiar `organization.id` y preserva siempre `existing.getOrganizationId()`. |
| S1 Soporte | Aislamiento tenant en `Organization`, `OrganizationMembership`, `Site`, `InstallationAccessGrant` | `OrganizationServiceImpl`<br>`OrganizationMembershipServiceImpl`<br>`SiteServiceImpl`<br>`InstallationAccessGrantServiceImpl` | `Site`, `OrganizationMembership` e `InstallationAccessGrant` asignan `organization` en servidor y filtran lecturas/escrituras al tenant activo. `InstallationAccessGrantServiceImpl` valida intervalo `validUntil > validFrom`, instalación propia, grantee activo y distinto del otorgante. `OrganizationServiceImpl` restringe lectura a organizaciones donde el usuario tiene membresía `ACTIVE` y crea membresía `OWNER`/`ACTIVE` al crear organización. |

### Endpoints S1

| Método y Ruta | Controlador / Método | Descripción |
|---|---|---|
| `POST /api/authenticate` | `AuthenticateController.authorize()` | Autentica credenciales; si el usuario tiene exactamente 1 organización activa, incluye el claim firmado `active_org` en el JWT. |
| `POST /api/auth/active-organization` | `AuthenticateController.selectActiveOrganization()` | Valida membresía `ACTIVE` en organización `ACTIVE` y emite nuevo JWT firmado con `active_org`. |
| `GET /api/me/context` | `MeContextResource.getMeContext()` | Devuelve usuario autenticado, `activeOrganizationId`, `activeRole`, `selectionRequired` y lista de membresías activas. |
| `GET /api/installations` | `InstallationResource.getAllInstallations()` | Lista paginada (`X-Total-Count`) filtrada por tenant propio + grants activos vigentes. |
| `POST /api/installations` | `InstallationResource.createInstallation()` | Crea instalación asignando `organization` internamente (`COMMISSIONING`, `nominalDcKw > 0`, `site` del tenant). |
| `GET /api/installations/{id}` | `InstallationResource.getInstallation()` | Obtiene instalación si pertenece al tenant o tiene grant vigente; si no, `404` uniforme. |

### Migraciones Liquibase

- `backend/src/main/resources/config/liquibase/changelog/20261124120000_s1_tenant_integrity_constraints.xml` (registrada en `master.xml`, expand-only, sin `DROP`):
  - `uq_site_pk_tenant` (`UNIQUE (id, organization_id)` en `site`)
  - `uq_installation_pk_tenant` (`UNIQUE (id, organization_id)` en `installation`)
  - `fk_installation_site_same_tenant` (`FOREIGN KEY (site_id, organization_id) REFERENCES site (id, organization_id)`)
  - `uq_installation_tenant_code` (`UNIQUE INDEX` en `installation (organization_id, code)`)

### Pruebas y Resultados Reales

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `TenantSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/TenantSddIT.java`):**
  - `T01` (`t01_singleActiveOrgListsOnlyOwnAndValidGrantedInstallations`): usuario con 1 org activa ve solo propias + grants vigentes (`X-Total-Count = 2`), excluyendo grants vencidos.
  - `T02` (`t02_multiOrgRequiresActiveOrgSelectionAndIgnoresClientOrgOverrides`): usuario multiempresa sin `active_org` recibe `409 Conflict` sin side effects en DB; tras `POST /api/auth/active-organization` crea instalación ignorando `X-Organization-Id` y `organization.id` del body.
  - `T03` (`t03_revokedMembershipDeniesPreviouslyIssuedJwt`): JWT previamente emitido es rechazado (`403 Forbidden`) tras revocar la membresía en DB.
  - `T04` + `T05` (`t04_t05_idorUniformDenialAndGrantScopedOnlyToGrantedInstallation`): acceso a instalación concedida `200 OK`; instalación no concedida y ID inexistente devuelven `404 Not Found` uniforme; al revocar el grant se bloquea de inmediato (`404`).
  - `T06` (`t06_domainInvariantsRejectInvalidKwCrossTenantSiteAndOrgReassign`): valida `nominalDcKw <= 0` (`400`), `site` cross-tenant (`400`), código duplicado en tenant (`409`) y reasignación de organización (`403`).

---

## 3. Sprint S2 — Topología Eléctrica y Binding Edge

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-ASSET-001` | Separación entre catálogo (`EquipmentModel`) e instancia física/lógica (`Equipment`, `SolarString`, `InverterMpptInput`) | `EquipmentModelServiceImpl`<br>`SolarStringServiceImpl`<br>`InverterMpptInputServiceImpl` | `EquipmentModel` queda como catálogo global (lectura con tenant activo, escritura con `MANAGE`). `SolarString` exige `EquipmentModel.kind == MODULE` (`INVALID_MODULE_MODEL_KIND`). `InverterMpptInput` exige `Equipment` del tenant cuyo modelo sea `INVERTER` (`EQUIPMENT_NOT_IN_TENANT` / `EQUIPMENT_NOT_INVERTER`). |
| `RB-ASSET-002` | Jerarquía `sector → array → string → MPPT/inversor`, ventanas temporales y prohibición de cruces entre tenants/instalaciones | `SolarSectorServiceImpl`<br>`SolarArrayServiceImpl`<br>`StringInputAssignmentServiceImpl`<br>`EquipmentServiceImpl`<br>`EquipmentConnectionServiceImpl`<br>`GatewayServiceImpl`<br>`TelemetryChannelBindingServiceImpl` | Valida que `SolarArray.solarSector` pertenezca a la misma instalación y tenant (`SECTOR_INSTALLATION_MISMATCH`), que `StringInputAssignment` conecte `SolarString` e `InverterMpptInput` de la misma instalación (`CROSS_INSTALLATION_WIRING_FORBIDDEN`) sin solapamiento temporal (`409 OVERLAPPING_STRING_ASSIGNMENT` + `EXCLUDE USING gist` en DB), y que `EquipmentConnection` no sea autoreferencial (`SELF_CONNECTION_FORBIDDEN`) ni cruce instalaciones (`CROSS_INSTALLATION_CONNECTION_FORBIDDEN`). |
| `RB-ID-003` | Prohibición de reasignación genérica de organización | `PermissionService.assertNoOrgReassignment()` y servicios S2 | Rechaza con `403 ORGANIZATION_REASSIGNMENT_FORBIDDEN` y preserva `organizationId` en servidor. |
| `RB-LOC-001` | Precisión de localización según evidencia disponible | `TopologyLocalizationService.resolvePrecision()` / `resolvePrecisionForString()` | Resuelve `MODULE`, `STRING`, `INVERTER`, `SECTOR`, `INSTALLATION` o `UNKNOWN` estrictamente según telemetría y topología activa disponible, sin fabricar precisión de módulo/string. |

### Migración Liquibase S2

- `backend/src/main/resources/config/liquibase/changelog/20261124130000_s2_topology_integrity_constraints.xml` (registrada en `master.xml`, expand-only):
  - `CREATE EXTENSION IF NOT EXISTS btree_gist;`
  - `uq_solar_sector_pk_tenant`, `fk_sector_installation_same_tenant`, `ck_sector_interval`, `uq_sector_install_code_period`
  - `uq_equipment_pk_tenant`, `fk_connection_from_same_tenant`, `fk_connection_to_same_tenant`, `ck_connection_interval`
  - `ck_mppt_input_interval`, `ck_assignment_interval`, `ex_string_single_active_input` (`EXCLUDE USING gist` con `tsrange` inmutable sobre `solar_string_id` y `[valid_from, valid_until)`), `ck_channel_binding_interval`.

### Pruebas y Resultados Reales (S1 + S2)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `TopologySddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/TopologySddIT.java`):**
  - Asignación server-side de `organization` ignorando spoofing en DTO.
  - Rechazo de `SolarArray` con sector de otra instalación (`400`).
  - Validación de `EquipmentModel.kind` (`MODULE` en `SolarString`, `INVERTER` en `InverterMpptInput`).
  - Rechazo de cableado `StringInputAssignment` entre instalaciones distintas (`400`), rechazo de intervalos solapados (`409` + `ex_string_single_active_input`), y aceptación de ventana secuencial contigua (`201`).
  - Rechazo de `EquipmentConnection` autoreferencial y cross-installation (`400`).
  - Denegación uniforme anti-IDOR (`404`) entre tenants sobre `SolarSector` y `Equipment`.
  - Verificación de niveles `RB-LOC-001` en `TopologyLocalizationService`.

---

## 4. Sprint S3 — Telemetría, Edge M2M Auth e Idempotencia

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-TEL-001`<br>`RB-SEC-003` | Autenticación M2M sin JWT humano y resolución de tenant/instalación desde dispositivo registrado | `DeviceAuthService.authenticateDevice()`<br>`SecurityConfiguration.springSecurityFilterChain()` | `POST /api/ingest/telemetry` autentica `X-Device-Uid` + `X-Device-Secret` comparando en tiempo constante el SHA-256 contra `DeviceIdentity.keyFingerprint` (`ACTIVE`, no vencida) y `Gateway` (no `REVOKED`), resolviendo `organizationId` e `installationId` desde DB. Fallos generan `TelemetryIntegrityEvent` (`AUTH_FAILURE`) sin loguear ni persistir secretos. |
| `RB-TEL-002`<br>`RB-TEL-003`<br>`RB-SEC-001` | Normalización, deduplicación idempotente por `(gateway_id, event_id)` y cuarentena ante hash distinto | `EdgeIngestionService.ingest()`<br>`EdgeIngestionService.processSingleEvent()`<br>`EdgeTelemetryResource.ingestTelemetry()` | Rechaza `403 FOREIGN_INSTALLATION_FORBIDDEN` si el payload declara otra instalación y persiste `TelemetryIntegrityEvent` (`AUTH_FAILURE`) fuera del rollback transaccional. Mismo `(gateway_id, event_id)` con igual `payloadSha256` → `DUPLICATE_IDEMPOTENT` (sin falsa acusación). Mismo `eventId` con `payloadSha256` distinto → `QUARANTINED` + `TelemetryIntegrityEvent` (`REPLAY`, `QUARANTINED`) preservando el evento original intacto. |
| `RB-TEL-004`<br>`RB-TEL-005` | Secuencia `bootId + sequenceNumber`, reboots, gaps, clock skew y sincronización batch offline | `EdgeIngestionService.evaluateSequenceAndSave()`<br>`EdgeIngestionService.evaluateClockSkew()` | Mantiene `DeviceSequenceCheckpoint` por `(gateway_id, device_identity_id)`; detecta `REBOOT_DETECTED`, `SEQUENCE_GAP`, `OUT_OF_ORDER` y `CLOCK_SKEW` (> 5 min), y soporta lotes de sincronización offline idempotente. |
| `RB-SEC-004` | Aislamiento tenant y máquina de estados en seguridad/integridad | `DeviceIdentityServiceImpl`<br>`TelemetryChannelServiceImpl`<br>`TelemetryIntegrityEventServiceImpl`<br>`IntegrityAssessmentServiceImpl`<br>`SecurityIncidentServiceImpl` | Aislamiento por tenant, asignación server-side de `organization` y transiciones explícitas en `SecurityIncident` (`OPEN → TRIAGE → INVESTIGATING → MITIGATED → CLOSED`). |

### Migración Liquibase S3

- `backend/src/main/resources/config/liquibase/changelog/20261124140000_s3_telemetry_ingestion_and_idempotency.xml` (registrada en `master.xml`, expand-only):
  - `telemetry_ingest_event` con `uq_telemetry_ingest_gateway_event` (`UNIQUE (gateway_id, event_id)`).
  - `device_sequence_checkpoint` con `uq_device_seq_checkpoint` (`UNIQUE (gateway_id, device_identity_id)`).

### Pruebas y Resultados Reales (S1 + S2 + S3)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `TelemetryEdgeSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/TelemetryEdgeSddIT.java`):**
  - `T06` (`t06_foreignInstallationRejectedAndSecurityEventRecorded`): gateway A declara `installationId` de B → `403 Forbidden` y persiste `TelemetryIntegrityEvent` (`AUTH_FAILURE`).
  - `T07` (`t07_offline100EventsRetriedThreeTimesPersistExactly100UniqueEvents`): lote de 100 eventos offline reenviado 3 veces → 100 `ACCEPTED` en el primer envío, 100 `DUPLICATE_IDEMPOTENT` en los 2 reintentos, y exactamente 100 filas en `telemetry_ingest_event`.
  - `T08` + `T09` (`t08_t09_idempotentDuplicateVsHashMismatchQuarantineAndInvalidAuth`): secreto inválido → `401 Unauthorized`; reintento exacto → `DUPLICATE_IDEMPOTENT`; mismo `eventId` con valor alterado → `QUARANTINED` + `TelemetryIntegrityEvent` (`REPLAY`, `QUARANTINED`) con el valor original intacto.

---

## 5. Sprint S4 — Modelado Energético y Clima

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-ENE-001` | Cálculo físico versionado con trazabilidad y prohibición de falsos reclamos Sentinel/GOES | `EnergyModelingService.execute()`<br>`EnergyModelingResource.executeModel()`<br>`DataProvenanceServiceImpl.checkSatelliteThermographyClaim()` | `POST /api/energy-runs/execute` calcula energía esperada AC por intervalo con banda de incertidumbre (±8%), usando `EnergyModelConfiguration` y `WeatherIngestionBatch` del tenant. `DataProvenanceServiceImpl` rechaza con `400 INVALID_SATELLITE_THERMOGRAPHY_CLAIM` reclamos de termografía de módulo sobre productos Sentinel/GOES. |
| `RB-ENE-002` | `INSUFFICIENT_DATA` sin reemplazar por cero | `EnergyModelingService.runCalculation()`<br>`EnergyModelRunServiceImpl.validateRunInvariants()` | Si falta irradiancia útil (<50% cobertura), configuración o batch meteorológico `VALIDATED`, persiste `EnergyModelRun(status=INSUFFICIENT_DATA, expectedWh=null)`. Nunca reemplaza `expectedWh` por `0`. |
| `RB-ENE-003` | Distinción potencia vs energía, delta de medidor y detección de reset | `EnergyModelingService.resolveActualWh()` | Soporta `actualEnergyWh`, delta de contadores (`meterCounterEndWh - meterCounterStartWh`) con detección de reset (`METER_RESET_DETECTED` si `end < start`), o integración `actualPowerW * hours`; calcula `deficitWh = max(0, expectedWh - actualWh)` solo sobre intervalos `COMPARABLE`. |
| `RB-ENE-004` | Nubosidad/frío y gating de hipótesis `SOILING` por evidencia | `EnergyModelingService.runCalculation()` | Intervalos con `cloudyOrCold = true` se marcan `WEATHER_LIMITED` (no pérdida evitable). `soilingHypothesisAllowed` solo se habilita si `hasSoilingTrendOrInspectionEvidence == true` y `deficitWh > 0`. |

### Migración Liquibase S4

- `backend/src/main/resources/config/liquibase/changelog/20261124150000_s4_energy_modeling_and_intervals.xml` (registrada en `master.xml`, expand-only):
  - `energy_interval_result` con `uq_energy_interval_run_start` (`UNIQUE (model_run_id, interval_start)`), FKs a `organization`, `installation`, `energy_model_run`, y `ck_energy_interval_window`.
  - Checks temporales en `weather_ingestion_batch`, `energy_model_configuration` y `energy_model_run`.

### Pruebas y Resultados Reales (S1–S4)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `EnergyModelingSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/EnergyModelingSddIT.java`):**
  - `T10`: irradiancia faltante → `EnergyModelRun.status == INSUFFICIENT_DATA` y `expectedWh == null`.
  - Cálculo físico con banda de incertidumbre, detección de `METER_RESET_DETECTED`, marcado `WEATHER_LIMITED`, gating de `soilingHypothesisAllowed`, rechazo `400` a termografía Sentinel/GOES e IDOR `404`.

---

## 6. Sprint S5 — Alertas, Diagnóstico e Integridad

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-TEL-006`<br>`RB-INC-002` | Diferenciación nocturna/offline/clima/falla y deduplicación de incidentes activos | `AlertDiagnosticService.evaluateAlert()`<br>`IncidentDiagnosticResource.evaluateAlert()` | `POST /api/incidents/evaluate`: si irradiancia ≤ 10 W/m² → `NO_PRODUCTION_NIGHT` (sin incidente). Si telemetría vencida → `TELEMETRY_OFFLINE` (nunca `PANEL_FAILURE`). Si `weatherLimited` → `WEATHER_LIMITED`. Deduplica incidentes activos por `(installation, alertRuleVersion, affectedScope)` actualizando `lastSeenAt` + `IncidentEvent`. |
| `RB-INC-001` | Inmutabilidad de `AlertRuleVersion` una vez usada | `AlertRuleVersionServiceImpl.assertMutable()` | Rechaza con `409 RULE_VERSION_IMMUTABLE` cualquier `PUT`/`PATCH`/`DELETE` sobre una versión ya referenciada por un `Incident`. |
| `RB-INC-003` | Ciclo de vida y aislamiento de `Incident` | `IncidentServiceImpl`<br>`IncidentEventServiceImpl`<br>`IncidentLocalizationServiceImpl` | Transiciones válidas `OPEN → ACKNOWLEDGED → IN_PROGRESS → RESOLVED → CLOSED` (`409 INVALID_INCIDENT_TRANSITION` si es inválida) con estampado de `acknowledgedAt`/`resolvedAt`. |
| `RB-SEC-002` | Integridad física sin imputar ciberataque sin evidencia | `AlertDiagnosticService.evaluatePhysicalIntegrity()`<br>`IncidentDiagnosticResource.evaluatePhysicalIntegrity()` | `POST /api/integrity/evaluate`: lecturas físicamente imposibles (negativas, >125% kW nominal o producción nocturna) generan `TelemetryIntegrityEvent(PHYSICAL_INCONSISTENCY, UNDER_REVIEW)` + `IntegrityAssessment(SUSPECT)` con `cyberAttackConfirmed = false`. |

### Migración Liquibase S5

- `backend/src/main/resources/config/liquibase/changelog/20261124160000_s5_incidents_and_integrity_indexes.xml` (registrada en `master.xml`, expand-only): índice `idx_incident_org_dedup_status` y check `ck_alert_rule_version_window`.

### Pruebas y Resultados Reales (S1–S5)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="IncidentDiagnosticSddIT,EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `IncidentDiagnosticSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/IncidentDiagnosticSddIT.java`):**
  - `T11`: irradiancia 0 en medianoche → `NO_PRODUCTION_NIGHT`, 0 incidentes creados.
  - `T12`: telemetría vencida → `TELEMETRY_OFFLINE` + deduplicación en repetición (1 incidente, 2 `IncidentEvent`).
  - `T13`: lectura físicamente imposible (50 kW en planta de 10 kW) → `DataTrustLevel.SUSPECT`, `PHYSICAL_INCONSISTENCY`, `cyberAttackConfirmed == false`.
  - Inmutabilidad de `AlertRuleVersion` (`409`), máquina de estados de `Incident` (`409` inválida / `200` válida) e IDOR `404`.

---

## 7. Sprint S6 — Pérdidas Económicas y Tarifas

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-ECO-001` | Sin doble conteo de causas y déficit residual `UNKNOWN` visible | `EconomicEvaluationService.persistAndCompute()`<br>`LossCauseEstimateServiceImpl.validateNoDoubleCounting()` | Rechaza con `409 LOSS_DOUBLE_COUNTING_FORBIDDEN` si la suma de causas atribuidas supera `totalLossWh`. Persiste explícitamente una entrada `LossCause.UNKNOWN` (`RESIDUAL_UNATTRIBUTED`) con el déficit no atribuido. |
| `RB-ECO-002` | Tarifa por vigencia y escenarios con supuestos sin curva de autoconsumo | `EconomicEvaluationService.evaluate()`<br>`EconomicEvaluationService.saveEconomic()`<br>`EconomicEvaluationResource.evaluate()` | `POST /api/economic-assessments/evaluate` selecciona `TariffRate` vigente en `assessedAt`. Con curva (`selfConsumptionLostWh` + `exportedLostWh`) calcula monto exacto; sin curva devuelve `exactAmountAvailable = false` y 3 escenarios con supuestos (`ALL_EXPORTED`, `MIXED_50_50`, `ALL_SELF_CONSUMED`). |
| `RB-ECO-003` | No prometer recuperación financiera por limpieza sin evidencia y seguridad | `EconomicEvaluationService.saveEconomic()` | `cleaningRecoveryPromised = true` solo si existen costo estimado, duración esperada del beneficio (> 0 días), `safetyCheckPassed == true` y `hasComparablePostRecoveryEvidence == true`. |

### Migración Liquibase S6

- `backend/src/main/resources/config/liquibase/changelog/20261124170000_s6_economy_and_tariff_constraints.xml` (registrada en `master.xml`, expand-only): checks de vigencia de tarifa (`ck_tariff_rate_window`), precios no negativos (`ck_tariff_rate_prices`) y montos/energías perdidas no negativas (`ck_loss_assessment_non_negative`, `ck_loss_cause_non_negative`, `ck_economic_assessment_non_negative`).

### Pruebas y Resultados Reales (S1–S6)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="EconomicLossSddIT,IncidentDiagnosticSddIT,EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `EconomicLossSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/EconomicLossSddIT.java`):**
  - `T14`: déficit 4000 Wh con atribuciones 3000 + 2000 Wh → `409 Conflict` (`LOSS_DOUBLE_COUNTING_FORBIDDEN`).
  - `T15`: sin curva de autoconsumo → `exactAmountAvailable == false`, `estimatedLossAmount == null`, 3 escenarios con supuestos y residual `UNKNOWN` visible (1500 Wh).
  - Con curva de autoconsumo y evidencia post-recuperación → monto exacto (`0.70 USD`) y `cleaningRecoveryPromised == true`; IDOR cross-tenant → `404`.

---

## 8. Sprint S7 — Auditorías y Planes de Mantenimiento

### Reglas de Negocio (`RB-*`) Implementadas

| RB-ID | Regla | Clase / Método | Comportamiento implementado |
|---|---|---|---|
| `RB-AUD-001` | Ciclo de `AuditReport` y gate de aprobación | `AuditAndPlanWorkflowService.approveAuditReport()`<br>`AuditAndPlanWorkflowResource.approveAuditReport()`<br>`AuditReportServiceImpl` | `AuditReport` inicia en `DRAFT`; `POST /api/audit-reports/{id}/approve` exige todas las preguntas obligatorias respondidas (`400 AUDIT_MANDATORY_QUESTIONS_MISSING` con códigos faltantes), evidencia en hallazgos `CRITICAL` abiertos (`AUDIT_CRITICAL_FINDING_EVIDENCE_REQUIRED`) y `AuditReportSignature` (`AUDIT_SIGNATURE_REQUIRED`). |
| `RB-AUD-002` | Inmutabilidad de templates publicados y aislamiento de evidencias/firmas | `AuditTemplateServiceImpl`<br>`AuditQuestionServiceImpl`<br>`AuditEvidenceServiceImpl`<br>`AuditReportSignatureServiceImpl` | Templates publicados (`publishedAt != null`) o usados por un reporte quedan inmutables (`409 AUDIT_TEMPLATE_IMMUTABLE`), incluyendo sus preguntas. |
| `RB-PLAN-001` | Evaluación de elegibilidad por hallazgos críticos | `AuditAndPlanWorkflowService.evaluateEligibility()`<br>`AuditAndPlanWorkflowResource.evaluatePlanEligibility()` | `POST /api/plan-eligibility-assessments/evaluate` devuelve `INCOMPLETE` si el reporte no está aprobado, `REQUIRES_REMEDIATION` (`requiresCorrection = true`) ante hallazgos `CRITICAL` abiertos, `CONDITIONAL` ante `HIGH`, y `ELIGIBLE` sin auto-contratación (`autoContracted = false`). |
| `RB-PLAN-002` | Versiones/beneficios inmutables y precio congelado en suscripción | `MaintenancePlanVersionServiceImpl`<br>`PlanBenefitServiceImpl`<br>`PlanSubscriptionServiceImpl` | Versiones publicadas/suscritas y sus beneficios son inmutables (`409 PLAN_VERSION_IMMUTABLE`). Al suscribir, `agreedAmount` y `currencyCode` se congelan desde `MaintenancePlanVersion` ignorando overrides del cliente. |

### Migración Liquibase S7

- `backend/src/main/resources/config/liquibase/changelog/20261124180000_s7_audits_and_plans_constraints.xml` (registrada en `master.xml`, expand-only): índices únicos `uq_plan_version_name`, `uq_benefit_version_code` y checks `ck_service_agreement_window`, `ck_plan_subscription_window`, `ck_plan_version_amount`.

### Pruebas y Resultados Reales (S1–S7)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="AuditAndPlanSddIT,EconomicLossSddIT,IncidentDiagnosticSddIT,EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `AuditAndPlanSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/AuditAndPlanSddIT.java`):**
  - `T16`: aprobación con pregunta obligatoria vacía (`Q_GROUNDING`) → `400` listando el código faltante; tras responder + adjuntar evidencia a hallazgo `CRITICAL` + firmar → `200 APPROVED`.
  - `T17`: evaluación de elegibilidad con hallazgo `CRITICAL` abierto → `REQUIRES_REMEDIATION` (`requiresCorrection == true`, `autoContracted == false`).
  - Inmutabilidad de `AuditTemplate` publicado (`409`), precio/moneda congelados en `PlanSubscription` (`149.99 USD`) e IDOR `404`.

---

## 9. Sprint S8 — Mantenimiento, Órdenes de Trabajo y Misiones de Dron (Completado)

### Reglas de Negocio Implementadas (`RB-*`)

| RB-ID | Regla | Clase / Método |
|---|---|---|
| `RB-WO-001` | Creación/edición tenant-scoped de `WorkOrder`, `WorkOrderApproval`, `WorkOrderAssignment`, `WorkOrderTask`, `MaintenanceIntervention`, `InterventionEvidence`, `RecoveryAssessment`, `VerificationResult`, `DroneAsset`, `DroneMission`, `MissionAuthorization`, `DroneMissionTarget`, `DroneMissionEvent`, `MissionSafetyCheck`; asignación interna de `organization` y sincronización `@Transient` | 14 `*ServiceImpl` en `ar.com.heliguard.app.service.impl.*` |
| `RB-WO-002` | Validación de ownership del padre (`WorkOrder`, `MaintenanceIntervention`, `RecoveryAssessment`, `DroneMission`, `DroneAsset`, `Installation`) dentro del tenant activo antes de persistir o consultar | 14 `*ServiceImpl` y `MaintenanceAndDroneWorkflowService` |
| `RB-WO-003` | Aceptación controlada de `WorkOrder`: requiere `WorkOrderStatus` válido (`DRAFT`/`OPEN`/`APPROVED`/`ASSIGNED`), verifica permiso tenant o `InstallationAccessGrant` (`WORK_ORDER_EXECUTION`) del usuario autenticado, registra `WorkOrderApproval` y transiciona la orden a `IN_PROGRESS` | `MaintenanceAndDroneWorkflowService.acceptWorkOrder` |
| `RB-DRONE-001` | Drones físicos deshabilitados en MVP: la solicitud/despacho de misión de dron opera exclusivamente en modo simulado/controlado (`simulatedOnly = true`, `physicalHardwareInvoked = false`) y registra evento `SIMULATED_DISPATCH` | `MaintenanceAndDroneWorkflowService.dispatchSimulatedMission` |
| `RB-DRONE-002` | Guardas de seguridad previas al despacho: `DroneAsset` debe estar `AVAILABLE`/`ACTIVE` (`409` si está `MAINTENANCE`/`RETIRED`), requiere `MissionAuthorization` aprobada (`409` si falta), al menos un `MissionSafetyCheck` aprobado (`passed = true`) y viento dentro de `maxWindMs` (`409` si excede) | `MaintenanceAndDroneWorkflowService.dispatchSimulatedMission` |
| `RB-DRONE-003` | Máquinas de estado estrictas para `WorkOrderStatus` y `DroneMissionStatus` (bloqueo de transiciones inválidas o mutación tras estados terminales con `409`) | `WorkOrderServiceImpl`, `DroneMissionServiceImpl`, `MaintenanceAndDroneWorkflowService` |
| `RB-REC-001` | Evaluación de recuperación (`verifyRecovery`): cuando `baselineSamples < minRequiredSamples` o `postSamples < minRequiredSamples`, el resultado queda marcado como `INCONCLUSIVE` (`recoveryPct = null`, `verified = false`) sin fabricar porcentajes | `MaintenanceAndDroneWorkflowService.verifyRecovery` |

### Endpoints de Dominio S8

- `POST /api/work-orders/{id}/accept` — `MaintenanceAndDroneWorkflowResource.acceptWorkOrder`
- `POST /api/drone-missions/{id}/dispatch` — `MaintenanceAndDroneWorkflowResource.dispatchSimulatedMission`
- `POST /api/recovery-assessments/verify` — `MaintenanceAndDroneWorkflowResource.verifyRecovery`

### Migración Liquibase S8

- [`20261124190000_s8_workorders_and_drones_constraints.xml`](file:///Users/juanlopez/Proyectos/Solar/backend/src/main/resources/config/liquibase/changelog/20261124190000_s8_workorders_and_drones_constraints.xml) (registrada en `master.xml`):
  - `ck_work_order_dates` (`due_at >= opened_at` cuando ambos existen)
  - `ck_drone_asset_max_wind` (`max_wind_ms > 0` cuando existe)
  - `ck_intervention_cost` (`cost_amount >= 0` cuando existe)

### Pruebas y Resultados Reales (S1–S8)

Comando ejecutado en `/Users/juanlopez/Proyectos/Solar/backend`:

```bash
./mvnw test -Dtest="MaintenanceDroneSddIT,AuditAndPlanSddIT,EconomicLossSddIT,IncidentDiagnosticSddIT,EnergyModelingSddIT,TelemetryEdgeSddIT,TopologySddIT,TenantSddIT,TechnicalStructureTest,AuthenticateControllerIT,SecurityUtilsUnitTest" -q
```

- **Resultado:** Código de salida `0` (todos los tests pasaron contra PostgreSQL 18.6 en Testcontainers).
- **Cobertura en `MaintenanceDroneSddIT` (`backend/src/test/java/ar/com/heliguard/app/web/rest/MaintenanceDroneSddIT.java`):**
  - `T18`: aceptación de `WorkOrder` por colaborador externo con `InstallationAccessGrant` (`WORK_ORDER_EXECUTION`) → `200 IN_PROGRESS` y registro de `WorkOrderApproval`; intento sin grant / cross-tenant → `403`/`404`.
  - `T19`: despacho de `DroneMission` bloqueado (`409`) sin `MissionAuthorization` o sin `MissionSafetyCheck` aprobado; tras autorización + safety check → `200` con `simulatedOnly == true`, `physicalHardwareInvoked == false` y evento `SIMULATED_DISPATCH`.
  - `T20`: `verifyRecovery` con muestras insuficientes (`baselineSamples < minRequiredSamples`) → `200 INCONCLUSIVE` (`verified == false`, `recoveryPct == null`).

---

## 10. Alcance de Verificación y Notas
- **Estado de Sprints S0.5–S8:** Todos los sprints S0.5–S8 están implementados, documentados y verificados con la suite SDD (`T01`–`T20`), `TechnicalStructureTest`, `AuthenticateControllerIT` y `SecurityUtilsUnitTest`.
- **Nota:** La suite completa de CRUD autogenerados por JHipster (`*ResourceIT` de las 67 entidades) no se ejecutó en este corte; solo se ejecutaron las suites SDD (`TenantSddIT`, `TopologySddIT`, `TelemetryEdgeSddIT`, `EnergyModelingSddIT`, `IncidentDiagnosticSddIT`, `EconomicLossSddIT`, `AuditAndPlanSddIT`, `MaintenanceDroneSddIT`), `TechnicalStructureTest`, `AuthenticateControllerIT` y `SecurityUtilsUnitTest`.
