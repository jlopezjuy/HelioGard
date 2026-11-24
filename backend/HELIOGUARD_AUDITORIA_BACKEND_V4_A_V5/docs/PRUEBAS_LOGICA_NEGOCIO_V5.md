# HelioGuard — Criterios de aceptación para servicios y reglas v5

Todos los tests a continuación son **por implementar**, no resultados de pruebas ejecutadas.

| ID | Given | When | Then | Sprint |
|---|---|---|---|---|
| T01 | Usuario con una organización activa | Consulta instalaciones | Solo propias y delegadas permitidas | S1 |
| T02 | Usuario con dos organizaciones, sin empresa seleccionada | Crea instalación | Rechazo explícito sin efecto lateral | S1 |
| T03 | JWT org A, membresía revocada | Pide instalación propia antigua | Denegación | S1 |
| T04 | Usuario A conoce `installationId` de B | GET, PATCH, DELETE o foto | Denegación uniforme | S1 |
| T05 | Prestadora B tiene grant sobre instalación A-1 | Accede A-2 | Denegación | S1 |
| T06 | Gateway autenticado A, evento declara B | Ingesta | Rechazo, registro de seguridad | S3 |
| T07 | 100 eventos en SQLite durante caída WAN | Reconecta, reintenta 3 veces | 100 únicos en cloud, cola confirmada | S3 |
| T08 | Evento duplicado mismo hash | Reintenta HTTP | Respuesta idempotente, sin falsa acusación | S3 |
| T09 | Mismo eventId con hash distinto | Publica | Cuarentena + incidente | S3 |
| T10 | Falta irradiancia suficiente | Ejecuta modelo | INSUFFICIENT_DATA, expectedWh null | S4 |
| T11 | Medianoche sin luz | Lee potencia cero | Ningún incidente de baja producción | S5 |
| T12 | Inversor inaccesible | Telemetría caduca | TELEMETRY_OFFLINE, no falla confirmada de panel | S5 |
| T13 | Lectura físicamente imposible | Evalúa integridad | SUSPECT, no ataque confirmado | S5 |
| T14 | Déficit de 4 kWh con atribuciones 3 kWh y 2 kWh | Guarda assessment | Rechazo o reclasificación; no doble conteo | S6 |
| T15 | No hay curva de autoconsumo | Calcula pérdida monetaria | Escenarios con supuestos, no valor exacto | S6 |
| T16 | Auditoría con preguntas obligatorias vacías | Solicita aprobar | Rechazo y faltantes | S7 |
| T17 | Auditoría con riesgo crítico | Evalúa elegibilidad | Requiere corrección o no elegible según reglas | S7 |
| T18 | Prestador sin grant vigente | Intenta aceptar OT | Rechazo | S8 |
| T19 | Misión dron sin autorización o viento incompatible | Solicita despacho | Rechazo + motivo | S8 |
| T20 | Limpieza terminada sin mediciones posteriores suficientes | Verifica recuperación | INCONCLUSIVE | S8 |

Pruebas recomendadas: unit tests por regla; tests MVC de autorización con principals y tokens; integración con PostgreSQL real para FKs/RLS; contract tests JSON de gateway; tests de migraciones Liquibase; pruebas de concurrencia (duplicados, OT, misiones); simulación de conexión offline con tiempos controlados.
