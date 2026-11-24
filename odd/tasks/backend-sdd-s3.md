# Sprint S3: Telemetría, Edge M2M Auth e Idempotencia (SDD)

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Implement Sprint S3 SDD rules (`RB-TEL-001`..`005`, `RB-SEC-001`..`004`, `RB-TEN-001`), Liquibase tables `telemetry_ingest_event` + `device_sequence_checkpoint`, M2M device authentication (no human JWT), `POST /api/ingest/telemetry`, tenant-scoped S3 services, and acceptance tests `T06`–`T09`.

## Specs

- `S3.1`: Incremental Liquibase migration `20261124140000_s3_telemetry_ingestion_and_idempotency.xml` (expand-only): `telemetry_ingest_event` with `uq_telemetry_ingest_gateway_event UNIQUE (gateway_id, event_id)` and `device_sequence_checkpoint` with `uq_device_seq_checkpoint UNIQUE (gateway_id, device_identity_id)`.
- `S3.2` (`RB-TEL-001`, `RB-SEC-003`): M2M authentication via `X-Device-Uid` + `X-Device-Secret` validated against `DeviceIdentity` (`ACTIVE`, not expired, SHA-256 `keyFingerprint`) + `Gateway` (not `REVOKED`), resolving `organizationId` and `installationId` from DB. Failed auth records `TelemetryIntegrityEvent` (`AUTH_FAILURE`) without logging/persisting secrets.
- `S3.3` (`RB-TEL-002`..`005`, `RB-SEC-001`, `RB-SEC-002`, `T06`–`T09`): `POST /api/ingest/telemetry` supports batch & single ingestion: rejects foreign `installationId` (`403` + `TelemetryIntegrityEvent`), deduplicates exact `(gateway_id, event_id)` + same `payloadSha256` idempotently (`DUPLICATE_IDEMPOTENT`), quarantines same `eventId` + different `payloadSha256` (`QUARANTINED` + `TelemetryIntegrityEvent` `REPLAY`), tracks `bootId` + `sequenceNumber` gaps/reboots and clock skew.
- `S3.4` (`RB-SEC-004`, `RB-TEN-001`): Tenant isolation on `DeviceIdentity`, `TelemetryChannel`, `TelemetryIntegrityEvent`, `IntegrityAssessment`, `SecurityIncident` (with `SecurityCaseStatus` transition validation).
- `S3.5`: Integration tests (`TelemetryEdgeSddIT`) covering `T06`, `T07` (100 offline events retried 3 times -> 100 unique rows), `T08`, `T09`, plus invalid secret auth failure & `SecurityIncident` transitions; update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.

## Tasks

- [ ] `T1` (`S3.1`): Add S3 Liquibase migration, domain entities, and repositories (`TelemetryIngestEvent`, `DeviceSequenceCheckpoint`) | commit: pending
- [ ] `T2` (`S3.2`, `S3.3`): Implement M2M `DeviceAuthService`, `EdgeIngestionService`, and `EdgeTelemetryResource` (`POST /api/ingest/telemetry`) + `SecurityConfiguration` matcher | commit: pending
- [ ] `T3` (`S3.4`): Protect `DeviceIdentity`, `TelemetryChannel`, `TelemetryIntegrityEvent`, `IntegrityAssessment`, and `SecurityIncident` services/DTOs with tenant isolation & state transitions | commit: pending
- [ ] `T4` (`S3.5`): Write and run `TelemetryEdgeSddIT` (`T06`–`T09`) against PostgreSQL Testcontainers and update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending
