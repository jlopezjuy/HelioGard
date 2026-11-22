# ADR — Decisiones de arquitectura

## ADR-001: Modular monolith central (aceptada provisionalmente)
**Contexto:** un proyecto de hackatón necesita velocidad sin sacrificar modelo multiempresa.  
**Decisión:** JHipster backend-only con paquetes de dominio y PostgreSQL. No partir tempranamente en microservicios.  
**Consecuencias:** una imagen backend más simple de desplegar; particionar responsabilidades mediante módulos y contratos internos. Revisar extracción de servicios cuando métricas reales lo justifiquen.

## ADR-002: Edge local independiente (aceptada provisionalmente)
**Contexto:** instalaciones aisladas de Jujuy/Salta con conectividad intermitente.  
**Decisión:** Raspberry Pi/mini-PC con gateway ligero, MQTT, SQLite, API LAN y outbox HTTPS hacia VPS.  
**Consecuencias:** mayor responsabilidad operativa en edge y seguridad de emparejamiento; continuidad sin WAN.

## ADR-003: ESP32 no obligatorio (aceptada provisionalmente)
**Contexto:** muchos inversores tienen telemetría nativa.  
**Decisión:** leer inversor directamente cuando se pueda; ESP32 solo para sensor/RS485 adicional.  
**Consecuencias:** menor costo y menor superficie de fallas.

## ADR-004: Diagnóstico físico antes que LLM (aceptada provisionalmente)
**Decisión:** pvlib para generación, reglas para incidentes, RdTools con datos suficientes para suciedad. IA generativa opcional solo para explicación, nunca diagnóstico eléctrico concluyente ni accionamiento.

## ADR-005: Eventos versionados e idempotentes (aceptada provisionalmente)
**Decisión:** JSON Schema con eventId, timestamp UTC, unidades y calidad. Cloud exige `gatewayId+eventId` único, resuelve late arrivals.

## ADR-006: Mobile offline-first (aceptada provisionalmente)
**Decisión:** Flutter con Drift local, lectura LAN primero si está disponible y remoto cuando haya internet. Estado visual de frescura y sincronización.

## ADR-007: Planes de mantenimiento separados de seguros (aceptada provisionalmente)
**Decisión:** comercializar monitoreo, auditoría y visitas definidas; cobertura indemnizatoria solamente vía socio asegurador autorizado.

## ADR-008: Docker solo para procesos contenerizables (aceptada provisionalmente)
**Decisión:** Compose DEV/EDGE/CLOUD para servicios; Flutter se construye con FVM/Flutter CLI, ESP32 con ESP-IDF. Evaluar ARM64 de cada imagen antes de desplegar en Raspberry.

## ADR-009: Fuentes del dashboard existente primero (pendiente de inspección)
**Decisión propuesta:** construir adaptador, reutilizar o migrar según inventario técnico real. Evitar reescribir sin análisis de paridad funcional.

## ADR-010: Autenticación SaaS (pendiente)
**Decisión de MVP:** JWT JHipster si no hay proveedor existente. Si hay IdP/OIDC, priorizar PKCE y evitar doble autenticación. Emparejamiento local separado en todos los casos.

## ADR-011: Integridad es independiente del diagnóstico de pérdida (aceptada provisionalmente)
**Decisión:** diferenciar incidentes de energía (`Incident`) y de integridad (`SecurityIncident`), registrar pruebas/replay/reintentos, conservar procedencia y calidad; ningún modelo físico confirma intención del atacante por sí solo.

## ADR-012: Satélite no diagnostica termografía individual (aceptada)
**Decisión:** irradiancia meteo satelital para expectativa; telemetría para localizar por inversor/string; inspección de campo o dron IR para confirmar. La vista cenital es representación GIS/ilustrativa.

## ADR-013: Misiones de dron supervisadas (aceptada)
**Decisión:** software propone misión y bloquea despacho hasta cumplimiento de reglas/operador/autorización. En hackatón son animaciones, sin control físico. Dron y técnico son canales de ejecución, no sustitutos de protecciones.

## ADR-014: Rebranding consolidado (aceptada provisionalmente)
**Decisión:** marca de trabajo HelioGuard, mantener SolarCare como referencia histórica fuera del repositorio limpio. Validar marca INPI/dominios antes de comercializar.
