# Prompt para Codex — Auditoría v4, contrato funcional y evolución a v5

Actuá como **arquitecto Java/JHipster, modelador relacional PostgreSQL, experto en sistemas fotovoltaicos, seguridad de telemetría IoT y QA multi-tenant**. Sé estricto, concreto y verificable.

## Fuentes obligatorias

- `/Users/juanlopez/Proyectos/Solar/Idea Original` (fuente primaria de funcionalidades; solo lectura).
- `/Users/juanlopez/Proyectos/Solar/ideas` (especificaciones y demos).
- `specs/domain.v4.draft.jdl` y `docs/00_ARQUITECTURA_MAESTRA_SDD.md` dentro de la estructura real de `ideas`; localizá las rutas antes de asumirlas.
- `database/*.sql`, `contracts/*.json`, `contracts/openapi.yaml`, `docs/02_SPRINTS_INTEGRALES_S0_S10.md`.
- `/mnt/data/AUDITORIA_HELIOGUARD_BACKEND_JDL_V4_A_V5.md` **solo si está copiado al repositorio**. Si no existe, continuá sin él.
- Backend JHipster existente, si realmente existe; no lo inventes ni crees automáticamente otro proyecto.

## Trabajo

1. Inventariá y extraé requisitos de la **Idea Original** con citas de archivo/sección. Distinguilos de ampliaciones posteriores (ciberseguridad, drones de limpieza, seguros, planes, etc.).
2. Compará requisitos contra JDL v4 (47 entidades), SQL/Liquibase, contratos, código backend y mocks Angular. Evidencia objetiva: `CUBIERTO`, `PARCIAL`, `SOLO SIMULADO`, `AUSENTE`, `NO VERIFICABLE`.
3. Audita normalización 3FN, cardinalidades, nomenclatura, índices, unidades, versiones, estados, dependencias funcionales y aislamiento multi-tenant. Presta especial atención a instalaciones ajenas atendidas por técnicos externos, topología string↔MPPT↔inversor↔sector GIS, auditorías, planes, pérdidas, drones y telemetría.
4. Detectá contradicciones concretas entre `database/telemetry.example.sql` y `database/security-integrity.example.sql` (existen dos tablas propuestas para el mismo dedup), así como diferencias entre IDs string de eventos y IDs JPA Long.
5. Modelá la ingestión con idempotencia `(gatewayId,eventId)` y replay por `(device,bootId,sequenceNumber)`. Separá retransmisión legítima, fallo de sensor, inconsistencia física y ataque confirmado. Datos sensibles y claves privadas fuera de JPA.
6. Diseñá reglas de negocio verificables: generación esperada con meteorología/incertidumbre, atribución honesta de pérdidas, clima extremo de Jujuy/Salta, alertas sin duplicados, inspecciones, servicio, órdenes, misión de dron con autorización y comprobación de recuperación.
7. Diseñá la frontera **SIMULATED vs REAL**, con bloqueo de efectos externos, fixtures deterministas y pruebas de que la demo no contamina KPIs ni dispara hardware.
8. Proponé **cambios mínimos y trazables** a JDL: campos, enums, relaciones y entidades nuevas con justificación. No generes CRUD para tablas de alta frecuencia. Series temporales, outbox, RLS, FKs compuestas y retención se implementan mediante Liquibase SQL complementario.
9. Si hay un backend real, hacé migraciones `expand/contract` sobre una copia de DB y tests; no sobrescribas bases productivas ni liquides datos existentes.
10. Usá CLI oficial JHipster fijando su versión, validá sintaxis/importación JDL, compilá backend e inspeccioná migraciones. Si CLI/DB no están disponibles, marcá `NO EJECUTADO`, sin fingir validación.

## Entregables en `ideas/docs/backend-review/`

- `01_MATRIZ_IDEA_ORIGINAL_VS_V4.md` (requisito → fuente → dominio → API → estado → evidencia → gap).
- `02_AUDITORIA_JDL_3FN_MULTITENANT.md` (hallazgos con P0/P1/P2 y ejemplos de filas cruzadas).
- `03_REGLAS_DE_NEGOCIO_Y_ESTADOS.md` (invariantes y diagramas de transición/decisión).
- `04_PROPUESTA_JDL_V5.md` (diff semántico, justificación por entidad).
- `05_PLAN_MIGRACION_LIQUIBASE_Y_QA.md` (migraciones, índices, RLS, rollback/forward fix, tests).
- `06_SPRINTS_BACKEND_SDD.md` (backlog de implementación dependiente de pruebas).
- `specs/domain.v5.draft.jdl` (crealo **solo después** de validar hallazgos; conservá v4 intacto).

## Restricciones

No modifiques `Idea Original`, no toques Angular/maquetas ni componentes visuales, no despliegues misiones reales, no cambies infra de producción, no uses datos inventados como mediciones y no afirmes detectar el panel exacto desde un satélite. No modifiques entidades por intuición si una entidad existente puede cumplir correctamente el objetivo.

**Primero entregá la auditoría y los invariantes. Después implementá cambios de dominio en una rama aislada cuando haya evidencia suficiente y los tests se puedan ejecutar.**
