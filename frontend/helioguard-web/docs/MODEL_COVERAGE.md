# Cobertura del modelo

El JDL v4 es borrador: 47 entidades. Cada una tiene interfaz en `src/app/core/domain/model.ts`, al menos un fixture en `createSeed()` y una acción en la ruta indicada. `climateZone` en Site es una anotación de demo, no un campo del JDL.

| Entidad | Ruta | Acción |
|---|---|---|
| `Organization` | `/organizaciones` | Cambiar tenant activo y ver el estado. |
| `OrganizationMembership` | `/organizaciones` | Elegir usuario demo y ver el rol simulado. |
| `InstallationAccessGrant` | `/organizaciones` | Probar lectura cruzada: Jujuy mantiene Casa Andina y no ve Salta. |
| `Site` | `/instalaciones` | Ver sitio, huso horario y clima de la región. |
| `Installation` | `/instalaciones` | Abrir la ficha y filtrar por tenant. |
| `EquipmentModel` | `/instalaciones` | Consultar catálogo en la ficha. Solo lectura de catálogo global. |
| `SolarArray` | `/parque` | Seleccionar un bloque en la vista cenital. |
| `SolarString` | `/instalaciones` | Activar o desactivar el string y persistir. |
| `Equipment` | `/instalaciones` | Ver inversor, sensor y estado en la ficha. |
| `Gateway` | `/telemetria` | Ver Raspberry, frescura y corte de WAN. |
| `MetricDefinition` | `/telemetria` | Ver unidad canónica W o Wh. Catálogo de solo lectura. |
| `TelemetryChannel` | `/telemetria` | Ver canal del inversor y período de muestreo. |
| `AlertRule` | `/incidentes` | Ver la regla que origina el incidente de déficit. |
| `Incident` | `/incidentes` | Crear, reconocer o resolver el incidente energético. |
| `IncidentEvent` | `/incidentes` | Agregar un evento de reconocimiento. |
| `EnergyModelRun` | `/energia` | Comparar esperado y real del intervalo. |
| `LossAssessment` | `/energia` | Recalcular el déficit al activar el sector sucio. |
| `LossCauseEstimate` | `/energia` | Repartir suciedad y causa desconocida sin superar el déficit. |
| `TariffPlan` | `/economia` | Ver el plan tarifario demostrativo en ARS. |
| `TariffRate` | `/economia` | Editar compra evitada e inyección y persistir. |
| `EconomicAssessment` | `/economia` | Ver el monto ARS recalculado del déficit. |
| `AuditTemplate` | `/auditorias` | Usar la plantilla versionada publicada. No se edita publicada. |
| `AuditQuestion` | `/auditorias` | Responder por sección. |
| `AuditReport` | `/auditorias` | Guardar, cerrar y reabrir el informe. |
| `AuditAnswer` | `/auditorias` | Persistir la respuesta del checklist. |
| `AuditFinding` | `/auditorias` | Registrar un hallazgo con recomendación. |
| `AuditEvidence` | `/auditorias` | Asociar una evidencia local con hash de demo. |
| `MaintenancePlan` | `/planes` | Ver el plan de cobertura. |
| `MaintenancePlanVersion` | `/planes` | Leer SLA, exclusiones y versión. |
| `ServiceAgreement` | `/planes` | Ver el acuerdo con el prestador. |
| `PlanSubscription` | `/planes` | Ver la suscripción activa. |
| `ServiceProposal` | `/planes` | Aceptar la propuesta versionada. |
| `WorkOrder` | `/mantenimiento` | Crear la orden y aceptar el trabajo. |
| `WorkOrderTask` | `/mantenimiento` | Pasar la tarea a en curso y a hecha. |
| `MaintenanceIntervention` | `/mantenimiento` | Registrar la intervención de limpieza simulada. |
| `InterventionEvidence` | `/mantenimiento` | Guardar el esquema local de antes y después. |
| `RecoveryAssessment` | `/mantenimiento` | Comparar recuperación bajo el mismo intervalo. |
| `DeviceIdentity` | `/integridad` | Ver identidad del inversor. Sin clave privada. |
| `TelemetryIntegrityEvent` | `/integridad` | Abrir la prueba de replay o de valor improbable. |
| `IntegrityAssessment` | `/integridad` | Leer el nivel de confianza y la regla aplicada. |
| `EvidenceSource` | `/fuentes` | Ver licencia y tipo. El clima no es termografía satelital. |
| `DataProvenance` | `/fuentes` | Ver huella, calidad y versión de algoritmo. |
| `SecurityIncident` | `/integridad` | Abrir investigación sin declarar ataque confirmado. |
| `DroneAsset` | `/misiones` | Ver flota y límite de viento. |
| `DroneMission` | `/misiones` | Avanzar estados válidos hasta completar la simulación. |
| `MissionAuthorization` | `/misiones` | Aprobar solo después del checklist. La aprobación no es permiso real. |
| `VerificationResult` | `/misiones` | Registrar el resultado comparado, sin prometer recuperación automática. |

Conteo: 47.
