-- HELIOGUARD EJEMPLO: verificar contra Liquibase y modelo real antes de ejecutar.
-- No es migración validada ni lista para producción.

-- Seguridad / dedup: el gateway autentica identidad; el PK evita sumar el mismo evento dos veces.
CREATE TABLE IF NOT EXISTS telemetry_event_dedup (
  gateway_id BIGINT NOT NULL,
  event_id VARCHAR(128) NOT NULL,
  organization_id BIGINT NOT NULL,
  accepted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (gateway_id, event_id)
);

-- Separar "mismo evento" de "misma secuencia bajo mismo boot".
-- Sólo para dispositivos con contador/bootId AUTENTICADOS y semántica definida.
CREATE TABLE IF NOT EXISTS telemetry_sequence_seen (
  device_identity_id BIGINT NOT NULL,
  boot_id VARCHAR(128) NOT NULL,
  sequence_number BIGINT NOT NULL,
  event_id VARCHAR(128) NOT NULL,
  first_seen_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (device_identity_id, boot_id, sequence_number)
);

-- Las tablas JDL TelemetryIntegrityEvent, SecurityIncident y DataProvenance
-- se crean tras generar y revisar Liquibase. Evitar guardar claves privadas/tokens.
-- TODO: RLS tenant y composite FK en estas dos tablas, índices de retención,
-- partición/dedup/late events, and pruebas de pooling/rollback/backup.
