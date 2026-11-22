# QA y evidencias — HelioGuard v4

## 1. Matriz de pruebas por sistema

| Área | Tests obligatorios | Evidencia |
|---|---|---|
| JDL/ER | parser JHipster, referencias, migraciones, cardinalidades, índices/RLS | log CLI + SQL inspeccionado |
| Multiempresa | 2 tenants + proveedor delegado, IDOR, archivos privados, revocación y pooling RLS | integration tests + casos 403/404 |
| Edge | corte WAN, cola persistente, reloj, reinicio, device offline, MQTT ACL, TLS | eventos antes/después + contadores |
| Integridad | duplicados, replay bootId/seq, reinicios legítimos, secuencia desordenada, falsedad física | test determinista y eventos clasificados |
| Modelo energético | kW vs kWh, intervalos, calibración, nubes, sombras, hielo, tarifa variable | fixtures versionados + informe diferencias |
| Misión dron | bloquear viento/helada/permiso faltante; no disparar hardware | diagrama de estados + tests negativos |
| Auditoría | pregunta/plantilla versionada, evidencia, permisos, informe persistente | test de flujo completo |
| Planes | propuesta/contrato histórico, costos/SLA y límites | escenarios de activación/cancelación |
| Web UX | navegación, filtros, formulario, teclado, contraste AA, sin botones inertes | E2E, capturas 375/768/1280/1440 |
| Mobile | offline, carga evidencia, permisos, reconexión | test Flutter + registro en dispositivos |
| Infra | backup/restore, secretos, ARM64, redes, TLS, logs, ingestión | compose config + restauración verificada |

## 2. Guion E2E de hackatón

1. Iniciar demo sin backend. Seleccionar operador y planta demo.
2. Leer producción sana, métricas, timestamps y procedencia marcada `SIMULADO`.
3. Inyectar polvo en B2: déficit e incidente, calcular kWh e importe sin atribución conclusiva.
4. Proponer misión de inspección/limpieza. Intentar despachar con viento fuerte ⇒ bloqueo; quitar condición, aprobar en mock y animar.
5. Verificar producción normalizada antes/después; cerrar orden y conservar historial.
6. Repetir telemetría con mismo evento/bootId/sequence ⇒ rechazo por replay/duplicado; crear evento de integridad.
7. Inyectar potencia físicamente inconsistente ⇒ cuarentena para investigación, sin claim de ataque confirmado.
8. Cortar WAN, continuar edge/Flutter simulados; reconectar y no duplicar registros.
9. Cambiar a usuario tenant B: no ve datos de A. Técnico con grant puntual sólo ve instalación autorizada.
10. Completar checklist de auditoría y proponer plan de mantenimiento.

## 3. Criterios visuales

- Contraste normal 4,5:1, grande 3:1 o lo que corresponda a WCAG 2.2 AA.
- Navegación con teclado, focus visible, semántica y labels; estados vacío/error/carga/sin conexión.
- Sin overflow horizontal 375px; responsive 375/768/1280/1440 y soporte tema claro u oscuro si se entrega.
- La vista cenital debe tener una alternativa tabular accesible que relacione cada bloque con su estado.
- Evitar visualizaciones que usan solo color; incluir etiquetas/íconos y títulos.
- Motion discretas con `prefers-reduced-motion` y controles de pausa.

## 4. Evidencia de ejecución

Crear `reports/QA_RESULTADOS.md` con cada comando ejecutado, timestamp, entorno, resultados, errores corregidos, pruebas no ejecutadas y capturas si están disponibles. Nunca declarar que una aplicación fue compilada si sólo existe prompt/plan.

**Estado de entrega v4:** paquete de documentos, JDL y contratos estáticos; no hay compilación Angular/JHipster/Flutter ni validación de hardware. Validar sintaxis de JSON/YAML y referencias por script local de empaquetado.
