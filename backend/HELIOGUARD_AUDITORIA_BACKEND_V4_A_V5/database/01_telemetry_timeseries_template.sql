-- HelioGuard v5 - Plantilla PostgreSQL complementaria al JDL, NO ejecutada.
-- Se diseñó para un PostgreSQL vacío. Adaptar a convenciones reales JHipster/Liquibase.
-- Los tipos BIGINT de organization_id, gateway_id, etc. son supuestos de IDs JPA.
-- Agregar FKs luego de inspeccionar tablas, perfiles y nombres generados.
BEGIN;

-- ÚNICA tabla canónica para idempotencia de recepción gatewayId + eventId.
-- Eliminar la duplicidad conceptual de telemetry_ingest_dedup vs telemetry_event_dedup.
CREATE TABLE IF NOT EXISTS telemetry_event_receipt (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  organization_id BIGINT NOT NULL,
  installation_id BIGINT NOT NULL,
  gateway_id BIGINT NOT NULL,
  event_id VARCHAR(150) NOT NULL,
  boot_id VARCHAR(150),
  sequence_number BIGINT,
  source_observed_at TIMESTAMPTZ NOT NULL,
  received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  payload_sha256 CHAR(64) NOT NULL,
  decision VARCHAR(25) NOT NULL CHECK (decision IN ('ACCEPTED', 'REJECTED', 'QUARANTINED')),
  raw_payload_ref TEXT,
  UNIQUE (gateway_id, event_id)
);
CREATE INDEX IF NOT EXISTS idx_receipt_tenant_time ON telemetry_event_receipt (organization_id, installation_id, received_at DESC);

-- La secuencia identifica conflictos de replay/orden, NO reemplaza la deduplicación de delivery.
CREATE TABLE IF NOT EXISTS telemetry_boot_sequence (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  organization_id BIGINT NOT NULL,
  device_uid VARCHAR(140) NOT NULL,
  boot_id VARCHAR(150) NOT NULL,
  sequence_number BIGINT NOT NULL,
  event_id VARCHAR(150) NOT NULL,
  payload_sha256 CHAR(64) NOT NULL,
  observed_at TIMESTAMPTZ NOT NULL,
  first_received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(device_uid, boot_id, sequence_number)
);
CREATE INDEX IF NOT EXISTS idx_seq_tenant_device ON telemetry_boot_sequence (organization_id, device_uid, first_received_at DESC);

-- Modelo estrecho: una métrica normalizada por fila/instante, unidades desde MetricDefinition.
CREATE TABLE IF NOT EXISTS telemetry_measurement (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  organization_id BIGINT NOT NULL,
  installation_id BIGINT NOT NULL,
  gateway_id BIGINT NOT NULL,
  telemetry_channel_id BIGINT NOT NULL,
  receipt_id BIGINT NOT NULL REFERENCES telemetry_event_receipt(id),
  observed_at TIMESTAMPTZ NOT NULL,
  numeric_value NUMERIC(24,8),
  text_value VARCHAR(250),
  quality_flags JSONB NOT NULL DEFAULT '{}'::jsonb,
  accepted BOOLEAN NOT NULL DEFAULT true,
  CHECK (numeric_value IS NOT NULL OR text_value IS NOT NULL),
  UNIQUE(receipt_id, telemetry_channel_id, observed_at)
);
CREATE INDEX IF NOT EXISTS idx_metric_tenant_channel_time ON telemetry_measurement (organization_id, telemetry_channel_id, observed_at DESC);
CREATE INDEX IF NOT EXISTS idx_metric_tenant_install_time ON telemetry_measurement (organization_id, installation_id, observed_at DESC);

-- Recurso solar: registros meteorológicos pueden tener un periodo y calidad variable.
CREATE TABLE IF NOT EXISTS weather_observation (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  organization_id BIGINT NOT NULL,
  installation_id BIGINT NOT NULL,
  evidence_source_id BIGINT NOT NULL,
  source_record_key VARCHAR(180) NOT NULL,
  observed_from TIMESTAMPTZ NOT NULL,
  observed_to TIMESTAMPTZ NOT NULL,
  ghi_w_m2 NUMERIC(16,5),
  dni_w_m2 NUMERIC(16,5),
  dhi_w_m2 NUMERIC(16,5),
  ambient_temperature_c NUMERIC(10,4),
  cloud_fraction NUMERIC(9,6),
  quality_flags JSONB NOT NULL DEFAULT '{}'::jsonb,
  imported_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CHECK (observed_to > observed_from),
  UNIQUE(installation_id, evidence_source_id, source_record_key)
);
CREATE INDEX IF NOT EXISTS idx_weather_tenant_interval ON weather_observation (organization_id, installation_id, observed_from DESC);

-- Una fila por intervalo de cálculo, nunca comparar W puntuales como si fueran Wh.
CREATE TABLE IF NOT EXISTS energy_interval_result (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  organization_id BIGINT NOT NULL,
  installation_id BIGINT NOT NULL,
  energy_model_run_id BIGINT NOT NULL,
  period_start TIMESTAMPTZ NOT NULL,
  period_end TIMESTAMPTZ NOT NULL,
  expected_wh NUMERIC(24,6),
  expected_lower_wh NUMERIC(24,6),
  expected_upper_wh NUMERIC(24,6),
  actual_wh NUMERIC(24,6),
  data_coverage_pct NUMERIC(7,4),
  quality_flags JSONB NOT NULL DEFAULT '{}'::jsonb,
  CHECK (period_end > period_start),
  CHECK (data_coverage_pct IS NULL OR data_coverage_pct BETWEEN 0 AND 100),
  UNIQUE(energy_model_run_id, period_start, period_end)
);
CREATE INDEX IF NOT EXISTS idx_energy_tenant_time ON energy_interval_result (organization_id, installation_id, period_start DESC);

-- RLS: EJEMPLO para datos propiedad del tenant. No implementa permisos delegados.
-- La cuenta runtime debe ser distinta de owner y no tener BYPASSRLS.
-- Definir app.tenant_id únicamente por transacción y limpiarlo al devolver conexión al pool.
ALTER TABLE telemetry_measurement ENABLE ROW LEVEL SECURITY;
ALTER TABLE telemetry_measurement FORCE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS telemetry_measurement_owner ON telemetry_measurement;
CREATE POLICY telemetry_measurement_owner ON telemetry_measurement
  USING (organization_id = NULLIF(current_setting('app.tenant_id', true), '')::BIGINT)
  WITH CHECK (organization_id = NULLIF(current_setting('app.tenant_id', true), '')::BIGINT);

COMMIT;
