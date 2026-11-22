# PROMPT — implementar HelioGuard v4 integral (JHipster, Edge, Angular, Flutter, Docker)

Actuá como arquitecto principal full-stack, especialista IoT/seguridad OT y líder QA. Trabajá por SDD y ejecutá lo documentado en `docs/02_SPRINTS_INTEGRALES_S0_S10.md`. **Inspeccioná primero dashboard/código real**, no reemplaces nada sin inventario ni respaldo. Fuente de verdad: `specs/domain.v4.draft.jdl` (47 entidades, 114 relaciones propuestas, pendiente de validar con CLI), `docs/01_MODELO_ER_3FN_MULTITENANT.md`, `docs/06_SEGURIDAD_TELEMETRIA_ATAQUES.md`, `contracts/` y `docs/00_ARQUITECTURA_MAESTRA_SDD.md`.

**Stack**: JHipster backend-only con PostgreSQL/Liquibase, Java 21 según generador fijado, Angular/Tailwind, Flutter/FVM, Raspberry Pi con SQLite/MQTT y ESP32 opcional, motor de energía Python/pvlib, Docker Compose para servicios. Usá los CLI oficiales de cada tecnología y verifica compatibilidad/estado antes de ejecutar.

1. Sprint 0/S0.5 bloqueante: inventariar repositorio y equipos, validar JDL, relaciones y esquema, escribir migraciones y pruebas de aislamiento tenant. El JDL no resuelve RLS ni seguridad automáticamente.
2. S1–S3: auth real, multiempresa, acceso delegado, telemetría y edge offline/outbox idempotente con credenciales únicas. Inversor/Modbus **solo lectura**.
3. S4–S6: modelado energético con procedencia y calidad, alerta, replay/ciberseguridad, pérdidas económicas sin doble conteo.
4. S7–S8: auditoría técnica, planes de servicio separados de seguros, órdenes, técnico, misiones propuestas y autorización supervisada, verificación postintervención.
5. S9–S10: QA negativa RLS/IDOR, backups/restauración, pruebas con WAN caída, mobile offline, ARM64, piloto real documentado.

Para cada módulo: SPEC con eventos/roles/casos límite → ADR/ER/OpenAPI → tareas → implementación → unit/integration/contract/E2E → revisión visual → evidencia. Nunca afirmar que un satélite hace termografía individual, que inconsistencia física confirma ataque ni que el software autoriza vuelo real.

**Criterio entrega:** scripts/versiones fijadas, migrations exitosas en ambiente de prueba, cobertura de permisos (tenant cross/delegación), resultados CLI verificables, pantallas/Flutter funcionales, estado de hardware/no hardware explícito, riesgos y lista de pendientes.
