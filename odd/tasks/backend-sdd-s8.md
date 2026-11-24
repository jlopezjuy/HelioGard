# Sprint S8: Mantenimiento, Órdenes de Trabajo y Misiones Dron (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S8 SDD rules (`RB-WO-001`..`003`, `RB-DRONE-001`..`003`, `RB-REC-001`, `RB-TEN-001`), S8 Liquibase constraints, tenant-scoped S8 services/DTOs, `MaintenanceAndDroneWorkflowService` (`POST /api/work-orders/{id}/accept`, `POST /api/drone-missions/{id}/dispatch`, `POST /api/recovery-assessments/verify`), and acceptance tests (`T18`–`T20`).

## Specs

- `S8.1`: Incremental Liquibase migration `20261124190000_s8_workorders_and_drones_constraints.xml` (expand-only): `ck_work_order_dates`, `ck_drone_asset_max_wind`, `ck_intervention_cost`, plus paginated queries on `WorkOrderRepository` & `DroneMissionRepository`.
- `S8.2` (`RB-WO-001`, `RB-WO-002`, `RB-TEN-001`): Remove `@NotNull` on `organization` in all 14 S8 DTOs; enforce tenant isolation and `WorkOrder` state lifecycle across all 14 S8 services.
- `S8.3` (`RB-WO-003`, `RB-DRONE-001`..`003`, `RB-REC-001`, `T18`–`T20`): Implement `MaintenanceAndDroneWorkflowService` + `MaintenanceAndDroneWorkflowResource`:
  - `POST /api/work-orders/{id}/accept`: rejects external provider without active `InstallationAccessGrant` (`T18`).
  - `POST /api/drone-missions/{id}/dispatch`: simulated dispatch only (physical drones disabled); rejects without valid `MissionAuthorization` or unsafe wind/ice/energized-damage (`T19`).
  - `POST /api/recovery-assessments/verify`: returns `VerificationStatus.INCONCLUSIVE` when post-maintenance comparable data is insufficient (`T20`).
- `S8.4`: Integration tests (`MaintenanceDroneSddIT`) covering `T18`, `T19`, `T20`, `WorkOrder` state transitions, and cross-tenant IDOR; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S8.1`): Add S8 Liquibase migration and paginated repo queries | commit: pending
- [x] `T2` (`S8.2`): Update S8 DTOs and enforce tenant/domain rules across the 14 S8 services | commit: pending
- [x] `T3` (`S8.3`): Implement `MaintenanceAndDroneWorkflowService` and `MaintenanceAndDroneWorkflowResource` | commit: pending
- [x] `T4` (`S8.4`): Write and run `MaintenanceDroneSddIT` (`T18`–`T20`) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
