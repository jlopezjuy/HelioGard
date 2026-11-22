# SDD — Plan integral HelioGuard: S0, S0.5 y S1–S10

**Cadencia estimativa:** 1–2 semanas por sprint en equipo; hackatón prioriza entrega F0–F7 del frontend mock de forma paralela. Validar hardware real y disponibilidad del equipo antes de fijar plazos. JDL v4 = **47 entidades / 114 relaciones declaradas, BORRADOR**.

## DoD por cada sprint

- Spec con historia + datos + estados + amenazas + criterios de aceptación.
- ADR y contrato JSON Schema/OpenAPI/enum actualizados si cambia el comportamiento.
- Implementación con CLI oficial, migraciones reproducibles y configuración Docker donde corresponda.
- Tests unitarios, integración/contrato, al menos un test negativo multiempresa y registro de resultados reales.
- Demo vertical operable; capturas/video del recorrido; fallas no escondidas.

| Sprint | Especificación y dominio | Backend JHipster / Edge / Docker | Angular | Flutter | DoD distintivo |
|---|---|---|---|---|---|
| S0 Descubrimiento | Auditoría del sistema/dashboard existente, protocolos, marca/modelo del inversor, perfiles de usuarios, fixtures | Validar CLI, ARM64 y repos; fuente de datos real vs simulada | Inventario visual y navegación actual | Flujos campo/proprietario | Decidir conservar/adaptar dashboard y alcance hackatón |
| S0.5 ERD | 47 entidades, 114 relaciones, 3FN, RLS y accesos delegados | JDL parseado con CLI, Liquibase, SQL constraints, dos tenants | Contratos TS y matriz ER ↔ rutas | Modelos offline y políticas de tenant | Sin JDL generado a ciegas; pruebas de aislamiento |
| S1 Identidad | Organization, Membership, Site, Installation, AccessGrant | App JHipster, auth, tests, PostgreSQL | Shell y organización activa | Login/pairing conceptual | Un usuario multiempresa no mezcla registros |
| S2 Inventario IoT | EquipmentModel, Array, String, Equipment, Gateway, MetricDefinition, Channel, DeviceIdentity | Lectura solo-read Modbus/API, MQTT, eventos idempotentes, catálogos | Circuito solar, mapa de sectores, telemetría | Salud de gateway local | Una muestra medible y con timestamp, fuente y unidad |
| S3 Resiliencia edge | outbox/sync, cursors, autorización gateway, clock, reconexión | Raspberry SQLite, HTTPS, Mosquitto/ACL, outbox; broker caído/WAN abajo | Sincronización y frescura | LAN offline con cache y reintentos | 24h fake WAN sin perder/duplicar datos simulados o pruebas reales |
| S4 Modelos y procedencia | EnergyModelRun, EvidenceSource, DataProvenance | pvlib, ingestión meteorológica autorizada, etiquetas de calidad/incertidumbre | Esperado vs real con procedencia | Rendimiento diario | Datos reales y simulados siempre distinguidos; kW ≠ kWh |
| S5 Alertas + integridad | AlertRule, Incident/Event, IntegrityEvent, IntegrityAssessment, SecurityIncident | Dedup, secuencias/replay, validación física y aislamiento de eventos sospechosos | Bandeja energética + seguridad, filtros y evidencias | Aviso, reconocimiento y revisión | Replay determinista rechazado; inconsistencia ≠ ataque probado |
| S6 Económico | LossAssessment, LossCauseEstimate, TariffPlan/Rate, EconomicAssessment | Agregaciones por intervalos, valor autoconsumo/exportación, no doble conteo | Pérdidas ARS y rentabilidad de intervención | Resumen costo/energía | Casos de tarifa, batería, nube y restricciones bien diferenciados |
| S7 Auditoría | AuditTemplate, Question, Report, Answer, Finding, Evidence | Versionado de preguntas, fotos, firmas y autorización | Checklist y expediente | Inspección offline con captura y sync | Auditoría reabierta con evidencia inmutable y permisos |
| S8 Misiones + mantenimiento | MaintenancePlan/Version, Agreement, Subscription, Proposal, WorkOrder/Task, Intervention/Evidence, RecoveryAssessment, DroneAsset/Mission/Auth, VerificationResult | Contratos, transiciones de estados, decisión supervisada, seguridad meteorológica y del fabricante | OT, despacho, flujo cenital dron/técnico/hogar | Técnico: aceptar, evidenciar, cerrar; piloto autorizado | No despegar sin aprobación; no confundir inspección con limpieza |
| S9 QA/hardening | Revisión dominio completo y amenazas | Backups/restauración, RLS pooling, Secrets/TLS, OWASP, resiliencia ARM64 | E2E/AA/perf, QA visual | Offline, batería, permisos, sincronización | Reporte QA + fallas resueltas o riesgos declarados |
| S10 Piloto | KPIs y mediciones reales | Instalar gateway en entorno autorizado; medir precisión y costos | Centro operaciones piloto | Técnico/proprietario piloto | Comparación con evidencia, tasas de falsos positivos y SLA medidos |

## Orden de generación con CLI

1. `S0.5`: validar JDL y cross-FKs en proyecto de prueba, no importar incondicionalmente.
2. `S1`: JHipster backend-only + migraciones + seguridad tenant (sin permitir CRUD global generado por defecto).
3. `S2-S3`: gateway edge desplegable de forma independiente y mock generador de telemetría.
4. `S4-S6`: módulos de cálculos y seguridad con contratos versionados.
5. `S7-S8`: operaciones y apps de campo.
6. `S9-S10`: QA integral y piloto autorizado.

## Criterios de éxito del piloto (definir objetivos tras medir base)

- Detección y recuperación sin confundir errores de red con pérdida energética.
- Integridad: identidad validada, duplicados rechazados, eventos falsamente sospechosos auditables.
- Deficit energético medido por intervalo con fuente y modelo explícitos.
- Ruta: incidente → orden → misión propuesta → aprobación → resultado → verificación.
- Tenant A jamás ve ni altera datos B sin grant vigente.
- Costos medidos, no inventados, y compatibilidad de hardware real documentada.
