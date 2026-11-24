# Changelog v4 → v5 (propuesta no desplegada)

- Mantiene las 47 entidades JDL de v4; añade 20 y suma 178 relaciones.
- Topología: sectores georreferenciados, entradas MPPT, asignaciones históricas y conexiones de equipos.
- Edge: `TelemetryChannelBinding`, modelo SQL central de dedup `(gateway,eventId)` y secuencias `(deviceUid,bootId,sequenceNumber)` separado.
- Clima/energía: perfil climático, importación meteorológica por lote, configuración versionada, banda de confianza y estado `INSUFFICIENT_DATA` con esperado nulo.
- Diagnóstico: localización con scope de medición y versiones de reglas.
- Seguridad: evidencias y eventos multiempresa de investigación.
- Operaciones: autorización de trabajo, asignación histórica, objetivos y checks de dron, bitácora, servicios concretos por plan y elegibilidad.
- Auditoría: evidencia de firma y hallazgos enlazables al equipo.
- Normalización: removidos `Equipment.kind`, `Incident.alertRule`, `AlertRule.thresholdValue`, `AlertRule.minPersistenceSeconds`, `WorkOrder.assignedMembership` por fuentes de verdad redundantes.
- El archivo JDL es **DRAFT**; no se han ejecutado JHipster ni migraciones PostgreSQL.
