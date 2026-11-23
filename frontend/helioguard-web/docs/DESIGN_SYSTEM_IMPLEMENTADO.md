# Design system implementado

Alcance: tokens y componentes en `frontend/helioguard-web`. Las pantallas de la demo no cambiaron de paleta.

## Dos sistemas, a propósito

| Token | Valor | Dónde vive |
|---|---|---|
| `q-*` | Quebrada, tema claro ya en producción de la demo | `src/styles.css`, shell y features |
| `hg-*` | Stitch, tema oscuro del catálogo | Mismo archivo, activado por la clase `.hg` |

No hay selector de tema. Las referencias no describen un modo claro/oscuro intercambiable: describen dos productos visuales distintos. Mezclarlos en el shell habría repintado Inicio, Parque y el guion.

Hex canónico, tomado de `ideas/maquetas/helioguard_dashboard_principal/code.html`:

- Canvas `#070A0D`
- Tarjeta `#10161D`
- Elevado `#18222C`
- Neón `#E6FF00` (hover `#F4FF4D`)
- Menta `#10B981`
- Ámbar `#F59E0B`
- Coral `#EF4444`
- Red `#64748B`
- Texto `#E2E8F0`, secundario `#94A3B8`

`design.md` usa `#DFFF00` / `#0C1012` / `#00E599`. El móvil usa `#D6F000` / `#111417`. Esas variantes quedan documentadas y no se promedian.

## Tipografía

Stitch pide Plus Jakarta Sans, Space Grotesk, Manrope y JetBrains Mono por Google Fonts. No se agregó esa dependencia ni un CDN. Roles sobre la pila ya definida (`ui-sans-serif` / `ui-monospace`):

- Título de página: hasta 2.6rem, tracking negativo
- Métrica: `clamp(1.75rem, 3vw, 2.25rem)`, `tabular-nums`
- Cuerpo y controles: 0.875rem, alto mínimo 2.75rem
- Nota: 0.8125rem. No hay texto de 10px

## Forma, elevación, estados

- Controles en píldora. Tarjetas a 16px, borde 1px `rgb(255 255 255 / 8%)`.
- Sombra solo en diálogo y panel (`--shadow-hg-overlay`). Las tarjetas se separan por superficie, no por glow.
- La grilla de 32px al 2.5% es la retícula técnica del HTML. No hay halos radiales de la maqueta: eran decorativos.
- Focus del catálogo: outline neón 2px. El resto de la app conserva el outline cactus.
- Hover, disabled, `aria-busy` en el botón que carga, vacío, error con reintento, skeleton.
- `prefers-reduced-motion` ya acortaba las animaciones; el spinner y el skeleton entran en esa regla.
- Iconos: solo `@lucide/angular`, el paquete que ya usaba el shell.

## Contraste calculado

Fórmula WCAG en `src/app/shared/ui/tokens.spec.ts` (20 tests en verde, 8 de ellos de estos pares). No es una medición con pantalla calibrada.

| Par | Relación |
|---|---|
| `#E2E8F0` sobre `#070A0D` | 16.10 |
| `#94A3B8` sobre `#070A0D` | 7.74 |
| `#94A3B8` sobre `#10161D` | 7.09 |
| `#070A0D` sobre `#E6FF00` | 17.64 |
| `#6EE7B7` sobre `#10161D` | 11.93 |
| `#FDE68A` sobre `#10161D` | 14.61 |
| `#FECACA` sobre `#10161D` | 12.57 |

`#64748B` y `#EF4444` no se usan como texto chico. En el catálogo son muestras de color con la etiqueta al lado, sobre la tarjeta. Ámbar y coral de estado van en `#FDE68A` y `#FECACA`, con icono y palabra.

## Qué no se copió

- Material Symbols (la maqueta móvil y la satelital los mezclan con SVG).
- El shader WebGL y el botón «Re-escaneo orbital».
- Cifras residenciales (AUD, 78 % de una batería doméstica) como datos de Puna Norte.
- Termografía satelital de paneles.
