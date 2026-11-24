# Sprint S7: Auditorías y Planes de Mantenimiento (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S7 SDD rules (`RB-AUD-001`, `RB-AUD-002`, `RB-PLAN-001`, `RB-PLAN-002`, `RB-TEN-001`), S7 Liquibase constraints, tenant-scoped S7 services/DTOs, `AuditAndPlanWorkflowService` (`POST /api/audit-reports/{id}/approve`, `POST /api/plan-eligibility-assessments/evaluate`), and acceptance tests (`T16`–`T17`).

## Specs

- `S7.1`: Incremental Liquibase migration `20261124180000_s7_audits_and_plans_constraints.xml` (expand-only): `uq_plan_version_name`, `uq_benefit_version_code`, `ck_service_agreement_window`, `ck_plan_subscription_window`, `ck_plan_version_amount`.
- `S7.2` (`RB-AUD-002`, `RB-PLAN-002`, `RB-TEN-001`): Remove `@NotNull` on `organization` in all 14 S7 DTOs; enforce tenant isolation, published template/plan-version immutability (`409`), and frozen price/currency on `PlanSubscription`.
- `S7.3` (`RB-AUD-001`, `RB-PLAN-001`, `T16`, `T17`): Implement `AuditAndPlanWorkflowService` + `AuditAndPlanWorkflowResource`:
  - `POST /api/audit-reports/{id}/approve`: rejects with missing mandatory question codes (`T16`), missing evidence on critical findings, or missing `AuditReportSignature`.
  - `POST /api/plan-eligibility-assessments/evaluate`: critical electrical findings yield `REQUIRES_REMEDIATION` / `NOT_ELIGIBLE` with `requiresCorrection = true` (`T17`), never auto-contracting.
- `S7.4`: Integration tests (`AuditAndPlanSddIT`) covering `T16`, `T17`, template/plan-version immutability, frozen subscription price, and cross-tenant IDOR; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S7.1`): Add S7 Liquibase migration and paginated repo queries | commit: pending
- [x] `T2` (`S7.2`): Update S7 DTOs and enforce tenant/immutability rules across the 14 S7 services | commit: pending
- [x] `T3` (`S7.3`): Implement `AuditAndPlanWorkflowService` and `AuditAndPlanWorkflowResource` | commit: pending
- [x] `T4` (`S7.4`): Write and run `AuditAndPlanSddIT` (`T16`–`T17`) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
