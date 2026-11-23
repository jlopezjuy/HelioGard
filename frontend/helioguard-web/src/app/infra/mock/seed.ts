import { recomputeSector } from '../../core/domain/actions';
import type { DemoSnapshot } from '../../core/domain/model';

const T0 = '2026-11-22T15:00:00.000Z';

export function createSeed(): DemoSnapshot {
  const snapshot: DemoSnapshot = {
    meta: {
      version: 'v4',
      seed: 'helioguard-hackaton-v4',
      currency: 'ARS',
      timeZone: 'America/Argentina/Jujuy',
    },
    clockIso: T0,
    session: { userId: 'usr-owner', organizationId: 'org-jujuy' },
    scenario: {
      soiling: false,
      lossScenario: 'NONE',
      wanOffline: false,
      windBlocked: false,
      frostBlocked: false,
      roofWorkBlocked: false,
      manufacturerOk: true,
      operatorOk: true,
      siteOk: true,
      droneProgress: 0,
      readFailure: false,
      syncCount: 0,
      selectedArrayId: 'arr-b2',
      notice: 'Planta en régimen simulado. Los datos no provienen de inversores reales.',
      edgeBuffer: [],
      ledger: [],
      lastProof: null,
    },
    users: [
      { id: 'usr-owner', name: 'Operadora Demo' },
      { id: 'usr-tech', name: 'Técnico Demo' },
      { id: 'usr-auditor', name: 'Auditora Demo' },
      { id: 'usr-home', name: 'Propietaria Demo' },
      { id: 'usr-salta', name: 'Administradora Salta' },
    ],
    organizations: [
      { id: 'org-jujuy', name: 'Andes Solar Jujuy', slug: 'andes-solar-jujuy', status: 'ACTIVE', kind: 'SERVICE_PROVIDER', createdAt: '2026-01-15T15:00:00.000Z' },
      { id: 'org-prop', name: 'Familia Andina', slug: 'familia-andina', status: 'ACTIVE', kind: 'HOUSEHOLD', createdAt: '2026-02-01T15:00:00.000Z' },
      { id: 'org-salta', name: 'Sol del Norte Salta', slug: 'sol-del-norte', status: 'ACTIVE', kind: 'BUSINESS', createdAt: '2026-03-01T15:00:00.000Z' },
    ],
    memberships: [
      { id: 'mem-owner', organizationId: 'org-jujuy', userId: 'usr-owner', role: 'OWNER', status: 'ACTIVE', invitedAt: '2026-01-15T15:00:00.000Z', activatedAt: '2026-01-15T15:10:00.000Z' },
      { id: 'mem-tech', organizationId: 'org-jujuy', userId: 'usr-tech', role: 'TECHNICIAN', status: 'ACTIVE', invitedAt: '2026-01-20T15:00:00.000Z', activatedAt: '2026-01-20T15:10:00.000Z' },
      { id: 'mem-auditor', organizationId: 'org-jujuy', userId: 'usr-auditor', role: 'AUDITOR', status: 'ACTIVE', invitedAt: '2026-02-02T15:00:00.000Z', activatedAt: '2026-02-02T15:10:00.000Z' },
      { id: 'mem-home', organizationId: 'org-prop', userId: 'usr-home', role: 'OWNER', status: 'ACTIVE', invitedAt: '2026-02-01T15:00:00.000Z', activatedAt: '2026-02-01T15:10:00.000Z' },
      { id: 'mem-salta', organizationId: 'org-salta', userId: 'usr-salta', role: 'OWNER', status: 'ACTIVE', invitedAt: '2026-03-01T15:00:00.000Z', activatedAt: '2026-03-01T15:10:00.000Z' },
    ],
    sites: [
      { id: 'site-abra', organizationId: 'org-jujuy', name: 'Planta Puna Demo', address: 'Abra Pampa, Jujuy', latitude: -22.72, longitude: -65.7, altitudeM: 3450, timeZone: 'America/Argentina/Jujuy', climateZone: 'Puna' },
      { id: 'site-casa', organizationId: 'org-prop', name: 'Casa Andina Demo', address: 'San Salvador de Jujuy', latitude: -24.18, longitude: -65.3, altitudeM: 1259, timeZone: 'America/Argentina/Jujuy', climateZone: 'Valles' },
      { id: 'site-oran', organizationId: 'org-salta', name: 'Parque Calchaquí Demo', address: 'Cafayate, Salta', latitude: -26.07, longitude: -65.98, altitudeM: 1683, timeZone: 'America/Argentina/Salta', climateZone: 'Valles Calchaquíes' },
    ],
    installations: [
      { id: 'ins-puna', organizationId: 'org-jujuy', siteId: 'site-abra', code: 'HG-PUNA-01', name: 'Puna Norte', gridMode: 'GRID_TIED', status: 'ACTIVE', nominalDcKw: 12000, commissionedOn: '2024-08-01' },
      { id: 'ins-casa', organizationId: 'org-prop', siteId: 'site-casa', code: 'HG-CASA-01', name: 'Casa Andina', gridMode: 'HYBRID', status: 'ACTIVE', nominalDcKw: 3.3, commissionedOn: '2025-04-12' },
      { id: 'ins-salta', organizationId: 'org-salta', siteId: 'site-oran', code: 'HG-SALTA-01', name: 'Calchaquí Oeste', gridMode: 'GRID_TIED', status: 'ACTIVE', nominalDcKw: 750, commissionedOn: '2023-11-20' },
    ],
    grants: [
      { id: 'grant-001', organizationId: 'org-prop', installationId: 'ins-casa', granteeOrganizationId: 'org-jujuy', scope: 'MAINTENANCE', validFrom: '2026-11-01T00:00:00.000Z', validUntil: null, revokedAt: null, reason: 'Mantenimiento puntual de la vivienda. Sin acceso a la economía del hogar.' },
    ],
    equipmentModels: [
      { id: 'em-mod', manufacturer: 'Cardón Power', modelCode: 'CP-550', revision: 'A', kind: 'MODULE', nominalPowerW: 550 },
      { id: 'em-inv', manufacturer: 'Quebrada Electric', modelCode: 'QE-110', revision: '2', kind: 'INVERTER', nominalPowerW: 110000 },
      { id: 'em-esp', manufacturer: 'Borde Andino', modelCode: 'BA-ESP32', revision: '1', kind: 'SENSOR', nominalPowerW: 2 },
      { id: 'em-home', manufacturer: 'Cardón Power', modelCode: 'CP-HYB-5', revision: 'B', kind: 'INVERTER', nominalPowerW: 5000 },
    ],
    arrays: [
      { id: 'arr-b1', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'B1', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2024-08-01' },
      { id: 'arr-b2', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'B2', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2024-08-01' },
      { id: 'arr-b3', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'B3', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2024-08-01' },
      { id: 'arr-b4', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'B4', azimuthDegrees: 0, tiltDegrees: 15, commissionedOn: '2024-08-01' },
      { id: 'arr-casa', organizationId: 'org-prop', installationId: 'ins-casa', name: 'Techo norte', azimuthDegrees: 10, tiltDegrees: 20, commissionedOn: '2025-04-12' },
      { id: 'arr-salta', organizationId: 'org-salta', installationId: 'ins-salta', name: 'Oeste 1', azimuthDegrees: 350, tiltDegrees: 18, commissionedOn: '2023-11-20' },
    ],
    strings: [
      { id: 'str-b2', organizationId: 'org-jujuy', solarArrayId: 'arr-b2', moduleModelId: 'em-mod', stringCode: 'S-B2-01', modulesInSeries: 18, active: true },
      { id: 'str-b1', organizationId: 'org-jujuy', solarArrayId: 'arr-b1', moduleModelId: 'em-mod', stringCode: 'S-B1-01', modulesInSeries: 18, active: true },
      { id: 'str-casa', organizationId: 'org-prop', solarArrayId: 'arr-casa', moduleModelId: 'em-mod', stringCode: 'S-CASA-01', modulesInSeries: 6, active: true },
      { id: 'str-salta', organizationId: 'org-salta', solarArrayId: 'arr-salta', moduleModelId: 'em-mod', stringCode: 'S-SAL-01', modulesInSeries: 16, active: true },
    ],
    equipment: [
      { id: 'eq-inv-03', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-inv', solarStringId: 'str-b2', tag: 'INV-03', serialNumber: 'QE110-DEMO-03', kind: 'INVERTER', status: 'ACTIVE', installedOn: '2024-08-01', removedOn: null },
      { id: 'eq-esp-03', organizationId: 'org-jujuy', installationId: 'ins-puna', equipmentModelId: 'em-esp', solarStringId: 'str-b2', tag: 'ESP-03', serialNumber: 'ESP-DEMO-03', kind: 'SENSOR', status: 'ACTIVE', installedOn: '2024-08-02', removedOn: null },
      { id: 'eq-casa', organizationId: 'org-prop', installationId: 'ins-casa', equipmentModelId: 'em-home', solarStringId: 'str-casa', tag: 'INV-CASA', serialNumber: 'HYB-DEMO-01', kind: 'INVERTER', status: 'ACTIVE', installedOn: '2025-04-12', removedOn: null },
      { id: 'eq-salta', organizationId: 'org-salta', installationId: 'ins-salta', equipmentModelId: 'em-inv', solarStringId: 'str-salta', tag: 'INV-A3', serialNumber: 'QE110-DEMO-A3', kind: 'INVERTER', status: 'FAILED', installedOn: '2023-11-20', removedOn: null },
    ],
    gateways: [
      { id: 'gw-1', organizationId: 'org-jujuy', installationId: 'ins-puna', hardwareId: 'raspi-demo-01', firmwareVersion: 'edge-0.4.0', status: 'ONLINE', lastSeenAt: T0, provisionedAt: '2024-08-03T15:00:00.000Z' },
      { id: 'gw-2', organizationId: 'org-prop', installationId: 'ins-casa', hardwareId: 'raspi-demo-02', firmwareVersion: 'edge-0.4.0', status: 'ONLINE', lastSeenAt: T0, provisionedAt: '2025-04-13T15:00:00.000Z' },
      { id: 'gw-3', organizationId: 'org-salta', installationId: 'ins-salta', hardwareId: 'raspi-demo-03', firmwareVersion: 'edge-0.3.2', status: 'ONLINE', lastSeenAt: T0, provisionedAt: '2023-11-21T15:00:00.000Z' },
    ],
    metrics: [
      { id: 'm-power', code: 'AC_POWER_W', unitCode: 'W', description: 'Potencia de CA instantánea', counter: false },
      { id: 'm-energy', code: 'ENERGY_WH', unitCode: 'Wh', description: 'Energía acumulada de contador', counter: true },
    ],
    channels: [
      { id: 'ch-inv-03', organizationId: 'org-jujuy', equipmentId: 'eq-inv-03', metricDefinitionId: 'm-power', externalChannelCode: 'inv-03-ac-power', enabled: true, samplePeriodSeconds: 900 },
      { id: 'ch-casa', organizationId: 'org-prop', equipmentId: 'eq-casa', metricDefinitionId: 'm-power', externalChannelCode: 'casa-ac-power', enabled: true, samplePeriodSeconds: 900 },
      { id: 'ch-salta', organizationId: 'org-salta', equipmentId: 'eq-salta', metricDefinitionId: 'm-power', externalChannelCode: 'salta-ac-power', enabled: true, samplePeriodSeconds: 900 },
    ],
    alertRules: [
      { id: 'rule-deficit', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Déficit de bloque', ruleCode: 'BLOCK_DEFICIT', severity: 'MEDIUM', enabled: true, thresholdValue: 0.08, minPersistenceSeconds: 900 },
    ],
    incidents: [
      { id: 'inc-casa', organizationId: 'org-prop', installationId: 'ins-casa', alertRuleId: null, title: 'Carga restringida por frío', severity: 'HIGH', status: 'ACKNOWLEDGED', firstSeenAt: '2026-11-22T12:20:00.000Z', lastSeenAt: '2026-11-22T12:20:00.000Z', acknowledgedAt: '2026-11-22T12:40:00.000Z', resolvedAt: null, dedupKey: 'battery:ins-casa', domain: 'ENERGY' },
      { id: 'inc-salta', organizationId: 'org-salta', installationId: 'ins-salta', alertRuleId: null, title: 'Falla de inversor A3', severity: 'HIGH', status: 'OPEN', firstSeenAt: '2026-11-22T15:15:00.000Z', lastSeenAt: '2026-11-22T15:15:00.000Z', acknowledgedAt: null, resolvedAt: null, dedupKey: 'hardware:ins-salta:a3', domain: 'ENERGY' },
    ],
    incidentEvents: [
      { id: 'iev-casa', organizationId: 'org-prop', incidentId: 'inc-casa', occurredAt: '2026-11-22T12:40:00.000Z', eventCode: 'ACKNOWLEDGED', detail: 'La propietaria vio el aviso. Sigue abierto el diagnóstico de batería.' },
      { id: 'iev-salta', organizationId: 'org-salta', incidentId: 'inc-salta', occurredAt: '2026-11-22T15:15:00.000Z', eventCode: 'OPENED', detail: 'El inversor A3 dejó de reportar potencia útil.' },
    ],
    modelRuns: [
      { id: 'run-b2', organizationId: 'org-jujuy', installationId: 'ins-puna', arrayId: 'arr-b2', modelName: 'helio-intervalo', modelVersion: '0.4.0', periodStart: '2026-11-22T13:30:00.000Z', periodEnd: '2026-11-22T13:45:00.000Z', expectedWh: 12000, actualWh: 11550, coveragePct: 100, inputFingerprint: 'weather-sim|b2|1030', calculatedAt: T0, comparable: true },
      { id: 'run-casa', organizationId: 'org-prop', installationId: 'ins-casa', arrayId: 'arr-casa', modelName: 'helio-intervalo', modelVersion: '0.4.0', periodStart: '2026-11-21T15:00:00.000Z', periodEnd: '2026-11-22T15:00:00.000Z', expectedWh: 13200, actualWh: 10900, coveragePct: 96, inputFingerprint: 'weather-sim|casa|day', calculatedAt: T0, comparable: true },
      { id: 'run-salta', organizationId: 'org-salta', installationId: 'ins-salta', arrayId: 'arr-salta', modelName: 'helio-intervalo', modelVersion: '0.4.0', periodStart: '2026-11-21T15:00:00.000Z', periodEnd: '2026-11-22T15:00:00.000Z', expectedWh: 2850000, actualWh: 2480000, coveragePct: 90, inputFingerprint: 'weather-sim|salta|day', calculatedAt: T0, comparable: true },
    ],
    lossAssessments: [
      { id: 'loss-b2', organizationId: 'org-jujuy', modelRunId: 'run-b2', totalLossWh: 450, lossPct: 3.75, methodology: 'integración de potencia en 15 min', assessedAt: T0, validationStatus: 'intervalo comparable, dato simulado' },
      { id: 'loss-casa', organizationId: 'org-prop', modelRunId: 'run-casa', totalLossWh: 2300, lossPct: 17.4, methodology: 'diferencia de contador diario', assessedAt: T0, validationStatus: 'causa no cerrada' },
      { id: 'loss-salta', organizationId: 'org-salta', modelRunId: 'run-salta', totalLossWh: 370000, lossPct: 13, methodology: 'diferencia de contador diario', assessedAt: T0, validationStatus: 'hardware sospechado, sin peritaje' },
    ],
    lossCauses: [
      { id: 'cause-casa', organizationId: 'org-prop', lossAssessmentId: 'loss-casa', cause: 'UNKNOWN', estimatedWh: 2300, method: 'sin sensor de batería independiente', evidenceNote: 'No se atribuye a suciedad.' },
      { id: 'cause-salta', organizationId: 'org-salta', lossAssessmentId: 'loss-salta', cause: 'HARDWARE', estimatedWh: 200000, method: 'inversor en falla', evidenceNote: 'El resto del déficit diario queda sin cerrar.' },
      { id: 'cause-salta-unk', organizationId: 'org-salta', lossAssessmentId: 'loss-salta', cause: 'UNKNOWN', estimatedWh: 170000, method: 'residual', evidenceNote: 'No se suma por encima del déficit.' },
    ],
    tariffPlans: [
      { id: 'tariff-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', name: 'Autoconsumo demo Puna', currencyCode: 'ARS', distributor: 'Cuadro demostrativo, no distribuidora real' },
    ],
    tariffRates: [
      { id: 'rate-puna', organizationId: 'org-jujuy', tariffPlanId: 'tariff-puna', validFrom: '2026-11-01T00:00:00.000Z', validTo: null, purchasePricePerKwh: 200, exportPricePerKwh: 80, selfConsumptionRatio: 0.7 },
    ],
    economicAssessments: [
      { id: 'eco-b2', organizationId: 'org-jujuy', lossAssessmentId: 'loss-b2', tariffRateId: 'rate-puna', assessedAt: T0, currencyCode: 'ARS', selfConsumptionLostWh: 315, exportedLostWh: 135, estimatedLossAmount: 73.8, tariffSnapshotNote: 'Supuesto de demo. No es tarifa vigente.' },
    ],
    auditTemplates: [
      { id: 'tpl-campo', organizationId: 'org-jujuy', title: 'Auditoría de puesta a tierra y rótulos', templateVersion: '1.2.0', active: true, publishedAt: '2026-10-01T15:00:00.000Z' },
    ],
    auditQuestions: [
      { id: 'q1', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'PE-01', label: 'La puesta a tierra del inversor INV-03 tiene continuidad medida.', answerType: 'text', sortOrder: 1, mandatory: true, section: 'Eléctrica' },
      { id: 'q2', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'ROT-01', label: 'Los rótulos de seccionamiento son legibles desde el pasillo.', answerType: 'text', sortOrder: 2, mandatory: true, section: 'Señalización' },
      { id: 'q3', organizationId: 'org-jujuy', auditTemplateId: 'tpl-campo', questionCode: 'ACC-01', label: 'El acceso al bloque B2 no exige trabajo improvisado en techo.', answerType: 'text', sortOrder: 3, mandatory: true, section: 'Acceso' },
    ],
    auditReports: [
      { id: 'audit-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', auditTemplateId: 'tpl-campo', auditorMembershipId: 'mem-auditor', status: 'IN_PROGRESS', startedAt: '2026-11-20T15:00:00.000Z', completedAt: null, summary: 'Recorrido parcial. Falta cerrar acceso.' },
    ],
    auditAnswers: [
      { id: 'ans-1', organizationId: 'org-jujuy', auditReportId: 'audit-puna', auditQuestionId: 'q1', answerText: 'Continuidad registrada en el parte de obra simulado.', answeredAt: '2026-11-20T16:00:00.000Z', compliant: true },
      { id: 'ans-2', organizationId: 'org-jujuy', auditReportId: 'audit-puna', auditQuestionId: 'q2', answerText: '', answeredAt: null, compliant: null },
      { id: 'ans-3', organizationId: 'org-jujuy', auditReportId: 'audit-puna', auditQuestionId: 'q3', answerText: '', answeredAt: null, compliant: null },
    ],
    auditFindings: [
      { id: 'find-1', organizationId: 'org-jujuy', auditReportId: 'audit-puna', title: 'Rótulo de seccionamiento desvanecido', severity: 'MEDIUM', description: 'El rótulo del pasillo B2 no se lee a un metro.', recommendation: 'Reponer rótulo con contraste suficiente. No implica corte eléctrico en esta demo.', resolvedAt: null },
    ],
    auditEvidence: [
      { id: 'aev-1', organizationId: 'org-jujuy', findingId: 'find-1', storageKey: 'local:rotulo-b2.svg', mimeType: 'image/svg+xml', sha256: 'demo-rotulo-b2', capturedAt: '2026-11-20T16:10:00.000Z' },
    ],
    maintenancePlans: [
      { id: 'plan-1', organizationId: 'org-jujuy', name: 'Cobertura de bloques críticos', status: 'ACTIVE' },
    ],
    planVersions: [
      { id: 'plan-v1', organizationId: 'org-jujuy', maintenancePlanId: 'plan-1', versionCode: '2026.11', publishedAt: '2026-11-01T15:00:00.000Z', currencyCode: 'ARS', recurringAmount: 480000, termsReference: 'local:terminos-cobertura-2026.11', sla: 'Respuesta de diagnóstico simulado en 8 h hábiles. No incluye desplazamiento real.', exclusions: 'No incluye reparación de inversores, trabajo en techo ni operación de drones certificados.' },
    ],
    agreements: [
      { id: 'agr-1', organizationId: 'org-jujuy', providerOrganizationId: 'org-jujuy', installationId: 'ins-puna', status: 'ACTIVE', startsAt: '2026-11-01T00:00:00.000Z', endsAt: '2027-11-01T00:00:00.000Z', approvedAt: '2026-10-28T15:00:00.000Z' },
    ],
    subscriptions: [
      { id: 'sub-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', planVersionId: 'plan-v1', serviceAgreementId: 'agr-1', status: 'ACTIVE', activatedAt: '2026-11-01T00:00:00.000Z', expiresAt: '2027-11-01T00:00:00.000Z', agreedAmount: 480000, currencyCode: 'ARS' },
    ],
    proposals: [
      { id: 'prop-puna', organizationId: 'org-jujuy', installationId: 'ins-puna', planVersionId: 'plan-v1', referenceCode: 'PR-2026-014', proposedAt: '2026-10-20T15:00:00.000Z', validUntil: '2026-12-20T15:00:00.000Z', amount: 480000, currencyCode: 'ARS', acceptedAt: null },
    ],
    workOrders: [
      { id: 'wo-casa', organizationId: 'org-prop', installationId: 'ins-casa', incidentId: 'inc-casa', subscriptionId: null, assignedMembershipId: 'mem-tech', referenceCode: 'OT-CASA-014', title: 'Revisión de batería por frío', status: 'ASSIGNED', priority: 'HIGH', openedAt: '2026-11-22T12:45:00.000Z', scheduledAt: '2026-11-23T13:00:00.000Z', closedAt: null },
    ],
    tasks: [
      { id: 'task-casa', organizationId: 'org-prop', workOrderId: 'wo-casa', description: 'Medir tensión de banco y no puentear protecciones.', status: 'TODO', sortOrder: 1 },
    ],
    interventions: [
      { id: 'int-hist', organizationId: 'org-jujuy', workOrderId: 'wo-casa', type: 'INSPECTION', startedAt: '2026-10-02T14:00:00.000Z', finishedAt: '2026-10-02T15:00:00.000Z', workCost: 0, currencyCode: 'ARS', notes: 'Inspección histórica simulada, previa a esta sesión.' },
    ],
    interventionEvidence: [
      { id: 'evd-hist', organizationId: 'org-jujuy', interventionId: 'int-hist', storageKey: 'local:hist-inspection.svg', mimeType: 'image/svg+xml', sha256: 'demo-hist', capturedAt: '2026-10-02T15:00:00.000Z', phase: 'AFTER' },
    ],
    recoveries: [
      { id: 'rec-hist', organizationId: 'org-jujuy', interventionId: 'int-hist', modelRunId: null, assessedAt: '2026-10-02T16:00:00.000Z', baselineMethod: 'sin línea de base equivalente', expectedRecoveredWh: null, verifiedRecoveredWh: null, confidenceNote: 'Inconclusa: no había intervalo comparable.' },
    ],
    devices: [
      { id: 'dev-inv-03', organizationId: 'org-jujuy', gatewayId: 'gw-1', equipmentId: 'eq-inv-03', deviceUid: 'inverter-03', keyFingerprint: 'demo-only-not-a-key', protocolName: 'mqtt-simulado', trustStatus: 'ACTIVE', keyRotatedAt: '2026-09-01T15:00:00.000Z', validUntil: null },
    ],
    integrityEvents: [
      { id: 'tev-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', deviceIdentityId: 'dev-inv-03', telemetryChannelId: 'ch-inv-03', eventUid: 'evt-hist-gap', eventType: 'SEQUENCE_GAP', status: 'EXPLAINED', observedAt: '2026-10-11T16:00:00.000Z', sourceObservedAt: '2026-10-11T16:00:00.000Z', sequenceNumber: 40, payloadSha256: 'sha256-demo-hist', detail: 'Hueco explicado por reinicio de mantenimiento. Caso cerrado en la simulación.', deviceId: 'inverter-03', bootId: 'boot-3', gatewayId: 'gw-1', sourceEventId: 'evt-hist-gap' },
    ],
    integrityAssessments: [
      { id: 'as-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', integrityEventId: 'tev-hist', modelRunId: null, assessedAt: '2026-10-11T17:00:00.000Z', trustLevel: 'ESTIMATED', ruleVersion: 'helioguard-dedup-1', reason: 'El hueco coincidió con un reinicio registrado. No se elevó a incidente abierto.' },
    ],
    evidenceSources: [
      { id: 'src-weather', code: 'weather-sim', providerName: 'Modelo meteorológico didáctico', kind: 'SATELLITE_WEATHER', spatialResolutionM: null, licenseReference: 'Generado para la demo. No es un producto satelital ni una licencia comercial.' },
      { id: 'src-inverter', code: 'inverter-sim', providerName: 'Telemetría simulada de inversor', kind: 'INVERTER', spatialResolutionM: null, licenseReference: 'Fixture local versionado.' },
      { id: 'src-edge', code: 'edge-sim', providerName: 'Raspberry simulada', kind: 'EDGE_GATEWAY', spatialResolutionM: null, licenseReference: 'Fixture local versionado.' },
    ],
    provenance: [
      { id: 'prov-b2', organizationId: 'org-jujuy', installationId: 'ins-puna', evidenceSourceId: 'src-weather', modelRunId: 'run-b2', sourceRecordKey: 'weather-sim|puna|2026-11-22T13:30Z', observedAt: '2026-11-22T13:30:00.000Z', ingestedAt: T0, contentSha256: 'sha256-weather-b2', trustLevel: 'ESTIMATED', qualityFlags: 'SIMULATED,NO_THERMAL_SATELLITE', algorithmVersion: '0.4.0' },
      { id: 'prov-inv', organizationId: 'org-jujuy', installationId: 'ins-puna', evidenceSourceId: 'src-inverter', modelRunId: 'run-b2', sourceRecordKey: 'inv-03|intervalo|1030', observedAt: '2026-11-22T13:30:00.000Z', ingestedAt: T0, contentSha256: 'sha256-inv-b2', trustLevel: 'ESTIMATED', qualityFlags: 'SIMULATED,UNSIGNED', algorithmVersion: '0.4.0' },
    ],
    securityIncidents: [
      { id: 'sec-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', integrityAssessmentId: 'as-hist', referenceCode: 'SEC-HIST-001', title: 'Hueco de secuencia ya explicado', status: 'CLOSED', openedAt: '2026-10-11T16:05:00.000Z', closedAt: '2026-10-11T18:00:00.000Z', resolutionNote: 'Cerrado como reinicio de mantenimiento. No fue un ataque.' },
    ],
    drones: [
      { id: 'drone-01', organizationId: 'org-jujuy', assetCode: 'DR-DEMO-1', modelName: 'Aeronave simulada de inspección', status: 'READY', maximumWindMs: 12 },
    ],
    missions: [
      { id: 'msn-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', workOrderId: null, droneAssetId: 'drone-01', providerOrganizationId: 'org-jujuy', missionCode: 'HG-HIST-000', kind: 'VISUAL_INSPECTION', status: 'COMPLETED', proposedAt: '2026-10-02T13:00:00.000Z', startedAt: '2026-10-02T14:00:00.000Z', completedAt: '2026-10-02T14:20:00.000Z', safetyChecklistSummary: 'Histórica, ya cerrada. No autoriza vuelos nuevos.', simulated: true },
    ],
    authorizations: [
      { id: 'auth-hist', organizationId: 'org-jujuy', missionId: 'msn-hist', authorizedByMembershipId: 'mem-owner', status: 'APPROVED', decidedAt: '2026-10-02T13:30:00.000Z', expiresAt: '2026-10-02T18:00:00.000Z', operatorReference: 'Operadora Demo', decisionNote: 'Aprobación histórica de la simulación. No es permiso vigente.' },
    ],
    verifications: [
      { id: 'ver-hist', organizationId: 'org-jujuy', installationId: 'ins-puna', droneMissionId: 'msn-hist', recoveryAssessmentId: 'rec-hist', status: 'INCONCLUSIVE', assessedAt: '2026-10-02T16:00:00.000Z', baselineMethod: 'sin intervalo equivalente', recoveredWh: null, evidenceSummary: 'La inspección histórica no demostró recuperación.' },
    ],
    intervals: [
      { id: 'iv-b2-1000', installationId: 'ins-puna', arrayId: 'arr-b2', label: '10:00', startIso: '2026-11-22T13:00:00.000Z', endIso: '2026-11-22T13:15:00.000Z', durationSeconds: 900, expectedPowerW: 40000, actualPowerW: 39200, comparable: true },
      { id: 'iv-b2-1015', installationId: 'ins-puna', arrayId: 'arr-b2', label: '10:15', startIso: '2026-11-22T13:15:00.000Z', endIso: '2026-11-22T13:30:00.000Z', durationSeconds: 900, expectedPowerW: 46000, actualPowerW: 45100, comparable: true },
      { id: 'iv-b2-1030', installationId: 'ins-puna', arrayId: 'arr-b2', label: '10:30', startIso: '2026-11-22T13:30:00.000Z', endIso: '2026-11-22T13:45:00.000Z', durationSeconds: 900, expectedPowerW: 48000, actualPowerW: 46200, comparable: true },
      { id: 'iv-b2-1045', installationId: 'ins-puna', arrayId: 'arr-b2', label: '10:45', startIso: '2026-11-22T13:45:00.000Z', endIso: '2026-11-22T14:00:00.000Z', durationSeconds: 900, expectedPowerW: 45000, actualPowerW: 44100, comparable: true },
    ],
  };
  recomputeSector(snapshot);
  return snapshot;
}
