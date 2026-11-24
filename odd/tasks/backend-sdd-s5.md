# Sprint S5: Alertas, Diagnóstico e Integridad (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S5 SDD rules (`RB-TEL-006`, `RB-SEC-002`, `RB-INC-001`..`003`, `RB-LOC-001`, `RB-TEN-001`), S5 Liquibase index/constraints, tenant-scoped S5 services/DTOs, `AlertDiagnosticService` (`POST /api/incidents/evaluate`, `POST /api/integrity/evaluate`), and acceptance tests (`T11`–`T13`).

## Specs

- `S5.1`: Incremental Liquibase migration `20261124160000_s5_incidents_and_integrity_indexes.xml` (expand-only): index `idx_incident_org_dedup_status` on `incident (organization_id, dedup_key, status)` and `ck_alert_rule_version_window` on `alert_rule_version`.
- `S5.2` (`RB-INC-001`..`003`, `RB-TEN-001`): Remove `@NotNull` on `organization` in S5 DTOs; enforce tenant isolation on `AlertRule`, `AlertRuleVersion` (immutable once used by an `Incident`), `Incident` (state machine `OPEN -> ACKNOWLEDGED -> IN_PROGRESS -> RESOLVED -> CLOSED`), `IncidentEvent`, `IncidentLocalization`, `SecurityCaseEvent`, `SecurityCaseEvidence`.
- `S5.3` (`RB-TEL-006`, `RB-SEC-002`, `RB-INC-002`, `T11`–`T13`): Implement `AlertDiagnosticService` + `IncidentDiagnosticResource`:
  - `T11`: Nighttime / zero irradiance (`NO_PRODUCTION_NIGHT`) does not create low-production `Incident`.
  - `T12`: Stale/unreachable telemetry classifies `TELEMETRY_OFFLINE` (never `PANEL_FAILURE`).
  - `RB-INC-002`: Active dedup by `(installation, ruleVersion, affectedScope)` updates `lastSeenAt` + appends `IncidentEvent` instead of duplicating `Incident`.
  - `T13`: Physically impossible reading yields `IntegrityAssessment(trustLevel=SUSPECT)` + `TelemetryIntegrityEvent(PHYSICAL_INCONSISTENCY, UNDER_REVIEW)`, never confirmed cyberattack.
- `S5.4`: Integration tests (`IncidentDiagnosticSddIT`) covering `T11`, `T12`, `T13`, `AlertRuleVersion` immutability, `Incident` deduplication & state transitions, and cross-tenant IDOR; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S5.1`): Add S5 Liquibase migration and repository query helpers | commit: pending
- [x] `T2` (`S5.2`): Update S5 DTOs and enforce tenant/domain rules on `AlertRule`, `AlertRuleVersion`, `Incident`, `IncidentEvent`, `IncidentLocalization`, `SecurityCaseEvent`, `SecurityCaseEvidence` | commit: pending
- [x] `T3` (`S5.3`): Implement `AlertDiagnosticService` and `IncidentDiagnosticResource` (`POST /api/incidents/evaluate`, `POST /api/integrity/evaluate`) | commit: pending
- [x] `T4` (`S5.4`): Write and run `IncidentDiagnosticSddIT` (`T11`–`T13`) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
