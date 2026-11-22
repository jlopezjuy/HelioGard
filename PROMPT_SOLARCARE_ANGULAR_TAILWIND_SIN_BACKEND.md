# PROMPT MAESTRO — SOLARCARE WEB HACKATÓN (Angular + Tailwind, FRONTEND REAL SIN BACKEND)

Actuá como un equipo senior de **Product Designer, UX Researcher, Director de Arte, Angular Tech Lead, Frontend Engineer y QA Engineer**. El resultado que necesito **no es un documento ni otra maqueta estática**: es una aplicación **Angular funcional, navegable, visualmente profesional y demostrable sin backend**.

## 0. Fuente de verdad y límites

Trabajá en el repositorio donde esté este paquete. **Primero inspeccioná el árbol real del proyecto y el código existente**; no inventes rutas ni sobreescribas aplicaciones actuales. Si existe frontend Angular, evaluá y reutilizá lo compatible. Si no existe, creá `apps/solarcare-web` con Angular CLI oficial.

Leé en este orden:
1. `specs/domain.v2.draft.jdl` — contrato de 37 entidades y 83 relaciones. **Es borrador**: NO lo declares validado ni asumas que el backend está implementado.
2. `docs/MODELO_ER_MULTITENANT_V2.md` — identidad, relaciones, aislamiento tenant y restricciones.
3. `docs/JDL_FRONTEND_ENTITY_MATRIX_V3.md` — matriz exhaustiva del JDL a rutas/UX; ninguna entidad omitida.
4. `docs/FRONTEND_FIRST_SPRINTS_V3.md` — hoja de ruta de funcionalidad.
5. `docs/DESIGN_SYSTEM_QUEBRADA_V3.md` — estilo visual profesional.
6. `frontend-demo/demo-fixtures.example.json` y `frontend-demo/navigation.example.json`.
7. `contracts/openapi.yaml` y `docs/PLAN_MAESTRO_SDD.md` para futura integración, sin intentar conectar ahora.
8. `skills/solarcare-visual-design/SKILL.md` si tu entorno admite skills; en caso contrario, aplicá sus reglas como guía de trabajo.

**Prohibido:** instalar un backend nuevo para mostrar el demo; inventar que existen endpoints, simular pagos/seguros reales, montar dependencias cloud, falsear resultados de tests o construir 37 CRUD sin sentido. No reescribas el dashboard actual sin conservar su funcionalidad o dejar una alternativa reversible.

## 1. Resultado esperado: una aplicación de verdad, no una landing

Construí un centro de operaciones para empresas instaladoras, mantenedoras y propietarios. Debe permitir usar datos DEMO con **roles y tenants simulados**, navegar instalaciones, ver telemetría, provocar fallas, gestionar alertas, auditar instalaciones y recorrer el ciclo de mantenimiento.

Diseñá experiencias completas, incluidas rutas y detalles:
- **Inicio de sesión DEMO / selector de persona:** propietario hogar, operador de empresa, técnico, auditor y administrador de plataforma (solo ámbito de demo). Ingreso local sin credenciales reales.
- **Multiempresa:** cambio de organización activa según membresías; datos y navegación filtrados. `InstallationAccessGrant` permite a técnico externo ver únicamente una instalación delegada. Esto no es seguridad real: documentalo.
- **Inicio / centro de operaciones:** no empezarlo con 4 tarjetas idénticas. La protagonista debe ser la instalación y sus incidentes con jerarquía de urgencia, pérdidas y acciones.
- **Mapa cenital / parque solar:** SVG interactivo y responsivo con sectores, strings, inversores, gate­way y estados; escenarios: polvo, falla inversor, nube temporal, escarcha, granizo, humo/ceniza, mantenimiento y recuperación. No afirmar que son imágenes satelitales reales.
- **Instalaciones y equipamiento:** listado, filtros, detalle de sitio, equipos, modelos, arrays/strings, canales métricos, Raspberry, ESP32 y estados.
- **Telemetría y edge:** potencia W/kW, energía Wh/kWh, calidad del dato, reloj, simulación de pérdida WAN, buffer local y sincronización visual; NO simular conexión a dispositivos reales.
- **Modelo energético y anomalías:** generación esperada vs. real en períodos equivalentes, hipótesis de suciedad, temperatura, sombra o hardware con evidencia y estado de validación; no etiquetar por certeza lo que es estimación.
- **Economía energética:** diferencia de kWh, valor de autoconsumo vs exportación, precios ARS editables, costo de limpieza y valor potencial recuperado; gráficos con ejes y períodos correctos.
- **Alertas e incidentes:** crear escenario, detectar anormalidad, clasificar, reconocer, asignar y cerrar solo si pasó por validación coherente; persistir historia y evitar alertas duplicadas.
- **Auditoría:** checklist por grupos, fotos locales, hallazgos, severidad y recomendaciones, guardar borrador, reabrir y emitir informe demo claramente identificado; usar `AuditTemplate` y preguntas versionadas.
- **Planes y propuestas:** vista comparativa Esencial/Protección/Empresarial con alcances, límites, SLA y costos simulados; versionar planes y condiciones aceptadas. Los seguros y coberturas de daños son un producto externo, nunca presentarlos como póliza emitida.
- **Mantenimiento end-to-end:** de alerta a orden de trabajo; elegir dron (solo si es seguro), técnico o dueño (solo paneles accesibles desde suelo y prácticas permitidas); visualización de despacho/limpieza/reparación; evidencia y verificación posintervención; no automatizar acciones reales.
- **Informes:** descargar JSON y CSV de los datos del demo; PDF solo si realmente implementado y generado.
- **Configuración:** tarifas, perfil climático, simulador, idioma español AR, valores de la demo y restablecimiento.

No todas las 37 entidades necesitan ruta. **Todas sí requieren tratamiento: tipo/interfaz, relación y uso en un flujo de negocio o pantalla de configuración.** Usá la matriz de cobertura.

## 2. Arquitectura frontend y datos sin backend

Usá la versión Angular estable compatible con Node instalado. **Primero ejecutar `node -v`, `ng version` (si existe), `npm -v`** y elegir comandos según el resultado. NO fijes ni adivines un número de versión no instalado.

- Angular CLI, standalone, strict TypeScript, Angular Router, lazy features, Signals/computed y patrones de estado apropiados para la versión real.
- Tailwind CSS mediante configuración oficial de Angular (`ng add tailwindcss` si soportado; si falla, instalación manual oficial); no mezclar configuraciones Tailwind 3 y 4.
- Iconos: Lucide o Tabler coherentes en toda la app; paquetes y licencias verificados.
- Datos: `interfaces`, `enums`, relaciones y repositorios mock derivados del JDL; usar InjectionTokens/puertos para permitir cambiar a HTTP sin tocar componentes.
- `DemoStore` / repositorios locales con fixtures deterministas y persistencia `IndexedDB` o `localStorage` versionada (elegí una y justifica). `resetDemo()` restaura datos iniciales. Validar importación/exportación.
- Separá telemetría de series temporales del JDL transaccional. No crear un enorme CRUD para mediciones.
- Incluir errores, estados vacíos, pausas, carga, permisos denegados, offline/online simulados y ausencia de sensores.
- Evitá llamadas `HttpClient` en el modo DEMO; **debe funcionar al abrir `ng serve` sin backend, sin Firebase y sin conexión de red** una vez instaladas las dependencias.
- Semillas razonables para empresas de Jujuy/Salta; nombres ficticios, valores tarifarios etiquetados como supuestos y tiempos consistentes.
- `FR-ID` para features, pruebas y hallazgos. Especificá documentación de decisiones en ADR si cambias arquitectura.

## 3. Dirección artística — «Estratos de la Quebrada» (NO AI slop)

El lenguaje visual será una reinterpretación industrial contemporánea de la Quebrada de Humahuaca: cerros estratificados, adobe, minerales, vegetación de altura y luz andina. **No** usar folclore como decoración, estampados excesivos ni una plantilla SaaS genérica. Usar la foto y el paisaje como referencia de paleta, no incrustar fotos con copyright sin licencia.

Tokens recomendados (se validan en `docs/DESIGN_SYSTEM_QUEBRADA_V3.md`):
- Fondo cálido `#F7F4EE`, superficies `#FFFFFF`, texto `#24302D`, bordes `#DDD5C8`.
- Tierra rojiza/terracota principal `#8F432E`; arcilla `#984B33`; vino mineral `#6B443D`.
- Verde cardón `#315D57`; pizarra azulada `#365D65`; arena secundaria `#D2BD98`.
- Estados de alerta con color semántico propio, texto y pictograma; no confundir marca y severidad.

Usá IBM Plex Sans (si se puede obtener con licencia y alojar apropiadamente) para UI e IBM Plex Mono para lecturas; si no, fuentes de sistema bien jerarquizadas. Evitá fuente genérica homogénea a 3 tamaños, sombras enormes, gradientes violetas, cajas redondeadas anidadas, iconos emoji, banners irrelevantes y enormes superficies vacías.

El diseño se juzga por legibilidad, decisiones y funcionamiento: pocas acciones principales visibles, información agrupada por tareas, microcopy humano, distintas jerarquías para mapa/listados/auditoría, densidad útil, scroll controlado y adaptación móvil auténtica.

No diseñes tres columnas idénticas de widgets. Para el centro de operaciones, usá una composición asimétrica que priorice el **parque cenital y el panel de incidentes**, con una barra de contexto de organización y un resumen de energía secundario. Otras páginas deben tener diseños funcionales propios.

Aplicar los aprendizajes de comunidades y referencias (ver `docs/DESIGN_SYSTEM_QUEBRADA_V3.md`): consistencia espacial, jerarquía tipográfica, evitar el "card soup", estados vacíos y feedback, contraste y acciones visibles. **No copies diseños comerciales exactos**.

## 4. Skills y herramientas de diseño

1. Buscá skills de Angular, frontend design y visual QA **ya instaladas** en tu entorno. Si existen, leelas y usalas.
2. Usá además `skills/solarcare-visual-design/SKILL.md`, una skill local incluida en este paquete (sin instalación externa).
3. Si tu agente permite instalar skills externas, instalá **solo si aportan una capacidad real** (por ejemplo, auditoría visual y capturas o mejores prácticas Angular), verificando su autor, licencia, contenido y scripts. Priorizar repositorios oficiales. No ejecutar instaladores desconocidos, ni instalar una skill por moda.
4. Si no tenés soporte de skills, aplicá el mismo checklist manualmente. No detengas la construcción por eso.
5. Podés investigar recomendaciones de profesionales y comunidades de UI/UX con el navegador disponible; contrastarlas con W3C y Angular oficial y registrar fuentes de manera breve. No consumas tiempo infinito buscando inspiración.

## 5. CLI y estructura de proyecto obligatorios

Si no existe app Angular y la versión Angular/Node instalada es compatible, los comandos orientativos son:

```bash
node --version
npm --version
ng version || true
# En la raíz del repositorio; si ya existe apps/solarcare-web, inspeccionarlo antes.
npx @angular/cli@latest new solarcare-web --directory=apps/solarcare-web --routing --style=css --standalone --strict --skip-git
cd apps/solarcare-web
npx ng add tailwindcss
# Si `ng add` no está soportado, seguir https://angular.dev/guide/tailwind y documentar el cambio
npx ng generate component features/operations/pages/operations-overview --standalone
npx ng generate component features/installations/pages/installation-list --standalone
npx ng generate service core/demo/demo-store
npm run build
npm test -- --watch=false
```

Ajustá rutas/comandos al workspace realmente detectado y a la versión. El comando de creación es guía, no permiso para sobreescribir.

Estructura sugerida:

```text
apps/solarcare-web/src/app/
  core/{auth-demo,tenant,models,ports,adapters,demo,guards}
  shared/{ui,charts,icons,forms,utils}
  features/{operations,installations,telemetry,incidents,performance,economics,audits,plans,maintenance,organizations,reports,settings}
  app.routes.ts
```

Centralizá tokens en `styles.css` usando `frontend-demo/design-tokens.tailwind-v4.css` como referencia; no dupliques ni inyectes estilos de forma inconsistente. Los nombres de archivos dependen del estilo del CLI.

## 6. Calidad, SDD y ejecución autónoma

Trabajá en mini-sprints F0–F7 del documento de frontend. Para **cada** funcionalidad: SPEC → DESIGN → TASKS → IMPLEMENT → TEST → REVIEW. Usá `docs/JDL_FRONTEND_ENTITY_MATRIX_V3.md` para marcar cobertura real.

Pruebas obligatorias:
- `ng build` sin errores y tests unitarios/smoke E2E ejecutados; no digas que pasan si no se ejecutaron.
- 375×812, 768×1024, 1280×800 y 1440×900 sin desbordes, scrolls fantasma ni botones ocultos.
- Estado DEMO claramente visible. No hay APIs, satélites, drones ni equipos reales conectados.
- Abrir un escenario de falla de inversor y generar incidente; las notificaciones y orden se ven y persisten en mock.
- Probar polvo: pérdida estimada, técnico/dron según seguridad, recuperación simulada.
- Probar nube: caída temporal sin despachar técnico ni atribuir suciedad.
- Probar helada: bloqueo de limpieza con agua y restricción de carga de batería por frío.
- Probar multiempresa: cambiar organización no muestra instalaciones ajenas; acceso delegado sólo instalación autorizada (admitir que es simulación frontend).
- Probar reset e import/export, cierre de navegador y persistencia.
- WCAG AA: contraste normal ≥4.5:1, teclado, foco visible, semántica, aria, motion reduction.
- Logs de consola sin errores relevantes y sin recursos de internet requeridos en runtime.

Capturá evidencia visual real mediante navegador/Playwright si están disponibles; si no, documentá claramente que no pudieron hacerse capturas y no inventes QA. Revisá el resultado de forma crítica; repará al menos los problemas de jerarquía, densidad y responsividad detectados antes de cerrar.

## 7. Guion de demo que debe quedar disponible en `/demo`

Demo de 5 minutos, reproducible con un clic:
1. Elegir planta simulada en Abra Pampa o parque Puna Norte.
2. Mostrar producción sana y datos por gateway/inversor.
3. Activar escenario de polvo: déficit energético y estimación económica.
4. Generar alerta; inspeccionar evidencia y elegir intervención (dron simulado si condiciones seguras).
5. Mostrar despacho cenital, limpieza, validación antes/después y cierre del incidente.
6. Cortar internet: edge continúa registrando; al volver, sincroniza sin duplicar.
7. Cambiar a hogar: helada, protección de batería y bloqueo de limpieza insegura.
8. Mostrar auditoría de instalación y plan de mantenimiento propuesto.

## 8. Entregables finales del agente

1. Código Angular ejecutable, dentro del proyecto real, sin backend.
2. README con CLI exacto de instalación/arranque/pruebas, versiones realmente utilizadas y ruta local de demo.
3. Matriz de cobertura de 37 entidades: `modelo TS`, `ruta/feature`, `fixture`, `acción funcional` y estado.
4. Capturas desktop/tablet/móvil, si se ejecutó navegador visual.
5. Tests reales y resumen de resultados (pasó/falló/no ejecutado).
6. `docs/FRONTEND_DEMO_IMPLEMENTATION_REPORT.md`: decisiones, pantallas, riesgos, desvíos y pendientes backend.
7. Guion DEMO con controles accesibles y botón "Restablecer demo".

Trabajá autónomamente y de forma incremental. No hagas preguntas si encontrás alguna ambigüedad menor: tomá una decisión técnica razonable, dejala documentada y continuá. Para requisitos inseguros, hardware real o manejo de credenciales, no simules permisos o capacidades reales.

**Criterio de éxito:** un jurado sin conocimientos técnicos debe comprender, en menos de dos minutos, qué está fallando, cuánto cuesta y cómo SolarCare coordina una solución. El equipo técnico debe comprobar que la implementación Angular y la futura integración JHipster están correctamente separadas.
