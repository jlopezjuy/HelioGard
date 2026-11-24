#!/usr/bin/env python3
"""Generates reviewable v5 JDL from the exact uploaded v4, preserving v4 entities."""
from pathlib import Path
import re
p=Path(__file__).parent
src=(p/'specs/domain.v4.original.jdl').read_text()
src=src.replace('// HelioGuard — dominio transaccional multi-tenant v4 (BORRADOR, VALIDACIÓN PENDIENTE)', '// HelioGuard — modelo transaccional multi-tenant v5 (DRAFT: VALIDAR CON JHIPSTER CLI + POSTGRESQL)')
src=src.replace('// No importar sin validar con la versión exacta de JHipster y contrastar esquema anterior.', '// Derivado de domain.v4.draft.jdl + REQUIREMENTS.md. NO IMPORTAR EN PRODUCCION. Revision 2026-11-23.')
src=src.replace('// Fuente de verdad del dominio para HELIOGUARD: este archivo, no los JDL históricos.', '// Especificacion propuesta. Prioridad: integridad, topologia, energia, seguridad, servicios y trazabilidad.')

def insert_before(text,anchor,piece):
    assert anchor in text, f'Missing anchor {anchor[:100]}'
    return text.replace(anchor,piece+'\n'+anchor,1)

def replace_exact(old,new):
    global src
    assert src.count(old)==1, f'Expected 1 match: {old[:90]}, got {src.count(old)}'
    src=src.replace(old,new,1)

# Existing object refinements: preserve identifiable entities and model clear invariants.
replace_exact('enum VerificationStatus { PENDING, INCONCLUSIVE, RECOVERED, NOT_RECOVERED }', '''enum VerificationStatus { PENDING, INCONCLUSIVE, RECOVERED, NOT_RECOVERED }
enum EnergyModelRunStatus { SUCCEEDED, INSUFFICIENT_DATA, FAILED }
enum LossAssessmentStatus { ESTIMATED, INCONCLUSIVE, VALIDATED }
enum ClimateZone { PUNA, QUEBRADA, VALLES, YUNGAS, CHACO, VALLES_CALCHAQUIES, OTHER }
enum WeatherBatchStatus { RECEIVED, VALIDATED, INSUFFICIENT_DATA, FAILED }
enum TopologyConnectionKind { DC, AC, DATA, BATTERY_DC }
enum LocalizationScope { INSTALLATION, SECTOR, INVERTER, STRING, MODULE, UNKNOWN }
enum RuleComparison { GREATER_THAN, LESS_THAN, OUTSIDE_RANGE, DEVIATION_PERCENT, STALE_DATA, BOOLEAN_MATCH }
enum AssignmentStatus { OFFERED, ACCEPTED, REJECTED, ACTIVE, RELEASED, REVOKED }
enum PlanEligibilityStatus { ELIGIBLE, CONDITIONAL, REQUIRES_REMEDIATION, INCOMPLETE, NOT_ELIGIBLE }
enum MissionTargetKind { SECTOR, STRING, EQUIPMENT }
enum SafetyCheckResult { PASS, FAIL, NOT_APPLICABLE, UNKNOWN }\nenum WorkApprovalStatus { REQUESTED, APPROVED, REJECTED, EXPIRED, REVOKED }''')
replace_exact('''  latitude BigDecimal
  longitude BigDecimal
  altitudeM BigDecimal''', '''  latitude BigDecimal min(-90) max(90)
  longitude BigDecimal min(-180) max(180)
  altitudeM BigDecimal''')
replace_exact('''  nominalDcKw BigDecimal required
''', '''  nominalDcKw BigDecimal required min(0)
''')
replace_exact('''  azimuthDegrees BigDecimal
  tiltDegrees BigDecimal''','''  azimuthDegrees BigDecimal min(0) max(360)
  tiltDegrees BigDecimal min(0) max(90)''')
replace_exact('''entity Equipment {
  tag String required maxlength(120)
  serialNumber String maxlength(160)
  kind EquipmentKind required
  status EquipmentStatus required''','''entity Equipment {
  tag String required maxlength(120)
  serialNumber String maxlength(160)
  status EquipmentStatus required''')
replace_exact('''entity AlertRule {
  name String required maxlength(120)
  ruleCode String required maxlength(100)
  severity AlertSeverity required
  enabled Boolean required
  thresholdValue BigDecimal
  minPersistenceSeconds Integer
}''','''entity AlertRule {
  name String required maxlength(120)
  ruleCode String required maxlength(100)
  severity AlertSeverity required
  enabled Boolean required
}''')
replace_exact('''  periodStart Instant required
  periodEnd Instant required
  expectedWh BigDecimal required
  actualWh BigDecimal
  coveragePct BigDecimal
  inputFingerprint String maxlength(100)
  calculatedAt Instant required''','''  periodStart Instant required
  periodEnd Instant required
  status EnergyModelRunStatus required
  expectedWh BigDecimal
  expectedLowerWh BigDecimal
  expectedUpperWh BigDecimal
  actualWh BigDecimal
  coveragePct BigDecimal min(0) max(100)
  inputFingerprint String maxlength(100)
  qualityNote String maxlength(300)
  calculatedAt Instant required''')
replace_exact('''  methodology String required maxlength(100)
  assessedAt Instant required
  validationStatus String required maxlength(60)''','''  methodology String required maxlength(100)
  assessedAt Instant required
  validationStatus LossAssessmentStatus required''')
replace_exact('''entity AuditAnswer {
  answerText TextBlob
  answeredAt Instant
  compliant Boolean
}''','''entity AuditAnswer {
  answerText TextBlob
  answerNumber BigDecimal
  answerBoolean Boolean
  answerUnitCode String maxlength(24)
  answeredAt Instant
  compliant Boolean
}''')
replace_exact('''  sourceObservedAt Instant
  sequenceNumber Long
  payloadSha256''','''  sourceObservedAt Instant
  bootId String maxlength(120)
  sequenceNumber Long
  payloadSha256''')
replace_exact('''entity DataProvenance {
  sourceRecordKey String required maxlength(180)
  observedAt Instant required
  ingestedAt Instant required
  contentSha256 String maxlength(64)
  trustLevel DataTrustLevel required
  qualityFlags String maxlength(300)
  algorithmVersion String maxlength(80)
}''','''entity DataProvenance {
  sourceRecordKey String required maxlength(180)
  observedAt Instant required
  periodStart Instant
  periodEnd Instant
  ingestedAt Instant required
  sourceProductCode String maxlength(140)
  spatialResolutionM BigDecimal
  contentSha256 String maxlength(64)
  trustLevel DataTrustLevel required
  qualityFlags String maxlength(300)
  algorithmVersion String maxlength(80)
}''')
replace_exact('''entity WorkOrder {
  referenceCode String required maxlength(80)
  title String required maxlength(160)
  status WorkOrderStatus required
  priority AlertSeverity required
  openedAt Instant required
  scheduledAt Instant
  closedAt Instant
}''','''entity WorkOrder {
  referenceCode String required maxlength(80)
  title String required maxlength(160)
  status WorkOrderStatus required
  priority AlertSeverity required
  openedAt Instant required
  scheduledAt Instant
  closedAt Instant
}''')
# New entities, grouped near related contexts.
insertions = {
  '// --- REGLAS, INCIDENTES Y MODELOS ENERGÉTICOS ---': '''// --- TOPOLOGIA ELECTRICA Y CARTOGRAFICA (P0) ---
// Geometria poligonal PostGIS/GeoJSON se almacena por asset externo versionado.
entity SolarSector {
  sectorCode String required maxlength(80)
  name String required maxlength(140)
  geometryReference String maxlength(350)
  validFrom Instant required
  validUntil Instant
}
// Validar Equipment.kind == INVERTER en servicio.
entity InverterMpptInput {
  inputCode String required maxlength(80)
  enabled Boolean required
  validFrom Instant required
  validUntil Instant
}
// Permite reconstruir exactamente la conexion durante una anomalia historica.
entity StringInputAssignment {
  validFrom Instant required
  validUntil Instant
  notes String maxlength(240)
}
// Conexion conceptual entre dos equipos, con trazabilidad historica.
entity EquipmentConnection {
  kind TopologyConnectionKind required
  connectionCode String required maxlength(100)
  validFrom Instant required
  validUntil Instant
}
// Relacion vigente entre canal leido y Raspberry / Gateway que lo captura.
entity TelemetryChannelBinding {
  externalPath String required maxlength(180)
  validFrom Instant required
  validUntil Instant
}
''',
  '// --- TARIFAS Y VALORIZACIÓN ---': '''// --- CLIMA, MODELO Y LOCALIZACION DE ANOMALIAS ---
// Catalogo de clima para reglas regionales: no es un pronostico.
entity ClimateRiskProfile {
  code String required unique maxlength(100)
  region ClimateZone required
  profileVersion String required maxlength(40)
  description String maxlength(250)
  active Boolean required
}
// Metadatos de una importacion; observaciones por intervalo se guardan en SQL.
entity WeatherIngestionBatch {
  providerBatchCode String required maxlength(150)
  datasetCode String required maxlength(100)
  periodStart Instant required
  periodEnd Instant required
  fetchedAt Instant required
  status WeatherBatchStatus required
  qualityNote String maxlength(300)
}
// Configuracion INMUTABLE y versionada del modelo fisico.
entity EnergyModelConfiguration {
  algorithmCode String required maxlength(80)
  configVersion String required maxlength(60)
  parameterSnapshotRef String required maxlength(350)
  inputFingerprint String required maxlength(100)
  calibratedAt Instant
  activeFrom Instant required
  activeUntil Instant
}
// No afirmar precision al panel cuando el nivel observado es inversor/string.
entity IncidentLocalization {
  scope LocalizationScope required
  locatedAt Instant required
  confidencePct BigDecimal min(0) max(100)
  evidenceSummary TextBlob
}
// Configuracion versionada separada de la definicion logica de la regla.
entity AlertRuleVersion {
  versionCode String required maxlength(50)
  comparator RuleComparison required
  thresholdValue BigDecimal
  thresholdMaxValue BigDecimal
  metricUnitCode String maxlength(24)
  windowSeconds Integer min(1)
  cooldownSeconds Integer min(0)
  minCoveragePct BigDecimal min(0) max(100)
  validFrom Instant required
  validUntil Instant
}
''',
  '// --- MANTENIMIENTO Y PLANES COMERCIALES ---': '''// Firma como registro de evidencia, NO equivale automaticamente a firma digital legal.
entity AuditReportSignature {
  signedAt Instant required
  documentSha256 String required maxlength(64)
  signatureReference String maxlength(400)
}
''',
  '// --- SEGURIDAD DE TELEMETRÍA, PROCEDENCIA Y MISIONES ---': '''// Historico de asignacion a prestador, usuario y periodo (no solo miembro actual).
entity WorkOrderApproval {
  status WorkApprovalStatus required
  requestedAt Instant required
  decidedAt Instant
  expiresAt Instant
  consentReference String maxlength(400)
  decisionNote String maxlength(300)
}
entity WorkOrderAssignment {
  status AssignmentStatus required
  assignedAt Instant required
  acceptedAt Instant
  releasedAt Instant
  notes String maxlength(300)
}
// Prestaciones medibles de una version publicada del plan de mantenimiento.
entity PlanBenefit {
  benefitCode String required maxlength(90)
  description String required maxlength(240)
  includedQuantity BigDecimal
  unitCode String maxlength(35)
  intervalMonths Integer min(1)
  termsReference String maxlength(350)
}
// Eligibility no representa suscripcion ni emision de seguro.
entity PlanEligibilityAssessment {
  status PlanEligibilityStatus required
  assessedAt Instant required
  rationale TextBlob
  requiresCorrection Boolean required
}
''',
  'relationship ManyToOne {': '''// Evidencia y bitacora append-only de casos de seguridad.
entity SecurityCaseEvent {
  eventCode String required maxlength(100)
  occurredAt Instant required
  detail TextBlob
}
entity SecurityCaseEvidence {
  evidenceCode String required maxlength(100)
  storageReference String maxlength(400)
  sha256 String maxlength(64)
  recordedAt Instant required
  note String maxlength(250)
}
// Las misiones y el acceso a rutas reales SIEMPRE requieren autorizacion externa.
entity DroneMissionTarget {
  targetCode String required maxlength(100)
  kind MissionTargetKind required
  priority Integer min(1)
  notes String maxlength(250)
}
entity DroneMissionEvent {
  eventCode String required maxlength(100)
  occurredAt Instant required
  description TextBlob
}
entity MissionSafetyCheck {
  checkCode String required maxlength(100)
  result SafetyCheckResult required
  checkedAt Instant required
  evidenceReference String maxlength(400)
}
'''
}
for anchor,block in insertions.items(): src=insert_before(src, anchor, block.rstrip())

# Original redundant work assignment field is REMOVED: assignments become historical.
replace_exact('  WorkOrder{assignedMembership(id)} to OrganizationMembership\n','')
replace_exact('  Incident{alertRule(name)} to AlertRule\n','')

# Append relationships within existing ManyToOne block before its final closing brace.
rel='''
  // V5: todas las referencias con "organization" deben validarse con FKs compuestas en SQL.
  SolarSector{organization(name) required} to Organization
  SolarSector{installation(name) required} to Installation
  SolarArray{solarSector(sectorCode)} to SolarSector
  InverterMpptInput{organization(name) required} to Organization
  InverterMpptInput{inverter(tag) required} to Equipment
  StringInputAssignment{organization(name) required} to Organization
  StringInputAssignment{solarString(stringCode) required} to SolarString
  StringInputAssignment{mpptInput(inputCode) required} to InverterMpptInput
  EquipmentConnection{organization(name) required} to Organization
  EquipmentConnection{fromEquipment(tag) required} to Equipment
  EquipmentConnection{toEquipment(tag) required} to Equipment
  TelemetryChannelBinding{organization(name) required} to Organization
  TelemetryChannelBinding{telemetryChannel(externalChannelCode) required} to TelemetryChannel
  TelemetryChannelBinding{gateway(hardwareId) required} to Gateway
  ClimateRiskProfile{defaultInstallation(name)} to Installation
  WeatherIngestionBatch{organization(name) required} to Organization
  WeatherIngestionBatch{installation(name) required} to Installation
  WeatherIngestionBatch{evidenceSource(code) required} to EvidenceSource
  EnergyModelConfiguration{organization(name) required} to Organization
  EnergyModelConfiguration{installation(name) required} to Installation
  EnergyModelRun{modelConfiguration(configVersion)} to EnergyModelConfiguration
  EnergyModelRun{weatherBatch(providerBatchCode)} to WeatherIngestionBatch
  IncidentLocalization{organization(name) required} to Organization
  IncidentLocalization{incident(title) required} to Incident
  IncidentLocalization{solarSector(sectorCode)} to SolarSector
  IncidentLocalization{solarString(stringCode)} to SolarString
  IncidentLocalization{equipment(tag)} to Equipment
  AlertRuleVersion{organization(name) required} to Organization
  AlertRuleVersion{alertRule(ruleCode) required} to AlertRule
  Incident{alertRuleVersion(versionCode)} to AlertRuleVersion
  AuditFinding{equipment(tag)} to Equipment
  AuditReportSignature{organization(name) required} to Organization
  AuditReportSignature{auditReport(id) required} to AuditReport
  AuditReportSignature{signerMembership(id) required} to OrganizationMembership
  WorkOrderApproval{organization(name) required} to Organization
  WorkOrderApproval{workOrder(referenceCode) required} to WorkOrder
  WorkOrderApproval{decidedByMembership(id)} to OrganizationMembership
  WorkOrderAssignment{organization(name) required} to Organization
  WorkOrderAssignment{workOrder(referenceCode) required} to WorkOrder
  WorkOrderAssignment{providerOrganization(name) required} to Organization
  WorkOrderAssignment{assigneeMembership(id) required} to OrganizationMembership
  MaintenanceIntervention{performedByMembership(id)} to OrganizationMembership
  PlanBenefit{organization(name) required} to Organization
  PlanBenefit{planVersion(versionCode) required} to MaintenancePlanVersion
  PlanEligibilityAssessment{organization(name) required} to Organization
  PlanEligibilityAssessment{installation(name) required} to Installation
  PlanEligibilityAssessment{auditReport(id)} to AuditReport
  PlanEligibilityAssessment{planVersion(versionCode)} to MaintenancePlanVersion
  SecurityCaseEvent{organization(name) required} to Organization
  SecurityCaseEvent{securityIncident(referenceCode) required} to SecurityIncident
  SecurityCaseEvent{actorMembership(id)} to OrganizationMembership
  SecurityCaseEvidence{organization(name) required} to Organization
  SecurityCaseEvidence{securityIncident(referenceCode) required} to SecurityIncident
  SecurityCaseEvidence{integrityAssessment(id)} to IntegrityAssessment
  SecurityCaseEvidence{dataProvenance(id)} to DataProvenance
  DroneMissionTarget{organization(name) required} to Organization
  DroneMissionTarget{mission(missionCode) required} to DroneMission
  DroneMissionTarget{solarSector(sectorCode)} to SolarSector
  DroneMissionTarget{solarString(stringCode)} to SolarString
  DroneMissionTarget{equipment(tag)} to Equipment
  DroneMissionEvent{organization(name) required} to Organization
  DroneMissionEvent{mission(missionCode) required} to DroneMission
  DroneMissionEvent{operatorMembership(id)} to OrganizationMembership
  MissionSafetyCheck{organization(name) required} to Organization
  MissionSafetyCheck{mission(missionCode) required} to DroneMission
  MissionSafetyCheck{checkedByMembership(id)} to OrganizationMembership
'''
# Fix ClimateRiskProfile direction: reference installation to global profile, not inverse.
rel=rel.replace('  ClimateRiskProfile{defaultInstallation(name)} to Installation\n','  Installation{climateRiskProfile(code)} to ClimateRiskProfile\n')
# Insert rel before end of ManyToOne block, right before dto.
replace_exact('}\n\ndto * with mapstruct',rel+'}\n\ndto * with mapstruct')
# v9 docs caution: use "pagination" option key; semantics must still be tested against generator.
replace_exact('paginate Installation, Equipment, Gateway, Incident, AuditReport, WorkOrder, PlanSubscription, ServiceProposal, TelemetryIntegrityEvent, SecurityIncident, DroneMission with pagination','pagination Installation, Equipment, Gateway, Incident, AuditReport, WorkOrder, PlanSubscription, ServiceProposal, TelemetryIntegrityEvent, SecurityIncident, DroneMission with pagination')
(p/'specs/domain.v5.review.jdl').write_text(src)
print('Written',p/'specs/domain.v5.review.jdl')
