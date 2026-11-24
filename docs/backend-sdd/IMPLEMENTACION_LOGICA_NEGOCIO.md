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

## 3. Pendientes (Sprints S2–S8)

- **S2:** Topología eléctrica (`SolarSector`, `Equipment`, `EquipmentConnection`, `SolarString`, `StringInputAssignment`, anti-solapamiento `btree_gist`).
- **S3:** Telemetría/edge e idempotencia (`DeviceIdentity` con autenticación máquina-a-máquina sin JWT humano, `TelemetryPoint`, `TelemetryReading`, `TelemetryChannelBinding`).
- **S4:** Modelado energético y clima (`WeatherObservation`, `BaselineModel`, `ExpectedEnergyCalculation`, sin atribuir suciedad sin evidencia ni llamar «mapa térmico satelital» a Sentinel/GOES).
- **S5:** Incidentes, integridad y seguridad (`IntegrityAssessment`, `TelemetryIntegrityEvent`, `SecurityIncident`, sin confundir datos inconsistentes con ciberataque confirmado).
- **S6:** Pérdidas económicas (`EconomicLossAssessment`, `TariffRule`, `EnergyLossSummary`).
- **S7:** Auditorías y planes de mantenimiento (`MaintenancePlan`, `MaintenancePlanVersion`, `PlanBenefit`, `PlanEligibilityAssessment`).
- **S8:** Mantenimiento y misiones (`WorkOrder`, `DroneMission` simulada/controlada con drones físicos deshabilitados).
- **Nota:** La suite completa de CRUD autogenerados por JHipster (`*ResourceIT` de las 67 entidades) no se ejecutó en este corte; solo se ejecutaron `TenantSddIT`, `TechnicalStructureTest`, `AuthenticateControllerIT` y `SecurityUtilsUnitTest`.
