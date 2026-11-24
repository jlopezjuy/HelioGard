import type {
  AuditStatus,
  DemoSnapshot,
  LossCause,
  LossScenario,
  IngestProof,
  IntegrityAssessment,
  MembershipRole,
  SecurityIncident,
  TelemetryIntegrityEvent,
  TelemetryMessage,
} from './model';
import { LIVE, SECTOR } from './model';
import { currentRole } from './access';
import {
  assertCauseAllocation,
  canTransition,
  deficitWh,
  evaluateIngest,
  lossAmountArs,
  safetyBlockReasons,
  wattHoursFromPower,
} from './rules';

const APPROVERS: readonly MembershipRole[] = ['OWNER', 'ADMIN'];
const OPERATORS: readonly MembershipRole[] = ['OWNER', 'ADMIN', 'OPERATOR'];

function clone(snapshot: DemoSnapshot): DemoSnapshot {
  return structuredClone(snapshot);
}

function advance(snapshot: DemoSnapshot, minutes: number): void {
  const time = new Date(snapshot.clockIso);
  time.setUTCMinutes(time.getUTCMinutes() + minutes);
  snapshot.clockIso = time.toISOString();
}

function say(snapshot: DemoSnapshot, notice: string): DemoSnapshot {
  snapshot.scenario.notice = notice;
  return snapshot;
}

function liveMission(snapshot: DemoSnapshot) {
  return snapshot.missions.find((mission) => mission.id === LIVE.missionId);
}

function blocksOf(snapshot: DemoSnapshot): string[] {
  return safetyBlockReasons(snapshot.scenario);
}

export function recomputeSector(snapshot: DemoSnapshot): void {
  const interval = snapshot.intervals.find((item) => item.id === SECTOR.intervalId);
  const run = snapshot.modelRuns.find((item) => item.id === SECTOR.modelRunId);
  const loss = snapshot.lossAssessments.find((item) => item.id === SECTOR.lossId);
  const rate = snapshot.tariffRates.find((item) => item.id === 'rate-puna');
  const economy = snapshot.economicAssessments.find((item) => item.id === SECTOR.economicId);
  if (!interval || !run || !loss || !rate || !economy) {
    return;
  }
  interval.actualPowerW = actualPowerFor(snapshot.scenario.lossScenario);
  const expectedWh = wattHoursFromPower(interval.expectedPowerW, interval.durationSeconds);
  const actualWh =
    interval.actualPowerW === null
      ? null
      : wattHoursFromPower(interval.actualPowerW, interval.durationSeconds);
  run.expectedWh = expectedWh;
  run.actualWh = actualWh;
  run.comparable = interval.comparable;
  run.calculatedAt = snapshot.clockIso;
  const deficit = deficitWh(expectedWh, actualWh, interval.comparable);
  snapshot.lossCauses = snapshot.lossCauses.filter((cause) => cause.lossAssessmentId !== loss.id);
  if (deficit.kind === 'unavailable') {
    loss.totalLossWh = 0;
    loss.lossPct = null;
    loss.validationStatus = 'sin estimación confiable';
    loss.assessedAt = snapshot.clockIso;
    economy.estimatedLossAmount = 0;
    economy.selfConsumptionLostWh = 0;
    economy.exportedLostWh = 0;
    economy.assessedAt = snapshot.clockIso;
    economy.tariffSnapshotNote = 'Sin estimación confiable: el intervalo no es comparable.';
    return;
  }
  const parts = causeParts(snapshot.scenario.lossScenario, deficit.deficitWh);
  const allocation = assertCauseAllocation(deficit.deficitWh, parts);
  if (!allocation.ok) {
    loss.validationStatus = allocation.reason;
    return;
  }
  parts.forEach((part, index) => {
    snapshot.lossCauses.push({
      id: `cause-b2-${part.cause.toLowerCase()}`,
      organizationId: 'org-jujuy',
      lossAssessmentId: loss.id,
      cause: part.cause,
      estimatedWh: part.estimatedWh,
      method: part.cause === 'UNKNOWN' ? 'residual no atribuido' : 'comparación de intervalo, simulada',
      evidenceNote:
        part.cause === 'UNKNOWN'
          ? 'El resto queda sin causa cerrada.'
          : 'Hipótesis parcial. No hay medición directa que cierre la causa.',
    });
    void index;
  });
  loss.totalLossWh = deficit.deficitWh;
  loss.lossPct = expectedWh === 0 ? null : (deficit.deficitWh / expectedWh) * 100;
  loss.validationStatus = 'intervalo comparable, dato simulado';
  loss.assessedAt = snapshot.clockIso;
  const selfWh = deficit.deficitWh * rate.selfConsumptionRatio;
  economy.selfConsumptionLostWh = selfWh;
  economy.exportedLostWh = deficit.deficitWh - selfWh;
  economy.estimatedLossAmount = lossAmountArs(
    deficit.deficitWh,
    rate.selfConsumptionRatio,
    rate.purchasePricePerKwh,
    rate.exportPricePerKwh,
  );
  economy.tariffRateId = rate.id;
  economy.assessedAt = snapshot.clockIso;
  economy.tariffSnapshotNote = `Demo ARS. Compra ${rate.purchasePricePerKwh} / inyección ${rate.exportPricePerKwh} por kWh. No es tarifa vigente.`;
}

const LOSS_POWER_W: Record<LossScenario, number> = {
  NONE: SECTOR.healthyPowerW,
  SOILING: SECTOR.soiledPowerW,
  INVERTER: 6_000,
  CLOUD: 36_000,
  FROST: 22_000,
  HAIL: 14_000,
  SMOKE: 27_000,
};

const LOSS_NOTICE: Record<LossScenario, string> = {
  NONE: 'Sector B2 vuelve al régimen simulado sano.',
  SOILING: 'Polvo en B2: la potencia cae. La suciedad es hipótesis parcial, no certeza.',
  INVERTER: 'Inversor INV-03 por debajo del límite. Puede ser falla de equipo. No está cerrado.',
  CLOUD: 'Nube transitoria en el intervalo. Parte del déficit queda como clima y el resto sin causa.',
  FROST: 'Helada en el intervalo. No se simula limpieza con agua. La causa térmica no cubre todo el déficit.',
  HAIL: 'Granizo simulado en el intervalo. El daño no se da por confirmado.',
  SMOKE: 'Humo o sombreado en el intervalo. No se atribuye todo el déficit a esa causa.',
};

function actualPowerFor(scenario: LossScenario | undefined): number {
  return LOSS_POWER_W[scenario ?? 'NONE'];
}

function causeParts(scenario: LossScenario | undefined, deficitWhValue: number): { cause: LossCause; estimatedWh: number }[] {
  if (!scenario || scenario === 'NONE' || deficitWhValue === 0) {
    return [{ cause: 'UNKNOWN', estimatedWh: deficitWhValue }];
  }
  const primary: LossCause =
    scenario === 'SOILING'
      ? 'SOILING'
      : scenario === 'INVERTER'
        ? 'HARDWARE'
        : scenario === 'FROST'
          ? 'THERMAL'
          : scenario === 'SMOKE'
            ? 'SHADING'
            : 'WEATHER';
  const share = Math.round(deficitWhValue * 0.6);
  return [
    { cause: primary, estimatedWh: share },
    { cause: 'UNKNOWN', estimatedWh: deficitWhValue - share },
  ];
}

export function setLossScenario(snapshot: DemoSnapshot, scenario: LossScenario): DemoSnapshot {
  const next = clone(snapshot);
  next.scenario.lossScenario = scenario;
  next.scenario.soiling = scenario === 'SOILING';
  advance(next, 1);
  recomputeSector(next);
  return say(next, LOSS_NOTICE[scenario]);
}

export function setSoiling(snapshot: DemoSnapshot, enabled: boolean): DemoSnapshot {
  return setLossScenario(snapshot, enabled ? 'SOILING' : 'NONE');
}

export function setSession(snapshot: DemoSnapshot, userId: string): DemoSnapshot {
  const next = clone(snapshot);
  const membership = next.memberships.find((item) => item.userId === userId && item.status === 'ACTIVE');
  if (!membership) {
    return say(next, 'Ese usuario no tiene una membresía activa en la demo.');
  }
  next.session.userId = userId;
  next.session.organizationId = membership.organizationId;
  const user = next.users.find((item) => item.id === userId);
  return say(next, `Sesión demo: ${user?.name ?? userId}. Los permisos son una simulación de interfaz.`);
}

export function createResponseChain(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const role = currentRole(next);
  if (!role || !OPERATORS.includes(role)) {
    return say(next, 'Solo lectura para este rol: crear la misión requiere OWNER, ADMIN u OPERATOR.');
  }
  if (next.incidents.some((incident) => incident.id === LIVE.incidentId)) {
    return say(next, 'La cadena del bloque B2 ya existe. Seguí en Misiones o Mantenimiento.');
  }
  next.scenario.soiling = true;
  next.scenario.lossScenario = 'SOILING';
  advance(next, 1);
  recomputeSector(next);
  next.incidents.push({
    id: LIVE.incidentId,
    organizationId: 'org-jujuy',
    installationId: 'ins-puna',
    alertRuleId: 'rule-deficit',
    title: 'Pérdida sectorizada en bloque B2',
    severity: 'MEDIUM',
    status: 'OPEN',
    firstSeenAt: next.clockIso,
    lastSeenAt: next.clockIso,
    acknowledgedAt: null,
    resolvedAt: null,
    dedupKey: 'soiling:ins-puna:arr-b2',
    domain: 'ENERGY',
  });
  next.incidentEvents.push({
    id: 'iev-live-b2',
    organizationId: 'org-jujuy',
    incidentId: LIVE.incidentId,
    occurredAt: next.clockIso,
    eventCode: 'OPENED',
    detail: 'Déficit del intervalo 10:30 en INV-03. Causa preliminar repartida; no cerrada.',
  });
  next.workOrders.push({
    id: LIVE.workOrderId,
    organizationId: 'org-jujuy',
    installationId: 'ins-puna',
    incidentId: LIVE.incidentId,
    subscriptionId: 'sub-puna',
    assignedMembershipId: 'mem-tech',
    referenceCode: 'OT-B2-001',
    title: 'Inspección y limpieza simulada del bloque B2',
    status: 'CREATED',
    priority: 'MEDIUM',
    openedAt: next.clockIso,
    scheduledAt: next.clockIso,
    closedAt: null,
  });
  next.tasks.push({
    id: LIVE.taskId,
    organizationId: 'org-jujuy',
    workOrderId: LIVE.workOrderId,
    description: 'Relevar bloque B2 y registrar comparación antes/después.',
    status: 'TODO',
    sortOrder: 1,
  });
  next.missions.push({
    id: LIVE.missionId,
    organizationId: 'org-jujuy',
    installationId: 'ins-puna',
    workOrderId: LIVE.workOrderId,
    droneAssetId: 'drone-01',
    providerOrganizationId: 'org-jujuy',
    missionCode: 'HG-DEMO-B2',
    kind: 'CLEANING',
    status: 'DRAFT',
    proposedAt: next.clockIso,
    startedAt: null,
    completedAt: null,
    safetyChecklistSummary: 'Borrador. Checklist todavía no corrido.',
    simulated: true,
  });
  return say(
    next,
    'Quedó Incident → WorkOrder → DroneMission en DRAFT. Falta la revisión de seguridad antes de cualquier despacho.',
  );
}

export function reviewSafety(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const mission = liveMission(next);
  if (!mission) {
    return say(next, 'No hay misión viva. Creá primero la cadena del bloque B2.');
  }
  if (mission.status !== 'DRAFT' && mission.status !== 'SAFETY_REVIEW') {
    return say(next, `La misión ya está en ${mission.status}. No se rehace el checklist desde este estado.`);
  }
  if (mission.status === 'DRAFT' && !canTransition('DRAFT', 'SAFETY_REVIEW')) {
    return say(next, 'Transición inválida.');
  }
  mission.status = 'SAFETY_REVIEW';
  const reasons = blocksOf(next);
  mission.safetyChecklistSummary = reasons.length
    ? `Bloqueada: ${reasons.join(' ')}`
    : 'Clima, fabricante, operador y sitio aceptados en la simulación.';
  if (reasons.length) {
    return say(next, `No se despacha ni se autoriza. ${reasons.join(' ')}`);
  }
  if (!canTransition('SAFETY_REVIEW', 'AWAITING_AUTHORIZATION')) {
    return say(next, 'No se puede pedir autorización desde este estado.');
  }
  mission.status = 'AWAITING_AUTHORIZATION';
  if (!next.authorizations.some((item) => item.id === LIVE.authorizationId)) {
    next.authorizations.push({
      id: LIVE.authorizationId,
      organizationId: 'org-jujuy',
      missionId: LIVE.missionId,
      authorizedByMembershipId: null,
      status: 'PENDING',
      decidedAt: null,
      expiresAt: null,
      operatorReference: 'Operadora Demo',
      decisionNote: 'Pendiente. Misión simulada.',
    });
  }
  advance(next, 1);
  return say(next, 'Checklist aceptado. La misión espera autorización. Todavía no está en vuelo.');
}

export function approveMission(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const role = currentRole(next);
  if (!role || !APPROVERS.includes(role)) {
    return say(next, 'Solo lectura para este rol: aprobar la misión simulada requiere OWNER o ADMIN.');
  }
  const mission = liveMission(next);
  const authorization = next.authorizations.find((item) => item.id === LIVE.authorizationId);
  if (!mission || !authorization) {
    return say(next, 'Falta la revisión de seguridad. No se puede aprobar un borrador.');
  }
  const reasons = blocksOf(next);
  if (reasons.length) {
    return say(next, `Autorización retenida. ${reasons.join(' ')}`);
  }
  if (mission.status !== 'AWAITING_AUTHORIZATION' || !canTransition(mission.status, 'AUTHORIZED')) {
    return say(next, `No se salta a AUTHORIZED desde ${mission.status}.`);
  }
  mission.status = 'AUTHORIZED';
  authorization.status = 'APPROVED';
  authorization.authorizedByMembershipId = 'mem-owner';
  authorization.decidedAt = next.clockIso;
  const expires = new Date(next.clockIso);
  expires.setUTCHours(expires.getUTCHours() + 2);
  authorization.expiresAt = expires.toISOString();
  authorization.decisionNote =
    'Misión simulada — esta aprobación no representa un permiso operativo real ni habilita un dron físico.';
  advance(next, 1);
  return say(next, authorization.decisionNote);
}

export function dispatchMission(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const role = currentRole(next);
  if (!role || !OPERATORS.includes(role)) {
    return say(next, 'Solo lectura para este rol: despachar requiere OWNER, ADMIN u OPERATOR.');
  }
  const mission = liveMission(next);
  const authorization = next.authorizations.find((item) => item.missionId === LIVE.missionId);
  if (!mission) {
    return say(next, 'No hay misión para despachar.');
  }
  if (mission.status === 'DRAFT' || mission.status === 'SAFETY_REVIEW' || mission.status === 'AWAITING_AUTHORIZATION') {
    return say(next, `No se puede pasar de ${mission.status} a IN_FLIGHT. Falta autorización previa.`);
  }
  const reasons = blocksOf(next);
  if (reasons.length) {
    return say(next, `Despacho bloqueado. ${reasons.join(' ')}`);
  }
  if (!authorization || authorization.status !== 'APPROVED') {
    return say(next, 'Sin MissionAuthorization APPROVED no hay despacho.');
  }
  if (!canTransition(mission.status, 'DISPATCHED') || !canTransition('DISPATCHED', 'IN_FLIGHT')) {
    return say(next, `El estado ${mission.status} no admite despacho.`);
  }
  mission.status = 'DISPATCHED';
  mission.status = 'IN_FLIGHT';
  mission.startedAt = next.clockIso;
  next.scenario.droneProgress = 0;
  const order = next.workOrders.find((item) => item.id === LIVE.workOrderId);
  if (order) {
    order.status = 'IN_PROGRESS';
  }
  advance(next, 1);
  return say(next, 'Despacho simulado: DISPATCHED y luego IN_FLIGHT. El dron de la pantalla no existe fuera de esta demo.');
}

export function tickDrone(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const mission = liveMission(next);
  if (!mission || mission.status !== 'IN_FLIGHT') {
    return say(next, 'El recorrido solo avanza con la misión en IN_FLIGHT.');
  }
  next.scenario.droneProgress = Math.min(1, Math.round((next.scenario.droneProgress + 0.125) * 1000) / 1000);
  return say(next, `Recorrido simulado ${Math.round(next.scenario.droneProgress * 100)} %.`);
}

export function verifyMission(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const mission = liveMission(next);
  if (!mission || mission.status !== 'IN_FLIGHT') {
    return say(next, 'La verificación pide una misión en vuelo simulado.');
  }
  if (next.scenario.droneProgress < 1) {
    return say(next, 'El dron simulado todavía no completó el recorrido.');
  }
  const loss = next.lossAssessments.find((item) => item.id === SECTOR.lossId);
  const before = loss?.totalLossWh ?? 0;
  next.scenario.soiling = false;
  next.scenario.lossScenario = 'NONE';
  recomputeSector(next);
  const after = next.lossAssessments.find((item) => item.id === SECTOR.lossId)?.totalLossWh ?? 0;
  const recovered = Math.max(0, before - after);
  mission.status = 'COMPLETED';
  mission.completedAt = next.clockIso;
  const order = next.workOrders.find((item) => item.id === LIVE.workOrderId);
  if (order) {
    order.status = 'VERIFIED';
    order.closedAt = next.clockIso;
  }
  const task = next.tasks.find((item) => item.id === LIVE.taskId);
  if (task) {
    task.status = 'DONE';
  }
  if (!next.interventions.some((item) => item.id === LIVE.interventionId)) {
    next.interventions.push({
      id: LIVE.interventionId,
      organizationId: 'org-jujuy',
      workOrderId: LIVE.workOrderId,
      type: 'CLEANING',
      startedAt: mission.startedAt,
      finishedAt: next.clockIso,
      workCost: 0,
      currencyCode: 'ARS',
      notes: 'Limpieza simulada del bloque B2. No hubo contacto con un equipo real.',
    });
    next.interventionEvidence.push(
      {
        id: 'evd-before',
        organizationId: 'org-jujuy',
        interventionId: LIVE.interventionId,
        storageKey: 'local:b2-before.svg',
        mimeType: 'image/svg+xml',
        sha256: 'demo-before-b2',
        capturedAt: mission.startedAt ?? next.clockIso,
        phase: 'BEFORE',
      },
      {
        id: 'evd-after',
        organizationId: 'org-jujuy',
        interventionId: LIVE.interventionId,
        storageKey: 'local:b2-after.svg',
        mimeType: 'image/svg+xml',
        sha256: 'demo-after-b2',
        capturedAt: next.clockIso,
        phase: 'AFTER',
      },
    );
    next.recoveries.push({
      id: 'rec-live-b2',
      organizationId: 'org-jujuy',
      interventionId: LIVE.interventionId,
      modelRunId: SECTOR.modelRunId,
      assessedAt: next.clockIso,
      baselineMethod: 'mismo intervalo de 15 min, modelo meteorológico simulado',
      expectedRecoveredWh: before,
      verifiedRecoveredWh: recovered,
      confidenceNote: 'Comparación simulada. No certifica recuperación de campo.',
    });
    next.verifications.push({
      id: LIVE.verificationId,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      droneMissionId: LIVE.missionId,
      recoveryAssessmentId: 'rec-live-b2',
      status: recovered > 0 ? 'RECOVERED' : 'INCONCLUSIVE',
      assessedAt: next.clockIso,
      baselineMethod: 'intervalo equivalente antes/después',
      recoveredWh: recovered,
      evidenceSummary:
        'La mejora aparece al quitar la hipótesis de suciedad en el mismo intervalo. No es una medición posterior real.',
    });
  }
  advance(next, 1);
  return say(
    next,
    'Verificación registrada bajo condiciones equivalentes simuladas. No promete recuperación automática en la planta.',
  );
}

function rememberProof(snapshot: DemoSnapshot, proof: IngestProof): void {
  snapshot.scenario.lastProof = proof;
}

function addIntegrity(
  snapshot: DemoSnapshot,
  message: TelemetryMessage,
  eventType: NonNullable<IngestProof['eventType']>,
  status: NonNullable<IngestProof['status']>,
  reason: string,
): string {
  const id = `tev-${message.eventId}`;
  if (!snapshot.integrityEvents.some((item) => item.id === id)) {
    const event: TelemetryIntegrityEvent = {
      id,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      deviceIdentityId: 'dev-inv-03',
      telemetryChannelId: 'ch-inv-03',
      eventUid: message.eventId,
      eventType,
      status,
      observedAt: snapshot.clockIso,
      sourceObservedAt: message.observedAt,
      sequenceNumber: message.sequenceNumber,
      payloadSha256: `sha256-demo-${message.eventId}`,
      detail: reason,
      deviceId: message.deviceId,
      bootId: message.bootId,
      gatewayId: message.gatewayId,
      sourceEventId: message.eventId,
    };
    snapshot.integrityEvents.push(event);
    const assessmentId = `as-${message.eventId}`;
    const assessment: IntegrityAssessment = {
      id: assessmentId,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      integrityEventId: id,
      modelRunId: SECTOR.modelRunId,
      assessedAt: snapshot.clockIso,
      trustLevel: 'SUSPECT',
      ruleVersion: 'helioguard-dedup-1',
      reason,
    };
    snapshot.integrityAssessments.push(assessment);
    const securityId = `sec-${message.eventId}`;
    if (!snapshot.securityIncidents.some((item) => item.id === securityId)) {
      const security: SecurityIncident = {
        id: securityId,
        organizationId: 'org-jujuy',
        installationId: 'ins-puna',
        integrityAssessmentId: assessmentId,
        referenceCode: `SEC-${message.eventId}`,
        title:
          eventType === 'PHYSICAL_INCONSISTENCY'
            ? 'Valor físicamente improbable en INV-03'
            : 'Lectura repetida retenida en INV-03',
        status: 'INVESTIGATING',
        openedAt: snapshot.clockIso,
        closedAt: null,
        resolutionNote:
          eventType === 'PHYSICAL_INCONSISTENCY'
            ? 'En investigación. No se afirma intrusión ni autor.'
            : 'Retenida por idempotencia o secuencia. Puede ser retransmisión. No es un ataque demostrado.',
      };
      snapshot.securityIncidents.push(security);
    }
  }
  return id;
}

export function ingestMessage(snapshot: DemoSnapshot, message: TelemetryMessage): DemoSnapshot {
  const next = clone(snapshot);
  advance(next, 1);
  if (next.scenario.wanOffline) {
    next.scenario.edgeBuffer.push(message);
    const gateway = next.gateways.find((item) => item.id === message.gatewayId);
    if (gateway) {
      gateway.status = 'ONLINE';
      gateway.lastSeenAt = next.clockIso;
    }
    rememberProof(next, {
      at: next.clockIso,
      action: 'BUFFERED',
      eventType: null,
      status: null,
      trustLevel: 'UNKNOWN',
      reason: 'WAN cortada. El evento quedó en el outbox de la Raspberry simulada y todavía no entra a la nube.',
      matchedKeys: [],
      useInEnergy: false,
      cryptographicallyVerified: false,
      message,
      integrityEventId: null,
    });
    return say(next, 'Evento en buffer de borde. No se duplicará al sincronizar si ya fue ingerido.');
  }
  const outcome = evaluateIngest(next.scenario.ledger, message, {
    maxPowerW: SECTOR.limitW,
    uncertaintyW: SECTOR.uncertaintyW,
  });
  const already = next.scenario.ledger.some(
    (entry) => entry.gatewayId === message.gatewayId && entry.eventId === message.eventId,
  );
  if (!already) {
    next.scenario.ledger.push({
      gatewayId: message.gatewayId,
      eventId: message.eventId,
      deviceId: message.deviceId,
      bootId: message.bootId,
      sequenceNumber: message.sequenceNumber,
      acceptedForEnergy: outcome.useInEnergy,
      trustLevel: outcome.trustLevel,
      powerW: message.powerW,
      voltageV: message.voltageV,
    });
  }
  const integrityEventId =
    outcome.eventType && outcome.status ? addIntegrity(next, message, outcome.eventType, outcome.status, outcome.reason) : null;
  rememberProof(next, {
    at: next.clockIso,
    action: outcome.action,
    eventType: outcome.eventType,
    status: outcome.status,
    trustLevel: outcome.trustLevel,
    reason: outcome.reason,
    matchedKeys: outcome.matchedKeys,
    useInEnergy: outcome.useInEnergy,
    cryptographicallyVerified: false,
    message,
    integrityEventId,
  });
  return say(next, outcome.reason);
}

export function messageOriginal(snapshot: DemoSnapshot): TelemetryMessage {
  return {
    gatewayId: 'gw-1',
    eventId: 'evt-b2-0007',
    deviceId: 'inverter-03',
    bootId: 'boot-7',
    sequenceNumber: 7,
    powerW: snapshot.scenario.soiling ? SECTOR.soiledPowerW : SECTOR.healthyPowerW,
    observedAt: snapshot.clockIso,
    authenticSignature: false,
    channelCode: 'inv-03-ac-power',
  };
}

export function messageExactReplay(snapshot: DemoSnapshot): TelemetryMessage {
  return { ...messageOriginal(snapshot), observedAt: snapshot.clockIso };
}

export function messageSameSequence(snapshot: DemoSnapshot): TelemetryMessage {
  return {
    ...messageOriginal(snapshot),
    eventId: 'evt-b2-0007-copy',
    observedAt: snapshot.clockIso,
  };
}

export function messageNewBoot(snapshot: DemoSnapshot): TelemetryMessage {
  return {
    ...messageOriginal(snapshot),
    eventId: 'evt-b2-boot8',
    bootId: 'boot-8',
    sequenceNumber: 0,
    observedAt: snapshot.clockIso,
  };
}

export function messageImplausible(snapshot: DemoSnapshot): TelemetryMessage {
  return {
    gatewayId: 'gw-1',
    eventId: 'evt-b2-spike',
    deviceId: 'inverter-03',
    bootId: 'boot-7',
    sequenceNumber: 99,
    powerW: 8_000_000,
    observedAt: snapshot.clockIso,
    authenticSignature: false,
    channelCode: 'inv-03-ac-power',
  };
}

export function publishOriginal(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageOriginal(snapshot));
}

export function publishExactReplay(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageExactReplay(snapshot));
}

export function publishSameSequence(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageSameSequence(snapshot));
}

export function publishNewBoot(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageNewBoot(snapshot));
}

export function publishImplausible(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageImplausible(snapshot));
}

export function messageElectricalInjection(snapshot: DemoSnapshot): TelemetryMessage {
  return {
    gatewayId: 'gw-1',
    eventId: `evt-b2-vxi-${snapshot.scenario.ledger.length + 1}`,
    deviceId: 'inverter-03',
    bootId: 'boot-7',
    sequenceNumber: 200 + snapshot.scenario.ledger.length + 1,
    powerW: 82_400,
    voltageV: 380,
    currentA: 121,
    expectedPowerW: SECTOR.expectedPowerW,
    observedAt: snapshot.clockIso,
    authenticSignature: false,
    channelCode: 'inv-03-ac-power',
  };
}

export function publishElectricalInjection(snapshot: DemoSnapshot): DemoSnapshot {
  return ingestMessage(snapshot, messageElectricalInjection(snapshot));
}

export function publishFrozenStream(snapshot: DemoSnapshot): DemoSnapshot {
  let current = snapshot;
  const baseSeq = 300 + snapshot.scenario.ledger.length;
  for (let i = 0; i < 5; i += 1) {
    current = ingestMessage(current, {
      gatewayId: 'gw-1',
      eventId: `evt-b2-freeze-${baseSeq + i}`,
      deviceId: 'inverter-03',
      bootId: 'boot-7',
      sequenceNumber: baseSeq + i,
      powerW: SECTOR.healthyPowerW,
      voltageV: 380.2,
      currentA: Math.round((SECTOR.healthyPowerW / 380.2) * 100) / 100,
      expectedPowerW: SECTOR.expectedPowerW,
      observedAt: current.clockIso,
      authenticSignature: false,
      channelCode: 'inv-03-ac-power',
    });
  }
  return current;
}

export function cutWan(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  next.scenario.wanOffline = true;
  return say(next, 'WAN simulada cortada. El borde puede seguir encolando. La nube no recibe.');
}

export function restoreWan(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  if (!next.scenario.wanOffline && next.scenario.edgeBuffer.length === 0) {
    return say(next, 'La WAN ya estaba disponible y el outbox está vacío.');
  }
  next.scenario.wanOffline = false;
  const pending = [...next.scenario.edgeBuffer];
  next.scenario.edgeBuffer = [];
  next.scenario.syncCount += 1;
  let current = next;
  for (const message of pending) {
    current = ingestMessage(current, message);
  }
  current.scenario.notice = `Sincronización ${current.scenario.syncCount}: ${pending.length} evento(s) del outbox procesados una vez.`;
  return current;
}

export function acknowledgeIncident(snapshot: DemoSnapshot, incidentId: string): DemoSnapshot {
  const next = clone(snapshot);
  const incident = next.incidents.find((item) => item.id === incidentId);
  if (!incident) {
    return say(next, 'No está ese incidente en el tenant visible.');
  }
  if (incident.status === 'OPEN') {
    incident.status = 'ACKNOWLEDGED';
    incident.acknowledgedAt = next.clockIso;
  }
  incident.lastSeenAt = next.clockIso;
  next.incidentEvents.push({
    id: `iev-ack-${incidentId}-${next.incidentEvents.length}`,
    organizationId: incident.organizationId,
    incidentId,
    occurredAt: next.clockIso,
    eventCode: 'ACKNOWLEDGED',
    detail: 'Reconocido en la demo por el usuario de la sesión.',
  });
  return say(next, `Incidente ${incident.title} reconocido.`);
}

export function toggleString(snapshot: DemoSnapshot, stringId: string): DemoSnapshot {
  const next = clone(snapshot);
  const solarString = next.strings.find((item) => item.id === stringId);
  if (!solarString) {
    return say(next, 'Ese string no está en la copia local.');
  }
  solarString.active = !solarString.active;
  return say(next, `${solarString.stringCode} quedó ${solarString.active ? 'activo' : 'fuera de servicio'} en la demo.`);
}

export function saveAudit(snapshot: DemoSnapshot, answers: Record<string, string>, status: AuditStatus): DemoSnapshot {
  const next = clone(snapshot);
  const report = next.auditReports.find((item) => item.id === 'audit-puna');
  if (!report) {
    return say(next, 'No hay informe de auditoría en esta sesión.');
  }
  if (report.status === 'APPROVED') {
    return say(next, 'Solo lectura: el informe aprobado no se reabre desde la demo.');
  }
  for (const answer of next.auditAnswers.filter((item) => item.auditReportId === report.id)) {
    const text = answers[answer.id];
    if (text !== undefined) {
      answer.answerText = text;
      answer.answeredAt = next.clockIso;
      answer.compliant = text.trim().length > 0;
    }
  }
  if (status === 'SUBMITTED') {
    report.status = 'SUBMITTED';
    report.completedAt = next.clockIso;
    report.summary = 'Informe cerrado en la demo. Las evidencias siguen siendo sintéticas.';
  } else if (status === 'IN_PROGRESS') {
    report.status = 'IN_PROGRESS';
    report.completedAt = null;
  } else {
    report.status = status;
  }
  return say(next, status === 'SUBMITTED' ? 'Auditoría cerrada en la copia local.' : 'Auditoría guardada. Podés reabrirla si sigue enviada.');
}

export function reopenAudit(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const report = next.auditReports.find((item) => item.id === 'audit-puna');
  if (!report) {
    return say(next, 'No hay informe.');
  }
  if (report.status !== 'SUBMITTED') {
    return say(next, 'Solo se reabre un informe enviado.');
  }
  report.status = 'IN_PROGRESS';
  report.completedAt = null;
  return say(next, 'Informe reabierto.');
}

export function updateTariff(snapshot: DemoSnapshot, purchase: number, exported: number): DemoSnapshot {
  const next = clone(snapshot);
  const rate = next.tariffRates.find((item) => item.id === 'rate-puna');
  if (!rate || purchase < 0 || exported < 0) {
    return say(next, 'La tarifa demo no acepta valores negativos.');
  }
  rate.purchasePricePerKwh = purchase;
  rate.exportPricePerKwh = exported;
  recomputeSector(next);
  return say(next, 'Tarifa demostrativa actualizada. Los ARS no son una factura.');
}

export function acceptProposal(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const role = currentRole(next);
  if (!role || !APPROVERS.includes(role)) {
    return say(next, 'Solo lectura para este rol: aceptar el plan requiere OWNER o ADMIN.');
  }
  const proposal = next.proposals.find((item) => item.id === 'prop-puna');
  if (!proposal) {
    return say(next, 'No hay propuesta para esta instalación.');
  }
  if (proposal.acceptedAt) {
    return say(next, 'La propuesta ya estaba aceptada.');
  }
  proposal.acceptedAt = next.clockIso;
  return say(next, 'Propuesta aceptada en la demo. No genera un contrato real.');
}

export function acceptWork(snapshot: DemoSnapshot): DemoSnapshot {
  const next = clone(snapshot);
  const order = next.workOrders.find((item) => item.id === LIVE.workOrderId);
  const task = next.tasks.find((item) => item.id === LIVE.taskId);
  if (!order || !task) {
    return say(next, 'Todavía no hay orden viva. Creá la cadena desde el parque.');
  }
  if (order.status === 'CREATED' || order.status === 'ASSIGNED') {
    order.status = 'ACCEPTED';
    task.status = 'IN_PROGRESS';
    return say(next, 'Orden aceptada por el técnico demo.');
  }
  return say(next, `La orden ya está en ${order.status}.`);
}

export function setScenarioFlag(
  snapshot: DemoSnapshot,
  flag: 'windBlocked' | 'frostBlocked' | 'roofWorkBlocked' | 'manufacturerOk' | 'operatorOk' | 'siteOk' | 'readFailure',
  value: boolean,
): DemoSnapshot {
  const next = clone(snapshot);
  next.scenario[flag] = value;
  return say(next, 'Condición de la demo actualizada.');
}

export function selectArray(snapshot: DemoSnapshot, arrayId: string): DemoSnapshot {
  const next = clone(snapshot);
  next.scenario.selectedArrayId = arrayId;
  return next;
}

export function setIntervalComparable(snapshot: DemoSnapshot, intervalId: string, comparable: boolean): DemoSnapshot {
  const next = clone(snapshot);
  const interval = next.intervals.find((item) => item.id === intervalId);
  if (!interval) {
    return say(next, 'Ese intervalo no está en la serie simulada.');
  }
  interval.comparable = comparable;
  if (intervalId === SECTOR.intervalId) {
    recomputeSector(next);
  }
  return say(
    next,
    comparable ? 'El intervalo vuelve a ser comparable.' : 'Sin estimación confiable: el período dejó de ser comparable.',
  );
}
