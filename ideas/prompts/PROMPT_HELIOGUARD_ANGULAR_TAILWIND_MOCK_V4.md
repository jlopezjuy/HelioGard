# PROMPT MAESTRO PARA CUALQUIER IA — HELIOGUARD FRONTEND HACKATÓN

**Objetivo de ejecución:** construí una **aplicación real Angular standalone + Tailwind CSS**, visualmente diferenciada, totalmente navegable y con estado persistente, **sin backend ni llamadas API externas**. No quiero otra propuesta, PDF, solo imágenes, pseudo-código ni una landing estática. La demo debe explicar en 2 minutos cómo HelioGuard detecta una pérdida en un parque solar, verifica la telemetría, localiza un sector sospechoso y coordina una misión autorizada de dron, mientras demuestra rechazo de lecturas repetidas.

## 0. Trabajo previo (obligatorio)

1. **Inspeccioná el repositorio existente** (`pwd`, `find`, `package.json`, `angular.json`, rutas, UI), no inventes rutas ni sobreescribas código. Si existe frontend angular reutilizable, analizá integración; si no existe, creá `apps/helioguard-web` con Angular CLI oficial. No rompas el dashboard previo.
2. Leé `AGENTS.md`, `README.md`, `specs/domain.v4.draft.jdl`, `docs/01_MODELO_ER_3FN_MULTITENANT.md`, `docs/05_FRONTEND_JDL_MATRIX.md`, `docs/03_SPRINTS_FRONTEND_DEMO_F0_F7.md`, `docs/04_DESIGN_SYSTEM_QUEBRADA.md`, `docs/06_SEGURIDAD_TELEMETRIA_ATAQUES.md`, `docs/07_SATELITES_DRONES_LIMITACIONES.md`, `frontend-demo/demo-fixtures.example.json`, `frontend-demo/navigation.example.json` y `contracts/*.schema.json`.
3. Reconocé que el JDL **es borrador**: 47 entidades, 114 relaciones declaradas y enums. No afirmar backend generado ni esquema validado. Generá interfaces TypeScript y relaciones según el JDL; en `docs/MODEL_COVERAGE.md` documentá cada entidad con tipo, fixture, pantalla/flujo y acción disponible.
4. Leé y usá `skills/helioguard-visual-design/SKILL.md`. Si el entorno permite skill profesional de diseño (angular UI, accessibility, UI QA), **buscá en fuentes oficiales/verificadas e instalá únicamente si es seguro, permitido y pertinente**. Documentá fuente/licencia/permisos. Nunca ejecutes scripts desconocidos de foros sin revisión. Si no podés instalarlo, aplicá la skill local.
5. Consultá, si tenés acceso web, debates y recomendaciones **reales** de profesionales y comunidades de diseño (`r/UI_Design`, `r/web_design`, NN/g, W3C y galerías técnicas industriales) para identificar al menos 5 antipatrones visuales y buenas prácticas. Guardá enlaces verificables en `docs/DESIGN_RESEARCH.md`. No inventes que navegaste ni atribuyas recomendaciones que no pudiste verificar.
6. Elegí una dirección artística antes de codificar: **“Estratos de la Quebrada”**, inspirada en la geología de Jujuy, no postal turística. Aplicá paleta, tipografía, tipografía monoespaciada para mediciones, rejilla, contraste y jerarquía del design system. Crea o adapta sprites/diagramas SVG propios; no usar iconos emoji, fuentes o imágenes sin derechos comprobados.

## 1. Arquitectura obligatoria Angular

- Angular CLI oficial, standalone components, lazy routes, forms tipados, Signals/computed (o state management equivalente justificado), TypeScript strict y Tailwind CSS con tokens en `frontend-demo/design-tokens.tailwind-v4.css` adaptados a configuración real.
- Separá **presentation / application / domain / infrastructure**: `features/*`, `core/data/ports`, `infra/mock-repositories`, `core/state/demo-clock`, `shared/ui`.
- Inyección de dependencias para repositorios tipados (`InjectionToken` / providers). Una futura implementación HTTP debe reemplazar mocks sin reescribir componentes ni lógica de negocio.
- **Cero HTTP en runtime** (incluido favicon/fonts/CDN), no pedir localhost:8080. Los datos son mock deterministas con semilla y versión. Persistencia local (IndexedDB ideal; localStorage para escala pequeña), export/import JSON validado y botón `Restablecer demo`.
- Separar estado por **tenant activo** y por usuario; frontend **solo simula** permisos, nunca afirmar seguridad real.
- Un único reloj/escenario `DemoClock` para animaciones, streaming simulado, timestamps y recuperación. No generar números aleatorios irreproducibles; respetar unidades W/kW/Wh/kWh y moneda ARS.

## 2. Rutas/experiencias funcionales, no 47 CRUD

Usá `frontend-demo/entity-to-ui.csv` como matriz base y completala para 47 entidades. Cada entidad debe existir como modelo TS y ser accesible desde un flujo, un detalle, una edición contextual o un expediente, sin inflar la navegación con 47 menús.

**Pantallas protagonistas**:

1. `/inicio`: centro de operaciones con instalación/urgencias; no empezar con cuatro tarjetas KPI idénticas.
2. `/parque`: vista cenital georreferenciada **simulada** de parque solar, selección de bloques/strings, leyenda accesible, estados por inversor, animaciones de incidencias y dron.
3. `/instalaciones` + `/:id`: empresas, sitios, arrays, strings, equipos, inversores, gateways, canales, catálogos y accesos externos.
4. `/telemetria`: flujo de datos desde inversor/ESP32 a Raspberry, outbox local/WAN/Cloud, frescura, deduplicación y logs contextualizados.
5. `/energia`: modelo limpio esperado vs real, intervalos comparables, causas `UNKNOWN` cuando falta evidencia; clima Puna/Quebrada/Valles/Yungas/Salta.
6. `/integridad` + `/fuentes`: explorer de procedencia (`EvidenceSource`, `DataProvenance`), `DeviceIdentity`, eventos integridad, valoración, incidentes de seguridad, inspección de evidencia.
7. `/incidentes`: bandeja unificada con distinción energética vs seguridad, dedup, asignación y acciones.
8. `/misiones`: flota, plan de inspección o limpieza, checklist meteorológico/fabricante/operador, autorización, despacho animado, verificación.
9. `/mantenimiento`: trabajo técnico, asignación, aceptación, tareas, fotos locales y comparación antes/después.
10. `/auditorias`: checklist multisección, plantillas versionadas, hallazgos/evidencias, guardar/reabrir/cerrar, propuesta de adecuación.
11. `/economia` + `/planes`: ARS/kWh, compra evitada vs inyección, tarifa vigentes, costo limpieza, propuesta/plan versionado, SLA y exclusiones.
12. `/organizaciones`: selector tenant, miembros/roles, prestador externo con `InstallationAccessGrant`, probar permiso denegado.

**Cada botón funcional**: navega, crea/edita/persiste un mock, cambia estado, exporta o explica `Solo lectura` con una razón concreta. Nada de `alert('próximamente')` en una acción del flujo central. Proveer estados vacío, loading simulado, error, falta de datos y sin conexión.

## 3. DEMO PRINCIPAL (60 segundos con controles grandes)

1. Parque funcionando con **datos explícitamente simulados**; energía estimada por recurso solar meteorológico externo, telemetría por inversor/string.
2. Activar `Sector sucio`: sector rojo/ámbar, potencia real menor a esperada, déficit kWh por intervalo y costo ARS estimado, diagnóstico preliminar.
3. Crear `Incident → WorkOrder → DroneMission(DRAFT/SAFETY_REVIEW)`. Validar clima, fabricante, operador y sitio. Con bloqueo no despachar; con autorización demo registrar `MissionAuthorization(APPROVED)`.
4. Animar dron en mapa cenital, completar intervención y crear `VerificationResult`; comparar antes/después bajo condiciones equivalentes. No prometer recuperación real automática.
5. Activar `Replay`: generar evento ORIGINAL con `deviceId,eventId,bootId,sequenceNumber`; reinyectar **exactamente la misma secuencia o evento**. Ejecutar lógica de dedup real en TypeScript del mock, retenerlo, abrir `TelemetryIntegrityEvent`, mostrar prueba (no solo pintar rojo). Prever retransmisión legítima y reinicio bootId nuevo.
6. Activar `Inyección falsa`: valor físicamente improbable según límites configurados → `PHYSICAL_INCONSISTENCY`, estado `UNDER_REVIEW`, no afirmar intrusión demostrada.
7. Opcional: cortar internet y continuar en edge simulated; restaurarlo y sincronizar una sola vez. Mostrar auditoría y propuesta de cobertura.

**Seguridad física:** simulador NO controla un dron; no simular agua sobre hielo, limpieza casera de techos, bypass de protecciones eléctricas. Dron agrícola no equivale a sistema certificado. Mostrar «Misión simulada — aprobación no representa permiso operativo real».

## 4. Reglas y cálculos a implementar como funciones puras testeables

- Energía por integración de potencia a lo largo del intervalo o por diferencias de contador; **nunca restar kW como kWh**.
- `deficitWh = max(0, expectedWh - actualWh)` cuando los períodos y la calidad sean comparables, de lo contrario `sin estimación confiable`.
- Monetización configurada por tarifa de autoconsumo/compra evitada e inyección; todos los ARS demostrativos.
- Causas de pérdida y confianza: no asignar 100% a suciedad sin medición. No sumar causas por encima del déficit.
- Integridad: ingestión idempotente por `gatewayId+eventId`; dedup adicional por `deviceId+bootId+sequenceNumber` cuando la fuente los soporte. Fuentes sin firma auténtica no se muestran como `CRIPTOGRÁFICAMENTE VERIFICADAS`.
- Criterio de anomalía física con límites por equipo e intervalo de incertidumbre; diferencia implica sospecha o fallo de sensor, no ataque confirmado.
- Estados de misión válidos y autorización previa; no permitir salto de DRAFT a IN_FLIGHT.
- Fecha local de instalación según `timeZone`, persistencia UTC.

## 5. Estilo visual profesional y anti-IA-slop

- Usá design tokens mineral/Quebrada: terracota `#8F432E`, verde cardón `#315D57`, pizarra `#365D65`, arena `#D2BD98`, base marfil `#F7F4EE`, tinta `#24302D` y semántica de fallas independiente.
- Layout de sala de control: mapa protagonista, panel de incidente contextual, detalle accesible. Tipografía editorial técnica con jerarquía controlada (IBM Plex si licencia y paquete local, si no fuentes de sistema).
- Iconografía Lucide/Tabler consistente; legible a 375/768/1280/1440 px y teclado. Motion funcional (datos, dron, cambio estado), no animaciones decorativas permanentes. `prefers-reduced-motion` obligatorio.
- El color nunca indica estado sin etiqueta/ícono, contrastes WCAG 2.2 AA para texto, texto/tabla real y unidades visibles.
- Prohibido: vidrio/gradientes púrpura genéricos, 4 KPIs vacías como home, tarjetas anidadas sin sentido, charts sin eje/intervalos, emoji como iconos, stock de paneles genérico, interfaces que parecen IA, botones inertes, menús inflados, placeholders mentirosos.
- Aplicar una auditoría visual CRÍTICA, no complaciente: medir densidad, consistencia de spacing, iconografía, alineación, contrastes, estados, jerarquía, velocidad, scroll y tab/focus. Corregir antes de finalizar.

## 6. SDD y ejecución, no pseudoimplementación

Seguí F0→F7 en `docs/03_SPRINTS_FRONTEND_DEMO_F0_F7.md`. Para cada sprint: especificar → modelar → implementar → testear → revisar UI → registrar evidencia. Usá Angular CLI (`npx @angular/cli@latest new ...` si no existe, `npx ng add tailwindcss`, `ng generate`) y los comandos realmente disponibles en el entorno.

No hagas preguntas por ambigüedades menores; tomá una decisión conservadora y documentala. No instales plugins de navegador ni skills sin verificar origen. No consumas tiempo regenerando por completo assets ya existentes si tienen valor y son reutilizables. No inventes comandos ejecutados.

## 7. Entregables / DoD inexcusables

1. **Código Angular real** con README de ejecución y sin API/backend, listo para demo offline.
2. 47 entidades con interfaces TS, relaciones y tratamiento UX, matriz `docs/MODEL_COVERAGE.md` actualizada.
3. Fixtures de tres tenants con roles y grants; persistencia, export/import, reset y demo runner.
4. Tests de negocio: kW/Wh, replay original/repetido/nuevo bootId, valor improbable, tenant demo, autorización de misión, bloqueos clima, pérdida económica.
5. `ng build`, unit tests y E2E de la ruta principal ejecutados de verdad; reporte con comandos, resultados y fallos.
6. Capturas verificadas de desktop/tablet/mobile; si no hay navegador, declarar limitación sin fingir QA visual.
7. `docs/FRONTEND_DEMO_IMPLEMENTATION_REPORT.md` con decisiones, UI auditada, pendientes y ausencia de backend real.
8. Guion de presentación 2 min con atajos y botón `Restablecer demo` que siempre deje el estado limpio.

**Éxito:** el jurado comprende «qué está fallando, cuánto cuesta, qué tan confiable es el dato y cómo lo resolvemos», y otro desarrollador puede cambiar MockRepository por HTTP sin rehacer la UI.
