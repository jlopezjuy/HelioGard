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

## 5. Pendientes (Sprints S4–S8)
- **S4:** Modelado energético y clima (`WeatherObservation`, `BaselineModel`, `ExpectedEnergyCalculation`, sin atribuir suciedad sin evidencia ni llamar «mapa térmico satelital» a Sentinel/GOES).
- **S5:** Incidentes, integridad y seguridad (`IntegrityAssessment`, `TelemetryIntegrityEvent`, `SecurityIncident`, sin confundir datos inconsistentes con ciberataque confirmado).
- **S6:** Pérdidas económicas (`EconomicLossAssessment`, `TariffRule`, `EnergyLossSummary`).
- **S7:** Auditorías y planes de mantenimiento (`MaintenancePlan`, `MaintenancePlanVersion`, `PlanBenefit`, `PlanEligibilityAssessment`).
- **S8:** Mantenimiento y misiones (`WorkOrder`, `DroneMission` simulada/controlada con drones físicos deshabilitados).
- **Nota:** La suite completa de CRUD autogenerados por JHipster (`*ResourceIT` de las 67 entidades) no se ejecutó en este corte; solo se ejecutaron `TenantSddIT`, `TechnicalStructureTest`, `AuthenticateControllerIT` y `SecurityUtilsUnitTest`.
