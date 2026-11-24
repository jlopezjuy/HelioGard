# Sprint S6: Pérdidas Económicas y Tarifas (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S6 SDD rules (`RB-ECO-001`..`003`, `RB-TEN-001`), S6 Liquibase constraints, tenant-scoped S6 services/DTOs, `EconomicEvaluationService` (`POST /api/economic-assessments/evaluate`), and acceptance tests (`T14`–`T15`).

## Specs

- `S6.1`: Incremental Liquibase migration `20261124170000_s6_economy_and_tariff_constraints.xml` (expand-only): `ck_tariff_rate_window`, `ck_tariff_rate_prices`, `ck_loss_assessment_non_negative`, `ck_loss_cause_non_negative`, `ck_economic_assessment_non_negative`.
- `S6.2` (`RB-TEN-001`, `RB-ECO-001`): Remove `@NotNull` on `organization` in S6 DTOs; enforce tenant isolation on `LossAssessment`, `LossCauseEstimate` (rejecting double-counting when sum > `totalLossWh` with `409 LOSS_DOUBLE_COUNTING_FORBIDDEN`), `TariffPlan`, `TariffRate`, `EconomicAssessment`.
- `S6.3` (`RB-ECO-001`..`003`, `T14`, `T15`): Implement `EconomicEvaluationService` + `EconomicEvaluationResource` (`POST /api/economic-assessments/evaluate`):
  - `T14`: Rejects over-attributed causes (`409`), records residual `UNKNOWN` cause explicitly.
  - `T15`: Resolves active `TariffRate` by validity window; when self-consumption/export curve is missing, returns scenario range with assumptions (`exactAmountAvailable = false`) instead of fabricating a single exact value.
  - `RB-ECO-003`: Gates cleaning ROI promise on benefit duration, cost, safety, and comparable post-recovery evidence.
- `S6.4`: Integration tests (`EconomicLossSddIT`) covering `T14`, `T15`, active tariff validity, `RB-ECO-003` cleaning ROI gating, and cross-tenant IDOR; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S6.1`): Add S6 Liquibase migration | commit: pending
- [x] `T2` (`S6.2`): Update S6 DTOs and enforce tenant/domain rules on `LossAssessment`, `LossCauseEstimate`, `TariffPlan`, `TariffRate`, `EconomicAssessment` | commit: pending
- [x] `T3` (`S6.3`): Implement `EconomicEvaluationService` and `EconomicEvaluationResource` (`POST /api/economic-assessments/evaluate`) | commit: pending
- [x] `T4` (`S6.4`): Write and run `EconomicLossSddIT` (`T14`–`T15`) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
