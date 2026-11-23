# Catálogo de componentes

Ruta: `/sistema`. Código: `src/app/shared/ui/`. Enlace «Sistema visual» en la barra del shell. No reemplaza el Storybook HTML de `ideas/maquetas/`; ese archivo no es una dependencia Angular.

Los componentes son standalone. El botón, el campo y los marcos no llaman al `DemoStore`.

| Selector | API breve | Para qué pantalla sirve |
|---|---|---|
| `hg-brand` | Enlace a `/inicio` | Shell |
| `hg-nav-pill` | `items`, `active`, `href` opcional | Atajos; hoy el shell sigue en lateral |
| `hg-button` | `variant` primary, secondary, ghost, danger; `loading`; `disabled`; `type` | Acciones del guion y de cada feature |
| `hg-icon-button` | `label` obligatorio, `badge` | Buscar, avisos, cerrar |
| `hg-badge` | `tone` neutral, solar, mint, amber, coral | Estado corto junto a texto |
| `hg-tooltip` | `text` | Aclaración del medidor |
| `hg-field` + `hgControl` | `label`, `hint`, `error`. Marca `aria-invalid` y `aria-describedby` | Filtros, tarifas, auditoría, misión |
| `hg-card` | `title`, slot `[hgCardAction]` | Paneles que hoy usan `.sheet` |
| `hg-kpi` | `label`, `value`, `unit`, `note` | Una cifra con unidad. No es una fila de cuatro clones |
| `hg-table` | Proyecta `<table>` con `<caption>` | Integridad, incidentes, instalaciones |
| `hg-search` | `value`, `valueChange` | Filtro de tablas |
| `hg-pagination` | `page`, `pages`, `pageChange` | Listados largos |
| `hg-tabs` | `tabs`, `selected`, flechas, Home, End | Energía: esperado / real / causa |
| `hg-modal` | `title`, `open` | Confirmaciones. `<dialog>` nativo, Escape y fondo |
| `hg-drawer` | Igual, anclado a la derecha | Ficha o evidencia sin cambiar de ruta |
| `hg-alert` | `tone`, `title` | Info, éxito, aviso, peligro, con icono |
| `hg-sensor` | `tone`, `label` obligatorio | Línea, desvío, caído, WAN, sin clasificar |
| `hg-empty` | `title`, `detail` | Listas vacías |
| `hg-error` | `title`, `detail`, `retryLabel`, `retry` | Fallo de lectura |
| `hg-skeleton` | `lines` | Carga. Hoy las pantallas dicen «Abriendo la sala…» |
| `hg-chart-frame` | `title`, `unit`, `rangeLabel`, `note` | El gráfico sigue siendo el contenido proyectado |
| `hg-equalizer` | `bars`, `activeUntil`, `label` | Muestra de carga, no el ledger |
| `hg-gauge` | `percent`, `status`, `label`. El número va en el centro | Reserva. No compara dos series |
| `hg-progress` | `label`, `value` | Avance con texto |
| `hg-flow` | `parts`: label, value, tone | Solar / sitio / red, con leyenda textual |
| `hg-map-frame` | `title`. Sello fijo «Vista cenital simulada» | Parque. El SVG o los bloques siguen siendo interactivos |

## Qué no se duplicó

`.sheet`, `.fact`, `.kicker`, `.mono` y `.hatch` siguen en las pantallas. No hay una segunda tabla ni otro botón dentro de cada feature. El catálogo es el lugar para ver variantes antes de sustituir esas clases, pantalla por pantalla.

## Verificación hecha en el navegador

En `http://127.0.0.1:4217/sistema`:

- La pestaña «Causa» pasa a seleccionada y cambia el panel a la hipótesis (polvo, nube, inversor).
- El sitio vacío queda `aria-invalid` con el texto «El sitio es obligatorio».
- El botón de carga expone `disabled` y `aria-busy`.
- B2 del mapa de ejemplo arranca `aria-pressed`. Los diálogos nacen cerrados (`dialog.open === false`).

Capturas de esa ruta, a 1440, 768 y 390, en `docs/screenshots/sistema-*.png`.
