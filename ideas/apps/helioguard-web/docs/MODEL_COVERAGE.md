# Cobertura del JDL v4 en la demo

Fuente: `specs/domain.v4.draft.jdl`. **47 entidades.** El JDL sigue en borrador: esta corrida no ejecutó el CLI de JHipster. Estado de esa validación: **NO VERIFICADO**.

La telemetría cruda no es entidad JDL. Vive en `TelemetrySample` y en la cola `OutboxItem`.

Permiso DEMO: la sesión simula el rol de la membresía activa. No es seguridad real.

| Entidad | Tipo TS | Fixture | Ruta | Acción que persiste |
|---|---|---|---|---|
| Organization | `Organization` | 3 organizaciones | `/organizaciones` | Cambiar de persona cambia la organización activa |
| OrganizationMembership | `OrganizationMembership` | 5 membresías | `/organizaciones` | El rol filtra el menú |
| InstallationAccessGrant | `InstallationAccessGrant` | grant de mantenimiento | `/organizaciones` | Andes Solar ve Casa Andina solo en mantenimiento |
| Site | `Site` | 3 sitios | `/instalaciones/:id` | Lectura de clima, techo y zona horaria |
| Installation | `Installation` | Puna, Casa, Calchaquí | `/instalaciones` | Selección y rechazo si está fuera de ámbito |
| EquipmentModel | `EquipmentModel` | 5 modelos de catálogo | `/instalaciones/:id` | Lectura. Catálogo global, sin secretos |
| SolarArray | `SolarArray` | A1–A4, techo, mesa | `/parque`, `/instalaciones/:id` | Selección de bloque |
| SolarString | `SolarString` | 6 strings | `/instalaciones/:id` | Activar o sacar de servicio |
| Equipment | `Equipment` | inversores, ESP32, medidor, batería | `/instalaciones/:id` | El escenario de inversor marca INV-03 FAILED |
| Gateway | `Gateway` | 3 Raspberry demo | `/telemetria` | Escenario sin enlace lo pasa a OFFLINE |
| MetricDefinition | `MetricDefinition` | W, Wh, W/m2 | `/instalaciones/:id` | Lectura de unidad y si es contador |
| TelemetryChannel | `TelemetryChannel` | canal de INV-03 y GHI | `/telemetria` | Lectura del período de muestreo |
| AlertRule | `AlertRule` | déficit, BMS, inversor | `/incidentes` | Dispara el incidente de escenario, sin editor aparte |
| Incident | `Incident` | casa, Salta y el de suciedad al activarlo | `/incidentes` | Reconocer y deduplicar por `dedupKey` |
| IncidentEvent | `IncidentEvent` | eventos de apertura | `/incidentes` | Se agrega al reconocer o crear la orden |
| EnergyModelRun | `EnergyModelRun` | ayer y ventana viva | `/energia` | El escenario reescribe la corrida viva |
| LossAssessment | `LossAssessment` | ayer y viva | `/energia` | Wh integrados, o sin estimación |
| LossCauseEstimate | `LossCauseEstimate` | UNKNOWN / SOILING / HARDWARE / WEATHER | `/energia` | La suma no supera el déficit |
| TariffPlan | `TariffPlan` | una por instalación | `/economia` | Lectura. Precios ficticios |
| TariffRate | `TariffRate` | 200/80 ARS en Puna | `/economia` | Editar compra e inyección |
| EconomicAssessment | `EconomicAssessment` | ayer y viva | `/economia` | Se recalcula al guardar la tarifa |
| AuditTemplate | `AuditTemplate` | plantilla 1.0 publicada | `/auditorias` | Lectura. No se reescribe publicada |
| AuditQuestion | `AuditQuestion` | 4 preguntas | `/auditorias` | Lectura ordenada |
| AuditReport | `AuditReport` | informe en curso | `/auditorias` | Enviar, cerrar, reabrir |
| AuditAnswer | `AuditAnswer` | 4 respuestas vacías | `/auditorias` | Guardar sí, no o nota |
| AuditFinding | `AuditFinding` | se crea al cerrar | `/auditorias` | Alta al aprobar |
| AuditEvidence | `AuditEvidence` | clave local | `/auditorias` | Alta junto al hallazgo |
| MaintenancePlan | `MaintenancePlan` | cobertura Puna | `/planes` | Lectura |
| MaintenancePlanVersion | `MaintenancePlanVersion` | 2026.1 y borrador 2026.2 | `/planes` | Publicar al aceptar la propuesta |
| ServiceAgreement | `ServiceAgreement` | acuerdo activo | `/planes` | Lectura |
| PlanSubscription | `PlanSubscription` | activa y otra PROPOSED | `/planes` | Alta al aceptar |
| ServiceProposal | `ServiceProposal` | PROP-PUNA-2026-2 | `/planes` | Aceptar |
| WorkOrder | `WorkOrder` | trimestral y las de incidente | `/mantenimiento` | Transiciones de a un estado |
| WorkOrderTask | `WorkOrderTask` | tareas de la orden | `/mantenimiento` | Marcar hecha |
| MaintenanceIntervention | `MaintenanceIntervention` | al completar misión o guardar nota | `/mantenimiento` | Nota y costo ilustrativo |
| InterventionEvidence | `InterventionEvidence` | SVG local | `/misiones` | Alta al completar |
| RecoveryAssessment | `RecoveryAssessment` | al verificar | `/misiones` | Wh comparados en el mismo horario |
| DeviceIdentity | `DeviceIdentity` | inverter-03 | `/integridad` | Lectura. La huella no es una clave |
| TelemetryIntegrityEvent | `TelemetryIntegrityEvent` | histórico y los de la sesión | `/integridad` | Replay, duplicado o inconsistencia |
| IntegrityAssessment | `IntegrityAssessment` | uno por evento nuevo | `/integridad` | Confianza SUSPECT o VALIDATED |
| EvidenceSource | `EvidenceSource` | clima, inversor, dron simulados | `/fuentes` | Lectura y licencia |
| DataProvenance | `DataProvenance` | dos registros de Puna | `/fuentes` | Lectura ESTIMATED |
| SecurityIncident | `SecurityIncident` | se abre con replay o inyección | `/integridad` | Nota: no hay prueba de intrusión |
| DroneAsset | `DroneAsset` | DR-DEMO-1 | `/misiones` | Límite de viento usado al despachar |
| DroneMission | `DroneMission` | se crea en la demo | `/misiones` | Estados sin saltar a IN_FLIGHT |
| MissionAuthorization | `MissionAuthorization` | PENDING hasta decidir | `/misiones` | Aprobar o rechazar |
| VerificationResult | `VerificationResult` | al completar | `/misiones` | RECOVERED, NOT_RECOVERED o INCONCLUSIVE |

Prueba de conteo: `JDL_ENTITY_NAMES` en `src/app/core/domain/model.ts` tiene 47 nombres y esta tabla tiene 47 filas.
