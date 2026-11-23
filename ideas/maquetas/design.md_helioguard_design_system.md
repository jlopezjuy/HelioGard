# DESIGN SYSTEM — HELIOGUARD (DESIGN.MD)
> **Energy Intelligence & Solar IoT Dashboard System**
> Versión: `1.0.0` | Especificación de Tokens, Componentes, Tipografía y UI Guidelines para Web & Angular.

---

## 1. Visión y Fundamentos del Diseño

**HelioGuard** proyecta precisión tecnológica, sostenibilidad moderna y control de energía en tiempo real. Combina un modo oscuro inmersivo (*OLED Deep Dark*) con acentos bio-luminescentes (*Electric Lime* y *Emerald Mint*), permitiendo al usuario interpretar telemetría compleja de un solo vistazo.

### Principios Fundamentales
1. **At-a-Glance Telemetry**: La información crítica (batería, generación, flujo y balance de carga) debe ser legible en menos de 2 segundos.
2. **Luminous Hierarchy**: El color se utiliza de forma funcional para indicar estados de energía, flujo y rendimiento, nunca como mero adorno.
3. **Tactile Elevation & Depth**: Contenedores oscuros multicapa con sutiles bordes translúcidos y esquinas amplias (`rounded-2xl` / `rounded-3xl`) que aíslan el contenido del fondo.
4. **Resilient Data States**: Tolerancia visual para valores altos, variaciones negativas/positivas y métricas en vivo (IoT).

---

## 2. Paleta de Colores & Design Tokens

### 2.1 Colores Primarios y Semánticos
| Nombre del Token | Valor Hex | Uso / Semántica |
| :--- | :--- | :--- |
| `--color-primary-solar` | `#DFFF00` / `#E5FF10` | Energía solar activa, carga de batería, barras de rendimiento principal. |
| `--color-secondary-mint` | `#00E599` / `#10B981` | Consumo doméstico, autosuficiencia energética, estados positivos (+1.5%). |
| `--color-accent-amber` | `#F59E0B` | Radiación solar, indicadores climáticos y advertencias moderadas. |
| `--color-accent-coral` | `#EF4444` / `#DC2626` | Alertas de falla en inversor, sobrecarga o estados críticos. |
| `--color-grid-neutral` | `#64748B` / `#94A3B8` | Red eléctrica pública (Grid), mediciones secundarias y barras inactivas. |

### 2.2 Escala de Superficies (Dark Surfaces)
| Nombre del Token | Valor Hex / RGBA | Uso |
| :--- | :--- | :--- |
| `--surface-canvas` | `#0A0D0E` / `#0C1012` | Fondo global de la aplicación (Canvas principal con textura de grilla o sutil malla solar). |
| `--surface-card-base` | `#111618` / `#141A1D` | Fondo de tarjetas estándar (KPIs, widgets métricos). |
| `--surface-card-elevated`| `#182024` | Tarjetas activas, popovers, selectores desplegables y modales. |
| `--surface-pill-active` | `#1E262B` | Píldora de navegación activa (`Dashboard`). |
| `--surface-pill-inactive`| `transparent` | Botones de navegación secundarios en la barra superior. |
| `--border-subtle` | `rgba(255, 255, 255, 0.08)` | Bordes ultra finos para delimitación nítida de tarjetas sin restar contraste. |
| `--border-focus` | `rgba(223, 255, 0, 0.4)` | Halo de foco accesible para elementos interactivos. |

### 2.3 Tipografía & Jerarquía de Texto
| Nombre del Token | Valor Hex | Uso |
| :--- | :--- | :--- |
| `--text-primary` | `#FFFFFF` | Titulares, KPIs principales (`1,248 kWh`, `78%`, etc.). |
| `--text-secondary` | `#94A3B8` / `#A0ABBA` | Etiquetas descriptivas, unidades métricas, subtítulos. |
| `--text-muted` | `#64748B` / `#475569` | Marcadores de tiempo, texto de copyright y badges auxiliares. |

---

## 3. Tipografía & Escala

- **Familia Tipográfica Primaria**: `Inter`, `Plus Jakarta Sans`, o `-apple-system, BlinkMacSystemFont, sans-serif`.
- **Familia Numérica / Datos (Opcional)**: `JetBrains Mono` o `Inter Display` con `font-feature-settings: 'tnum' on` (tabular figures para números estables en tiempo real).

### Escala de Tamaños
- **Display 1 (Hero Title)**: `40px` a `48px` | `font-weight: 700` | Line-height: `1.1` | Tracking: `-0.02em`
- **Metric Giant (KPI Numbers)**: `36px` a `42px` | `font-weight: 700` | Line-height: `1.1`
- **Card Heading (Widget Titles)**: `14px` a `15px` | `font-weight: 500` | Tracking: `0`
- **Submetric / Body**: `13px` a `14px` | `font-weight: 400`
- **Micro / Captions / Badges**: `10px` a `12px` | `font-weight: 600` | Tracking: `0.04em` (Uppercase)

---

## 4. Spacing, Bordes & Sombras

### 4.1 Sistema de Espaciado (Grid de 4px / 8px)
- `space-1`: `4px`
- `space-2`: `8px`
- `space-3`: `12px`
- `space-4`: `16px` (Padding interno compacto de chips/botones)
- `space-6`: `24px` (Padding interno estándar de tarjetas de métricas)
- `space-8`: `32px` (Gaps entre columnas y módulos del dashboard)

### 4.2 Radios de Borde (Border Radius)
- `rounded-pill`: `9999px` (Navegación superior, botones de acción, tags de estado, barras de progreso redondeadas).
- `rounded-card`: `20px` a `24px` (`1.25rem` - `1.5rem` — Tarjetas de métricas y visualizadores).
- `rounded-subcard`: `12px` a `14px` (Contenedores anidados, selectores de fechas, tooltips).

### 4.3 Elevación & Glow Effects
- **Card Shadow**: `0 10px 30px -10px rgba(0, 0, 0, 0.5)`
- **Solar Neon Glow**: `0 0 25px rgba(223, 255, 0, 0.25)` (utilizado en el arco radial de batería y barra activa de energía).
- **Emerald Pulse**: `0 0 20px rgba(0, 229, 153, 0.2)`

---

## 5. Anatomía de Componentes del Dashboard

### 5.1 App Navigation Chrome (Navbar)
- **Estructura**: Barra horizontal fija o sticky superior con altura de `72px`.
- **Isotipo/Logo**: Escudo con rayo de sol / hoja angular en `#DFFF00` junto a la palabra *HelioGuard*.
- **Pill Switcher**: Contenedor oscuro envolvente (`bg-[#111618]`) que agrupa:
  - *Dashboard* (Activo, fondo oscuro sutil y texto blanco).
  - *Solar Panels*, *Battery*, *Analytics*, *Live Monitoring* (Texto secundario, hover con transición rápida).
- **Herramientas de Usuario (Right cluster)**:
  - Buscador circular (`rounded-full bg-[#182024]`).
  - Botón de notificaciones con badge indicador ámbar/verde.
  - Configuración y Avatar circular de usuario (`w-10 h-10 border border-white/10`).

### 5.2 Hero Welcome Banner
- Fondo texturizado con sutil patrón vectorial de celdas solares (Solar Grid lines).
- Título principal: *"Welcome in, HelioGuard."*
- Subtexto descriptivo: *"Monitor solar energy. Reduce costs. Power future."*
- Grupo de chips de telemetría / alerta: 3 accesos circulares rápidos (Rayo, Cronómetro, Alerta).

### 5.3 Widget: "Estimated Full" (Tiempo de Carga)
- Muestra el tiempo restante con unidades diferenciadas: `1` hr `34` mm.
- Ecualizador de carga: 16–20 barras verticales con gradiente dinámico (barras amarillas en nivel actual, grises en capacidad residual).

### 5.4 Widget: "Battery Status" (Medidor Radial)
- Anillo de porcentaje radial con escala circular segmentada (80 ticks o línea continua con gradiente neón).
- Valor central: `78% Charging...`.
- Pie de tarjeta: Identificador de tecnología de celda: `LITHIUM-IRON BUFFER (14.2 KWH)`.

### 5.5 Widget: "Total Energy" & "Load Balance"
- Lectura total en kWh (`1,248 kwh`) con indicador de porcentaje diario (`70% Today`).
- Barra de progreso segmentada con rayado diagonal (striped track) para capacidad no consumida.
- Medidor bifásico (Left Side vs. Right Side) con barras de nivelación espectral divididas.

### 5.6 Widget: "Energy Generation" (Histórico & Rendimiento)
- Selector de rango temporal: Dropdown tipo pastilla (`6 months`, `1 year`, etc.).
- Gráfico de barras de alta densidad: 24–32 barras agrupadas por mes (Apr, May, Jun, Jul, Aug, Sep).
- Resumen financiero de inyección: `$20,678.89` con tag de tendencia (`-1.5%` / `+2.4%`) e indicador de leyenda `Solar Yield`.

### 5.7 Widget: "Earning" & "Weather"
- Iconografía con contenedor suave (`rounded-xl p-3 bg-yellow-400/10`).
- Conversión monetaria en tiempo real (`58.60 AUD`), con tasa de compensación por excedente (`$0.12 / kWh`).
- Widget de clima sincronizado con UV Index y nivel de radiación solar (`Sunny 32°C`, `Solar irradiance: High`).

### 5.8 Widget: "Energy Flow" (Diagrama de Flujo Tripartito)
- Barra de distribución tricromática continua:
  - Segmento 1: **Solar** (`5.6 kW` - Solar Neon `#DFFF00`).
  - Segmento 2: **Home** (`4.8 kW` - Emerald Mint `#00E599`).
  - Segmento 3: **Grid** (`1.2 kW` - Slate Neutral `#64748B`).
- Métricas de pie: Autosuficiencia (`Self-sufficiency: 92%`), estado del inversor (`Active Inverter #01`).

---

## 6. Pautas de Accesibilidad (WCAG 2.1 AA)

1. **Relación de Contraste**: Todos los textos blancos y métricas clave sobre fondos oscuros superan una relación `12:1` (muy por encima del estándar `4.5:1`).
2. **Textos Neón**: El color amarillo `#DFFF00` se utiliza como fondo con texto oscuro, o como acento luminoso sobre `#0A0D0E` logrando contraste superior a `14:1`.
3. **Indicadores de Estado Accesibles**: Las alertas no dependen exclusivamente del color; se acompañan siempre de iconos descriptivos (ej. triángulo de advertencia, flechas direccionales).

---

## 7. Mapeo de Clases Tailwind CSS para Frontend / Angular

```json
{
  "theme": {
    "extend": {
      "colors": {
        "helioguard": {
          "bg": "#0c1012",
          "card": "#111618",
          "card-hover": "#161c20",
          "card-border": "rgba(255, 255, 255, 0.08)",
          "solar": "#dfff00",
          "solar-glow": "rgba(223, 255, 0, 0.25)",
          "mint": "#00e599",
          "coral": "#ef4444",
          "grid": "#64748b"
        }
      },
      "borderRadius": {
        "card": "1.5rem"
      }
    }
  }
}
```
