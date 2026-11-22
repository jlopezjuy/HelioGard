# HelioGuard web hackatón — Frontend-first F0–F7 (Angular + Tailwind, sin backend)

**Resultado requerido:** aplicación Angular completamente operable sin servidor; persistencia mock por tenant, una demostración de 2 minutos y 47 entidades mapeadas a casos UX, no 47 CRUD. El prompt ejecutable está en `prompts/PROMPT_MAESTRO_IA_FRONTEND_ANGULAR_MOCK.md`.

| Sprint | Alcance | Comportamiento que DEBE funcionar | Verificación |
|---|---|---|---|
| F0 | Inventario, 47 entidades, design tokens, referentes y navegación | Revisar existente, elegir arquitectura, persistencia, wireframes; no clonar UI genérica | Matriz completa, tokens, plan de rutas |
| F1 | Shell, actores, multiempresa | Persona DEMO, tenant activo, grants, ruta protegida de demo, switch y reset | 2 empresas + 1 prestador, ningún dato mezclado |
| F2 | Sitios, parques, inversores, Raspberry, ESP32 y telemetría | Vista cenital interactiva, bloques, telemetría simulada, reloj de demo | Selección bloque→detalle coherente; animación fluida |
| F3 | Modelo físico, clima y pérdidas | Escenarios polvo, inversor, nube, helada, granizo, humo; producción esperada/real y kWh | Tests de fórmulas; no atribuir a suciedad sin evidencia |
| F4 | Integridad/ciberseguridad | Replay con eventId/bootId/seq duplicado → retener; inyección inconsistente → investigar; fuentes en detalle | Tests de reglas y estados; no declarar ataques sin prueba |
| F5 | Dron y mantenimiento | Incidente→OT→misión propuesta→safety review→aprobación mock→despacho animado→verificación | Bloqueos viento/helada/techo, autorizaciones no omitibles |
| F6 | Auditoría, planes, economía | Checklist y evidencias offline demo, propuesta/plan versionado, costos ARS, tarifas | Crear/editar/persistir; ningún botón inerte |
| F7 | QA, responsive, pitch | E2E, accesibilidad AA, contraste, performance, script jurado 2 min | `ng build`/test reales y capturas 375/768/1280/1440 |

## Rutas mínimas funcionales

`/inicio`, `/parque`, `/instalaciones`, `/instalaciones/:id`, `/telemetria`, `/energia`, `/integridad`, `/incidentes`, `/misiones`, `/auditorias`, `/mantenimiento`, `/economia`, `/planes`, `/organizaciones`, `/configuracion`, `/fuentes`.

Navegación no debe mostrar entidades técnicas aisladas sin sentido. Subentidades se editan en detalles o paneles laterales con relaciones reales y persistencia local. Enrutado por lazy-loading y accesibilidad por teclado. Mocks deterministas almacenados en IndexedDB preferido, con versionado y reseteo reproducible; una API adapter futura sustituye solo el repositorio.

## Historia principal de 2 minutos

- Demo inicia en parque Puna Norte con sector y señales de telemetría **simuladas**.
- Activar suciedad: generación baja, ROI/impacto ARS, incidente y localización por telemetría del inversor/string.
- Mostrar fuente de irradiancia **meteorológica simulada** y transparencia de procedencia. NO mapa térmico satelital.
- Proponer misión dron, inspección (simulada), aprobar en demo después de safety checklist, animar vuelo/limpieza y validar recuperación.
- Ejecutar replay con `bootId+sequenceNumber` repetidos: dedup y evento de integridad. Inyección física improbable: abrir investigación, NO decir "ataque confirmado".
- Mostrar resiliencia sin internet (edge buffer visual), auditoría y plan de mantenimiento.

## Done del agente frontend

- 47 entidades JDL tienen tipo TS, relación y destino funcional, `frontend-demo/entity-to-ui.csv` completo.
- Cero peticiones REST reales y ninguna necesidad de levantar JHipster, PostgreSQL o Docker para demo Angular.
- Filtros, formularios y botones hacen acciones reales en el mock, guardan/recuperan o explican lectura solamente.
- Simulación de eventos no predice precisión, ganancias ni tasas de ataques sin medición.
- No hay textos de muestra tipo Lorem Ipsum, gradientes decorativos genéricos ni iconografía emoji en interfaz final.
- Capturas visuales y pruebas ejecutadas y registradas, no asumidas.
