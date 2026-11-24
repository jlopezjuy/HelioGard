# Sprint S2: Topología Eléctrica + Edge Binding (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S2 SDD rules (`RB-ASSET-001`, `RB-ASSET-002`, `RB-ID-003`, `RB-LOC-001`, `RB-TEN-001`), Liquibase composite/temporal/`btree_gist` constraints, DTOs without editable `organization`, tenant-scoped services for S2 entities, and integration tests.

## Specs

- `S2.1`: Incremental Liquibase migration `20261124130000_s2_topology_integrity_constraints.xml` (expand-only, no DROP): `btree_gist` extension, `uq_solar_sector_pk_tenant`, `fk_sector_installation_same_tenant`, `ck_sector_interval`, `uq_sector_install_code_period`, `uq_equipment_pk_tenant`, `fk_connection_from_same_tenant`, `fk_connection_to_same_tenant`, `ck_connection_interval`, `ck_mppt_input_interval`, `ck_assignment_interval`, `ex_string_single_active_input` (`EXCLUDE USING gist`), `ck_channel_binding_interval`.
- `S2.2` (`RB-ASSET-001`, `RB-ASSET-002`, `RB-ID-003`, `RB-TEN-001`): Remove `@NotNull` on `organization` in S2 DTOs; enforce tenant isolation, server-side `organization` assignment, cross-installation/cross-tenant rejection, `EquipmentModel` kind checks (`MODULE` on `SolarString`, `INVERTER` on `InverterMpptInput`), temporal window validation, `StringInputAssignment` non-overlap (`409 OVERLAPPING_STRING_ASSIGNMENT`), `EquipmentConnection` non-self/same-installation checks, and `Gateway`/`TelemetryChannelBinding` tenant isolation.
- `S2.3` (`RB-LOC-001`): Implement `TopologyLocalizationService` resolving localization precision (`INSTALLATION`, `SECTOR`, `INVERTER`, `STRING`, `MODULE`, `UNKNOWN`) strictly from supporting topology/telemetry evidence.
- `S2.4`: Integration tests (`TopologySddIT`) covering tenant isolation, cross-installation wiring rejection, `EquipmentModel` kind validation, `StringInputAssignment` temporal overlap (`EXCLUDE USING gist` / `409`), `EquipmentConnection` same-installation & non-self checks, and `RB-LOC-001` precision levels; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [x] `T1` (`S2.1`, `inline`): Add S2 Liquibase migration with composite FKs, temporal checks, and `btree_gist` exclusion constraint | commit: pending
- [x] `T2` (`S2.2`, `inline`): Update S2 DTOs, repositories, and `PermissionService` shared helpers; implement SDD rules in `SolarSector`, `SolarArray`, `SolarString`, `InverterMpptInput`, `StringInputAssignment`, `Equipment`, `EquipmentConnection`, `EquipmentModel`, `Gateway`, `TelemetryChannelBinding` | commit: pending
- [x] `T3` (`S2.3`, `S2.4`, `inline`): Implement `TopologyLocalizationService`, write and run `TopologySddIT` against PostgreSQL Testcontainers, and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
