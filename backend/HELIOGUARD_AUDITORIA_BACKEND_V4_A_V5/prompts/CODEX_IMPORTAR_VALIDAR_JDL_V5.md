# Prompt — Codex: validar HelioGuard JDL v5 y normalización

Actuá como arquitecto JHipster/Spring Boot senior, DBA PostgreSQL y auditor SDD.

**Fuentes**: `specs/domain.v4.original.jdl`, `specs/REQUIREMENTS.md`, `specs/domain.v5.review.jdl`, `docs/NORMALIZACION_Y_CAMBIOS_V5.md`, `database/*`. También inspeccioná **en la Mac local** `/Users/juanlopez/Proyectos/Solar/Idea Original` y el backend existente, si lo hubiera. No supongas que estas carpetas fueron revisadas por la IA que preparó la propuesta.

1. Identificá la versión exacta instalada de Java, JHipster CLI, Node y PostgreSQL. Confirmá en la documentación del generador la sintaxis JDL, incluidas opciones `pagination`, `TextBlob`, enums, fechas y `with builtInEntity`.
2. En una **carpeta de prueba separada**, ejecutá `jhipster jdl <ruta-absoluta>/specs/domain.v5.review.jdl --json-only`, validá los JSON generados y corregí cualquier incompatibilidad **sin tocar v4**. Después generá un proyecto sandbox JHipster **backend-only** y probá Liquibase.
3. Contrastá el JDL con `Idea Original`, `REQUIREMENTS.md` y código actual: marcá implementado/parcial/faltante/no verificable. No inventes comportamientos o precisión satelital.
4. Revisá cuidadosamente 1FN/2FN/3FN, dependencias funcionales, claves candidatas, condiciones `required`, únicas por tenant, índices y relaciones cross-tenant. Documentá las excepciones deliberadas (organization_id duplicado, sumarios inmutables).
5. Aplicá **Liquibase incremental** para constraints y series temporales: no copies plantillas SQL sin verificar DDL generado, permisos, columnas e historial. Garantizá una sola tabla canónica de dedup para `(gatewayId,eventId)` y otra identidad de secuencia `(deviceUid,bootId,seq)`.
6. Implementá servicios y guardas de negocio para tenencia, ingestión read-only, meteorología/energía, alertas, consentimientos, asignaciones, auditorías, planes y misiones de dron. Nunca permitas que un evento simulado controle hardware, pagos o drones reales.
7. Tests obligatorios: tenant A/B, grant expirado, boot+seq repetidos vs reenvío legítimo, día nublado, `INSUFFICIENT_DATA`, precio de exportación, contrato versionado, limpieza insegura y dron sin autorización.
8. Entregá: `VALIDACION_JDL_V5.md`, migraciones Liquibase, ERD generado, matriz de trazabilidad actualizada, comandos ejecutados y resultados de tests. **No declares JDL compilado sin ejecutar realmente el CLI**.

**No regeneres 67 endpoints CRUD públicos ni hagas migración destructiva.** Revisá primero en rama aislada y convertí los hallazgos en tareas SDD verificables.
