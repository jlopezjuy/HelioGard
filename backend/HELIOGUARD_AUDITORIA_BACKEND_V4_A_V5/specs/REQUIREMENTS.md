# Especificaciones funcionales y de calidad — HelioGuard

## Dependencias previas

- **DEP-001**: inspeccionar dashboard actual y definir si se integra, se extiende o se migra; no asumir sus APIs.
- **DEP-002**: registrar modelo y protocolo de inversor real, muestras de telemetría y permisos de lectura.
- **DEP-003**: validar criterios de plan de servicio (no seguro) y responsables de inspección técnica.

## Historias funcionales prioritarias

### FR-001 — Registro multiempresa e instalaciones
**Como** administrador de una organización, **quiero** crear una instalación con datos físicos y ubicación **para** calcular un rendimiento de referencia.

**Criterios**: requiere potencia nominal, orientación, inclinación, altitud/ubicación y equipo asociado cuando se disponga; siempre registra propietario y tenant; usuario de otra organización no puede leer ni editar.

### FR-002 — Adquisición read-only
**Como** sistema, **quiero** recibir mediciones y alarmas del inversor sin intervenir su configuración **para** supervisarlo con seguridad.

**Criterios**: telemetría validada con schema; unidad y UTC correctos; estatus STALE/OFFLINE y origen de los datos; datos corruptos en cuarentena.

### FR-003 — Continuidad local
**Como** dueño, **quiero** consultar últimos datos de mi instalación mediante LAN sin internet **para** conocer el estado básico.

**Criterios Given/When/Then**:
- **Given** Raspberry y router activos sin WAN **When** Flutter se conecta por LAN **Then** se visualiza estado y últimas mediciones con etiqueta de frescura.
- **Given** WAN caída **When** llegan 100 eventos **Then** persisten en SQLite; **When** vuelve la WAN **Then** aparecen una sola vez en cloud.

### FR-004 — Producción esperada
**Como** dueño, **quiero** ver la energía esperada ajustada a la meteorología **para** separar clima de anomalías.

**Criterios**: modelo versionado, entrada meteorológica con frescura, salida en kWh y ventana; sin irradiancia confiable muestra INSUFFICIENT_DATA; no afirma suciedad solo por déficit.

### FR-005 — Alertas automáticas
**Como** propietario o técnico, **quiero** enterarme de errores del inversor, desconexión o pérdidas persistentes **para** reaccionar a tiempo.

**Criterios**: no generar alerta de cero producción nocturna; comunicar diferencia `OFFLINE_TELEMETRY` vs `INVERTER_FAULT`; deduplicación; estados y reconocimiento auditados.

### FR-006 — Pérdida energética y económica
**Como** propietario, **quiero** conocer kWh faltantes y valoración en dinero **para** decidir mantenimiento.

**Criterios**: kWh totales estimados separados de suciedad atribuible y residual desconocido; tarifa vigente; distingue autoconsumo/inyección o muestra escenarios con supuestos; cálculos deterministas con tests.

### FR-007 — Auditoría digital y presencial
**Como** técnico, **quiero** completar una auditoría de equipos y seguridad con evidencias **para** documentar hallazgos y proponer correcciones.

**Criterios**: plantilla versionada, fotos y mediciones, hallazgos priorizados, bloqueo de firma incompleta, firma de profesional cuando aplique, progreso offline.

### FR-008 — Plan de mantenimiento y propuesta
**Como** proveedor, **quiero** cotizar planes conforme al estado y ubicación **para** establecer servicios y compromisos sostenibles.

**Criterios**: plan versionado, vigencia, SLA, viajes/visitas, límites y exclusiones; no emitir pólizas desde nuestra aplicación.

### FR-009 — Orden técnica y recuperación
**Como** técnico, **quiero** recibir una OT y registrar evidencia de intervención **para** comprobar la recuperación del sistema.

**Criterios**: autorización de propietario cuando corresponda, asignación, fecha, materiales, prueba posservicio meteorológicamente comparable, estado INCONCLUSIVE si faltan datos.

### FR-010 — Riesgos regionales
**Como** operador, **quiero** reglas diferenciadas para Puna, Quebrada, Valles, Yungas y Chaco **para** prevenir falsas alertas y daños.

**Criterios**: temperatura fría no supone hielo; sensor BMS tiene prioridad para restricciones de carga; eventos meteorológicos no equivalen a daño comprobado; trazabilidad de fuente.

## Requisitos no funcionales

- **NFR-001 Offline:** gateway captura y conserva datos de manera independiente del VPS; la retención local se define por capacidad SSD y frecuencia real.
- **NFR-002 Seguridad:** lectura pasiva en OT; HTTPS, AuthZ tenant, no MQTT/Modbus públicos; secretos fuera de repositorio.
- **NFR-003 Idempotencia:** toda ingestión tiene eventId y gatewayId; duplicados y reintentos no duplican métricas.
- **NFR-004 Data quality:** cada cálculo indica origen, timestamps, método, cobertura y `qualityFlags`; no estimación silenciosa ante faltantes.
- **NFR-005 Mantenibilidad:** tests, lint, OpenAPI versionado, ADR, JDL/Liquibase controlados.
- **NFR-006 Observabilidad:** métricas gateway-cloud, rate de errores, salud y alertas por desconexión.
- **NFR-007 Resiliencia:** backup y restore probados, reinicio de contenedores, reconexión con backoff.
- **NFR-008 Accesibilidad:** interfaz responsive, texto claro, indicadores no solo por color.
- **NFR-009 Privacidad:** acceso a fotos, planos y ubicación restringido por rol; retención documentada.
- **NFR-010 Seguridad física:** control eléctrico y deshielo no dependen de LLM, Flutter ni conectividad.

## Regla transversal

Toda feature nueva: `SPEC → contrato → test → CLI/scaffold → implementación → prueba funcional → evidencia`. No declarar lista una característica no observada/ejecutada.
