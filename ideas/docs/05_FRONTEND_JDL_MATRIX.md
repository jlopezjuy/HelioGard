# HelioGuard — matriz JDL v4 → Angular UX

**Fuente única:** `specs/domain.v4.draft.jdl` (**47 entidades, 114 relaciones declaradas; DRAFT, pendiente validar con CLI**).

Nunca generar 47 CRUD automáticos como menú. Diseñar experiencias agrupadas y cada entidad con modelo TypeScript/relaciones, fixture y función contextual o principal.

| Entidad JDL | Contexto | Ruta | Tratamiento |
|---|---|---|---|
| `Organization` | Organizaciones y permisos | `/organizaciones` | Principal |
| `OrganizationMembership` | Organizaciones y permisos | `/organizaciones` | Principal |
| `InstallationAccessGrant` | Organizaciones y permisos | `/organizaciones` | Principal |
| `Site` | Instalaciones y equipamiento | `/instalaciones` | Principal |
| `Installation` | Instalaciones y equipamiento | `/instalaciones` | Principal |
| `EquipmentModel` | Instalaciones y equipamiento | `/instalaciones` | Contextual |
| `SolarArray` | Instalaciones y equipamiento | `/instalaciones` | Contextual |
| `SolarString` | Instalaciones y equipamiento | `/instalaciones` | Contextual |
| `Equipment` | Instalaciones y equipamiento | `/instalaciones` | Principal |
| `Gateway` | Instalaciones y equipamiento | `/instalaciones` | Principal |
| `MetricDefinition` | Instalaciones y equipamiento | `/instalaciones` | Contextual |
| `TelemetryChannel` | Instalaciones y equipamiento | `/instalaciones` | Contextual |
| `AlertRule` | Monitoreo y diagnóstico | `/monitoreo` | Contextual |
| `Incident` | Monitoreo y diagnóstico | `/monitoreo` | Principal |
| `IncidentEvent` | Monitoreo y diagnóstico | `/monitoreo` | Contextual |
| `EnergyModelRun` | Monitoreo y diagnóstico | `/monitoreo` | Principal |
| `LossAssessment` | Monitoreo y diagnóstico | `/monitoreo` | Principal |
| `LossCauseEstimate` | Monitoreo y diagnóstico | `/monitoreo` | Contextual |
| `TariffPlan` | Economía energética | `/economia` | Principal |
| `TariffRate` | Economía energética | `/economia` | Contextual |
| `EconomicAssessment` | Economía energética | `/economia` | Principal |
| `AuditTemplate` | Auditorías | `/auditorias` | Contextual |
| `AuditQuestion` | Auditorías | `/auditorias` | Contextual |
| `AuditReport` | Auditorías | `/auditorias` | Principal |
| `AuditAnswer` | Auditorías | `/auditorias` | Contextual |
| `AuditFinding` | Auditorías | `/auditorias` | Principal |
| `AuditEvidence` | Auditorías | `/auditorias` | Contextual |
| `MaintenancePlan` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `MaintenancePlanVersion` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `ServiceAgreement` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `PlanSubscription` | Cobertura y mantenimiento | `/mantenimiento` | Principal |
| `ServiceProposal` | Cobertura y mantenimiento | `/mantenimiento` | Principal |
| `WorkOrder` | Cobertura y mantenimiento | `/mantenimiento` | Principal |
| `WorkOrderTask` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `MaintenanceIntervention` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `InterventionEvidence` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `RecoveryAssessment` | Cobertura y mantenimiento | `/mantenimiento` | Contextual |
| `DeviceIdentity` | Integridad y confianza | `/integridad` | Contextual |
| `TelemetryIntegrityEvent` | Integridad y confianza | `/integridad` | Principal |
| `IntegrityAssessment` | Integridad y confianza | `/integridad` | Contextual |
| `EvidenceSource` | Fuentes y procedencia | `/fuentes` | Contextual |
| `DataProvenance` | Fuentes y procedencia | `/fuentes` | Contextual |
| `SecurityIncident` | Integridad y confianza | `/integridad` | Principal |
| `DroneAsset` | Misiones de drones | `/misiones` | Contextual |
| `DroneMission` | Misiones de drones | `/misiones` | Principal |
| `MissionAuthorization` | Misiones de drones | `/misiones` | Contextual |
| `VerificationResult` | Misiones de drones | `/misiones` | Contextual |

## Historias transversales obligatorias

- Múltiples organizaciones, permisos por `OrganizationMembership`, `InstallationAccessGrant` y activo de dron de empresa externa con permiso puntual.
- `EnergyModelRun` → `LossAssessment` → `LossCauseEstimate` → `EconomicAssessment` presenta comparabilidad y procedencia sin sumar atribuciones dos veces.
- `TelemetryIntegrityEvent` y `IntegrityAssessment` discriminan duplicado/replay de inconsistencia física, `SecurityIncident` conserva resolución.
- `EvidenceSource` y `DataProvenance` muestran fuente, fecha, resolución/calidad, licencia y etiqueta `ESTIMATED/SUSPECT`.
- `DroneAsset` → `DroneMission` → `MissionAuthorization` → `VerificationResult` exige aprobación en mock antes de vuelo simulado; `WorkOrder`/`RecoveryAssessment` completan circuito.
- `AuditTemplate`/`AuditQuestion` inmutables tras publicación; evidencias identificadas; planes comerciales versionados.
- Telemetría de alta frecuencia y eventos MQTT no son entidades JDL y se simulan mediante fixtures versionados.

## Cobertura de implementación

El agente Angular genera `docs/MODEL_COVERAGE.md` con para cada una de las 47 entidades: interfaz TypeScript, origen fixture, ruta, permisos DEMO, acción persistente y test. Validar recuento exacto contra JDL y CSV antes de declarar completo.

## Menú operativo (no por tabla)

`/inicio`, `/parque`, `/instalaciones`, `/telemetria`, `/energia`, `/incidentes`, `/integridad`, `/fuentes`, `/misiones`, `/auditorias`, `/mantenimiento`, `/economia`, `/planes`, `/organizaciones`, `/configuracion`.
