-- HelioGuard v5 - Plantilla de constraints para aplicar DESPUES del JDL.
-- No ejecutar sin verificar los nombres y la situación de datos existentes.
-- Estas constraints complementan el JDL para FKs compuestas e intervalos.

-- Instalacion propietaria coherente con sitio. Solo cuando organization_id existe en ambas.
ALTER TABLE installation
  ADD CONSTRAINT uq_installation_pk_tenant UNIQUE (id, organization_id);
ALTER TABLE site
  ADD CONSTRAINT uq_site_pk_tenant UNIQUE (id, organization_id);
ALTER TABLE installation
  ADD CONSTRAINT fk_installation_site_same_tenant
  FOREIGN KEY (site_id, organization_id)
  REFERENCES site (id, organization_id);

ALTER TABLE solar_sector
  ADD CONSTRAINT uq_solar_sector_pk_tenant UNIQUE (id, organization_id);
ALTER TABLE solar_sector
  ADD CONSTRAINT fk_sector_installation_same_tenant
  FOREIGN KEY (installation_id, organization_id)
  REFERENCES installation (id, organization_id);

-- Asegurar que los grupos electrico/circuito pertenezcan al tenant correcto.
ALTER TABLE equipment
  ADD CONSTRAINT uq_equipment_pk_tenant UNIQUE (id, organization_id);
ALTER TABLE equipment_connection
  ADD CONSTRAINT fk_connection_from_same_tenant
  FOREIGN KEY (from_equipment_id, organization_id)
  REFERENCES equipment (id, organization_id);
ALTER TABLE equipment_connection
  ADD CONSTRAINT fk_connection_to_same_tenant
  FOREIGN KEY (to_equipment_id, organization_id)
  REFERENCES equipment (id, organization_id);

-- Evita que un string pertenezca al mismo tiempo a dos entradas MPPT incompatibles.
-- Habilitar btree_gist solo mediante migración administrada con permisos suficientes.
CREATE EXTENSION IF NOT EXISTS btree_gist;
ALTER TABLE string_input_assignment
  ADD CONSTRAINT ex_string_single_active_input
  EXCLUDE USING gist (
    solar_string_id WITH =,
    tstzrange(valid_from, COALESCE(valid_until, 'infinity'::timestamptz), '[)') WITH &&
  );

-- Invariantes temporales básicas (no son prueba completa de normalización):
ALTER TABLE string_input_assignment
  ADD CONSTRAINT ck_assignment_interval CHECK (valid_until IS NULL OR valid_until > valid_from);
ALTER TABLE solar_sector
  ADD CONSTRAINT ck_sector_interval CHECK (valid_until IS NULL OR valid_until > valid_from);
ALTER TABLE telemetry_channel_binding
  ADD CONSTRAINT ck_channel_binding_interval CHECK (valid_until IS NULL OR valid_until > valid_from);

-- Solo un perfil de reglas versionado puede aplicar en una ventana, validar el alcance en código.
-- Imponer unicidad tenant-scoped en reglas/códigos a través de migraciones revisadas:
CREATE UNIQUE INDEX IF NOT EXISTS uq_installation_tenant_code ON installation (organization_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS uq_sector_install_code_period ON solar_sector (organization_id, installation_id, sector_code, valid_from);
CREATE UNIQUE INDEX IF NOT EXISTS uq_plan_version_name ON maintenance_plan_version (maintenance_plan_id, version_code);
CREATE UNIQUE INDEX IF NOT EXISTS uq_benefit_version_code ON plan_benefit (plan_version_id, benefit_code);

-- Faltan políticas de delegación cross-tenant deliberadas, tests de autorización, y controles de estado.
-- Nunca inferir seguridad total a partir de estas constraints de ejemplo.
