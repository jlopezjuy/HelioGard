# Plan de migración de pantallas

Cinco sprints sobre `frontend/helioguard-web`. El sprint 0 ya está en el repo: tokens `hg-*`, biblioteca `shared/ui` y `/sistema`. Las rutas y el mock no se movieron.

Ninguna maqueta Stitch cubre el guion de la hackatón (suciedad, misión, replay, inyección). La migración cambia chrome y componentes. No cambia `DemoStore`, los textos de límite ni la vista cenital interactiva por una imagen.

Prioridad P0: lo que el jurado ve en dos minutos. P1: el resto operativo. P2: cuenta.

## Sprint 0 — Hecho

Tokens, componentes y catálogo. Criterio cumplido: `ng test` (20) y `ng build` en verde. El cuerpo de `/inicio` sigue en la paleta `q-*`.

## Sprint 1 — Shell, inicio, parque

### Shell — Hecho

Chrome Stitch en `src/app/features/shell/`. Escritorio: marca y herramientas en la primera fila (avisos, configuración si el rol entra, persona, iniciales, restablecer) y píldora con scroll de las rutas reales del rol. Móvil: el mismo encabezado, barra fija (Inicio, Parque, Telemetría, Incidentes cuando el rol las tiene) y «Más» en el cajón agrupado, con enlace a `/sistema`. El contenido de cada pantalla queda en `.shell-stage` claro hasta que esa ruta se migre.

Se conserva el guion, los nombres accesibles, el aviso `role="status"`, la alerta WAN `role="alert"`, persona, organización, rol y reloj. No hay buscador inerte. Capturas: `docs/screenshots/shell-1440.png` y `docs/screenshots/shell-390.png`.

Pendiente en este sprint: `/inicio` y `/parque`.

| | `/inicio` | `/parque` |
|---|---|---|
| Maqueta | `helioguard_dashboard_principal` y `helioguard_mobile_dashboard` como chrome, no como contenido residencial | `helioguard_monitoreo_satelital_telemetr_a_cenital` como marco. Sin shader, sin «Térmica (FLIR)», sin Sentinel |
| Componentes | `hg-kpi` solo para el déficit real del mock, `hg-alert`, `hg-button`, `hg-progress` para esperado/real | `hg-map-frame`, `hg-sensor`, `hg-button` |
| Diferencia | La maqueta abre con «Welcome in» y cuatro widgets de hogar. Inicio hoy responde si hay algo que atender | La maqueta es un teatro orbital. Parque hoy son cuatro bloques, trama y dron simulado |
| Layout | Escritorio: frase + un paso. Tablet y 390: la acción primaria queda arriba del texto secundario | La grilla 2×2 de bloques pasa a una columna bajo 768. El sello cenital no se esconde |
| Se conserva | Escenario, déficit Wh, ARS si el rol lo ve, enlace a incidente o parque, estado de carga | Selección de bloque, escenarios, animación del dron, leyenda «no es una aeronave real» |
| Prioridad | P0 | P0 |
| Aceptación | Misma frase de régimen o de B2. Un solo botón primario. Contraste de los tokens `hg` si la pantalla entra al lienzo oscuro | Clic en B2 sigue marcando suciedad. El dron se mueve con «Animar dron». El sello sigue visible a 390 |

El shell (`hg-nav-pill` o el lateral actual) se decide en este sprint. Si el lateral se mantiene, no hace falta la píldora de cinco ítems de la maqueta: la app tiene catorce rutas con roles.

## Sprint 2 — Telemetría, energía, integridad

| | `/telemetria` | `/energia` | `/integridad` |
|---|---|---|---|
| Maqueta | Panel lateral de la cenital y widgets de flujo. No el canvas satelital | Barras de `helioguard_dashboard_principal` («Energy Generation»), con unidad Wh y hora local | No hay maqueta. Catálogo: tabla, alerta, badge |
| Componentes | `hg-sensor`, `hg-chart-frame`, `hg-alert`, `hg-error`, `hg-button` | `hg-tabs`, `hg-chart-frame`, `hg-progress`, `hg-badge` | `hg-table`, `hg-search`, `hg-badge`, `hg-alert` |
| Diferencia | La maqueta muestra RTT, lat/lon de Australia y un enlace satelital. La pantalla muestra inversor → ESP32 → Raspberry → outbox → WAN | La maqueta no tiene causa. La pantalla sí: polvo, nube, granizo, humo, inversor | La maqueta no distingue replay de intrusión. La pantalla lo dice en texto |
| Layout | Los cuatro pasos del camino caben en fila desde 768 y en columna a 390 | Tabs arriba, gráfico debajo, sin recortar la unidad | Tabla con scroll horizontal. La caption sigue |
| Se conserva | Publicar original, replay, misma secuencia, boot nuevo, fallo de lectura, reintento, prueba de ingestión | Escenarios de pérdida y la regla de no cerrar la causa sola | Eventos, dispositivos, valoraciones, «criptográficamente verificada: no» |
| Prioridad | P0 | P0 | P0 |
| Aceptación | Replay y boot siguen escribiendo el mock. El fallo no borra el ledger | Cambiar el escenario actualiza el gráfico con unidad y tiempo | La tabla vacía usa `hg-empty`. Sigue sin aparecer «ataque confirmado» |

## Sprint 3 — Incidentes, misiones, mantenimiento

| | `/incidentes` | `/misiones` | `/mantenimiento` |
|---|---|---|---|
| Maqueta | Badges y alertas del catálogo Stitch. No hay pantalla propia | Marco de mapa + checklist. No el botón de despegue de la maqueta satelital | Tabla y estados. No hay maqueta propia |
| Componentes | `hg-alert`, `hg-badge`, `hg-table`, `hg-button`, `hg-empty` | `hg-map-frame`, `hg-field`, `hg-sensor`, `hg-button` | `hg-table`, `hg-badge`, `hg-field`, `hg-button` |
| Diferencia | La maqueta no modela severidad + dominio energía/seguridad | La maqueta no tiene viento, helada, techo, fabricante, operador ni sitio | La maqueta no tiene orden de trabajo ni nota |
| Layout | Lista primero, acción de la fila visible sin hover | Checklist en columna a 390. El mapa no tapa los bloqueos | La nota y el cambio de estado no comparten un solo botón |
| Se conserva | Reconocer, crear orden, enlace al parque | Proponer, checklist, aprobar en la demo, despachar, recorrer, verificar. La aprobación no es un permiso de vuelo | Aceptar orden, notas, pasos, cancelar |
| Prioridad | P0 | P0 | P0 |
| Aceptación | Crear la orden desde el incidente sigue abriendo la misión | Con helada o viento el despacho no se salta. El dron no arranca solo | La orden viva cambia de estado y la nota queda en el mock |

## Sprint 4 — Instalaciones, auditoría, fuentes

| | `/instalaciones` y `/instalaciones/:id` | `/auditorias` | `/fuentes` |
|---|---|---|---|
| Maqueta | Ficha no dibujada en Stitch. Usar `hg-card`, `hg-drawer`, `hg-sensor` | Formulario por secciones del catálogo | Tabla de procedencia. Sin maqueta |
| Diferencia | No hay equivalente Stitch. No inventar un marketplace de plantas | La maqueta no tiene preguntas sí/no ni evidencias | No mezclar fuente meteorológica con imagen térmica |
| Se conserva | Listado del ámbito, strings, denegado cross-tenant, cronología | Sí/no/nota, enviar, cerrar, reabrir, offline de la demo | Procedencia y el texto de que no es termografía |
| Layout | Ficha: datos arriba, strings debajo. Drawer solo para un equipo | Una pregunta visible por grupo en 390 | Tabla con scroll, sin perder la unidad |
| Prioridad | P1 | P1 | P1 |
| Aceptación | Abrir Salta con la persona que no tiene acceso sigue en el rechazo. El string cambia de estado | Cada respuesta persiste al recargar | Ninguna fila nueva afirma un satélite que no está en el mock |

## Sprint 5 — Economía, planes, organizaciones, configuración

| Ruta | Maqueta | Componentes | Se conserva | Prioridad |
|---|---|---|---|---|
| `/economia` | Widget «Earning» solo como referencia de una cifra. La moneda de la demo es ARS, no AUD | `hg-kpi`, `hg-field`, `hg-button` | Tarifa, pérdida demostrativa, roles que no entran | P2 |
| `/planes` | Ninguna | `hg-card`, `hg-badge`, `hg-button` | Aceptar propuesta una vez | P2 |
| `/organizaciones` | Ninguna | `hg-table`, `hg-alert` | Cambio de persona y aislamiento de Salta | P2 |
| `/configuracion` | Ninguna | `hg-button`, `hg-field`, `hg-alert` | Exportar, importar, restablecer | P2 |

`/inicio?denegado=` sigue mostrando el aviso de permiso. No es una ruta nueva.

## Criterio común de cada sprint

- `ng test` y `ng build`.
- El e2e de `e2e/demo.spec.ts` sigue encontrando Sector sucio, la misión, el replay y el rol técnico.
- Capturas de la pantalla migrada a 1440, 768 y 390.
- Ningún botón del guion pasa a ser decorativo.
- Si el lienzo de esa pantalla pasa a `.hg`, los pares de `tokens.spec.ts` siguen cumpliéndose y el texto de límite (simulado, sin permiso de vuelo, sin ataque confirmado) permanece.
