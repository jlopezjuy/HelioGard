/** Interfaces alineadas al JDL v4 borrador. No implica esquema validado por JHipster. */

export const JDL_ENTITY_NAMES = [
  'Organization',
  'OrganizationMembership',
  'Site',
  'Installation',
  'InstallationAccessGrant',
  'EquipmentModel',
  'SolarArray',
  'SolarString',
  'Equipment',
  'Gateway',
  'MetricDefinition',
  'TelemetryChannel',
  'AlertRule',
  'Incident',
  'IncidentEvent',
  'EnergyModelRun',
  'LossAssessment',
  'LossCauseEstimate',
  'TariffPlan',
  'TariffRate',
  'EconomicAssessment',
  'AuditTemplate',
  'AuditQuestion',
  'AuditReport',
  'AuditAnswer',
  'AuditFinding',
  'AuditEvidence',
  'MaintenancePlan',
  'MaintenancePlanVersion',
  'ServiceAgreement',
  'PlanSubscription',
  'ServiceProposal',
  'WorkOrder',
  'WorkOrderTask',
  'MaintenanceIntervention',
  'InterventionEvidence',
  'RecoveryAssessment',
  'DeviceIdentity',
  'TelemetryIntegrityEvent',
  'IntegrityAssessment',
  'EvidenceSource',
  'DataProvenance',
  'SecurityIncident',
  'DroneAsset',
  'DroneMission',
  'MissionAuthorization',
  'VerificationResult',
] as const;

export type JdlEntityName = (typeof JDL_ENTITY_NAMES)[number];

export type OrganizationStatus = 'ACTIVE' | 'SUSPENDED';
export type OrganizationKind = 'HOUSEHOLD' | 'BUSINESS' | 'SERVICE_PROVIDER';
export type MembershipRole = 'OWNER' | 'ADMIN' | 'OPERATOR' | 'TECHNICIAN' | 'AUDITOR' | 'VIEWER';
export type MembershipStatus = 'INVITED' | 'ACTIVE' | 'SUSPENDED' | 'REVOKED';
export type GridMode = 'GRID_TIED' | 'HYBRID' | 'OFF_GRID';
export type InstallationStatus = 'COMMISSIONING' | 'ACTIVE' | 'MAINTENANCE' | 'INACTIVE';
export type EquipmentKind = 'MODULE' | 'INVERTER' | 'BATTERY' | 'METER' | 'SENSOR' | 'OPTIMIZER' | 'PROTECTION' | 'OTHER';
export type EquipmentStatus = 'ACTIVE' | 'FAILED' | 'DECOMMISSIONED';
export type GatewayStatus = 'PROVISIONING' | 'ONLINE' | 'OFFLINE' | 'REVOKED';
export type PermissionScope = 'READ_ONLY' | 'AUDIT' | 'MAINTENANCE' | 'MANAGE';
export type AlertSeverity = 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type IncidentStatus = 'OPEN' | 'ACKNOWLEDGED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type LossCause =
  | 'SOILING'
  | 'HARDWARE'
  | 'SHADING'
  | 'THERMAL'
  | 'WEATHER'
  | 'CURTAILMENT'
  | 'BATTERY_RESTRICTION'
  | 'UNKNOWN';
export type AuditStatus = 'DRAFT' | 'IN_PROGRESS' | 'SUBMITTED' | 'APPROVED' | 'REJECTED';
export type FindingSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type WorkOrderStatus = 'CREATED' | 'ASSIGNED' | 'ACCEPTED' | 'IN_PROGRESS' | 'DONE' | 'VERIFIED' | 'CANCELLED';
export type WorkTaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE' | 'BLOCKED';
export type InterventionType = 'INSPECTION' | 'CLEANING' | 'REPAIR' | 'REPLACEMENT';
export type MaintenancePlanStatus = 'DRAFT' | 'ACTIVE' | 'RETIRED';
export type SubscriptionStatus = 'PROPOSED' | 'ACTIVE' | 'SUSPENDED' | 'CANCELLED' | 'EXPIRED';
export type ServiceAgreementStatus = 'REQUESTED' | 'ACTIVE' | 'SUSPENDED' | 'CLOSED';
export type DeviceTrustStatus = 'PROVISIONING' | 'ACTIVE' | 'SUSPENDED' | 'REVOKED';
export type IntegrityEventType =
  | 'REPLAY'
  | 'DUPLICATE_EVENT'
  | 'SEQUENCE_GAP'
  | 'SIGNATURE_INVALID'
  | 'AUTH_FAILURE'
  | 'CLOCK_SKEW'
  | 'PHYSICAL_INCONSISTENCY'
  | 'DEVICE_UNAVAILABLE';
export type IntegrityEventStatus = 'OBSERVED' | 'QUARANTINED' | 'UNDER_REVIEW' | 'EXPLAINED' | 'CONFIRMED' | 'CLOSED';
export type DataTrustLevel = 'VALIDATED' | 'ESTIMATED' | 'SUSPECT' | 'UNKNOWN';
export type EvidenceSourceKind = 'SATELLITE_WEATHER' | 'ONSITE_SENSOR' | 'INVERTER' | 'EDGE_GATEWAY' | 'DRONE' | 'MANUAL' | 'MODEL';
export type SecurityCaseStatus = 'OPEN' | 'TRIAGE' | 'INVESTIGATING' | 'MITIGATED' | 'CLOSED';
export type DroneAssetStatus = 'READY' | 'MAINTENANCE' | 'GROUNDED' | 'RETIRED';
export type DroneMissionKind = 'VISUAL_INSPECTION' | 'THERMAL_INSPECTION' | 'CLEANING';
export type DroneMissionStatus =
  | 'DRAFT'
  | 'SAFETY_REVIEW'
  | 'AWAITING_AUTHORIZATION'
  | 'AUTHORIZED'
  | 'DISPATCHED'
  | 'IN_FLIGHT'
  | 'COMPLETED'
  | 'ABORTED';
export type MissionAuthorizationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED' | 'REVOKED';
export type VerificationStatus = 'PENDING' | 'INCONCLUSIVE' | 'RECOVERED' | 'NOT_RECOVERED';
export type ScenarioId = 'normal' | 'soiling' | 'cloud' | 'frost' | 'inverter' | 'hail' | 'smoke' | 'offline-gateway';
export type BlockCode = 'A1' | 'A2' | 'A3' | 'A4';

export interface Organization {
  id: string;
  name: string;
  slug: string;
  status: OrganizationStatus;
  kind: OrganizationKind;
  createdAt: string;
}

export interface DemoUser {
  id: string;
  name: string;
  login: string;
}

export interface OrganizationMembership {
  id: string;
  organizationId: string;
  userId: string;
  role: MembershipRole;
  status: MembershipStatus;
  invitedAt: string | null;
  activatedAt: string | null;
}

export interface Site {
  id: string;
  organizationId: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  altitudeM: number;
  timeZone: string;
  climate: string;
  roof: boolean;
}

export interface Installation {
  id: string;
  organizationId: string;
  siteId: string;
  code: string;
  name: string;
  gridMode: GridMode;
  status: InstallationStatus;
  nominalDcKw: number;
  commissionedOn: string;
}

export interface InstallationAccessGrant {
  id: string;
  organizationId: string;
  installationId: string;
  granteeOrganizationId: string;
  scope: PermissionScope;
  validFrom: string;
  validUntil: string | null;
  revokedAt: string | null;
  reason: string;
}

export interface EquipmentModel {
  id: string;
  manufacturer: string;
  modelCode: string;
  revision: string;
  kind: EquipmentKind;
  nominalPowerW: number | null;
}

export interface SolarArray {
  id: string;
  organizationId: string;
  installationId: string;
  name: string;
  blockCode: BlockCode | null;
  azimuthDegrees: number;
  tiltDegrees: number;
  commissionedOn: string;
}

export interface SolarString {
  id: string;
  organizationId: string;
  solarArrayId: string;
  moduleModelId: string | null;
  stringCode: string;
  modulesInSeries: number;
  active: boolean;
}

export interface Equipment {
  id: string;
  organizationId: string;
  installationId: string;
  equipmentModelId: string;
  solarStringId: string | null;
  tag: string;
  serialNumber: string;
  kind: EquipmentKind;
  status: EquipmentStatus;
  installedOn: string;
  removedOn: string | null;
}

export interface Gateway {
  id: string;
  organizationId: string;
  installationId: string;
  hardwareId: string;
  firmwareVersion: string;
  status: GatewayStatus;
  lastSeenAt: string | null;
  provisionedAt: string;
}

export interface MetricDefinition {
  id: string;
  code: string;
  unitCode: string;
  description: string;
  counter: boolean;
}

export interface TelemetryChannel {
  id: string;
  organizationId: string;
  equipmentId: string;
  metricDefinitionId: string;
  externalChannelCode: string;
  enabled: boolean;
  samplePeriodSeconds: number;
}

export interface AlertRule {
  id: string;
  organizationId: string;
  installationId: string | null;
  name: string;
  ruleCode: string;
  severity: AlertSeverity;
  enabled: boolean;
  thresholdValue: number | null;
  minPersistenceSeconds: number | null;
}

export interface Incident {
  id: string;
  organizationId: string;
  installationId: string;
  alertRuleId: string | null;
  title: string;
  severity: AlertSeverity;
  status: IncidentStatus;
  firstSeenAt: string;
  lastSeenAt: string;
  acknowledgedAt: string | null;
  resolvedAt: string | null;
  dedupKey: string;
}

export interface IncidentEvent {
  id: string;
  organizationId: string;
  incidentId: string;
  occurredAt: string;
  eventCode: string;
  detail: string;
}

export interface EnergyModelRun {
  id: string;
  organizationId: string;
  installationId: string;
  modelName: string;
  modelVersion: string;
  periodStart: string;
  periodEnd: string;
  expectedWh: number;
  actualWh: number | null;
  coveragePct: number;
  inputFingerprint: string;
  calculatedAt: string;
  comparable: boolean;
}

export interface LossAssessment {
  id: string;
  organizationId: string;
  modelRunId: string;
  totalLossWh: number;
  lossPct: number | null;
  methodology: string;
  assessedAt: string;
  validationStatus: string;
}

export interface LossCauseEstimate {
  id: string;
  organizationId: string;
  lossAssessmentId: string;
  cause: LossCause;
  estimatedWh: number;
  method: string;
  evidenceNote: string;
}

export interface TariffPlan {
  id: string;
  organizationId: string;
  installationId: string;
  name: string;
  currencyCode: string;
  distributor: string;
}

export interface TariffRate {
  id: string;
  organizationId: string;
  tariffPlanId: string;
  validFrom: string;
  validTo: string | null;
  purchasePricePerKwh: number;
  exportPricePerKwh: number;
}

export interface EconomicAssessment {
  id: string;
  organizationId: string;
  lossAssessmentId: string;
  tariffRateId: string | null;
  assessedAt: string;
  currencyCode: string;
  selfConsumptionLostWh: number;
  exportedLostWh: number;
  estimatedLossAmount: number;
  tariffSnapshotNote: string;
}

export interface AuditTemplate {
  id: string;
  organizationId: string;
  title: string;
  templateVersion: string;
  active: boolean;
  publishedAt: string | null;
}

export interface AuditQuestion {
  id: string;
  organizationId: string;
  auditTemplateId: string;
  questionCode: string;
  label: string;
  answerType: string;
  sortOrder: number;
  mandatory: boolean;
}

export interface AuditReport {
  id: string;
  organizationId: string;
  installationId: string;
  auditTemplateId: string;
  auditorMembershipId: string | null;
  status: AuditStatus;
  startedAt: string;
  completedAt: string | null;
  summary: string;
}

export interface AuditAnswer {
  id: string;
  organizationId: string;
  auditReportId: string;
  auditQuestionId: string;
  answerText: string;
  answeredAt: string | null;
  compliant: boolean | null;
}

export interface AuditFinding {
  id: string;
  organizationId: string;
  auditReportId: string;
  title: string;
  severity: FindingSeverity;
  description: string;
  recommendation: string;
  resolvedAt: string | null;
}

export interface AuditEvidence {
  id: string;
  organizationId: string;
  findingId: string;
  storageKey: string;
  mimeType: string;
  sha256: string;
  capturedAt: string;
}

export interface MaintenancePlan {
  id: string;
  organizationId: string;
  name: string;
  status: MaintenancePlanStatus;
}

export interface MaintenancePlanVersion {
  id: string;
  organizationId: string;
  maintenancePlanId: string;
  versionCode: string;
  publishedAt: string | null;
  currencyCode: string;
  recurringAmount: number;
  termsReference: string;
}

export interface ServiceAgreement {
  id: string;
  organizationId: string;
  providerOrganizationId: string;
  installationId: string;
  status: ServiceAgreementStatus;
  startsAt: string;
  endsAt: string | null;
  approvedAt: string | null;
}

export interface PlanSubscription {
  id: string;
  organizationId: string;
  installationId: string;
  planVersionId: string;
  serviceAgreementId: string;
  status: SubscriptionStatus;
  activatedAt: string | null;
  expiresAt: string | null;
  agreedAmount: number;
  currencyCode: string;
}

export interface ServiceProposal {
  id: string;
  organizationId: string;
  installationId: string;
  planVersionId: string | null;
  referenceCode: string;
  proposedAt: string;
  validUntil: string;
  amount: number;
  currencyCode: string;
  acceptedAt: string | null;
}

export interface WorkOrder {
  id: string;
  organizationId: string;
  installationId: string;
  incidentId: string | null;
  subscriptionId: string | null;
  assignedMembershipId: string | null;
  referenceCode: string;
  title: string;
  status: WorkOrderStatus;
  priority: AlertSeverity;
  openedAt: string;
  scheduledAt: string | null;
  closedAt: string | null;
}

export interface WorkOrderTask {
  id: string;
  organizationId: string;
  workOrderId: string;
  description: string;
  status: WorkTaskStatus;
  sortOrder: number;
}

export interface MaintenanceIntervention {
  id: string;
  organizationId: string;
  workOrderId: string;
  type: InterventionType;
  startedAt: string | null;
  finishedAt: string | null;
  workCost: number | null;
  currencyCode: string;
  notes: string;
}

export interface InterventionEvidence {
  id: string;
  organizationId: string;
  interventionId: string;
  storageKey: string;
  mimeType: string;
  sha256: string;
  capturedAt: string;
}

export interface RecoveryAssessment {
  id: string;
  organizationId: string;
  interventionId: string;
  modelRunId: string | null;
  assessedAt: string;
  baselineMethod: string;
  expectedRecoveredWh: number | null;
  verifiedRecoveredWh: number | null;
  confidenceNote: string;
}

export interface DeviceIdentity {
  id: string;
  organizationId: string;
  gatewayId: string | null;
  equipmentId: string | null;
  deviceUid: string;
  keyFingerprint: string;
  protocolName: string;
  trustStatus: DeviceTrustStatus;
  keyRotatedAt: string | null;
  validUntil: string | null;
}

export interface TelemetryIntegrityEvent {
  id: string;
  organizationId: string;
  installationId: string;
  deviceIdentityId: string | null;
  telemetryChannelId: string | null;
  eventUid: string;
  eventType: IntegrityEventType;
  status: IntegrityEventStatus;
  observedAt: string;
  sourceObservedAt: string;
  sequenceNumber: number | null;
  bootId: string | null;
  payloadSha256: string;
  detail: string;
}

export interface IntegrityAssessment {
  id: string;
  organizationId: string;
  installationId: string;
  integrityEventId: string;
  modelRunId: string | null;
  assessedAt: string;
  trustLevel: DataTrustLevel;
  ruleVersion: string;
  reason: string;
}

export interface EvidenceSource {
  id: string;
  code: string;
  providerName: string;
  kind: EvidenceSourceKind;
  spatialResolutionM: number | null;
  licenseReference: string;
}

export interface DataProvenance {
  id: string;
  organizationId: string;
  installationId: string;
  evidenceSourceId: string;
  modelRunId: string | null;
  sourceRecordKey: string;
  observedAt: string;
  ingestedAt: string;
  contentSha256: string;
  trustLevel: DataTrustLevel;
  qualityFlags: string;
  algorithmVersion: string;
}

export interface SecurityIncident {
  id: string;
  organizationId: string;
  installationId: string;
  integrityAssessmentId: string | null;
  referenceCode: string;
  title: string;
  status: SecurityCaseStatus;
  openedAt: string;
  closedAt: string | null;
  resolutionNote: string;
}

export interface DroneAsset {
  id: string;
  organizationId: string;
  assetCode: string;
  modelName: string;
  status: DroneAssetStatus;
  maximumWindMs: number;
}

export interface DroneMission {
  id: string;
  organizationId: string;
  installationId: string;
  workOrderId: string | null;
  droneAssetId: string | null;
  providerOrganizationId: string | null;
  missionCode: string;
  kind: DroneMissionKind;
  status: DroneMissionStatus;
  proposedAt: string;
  startedAt: string | null;
  completedAt: string | null;
  safetyChecklistSummary: string;
}

export interface MissionAuthorization {
  id: string;
  organizationId: string;
  missionId: string;
  authorizedByMembershipId: string | null;
  status: MissionAuthorizationStatus;
  decidedAt: string | null;
  expiresAt: string | null;
  operatorReference: string;
  decisionNote: string;
}

export interface VerificationResult {
  id: string;
  organizationId: string;
  installationId: string;
  droneMissionId: string | null;
  recoveryAssessmentId: string | null;
  status: VerificationStatus;
  assessedAt: string;
  baselineMethod: string;
  recoveredWh: number | null;
  evidenceSummary: string;
}

/** Serie simulada. No es entidad JDL: el borrador deja la telemetría cruda fuera del CRUD. */
export interface TelemetrySample {
  id: string;
  organizationId: string;
  installationId: string;
  gatewayId: string;
  deviceId: string;
  eventId: string;
  bootId: string;
  sequenceNumber: number;
  powerW: number;
  observedAt: string;
  accepted: boolean;
  decision: 'ACCEPTED' | 'DUPLICATE_EVENT' | 'REPLAY' | 'PHYSICAL_INCONSISTENCY';
  legitimateReboot: boolean;
}

export interface OutboxItem {
  id: string;
  gatewayId: string;
  eventId: string;
  queuedAt: string;
  syncedAt: string | null;
}

export interface DemoState {
  version: 4;
  seed: 'helioguard-hackaton-v4';
  savedAt: string;
  activeUserId: string;
  activeOrganizationId: string;
  selectedInstallationId: string;
  offline: boolean;
  scenario: ScenarioId;
  recoveryApplied: boolean;
  clockIso: string;
  notice: string | null;
  seq: number;
  organizations: Organization[];
  users: DemoUser[];
  memberships: OrganizationMembership[];
  sites: Site[];
  installations: Installation[];
  grants: InstallationAccessGrant[];
  equipmentModels: EquipmentModel[];
  arrays: SolarArray[];
  strings: SolarString[];
  equipment: Equipment[];
  gateways: Gateway[];
  metrics: MetricDefinition[];
  channels: TelemetryChannel[];
  alertRules: AlertRule[];
  incidents: Incident[];
  incidentEvents: IncidentEvent[];
  modelRuns: EnergyModelRun[];
  lossAssessments: LossAssessment[];
  lossCauses: LossCauseEstimate[];
  tariffPlans: TariffPlan[];
  tariffRates: TariffRate[];
  economicAssessments: EconomicAssessment[];
  auditTemplates: AuditTemplate[];
  auditQuestions: AuditQuestion[];
  auditReports: AuditReport[];
  auditAnswers: AuditAnswer[];
  auditFindings: AuditFinding[];
  auditEvidence: AuditEvidence[];
  maintenancePlans: MaintenancePlan[];
  planVersions: MaintenancePlanVersion[];
  agreements: ServiceAgreement[];
  subscriptions: PlanSubscription[];
  proposals: ServiceProposal[];
  workOrders: WorkOrder[];
  tasks: WorkOrderTask[];
  interventions: MaintenanceIntervention[];
  interventionEvidence: InterventionEvidence[];
  recoveries: RecoveryAssessment[];
  devices: DeviceIdentity[];
  integrityEvents: TelemetryIntegrityEvent[];
  integrityAssessments: IntegrityAssessment[];
  evidenceSources: EvidenceSource[];
  provenance: DataProvenance[];
  securityIncidents: SecurityIncident[];
  drones: DroneAsset[];
  missions: DroneMission[];
  authorizations: MissionAuthorization[];
  verifications: VerificationResult[];
  samples: TelemetrySample[];
  outbox: OutboxItem[];
}
