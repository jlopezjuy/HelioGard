# HelioGuard — Especificación de lógica de negocio v5

**Estado:** especificación funcional propuesta a implementar sobre el backend JHipster existente. Deriva de `REQUIREMENTS.md`, `specs/domain.v5.review.jdl` y las decisiones del proyecto. No constituye código implementado ni prueba de comportamiento.

## Decisión transversal RB-TEN-001: organización desde la identidad

- Para endpoints REST **humanos**, el backend resuelve `Organization` desde usuario autenticado (JWT → principal/login → `OrganizationMembership` ACTIVE → `Organization` ACTIVE). El `organizationId` nunca se recibe como parámetro de ámbito para CRUD ordinarios de instalaciones, auditorías, incidentes, contratos, telemetría o misiones. Un request que incluya `organizationId` en DTO no debe utilizar ese valor como autoridad; idealmente los DTO no lo exponen.
- Una membresía activa: resolver automáticamente; ninguna: 403; varias: requerir contexto de empresa activa inequívoco (claim firmado del JWT, emitido tras selección autorizada); sin claim válido: `TENANT_SELECTION_REQUIRED`, no elegir el primero.
- La selección de empresa **es una operación explícita de autenticación**, por ejemplo `POST /api/auth/active-organization {organizationId}`, con validación de membresía activa antes de emitir nuevo JWT; no se usa ese campo en endpoints de negocio. La selección puede hacerse por código/slug, pero siempre se valida. Si se exige no enviar jamás ninguna referencia de empresa, la operación multiempresa queda sin resolver: el usuario solo podrá actuar en una organización predeterminada determinada por el servidor.
- Revocación/suspensión: revalidar membresía y organización por cada operación crítica o utilizar revocación consistente de sesiones/tokens; nunca confiar exclusivamente en un claim antiguo.
- Lecturas de `installationId`, `siteId`, `workOrderId`, etc. sí pueden usar ID de *recurso* en path; SIEMPRE exigir propiedad o permiso delegado, también para listas, archivos, paginación y actualizaciones.
- Las integraciones máquina-a-máquina **no usan JWT humano**: Raspberry/edge obtiene su `organization` e `installation` mediante `Gateway` + `DeviceIdentity` registrados y autenticados; nunca por `organizationId` arbitrario incluido en el payload.
- Logs y trabajos batch: contexto tenant explícito del proceso controlado por servidor; no simular un usuario humano. RLS/`SET LOCAL` para Postgres requiere diseño transaccional, usuario DB runtime sin BYPASSRLS, FKs compuestas y pruebas de conexiones reutilizadas.

## Modelo de autorización

- `ROLE_ADMIN` JHipster: capacidad de plataforma solo cuando esté expresamente autorizada, **no** equivale a OWNER de cada tenant.
- `OrganizationMembership`: OWNER/ADMIN/OPERATOR/TECHNICIAN/AUDITOR/VIEWER con estado. Operación permitida = autenticación + membresía activa + rol/permiso + recurso + condiciones de negocio.
- `InstallationAccessGrant`: al prestador B se le permite actuar sobre *la instalación puntual* propiedad de A, según alcance y vigencia. `ServiceAgreement` y `WorkOrderAssignment` limitan el trabajo asignado. El propietario de datos sigue siendo A.
- Para tareas delegadas, evaluar `actingOrganization` (prestadora), `resourceOwnerOrganization` (propietaria), operación, alcance del grant, acuerdo/OT y miembro asignado. No cambiar `organization_id` del recurso para facilitar la delegación.
- Campos de auditoría `createdBy`, `actorOrganizationId`, `timestamp`, correlación, cambios de estado y motivo cuando aplique; el origen de la identidad es el token validado y las relaciones en DB.

## Módulo identidad e instalaciones

**RB-ID-001** Crear instalación requiere membresía ACTIVE con rol con permiso `INSTALLATION_CREATE`, `Site` perteneciente a empresa actual, `nominalDcKw > 0`, `gridMode` válido, código único por tenant. Asignar org en servidor. Inicial `COMMISSIONING`; habilitar `ACTIVE` tras chequeos mínimos definidos.

**RB-ID-002** Listar y detalle: únicamente instalaciones propias o delegadas. Los filtros enviados por frontend nunca amplían visibilidad; acceso externo expuesto en proyecciones limitadas.

**RB-ID-003** Mover equipos o instalaciones a otra empresa requiere workflow administrativo explícito con auditoría; prohibida actualización genérica del `organization`.

**RB-ASSET-001** Inventario: `EquipmentModel` describe fabricante/modelo; `Equipment` es instancia con serie y fechas. No confundir `Equipment` con `SolarString`, `SolarSector`, `InverterMpptInput`.

**RB-ASSET-002** Topología sector → array → string → MPPT/inversor con ventanas temporales. Prohibir asignaciones activas incompatibles y referencias cruzadas. No localizar al nivel módulo sin medición/evidencia que lo sostenga.

## Ingestión, edge y disponibilidad

**RB-TEL-001** Autenticar gateway/dispositivo; resolver tenant desde `Gateway` vinculado y estado de `DeviceIdentity`. Cifrado y esquema de eventos versionado. Rechazar instalación ajena.

**RB-TEL-002** Normalizar `metricCode`, unidad canónica, timestamp UTC, `eventId`, `bootId`, secuencia, canal, origen, calidad, instalación. Campos faltantes provocan `REJECTED`, `QUARANTINED` o `UNVERIFIABLE` según contrato explícito, sin introducir medición silenciosa.

**RB-TEL-003** Deduplicación transaccional por `(gateway_id,event_id)` y/o identificador único por dispositivo estable; un reintento legítimo HTTP devuelve resultado idempotente; mismo eventId con payload distinto genera incidente de integridad. No equiparar automáticamente duplicado legítimo con ataque replay.

**RB-TEL-004** Validar `bootId + sequenceNumber` por fuente; permitir reinicios reconocidos; retardos fuera de orden, saltos y reloj desincronizado generan banderas según tolerancias versionadas.

**RB-TEL-005** Si la WAN cae, Raspberry sigue capturando y guardando outbox SQLite con IDs inmutables; después sincroniza por HTTPS con backoff, checkpoints y deduplicación. Flutter LAN puede mostrar datos con indicador `fresh/stale/offline`, pero push remoto depende de red.

**RB-TEL-006** Diferenciar `INVERTER_FAULT`, `TELEMETRY_OFFLINE`, `WEATHER_LIMITED`, `NO_PRODUCTION_NIGHT` y `DATA_SUSPECT`. Nunca generar `PANEL_FAILURE` por producción nocturna cero.

## Meteorología y producción energética

**RB-ENE-001** El worker Python (pvlib) o motor versionado calcula producción AC esperada por intervalo a partir de ubicación, potencia, arreglo, orientación, inclinación, pérdidas previstas, edad y radiación/temperatura con procedencia y calidad. No prometer que Sentinel/GOES produce termografía de módulos.

**RB-ENE-002** Si falta irradiancia útil, topología, configuración, suficientes muestras o hay periodos incomparables, crear `EnergyModelRun(status=INSUFFICIENT_DATA, expectedWh=null)`. No reemplazar silenciosamente por cero.

**RB-ENE-003** Diferenciar potencia W/kW de energía Wh/kWh; integrar intervalos válidos o usar deltas del contador; detectar reinicio/desborde del contador. `deficitWh = max(0, expectedWh-actualWh)` en intervalos comparables, informar banda de incertidumbre y cobertura.

**RB-ENE-004** La nubosidad o frío modelados no se consideran automáticamente pérdida evitable. La hipótesis `SOILING` solo se propone con evidencia suficiente (tendencia, sensor comparativo o inspección), no por déficit aislado.

## Integridad de datos y ciberseguridad

**RB-SEC-001** Replay exacto: marcar contexto, origen, secuencia/timestamp/firma y poner evento sospechoso en cuarentena lógica sin contaminar métricas confiables; conservar evidencia mínima. No bloquear toda la planta ante un único duplicado de transporte.

**RB-SEC-002** Inyección físicamente inconsistente: contrastar capacidad, límites eléctricos, irradiancia, mediciones alternativas y tolerancias. Resultado `SUSPECT/UNDER_REVIEW`, NO `ATTACK_CONFIRMED` solo por divergencia.

**RB-SEC-003** Autenticación fallida y firma inválida: rechazar ingesta, registrar incidente de seguridad, rate-limit y métricas; nunca guardar secretos en logs ni JPA.

**RB-SEC-004** `SecurityIncident` mantiene estado, evidencias, resolución, actor y fecha; transiciones y permisos se validan en servicio, sin endpoints CRUD libres para cambiar `status`.

## Anomalías, incidentes y localización

**RB-INC-001** `AlertRuleVersion` es inmutable una vez usada; procesa cobertura mínima, persistencia, cooldown, histéresis y calidad de medición. No convertir evento climático en certeza de avería.

**RB-INC-002** Clave deduplicación `(installation, ruleVersion, affectedScope)` activa; una alarma repetida actualiza `lastSeenAt` y agrega evento, no crea cientos de tickets. Cierre/reapertura según reglas explícitas.

**RB-INC-003** Estados de incidente propuestos: `OPEN → ACKNOWLEDGED → IN_PROGRESS → RESOLVED → CLOSED`. Permitidas transiciones de cancelación/reapertura solo si el modelo las agrega o se documenta workaround; registrar actor/fecha.

**RB-LOC-001** La localización se determina por resolución de telemetría: INSTALLATION, SECTOR, INVERTER, STRING, MODULE, UNKNOWN. No asignar coordenadas de un panel desde imagen satelital meteorológica; una inspección con dron puede actualizar precisión de forma versionada.

## Pérdida económica

**RB-ECO-001** `LossAssessment` registra déficit residual desconocido; causas atribuidas no superan déficit total ni se suman entre sí si son explicaciones alternativas.

**RB-ECO-002** Valor económico según tarifa de vigencia: energía que habría sido autoconsumida × tarifa evitable + energía que habría sido exportada × tarifa de inyección. Si faltan curvas de consumo/inyección, reportar **escenarios con supuestos**, no una cifra exacta.

**RB-ECO-003** No prometer recuperación de dinero por limpieza sin estimar duración del beneficio, costo de visita/dron, seguridad y evidencia de recuperación posterior comparable.

## Auditorías y elegibilidad comercial

**RB-AUD-001** `AuditReport` inicia DRAFT, pasa IN_PROGRESS/SUBMITTED y solo APRUEBA con preguntas obligatorias resueltas, hallazgos críticos tratados o explicitados, evidencias requeridas y firma profesional cuando aplique.

**RB-AUD-002** Plantilla/versiones publicadas no se reescriben retroactivamente. Fotos, archivos y firma están protegidos por tenant y autorización de auditor asignado.

**RB-PLAN-001** `PlanEligibilityAssessment` es resultado de reglas versionadas: ELIGIBLE, CONDITIONAL, REQUIRES_REMEDIATION, INCOMPLETE, NOT_ELIGIBLE. Una falla eléctrica crítica bloquea servicios que presuponen operación segura; no supone contratación automática.

**RB-PLAN-002** `MaintenancePlanVersion` y beneficios incluidos quedan congelados en suscripción; precio, cobertura, visitas y SLA se extraen de esa versión. Planes de mantenimiento no son pólizas.

## Órdenes, técnicos y drones

**RB-WO-001** Crear OT desde incidente/auditoría o manual con permiso vigente. `WorkOrder.organization` corresponde al dueño de la instalación; prestador externo se enlaza por `WorkOrderAssignment` y grant, no se convierte en propietario de la OT.

**RB-WO-002** Secuencia: CREATED → ASSIGNED → ACCEPTED → IN_PROGRESS → DONE → VERIFIED; CANCELLED con causa según reglas. Requiere autorización del propietario si corresponde, evidencia final y revisión/verificación independiente antes de VERIFIED.

**RB-WO-003** Un técnico externo puede aceptar solamente una OT que le fue asignada con membresía ACTIVE en la organización prestadora, grant válido y relación vigente. Rechazar asignación a otra instalación con mismo tenant pero sin grant.

**RB-DRONE-001** `DroneMission` es propuesta con objetivo y localización sustentada; revisión de checklist y contexto meteorológico antes de solicitar autorización. Nunca invocar vuelos físicos desde operaciones mock.

**RB-DRONE-002** Estados: DRAFT → SAFETY_REVIEW → AWAITING_AUTHORIZATION → AUTHORIZED → DISPATCHED → IN_FLIGHT → COMPLETED. ABORTED solo con causa y actor. Aprobación solo por rol competente; denegación/expiración bloquean despacho.

**RB-DRONE-003** Viento, hielo, equipos energizados dañados, método de limpieza incompatible, falta de operador o permiso: bloquear misión de limpieza; permitir solo inspección si evaluación y operación cumplen condiciones reales. En MVP **la misión es simulada**.

**RB-REC-001** Verificar recuperación exige ventana comparable normalizada por irradiancia y calidad; ausencia de datos → `INCONCLUSIVE`, nunca dar limpieza como exitosa por cambio de color en UI.

## API humana sin empresa en path ni body

| Operación | Método/ruta | Cómo se resuelve organización |
|---|---|---|
| Perfil | `GET /api/me/context` | JWT + membership ACTIVE |
| Listar instalaciones | `GET /api/installations` | CURRENT tenant + delegaciones válidas |
| Crear instalación | `POST /api/installations` | `organization` asignado en servidor |
| Detalle | `GET /api/installations/{installationId}` | Verificación de recurso por ID |
| Crear auditoría | `POST /api/audit-reports` | Resolver instalación autorizada |
| Incidentes | `GET /api/incidents` | Owner actual + scopes delegados |
| Crear OT | `POST /api/work-orders` | Owner org derivado de instalación; actor de JWT |
| Solicitar misión | `POST /api/drone-missions` | Instalación + permiso + aprobación posterior |
| Cambiar organización activa | `POST /api/auth/active-organization` | **Excepción controlada**: selección de organización autorizada para emitir nuevo JWT; no CRUD |

**Las operaciones máquina-a-máquina tienen otro flujo:** `POST /api/ingest/telemetry` resuelve organización desde credenciales de gateway registradas y valida cada evento; no desde JWT humano, ni desde tenant libre del payload.

## Definition of Done transversal

- Unit: casos límite y fórmulas exactas; integración: JPA + Liquibase + DB real de prueba; seguridad: usuarios de 2 tenants, usuario multiempresa, técnico delegado, JWT suspendido, IDOR en detalle/archivo, RLS pool, dos gateways cruzados.
- Idempotencia: 100 reintentos tras caída WAN resultan en 100 eventos únicos; mismo ID + payload diferente se investiga.
- API: DTO no incluye `organizationId` editable; IDs de instalaciones/OT sí cuando permiten identificar recursos; todos los tests verifican estado y side effects.
- Medición: timestamp UTC y units, porcentaje de cobertura y procedencia; resultados no confiables preservan estado explícito.
