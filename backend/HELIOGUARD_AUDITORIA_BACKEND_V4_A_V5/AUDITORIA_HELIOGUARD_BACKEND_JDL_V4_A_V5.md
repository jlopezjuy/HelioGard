# HelioGuard — Auditoría del backend y JDL v4 → propuesta v5

**Fecha:** 2026-11-23 · **Estado:** evaluación documental, no implementación · **Documento base:** `MODELO_HELIOGUARD_V4_BORRADOR.jdl` (47 entidades, 114 relaciones). Revisadas también `PLAN_HELIOGUARD_SDD_V4.md`, `database/telemetry.example.sql`, `database/security-integrity.example.sql`, `database/tenant-integrity.example.sql`, y `contracts/telemetry-event.schema.json` del ZIP v4.

> Límite: no se ha tenido acceso al directorio de macOS `/Users/juanlopez/Proyectos/Solar/Idea Original` ni al backend Angular/JHipster actual. Por tanto, esta auditoría identifica huecos del diseño conocido; **no certifica cobertura de la idea original** ni migraciones compilables.

## 1. Dictamen

**Sí conviene evolucionar el dominio y los servicios backend**, pero no regenerar 47 CRUD automáticamente ni añadir decenas de entidades de forma acrítica. La v4 es buena como mapa transaccional de alto nivel. Los puntos más débiles son la topología física (localización), el tratamiento de series temporales, los controles de integridad reales, las reglas de simulación y las asignaciones de mantenimiento entre empresas.

## 2. Hallazgos priorizados

| ID | Prioridad | Hallazgo probado en v4 | Impacto | Acción propuesta |
|---|---|---|---|---|
| HG-B01 | P0 | No existe unión explícita entre `SolarString` y entrada MPPT concreta de un inversor, ni agrupación cartográfica de sectores. | No se puede justificar qué panel/sector falló a partir de una pérdida agregada. | Incorporar `SolarSector`, `InverterMpptInput` y `StringInputAssignment` (o topología equivalente versionada). |
| HG-B02 | P0 | `database/telemetry.example.sql` crea `telemetry_ingest_dedup`; `database/security-integrity.example.sql` crea `telemetry_event_dedup` con la misma intención. | Doble fuente de idempotencia e implementaciones divergentes. | Una sola tabla de dedup por `(gateway_id,event_id)` con una migración Liquibase, y tabla de secuencia aparte. |
| HG-B03 | P0 | Los contratos usan IDs externos de tipo `string`, mientras JHipster usa claves internas `Long`; `TelemetryChannel` no referencia el `Gateway` lector. | Asociaciones ambiguas de mediciones, riesgo multiempresa y datos huérfanos. | Definir resolver de identificadores externos→PK, asociación canal/lector vigente y claves compuestas tenant-safe. |
| HG-B04 | P0 | 47 relaciones JDL y `organization_id` repetido no representan RLS ni comprobación de propietario de objeto; existe acceso delegado. | Accesos cruzados y asignaciones incoherentes entre organizaciones. | Políticas, servicios de autorización, FKs compuestas, contexto transaccional y tests de tenant A/B + técnico externo. |
| HG-B05 | P0 | No existe un límite persistente explícito entre eventos de demo `SIMULATED` y operación real; las simulaciones están en Angular. | Contaminación de KPIs, incidentes, órdenes y potenciales despachos reales. | Ambiente/scope de simulación aislado, entradas reproducibles, bloqueo duro de efectos externos en modo simulación. |
| HG-B06 | P1 | `EnergyModelRun` tiene totales de ventana, no intervalos, rango de incertidumbre ni input de irradiancia documentado de forma suficiente. | Un déficit por nube podría confundirse con suciedad o falla. | Agregar versión de configuración/calibración, cobertura temporal, bandas de incertidumbre, referencia a datos meteorológicos; mantener series por intervalo fuera de JDL. |
| HG-B07 | P1 | `AlertRule` es un umbral con persistencia; sin comparador, severidad dinámica, ventana, supresión, cooldown ni versión de algoritmo. | Falsos positivos y alertas reiteradas. | Modelo de reglas versionadas + política de dedup/ack/supresión y motor de evaluación en servicio. |
| HG-B08 | P1 | `TelemetryIntegrityEvent` no almacena `bootId`; el esquema de evento sí lo define. No hay protocolo explícito de dato no confiable→incidente. | Replay confundido con retransmisiones legítimas; pérdida de trazabilidad. | Distinguir entrega duplicada de replay auténtico; firma/autenticación, secuencias `(device, bootId, seq)`, clock-skew, retención y cadena de evidencias. |
| HG-B09 | P1 | `SecurityIncident` referencia solo una `IntegrityAssessment` opcional; no agrupa evidencias múltiples ni cambios de analista. | Investigación incompleta de incidentes compuestos. | `SecurityCaseEvidence` o asociación n:m auditable; append-only `SecurityCaseEvent`. |
| HG-B10 | P1 | `DroneMission` no tiene targets geográficos versionados, ruta de misión ni eventos operativos; `MissionAuthorization` está pero sin gate formal de seguridad. | Imposible justificar y reproducir despacho/limpieza. | `DroneMissionTarget`, `DroneMissionEvent`, checklist y guardas de estado, operador autorizado, bloqueo por hielo/viento/daño eléctrico. |
| HG-B11 | P1 | `WorkOrder` asigna una sola `OrganizationMembership` sin histórico; puede ser miembro de otro tenant/proveedor. | No cubre reasignaciones, cuadrillas ni SLA multiempresa. | `WorkOrderAssignment` con prestador, miembro, vigencia, aceptación y autorización de instalación. |
| HG-B12 | P1 | `MaintenancePlanVersion` solo conserva monto y referencia a términos. No define servicios incluidos, cupos ni elegibilidad. | No hay forma fiable de aplicar el plan contratado a una orden. | `PlanBenefit`, `PlanEligibilityAssessment`, entitlements/snapshots del contrato. Separar claramente servicios de seguros. |
| HG-B13 | P2 | `AuditTemplate` referencia versión como string, pero no está explícitamente prohibido modificar una plantilla publicada. | Auditorías antiguas podrían cambiar semántica retroactivamente. | Publicación inmutable / snapshot de preguntas y método de scoring, firma y cadena de evidencias. |
| HG-B14 | P2 | `DataProvenance` identifica fuente y claves externas, pero no conserva de forma completa tipo de producto satelital, pixel/resolución/intervalo, incertidumbre y referencia estable al cálculo. | Riesgo de afirmar que un satélite produjo evidencia que no puede producir. | Metadatos de observación y assets por referencia + `weather_observation`/`satellite_product` (SQL o módulo dedicado según volumen). |

## 3. Cambios de dominio recomendados (sujetos a trazabilidad)

**Conservar y evolucionar**: `Organization`, `OrganizationMembership`, `Site`, `Installation`, `InstallationAccessGrant`, `Equipment`, `SolarArray`, `SolarString`, `Gateway`, `TelemetryChannel`, `AlertRule`, `Incident`, `EnergyModelRun`, `LossAssessment`, `LossCauseEstimate`, `TariffPlan`, `AuditReport`, `MaintenancePlanVersion`, `ServiceAgreement`, `PlanSubscription`, `WorkOrder`, `DroneMission`, `MissionAuthorization`, `DeviceIdentity`, `DataProvenance`, `SecurityIncident`.

**Candidatas P0 para JDL v5 (solo si el modelo actual no permite los invariantes):**

1. `SolarSector`: instalación, código, nombre, georreferencia/`geometryRef`, vigencia y geometría de referencia en PostGIS o GeoJSON externo según escala.
2. `InverterMpptInput`: inversor físico (`Equipment`), identificador de canal MPPT/entrada, estado, periodo de validez.
3. `StringInputAssignment`: `SolarString` ↔ `InverterMpptInput`, válido desde/hasta, restricciones que prohíban solapamientos no permitidos.

**Candidatas P1 para JDL v5:**

4. `AlertRuleVersion` o configuración JSON versionada: comparador, unidad, ventana, rearmado, condiciones mínimas y política de supresión. No duplicar `AlertRule`.
5. `SecurityCaseEvent` / `SecurityCaseEvidence`: evidencias y decisiones con retención y actor, integridad y trazabilidad.
6. `DroneMissionTarget`: vínculo sector/string/geometría y prioridad; `DroneMissionEvent`: transición, actor, timestamp y resultado.
7. `WorkOrderAssignment`: asignación/reasignación de técnico/cuadrilla, autorización interempresa, timestamps.
8. `PlanBenefit`: cupos y prestaciones de plan; `PlanEligibilityAssessment`: resultado de auditoría y bloqueos.

**Simulaciones:** para MVP hackatón conservar un motor en Angular con escenarios deterministas separados de datos productivos. Para backend de evaluación o demos repetibles, agregar módulo `simulation` con `SimulationScenario`, `SimulationRun` (IDs/semillas/versión/configuración), y almacenamiento inmutable de `SimulationEvent` en JSONB o tabla append-only; no emitir comandos a hardware desde este módulo. No poner miles de eventos de simulación como CRUD JPA si se espera mucho volumen.

**Datos de alto volumen fuera del JDL**: mediciones, observaciones meteorológicas, resultados energéticos por intervalo, outbox edge/cloud, dedup, secuencias, logs append-only y rutas/telemetría de drones de alta frecuencia. Gestionar mediante Liquibase/SQL con índices, particiones, retención y RLS.

## 4. Servicios de negocio y estados requeridos

### 4.1 Ingestión confiable (P0)

`DeviceAuth → TenantResolution → Signature/TLS Validation → Dedup `(gatewayId,eventId)` → SequenceCheck `(device,bootId,seq)` → Quality/Normalization → DurablePersist → Outbox → KPI/Rules`.

- Retorno idempotente para reenvíos legítimos con el mismo `eventId`; no sumar ni duplicar métricas.
- Secuencia repetida con payload distinto = anomalía investigable; una retransmisión idéntica no implica ataque confirmado.
- Dato sin firma/contador = grado de confianza limitado, no inventar controles no soportados por equipo.
- Interrupción WAN = cola durable de edge; reenvío ordenado cuando regresa, incluso con eventos atrasados.

### 4.2 Diagnóstico solar (P1)

`ObservationSet → Weather/QualityNormalization → ExpectedGeneration → MeasuredGeneration → GapAssessment → Hypothesis → Incident`.

- `expectedWh` por ventana meteorológicamente ajustada, incluyendo antigüedad y restricciones conocidas.
- Nubes, heladas, curtailment y datos inválidos NO se atribuyen automáticamente a suciedad.
- `sum(cause.estimatedWh) <= loss.totalLossWh`, preservar `UNKNOWN` y bandas de incertidumbre.
- Localizar en nivel de instalación/sector/inversor/string **solo hasta donde soporte la telemetría**; exactitud de panel requiere instrumentación o inspección adicional.
- No convertir W instantáneos a Wh sin integrar por intervalo o usar delta de medidor validado.

### 4.3 Alertas, mantenimiento y drones (P1)

`Incident OPEN → ACKNOWLEDGED → WorkOrder CREATED → ASSIGNED/ACCEPTED → Intervention IN_PROGRESS → Verification → DONE/VERIFIED → CLOSED`.

Para dron: `DRAFT → SAFETY_REVIEW → AWAITING_AUTHORIZATION → AUTHORIZED → DISPATCHED → IN_FLIGHT → COMPLETED/ABORTED`.

- Bloquear `DISPATCHED` si no hay autorización válida, dron disponible, condiciones operativas compatibles y misión aprobada por quien corresponde.
- Si la misión es de limpieza, comprobar compatibilidad del procedimiento con fabricante y condiciones ambientales; no limpiar con hielo ni enviar a zona con fallas eléctricas inseguras.
- Confirmar recuperación mediante ventana comparable con línea base y telemetría suficiente: `INCONCLUSIVE` cuando falte evidencia.
- Intervención de propietario solo para condiciones seguras de acceso, nunca proponer subir a un techo como parte automática de la aplicación.

### 4.4 Auditorías y planes (P1)

- Auditoría congelada al publicar: versiones, respuestas y hallazgos no cambian retroactivamente.
- Plan vendido como servicios explícitos medibles: visitas, SLA, desplazamientos, limpieza, exclusiones y costos. Elegibilidad basada en auditoría y excepciones.
- Garantías de indemnización y seguros quedan fuera del producto hasta contar con aseguradora autorizada y contrato separado.

### 4.5 Multiempresa y prestadores (P0)

- `organization_id` derivado de identidad / membresía activa, nunca confiado a un header arbitrario.
- Tenant propietario de la instalación ≠ tenant que presta mantenimiento; verificar `InstallationAccessGrant` y `ServiceAgreement` activos para cada acción.
- Políticas RLS con transacción local y rol runtime sin bypass; `FORCE ROW LEVEL SECURITY` donde aplique, FKs compuestas y pruebas de reutilización del pool.
- Nunca aceptar IDs de instalación, sector o evidencia de otro tenant ni en operaciones bulk ni por URL adivinada.

## 5. Cambios de contratos y persistencia

- **Contratos**: definir de forma única ID interno vs `installationCode`/`gatewayHardwareId`/`deviceUid`; evitar mezclar long JPA con string API. Mantener `schemaVersion`, `eventId`, `bootId`, `sequenceNumber`, UTC y calidad.
- **SQL**: ELIMINAR duplicidad lógica de `telemetry_ingest_dedup` / `telemetry_event_dedup` en el diseño futuro; una sola tabla de recepción idempotente; otra tabla para secuencias por boot. No borrar tablas existentes sin migración y backfill.
- **JDL**: revisar claves únicas con contexto tenant (`organization_id, code`) y no imponer unicidad global donde el negocio permite repetición; los `unique` globales para `hardwareId` y `deviceUid` pueden ser intencionales si representan identidad física global.
- **Migraciones**: estrategia expand/contract, sin regenerar todas las tablas ni borrar datos; Liquibase incrementales, índices compuestos y policies explícitas.
- **Estados simulados**: `SIMULATED` siempre visible y bloqueo a envío real de órdenes, drones, servicios o pagos.

## 6. DoD y pruebas imprescindibles

| Prueba | Resultado |
|---|---|
| Tenant A lee/edita recurso B | 403/404 y ninguna mutación |
| Técnico B accede con grant vigente a planta A | Solo recursos y acciones autorizadas |
| Técnico B accede a planta A tras revocación | Rechazo aunque conserve JWT |
| Gateway A envía dato de instalación B | Rechazo y registro de seguridad |
| Mismo `(gateway,eventId)` llega dos veces | Un único registro efectivo |
| Misma secuencia/boot pero payload distinto | Marcado como conflicto de integridad |
| Evento atrasado por desconexión WAN | Se acepta según política de lateness, sin duplicar ni alterar cronología física |
| Nube reduce generación según modelo | No se recomienda limpiar por esa caída normal |
| Sector no instrumentado | No se afirma localizar falla por panel |
| Misión de dron sin autorización/meteorología apta | Dispatch bloqueado |
| Demostración simulada | Ninguna llamada real a hardware, SMS, pagos o drones |
| Cambio de versión de plan/auditoría | Documentos históricos intactos |
| Intervención sin telemetría suficiente | Verificación INCONCLUSIVE |
| Build JDL + Liquibase PostgreSQL | Compilación y migración desde esquema vacío y copia de versión anterior |

## 7. Orden propuesto en sprints

- **S0.5**: comparación exhaustiva `Idea Original` vs v4 vs backend existente; fijar invariantes; modelo JDL v5 y migraciones en rama sin alterar v4.
- **S1**: identidad, tenant/RLS, delegaciones y relaciones tenant-safe.
- **S2**: topología del parque, gateways, contratos de ingesta, MQTT/Modbus en modo lectura.
- **S3**: dedup, outbox, secuencias e integridad básica; offline y reenvío.
- **S4**: meteorología, pvlib, modelo esperado por intervalos y pérdidas.
- **S5**: reglas versionadas, anomalías físicas, incidentes y calidad de evidencia.
- **S6**: escenarios reproducibles; eventos simulados aislados del backend operativo; ciberseguridad y pruebas replay/inyección.
- **S7**: auditoría técnica versionada y planes con condiciones explícitas.
- **S8**: órdenes, asignaciones, misiones de dron con autorizaciones y verificación de recuperación.
- **S9**: QA E2E, performance/retención, seguridad y recuperabilidad.
- **S10**: piloto con datos reales y contrastes de precisión/costos.

## 8. Decisión

**No reemplazar v4 por un JDL inventado inmediatamente.** Primero inspeccionar los archivos de `Idea Original` y el backend de trabajo, resolver los invariantes listados, después escribir v5 con cambios mínimos y migraciones verificadas. Especialmente: las simulaciones de hackatón no necesitan reproducirse en JPA desde el primer día si el backend operacional no existe aún.
