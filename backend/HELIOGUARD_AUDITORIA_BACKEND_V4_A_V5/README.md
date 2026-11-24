# HelioGuard Backend — JDL v5 revisado y paquete de normalización

**Archivos fuente reales de usuario:** `specs/domain.v4.original.jdl`, `specs/REQUIREMENTS.md`.

**Propuesta resultante:** `specs/domain.v5.review.jdl` — 67 entidades / 178 asociaciones / 44 enums. Preserva 47 entidades de v4 y agrega 20.

## Archivos

- `specs/domain.v5.review.jdl`: modelo JDL actualizado, borrador no importado en JHipster real.
- `docs/NORMALIZACION_Y_CAMBIOS_V5.md`: cambios de v4, separación transaccional/serie temporal, análisis 1FN/2FN/3FN y límites.
- `docs/TRAZABILIDAD_REQUIREMENTS.md`: FR-001..010 → dominio/servicios.
- `docs/SPRINTS_BACKEND_V5.md`: plan SDD S0.5–S10.
- `database/01_telemetry_timeseries_template.sql`: SQL de referencia con única fuente canónica de idempotencia.
- `database/02_integrity_constraints_template.sql`: FKs compuestas, vigencias y unicidades; **no ejecutar sin adaptación**.
- `database/03_service_invariants.md`: reglas que pertenecen a Spring/seguridad/operación, no a JDL.
- `tests/validate_jdl_structure.py`: validador estático de declaraciones, referencias, duplicación y referencias de campos.
- `prompts/CODEX_IMPORTAR_VALIDAR_JDL_V5.md`: proceso de prueba real con JHipster CLI.
- `build_v5.py`: script reproducible que transforma **exactamente** el JDL v4 adjunto en el modelo v5. No tiene dependencias.

## Uso

```bash
python3 tests/validate_jdl_structure.py

# Ejecutar dentro de una aplicación sandbox JHipster, NO en producción:
jhipster jdl /ruta/helioguard_backend_v5/specs/domain.v5.review.jdl --json-only
```

Después generar/inspeccionar migraciones Liquibase en una base vacía de prueba y simular upgrade con una copia de v4 si existe. Confirmar versión CLI, PostgreSQL e invariantes multiempresa.

**Estado real de validación:** control estructural Python pasa; **NO se ejecutaron** CLI JHipster, Spring Boot, PostgreSQL, RLS, Liquibase, pruebas de negocio ni contrastación con la carpeta privada `Idea Original`.
