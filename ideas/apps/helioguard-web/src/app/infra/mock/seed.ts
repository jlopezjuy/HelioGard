import type { DemoState } from '../../core/domain/model';

const NOW = '2026-11-22T15:00:00.000Z';
const YDAY_START = '2026-11-21T13:00:00.000Z';
const YDAY_END = '2026-11-21T23:00:00.000Z';

export function buildSeed(): DemoState {
  return {
    version: 4,
    seed: 'helioguard-hackaton-v4',
    savedAt: NOW,
    activeUserId: 'usr-owner',
    activeOrganizationId: 'org-jujuy',
    selectedInstallationId: 'ins-puna',
    offline: false,
    scenario: 'normal',
    recoveryApplied: false,
    clockIso: NOW,
    notice: null,
    seq: 100,
    organizations: [
      { id: 'org-jujuy', name: 'Andes Solar Jujuy', slug: 'andes-solar', status: 'ACTIVE', kind: 'SERVICE_PROVIDER', createdAt: '2024-03-01T12:00:00.000Z' },
      { id: 'org-prop', name: 'Familia Andina', slug: 'familia-andina', status: 'ACTIVE', kind: 'HOUSEHOLD', createdAt: '2025-01-15T12:00:00.000Z' },
      { id: 'org-salta', name: 'Sol del Norte Salta', slug: 'sol-norte', status: 'ACTIVE', kind: 'BUSINESS', createdAt: '2023-08-20T12:00:00.000Z' },
    ],
    users: [
      { id: 'usr-owner', name: 'Operadora Demo', login: 'operadora.demo' },
      { id: 'usr-tech', name: 'Técnico Demo', login: 'tecnico.demo' },
      { id: 'usr-home', name: 'Propietaria Demo', login: 'propietaria.demo' },
      { id: 'usr-salta', name: 'Administradora Salta', login: 'admin.salta' },
      { id: 'usr-auditor', name: 'Auditor Demo', login: 'auditor.demo' },
    ],
    memberships: [
      { id: 'mem-owner', organizationId: 'org-jujuy', userId: 'usr-owner', role: 'OWNER', status: 'ACTIVE', invitedAt: '2024-03-01T12:00:00.000Z', activatedAt: '2024-03-01T12:10:00.000Z' },
      { id: 'mem-tech', organizationId: 'org-jujuy', userId: 'usr-tech', role: 'TECHNICIAN', status: 'ACTIVE', invitedAt: '2024-06-01T12:00:00.000Z', activatedAt: '2024-06-02T12:00:00.000Z' },
      { id: 'mem-auditor', organizationId: 'org-jujuy', userId: 'usr-auditor', role: 'AUDITOR', status: 'ACTIVE', invitedAt: '2025-02-01T12:00:00.000Z', activatedAt: '2025-02-01T13:00:00.000Z' },
      { id: 'mem-home', organizationId: 'org-prop', userId: 'usr-home', role: 'OWNER', status: 'ACTIVE', invitedAt: '2025-01-15T12:00:00.000Z', activatedAt: '2025-01-15T12:20:00.000Z' },
      { id: 'mem-salta', organizationId: 'org-salta', userId: 'usr-salta', role: 'OWNER', status: 'ACTIVE', invitedAt: '2023-08-20T12:00:00.000Z', activatedAt: '2023-08-20T12:30:00.000Z' },
    ],
    sites: [
      { id: 'site-abra', organizationId: 'org-jujuy', name: 'Planta Puna Demo', address: 'Abra Pampa, Jujuy', latitude: -22.72, longitude: -65.7, altitudeM: 3450, timeZone: 'America/Argentina/Jujuy', climate: 'Puna', roof: false },
      { id: 'site-casa', organizationId: 'org-prop', name: 'Casa Andina Demo', address: 'San Salvador de Jujuy', latitude: -24.19, longitude: -65.3, altitudeM: 1260, timeZone: 'America/Argentina/Jujuy', climate: 'Valles', roof: true },
      { id: 'site-cafayate', organizationId: 'org-salta', name: 'Parque Calchaquí Demo', address: 'Cafayate, Salta', latitude: -26.07, longitude: -65.98, altitudeM: 1683, timeZone: 'America/Argentina/Salta', climate: 'Valles Calchaquíes', roof: false },
    ],
    installations: [
      { id: 'ins-puna', organizationId: 'org-jujuy', siteId: 'site-abra', code: 'PN-12000', name: 'Puna Norte', gridMode: 'GRID_TIED', status: 'ACTIVE', nominalDcKw: 12000, commissionedOn: '2022-09-01' },
      { id: 'ins-casa', organizationId: 'org-prop', siteId: 'site-casa', code: 'CA-3K', name: 'Casa Andina', gridMode: 'HYBRID', status: 'ACTIVE', nominalDcKw: 3.3, commissionedOn: '2025-02-11' },
      { id: 'ins-salta', organizationId: 'org-salta', siteId: 'site-cafayate', code: 'CO-750', name: 'Calchaquí Oeste', gridMode: 'GRID_TIED', status: 'ACTIVE', nominalDcKw: 750, commissionedOn: '2021-11-03' },
    ],
    grants: [
      { id: 'grant-casa', organizationId: 'org-prop', installationId: 'ins-casa', granteeOrganizationId: 'org-jujuy', scope: 'MAINTENANCE', validFrom: '2026-11-01T00:00:00.000Z', validUntil: '2027-11-01T00:00:00.000Z', revokedAt: null, reason: 'Mantenimiento puntual del híbrido. Sin acceso a tarifas ni a integridad.' },
    ],
    equipmentModels: [
      { id: 'em-mod', manufacturer: 'Catálogo demo', modelCode: 'MOD-550', revision: 'A', kind: 'MODULE', nominalPowerW: 550 },
      { id: 'em-inv', manufacturer: 'Catálogo demo', modelCode: 'INV-250', revision: 'B', kind: 'INVERTER', nominalPowerW: 250000 },
      { id: 'em-esp', manufacturer: 'Catálogo demo', modelCode: 'ESP32-EDGE', revision: '1', kind: 'SENSOR', nominalPowerW: null },
      { id: 'em-met', manufacturer: 'Catálogo demo', modelCode: 'MET-01', revision: '1', kind: 'METER', nominalPowerW: null },
      { id: 'em-bat', manufacturer: 'Catálogo demo', modelCode: 'BAT-5', revision: '1', kind: 'BATTERY', nominalPowerW: 5000 },
    ],
    arrays: [
      { id: 'arr-a1', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Bloque A1 Norte', blockCode: 'A1', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2022-09-01' },
      { id: 'arr-a2', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Bloque A2 Este', blockCode: 'A2', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2022-09-01' },
      { id: 'arr-a3', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Bloque A3 Sur', blockCode: 'A3', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2022-09-01' },
      { id: 'arr-a4', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Bloque A4 Oeste', blockCode: 'A4', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2022-09-01' },
      { id: 'arr-casa', organizationId: 'org-prop', installationId: 'ins-casa', name: 'Techo norte', blockCode: null, azimuthDegrees: 10, tiltDegrees: 20, commissionedOn: '2025-02-11' },
      { id: 'arr-salta', organizationId: 'org-salta', installationId: 'ins-salta', name: 'Mesa oeste', blockCode: null, azimuthDegrees: 350, tiltDegrees: 18, commissionedOn: '2021-11-03' },
    ],
    strings: [
      { id: 'str-a1', organizationId: 'org-jujuy', solarArrayId: 'arr-a1', moduleModelId: 'em-mod', stringCode: 'A1-S1', modulesInSeries: 20, active: true },
      { id: 'str-a2', organizationId: 'org-jujuy', solarArrayId: 'arr-a2', moduleModelId: 'em-mod', stringCode: 'A2-S1', modulesInSeries: 20, active: true },
      { id: 'str-a3', organizationId: 'org-jujuy', solarArrayId: 'arr-a3', moduleModelId: 'em-mod', stringCode: 'A3-S1', modulesInSeries: 20, active: true },
      { id: 'str-a4', organizationId: 'org-jujuy', solarArrayId: 'arr-a4', moduleModelId: 'em-mod', stringCode: 'A4-S1', modulesInSeries: 20, active: true },
      { id: 'str-casa', organizationId: 'org-prop', solarArrayId: 'arr-casa', moduleModelId: 'em-mod', stringCode: 'CASA-S1', modulesInSeries: 6, active: true },
      { id: 'str-salta', organizationId: 'org-salta', solarArrayId: 'arr-salta', moduleModelId: 'em-mod', stringCode: 'SAL-S1', modulesInSeries: 18, active: true },
    ],
    equipment: [
      { id: 'eq-inv-01', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-inv', solarStringId: 'str-a1', tag: 'INV-01', serialNumber: 'DEMO-INV-01', kind: 'INVERTER', status: 'ACTIVE', installedOn: '2022-09-01', removedOn: null },
      { id: 'eq-inv-03', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-inv', solarStringId: 'str-a2', tag: 'INV-03', serialNumber: 'DEMO-INV-03', kind: 'INVERTER', status: 'ACTIVE', installedOn: '2022-09-01', removedOn: null },
      { id: 'eq-esp', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-esp', solarStringId: null, tag: 'ESP-PUNA', serialNumber: 'DEMO-ESP-01', kind: 'SENSOR', status: 'ACTIVE', installedOn: '2024-01-10', removedOn: null },
      { id: 'eq-meter', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-met', solarStringId: null, tag: 'MED-PUNA', serialNumber: 'DEMO-M-01', kind: 'METER', status: 'ACTIVE', installedOn: '2022-09-01', removedOn: null },
      { id: 'eq-bat', organizationId: 'org-prop', installationId: 'ins-casa', equipmentModelId: 'em-bat', solarStringId: null, tag: 'BAT-CASA', serialNumber: 'DEMO-B-01', kind: 'BATTERY', status: 'ACTIVE', installedOn: '2025-02-11', removedOn: null },
      { id: 'eq-inv-salta', organizationId: 'org-salta', installationId: 'ins-salta', equipmentModelId: 'em-inv', solarStringId: 'str-salta', tag: 'INV-A3', serialNumber: 'DEMO-INV-A3', kind: 'INVERTER', status: 'FAILED', installedOn: '2021-11-03', removedOn: null },
    ],
    gateways: [
      { id: 'gw-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', hardwareId: 'raspi-demo-01', firmwareVersion: 'edge-0.4-demo', status: 'ONLINE', lastSeenAt: NOW, provisionedAt: '2024-01-10T12:00:00.000Z' },
      { id: 'gw-casa', organizationId: 'org-prop', installationId: 'ins-casa', hardwareId: 'raspi-demo-02', firmwareVersion: 'edge-0.4-demo', status: 'ONLINE', lastSeenAt: NOW, provisionedAt: '2025-02-11T12:00:00.000Z' },
      { id: 'gw-salta', organizationId: 'org-salta', installationId: 'ins-salta', hardwareId: 'raspi-demo-03', firmwareVersion: 'edge-0.3-demo', status: 'ONLINE', lastSeenAt: NOW, provisionedAt: '2023-01-01T12:00:00.000Z' },
    ],
    metrics: [
      { id: 'metric-power', code: 'AC_POWER', unitCode: 'W', description: 'Potencia activa de salida', counter: false },
      { id: 'metric-energy', code: 'ENERGY_COUNTER', unitCode: 'Wh', description: 'Contador de energía', counter: true },
      { id: 'metric-irr', code: 'GHI_ESTIMATED', unitCode: 'W/m2', description: 'Irradiancia estimada por modelo meteorológico', counter: false },
    ],
    channels: [
      { id: 'ch-inv3', organizationId: 'org-jujuy', equipmentId: 'eq-inv-03', metricDefinitionId: 'metric-power', externalChannelCode: 'inv3.ac.power', enabled: true, samplePeriodSeconds: 60 },
      { id: 'ch-irr', organizationId: 'org-jujuy', equipmentId: 'eq-esp', metricDefinitionId: 'metric-irr', externalChannelCode: 'edge.ghi.sim', enabled: true, samplePeriodSeconds: 900 },
    ],
    alertRules: [
      { id: 'rule-deficit', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Déficit sectorizado', ruleCode: 'DEFICIT_BLOCK', severity: 'MEDIUM', enabled: true, thresholdValue: 0.15, minPersistenceSeconds: 900 },
      { id: 'rule-bms', organizationId: 'org-prop', installationId: 'ins-casa', name: 'BMS por frío', ruleCode: 'BMS_COLD', severity: 'HIGH', enabled: true, thresholdValue: 0, minPersistenceSeconds: 600 },
      { id: 'rule-hw', organizationId: 'org-salta', installationId: 'ins-salta', name: 'Inversor sin entrega', ruleCode: 'INV_DOWN', severity: 'HIGH', enabled: true, thresholdValue: 0, minPersistenceSeconds: 300 },
    ],
    incidents: [
      { id: 'inc-casa', organizationId: 'org-prop', installationId: 'ins-casa', alertRuleId: 'rule-bms', title: 'Carga BMS restringida por frío', severity: 'HIGH', status: 'ACKNOWLEDGED', firstSeenAt: '2026-11-22T12:20:00.000Z', lastSeenAt: NOW, acknowledgedAt: '2026-11-22T12:40:00.000Z', resolvedAt: null, dedupKey: 'bms-casa-2026-11-22' },
      { id: 'inc-salta', organizationId: 'org-salta', installationId: 'ins-salta', alertRuleId: 'rule-hw', title: 'Falla inversor A3', severity: 'HIGH', status: 'OPEN', firstSeenAt: '2026-11-22T15:15:00.000Z', lastSeenAt: NOW, acknowledgedAt: null, resolvedAt: null, dedupKey: 'inv-a3-2026-11-22' },
    ],
    incidentEvents: [
      { id: 'iev-casa', organizationId: 'org-prop', incidentId: 'inc-casa', occurredAt: '2026-11-22T12:20:00.000Z', eventCode: 'BMS_LIMIT', detail: 'El híbrido limita carga bajo cero. No es suciedad ni un ataque.' },
      { id: 'iev-salta', organizationId: 'org-salta', incidentId: 'inc-salta', occurredAt: '2026-11-22T15:15:00.000Z', eventCode: 'INVERTER_ZERO', detail: 'INV-A3 sin potencia. La acción es técnica, no una limpieza.' },
    ],
    modelRuns: [
      { id: 'run-puna-yday', organizationId: 'org-jujuy', installationId: 'ins-puna', modelName: 'helioguard-clearsky-demo', modelVersion: '0.4', periodStart: YDAY_START, periodEnd: YDAY_END, expectedWh: 49_000_000, actualWh: 44_700_000, coveragePct: 0.92, inputFingerprint: 'sim-weather-yday', calculatedAt: '2026-11-22T02:00:00.000Z', comparable: true },
      { id: 'run-casa-yday', organizationId: 'org-prop', installationId: 'ins-casa', modelName: 'helioguard-clearsky-demo', modelVersion: '0.4', periodStart: YDAY_START, periodEnd: YDAY_END, expectedWh: 13_200, actualWh: 10_900, coveragePct: 0.88, inputFingerprint: 'sim-weather-yday', calculatedAt: '2026-11-22T02:00:00.000Z', comparable: true },
      { id: 'run-salta-yday', organizationId: 'org-salta', installationId: 'ins-salta', modelName: 'helioguard-clearsky-demo', modelVersion: '0.4', periodStart: YDAY_START, periodEnd: YDAY_END, expectedWh: 2_850_000, actualWh: 2_480_000, coveragePct: 0.9, inputFingerprint: 'sim-weather-yday', calculatedAt: '2026-11-22T02:00:00.000Z', comparable: true },
    ],
    lossAssessments: [
      { id: 'loss-puna-yday', organizationId: 'org-jujuy', modelRunId: 'run-puna-yday', totalLossWh: 4_300_000, lossPct: 8.8, methodology: 'intervalo-comparable-simulado', assessedAt: '2026-11-22T02:05:00.000Z', validationStatus: 'PRELIMINAR' },
      { id: 'loss-casa-yday', organizationId: 'org-prop', modelRunId: 'run-casa-yday', totalLossWh: 2_300, lossPct: 17.4, methodology: 'intervalo-comparable-simulado', assessedAt: '2026-11-22T02:05:00.000Z', validationStatus: 'PRELIMINAR' },
      { id: 'loss-salta-yday', organizationId: 'org-salta', modelRunId: 'run-salta-yday', totalLossWh: 370_000, lossPct: 13, methodology: 'intervalo-comparable-simulado', assessedAt: '2026-11-22T02:05:00.000Z', validationStatus: 'PRELIMINAR' },
    ],
    lossCauses: [
      { id: 'cause-puna-u', organizationId: 'org-jujuy', lossAssessmentId: 'loss-puna-yday', cause: 'UNKNOWN', estimatedWh: 4_300_000, method: 'sin-inspeccion', evidenceNote: 'Ayer no hay evidencia para repartir la causa. No se afirma suciedad.' },
      { id: 'cause-casa-b', organizationId: 'org-prop', lossAssessmentId: 'loss-casa-yday', cause: 'BATTERY_RESTRICTION', estimatedWh: 900, method: 'bms', evidenceNote: 'Parte del hueco coincide con el límite de carga. El resto queda sin atribuir.' },
      { id: 'cause-casa-u', organizationId: 'org-prop', lossAssessmentId: 'loss-casa-yday', cause: 'UNKNOWN', estimatedWh: 1_400, method: 'residual', evidenceNote: 'Residual sin medición de irradiancia local.' },
      { id: 'cause-salta-h', organizationId: 'org-salta', lossAssessmentId: 'loss-salta-yday', cause: 'HARDWARE', estimatedWh: 370_000, method: 'inversor-caido', evidenceNote: 'INV-A3 sin entrega. No proponer lavado.' },
    ],
    tariffPlans: [
      { id: 'tar-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Tarifa demo Puna', currencyCode: 'ARS', distributor: 'Distribuidora ficticia' },
      { id: 'tar-casa', organizationId: 'org-prop', installationId: 'ins-casa', name: 'Tarifa demo casa', currencyCode: 'ARS', distributor: 'Distribuidora ficticia' },
      { id: 'tar-salta', organizationId: 'org-salta', installationId: 'ins-salta', name: 'Tarifa demo Calchaquí', currencyCode: 'ARS', distributor: 'Distribuidora ficticia' },
    ],
    tariffRates: [
      { id: 'rate-puna', organizationId: 'org-jujuy', tariffPlanId: 'tar-puna', validFrom: '2026-01-01T03:00:00.000Z', validTo: null, purchasePricePerKwh: 200, exportPricePerKwh: 80 },
      { id: 'rate-casa', organizationId: 'org-prop', tariffPlanId: 'tar-casa', validFrom: '2026-01-01T03:00:00.000Z', validTo: null, purchasePricePerKwh: 180, exportPricePerKwh: 70 },
      { id: 'rate-salta', organizationId: 'org-salta', tariffPlanId: 'tar-salta', validFrom: '2026-01-01T03:00:00.000Z', validTo: null, purchasePricePerKwh: 190, exportPricePerKwh: 75 },
    ],
    economicAssessments: [
      { id: 'eco-puna-yday', organizationId: 'org-jujuy', lossAssessmentId: 'loss-puna-yday', tariffRateId: 'rate-puna', assessedAt: '2026-11-22T02:10:00.000Z', currencyCode: 'ARS', selfConsumptionLostWh: 3_010_000, exportedLostWh: 1_290_000, estimatedLossAmount: 705_200, tariffSnapshotNote: 'Cifra de demostración. 70/30 compra evitada e inyección. No es una factura.' },
    ],
    auditTemplates: [
      { id: 'tpl-campo', organizationId: 'org-jujuy', title: 'Inspección de campo Puna', templateVersion: '1.0', active: true, publishedAt: '2026-10-01T12:00:00.000Z' },
    ],
    auditQuestions: [
      { id: 'q1', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'MOD-VIS', label: '¿Hay depósito visible uniforme en el bloque señalado?', answerType: 'BOOLEAN', sortOrder: 1, mandatory: true },
      { id: 'q2', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'INV-ALM', label: '¿El inversor del string presenta alarma de hardware?', answerType: 'BOOLEAN', sortOrder: 2, mandatory: true },
      { id: 'q3', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'CLI-SEG', label: '¿El método de limpieza está permitido por el fabricante y el clima?', answerType: 'BOOLEAN', sortOrder: 3, mandatory: true },
      { id: 'q4', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'NOTA', label: 'Nota de campo', answerType: 'TEXT', sortOrder: 4, mandatory: false },
    ],
    auditReports: [
      { id: 'rep-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', auditTemplateId: 'tpl-campo', auditorMembershipId: 'mem-auditor', status: 'IN_PROGRESS', startedAt: '2026-11-22T14:00:00.000Z', completedAt: null, summary: 'Recorrido abierto. Todavía sin cierre.' },
    ],
    auditAnswers: [
      { id: 'ans-1', organizationId: 'org-jujuy', auditReportId: 'rep-puna', auditQuestionId: 'q1', answerText: '', answeredAt: null, compliant: null },
      { id: 'ans-2', organizationId: 'org-jujuy', auditReportId: 'rep-puna', auditQuestionId: 'q2', answerText: '', answeredAt: null, compliant: null },
      { id: 'ans-3', organizationId: 'org-jujuy', auditReportId: 'rep-puna', auditQuestionId: 'q3', answerText: '', answeredAt: null, compliant: null },
      { id: 'ans-4', organizationId: 'org-jujuy', auditReportId: 'rep-puna', auditQuestionId: 'q4', answerText: '', answeredAt: null, compliant: null },
    ],
    auditFindings: [],
    auditEvidence: [],
    maintenancePlans: [
      { id: 'plan-puna', organizationId: 'org-jujuy', name: 'Cobertura de inspección Puna', status: 'ACTIVE' },
    ],
    planVersions: [
      { id: 'plan-v1', organizationId: 'org-jujuy', maintenancePlanId: 'plan-puna', versionCode: '2026.1', publishedAt: '2026-08-01T12:00:00.000Z', currencyCode: 'ARS', recurringAmount: 1_800_000, termsReference: 'Incluye inspección visual trimestral simulada. Excluye granizo, reemplazo de inversores y cualquier operación sin operador.' },
      { id: 'plan-v2', organizationId: 'org-jujuy', maintenancePlanId: 'plan-puna', versionCode: '2026.2-draft', publishedAt: null, currencyCode: 'ARS', recurringAmount: 2_100_000, termsReference: 'Borrador. Suma una inspección térmica con dron, todavía sin publicar.' },
    ],
    agreements: [
      { id: 'agr-puna', organizationId: 'org-jujuy', providerOrganizationId: 'org-jujuy', installationId: 'ins-puna', status: 'ACTIVE', startsAt: '2026-08-01T12:00:00.000Z', endsAt: '2027-08-01T12:00:00.000Z', approvedAt: '2026-08-01T12:00:00.000Z' },
    ],
    subscriptions: [
      { id: 'sub-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', planVersionId: 'plan-v1', serviceAgreementId: 'agr-puna', status: 'ACTIVE', activatedAt: '2026-08-01T12:00:00.000Z', expiresAt: '2027-08-01T12:00:00.000Z', agreedAmount: 1_800_000, currencyCode: 'ARS' },
    ],
    proposals: [
      { id: 'prop-v2', organizationId: 'org-jujuy', installationId: 'ins-puna', planVersionId: 'plan-v2', referenceCode: 'PROP-PUNA-2026-2', proposedAt: '2026-11-01T12:00:00.000Z', validUntil: '2026-12-15T12:00:00.000Z', amount: 2_100_000, currencyCode: 'ARS', acceptedAt: null },
    ],
    workOrders: [
      { id: 'wo-trimestral', organizationId: 'org-jujuy', installationId: 'ins-puna', incidentId: null, subscriptionId: 'sub-puna', assignedMembershipId: 'mem-tech', referenceCode: 'OT-TRIM-01', title: 'Inspección trimestral de rutina', status: 'ASSIGNED', priority: 'LOW', openedAt: '2026-11-10T12:00:00.000Z', scheduledAt: '2026-11-25T12:00:00.000Z', closedAt: null },
    ],
    tasks: [
      { id: 'task-1', organizationId: 'org-jujuy', workOrderId: 'wo-trimestral', description: 'Recorrer bloques y anotar depósito visible', status: 'TODO', sortOrder: 1 },
      { id: 'task-2', organizationId: 'org-jujuy', workOrderId: 'wo-trimestral', description: 'Leer alarmas de INV-01 e INV-03', status: 'TODO', sortOrder: 2 },
    ],
    interventions: [],
    interventionEvidence: [],
    recoveries: [],
    devices: [
      { id: 'dev-inv3', organizationId: 'org-jujuy', gatewayId: 'gw-puna', equipmentId: 'eq-inv-03', deviceUid: 'inverter-03', keyFingerprint: 'demo-only-not-a-key', protocolName: 'mqtt-demo', trustStatus: 'ACTIVE', keyRotatedAt: '2026-06-01T12:00:00.000Z', validUntil: null },
    ],
    integrityEvents: [
      { id: 'iev-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', deviceIdentityId: 'dev-inv3', telemetryChannelId: 'ch-inv3', eventUid: 'hist-dup-1', eventType: 'DUPLICATE_EVENT', status: 'EXPLAINED', observedAt: '2026-11-20T16:00:00.000Z', sourceObservedAt: '2026-11-20T16:00:00.000Z', sequenceNumber: 900, bootId: 'boot-old', payloadSha256: 'demo-fnv-hist', detail: 'Reintento de transporte con el mismo eventId. No se insertó de nuevo y no se trató como ataque.' },
    ],
    integrityAssessments: [
      { id: 'ias-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', integrityEventId: 'iev-hist', modelRunId: null, assessedAt: '2026-11-20T16:01:00.000Z', trustLevel: 'VALIDATED', ruleVersion: 'dedup-1', reason: 'Idempotencia por gatewayId+eventId. La medición original se conservó una sola vez.' },
    ],
    evidenceSources: [
      { id: 'src-weather', code: 'weather-sim', providerName: 'Modelo meteorológico simulado', kind: 'SATELLITE_WEATHER', spatialResolutionM: 2000, licenseReference: 'Dato sintético de la hackatón. No es Sentinel-2 ni GOES.' },
      { id: 'src-inv', code: 'inverter-demo', providerName: 'Telemetría de inversor simulada', kind: 'INVERTER', spatialResolutionM: null, licenseReference: 'Generada en el navegador.' },
      { id: 'src-drone', code: 'drone-demo', providerName: 'Inspección de dron simulada', kind: 'DRONE', spatialResolutionM: 0.05, licenseReference: 'No hay vuelo real ni termografía capturada.' },
    ],
    provenance: [
      { id: 'prov-weather', organizationId: 'org-jujuy', installationId: 'ins-puna', evidenceSourceId: 'src-weather', modelRunId: 'run-puna-yday', sourceRecordKey: 'sim://weather/puna/2026-11-21', observedAt: YDAY_START, ingestedAt: '2026-11-22T02:00:00.000Z', contentSha256: 'demo-fnv-weather', trustLevel: 'ESTIMATED', qualityFlags: 'SIMULATED,NO_PANEL_THERMAL', algorithmVersion: '0.4' },
      { id: 'prov-inv', organizationId: 'org-jujuy', installationId: 'ins-puna', evidenceSourceId: 'src-inv', modelRunId: 'run-puna-yday', sourceRecordKey: 'sim://inverter/INV-03/2026-11-21', observedAt: YDAY_END, ingestedAt: '2026-11-22T02:00:00.000Z', contentSha256: 'demo-fnv-inv', trustLevel: 'ESTIMATED', qualityFlags: 'SIMULATED', algorithmVersion: '0.4' },
    ],
    securityIncidents: [],
    drones: [
      { id: 'drone-01', organizationId: 'org-jujuy', assetCode: 'DR-DEMO-1', modelName: 'Equipo de inspección demo', status: 'READY', maximumWindMs: 12 },
    ],
    missions: [],
    authorizations: [],
    verifications: [],
    samples: [],
    outbox: [],
  };
}
