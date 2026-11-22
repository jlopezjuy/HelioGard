-- HELIOGUARD - SQL DE REFERENCIA (NO ejecutar sin revisar nombres reales generados por JHipster)
-- Ejemplo para PostgreSQL. Se integrará en cambios Liquibase después de generar el JDL.
-- Objetivo: evitar referencias cruzadas entre tenants y activar RLS.
-- El rol runtime NO debe ser superusuario, dueño de las tablas ni tener BYPASSRLS.
-- En cada transacción de negocio el backend debe configurar app.organization_id a partir
-- de una membresía ACTIVE autenticada; NUNCA aceptar directamente el tenant del cliente.

-- 1) Claves para integridad intraempresa (composite FK)
ALTER TABLE site ADD CONSTRAINT uq_site_id_org UNIQUE (id, organization_id);
ALTER TABLE installation ADD CONSTRAINT uq_installation_id_org UNIQUE (id, organization_id);
ALTER TABLE installation ADD CONSTRAINT fk_installation_site_same_org
  FOREIGN KEY (site_id, organization_id) REFERENCES site(id, organization_id);

ALTER TABLE incident ADD CONSTRAINT fk_incident_installation_same_org
  FOREIGN KEY (installation_id, organization_id) REFERENCES installation(id, organization_id);

ALTER TABLE gateway ADD CONSTRAINT fk_gateway_installation_same_org
  FOREIGN KEY (installation_id, organization_id) REFERENCES installation(id, organization_id);

-- Repetir para todas las entidades de tenant que referencien otras entidades propias.
-- Las relaciones explícitamente entre empresas requieren validación por acuerdo (ServiceAgreement),
-- no una FK compuesta de tenant al objeto de la empresa proveedora.

-- 2) Índices necesarios desde el primer despliegue
CREATE UNIQUE INDEX uq_installation_org_code ON installation(organization_id, code);
CREATE INDEX idx_installation_org_status ON installation(organization_id, status);
CREATE INDEX idx_incident_org_status_seen ON incident(organization_id, status, first_seen_at DESC);
CREATE UNIQUE INDEX uq_membership_org_user ON organization_membership(organization_id, member_id);
-- TODO: revisar nombres reales de tablas y columnas JHipster y adaptar.

-- 3) RLS con fail-closed. Si no hay contexto de organización, no se ven registros.
ALTER TABLE installation ENABLE ROW LEVEL SECURITY;
ALTER TABLE installation FORCE ROW LEVEL SECURITY;
CREATE POLICY installation_org_policy ON installation
  FOR ALL
  USING (organization_id = nullif(current_setting('app.organization_id', true), '')::bigint)
  WITH CHECK (organization_id = nullif(current_setting('app.organization_id', true), '')::bigint);

ALTER TABLE incident ENABLE ROW LEVEL SECURITY;
ALTER TABLE incident FORCE ROW LEVEL SECURITY;
CREATE POLICY incident_org_policy ON incident
  FOR ALL
  USING (organization_id = nullif(current_setting('app.organization_id', true), '')::bigint)
  WITH CHECK (organization_id = nullif(current_setting('app.organization_id', true), '')::bigint);

-- Replicar con migrations para TODAS las tablas de negocio compartidas.
-- OrganizationMembership/InstallationAccessGrant/ServiceAgreement requieren políticas
-- que contemplen explícitamente el acceso autorizado a recursos ajenos sin abrir el tenant.

-- 4) En la misma transacción Spring, con conexión de pool que se devuelve limpia:
-- BEGIN;
-- SELECT set_config('app.organization_id', :authorized_org_id, true);
-- SELECT * FROM installation WHERE id = :installation_id;
-- COMMIT;
-- Para acceso delegado, diseñar un mecanismo de autorización diferenciado y auditado;
-- no permitir cambiar el organization_id de la tabla base en el cliente.
