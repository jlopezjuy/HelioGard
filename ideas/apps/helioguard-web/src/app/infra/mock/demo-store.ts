import { Injectable, computed, signal } from '@angular/core';
import { activeMembership, installationScope, visibleInstallations } from '../../core/domain/access';
import type {
  AuditStatus,
  DemoState,
  DroneMissionKind,
  Incident,
  MissionAuthorizationStatus,
  ScenarioId,
  TelemetrySample,
  WorkOrderStatus,
  WorkTaskStatus,
} from '../../core/domain/model';
import {
  canTransitionMission,
  canTransitionWorkOrder,
  classifySample,
  cleaningBlockReason,
  lossAmountArs,
  rollupInstallation,
  splitLostEnergy,
  weatherFor,
} from '../../core/domain/rules';
import { buildSeed } from './seed';

export const STORAGE_KEY = 'helioguard.demo.v4';
const LIVE_RUN = 'run-puna-live';
const LIVE_LOSS = 'loss-puna-live';
const LIVE_ECO = 'eco-puna-live';
const SOILING_KEY = 'soiling-puna-a2';
const LIVE_START = '2026-11-22T13:00:00.000Z';
const LIVE_END = '2026-11-22T15:00:00.000Z';

@Injectable({ providedIn: 'root' })
export class DemoStore {
  private readonly raw = signal<DemoState>(readState());
  readonly state = this.raw.asReadonly();
  readonly user = computed(() => this.raw().users.find((item) => item.id === this.raw().activeUserId) ?? null);
  readonly organization = computed(
    () => this.raw().organizations.find((item) => item.id === this.raw().activeOrganizationId) ?? null,
  );
  readonly membership = computed(() => activeMembership(this.raw()));
  readonly role = computed(() => this.membership()?.role ?? null);
  readonly installations = computed(() => visibleInstallations(this.raw()));
  readonly selected = computed(() => {
    const visible = this.installations();
    return visible.find((item) => item.id === this.raw().selectedInstallationId) ?? visible[0] ?? null;
  });
  readonly scope = computed(() => {
    const installation = this.selected();
    return installation ? installationScope(this.raw(), installation) : 'NONE';
  });
  readonly rollup = computed(() => rollupInstallation(this.raw().scenario, this.raw().recoveryApplied));
  readonly acceptedCount = computed(() => this.raw().samples.filter((sample) => sample.accepted).length);

  reset(): void {
    const fresh = buildSeed();
    this.raw.set(fresh);
    this.persist(fresh);
  }

  exportJson(): string {
    return JSON.stringify(this.raw(), null, 2);
  }

  importJson(text: string): string | null {
    try {
      const parsed = JSON.parse(text) as DemoState;
      if (parsed.version !== 4 || parsed.seed !== 'helioguard-hackaton-v4' || !Array.isArray(parsed.organizations)) {
        return 'El archivo no es un estado HelioGuard v4.';
      }
      if (parsed.organizations.length < 3) {
        return 'Faltan las tres organizaciones de la demo.';
      }
      this.raw.set(parsed);
      this.persist(parsed);
      return null;
    } catch {
      return 'JSON inválido.';
    }
  }

  setUser(userId: string): void {
    this.patch((draft) => {
      const membership = draft.memberships.find((item) => item.userId === userId && item.status === 'ACTIVE');
      if (!membership) {
        draft.notice = 'Esa persona no tiene una membresía activa.';
        return;
      }
      draft.activeUserId = userId;
      draft.activeOrganizationId = membership.organizationId;
      const visible = visibleInstallations(draft);
      if (!visible.some((item) => item.id === draft.selectedInstallationId)) {
        draft.selectedInstallationId = visible[0]?.id ?? draft.selectedInstallationId;
      }
      draft.notice = null;
    });
  }

  selectInstallation(id: string): void {
    this.patch((draft) => {
      const installation = draft.installations.find((item) => item.id === id);
      if (!installation || installationScope(draft, installation) === 'NONE') {
        draft.notice = 'Esta sesión demo no tiene esa instalación en su ámbito.';
        return;
      }
      draft.selectedInstallationId = id;
    });
  }

  setOffline(offline: boolean): void {
    this.patch((draft) => {
      draft.offline = offline;
      draft.notice = offline
        ? 'Enlace WAN simulado cortado. El borde sigue aceptando formularios en este navegador.'
        : 'Enlace WAN simulado restaurado.';
      if (!offline) {
        this.syncOutbox(draft);
      }
    });
  }

  activateScenario(scenario: ScenarioId): void {
    this.patch((draft) => {
      draft.scenario = scenario;
      draft.recoveryApplied = false;
      const gateway = draft.gateways.find((item) => item.id === 'gw-puna');
      if (gateway) {
        gateway.status = scenario === 'offline-gateway' ? 'OFFLINE' : 'ONLINE';
        gateway.lastSeenAt = draft.clockIso;
      }
      const inverter = draft.equipment.find((item) => item.id === 'eq-inv-03');
      if (inverter) {
        inverter.status = scenario === 'inverter' ? 'FAILED' : 'ACTIVE';
      }
      this.writeLiveEnergy(draft);
      if (scenario === 'soiling') {
        this.ensureIncident(draft, {
          id: 'inc-soiling',
          dedupKey: SOILING_KEY,
          organizationId: 'org-jujuy',
          installationId: 'ins-puna',
          alertRuleId: 'rule-deficit',
          title: 'Puna Norte · bloque A2 por debajo de lo esperado',
          severity: 'MEDIUM',
          detail: 'Hipótesis preliminar de polvo. La causa no está confirmada: queda un residual UNKNOWN.',
        });
        draft.notice = 'Sector A2 marcado. El déficit está en Wh del intervalo 10:00–12:00 y el monto es demostrativo.';
      } else if (scenario === 'cloud') {
        draft.notice = 'Nube pasajera simulada. El recorte es meteorológico: no se abre limpieza.';
      } else if (scenario === 'inverter') {
        this.ensureIncident(draft, {
          id: 'inc-inv3',
          dedupKey: 'inv3-down',
          organizationId: 'org-jujuy',
          installationId: 'ins-puna',
          alertRuleId: 'rule-deficit',
          title: 'INV-03 sin entrega',
          severity: 'HIGH',
          detail: 'Falla de equipo. La recomendación es inspección técnica, no lavar el bloque.',
        });
        draft.notice = 'INV-03 en falla simulada. No se propone limpieza.';
      } else if (scenario === 'frost') {
        draft.notice = 'Helada en la casa híbrida. Una misión de limpieza con agua queda bloqueada.';
      } else if (scenario === 'hail') {
        draft.notice = 'Granizo y viento sobre el límite del dron. El despacho no sale.';
      } else if (scenario === 'smoke') {
        draft.notice = 'Humo regional simulado. La baja es climática, no un mapa térmico.';
      } else if (scenario === 'offline-gateway') {
        draft.notice = 'Gateway de Puna sin enlace. No hay déficit confiable porque falta la medición.';
      } else {
        draft.notice = 'Parque en banda normal simulada.';
      }
    });
  }

  acknowledge(incidentId: string): void {
    this.patch((draft) => {
      const incident = this.writableIncident(draft, incidentId);
      if (!incident || incident.status !== 'OPEN') {
        return;
      }
      incident.status = 'ACKNOWLEDGED';
      incident.acknowledgedAt = draft.clockIso;
      this.pushIncidentEvent(draft, incident, 'ACK', 'Incidente reconocido en la demo.');
    });
  }

  openWorkOrder(incidentId: string): string | null {
    let created: string | null = null;
    this.patch((draft) => {
      const incident = this.writableIncident(draft, incidentId);
      if (!incident) {
        draft.notice = 'Ese incidente no está en el ámbito de esta sesión.';
        return;
      }
      const existing = draft.workOrders.find((order) => order.incidentId === incident.id);
      if (existing) {
        created = existing.id;
        draft.notice = 'La orden ya existía. No se duplicó.';
        return;
      }
      const id = this.nid(draft, 'wo');
      draft.workOrders.unshift({
        id,
        organizationId: incident.organizationId,
        installationId: incident.installationId,
        incidentId: incident.id,
        subscriptionId: 'sub-puna',
        assignedMembershipId: null,
        referenceCode: `OT-${draft.seq}`,
        title: `Atención · ${incident.title}`,
        status: 'CREATED',
        priority: incident.severity,
        openedAt: draft.clockIso,
        scheduledAt: null,
        closedAt: null,
      });
      draft.tasks.push({
        id: this.nid(draft, 'task'),
        organizationId: incident.organizationId,
        workOrderId: id,
        description: 'Confirmar bloque, clima y método antes de cualquier intervención.',
        status: 'TODO',
        sortOrder: 1,
      });
      incident.status = 'IN_PROGRESS';
      this.pushIncidentEvent(draft, incident, 'WORK_ORDER', `Orden ${id} creada.`);
      created = id;
      this.queue(draft, `work-order:${id}`);
    });
    return created;
  }

  moveWorkOrder(workOrderId: string, to: WorkOrderStatus): void {
    this.patch((draft) => {
      const order = draft.workOrders.find((item) => item.id === workOrderId);
      if (!order || !this.canMaintain(draft, order.installationId)) {
        return;
      }
      if (!canTransitionWorkOrder(order.status, to)) {
        draft.notice = `No se puede pasar la orden de ${order.status} a ${to}.`;
        return;
      }
      order.status = to;
      if (to === 'ASSIGNED') {
        order.assignedMembershipId = 'mem-tech';
      }
      if (to === 'VERIFIED' || to === 'CANCELLED') {
        order.closedAt = draft.clockIso;
      }
      this.queue(draft, `work-order:${order.id}`);
    });
  }

  setTask(taskId: string, status: WorkTaskStatus): void {
    this.patch((draft) => {
      const task = draft.tasks.find((item) => item.id === taskId);
      if (!task || !this.canMaintain(draft, draft.workOrders.find((order) => order.id === task.workOrderId)?.installationId ?? '')) {
        return;
      }
      task.status = status;
      this.queue(draft, `task:${task.id}`);
    });
  }

  proposeMission(workOrderId: string, kind: DroneMissionKind): string | null {
    let missionId: string | null = null;
    this.patch((draft) => {
      const order = draft.workOrders.find((item) => item.id === workOrderId);
      if (!order || !this.owns(draft, order.organizationId)) {
        draft.notice = 'La orden no está disponible para esta organización.';
        return;
      }
      if (kind === 'CLEANING' && order.title.toLowerCase().includes('inv-03')) {
        draft.notice = 'Una falla de inversor no se resuelve con limpieza.';
        return;
      }
      const id = this.nid(draft, 'mission');
      draft.missions.unshift({
        id,
        organizationId: order.organizationId,
        installationId: order.installationId,
        workOrderId: order.id,
        droneAssetId: 'drone-01',
        providerOrganizationId: 'org-jujuy',
        missionCode: `HG-${draft.seq}`,
        kind,
        status: 'DRAFT',
        proposedAt: draft.clockIso,
        startedAt: null,
        completedAt: null,
        safetyChecklistSummary: 'Borrador. Checklist todavía vacío.',
      });
      draft.authorizations.unshift({
        id: this.nid(draft, 'auth'),
        organizationId: order.organizationId,
        missionId: id,
        authorizedByMembershipId: null,
        status: 'PENDING',
        decidedAt: null,
        expiresAt: null,
        operatorReference: '',
        decisionNote: 'Misión simulada — la aprobación no representa un permiso operativo real.',
      });
      missionId = id;
      draft.notice = 'Misión en DRAFT. Falta revisión de seguridad y autorización.';
    });
    return missionId;
  }

  reviewSafety(missionId: string): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      if (!mission || !canTransitionMission(mission.status, 'SAFETY_REVIEW')) {
        draft.notice = 'La revisión solo parte de DRAFT.';
        return;
      }
      mission.status = 'SAFETY_REVIEW';
      const site = this.siteOf(draft, mission.installationId);
      const weather = weatherFor(draft.scenario, site?.roof ?? false);
      const drone = draft.drones.find((item) => item.id === mission.droneAssetId);
      const block = cleaningBlockReason({
        kind: mission.kind,
        frost: weather.frost,
        windMs: weather.windMs,
        maxWindMs: drone?.maximumWindMs ?? 12,
        hail: weather.hail,
        roof: weather.roof,
        authorization: 'APPROVED',
      });
      const operational = block && !block.includes('autorización') ? block : null;
      mission.safetyChecklistSummary = operational
        ? `Bloqueado: ${operational}`
        : 'Clima, fabricante, operador de demo y sitio revisados. Pendiente de autorización humana.';
      if (operational) {
        draft.notice = operational;
        return;
      }
      if (!canTransitionMission(mission.status, 'AWAITING_AUTHORIZATION')) {
        return;
      }
      mission.status = 'AWAITING_AUTHORIZATION';
      draft.notice = 'Checklist aceptado en la simulación. Sigue faltando la autorización.';
    });
  }

  decideMission(missionId: string, status: Extract<MissionAuthorizationStatus, 'APPROVED' | 'REJECTED'>): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      const auth = draft.authorizations.find((item) => item.missionId === missionId);
      if (!mission || !auth || mission.status !== 'AWAITING_AUTHORIZATION') {
        draft.notice = 'Solo se decide una misión que ya está en AWAITING_AUTHORIZATION.';
        return;
      }
      auth.status = status;
      auth.decidedAt = draft.clockIso;
      auth.authorizedByMembershipId = draft.memberships.find((item) => item.userId === draft.activeUserId)?.id ?? null;
      auth.operatorReference = 'Operador demo';
      auth.decisionNote =
        status === 'APPROVED'
          ? 'Aprobación de la demo. No habilita un despegue real.'
          : 'Rechazada en la demo.';
      if (status === 'APPROVED' && canTransitionMission(mission.status, 'AUTHORIZED')) {
        mission.status = 'AUTHORIZED';
      }
      if (status === 'REJECTED') {
        mission.status = 'ABORTED';
      }
    });
  }

  dispatch(missionId: string): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      if (!mission) {
        return;
      }
      const auth = draft.authorizations.find((item) => item.missionId === missionId);
      const site = this.siteOf(draft, mission.installationId);
      const weather = weatherFor(draft.scenario, site?.roof ?? false);
      const drone = draft.drones.find((item) => item.id === mission.droneAssetId);
      const reason = cleaningBlockReason({
        kind: mission.kind,
        frost: weather.frost,
        windMs: weather.windMs,
        maxWindMs: drone?.maximumWindMs ?? 12,
        hail: weather.hail,
        roof: weather.roof,
        authorization: auth?.status ?? null,
      });
      if (reason || !canTransitionMission(mission.status, 'DISPATCHED')) {
        draft.notice = reason ?? 'El despacho solo sale desde AUTHORIZED.';
        return;
      }
      mission.status = 'DISPATCHED';
      mission.startedAt = draft.clockIso;
      draft.notice = 'Despacho simulado registrado. El vuelo todavía no empezó.';
    });
  }

  fly(missionId: string): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      if (!mission || !canTransitionMission(mission.status, 'IN_FLIGHT')) {
        draft.notice = 'El vuelo simulado solo sigue a un despacho autorizado.';
        return;
      }
      mission.status = 'IN_FLIGHT';
      draft.notice = 'Recorrido simulado sobre el bloque. No hay aeronave real.';
    });
  }

  completeMission(missionId: string): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      if (!mission || !canTransitionMission(mission.status, 'COMPLETED')) {
        draft.notice = 'Primero tiene que recorrer el vuelo simulado.';
        return;
      }
      mission.status = 'COMPLETED';
      mission.completedAt = draft.clockIso;
      const order = draft.workOrders.find((item) => item.id === mission.workOrderId);
      if (order && canTransitionWorkOrder(order.status, 'ASSIGNED') && order.status === 'CREATED') {
        order.status = 'ASSIGNED';
      }
      const interventionId = this.nid(draft, 'int');
      draft.interventions.push({
        id: interventionId,
        organizationId: mission.organizationId,
        workOrderId: mission.workOrderId ?? '',
        type: mission.kind === 'CLEANING' ? 'CLEANING' : 'INSPECTION',
        startedAt: mission.startedAt,
        finishedAt: draft.clockIso,
        workCost: mission.kind === 'CLEANING' ? 450000 : 180000,
        currencyCode: 'ARS',
        notes: 'Intervención simulada. El costo es ilustrativo.',
      });
      draft.interventionEvidence.push({
        id: this.nid(draft, 'evd'),
        organizationId: mission.organizationId,
        interventionId,
        storageKey: `local://demo/${interventionId}.svg`,
        mimeType: 'image/svg+xml',
        sha256: 'demo-fnv-not-a-sha256',
        capturedAt: draft.clockIso,
      });
      draft.verifications.push({
        id: this.nid(draft, 'ver'),
        organizationId: mission.organizationId,
        installationId: mission.installationId,
        droneMissionId: mission.id,
        recoveryAssessmentId: null,
        status: 'PENDING',
        assessedAt: draft.clockIso,
        baselineMethod: 'mismo-intervalo-horario-simulado',
        recoveredWh: null,
        evidenceSummary: 'Falta comparar antes y después en condiciones equivalentes.',
      });
      draft.notice = 'Misión completa en la simulación. La verificación todavía no afirma recuperación.';
    });
  }

  verify(missionId: string): void {
    this.patch((draft) => {
      const mission = draft.missions.find((item) => item.id === missionId);
      const verification = draft.verifications.find((item) => item.droneMissionId === missionId);
      if (!mission || !verification || mission.status !== 'COMPLETED') {
        draft.notice = 'No hay una misión completa para verificar.';
        return;
      }
      if (draft.scenario !== 'soiling') {
        verification.status = 'INCONCLUSIVE';
        verification.evidenceSummary = 'La comparación no es concluyente fuera del escenario de suciedad sectorizada.';
        draft.notice = 'Verificación inconclusa: el escenario activo no permite atribuir una recuperación.';
        return;
      }
      const before = rollupInstallation('soiling', false);
      const after = rollupInstallation('soiling', true);
      const beforeDeficit = typeof before.deficit === 'number' ? before.deficit : 0;
      const afterDeficit = typeof after.deficit === 'number' ? after.deficit : 0;
      const recovered = Math.max(0, beforeDeficit - afterDeficit);
      draft.recoveryApplied = true;
      this.writeLiveEnergy(draft);
      verification.status = recovered > 0 ? 'RECOVERED' : 'NOT_RECOVERED';
      verification.recoveredWh = recovered;
      verification.assessedAt = draft.clockIso;
      verification.evidenceSummary = `Comparación simulada del mismo horario. Antes ${Math.round(beforeDeficit)} Wh, después ${Math.round(afterDeficit)} Wh. No es una medición de campo.`;
      const intervention = draft.interventions.find((item) => item.workOrderId === mission.workOrderId);
      if (intervention) {
        draft.recoveries.push({
          id: this.nid(draft, 'rec'),
          organizationId: mission.organizationId,
          interventionId: intervention.id,
          modelRunId: LIVE_RUN,
          assessedAt: draft.clockIso,
          baselineMethod: verification.baselineMethod,
          expectedRecoveredWh: beforeDeficit,
          verifiedRecoveredWh: recovered,
          confidenceNote: 'Confianza baja: un solo intervalo simulado, sin piranómetro.',
        });
        verification.recoveryAssessmentId = draft.recoveries[draft.recoveries.length - 1]?.id ?? null;
      }
      const incident = draft.incidents.find((item) => item.dedupKey === SOILING_KEY);
      if (incident) {
        incident.status = 'RESOLVED';
        incident.resolvedAt = draft.clockIso;
      }
      draft.notice = 'Verificación guardada. La recuperación es del modelo demo, no una promesa de campo.';
    });
  }

  replay(): void {
    this.patch((draft) => {
      const originalId = 'evt-original-1542';
      const accepted = draft.samples.filter((sample) => sample.accepted);
      if (!accepted.some((sample) => sample.eventId === originalId)) {
        this.ingest(draft, {
          eventId: originalId,
          bootId: 'boot-A',
          sequenceNumber: 1542,
          powerW: 180_000,
        });
      }
      this.ingest(draft, {
        eventId: `evt-replay-${draft.seq + 1}`,
        bootId: 'boot-A',
        sequenceNumber: 1542,
        powerW: 180_000,
      });
      draft.notice = 'Misma secuencia boot-A/1542 con otro eventId. Quedó retenida: el contador de mediciones válidas no sube.';
    });
  }

  injectFalse(): void {
    this.patch((draft) => {
      this.ingest(draft, {
        eventId: `evt-inject-${draft.seq + 1}`,
        bootId: 'boot-A',
        sequenceNumber: 2000 + draft.samples.length,
        powerW: 8_000_000,
      });
      draft.notice = 'Valor por encima del límite físico. Quedó UNDER_REVIEW. No se afirma una intrusión.';
    });
  }

  legitimateReboot(): void {
    this.patch((draft) => {
      this.ingest(draft, {
        eventId: `evt-boot-${draft.seq + 1}`,
        bootId: 'boot-B',
        sequenceNumber: 0,
        powerW: 175_000,
      });
      draft.notice = 'bootId nuevo y secuencia 0. Se acepta como reinicio, sin evento de replay.';
    });
  }

  saveAnswer(answerId: string, compliant: boolean | null, text: string): void {
    this.patch((draft) => {
      const answer = draft.auditAnswers.find((item) => item.id === answerId);
      const report = draft.auditReports.find((item) => item.id === answer?.auditReportId);
      if (!answer || !report || report.status === 'APPROVED') {
        draft.notice = 'El informe cerrado no se edita. Reabrilo primero.';
        return;
      }
      answer.compliant = compliant;
      answer.answerText = text;
      answer.answeredAt = draft.clockIso;
      if (report.status === 'DRAFT') {
        report.status = 'IN_PROGRESS';
      }
      this.queue(draft, `audit:${answer.id}`);
    });
  }

  setAuditStatus(reportId: string, status: AuditStatus): void {
    this.patch((draft) => {
      const report = draft.auditReports.find((item) => item.id === reportId);
      if (!report || !this.owns(draft, report.organizationId)) {
        return;
      }
      const allowed =
        (report.status === 'IN_PROGRESS' && (status === 'SUBMITTED' || status === 'DRAFT')) ||
        (report.status === 'SUBMITTED' && (status === 'APPROVED' || status === 'REJECTED' || status === 'IN_PROGRESS')) ||
        (report.status === 'APPROVED' && status === 'IN_PROGRESS') ||
        (report.status === 'DRAFT' && status === 'IN_PROGRESS');
      if (!allowed) {
        draft.notice = 'Esa transición de informe no está permitida.';
        return;
      }
      if (status === 'SUBMITTED') {
        const pending = draft.auditAnswers.some(
          (answer) =>
            answer.auditReportId === report.id &&
            draft.auditQuestions.find((question) => question.id === answer.auditQuestionId)?.mandatory &&
            answer.answeredAt === null,
        );
        if (pending) {
          draft.notice = 'Faltan respuestas obligatorias.';
          return;
        }
      }
      report.status = status;
      report.completedAt = status === 'APPROVED' ? draft.clockIso : null;
      if (status === 'APPROVED' && draft.auditFindings.every((finding) => finding.auditReportId !== report.id)) {
        const findingId = this.nid(draft, 'find');
        draft.auditFindings.push({
          id: findingId,
          organizationId: report.organizationId,
          auditReportId: report.id,
          title: 'Depósito a confirmar en A2',
          severity: 'MEDIUM',
          description: 'La telemetría sugiere un sector, la visita todavía no lo confirma.',
          recommendation: 'Inspección visual antes de contratar limpieza.',
          resolvedAt: null,
        });
        draft.auditEvidence.push({
          id: this.nid(draft, 'aev'),
          organizationId: report.organizationId,
          findingId,
          storageKey: 'local://audit/a2-nota.txt',
          mimeType: 'text/plain',
          sha256: 'demo-fnv-audit',
          capturedAt: draft.clockIso,
        });
      }
      this.queue(draft, `report:${report.id}`);
    });
  }

  updateTariff(rateId: string, purchase: number, exported: number): void {
    this.patch((draft) => {
      const rate = draft.tariffRates.find((item) => item.id === rateId);
      if (!rate || !this.owns(draft, rate.organizationId)) {
        draft.notice = 'Esa tarifa no pertenece a la organización activa.';
        return;
      }
      if (purchase < 0 || exported < 0) {
        draft.notice = 'Los precios no pueden ser negativos.';
        return;
      }
      rate.purchasePricePerKwh = purchase;
      rate.exportPricePerKwh = exported;
      this.writeLiveEnergy(draft);
      draft.notice = 'Tarifa demo actualizada. Los ARS siguen siendo ficticios.';
    });
  }

  acceptProposal(proposalId: string): void {
    this.patch((draft) => {
      const proposal = draft.proposals.find((item) => item.id === proposalId);
      if (!proposal || !this.owns(draft, proposal.organizationId)) {
        return;
      }
      if (proposal.acceptedAt) {
        draft.notice = 'La propuesta ya estaba aceptada.';
        return;
      }
      proposal.acceptedAt = draft.clockIso;
      if (proposal.planVersionId) {
        const version = draft.planVersions.find((item) => item.id === proposal.planVersionId);
        if (version && !version.publishedAt) {
          version.publishedAt = draft.clockIso;
          version.versionCode = '2026.2';
        }
        draft.subscriptions.push({
          id: this.nid(draft, 'sub'),
          organizationId: proposal.organizationId,
          installationId: proposal.installationId,
          planVersionId: proposal.planVersionId,
          serviceAgreementId: 'agr-puna',
          status: 'PROPOSED',
          activatedAt: null,
          expiresAt: proposal.validUntil,
          agreedAmount: proposal.amount,
          currencyCode: proposal.currencyCode,
        });
      }
      draft.notice = 'Propuesta aceptada en la demo. La suscripción queda PROPOSED, no es un contrato.';
    });
  }

  toggleString(stringId: string): void {
    this.patch((draft) => {
      const item = draft.strings.find((entry) => entry.id === stringId);
      if (!item || !this.owns(draft, item.organizationId)) {
        draft.notice = 'String fuera de ámbito.';
        return;
      }
      item.active = !item.active;
      this.queue(draft, `string:${item.id}`);
    });
  }

  addNote(workOrderId: string, note: string): void {
    this.patch((draft) => {
      const order = draft.workOrders.find((item) => item.id === workOrderId);
      if (!order || !note.trim()) {
        return;
      }
      const intervention = draft.interventions.find((item) => item.workOrderId === order.id);
      if (intervention) {
        intervention.notes = note.trim();
      } else if (this.owns(draft, order.organizationId) || installationScope(draft, draft.installations.find((item) => item.id === order.installationId)!) === 'MAINTENANCE') {
        draft.interventions.push({
          id: this.nid(draft, 'int'),
          organizationId: order.organizationId,
          workOrderId: order.id,
          type: 'INSPECTION',
          startedAt: draft.clockIso,
          finishedAt: null,
          workCost: null,
          currencyCode: 'ARS',
          notes: note.trim(),
        });
      }
      this.queue(draft, `note:${workOrderId}`);
    });
  }

  private ingest(
    draft: DemoState,
    input: { eventId: string; bootId: string; sequenceNumber: number; powerW: number },
  ): void {
    const prior = draft.samples
      .filter((sample) => sample.accepted)
      .map((sample) => ({
        gatewayId: sample.gatewayId,
        deviceId: sample.deviceId,
        eventId: sample.eventId,
        bootId: sample.bootId,
        sequenceNumber: sample.sequenceNumber,
        powerW: sample.powerW,
      }));
    const identity = {
      gatewayId: 'gw-puna',
      deviceId: 'inverter-03',
      ...input,
    };
    if (draft.samples.some((sample) => sample.gatewayId === identity.gatewayId && sample.eventId === identity.eventId)) {
      return;
    }
    const result = classifySample(prior, identity);
    const sample: TelemetrySample = {
      id: this.nid(draft, 'smp'),
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      gatewayId: identity.gatewayId,
      deviceId: identity.deviceId,
      eventId: identity.eventId,
      bootId: identity.bootId,
      sequenceNumber: identity.sequenceNumber,
      powerW: identity.powerW,
      observedAt: draft.clockIso,
      accepted: result.decision === 'ACCEPTED',
      decision: result.decision,
      legitimateReboot: result.legitimateReboot,
    };
    if (draft.offline) {
      draft.outbox.push({
        id: this.nid(draft, 'out'),
        gatewayId: 'gw-puna',
        eventId: sample.eventId,
        queuedAt: draft.clockIso,
        syncedAt: null,
      });
    }
    draft.samples.unshift(sample);
    if (result.decision === 'ACCEPTED') {
      return;
    }
    const eventType = result.decision === 'PHYSICAL_INCONSISTENCY' ? 'PHYSICAL_INCONSISTENCY' : result.decision === 'REPLAY' ? 'REPLAY' : 'DUPLICATE_EVENT';
    const status = result.decision === 'PHYSICAL_INCONSISTENCY' ? 'UNDER_REVIEW' : 'QUARANTINED';
    const eventId = this.nid(draft, 'tie');
    draft.integrityEvents.unshift({
      id: eventId,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      deviceIdentityId: 'dev-inv3',
      telemetryChannelId: 'ch-inv3',
      eventUid: `uid-${sample.eventId}`,
      eventType,
      status,
      observedAt: draft.clockIso,
      sourceObservedAt: draft.clockIso,
      sequenceNumber: sample.sequenceNumber,
      bootId: sample.bootId,
      payloadSha256: `demo-fnv-${sample.eventId}`,
      detail:
        result.decision === 'REPLAY'
          ? `eventId ${sample.eventId} es nuevo, pero deviceId+bootId+sequenceNumber ya fue aceptado. No se suma como medición válida.`
          : result.decision === 'PHYSICAL_INCONSISTENCY'
            ? '8 MW contra un inversor de 250 kW más 8 % de incertidumbre. Sospecha de dato o sensor. Sin atribución de ataque.'
            : 'eventId repetido. Idempotencia.',
    });
    const assessmentId = this.nid(draft, 'ias');
    draft.integrityAssessments.unshift({
      id: assessmentId,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      integrityEventId: eventId,
      modelRunId: null,
      assessedAt: draft.clockIso,
      trustLevel: 'SUSPECT',
      ruleVersion: 'dedup-physical-1',
      reason: draft.integrityEvents[0]?.detail ?? '',
    });
    if (result.decision === 'PHYSICAL_INCONSISTENCY' || result.decision === 'REPLAY') {
      draft.securityIncidents.unshift({
        id: this.nid(draft, 'sec'),
        organizationId: 'org-jujuy',
        installationId: 'ins-puna',
        integrityAssessmentId: assessmentId,
        referenceCode: `SEC-${draft.seq}`,
        title: result.decision === 'REPLAY' ? 'Secuencia reutilizada' : 'Potencia físicamente inconsistente',
        status: result.decision === 'REPLAY' ? 'TRIAGE' : 'INVESTIGATING',
        openedAt: draft.clockIso,
        closedAt: null,
        resolutionNote: 'Investigación abierta. No hay prueba de intrusión ni de identidad del emisor.',
      });
    }
  }

  private writeLiveEnergy(draft: DemoState): void {
    const rollup = rollupInstallation(draft.scenario, draft.recoveryApplied);
    const deficit = typeof rollup.deficit === 'number' ? rollup.deficit : 0;
    const run = draft.modelRuns.find((item) => item.id === LIVE_RUN);
    const nextRun = {
      id: LIVE_RUN,
      organizationId: 'org-jujuy',
      installationId: 'ins-puna',
      modelName: 'helioguard-clearsky-demo',
      modelVersion: '0.4',
      periodStart: LIVE_START,
      periodEnd: LIVE_END,
      expectedWh: Math.round(rollup.expectedWh),
      actualWh: rollup.actualWh === null ? null : Math.round(rollup.actualWh),
      coveragePct: rollup.comparable ? 1 : 0,
      inputFingerprint: `scenario:${draft.scenario}:recovered:${draft.recoveryApplied}`,
      calculatedAt: draft.clockIso,
      comparable: rollup.comparable,
    };
    if (run) {
      Object.assign(run, nextRun);
    } else {
      draft.modelRuns.unshift(nextRun);
    }
    const loss = draft.lossAssessments.find((item) => item.id === LIVE_LOSS);
    const nextLoss = {
      id: LIVE_LOSS,
      organizationId: 'org-jujuy',
      modelRunId: LIVE_RUN,
      totalLossWh: deficit,
      lossPct: rollup.expectedWh > 0 ? Math.round((deficit / rollup.expectedWh) * 1000) / 10 : null,
      methodology: rollup.comparable ? 'integracion-potencia-15min' : 'sin-medicion',
      assessedAt: draft.clockIso,
      validationStatus: rollup.comparable ? 'PRELIMINAR' : 'SIN_ESTIMACION',
    };
    if (loss) {
      Object.assign(loss, nextLoss);
    } else {
      draft.lossAssessments.unshift(nextLoss);
    }
    draft.lossCauses = draft.lossCauses.filter((cause) => cause.lossAssessmentId !== LIVE_LOSS);
    rollup.causes.forEach((cause, index) => {
      draft.lossCauses.push({
        id: `cause-live-${index}`,
        organizationId: 'org-jujuy',
        lossAssessmentId: LIVE_LOSS,
        cause: cause.cause,
        estimatedWh: cause.estimatedWh,
        method: cause.method,
        evidenceNote: cause.evidenceNote,
      });
    });
    const rate = draft.tariffRates.find((item) => item.id === 'rate-puna');
    const split = splitLostEnergy(deficit);
    const amount = rate
      ? lossAmountArs({
          selfConsumptionLostWh: split.selfConsumptionLostWh,
          exportedLostWh: split.exportedLostWh,
          purchasePricePerKwh: rate.purchasePricePerKwh,
          exportPricePerKwh: rate.exportPricePerKwh,
        })
      : 0;
    const eco = draft.economicAssessments.find((item) => item.id === LIVE_ECO);
    const nextEco = {
      id: LIVE_ECO,
      organizationId: 'org-jujuy',
      lossAssessmentId: LIVE_LOSS,
      tariffRateId: 'rate-puna',
      assessedAt: draft.clockIso,
      currencyCode: 'ARS',
      selfConsumptionLostWh: split.selfConsumptionLostWh,
      exportedLostWh: split.exportedLostWh,
      estimatedLossAmount: amount,
      tariffSnapshotNote: 'Demostración. Compra evitada e inyección con precios ficticios. No es una liquidación.',
    };
    if (eco) {
      Object.assign(eco, nextEco);
    } else {
      draft.economicAssessments.unshift(nextEco);
    }
  }

  private ensureIncident(
    draft: DemoState,
    input: {
      id: string;
      dedupKey: string;
      organizationId: string;
      installationId: string;
      alertRuleId: string;
      title: string;
      severity: Incident['severity'];
      detail: string;
    },
  ): void {
    const found = draft.incidents.find((item) => item.dedupKey === input.dedupKey);
    if (found) {
      found.lastSeenAt = draft.clockIso;
      found.title = input.title;
      return;
    }
    draft.incidents.unshift({
      id: input.id,
      organizationId: input.organizationId,
      installationId: input.installationId,
      alertRuleId: input.alertRuleId,
      title: input.title,
      severity: input.severity,
      status: 'OPEN',
      firstSeenAt: draft.clockIso,
      lastSeenAt: draft.clockIso,
      acknowledgedAt: null,
      resolvedAt: null,
      dedupKey: input.dedupKey,
    });
    this.pushIncidentEvent(draft, draft.incidents[0]!, 'OPEN', input.detail);
  }

  private pushIncidentEvent(draft: DemoState, incident: Incident, code: string, detail: string): void {
    draft.incidentEvents.unshift({
      id: this.nid(draft, 'iev'),
      organizationId: incident.organizationId,
      incidentId: incident.id,
      occurredAt: draft.clockIso,
      eventCode: code,
      detail,
    });
  }

  private syncOutbox(draft: DemoState): void {
    const pending = draft.outbox.filter((item) => item.syncedAt === null);
    const seen = new Set<string>();
    for (const item of pending) {
      if (seen.has(item.eventId)) {
        continue;
      }
      seen.add(item.eventId);
      item.syncedAt = draft.clockIso;
    }
    draft.notice = pending.length
      ? `Cola de borde sincronizada una vez (${seen.size} eventos, sin duplicar).`
      : 'No había eventos pendientes en el borde.';
  }

  private queue(draft: DemoState, eventId: string): void {
    if (!draft.offline) {
      return;
    }
    draft.outbox.push({
      id: this.nid(draft, 'out'),
      gatewayId: 'browser-demo',
      eventId,
      queuedAt: draft.clockIso,
      syncedAt: null,
    });
  }

  private writableIncident(draft: DemoState, incidentId: string): Incident | null {
    const incident = draft.incidents.find((item) => item.id === incidentId);
    if (!incident) {
      return null;
    }
    const installation = draft.installations.find((item) => item.id === incident.installationId);
    if (!installation || installationScope(draft, installation) === 'NONE') {
      return null;
    }
    return incident;
  }

  private owns(draft: DemoState, organizationId: string): boolean {
    return organizationId === draft.activeOrganizationId;
  }

  private siteOf(draft: DemoState, installationId: string) {
    const installation = draft.installations.find((item) => item.id === installationId);
    return draft.sites.find((item) => item.id === installation?.siteId);
  }

  private canMaintain(draft: DemoState, installationId: string): boolean {
    const installation = draft.installations.find((item) => item.id === installationId);
    if (!installation) {
      return false;
    }
    const scope = installationScope(draft, installation);
    return scope === 'FULL' || scope === 'MAINTENANCE' || scope === 'MANAGE';
  }

  private nid(draft: DemoState, prefix: string): string {
    draft.seq += 1;
    return `${prefix}-${draft.seq}`;
  }

  private patch(mutator: (draft: DemoState) => void): void {
    const next = structuredClone(this.raw());
    mutator(next);
    next.savedAt = next.clockIso;
    this.raw.set(next);
    this.persist(next);
  }

  private persist(state: DemoState): void {
    if (typeof localStorage === 'undefined') {
      return;
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }
}

function readState(): DemoState {
  if (typeof localStorage === 'undefined') {
    return buildSeed();
  }
  const stored = localStorage.getItem(STORAGE_KEY);
  if (!stored) {
    return buildSeed();
  }
  try {
    const parsed = JSON.parse(stored) as DemoState;
    if (parsed.version !== 4 || parsed.seed !== 'helioguard-hackaton-v4') {
      return buildSeed();
    }
    return parsed;
  } catch {
    return buildSeed();
  }
}
