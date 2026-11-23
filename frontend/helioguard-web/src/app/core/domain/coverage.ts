import type { SnapshotListKey } from './model';

export interface CoverageRow {
  entity: string;
  key: SnapshotListKey;
  route: string;
  action: string;
}

export const MODEL_COVERAGE: readonly CoverageRow[] = [
  { entity: 'Organization', key: 'organizations', route: '/organizaciones', action: 'Cambiar tenant activo y ver el estado.' },
  { entity: 'OrganizationMembership', key: 'memberships', route: '/organizaciones', action: 'Elegir usuario demo y ver el rol simulado.' },
  { entity: 'InstallationAccessGrant', key: 'grants', route: '/organizaciones', action: 'Probar lectura cruzada: Jujuy mantiene Casa Andina y no ve Salta.' },
  { entity: 'Site', key: 'sites', route: '/instalaciones', action: 'Ver sitio, huso horario y clima de la región.' },
  { entity: 'Installation', key: 'installations', route: '/instalaciones', action: 'Abrir la ficha y filtrar por tenant.' },
  { entity: 'EquipmentModel', key: 'equipmentModels', route: '/instalaciones', action: 'Consultar catálogo en la ficha. Solo lectura de catálogo global.' },
  { entity: 'SolarArray', key: 'arrays', route: '/parque', action: 'Seleccionar un bloque en la vista cenital.' },
  { entity: 'SolarString', key: 'strings', route: '/instalaciones', action: 'Activar o desactivar el string y persistir.' },
  { entity: 'Equipment', key: 'equipment', route: '/instalaciones', action: 'Ver inversor, sensor y estado en la ficha.' },
  { entity: 'Gateway', key: 'gateways', route: '/telemetria', action: 'Ver Raspberry, frescura y corte de WAN.' },
  { entity: 'MetricDefinition', key: 'metrics', route: '/telemetria', action: 'Ver unidad canónica W o Wh. Catálogo de solo lectura.' },
  { entity: 'TelemetryChannel', key: 'channels', route: '/telemetria', action: 'Ver canal del inversor y período de muestreo.' },
  { entity: 'AlertRule', key: 'alertRules', route: '/incidentes', action: 'Ver la regla que origina el incidente de déficit.' },
  { entity: 'Incident', key: 'incidents', route: '/incidentes', action: 'Crear, reconocer o resolver el incidente energético.' },
  { entity: 'IncidentEvent', key: 'incidentEvents', route: '/incidentes', action: 'Agregar un evento de reconocimiento.' },
  { entity: 'EnergyModelRun', key: 'modelRuns', route: '/energia', action: 'Comparar esperado y real del intervalo.' },
  { entity: 'LossAssessment', key: 'lossAssessments', route: '/energia', action: 'Recalcular el déficit al activar el sector sucio.' },
  { entity: 'LossCauseEstimate', key: 'lossCauses', route: '/energia', action: 'Repartir suciedad y causa desconocida sin superar el déficit.' },
  { entity: 'TariffPlan', key: 'tariffPlans', route: '/economia', action: 'Ver el plan tarifario demostrativo en ARS.' },
  { entity: 'TariffRate', key: 'tariffRates', route: '/economia', action: 'Editar compra evitada e inyección y persistir.' },
  { entity: 'EconomicAssessment', key: 'economicAssessments', route: '/economia', action: 'Ver el monto ARS recalculado del déficit.' },
  { entity: 'AuditTemplate', key: 'auditTemplates', route: '/auditorias', action: 'Usar la plantilla versionada publicada. No se edita publicada.' },
  { entity: 'AuditQuestion', key: 'auditQuestions', route: '/auditorias', action: 'Responder por sección.' },
  { entity: 'AuditReport', key: 'auditReports', route: '/auditorias', action: 'Guardar, cerrar y reabrir el informe.' },
  { entity: 'AuditAnswer', key: 'auditAnswers', route: '/auditorias', action: 'Persistir la respuesta del checklist.' },
  { entity: 'AuditFinding', key: 'auditFindings', route: '/auditorias', action: 'Registrar un hallazgo con recomendación.' },
  { entity: 'AuditEvidence', key: 'auditEvidence', route: '/auditorias', action: 'Asociar una evidencia local con hash de demo.' },
  { entity: 'MaintenancePlan', key: 'maintenancePlans', route: '/planes', action: 'Ver el plan de cobertura.' },
  { entity: 'MaintenancePlanVersion', key: 'planVersions', route: '/planes', action: 'Leer SLA, exclusiones y versión.' },
  { entity: 'ServiceAgreement', key: 'agreements', route: '/planes', action: 'Ver el acuerdo con el prestador.' },
  { entity: 'PlanSubscription', key: 'subscriptions', route: '/planes', action: 'Ver la suscripción activa.' },
  { entity: 'ServiceProposal', key: 'proposals', route: '/planes', action: 'Aceptar la propuesta versionada.' },
  { entity: 'WorkOrder', key: 'workOrders', route: '/mantenimiento', action: 'Crear la orden y aceptar el trabajo.' },
  { entity: 'WorkOrderTask', key: 'tasks', route: '/mantenimiento', action: 'Pasar la tarea a en curso y a hecha.' },
  { entity: 'MaintenanceIntervention', key: 'interventions', route: '/mantenimiento', action: 'Registrar la intervención de limpieza simulada.' },
  { entity: 'InterventionEvidence', key: 'interventionEvidence', route: '/mantenimiento', action: 'Guardar el esquema local de antes y después.' },
  { entity: 'RecoveryAssessment', key: 'recoveries', route: '/mantenimiento', action: 'Comparar recuperación bajo el mismo intervalo.' },
  { entity: 'DeviceIdentity', key: 'devices', route: '/integridad', action: 'Ver identidad del inversor. Sin clave privada.' },
  { entity: 'TelemetryIntegrityEvent', key: 'integrityEvents', route: '/integridad', action: 'Abrir la prueba de replay o de valor improbable.' },
  { entity: 'IntegrityAssessment', key: 'integrityAssessments', route: '/integridad', action: 'Leer el nivel de confianza y la regla aplicada.' },
  { entity: 'EvidenceSource', key: 'evidenceSources', route: '/fuentes', action: 'Ver licencia y tipo. El clima no es termografía satelital.' },
  { entity: 'DataProvenance', key: 'provenance', route: '/fuentes', action: 'Ver huella, calidad y versión de algoritmo.' },
  { entity: 'SecurityIncident', key: 'securityIncidents', route: '/integridad', action: 'Abrir investigación sin declarar ataque confirmado.' },
  { entity: 'DroneAsset', key: 'drones', route: '/misiones', action: 'Ver flota y límite de viento.' },
  { entity: 'DroneMission', key: 'missions', route: '/misiones', action: 'Avanzar estados válidos hasta completar la simulación.' },
  { entity: 'MissionAuthorization', key: 'authorizations', route: '/misiones', action: 'Aprobar solo después del checklist. La aprobación no es permiso real.' },
  { entity: 'VerificationResult', key: 'verifications', route: '/misiones', action: 'Registrar el resultado comparado, sin prometer recuperación automática.' },
];
