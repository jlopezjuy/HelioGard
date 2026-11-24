# Trazabilidad REQUIREMENTS.md → JDL v5 / SQL / lógica de negocio

| Requisito | Entidades JDL v5 / SQL | Lógica de negocio obligatoria | No resuelto solo por JDL |
|---|---|---|---|
| FR-001 Multiempresa | Organization, Membership, Site, Installation, SolarSector | TenantAccess, ownership, 3FN lógica | FKs compuestas/RLS |
| FR-002 Read-only | Gateway, TelemetryChannel, DeviceIdentity, TelemetryChannelBinding, `telemetry_measurement` | Ingestión segura y quality flags, Modbus solo lectura | Parser protocolo, credenciales |
| FR-003 Offline | Gateway + `telemetry_event_receipt`, checkpoint local | Cola SQLite durable, retry, idempotencia | edge + Flutter LAN |
| FR-004 Esperado | EnergyModelConfiguration, EnergyModelRun, WeatherIngestionBatch, EvidenceSource | pvlib, periodo, banda de incertidumbre e `INSUFFICIENT_DATA` | observaciones meteorológicas SQL |
| FR-005 Alertas | AlertRule, AlertRuleVersion, Incident, IncidentEvent, Localization | dedup, ack, supresión y amanecer | job/event engine |
| FR-006 Pérdidas | LossAssessment, LossCauseEstimate, TariffPlan/Rate, EconomicAssessment | no doble conteo, autoconsumo/exportación | curvas demanda/meteorología |
| FR-007 Auditoría | Template, Question, Report, Answer, Finding, Evidence, Signature | inmutabilidad, validación, revisión, offline | firma profesional y control acceso |
| FR-008 Planes | Plan, PlanVersion, PlanBenefit, Eligibility, Proposal, Agreement, Subscription | SLA, visitas, traslado y exclusiones | texto contractual y reglas |
| FR-009 OT | WorkOrder, WorkOrderApproval, WorkOrderAssignment, Intervention, Recovery, VerificationResult | autorización, trazabilidad, resultados comparables | agenda/recurso técnico |
| FR-010 Clima | ClimateRiskProfile, WeatherIngestionBatch, DataProvenance | reglas regionales, hielo ≠ temperatura baja | fuentes reales y sensores |
| NFR-001 a 010 | Complementos SQL, RLS, índices, logs y tests | todos los módulos | no generables solo por JDL |

**Aclaración:** `REQUIREMENTS.md` no contiene la carpeta `Idea Original` ni la definición física real del inversor. El v5 cubre los requisitos proporcionados y parte de la evolución conversada, pero **no demuestra cobertura integral de aquella carpeta**.
