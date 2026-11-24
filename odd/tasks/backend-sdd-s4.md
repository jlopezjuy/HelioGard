# Sprint S4: Modelado Energético y Clima (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S4 SDD rules (`RB-ENE-001`..`004`, `RB-TEN-001`), Liquibase `energy_interval_result` + interval checks, tenant-scoped S4 services/DTOs, `EnergyModelingService` + `POST /api/energy-runs/execute`, and integration tests (`T10` + power/energy/meter-reset/soiling/provenance).

## Specs

- `S4.1`: Incremental Liquibase migration `20261124150000_s4_energy_modeling_and_intervals.xml` (expand-only): `energy_interval_result` table + `EnergyIntervalResult` entity & repository + interval check constraints on `weather_ingestion_batch`, `energy_model_configuration`, `energy_model_run`.
- `S4.2` (`RB-TEN-001`, `RB-ENE-001`): Remove `@NotNull` on `organization` in S4 DTOs; enforce tenant isolation & server-side org assignment on `WeatherIngestionBatch`, `EnergyModelConfiguration`, `EnergyModelRun`, `DataProvenance`, `ClimateRiskProfile`; reject Sentinel/GOES module thermography claims in `DataProvenance`.
- `S4.3` (`RB-ENE-001`..`004`, `T10`): Implement `EnergyModelingService` + `POST /api/energy-runs/execute` returning `INSUFFICIENT_DATA` with `expectedWh = null` when irradiance/config/samples are insufficient, integrating power vs energy, detecting meter reset, computing `deficitWh = max(0, expectedWh - actualWh)`, and gating `SOILING` hypotheses on supporting evidence.
- `S4.4`: Integration tests (`EnergyModelingSddIT`) covering `T10` (`INSUFFICIENT_DATA`, `expectedWh == null`), valid PV calculation + uncertainty band, meter reset detection, soiling evidence gating, Sentinel/GOES provenance guard, and cross-tenant IDOR; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S4.1`): Add S4 Liquibase migration, `EnergyIntervalResult` entity, and repository | commit: pending
- [x] `T2` (`S4.2`): Update S4 DTOs and enforce tenant/domain rules in `WeatherIngestionBatch`, `EnergyModelConfiguration`, `EnergyModelRun`, `DataProvenance`, `ClimateRiskProfile` | commit: pending
- [x] `T3` (`S4.3`): Implement `EnergyModelingService` and `EnergyModelingResource` (`POST /api/energy-runs/execute`) | commit: pending
- [x] `T4` (`S4.4`): Write and run `EnergyModelingSddIT` (`T10` + S4 rules) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
