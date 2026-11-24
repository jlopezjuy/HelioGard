# Prompt para Codex — HelioGuard lógica de negocio real + empresa desde JWT

Actuá como arquitecto backend senior experto en Java, JHipster, Spring Boot, Spring Security, PostgreSQL, Liquibase, SDD y sistemas fotovoltaicos.

**Repositorio:** `/Users/juanlopez/Proyectos/Solar/ideas/backend`

**Fuente de verdad:**
- `HELIOGUARD_AUDITORIA_BACKEND_V4_A_V5/specs/domain.v5.review.jdl` o ruta equivalente real.
- `HELIOGUARD_AUDITORIA_BACKEND_V4_A_V5/specs/REQUIREMENTS.md`
- `HELIOGUARD_AUDITORIA_BACKEND_V4_A_V5/docs/SPRINTS_BACKEND_V5.md`
- `docs/LOGICA_NEGOCIO_V5.md`, `docs/TENANT_RESUELTO_DESDE_JWT.md`, `docs/PRUEBAS_LOGICA_NEGOCIO_V5.md` (usar sus rutas reales si están dentro del paquete extraído).
- Inspeccionar además el backend y sus entidades/JDL/Liquibase existentes; no asumir un proyecto nuevo.

## Objetivo

Implementar SDD real, no solo entidades CRUD. Cada servicio debe aplicar reglas de dominio, estados, integridad, autorización y tests. No agregar un `empresaId`/`organizationId` en rutas de negocio ni en DTO de creación para definir alcance de tenant: **resolver empresa automáticamente desde usuario logueado a través de JWT validado y OrganizationMembership ACTIVE**. Seguir el patrón del proyecto Vendora para empresa actual, adaptado a `OrganizationMembership` y sin el peligro de `stream().findFirst()` si existen múltiples empresas.

## Prioridad inmediata (Sprint S1)

1. Inspeccioná JWT/JHipster/Spring Security reales y elegí cómo leer `login` y claim firmado `active_org`, sin inventar APIs.
2. Implementá `TenantContextService` y un `PermissionService` con filtros tenant/instalación y acceso delegado. Si una membresía ACTIVE única, elegila. Si varias, exigí empresa activa seleccionada y revalidada; no usar `findFirst`.
3. Para cambio de empresa, definí endpoint auth de selección que valida membresía y emite JWT nuevo. No usar header `X-Organization-Id` como autoridad.
4. Implementá `Organization`, `OrganizationMembership`, `Site`, `Installation` y `InstallationAccessGrant` con DTOs sin org editable. El servicio asigna `organization` internamente.
5. Endpoints: `GET /api/me/context`, `GET/POST /api/installations`, `GET /api/installations/{installationId}`. No generar `/api/empresas/{idEmpresa}/...` como mecanismo de ámbito.
6. Agregá pruebas con tenants A/B, usuario multiempresa, claim vencido/revocado, colaborador externo y IDOR; proteger archivos, listas y operaciones batch.
7. Revisá transacciones/DB RLS y relaciones compuestas. Conservar las migraciones existentes: expand/contract, no DROP sin aprobación.

## Luego — servicios funcionales por sprints

S2 topología eléctrica, S3 telemetría/edge e idempotencia, S4 modelado energético, S5 incidentes/integridad, S6 pérdidas económicas, S7 auditorías/planes, S8 mantenimiento/misiones. Implementar reglas `RB-*` y pruebas `T*` de los documentos, sin marcar un sprint terminado por CRUD o screenshots.

- Gateway/ESP32 **no** heredan JWT humano: autenticar credenciales de máquina y resolver su tenant desde el dispositivo registrado.
- No confundir datos inconsistentes con ciberataque confirmado.
- No atribuir suciedad sin evidencia ni llamar «mapa térmico satelital» a Sentinel/GOES.
- Drones físicos deshabilitados, misión simulada/solicitud controlada hasta integración y permisos.

## Entregables y control

Crear/actualizar `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` con RB-ID implementadas, clase/método, endpoints, migración, tests, resultados y pendientes. Usar CLI oficial solo tras inspección. Ejecutar compilación y tests reales; registrar errores. No inventar resultados. Empezar por S1 tras validar S0.5, sin modificar masivamente todas las 67 entidades.
