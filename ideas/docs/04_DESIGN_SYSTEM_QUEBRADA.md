# HelioGuard — Dirección de arte «Estratos de la Quebrada»

## Concepto

Una herramienta industrial confiable nacida en el NOA, no un dashboard genérico ni una postal turística. La identidad toma la **estratificación geológica de la Quebrada de Humahuaca** y la traduce a jerarquía informativa, paleta contenida y superficies sobrias.

## Paleta propuesta (inspiración, no muestreo cromático certificado)

| Token | HEX | Uso |
|---|---|---|
| `--q-bg` | `#F7F4EE` | fondo cálido de aplicación, no blanco puro |
| `--q-surface` | `#FFFFFF` | paneles que realmente agrupen información |
| `--q-ink` | `#24302D` | texto principal |
| `--q-muted` | `#4B5A57` | texto secundario |
| `--q-line` | `#DDD5C8` | separación discreta |
| `--q-terracotta` | `#8F432E` | acción principal / acento de marca |
| `--q-clay` | `#984B33` | variación de tierra rojiza |
| `--q-wine` | `#6B443D` | gravedad visual / contraste |
| `--q-cactus` | `#315D57` | estados informativos y navegación |
| `--q-slate` | `#365D65` | datos de energía, telemetría |
| `--q-sand` | `#D2BD98` | superficies complementarias, no texto |
| `--q-amber` | `#9A6E2C` | gráficos, NO texto pequeño sobre crema |
| `--q-danger` | `#A53232` | alertas críticas, con etiqueta + icono |

**Estados:** éxito y fallo semánticos distintos de los acentos geológicos. Nunca usar color como única señal.

## Tipografía y layout

- Cuerpo: **IBM Plex Sans** (con fuente local/licencia adecuada si se empaqueta); para paneles técnicos, **IBM Plex Mono** en lectura de métricas/códigos. Fallback de sistema si no se incluyen fuentes.
- No redistribuir fuentes descargadas sin verificar la licencia. Evitar dependencias externas en runtime.
- Escala basada en 4/8 pt. Densidad equilibrada: datos técnicos al alcance, sin tarjetas gigantes; jerarquía clara antes del detalle.
- Desktop: navegación lateral compacta, barra de contexto superior, zona principal con la instalación/mapa/tabla protagonista y panel de incidente secundario.
- Tablet: navegación colapsable; mobile: acciones principales, alertas y ficha; tablas se convierten en resumen/desglose sin truncar información importante.
- Iconos profesionales de familia **Lucide** o **Tabler** únicamente; tamaños y strokes coherentes. Sin emojis como sistema visual de la aplicación.
- Fotografías: usar solo las que tengan licencia verificada. Evitar stock genérico o imágenes sintéticas fingiendo medición real. Ilustraciones SVG propias para parque y equipos.

## Anti-slop: 12 prohibiciones verificables

1. Nada de fondo púrpura/azul en degradado para una app de energía local.
2. No comenzar con cuatro KPI en tarjetas clonadas si la tarea central es resolver una alerta.
3. Evitar tarjetas anidadas y bordes/sombras sobre cada elemento.
4. No usar espaciados todos iguales: agrupar por proximidad y tarea.
5. No repetir el mismo ícono para módulos distintos.
6. No inventar métricas exactas ni gráficas sin referencia temporal y unidad.
7. No mezclar gráficos decorativos con indicadores operativos.
8. No poner texto diminuto o de bajo contraste.
9. No ocultar acciones importantes detrás de hover ni menús ambiguos.
10. No usar botones que no hacen nada, modales sin estado, formularios vacíos ni tablas falsas.
11. No utilizar transiciones excesivas; animar relaciones funcionales (alerta→equipo→intervención).
12. No copiar estilos enteros de un framework sin personalizar jerarquía, tokens y contenido.

## Vistas principales que deben definir la identidad

- **Centro de operaciones e integridad:** parque cenital SVG interactivo con bloques seleccionables, estado de telemetría y lista de anomalías.
- **Ficha de instalación:** información de sitio, energía esperada/real y equipamiento, con cronología del activo.
- **Auditoría en campo:** formulario por secciones, progreso discreto, pruebas/evidencias reales, estados por paso.
- **Ciberseguridad:** bandeja de replay/inyección con trazabilidad, microdatos legibles y procedencia; sin confundir estados inferidos con confirmación.
- **Misiones de dron:** mapa cenital con ruta, checklist, operador/estado de autorización y verificación posintervención.
- **Mantenimiento:** tablero de trabajo operacional con técnico, dron simulado, horarios y validación antes/después.

## Criterios QA visuales

- WCAG 2.2 AA cuando aplica. Verificar `#8F432E` sobre `#F7F4EE` (~6.35:1), `#315D57` sobre `#F7F4EE` (~6.75:1), `#24302D` sobre `#F7F4EE` (~12.46:1).
- No usar `#9A6E2C` como texto de tamaño normal sobre crema (contraste ~4.12:1, insuficiente AA).
- Capturas 375×812, 768×1024, 1280×800, 1440×900 y modo de movimiento reducido.
- Auditoría visual final por un segundo agente o revisión manual: jerarquía, densidad, accesibilidad, acciones, originalidad.

## Referencias consultadas

- Paisaje/paleta: [La Ruta Natural — Quebrada de Humahuaca](https://www.larutanatural.gob.ar/es/imperdible/99/quebrada-de-humahuaca); [UNESCO Quebrada](https://whc.unesco.org/en/list/1116/).
- Criterios de diseño en comunidad: [r/UI_Design sobre patrones repetidos de IA](https://www.reddit.com/r/UI_Design/comments/1xifkp9/what_is_it_about_aidesigned_interfaces_that_makes/); [r/web_design sobre jerarquía](https://www.reddit.com/r/web_design/comments/1b3w8u6).
- W3C [WCAG 2.2](https://www.w3.org/TR/WCAG22/).
- Angular CLI/Tailwind: [Angular](https://angular.dev/guide/tailwind); [Tailwind CSS](https://tailwindcss.com/docs/installation/framework-guides/angular).

**Nota:** los HEX son una propuesta original inspirada en el paisaje, no colores oficiales del patrimonio ni una marca registrada.
