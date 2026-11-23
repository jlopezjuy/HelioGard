# Auditoría UX/UI — HelioGuard

Fecha: 23 de noviembre de 2026. Código auditado: `frontend/helioguard-web`. Referencias: `ideas/maquetas/`.

## Qué hay en las maquetas

No hay un Storybook Angular. Hay HTML estático de Google Stitch y dos especificaciones que no coinciden píxel a píxel.

| Referencia | Qué muestra | Tokens que declara |
|---|---|---|
| `helioguard_dashboard_principal/code.html` + `screen.png` | Dashboard residencial oscuro, navbar en píldora, KPIs, flujo solar/sitio/red | Canvas `#070A0D`, neón `#E6FF00`, esmeralda `#10B981`, tarjeta `rgba(16,22,29,.82)`, texto `#E2E8F0`, Plus Jakarta Sans |
| `helioguard_mobile_dashboard/code.html` + `screen.png` | La misma historia en una columna, barra inferior, Material Symbols | Canvas `#111417`, lima `#D6F000`, menta `#01E599`, Space Grotesk / Manrope / JetBrains Mono |
| `helioguard_monitoreo_satelital_telemetr_a_cenital/code.html` + `screen.png` | Teatro cenital con shader, capas «Térmica (FLIR)», «SAT-LINK: SENTINEL-4B», hub Alice Springs | Misma paleta oscura del dashboard de escritorio, más Material Symbols |
| `helioguard_ui_component_storybook/` y `helioguard_storybook_showcase_49_componentes/` | Catálogos HTML, no una app | Tokens del móvil (`primary-container #d6f000`) |
| `design.md_helioguard_design_system.md` | Spec de 41 piezas | Neón `#DFFF00`, canvas `#0C1012`, menta `#00E599`, fuente Space Grotesk |
| `obsidian_cleantech_telemetry/DESIGN.md` | Otra variante Material | Superficie `#111417`, lima `#E4FF1A` / `#D6F000` |

La maqueta satelital afirma termografía de paneles, un enlace Sentinel y coordenadas de Alice Springs. Eso contradice `ideas/docs/07_SATELITES_DRONES_LIMITACIONES.md` y el texto ya visible en Parque: no es ortofoto ni termografía. Esas afirmaciones no entran al sistema de componentes.

## Qué hay en Angular

Angular 21.2, zoneless, Tailwind CSS 4.3 (`@theme` en `src/styles.css`), Lucide 1.77. Rutas reales en `src/app/app.routes.ts`. No hay biblioteca `shared/ui` previa: cada pantalla repite `min-h-11`, `rounded-ui`, `bg-q-terracotta` y bordes `q-line`.

La app en ejecución es clara, de la Quebrada (`#F7F4EE`, terracota `#8F432E`, cactus `#315D57`). Tipografía de sistema. Shell con lateral verde, grupos Operar / Medir / Confiar / Cuenta, guion de demo y cambio de persona. Iconos Lucide, sin emojis.

`ideas/apps/helioguard-web` es otra copia, con IBM Plex empaquetado. No es este frontend.

## Diferencia visual

| | Angular actual | Stitch |
|---|---|---|
| Fondo | Crema `#F7F4EE` | Casi negro `#070A0D` |
| Acción primaria | Terracota | Lima `#E6FF00` con texto negro |
| Navegación | Lateral fijo desde `lg`, cajón en móvil | Píldora superior; en móvil, barra inferior |
| Densidad | Una pregunta por pantalla, poco chrome | Muchas tarjetas de energía residencial (kWh, AUD, clima, batería) |
| Datos | Wh, ARS de demostración, estados del mock | 1 248 kWh, 78 %, 58.60 AUD: no salen del ledger |
| Vista cenital | Cuatro bloques clicables, trama, dron simulado | Canvas WebGL y capas que el producto no puede afirmar |
| Estados | Párrafos y bordes | Gauges, ecualizador, badges, skeletons |

Lo que sí se puede reutilizar sin reescribir pantallas: la semántica de estados (régimen, desvío, sin lectura, WAN), Lucide, el lateral con roles, tablas, formularios nativos y la vista cenital ya interactiva.

Lo que pide refactor, no un reemplazo inmediato: botones repetidos, inputs sin componente, tablas sin contenedor, alertas solo con borde, carga como texto suelto.

## Decisiones de esta pasada

1. No se repintan las pantallas. El guion (`Sector sucio`, misión, replay, inyección) sigue igual.
2. Los tokens Stitch viven junto a los `q-*`. El catálogo `/sistema` es el único lienzo oscuro.
3. El hex de implementación sale del HTML de escritorio (`#070A0D`, `#E6FF00`, `#10B981`), no del markdown (`#DFFF00`) ni del móvil (`#D6F000`).
4. No se cargan Plus Jakarta, Space Grotesk, Manrope ni JetBrains por CDN. La jerarquía usa la pila de sistema ya configurada y cifras tabulares.
5. No se instaló Storybook ni otra skill. El catálogo es una ruta Angular.
6. El marco de mapa obliga el sello «Vista cenital simulada».
