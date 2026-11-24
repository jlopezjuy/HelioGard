# SDD backend — implementación incremental recomendada

**No regenerar directamente 67 CRUD en una base activa.** Usar el JDL como inventario de dominio y scaffolding, con servicios de negocio explícitos.

| Sprint | Función | Entidades/persistencia | Definition of Done |
|---|---|---|---|
| S0.5 | Congelar fuentes y validar dominio | JDL v5, ERD, Liquibase diff, DB de prueba, mapeo Idea Original cuando esté disponible | Import `--json-only`, revisión de cardinalidades, migración vacía y evolutiva |
| S1 | Identidad / multi-tenant | Organization, Membership, Site, Installation, AccessGrant + FKs/RLS | Tenant A no accede B; delegado sí con grant válido |
| S2 | Topología + edge | SolarSector, SolarArray, SolarString, InverterMpptInput, StringInputAssignment, Equipment, Gateway, ChannelBinding | Localizar nivel sector/string solo si datos lo permiten |
| S3 | Ingesta idempotente + offline | SQL recepción, secuencias, series; DeviceIdentity e IntegrityEvent | 100 eventos sin WAN se sincronizan una sola vez; replay ≠ duplicado legítimo |
| S4 | Meteorología/modelo físico | WeatherIngestionBatch, EnergyModelConfiguration, EnergyModelRun, energy_interval_result | `INSUFFICIENT_DATA` con modelo incompleto; cálculo versionado |
| S5 | Alertas + seguridad | AlertRuleVersion, Incident, Localization, SecurityCaseEvent/Evidence | Sin falsa alarma nocturna y sin imputar ataque sin evidencia |
| S6 | Economía | LossAssessment, LossCauseEstimate, TariffRate, EconomicAssessment | No doble conteo, desconocidos visibles, tarifa por vigencia |
| S7 | Auditoría / planes | AuditTemplate/Report/Signature, Benefit, Eligibility, Agreement | Versiones inmutables, checklist trazable, sin emitir seguros |
| S8 | Mantenimiento / dron | WorkOrderAssignment/Approval, Intervention, DroneMissionTarget, SafetyCheck, events, Recovery | autorización + checklist + estado; verificación INCONCLUSIVE si faltan datos |
| S9 | Endurecimiento | Pruebas E2E, rendimiento/particiones, backup y RLS | pruebas negativas multi-tenant y restauración, métricas completas |
| S10 | Piloto | Integración read-only con equipos reales | métricas calibradas y tiempos/costos observados |

**Backend modular:** `identity`, `assets`, `telemetry`, `energy`, `integrity`, `audits`, `operations`, `plans` bajo un mismo JHipster monolito. **Modelador Python** como worker de cálculo separado. No crear microservicio por cada módulo.
