-- HELIOGUARD — ALMACENAMIENTO DE TELEMETRÍA, EJEMPLO PARA REVISIÓN
-- Fuera de JDL: volumen y semántica de series temporales demandan SQL/Liquibase propio.
-- Entidades transaccionales (installation, telemetry_channel, gateway, organization) ya existen.

CREATE TABLE telemetry_ingest_dedup (
  gateway_id BIGINT NOT NULL,
  event_id VARCHAR(120) NOT NULL,
  organization_id BIGINT NOT NULL,
  first_received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (gateway_id, event_id)
);
-- Esta tabla separada conserva idempotencia por gateway + event_id sin depender
-- del esquema de particionado temporal de las mediciones.

CREATE TABLE telemetry_measurement (
  organization_id BIGINT NOT NULL,
  installation_id BIGINT NOT NULL,
  channel_id BIGINT NOT NULL,
  gateway_id BIGINT,
  event_id VARCHAR(120) NOT NULL,
  measured_at TIMESTAMPTZ NOT NULL,
  received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  value_num DOUBLE PRECISION,
  quality VARCHAR(24) NOT NULL,
  source VARCHAR(32) NOT NULL,
  PRIMARY KEY (measured_at, organization_id, channel_id, event_id)
) PARTITION BY RANGE (measured_at);

-- Crear particiones por mes con migraciones o job de DDL administrado.
-- Ejemplo ilustrativo: NO define todas las particiones necesarias.
CREATE TABLE telemetry_measurement_2026_11 PARTITION OF telemetry_measurement
  FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');

CREATE INDEX idx_telemetry_org_installation_time
  ON telemetry_measurement (organization_id, installation_id, measured_at DESC);
CREATE INDEX idx_telemetry_channel_time
  ON telemetry_measurement (channel_id, measured_at DESC);

-- No asumir que un registro instantáneo de potencia (W) es energía (Wh).
-- Para energía sumar deltas confiables de contador o integrar potencia en el tiempo,
-- conservando calidad, zona horaria y cobertura de datos.

-- TODO: Agregar FKs compuestas tenant-safe a entidades maestras, políticas RLS,
-- retención, agregados diarios y pruebas de backfill/late arrivals antes de producción.
