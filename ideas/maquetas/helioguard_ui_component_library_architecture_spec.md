# DESIGN SYSTEM & UI LIBRARY — HELIOGUARD DASHBOARD
> **Descomposición Atómica, Inventario Exhaustivo y Especificación de Componentes Aislados**
> Fuente: Maqueta `HelioGuard - Dashboard Principal` (Desktop High-Fidelity UI).

---

# FASE 1 — INVENTARIO EXHAUSTIVO

A continuación se detalla el desglose sistemático de todos los elementos visuales, estructurales, tipográficos, interactivos y decorativos detectados en la maqueta `HelioGuard - Dashboard Principal`, recorridos de arriba a abajo y de izquierda a derecha.

| # | Componente | Categoría | Tag HTML base | Variantes detectadas | Estados presentes | ¿Repetido? (nº instancias) | Ubicación en la maqueta | Notas |
|---|---|---|---|---|---|---|---|---|
| **01** | `AppShell / LayoutContainer` | Layout & Estructura | `<main>` / `<div>` | Fullscreen OLED dark canvas con grilla técnica de fondo | Default | No (1) | Raíz global de la página | Define el canvas `bg-[#0c1012]`, padding perimetral de 24px/32px y retícula sutil. |
| **02** | `Header / GlobalNavbar` | Layout & Navegación | `<header>` / `<nav>` | Top fixed/sticky bar con 3 clusters (Brand, Nav Pill, User Actions) | Default | No (1) | Cabecera superior | Altura fija ~72px, alineación `flex justify-between items-center`. |
| **03** | `BrandLogo / ShieldMark` | Identidad / Branding | `<div>` / `<a>` | Isotipo vectorial de escudo neón con rayo + Wordmark tipográfico | Default, Hover | No (1) | Header, extremo izquierdo | Escudo con gradiente `#dfff00`, palabra *Helio* (blanco 700) y *Guard* (solar neón 700). |
| **04** | `NavPillSwitcher` | Navegación / Tabs | `<nav>` / `<ul>` | Contenedor envolvente oscuro con items segmentados | Default | No (1) | Header, centro | Contenedor en cápsula (`bg-[#111618] border border-white/5 rounded-full p-1.5`). |
| **05** | `NavPillItem` | Navegación / Controles | `<button>` / `<a>` | 1. Activo (`bg-[#1e262b] text-white shadow-sm`)<br>2. Inactivo (`text-slate-400 hover:text-white`) | Active, Default, Hover, Focus | Sí (5 instancias: Dashboard [activo], Solar Panels, Battery, Analytics, Live Monitoring) | Dentro de `NavPillSwitcher` | Tipografía sans 13px, tracking sutil, padding horizontal `px-4 py-1.5`. |
| **06** | `IconButton / ActionCircular` | Botones / Acciones | `<button>` | 1. Estándar oscuro (`bg-[#182024] border border-white/5`)<br>2. Con indicador badge de notificación | Default, Hover, Active, Focus | Sí (3 instancias: Búsqueda, Notificaciones con dot, Configuración) | Header, cluster derecho | Circular (`w-10 h-10 rounded-full flex items-center justify-center`), SVG stroke 1.5. |
| **07** | `NotificationDot` | Elementos Decorativos / Feedback | `<span>` | Dot circular neón/ámbar | Default, Pulse | No (1) | Superpuesto en `IconButton` de notificaciones | Diámetro 8px, color `#dfff00`, posición absoluta superior derecha. |
| **08** | `UserAvatar` | Identidad / Perfil | `<button>` / `<img>` | Avatar circular con borde de contraste y status dot de conexión | Default, Hover | No (1) | Header, extremo derecho | Tamaño 40x40px, borde `border-white/10`, dot verde esmeralda (#00e599). |
| **09** | `GridDashboardLayout` | Layout & Estructura | `<div>` | Retícula responsive CSS Grid de 3 columnas (gap 24px) | Default | No (1) | Cuerpo principal (`<main>`) | Coordina Hero (col 1), Carga (col 2), Batería (col 3), métricas intermedias y base. |
| **10** | `CardContainer` | Layout & Estructura | `<article>` / `<section>` | 1. Card estándar (`bg-[#111618] border border-white/5 rounded-2xl`)<br>2. Card con fondo texturizado | Default, Hover | Sí (8 instancias principales) | En cada widget del dashboard | Radios amplios (`rounded-3xl` / 24px), elevación sutil, padding 24px. |
| **11** | `CardHeaderWithAction` | Estructuras de Layout | `<div>` | Título de card a la izquierda con botón de flecha diagonal a la derecha | Default | Sí (6 instancias: Estimated Full, Battery, Total Energy, Load Balance, Earning, Weather) | Cabecera superior de cada widget | `flex justify-between items-start`, tipografía 14px muted. |
| **12** | `DiagonalArrowIcon` | Elementos Decorativos / Iconos | `<button>` / `<span>` | Ícono SVG de flecha oblicua (`↗` / `arrow-up-right`) | Default, Hover | Sí (6 instancias) | Esquina superior derecha de widgets interactivos | Tamaño 16x16px dentro de contenedor táctil `text-slate-500 hover:text-white`. |
| **13** | `HeroWelcomeCard` | Layout / Contenedores | `<section>` | Card de bienvenida con textura de panel solar y controles de estado | Default | No (1) | Grid col 1, fila 1 | Textura SVG de malla de silicio/celdas solares, fondo degradado suave. |
| **14** | `DisplayHeading` | Tipografía / Encabezados | `<h1>` | Título Display Hero de dos líneas (*Welcome in,* / *HelioGuard.*) | Default | No (1) | Dentro de `HeroWelcomeCard` | Tamaño 38px–44px, peso 700, interlineado ajustado (`leading-tight`). |
| **15** | `BodyTextSubhead` | Tipografía / Textos | `<p>` | Texto descriptivo secundario (*Monitor solar energy. Reduce costs...*) | Default | No (1) | Debajo de `DisplayHeading` | Color `#94a3b8`, 14px, regular. |
| **16** | `StatusPillButtonGroup` | Botones / Grupos | `<div>` | Fila de 3 botones circulares de telemetría rápida | Default | No (1) | Pie de `HeroWelcomeCard` | Contenedor flex con `gap-3`. |
| **17** | `TelemetryIconButton` | Botones / Acciones | `<button>` | 1. Primario activo solar (`bg-[#dfff00] text-black shadow-glow`)<br>2. Secundario inactivo (`bg-[#182024] text-slate-400 border border-white/5`)<br>3. Alerta / Riesgo (`bg-red-500/10 text-red-400 border border-red-500/20`) | Default, Hover, Active | Sí (3 instancias: Rayo solar, Historial crono, Alerta peligro) | Dentro de `StatusPillButtonGroup` | Diámetro 44x44px, `rounded-full flex items-center justify-center`. |
| **18** | `MetricTimeDisplay` | Tipografía / KPIs | `<div>` | Cifra grande con unidades segmentadas (*1 hr 34 mm*) | Default | No (1) | Widget "Estimated Full" | Números en 36px font-bold (`text-white`), labels en 14px (`text-slate-400`). |
| **19** | `EqualizerBarChart` | Elementos Visuales / Gráficos | `<div>` | Ecualizador de carga de batería con 18 barras verticales | Default, Animado | No (1) | Widget "Estimated Full" | Barras activas (`bg-[#dfff00]`), barras de amortiguación (`bg-slate-700/50`). |
| **20** | `RadialProgressGauge` | Elementos Visuales / Gráficos | `<svg>` / `<div>` | Anillo radial segmentado de 270° con gradiente neón y porcentaje central | Default, Charging | No (1) | Widget "Battery Status" | Arco con glow neón, aguja/stroke `#dfff00`, ticks grises residuales. |
| **21** | `RadialMetricLabel` | Tipografía / Feedback | `<div>` | Texto de lectura central (*78%*) y estado en vivo (*Charging...*) | Default | No (1) | Centro de `RadialProgressGauge` | Valor 36px bold, texto de estado 13px font-medium `text-[#dfff00]`. |
| **22** | `SubcardFooterLabel` | Tipografía / Leyendas | `<div>` | Etiqueta en mayúsculas técnicas (*LITHIUM-IRON BUFFER (14.2 KWH)*) | Default | No (1) | Pie de widget "Battery Status" | 11px, tracking-widest, font-semibold, `text-slate-400`. |
| **23** | `MetricTotalKpi` | Tipografía / KPIs | `<div>` | Métrica numérica monumental con unidad (*1,248 kwh*) | Default | No (1) | Widget "Total Energy" | 38px font-bold blanco, unidad en 14px regular gris claro. |
| **24** | `StripedProgressBar` | Elementos Visuales / Progresos | `<div>` | Barra de progreso con relleno sólido neón y track con rayado diagonal | Default | No (1) | Widget "Total Energy" | Altura 12px, `rounded-full`, track con patrón CSS striped 45°. |
| **25** | `ProgressMetaLabel` | Tipografía / Leyendas | `<div>` | Par clave-valor (*Today* / *70%*) | Default | No (1) | Sobre `StripedProgressBar` | `text-slate-400` y `text-[#dfff00]` en 13px bold. |
| **26** | `BiphaseLoadDisplay` | Elementos Visuales / Telemetría | `<div>` | Reparto de carga bifásica (*Left Side 62%* vs *Right Side 42%*) | Default | No (1) | Widget "Load Balance" | Comparativa en dos columnas simétricas con métricas y barras divididas. |
| **27** | `SegmentedSpectralBar` | Elementos Visuales / Gráficos | `<div>` | Barra de barras micrométricas segmentadas (verde -> amarillo -> gris) | Default | Sí (2 instancias: Fase izquierda y Fase derecha) | Dentro de `BiphaseLoadDisplay` | 12 segmentos verticales de 3px de ancho por 8px de alto. |
| **28** | `DropdownPillSelect` | Inputs y Controles / Select | `<button>` / `<select>` | Selector desplegable en píldora (*6 months ▾*) | Default, Hover, Open | No (1) | Cabecera de widget "Energy Generation" | `bg-[#182024] text-slate-300 text-xs px-3 py-1.5 rounded-full border border-white/5`. |
| **29** | `MonthlyBarChart` | Elementos Visuales / Gráficos | `<div>` / `<svg>` | Gráfico de barras mensual de alta densidad (24 barras agrupadas por mes) | Default, Hover bar | No (1) | Widget "Energy Generation" | Cuadrícula con líneas horizontales, barras neón `#dfff00`, meses Apr a Sep. |
| **30** | `FinancialYieldBadge` | Tipografía / Badges | `<div>` | Monto acumulado (*$20,678.89*) + chip de delta de variación (*-1.5%*) | Default, Negative | No (1) | Pie de "Energy Generation" | Cifra 20px bold, badge en píldora verde mint translúcido con flecha abajo. |
| **31** | `ChartLegendIndicator` | Elementos Decorativos / Leyendas | `<div>` | Dot circular amarillo solar con texto descriptivo (*Solar Yield*) | Default | No (1) | Pie derecho de "Energy Generation" | Dot 8px `#dfff00`, texto 12px `text-slate-400`. |
| **32** | `IconBadgeContainer` | Elementos Decorativos / Contenedores | `<div>` | Contenedor cuadrado redondeado para íconos temáticos con fondo tintado | Default | Sí (2 instancias: Billetera de ganancias, Sol meteorológico) | Widgets "Earning" y "Weather" | 44x44px, `rounded-xl bg-yellow-400/10 text-yellow-300 flex items-center justify-center`. |
| **33** | `FinancialMetricDisplay` | Tipografía / KPIs | `<div>` | Valor monetario con código de divisa (*58.60 AUD*) | Default | No (1) | Widget "Earning" | Número en 32px font-bold, código de moneda en 13px semibold gris. |
| **34** | `SubmetricStatusIndicator` | Tipografía / Feedback | `<div>` | Dot verde pulsante + texto de estado (*• Solar irradiance: High*) | Active / High | Sí (2 instancias) | Debajo de métricas en "Earning" y "Weather" | Dot 6px verde mint `#00e599`, tipografía 12px regular. |
| **35** | `WeatherMetricDisplay` | Tipografía / KPIs | `<div>` | Condición meteorológica y temperatura (*Sunny 32.c*) | Default | No (1) | Widget "Weather" | 28px–32px font-bold blanco. |
| **36** | `AuxiliaryInfoRow` | Tipografía / Textos | `<div>` | Fila con dos textos secundarios explicativos a los extremos | Default | Sí (2 instancias: Tarifa feed-in y UV/Nubosidad) | Pie de widgets "Earning" y "Weather" | 11px–12px `text-slate-500 flex justify-between`. |
| **37** | `TripartiteFlowBar` | Elementos Visuales / Diagramas | `<div>` | Barra tricromática segmentada y continua (Solar, Home, Grid) | Default | No (1) | Widget "Energy Flow" | Barra de 24px de alto dividida en 3 bloques redondeados interconectados. |
| **38** | `FlowMetricLegendGroup` | Tipografía / Métricas | `<div>` | 3 columnas con valor en kW y nombre de fuente de energía | Default | No (1) | Sobre `TripartiteFlowBar` | Solar (5.6kw / `#dfff00`), Home (4.8kw / `#00e599`), Grid (1.2kw / `#64748b`). |
| **39** | `StatusPillBadge` | Tipografía / Badges | `<span>` | Píldora de estado de eficiencia general (*100%*) | Default, Success | No (1) | Esquina superior derecha de "Energy Flow" | `bg-white/5 text-slate-200 px-3 py-1 rounded-full text-xs font-semibold`. |
| **40** | `FlowFooterMetrics` | Tipografía / Leyendas | `<div>` | Autosuficiencia energética (*92%*) y estado del inversor (*Active Inverter #01*) | Default | No (1) | Pie de "Energy Flow" | 12px con acentos en verde esmeralda y texto secundario. |
| **41** | `AppFooterMeta` | Layout / Textos | `<footer>` | Barra de pie con créditos de plataforma, sincronización y versión | Connected | No (1) | Extremo inferior del canvas | Texto 12px centrado `text-slate-500` con dot verde de enlace satelital. |

---

### Resumen por Categoría (Fase 1)

- **Estructuras de Layout & Contenedores**: 7 componentes (`AppShell`, `GlobalNavbar`, `GridDashboardLayout`, `CardContainer`, `CardHeaderWithAction`, `HeroWelcomeCard`, `AppFooterMeta`).
- **Navegación & Acciones de Usuario**: 5 componentes (`BrandLogo`, `NavPillSwitcher`, `NavPillItem`, `IconButton`, `UserAvatar`).
- **Botones & Controles Interactivos**: 4 componentes (`StatusPillButtonGroup`, `TelemetryIconButton`, `DiagonalArrowIcon`, `DropdownPillSelect`).
- **Tipografía & Display de Métricas**: 11 componentes (`DisplayHeading`, `BodyTextSubhead`, `MetricTimeDisplay`, `RadialMetricLabel`, `SubcardFooterLabel`, `MetricTotalKpi`, `ProgressMetaLabel`, `FinancialYieldBadge`, `FinancialMetricDisplay`, `WeatherMetricDisplay`, `AuxiliaryInfoRow`).
- **Elementos Visuales & Gráficos de Telemetría**: 8 componentes (`EqualizerBarChart`, `RadialProgressGauge`, `StripedProgressBar`, `BiphaseLoadDisplay`, `SegmentedSpectralBar`, `MonthlyBarChart`, `TripartiteFlowBar`, `FlowMetricLegendGroup`).
- **Badges, Feedback & Decorativos**: 6 componentes (`NotificationDot`, `ChartLegendIndicator`, `IconBadgeContainer`, `SubmetricStatusIndicator`, `StatusPillBadge`, `FlowFooterMetrics`).

**Total Componentes Inventariados: 41 ítems únicos y completos.**

---

# FASE 2 — GENERACIÓN UNO POR UNO

## Tokens y Estilos Globales (CSS Custom Properties)

Estos tokens alimentan a todos los componentes siguientes de forma desacoplada:

```css
:root {
  /* Paleta Solar & Telemetría */
  --hg-color-solar: #dfff00;
  --hg-color-solar-glow: rgba(223, 255, 0, 0.25);
  --hg-color-mint: #00e599;
  --hg-color-mint-glow: rgba(0, 229, 153, 0.2);
  --hg-color-coral: #ef4444;
  --hg-color-amber: #f59e0b;
  --hg-color-grid-neutral: #64748b;

  /* Superficies OLED Dark */
  --hg-surface-canvas: #0c1012;
  --hg-surface-card: #111618;
  --hg-surface-card-hover: #161c20;
  --hg-surface-elevated: #182024;
  --hg-surface-pill-active: #1e262b;
  --hg-border-subtle: rgba(255, 255, 255, 0.07);
  --hg-border-hover: rgba(255, 255, 255, 0.15);

  /* Tipografía & Textos */
  --hg-font-family: 'Space Grotesk', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  --hg-font-mono: 'JetBrains Mono', monospace;
  --hg-text-primary: #ffffff;
  --hg-text-secondary: #94a3b8;
  --hg-text-muted: #64748b;

  /* Radios & Sombras */
  --hg-radius-card: 24px;
  --hg-radius-subcard: 16px;
  --hg-radius-pill: 9999px;
  --hg-shadow-card: 0 16px 36px -12px rgba(0, 0, 0, 0.6);
  --hg-shadow-glow-solar: 0 0 24px rgba(223, 255, 0, 0.28);
}
```

---

### [01] AppShell / LayoutContainer — Layout & Estructura

- **Descripción funcional**: Contenedor maestro a pantalla completa con fondo negro azabache (OLED dark), textura de grilla de telemetría y contención responsive.
- **Anatomía**: Elemento raíz con pseudo-fondo de malla vectorial y contenedor interno con padding perimetral.
- **Variantes**: Default (Dark Telemetry Grid).
- **Estados**: Default.
- **Props**: `maxWidth: string = '1440px'`, `showGridPattern: boolean = true`.
- **Código HTML**:
```html
<div class="c-app-shell">
  <div class="c-app-shell__inner">
    <!-- Contenido inyectado aquí -->
  </div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-app-shell {
  min-height: 100vh;
  width: 100%;
  background-color: var(--hg-surface-canvas);
  color: var(--hg-text-primary);
  font-family: var(--hg-font-family);
  background-image: 
    linear-gradient(to right, rgba(255,255,255,0.02) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(255,255,255,0.02) 1px, transparent 1px);
  background-size: 32px 32px;
  padding: 24px;
  box-sizing: border-box;
}
.c-app-shell__inner {
  max-width: 1440px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}
@media (min-width: 1024px) {
  .c-app-shell { padding: 32px; }
  .c-app-shell__inner { gap: 32px; }
}
```
- **Tokens consumidos**: `--hg-surface-canvas`, `--hg-text-primary`, `--hg-font-family`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Ajuste de padding perimetral en desktop.

---

### [02] Header / GlobalNavbar — Layout & Navegación

- **Descripción funcional**: Barra superior que distribuye la identidad de marca, navegación principal y herramientas de usuario en tres zonas simétricas.
- **Anatomía**: Contenedor flex con alineación horizontal y separación `justify-between`.
- **Variantes**: Fixed / In-flow.
- **Estados**: Default.
- **Props**: `activeRoute: string = 'Dashboard'`.
- **Código HTML**:
```html
<header class="c-global-navbar" role="banner">
  <div class="c-global-navbar__brand">
    <!-- BrandLogo [03] -->
  </div>
  <nav class="c-global-navbar__nav" aria-label="Navegación principal">
    <!-- NavPillSwitcher [04] -->
  </nav>
  <div class="c-global-navbar__actions">
    <!-- IconButtons [06] & UserAvatar [08] -->
  </div>
</header>
```
- **Estilos CSS encapsulados**:
```css
.c-global-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 64px;
  gap: 16px;
}
.c-global-navbar__brand,
.c-global-navbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.c-global-navbar__nav {
  display: flex;
  justify-content: center;
}
@media (max-width: 768px) {
  .c-global-navbar__nav { display: none; }
}
```
- **Tokens consumidos**: Ninguno directo (usa componentes hijos).
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Componentes [03], [04], [06], [08].
- **Responsive**: Se oculta la navegación central en dispositivos angostos (delegada a drawer/tabs).

---

### [03] BrandLogo / ShieldMark — Identidad / Branding

- **Descripción funcional**: Isotipo de escudo con rayo de alta energía y wordmark tipográfico de la solución.
- **Anatomía**: Envoltorio de isotipo SVG neón + contenedor de texto con dos tonos de color.
- **Variantes**: Con texto, Solo isotipo.
- **Estados**: Default, Hover, Focus.
- **Props**: `href: string = '/'`, `brandName: string = 'Helio Guard'`.
- **Código HTML**:
```html
<a href="#" class="c-brand-logo" aria-label="HelioGuard Home">
  <div class="c-brand-logo__mark" aria-hidden="true">
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
      <path d="M12 2L4 5V11C4 16.55 7.42 21.74 12 23C16.58 21.74 20 16.55 20 11V5L12 2Z" fill="#DFFF00"/>
      <path d="M12.5 7L8 13.5H12L11.5 17L16 10.5H12L12.5 7Z" fill="#0C1012"/>
    </svg>
  </div>
  <span class="c-brand-logo__text">
    <span class="c-brand-logo__primary">Helio</span>
    <span class="c-brand-logo__secondary">Guard</span>
  </span>
</a>
```
- **Estilos CSS encapsulados**:
```css
.c-brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  border-radius: var(--hg-radius-pill);
  transition: transform 0.15s ease;
}
.c-brand-logo:hover {
  transform: translateY(-1px);
}
.c-brand-logo__mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background-color: var(--hg-color-solar);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 16px var(--hg-color-solar-glow);
}
.c-brand-logo__text {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.02em;
}
.c-brand-logo__primary { color: var(--hg-text-primary); }
.c-brand-logo__secondary { color: var(--hg-text-primary); margin-left: 4px; }
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-text-primary`, `--hg-radius-pill`.
- **Comportamiento JS**: Enrutamiento al pulsar.
- **Dependencias**: Ícono SVG incrustado.
- **Responsive**: Puede ocultar el texto secundario en mobile.

---

### [04] NavPillSwitcher — Navegación / Tabs

- **Descripción funcional**: Contenedor estilo píldora oscuro que agrupa los accesos a las pantallas principales.
- **Anatomía**: Elemento `<ul>` con lista de items y borde sutil.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `items: NavItem[]`.
- **Código HTML**:
```html
<ul class="c-nav-pill-switcher" role="tablist">
  <!-- NavPillItem [05] -->
</ul>
```
- **Estilos CSS encapsulados**:
```css
.c-nav-pill-switcher {
  display: inline-flex;
  align-items: center;
  background-color: var(--hg-surface-card);
  border: 1px solid var(--hg-border-subtle);
  border-radius: var(--hg-radius-pill);
  padding: 4px;
  list-style: none;
  margin: 0;
  gap: 2px;
}
```
- **Tokens consumidos**: `--hg-surface-card`, `--hg-border-subtle`, `--hg-radius-pill`.
- **Comportamiento JS**: Gestión de selección y navegación con teclas de flecha (ArrowLeft/ArrowRight).
- **Dependencias**: Componente [05].
- **Responsive**: Display flex en desktop.

---

### [05] NavPillItem — Navegación / Controles

- **Descripción funcional**: Botón individual dentro del conmutador de navegación.
- **Anatomía**: Elemento `<li>` que contiene un `<button>` o `<a>`.
- **Variantes**: Active, Inactive.
- **Estados**: Default, Hover, Focus, Active.
- **Props**: `label: string`, `isActive: boolean = false`, `href?: string`.
- **Código HTML**:
```html
<!-- Variante Activa -->
<li role="presentation">
  <button class="c-nav-pill-item c-nav-pill-item--active" role="tab" aria-selected="true">
    Dashboard
  </button>
</li>

<!-- Variante Inactiva -->
<li role="presentation">
  <button class="c-nav-pill-item" role="tab" aria-selected="false">
    Solar Panels
  </button>
</li>
```
- **Estilos CSS encapsulados**:
```css
.c-nav-pill-item {
  background: transparent;
  border: none;
  color: var(--hg-text-secondary);
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 18px;
  border-radius: var(--hg-radius-pill);
  cursor: pointer;
  transition: all 0.15s ease;
  outline: none;
  white-space: nowrap;
}
.c-nav-pill-item:hover {
  color: var(--hg-text-primary);
}
.c-nav-pill-item:focus-visible {
  box-shadow: 0 0 0 2px var(--hg-color-solar);
}
.c-nav-pill-item--active {
  background-color: var(--hg-surface-pill-active);
  color: var(--hg-text-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}
```
- **Tokens consumidos**: `--hg-text-secondary`, `--hg-text-primary`, `--hg-surface-pill-active`, `--hg-radius-pill`, `--hg-color-solar`.
- **Comportamiento JS**: Dispara evento `selectTab` al hacer click o enter.
- **Dependencias**: Ninguna.
- **Responsive**: Ajuste de padding en pantallas compactas.

---

### [06] IconButton / ActionCircular — Botones / Acciones

- **Descripción funcional**: Botón de acción rápida circular para utilidades (búsqueda, notificaciones, settings).
- **Anatomía**: Botón circular con ícono SVG centrado y slot opcional para badge.
- **Variantes**: Simple, Con Badge de Notificación.
- **Estados**: Default, Hover, Focus, Active, Disabled.
- **Props**: `ariaLabel: string`, `hasNotification?: boolean`.
- **Código HTML**:
```html
<button class="c-icon-button" aria-label="Notificaciones">
  <svg class="c-icon-button__svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M18 8A6 6 0 0 0 6 8C6 15 3 17 3 17H21S18 15 18 8Z"></path>
    <path d="M13.73 21A2 2 0 0 1 10.27 21"></path>
  </svg>
  <span class="c-icon-button__badge" aria-hidden="true"></span>
</button>
```
- **Estilos CSS encapsulados**:
```css
.c-icon-button {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: var(--hg-radius-pill);
  background-color: var(--hg-surface-elevated);
  border: 1px solid var(--hg-border-subtle);
  color: var(--hg-text-secondary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  outline: none;
}
.c-icon-button:hover {
  background-color: var(--hg-surface-card-hover);
  color: var(--hg-text-primary);
  border-color: var(--hg-border-hover);
}
.c-icon-button:focus-visible {
  box-shadow: 0 0 0 2px var(--hg-color-solar);
}
.c-icon-button__badge {
  position: absolute;
  top: 9px;
  right: 9px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--hg-color-solar);
  box-shadow: 0 0 6px var(--hg-color-solar);
}
```
- **Tokens consumidos**: `--hg-surface-elevated`, `--hg-border-subtle`, `--hg-text-secondary`, `--hg-text-primary`, `--hg-color-solar`.
- **Comportamiento JS**: Emite click para abrir modal o popover.
- **Dependencias**: SVG de ícono.
- **Responsive**: Tamaño fijo de 40px accesible táctilmente.

---

### [07] NotificationDot — Elementos Decorativos / Feedback

- **Descripción funcional**: Indicador luminoso de alertas no leídas sobre elementos de interfaz.
- **Anatomía**: Pseudo-elemento o elemento `<span>` circular absoluto.
- **Variantes**: Solar neón, Ámbar, Verde mint.
- **Estados**: Static, Pulse.
- **Props**: `variant: 'solar' | 'amber' | 'mint' = 'solar'`.
- **Código HTML**:
```html
<span class="c-notification-dot c-notification-dot--solar" aria-hidden="true"></span>
```
- **Estilos CSS encapsulados**:
```css
.c-notification-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.c-notification-dot--solar {
  background-color: var(--hg-color-solar);
  box-shadow: 0 0 8px var(--hg-color-solar);
}
.c-notification-dot--mint {
  background-color: var(--hg-color-mint);
  box-shadow: 0 0 8px var(--hg-color-mint);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-mint`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fijo 8px.

---

### [08] UserAvatar — Identidad / Perfil

- **Descripción funcional**: Elemento de avatar fotográfico con halo de borde y punto de conectividad en vivo.
- **Anatomía**: Contenedor circular con imagen y dot de estado anidado.
- **Variantes**: Default.
- **Estados**: Default, Hover, Focus.
- **Props**: `src: string`, `alt: string`, `status: 'online' | 'offline' = 'online'`.
- **Código HTML**:
```html
<button class="c-user-avatar" aria-label="Abrir perfil de usuario">
  <img class="c-user-avatar__image" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop" alt="Foto de perfil de operador" />
  <span class="c-user-avatar__status" aria-hidden="true"></span>
</button>
```
- **Estilos CSS encapsulados**:
```css
.c-user-avatar {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid var(--hg-border-subtle);
  padding: 0;
  background: transparent;
  cursor: pointer;
  outline: none;
}
.c-user-avatar__image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}
.c-user-avatar__status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: var(--hg-color-mint);
  border: 2px solid var(--hg-surface-canvas);
}
```
- **Tokens consumidos**: `--hg-border-subtle`, `--hg-color-mint`, `--hg-surface-canvas`.
- **Comportamiento JS**: Dispara menú de usuario.
- **Dependencias**: Recurso de imagen.
- **Responsive**: Tamaño fijo.

---

### [09] GridDashboardLayout — Layout & Estructura

- **Descripción funcional**: Rejilla CSS principal de 3 columnas para orquestar la distribución proporcional de widgets del dashboard.
- **Anatomía**: Contenedor grid con filas implícitas y brechas consistentes.
- **Variantes**: 3 Columnas Desktop.
- **Estados**: Default.
- **Props**: Ninguna.
- **Código HTML**:
```html
<div class="c-dashboard-grid">
  <!-- Widgets hijos [10], [13] -->
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}
@media (min-width: 768px) {
  .c-dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (min-width: 1120px) {
  .c-dashboard-grid {
    grid-template-columns: 1.2fr 0.9fr 0.9fr;
    gap: 24px;
  }
}
```
- **Tokens consumidos**: Ninguno.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Contenedores hijos.
- **Responsive**: Colapsa a 1 columna en mobile, 2 en tablet y 3 en desktop amplio.

---

### [10] CardContainer — Layout & Estructura

- **Descripción funcional**: Tarjeta contenedora base oscura para métricas con esquinas redondeadas, borde translúcido y elevación.
- **Anatomía**: Caja semántica `<article>` con padding interno generoso y contorno.
- **Variantes**: Default, Hoverable.
- **Estados**: Default, Hover.
- **Props**: `padding?: string`.
- **Código HTML**:
```html
<article class="c-card-container">
  <!-- Contenido del widget -->
</article>
```
- **Estilos CSS encapsulados**:
```css
.c-card-container {
  background-color: var(--hg-surface-card);
  border: 1px solid var(--hg-border-subtle);
  border-radius: var(--hg-radius-card);
  padding: 24px;
  box-shadow: var(--hg-shadow-card);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s ease, transform 0.2s ease;
}
.c-card-container:hover {
  border-color: var(--hg-border-hover);
}
```
- **Tokens consumidos**: `--hg-surface-card`, `--hg-border-subtle`, `--hg-radius-card`, `--hg-shadow-card`, `--hg-border-hover`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Padding adaptativo (20px en mobile, 24px en desktop).

---

### [11] CardHeaderWithAction — Estructuras de Layout

- **Descripción funcional**: Cabecera para widgets con título descriptivo del indicador y botón de acceso a detalle.
- **Anatomía**: Flexbox horizontal con título a la izquierda y botón de flecha diagonal a la derecha.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `title: string`, `actionAriaLabel: string`.
- **Código HTML**:
```html
<header class="c-card-header">
  <h2 class="c-card-header__title">Estimated Full</h2>
  <button class="c-card-header__action" aria-label="Ver detalles de Estimated Full">
    <!-- DiagonalArrowIcon [12] -->
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
      <path d="M7 17L17 7M17 7H7M17 7V17"/>
    </svg>
  </button>
</header>
```
- **Estilos CSS encapsulados**:
```css
.c-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
}
.c-card-header__title {
  font-size: 14px;
  font-weight: 500;
  color: var(--hg-text-primary);
  margin: 0;
  letter-spacing: 0.01em;
}
.c-card-header__action {
  background: transparent;
  border: none;
  color: var(--hg-text-muted);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
}
.c-card-header__action:hover {
  color: var(--hg-text-primary);
  background-color: var(--hg-surface-elevated);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-muted`, `--hg-surface-elevated`.
- **Comportamiento JS**: Click redirige al reporte correspondiente.
- **Dependencias**: Componente [12].
- **Responsive**: Flex standard.

---

### [12] DiagonalArrowIcon — Elementos Decorativos / Iconos

- **Descripción funcional**: Ícono minimalista de flecha hacia arriba y a la derecha (`↗`) que denota interactividad hacia vista expandida.
- **Anatomía**: SVG stroke monocromo.
- **Variantes**: 14px, 16px.
- **Estados**: Default, Hover.
- **Props**: `size: number = 14`.
- **Código HTML**:
```html
<svg class="c-diagonal-arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
  <path d="M7 17L17 7M17 7H7M17 7V17" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
```
- **Estilos CSS encapsulados**:
```css
.c-diagonal-arrow-icon {
  display: inline-block;
  transition: transform 0.15s ease;
}
.c-card-header__action:hover .c-diagonal-arrow-icon {
  transform: translate(1px, -1px);
}
```
- **Tokens consumidos**: Hereda `currentColor`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Escala vectorial.

---

### [13] HeroWelcomeCard — Layout / Contenedores

- **Descripción funcional**: Tarjeta principal de saludo con fondo texturizado de silicio fotovoltaico, titular prominente y botones de telemetría instantánea.
- **Anatomía**: Contenedor con background vectorial, titular en dos líneas, subtítulo descriptivo y barra inferior de accesos rápidos.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `userName: string = 'HelioGuard'`.
- **Código HTML**:
```html
<section class="c-hero-welcome-card" aria-labelledby="hero-title">
  <div class="c-hero-welcome-card__content">
    <h1 id="hero-title" class="c-hero-welcome-card__title">
      Welcome in,<br><span class="c-hero-welcome-card__brand">HelioGuard.</span>
    </h1>
    <p class="c-hero-welcome-card__desc">
      Monitor solar energy. Reduce costs. Power future.
    </p>
  </div>
  <div class="c-hero-welcome-card__actions">
    <!-- StatusPillButtonGroup [16] -->
  </div>
</section>
```
- **Estilos CSS encapsulados**:
```css
.c-hero-welcome-card {
  background-color: var(--hg-surface-card);
  border: 1px solid var(--hg-border-subtle);
  border-radius: var(--hg-radius-card);
  padding: 32px 28px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 280px;
  position: relative;
  background-image: radial-gradient(circle at 10% 20%, rgba(223, 255, 0, 0.04) 0%, transparent 60%);
}
.c-hero-welcome-card__title {
  font-size: 38px;
  font-weight: 700;
  line-height: 1.1;
  color: var(--hg-text-primary);
  margin: 0 0 12px 0;
  letter-spacing: -0.03em;
}
.c-hero-welcome-card__brand {
  color: var(--hg-text-primary);
}
.c-hero-welcome-card__desc {
  font-size: 14px;
  color: var(--hg-text-secondary);
  margin: 0;
  max-width: 300px;
  line-height: 1.5;
}
.c-hero-welcome-card__actions {
  margin-top: 32px;
}
```
- **Tokens consumidos**: `--hg-surface-card`, `--hg-border-subtle`, `--hg-radius-card`, `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Componentes [14], [15], [16].
- **Responsive**: Titular ajusta a 30px en mobile.

---

### [14] DisplayHeading — Tipografía / Encabezados

- **Descripción funcional**: Encabezado visual de máxima jerarquía con soporte de acento tipográfico.
- **Anatomía**: Etiqueta `<h1>` con tracking condensado y peso bold.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `textLine1: string`, `textLine2: string`.
- **Código HTML**:
```html
<h1 class="c-display-heading">
  Welcome in,<br><span class="c-display-heading__accent">HelioGuard.</span>
</h1>
```
- **Estilos CSS encapsulados**:
```css
.c-display-heading {
  font-size: 40px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.025em;
  color: var(--hg-text-primary);
  margin: 0;
}
.c-display-heading__accent {
  color: var(--hg-text-primary);
}
```
- **Tokens consumidos**: `--hg-text-primary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Reducción fluida con `clamp(28px, 4vw, 40px)`.

---

### [15] BodyTextSubhead — Tipografía / Textos

- **Descripción funcional**: Párrafo de lectura de acompañamiento con legibilidad contrastada.
- **Anatomía**: Etiqueta `<p>`.
- **Variantes**: Default, Muted.
- **Estados**: Default.
- **Props**: `content: string`.
- **Código HTML**:
```html
<p class="c-body-subhead">
  Monitor solar energy. Reduce costs. Power future.
</p>
```
- **Estilos CSS encapsulados**:
```css
.c-body-subhead {
  font-size: 14px;
  line-height: 1.5;
  color: var(--hg-text-secondary);
  margin: 0;
}
```
- **Tokens consumidos**: `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fuente fija 14px.

---

### [16] StatusPillButtonGroup — Botones / Grupos

- **Descripción funcional**: Agrupador horizontal para los botones circulares de telemetría del hero.
- **Anatomía**: Contenedor flex con brecha de 12px.
- **Variantes**: Inline group.
- **Estados**: Default.
- **Props**: Ninguna.
- **Código HTML**:
```html
<div class="c-status-pill-group" role="toolbar" aria-label="Accesos rápidos de telemetría">
  <!-- TelemetryIconButton [17] x 3 -->
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-status-pill-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
```
- **Tokens consumidos**: Ninguno.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Componente [17].
- **Responsive**: Envuelve si el ancho es inferior a 240px.

---

### [17] TelemetryIconButton — Botones / Acciones

- **Descripción funcional**: Botón táctil circular para alternar modos de sensor o ver eventos.
- **Anatomía**: Elemento `<button>` circular con ícono vectorial SVG.
- **Variantes**: 
  - `c-telemetry-btn--solar`: Activo, fondo solar neón `#dfff00`.
  - `c-telemetry-btn--neutral`: Inactivo oscuro con borde translúcido.
  - `c-telemetry-btn--danger`: Tintado rojizo para anomalías/alertas.
- **Estados**: Default, Hover, Active, Focus.
- **Props**: `variant: 'solar' | 'neutral' | 'danger'`, `ariaLabel: string`.
- **Código HTML**:
```html
<!-- Variante Solar Activa -->
<button class="c-telemetry-btn c-telemetry-btn--solar" aria-label="Telemetría de radiación activa">
  <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
    <path d="M13 2L3 14H12L11 22L21 10H12L13 2Z"/>
  </svg>
</button>

<!-- Variante Neutral Inactiva -->
<button class="c-telemetry-btn c-telemetry-btn--neutral" aria-label="Historial de sensores">
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
  </svg>
</button>

<!-- Variante Alerta -->
<button class="c-telemetry-btn c-telemetry-btn--danger" aria-label="Alerta en inversor">
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
    <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
  </svg>
</button>
```
- **Estilos CSS encapsulados**:
```css
.c-telemetry-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}
.c-telemetry-btn--solar {
  background-color: var(--hg-color-solar);
  color: #0c1012;
  box-shadow: 0 0 16px var(--hg-color-solar-glow);
}
.c-telemetry-btn--solar:hover {
  transform: scale(1.06);
}
.c-telemetry-btn--neutral {
  background-color: var(--hg-surface-elevated);
  border: 1px solid var(--hg-border-subtle);
  color: var(--hg-text-secondary);
}
.c-telemetry-btn--neutral:hover {
  color: var(--hg-text-primary);
  border-color: var(--hg-border-hover);
}
.c-telemetry-btn--danger {
  background-color: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
}
.c-telemetry-btn--danger:hover {
  background-color: rgba(239, 68, 68, 0.2);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-surface-elevated`, `--hg-border-subtle`, `--hg-text-secondary`.
- **Comportamiento JS**: Emite selección del filtro de telemetría.
- **Dependencias**: Íconos SVG.
- **Responsive**: Diámetro constante de 44px (touch friendly).

---

### [18] MetricTimeDisplay — Tipografía / KPIs

- **Descripción funcional**: Visualizador numérico para periodos de tiempo con unidades de hora y minuto diferenciadas.
- **Anatomía**: Grupo flex horizontal con números destacados y sufijos de texto en gris claro.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `hours: number = 1`, `minutes: number = 34`.
- **Código HTML**:
```html
<div class="c-metric-time" aria-label="Tiempo estimado: 1 hora y 34 minutos">
  <span class="c-metric-time__val">1</span>
  <span class="c-metric-time__unit">hr</span>
  <span class="c-metric-time__val">34</span>
  <span class="c-metric-time__unit">mm</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-metric-time {
  display: inline-flex;
  align-items: baseline;
  gap: 6px;
  font-variant-numeric: tabular-nums;
  margin: 12px 0 24px 0;
}
.c-metric-time__val {
  font-size: 38px;
  font-weight: 700;
  color: var(--hg-text-primary);
  line-height: 1;
}
.c-metric-time__unit {
  font-size: 14px;
  font-weight: 500;
  color: var(--hg-text-secondary);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Escalado natural.

---

### [19] EqualizerBarChart — Elementos Visuales / Gráficos

- **Descripción funcional**: Gráfico ecualizador de 18 barras verticales que representa la velocidad de absorción y carga en el tiempo.
- **Anatomía**: Contenedor flex con alineación inferior y barras de alturas y colores programados.
- **Variantes**: Default.
- **Estados**: Default, Animated.
- **Props**: `bars: { heightPct: number, isActive: boolean }[]`.
- **Código HTML**:
```html
<div class="c-equalizer-chart" aria-label="Histograma de carga de batería" role="img">
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 35%"></span>
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 55%"></span>
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 70%"></span>
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 40%"></span>
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 85%"></span>
  <span class="c-equalizer-chart__bar c-equalizer-chart__bar--active" style="height: 60%"></span>
  <span class="c-equalizer-chart__bar" style="height: 45%"></span>
  <span class="c-equalizer-chart__bar" style="height: 65%"></span>
  <span class="c-equalizer-chart__bar" style="height: 80%"></span>
  <span class="c-equalizer-chart__bar" style="height: 50%"></span>
  <span class="c-equalizer-chart__bar" style="height: 90%"></span>
  <span class="c-equalizer-chart__bar" style="height: 75%"></span>
  <span class="c-equalizer-chart__bar" style="height: 40%"></span>
  <span class="c-equalizer-chart__bar" style="height: 60%"></span>
  <span class="c-equalizer-chart__bar" style="height: 30%"></span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-equalizer-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 90px;
  width: 100%;
  gap: 4px;
}
.c-equalizer-chart__bar {
  flex: 1;
  background-color: #272d32;
  border-radius: var(--hg-radius-pill);
  transition: height 0.3s ease;
}
.c-equalizer-chart__bar--active {
  background-color: var(--hg-color-solar);
  box-shadow: 0 0 8px var(--hg-color-solar-glow);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-radius-pill`.
- **Comportamiento JS**: Animación de altura en tiempo real según telemetría del inversor.
- **Dependencias**: Ninguna.
- **Responsive**: Ajuste de ancho de barra proporcional al contenedor.

---

### [20] RadialProgressGauge — Elementos Visuales / Gráficos

- **Descripción funcional**: Indicador radial circular segmentado con arco neón continuo para porcentaje de reserva en acumulador litio-hierro.
- **Anatomía**: SVG con círculo de fondo estriado (ticks) y arco de trazado neón con filtro de resplandor.
- **Variantes**: Default (78% de carga).
- **Estados**: Charging, Full, Discharging.
- **Props**: `percentage: number = 78`, `strokeWidth: number = 8`.
- **Código HTML**:
```html
<div class="c-radial-gauge" role="progressbar" aria-valuenow="78" aria-valuemin="0" aria-valuemax="100">
  <svg class="c-radial-gauge__svg" viewBox="0 0 160 160">
    <!-- Pista base punteada / ticks -->
    <circle class="c-radial-gauge__track" cx="80" cy="80" r="64" stroke-dasharray="4 6"/>
    <!-- Arco activo con brillo -->
    <circle class="c-radial-gauge__meter" cx="80" cy="80" r="64" 
            stroke-dasharray="402" 
            stroke-dashoffset="88"/>
  </svg>
  <div class="c-radial-gauge__body">
    <!-- RadialMetricLabel [21] -->
    <span class="c-radial-gauge__pct">78%</span>
    <span class="c-radial-gauge__status">Charging...</span>
  </div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-radial-gauge {
  position: relative;
  width: 160px;
  height: 160px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.c-radial-gauge__svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}
.c-radial-gauge__track {
  fill: none;
  stroke: #252b2f;
  stroke-width: 7;
}
.c-radial-gauge__meter {
  fill: none;
  stroke: var(--hg-color-solar);
  stroke-width: 8;
  stroke-linecap: round;
  filter: drop-shadow(0 0 12px var(--hg-color-solar-glow));
  transition: stroke-dashoffset 0.8s ease-in-out;
}
.c-radial-gauge__body {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}
.c-radial-gauge__pct {
  font-size: 34px;
  font-weight: 700;
  color: var(--hg-text-primary);
  line-height: 1;
}
.c-radial-gauge__status {
  font-size: 13px;
  color: var(--hg-color-solar);
  margin-top: 4px;
  font-weight: 500;
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-text-primary`.
- **Comportamiento JS**: Cálculo de `stroke-dashoffset` reactivo a partir del porcentaje.
- **Dependencias**: Componente [21].
- **Responsive**: Escala SVG de 140px a 180px según el ancho del card.

---

### [21] RadialMetricLabel — Tipografía / Feedback

- **Descripción funcional**: Bloque tipográfico central dentro del velocímetro/anillo de carga.
- **Anatomía**: Cifra destacada en blanco con leyenda de estado coloreada en solar neón.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `percent: number`, `statusText: string`.
- **Código HTML**:
```html
<div class="c-radial-metric-label">
  <span class="c-radial-metric-label__value">78%</span>
  <span class="c-radial-metric-label__caption">Charging...</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-radial-metric-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.1;
}
.c-radial-metric-label__value {
  font-size: 36px;
  font-weight: 700;
  color: var(--hg-text-primary);
}
.c-radial-metric-label__caption {
  font-size: 13px;
  font-weight: 500;
  color: var(--hg-color-solar);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-color-solar`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fijo en centro radial.

---

### [22] SubcardFooterLabel — Tipografía / Leyendas

- **Descripción funcional**: Nota técnica en mayúsculas al pie del widget de batería que detalla la química de la celda y capacidad de almacenamiento.
- **Anatomía**: Elemento `<div>` centrado horizontalmente con tracking amplio.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `text: string = 'LITHIUM-IRON BUFFER (14.2 KWH)'`.
- **Código HTML**:
```html
<div class="c-subcard-footer-label">
  LITHIUM-IRON BUFFER (14.2 KWH)
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-subcard-footer-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--hg-text-secondary);
  text-align: center;
  margin-top: 20px;
}
```
- **Tokens consumidos**: `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Centrado continuo.

---

### [23] MetricTotalKpi — Tipografía / KPIs

- **Descripción funcional**: Despliegue de gran formato para la métrica acumulada de energía cosechada.
- **Anatomía**: Cifra con separador de miles y unidad en minúsculas.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `value: string = '1,248'`, `unit: string = 'kwh'`.
- **Código HTML**:
```html
<div class="c-metric-total-kpi">
  <span class="c-metric-total-kpi__number">1,248</span>
  <span class="c-metric-total-kpi__unit">kwh</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-metric-total-kpi {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin: 12px 0 20px 0;
}
.c-metric-total-kpi__number {
  font-size: 40px;
  font-weight: 700;
  color: var(--hg-text-primary);
  line-height: 1;
}
.c-metric-total-kpi__unit {
  font-size: 15px;
  font-weight: 500;
  color: var(--hg-text-secondary);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Auto-ajuste de tamaño.

---

### [24] StripedProgressBar — Elementos Visuales / Progresos

- **Descripción funcional**: Barra de progreso horizontal con tramo activo continuo neón y tramo inactivo texturizado con rayado en diagonal (striped pattern).
- **Anatomía**: Contenedor track con dos secciones internas adyacentes.
- **Variantes**: Default (70% solar activo).
- **Estados**: Default.
- **Props**: `percentage: number = 70`.
- **Código HTML**:
```html
<div class="c-striped-progress" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100">
  <div class="c-striped-progress__filled" style="width: 70%;"></div>
  <div class="c-striped-progress__track" style="width: 30%;"></div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-striped-progress {
  display: flex;
  width: 100%;
  height: 14px;
  border-radius: var(--hg-radius-pill);
  overflow: hidden;
  background-color: var(--hg-surface-elevated);
  gap: 2px;
}
.c-striped-progress__filled {
  height: 100%;
  background-color: var(--hg-color-solar);
  box-shadow: 0 0 10px var(--hg-color-solar-glow);
  border-radius: var(--hg-radius-pill) 0 0 var(--hg-radius-pill);
}
.c-striped-progress__track {
  height: 100%;
  background-color: #1e262b;
  background-image: repeating-linear-gradient(
    -45deg,
    rgba(255, 255, 255, 0.08),
    rgba(255, 255, 255, 0.08) 4px,
    transparent 4px,
    transparent 8px
  );
  border-radius: 0 var(--hg-radius-pill) var(--hg-radius-pill) 0;
}
```
- **Tokens consumidos**: `--hg-surface-elevated`, `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-radius-pill`.
- **Comportamiento JS**: Vinculación dinámica del ancho porcentual.
- **Dependencias**: Ninguna.
- **Responsive**: 100% de ancho disponible.

---

### [25] ProgressMetaLabel — Tipografía / Leyendas

- **Descripción funcional**: Fila con etiqueta de marco temporal (*Today*) y porcentaje completado (*70%*).
- **Anatomía**: Flexbox horizontal `justify-between`.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `label: string = 'Today'`, `percentText: string = '70%'`.
- **Código HTML**:
```html
<div class="c-progress-meta">
  <span class="c-progress-meta__label">Today</span>
  <span class="c-progress-meta__val">70%</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-progress-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
}
.c-progress-meta__label {
  color: var(--hg-text-secondary);
}
.c-progress-meta__val {
  color: var(--hg-color-solar);
  font-weight: 700;
}
```
- **Tokens consumidos**: `--hg-text-secondary`, `--hg-color-solar`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Flex simple.

---

### [26] BiphaseLoadDisplay — Elementos Visuales / Telemetría

- **Descripción funcional**: Distribución en columnas simétricas para monitorear el balance de las dos fases del circuito solar (Left Side vs Right Side).
- **Anatomía**: Contenedor grid de 2 columnas con métricas numéricas y barras espectrales segmentadas.
- **Variantes**: Default (62% vs 42%).
- **Estados**: Balanced, Unbalanced.
- **Props**: `leftPct: number = 62`, `rightPct: number = 42`.
- **Código HTML**:
```html
<div class="c-biphase-load">
  <div class="c-biphase-load__phase">
    <div class="c-biphase-load__meta">
      <span class="c-biphase-load__pct">62%</span>
      <span class="c-biphase-load__name">Left Side</span>
    </div>
    <!-- SegmentedSpectralBar [27] -->
    <div class="c-segmented-spectral-bar">
      <span class="c-segmented-spectral-bar__seg is-mint"></span>
      <span class="c-segmented-spectral-bar__seg is-mint"></span>
      <span class="c-segmented-spectral-bar__seg is-solar"></span>
      <span class="c-segmented-spectral-bar__seg is-solar"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
    </div>
  </div>

  <div class="c-biphase-load__phase">
    <div class="c-biphase-load__meta">
      <span class="c-biphase-load__pct">42%</span>
      <span class="c-biphase-load__name">Right Side</span>
    </div>
    <!-- SegmentedSpectralBar [27] -->
    <div class="c-segmented-spectral-bar">
      <span class="c-segmented-spectral-bar__seg is-mint"></span>
      <span class="c-segmented-spectral-bar__seg is-solar"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
      <span class="c-segmented-spectral-bar__seg is-off"></span>
    </div>
  </div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-biphase-load {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 8px;
}
.c-biphase-load__phase {
  display: flex;
  flex-direction: column;
}
.c-biphase-load__pct {
  font-size: 32px;
  font-weight: 700;
  color: var(--hg-text-primary);
  line-height: 1;
}
.c-biphase-load__name {
  font-size: 13px;
  color: var(--hg-text-secondary);
  margin-top: 4px;
  margin-bottom: 12px;
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Cálculo de equilibrio entre fases.
- **Dependencias**: Componente [27].
- **Responsive**: 2 columnas consistentes.

---

### [27] SegmentedSpectralBar — Elementos Visuales / Gráficos

- **Descripción funcional**: Barra ecualizadora micrométrica formada por pequeños segmentos verticales que cambian de tonalidad (verde -> amarillo -> inactivo).
- **Anatomía**: Contenedor flex horizontal con pastillas de 3px x 10px.
- **Variantes**: Con segmentos activos e inactivos.
- **Estados**: Default.
- **Props**: `segments: ('mint' | 'solar' | 'off')[]`.
- **Código HTML**:
```html
<div class="c-segmented-spectral-bar" aria-hidden="true">
  <span class="c-segmented-spectral-bar__seg is-mint"></span>
  <span class="c-segmented-spectral-bar__seg is-mint"></span>
  <span class="c-segmented-spectral-bar__seg is-solar"></span>
  <span class="c-segmented-spectral-bar__seg is-off"></span>
  <span class="c-segmented-spectral-bar__seg is-off"></span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-segmented-spectral-bar {
  display: flex;
  align-items: center;
  gap: 3px;
  height: 12px;
  background-color: rgba(255, 255, 255, 0.03);
  padding: 2px 4px;
  border-radius: 4px;
  width: fit-content;
}
.c-segmented-spectral-bar__seg {
  width: 4px;
  height: 8px;
  border-radius: 1px;
}
.c-segmented-spectral-bar__seg.is-mint {
  background-color: var(--hg-color-mint);
}
.c-segmented-spectral-bar__seg.is-solar {
  background-color: var(--hg-color-solar);
}
.c-segmented-spectral-bar__seg.is-off {
  background-color: #262c30;
}
```
- **Tokens consumidos**: `--hg-color-mint`, `--hg-color-solar`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Se ajusta al ancho del card.

---

### [28] DropdownPillSelect — Inputs y Controles / Select

- **Descripción funcional**: Selector desplegable tipo botón en píldora para filtrar el horizonte temporal del histórico (ej. *6 months*).
- **Anatomía**: Botón con texto centrado, ícono de chevron hacia abajo y menú popover asociado.
- **Variantes**: Default, Expanded.
- **Estados**: Default, Hover, Focus, Open.
- **Props**: `selectedOption: string = '6 months'`, `options: string[]`.
- **Código HTML**:
```html
<div class="c-dropdown-pill">
  <button class="c-dropdown-pill__btn" aria-haspopup="listbox" aria-expanded="false">
    <span>6 months</span>
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M6 9l6 6 6-6"/>
    </svg>
  </button>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-dropdown-pill {
  position: relative;
  display: inline-block;
}
.c-dropdown-pill__btn {
  background-color: var(--hg-surface-elevated);
  border: 1px solid var(--hg-border-subtle);
  border-radius: var(--hg-radius-pill);
  color: var(--hg-text-secondary);
  font-size: 12px;
  font-weight: 500;
  padding: 6px 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
  transition: all 0.15s ease;
}
.c-dropdown-pill__btn:hover {
  background-color: var(--hg-surface-card-hover);
  color: var(--hg-text-primary);
  border-color: var(--hg-border-hover);
}
.c-dropdown-pill__btn:focus-visible {
  box-shadow: 0 0 0 2px var(--hg-color-solar);
}
```
- **Tokens consumidos**: `--hg-surface-elevated`, `--hg-border-subtle`, `--hg-text-secondary`, `--hg-text-primary`, `--hg-radius-pill`.
- **Comportamiento JS**: Alterna estado `aria-expanded` y despliega listbox flotante con soporte de teclas Escape y flechas.
- **Dependencias**: Ninguna.
- **Responsive**: Compacto en cualquier ancho.

---

### [29] MonthlyBarChart — Elementos Visuales / Gráficos

- **Descripción funcional**: Gráfico de barras de alta densidad para comparar la generación energética mes a mes de Abril a Septiembre.
- **Anatomía**: Área de dibujo con líneas guía horizontales punteadas, barras verticales delgadas neón y eje X con nombres de meses.
- **Variantes**: Default.
- **Estados**: Default, Hover sobre barra individual (tooltip).
- **Props**: `months: string[] = ['Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep']`, `barData: number[]`.
- **Código HTML**:
```html
<div class="c-monthly-chart" role="region" aria-label="Gráfico de generación energética mensual">
  <div class="c-monthly-chart__plot">
    <div class="c-monthly-chart__gridlines">
      <span class="c-monthly-chart__line"></span>
      <span class="c-monthly-chart__line"></span>
      <span class="c-monthly-chart__line"></span>
    </div>
    <div class="c-monthly-chart__bars">
      <!-- Muestra representativa de barras -->
      <div class="c-monthly-chart__bar" style="height: 38%;"></div>
      <div class="c-monthly-chart__bar" style="height: 52%;"></div>
      <div class="c-monthly-chart__bar" style="height: 74%;"></div>
      <div class="c-monthly-chart__bar" style="height: 44%;"></div>
      <div class="c-monthly-chart__bar" style="height: 82%;"></div>
      <div class="c-monthly-chart__bar" style="height: 60%;"></div>
      <div class="c-monthly-chart__bar" style="height: 90%;"></div>
      <div class="c-monthly-chart__bar" style="height: 48%;"></div>
      <div class="c-monthly-chart__bar" style="height: 65%;"></div>
      <div class="c-monthly-chart__bar" style="height: 55%;"></div>
    </div>
  </div>
  <div class="c-monthly-chart__labels">
    <span>Apr</span><span>May</span><span>Jun</span><span>Jul</span><span>Aug</span><span>Sep</span>
  </div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-monthly-chart {
  width: 100%;
  margin: 16px 0;
}
.c-monthly-chart__plot {
  position: relative;
  height: 110px;
  width: 100%;
  display: flex;
  align-items: flex-end;
}
.c-monthly-chart__gridlines {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  pointer-events: none;
}
.c-monthly-chart__line {
  border-bottom: 1px dashed rgba(255, 255, 255, 0.05);
  width: 100%;
}
.c-monthly-chart__bars {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 4px;
}
.c-monthly-chart__bar {
  flex: 1;
  background-color: var(--hg-color-solar);
  border-radius: 2px 2px 0 0;
  transition: opacity 0.15s ease;
}
.c-monthly-chart__bar:hover {
  opacity: 0.8;
}
.c-monthly-chart__labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 11px;
  color: var(--hg-text-muted);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-text-muted`.
- **Comportamiento JS**: Tooltip flotante con valor en kWh al hacer hover/focus en cada barra.
- **Dependencias**: Ninguna.
- **Responsive**: Ajuste de separación automático con `justify-content: space-between`.

---

### [30] FinancialYieldBadge — Tipografía / Badges

- **Descripción funcional**: Muestra del valor financiero cosechado con indicador de tendencia porcentual.
- **Anatomía**: Cifra en blanco con formato monetario y badge en pastilla con delta negativo/positivo.
- **Variantes**: Negativo (`-1.5%`), Positivo (`+2.4%`).
- **Estados**: Default.
- **Props**: `amount: string = '$20,678.89'`, `delta: string = '-1.5%'`, `isPositive: boolean = false`.
- **Código HTML**:
```html
<div class="c-financial-yield">
  <span class="c-financial-yield__val">$20,678.89</span>
  <span class="c-financial-yield__badge c-financial-yield__badge--negative">
    <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
      <path d="M12 5v14M19 12l-7 7-7-7"/>
    </svg>
    -1.5%
  </span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-financial-yield {
  display: flex;
  align-items: center;
  gap: 10px;
}
.c-financial-yield__val {
  font-size: 20px;
  font-weight: 700;
  color: var(--hg-text-primary);
  font-variant-numeric: tabular-nums;
}
.c-financial-yield__badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: var(--hg-radius-pill);
}
.c-financial-yield__badge--negative {
  background-color: rgba(0, 229, 153, 0.12);
  color: var(--hg-color-mint);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-color-mint`, `--hg-radius-pill`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ícono SVG flecha.
- **Responsive**: Envoltura flex en viewport estrecho.

---

### [31] ChartLegendIndicator — Elementos Decorativos / Leyendas

- **Descripción funcional**: Leyenda técnica que asocia el color solar neón con la métrica *Solar Yield*.
- **Anatomía**: Pequeño dot circular neón seguido de texto descriptor.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `label: string = 'Solar Yield'`, `color: string = '#dfff00'`.
- **Código HTML**:
```html
<div class="c-chart-legend">
  <span class="c-chart-legend__dot" aria-hidden="true"></span>
  <span class="c-chart-legend__text">Solar Yield</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-chart-legend {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--hg-text-secondary);
}
.c-chart-legend__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--hg-color-solar);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fijo.

---

### [32] IconBadgeContainer — Elementos Decorativos / Contenedores

- **Descripción funcional**: Caja cuadrada con bordes suaves que contiene íconos de utilidades (billetera, sol, etc.) con iluminación de fondo suave.
- **Anatomía**: Contenedor `<div>` de 44x44px con fondo semitransparente tintado de amarillo o verde.
- **Variantes**: Solar, Mint.
- **Estados**: Default.
- **Props**: `variant: 'solar' | 'mint' = 'solar'`.
- **Código HTML**:
```html
<div class="c-icon-badge c-icon-badge--solar" aria-hidden="true">
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <rect x="2" y="6" width="20" height="12" rx="2"></rect>
    <path d="M16 12h.01M2 10h20"></path>
  </svg>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-icon-badge {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.c-icon-badge--solar {
  background-color: rgba(223, 255, 0, 0.1);
  color: var(--hg-color-solar);
}
.c-icon-badge--mint {
  background-color: rgba(0, 229, 153, 0.1);
  color: var(--hg-color-mint);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-mint`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Íconos SVG.
- **Responsive**: Tamaño constante.

---

### [33] FinancialMetricDisplay — Tipografía / KPIs

- **Descripción funcional**: Visualizador del ingreso acumulado diario con código monetario en mayúsculas (*58.60 AUD*).
- **Anatomía**: Cifra destacada en blanco con código AUD al costado.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `amount: string = '58.60'`, `currency: string = 'AUD'`.
- **Código HTML**:
```html
<div class="c-financial-metric">
  <span class="c-financial-metric__amount">58.60</span>
  <span class="c-financial-metric__currency">AUD</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-financial-metric {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin: 8px 0;
}
.c-financial-metric__amount {
  font-size: 34px;
  font-weight: 700;
  color: var(--hg-text-primary);
  line-height: 1;
}
.c-financial-metric__currency {
  font-size: 14px;
  font-weight: 600;
  color: var(--hg-text-secondary);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Ajuste automático.

---

### [34] SubmetricStatusIndicator — Tipografía / Feedback

- **Descripción funcional**: Indicador de estado en vivo con punto verde esmeralda y texto explicativo (*• Solar irradiance: High*).
- **Anatomía**: Pequeño dot circular integrado con etiqueta de texto.
- **Variantes**: High, Optimal, Warning.
- **Estados**: Active.
- **Props**: `statusText: string = 'Solar irradiance: High'`.
- **Código HTML**:
```html
<div class="c-submetric-status">
  <span class="c-submetric-status__dot" aria-hidden="true"></span>
  <span class="c-submetric-status__text">Solar irradiance: High</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-submetric-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--hg-color-mint);
  font-weight: 500;
}
.c-submetric-status__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--hg-color-mint);
  box-shadow: 0 0 6px var(--hg-color-mint);
}
```
- **Tokens consumidos**: `--hg-color-mint`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fijo en 12px.

---

### [35] WeatherMetricDisplay — Tipografía / KPIs

- **Descripción funcional**: Despliegue de temperatura ambiente y estado climático en el sitio del parque solar (*Sunny 32.c*).
- **Anatomía**: Texto de una línea en alto contraste.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `condition: string = 'Sunny'`, `temperature: string = '32.c'`.
- **Código HTML**:
```html
<div class="c-weather-metric">
  <span class="c-weather-metric__text">Sunny 32.c</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-weather-metric {
  font-size: 32px;
  font-weight: 700;
  color: var(--hg-text-primary);
  margin: 8px 0;
  line-height: 1;
}
```
- **Tokens consumidos**: `--hg-text-primary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Fuente fluida.

---

### [36] AuxiliaryInfoRow — Tipografía / Textos

- **Descripción funcional**: Fila de microtexto técnico ubicada en el borde inferior de las tarjetas de Earning y Weather.
- **Anatomía**: Flexbox horizontal con información a los dos extremos.
- **Variantes**: Single text, Double text.
- **Estados**: Default.
- **Props**: `leftText: string`, `rightText?: string`.
- **Código HTML**:
```html
<div class="c-aux-info-row">
  <span class="c-aux-info-row__item">UV Index: 9 (Extreme)</span>
  <span class="c-aux-info-row__item">Zero cloud coverage</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-aux-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-top: 16px;
  font-size: 11px;
  color: var(--hg-text-muted);
}
```
- **Tokens consumidos**: `--hg-text-muted`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Puede apilarse si el ancho es menor a 200px.

---

### [37] TripartiteFlowBar — Elementos Visuales / Diagramas

- **Descripción funcional**: Barra continua de flujo energético dividida en tres secciones interconectadas: Generación Solar (amarillo neón), Consumo Hogar (verde menta) e Inyección a Red (gris azulado).
- **Anatomía**: Contenedor flex horizontal de 28px de alto con pastillas redondeadas contiguas.
- **Variantes**: Proporciones variables según potencias activas.
- **Estados**: Default.
- **Props**: `solarKw: number = 5.6`, `homeKw: number = 4.8`, `gridKw: number = 1.2`.
- **Código HTML**:
```html
<div class="c-tripartite-flow" role="meter" aria-label="Distribución de flujo de energía" aria-valuenow="100">
  <div class="c-tripartite-flow__segment is-solar" style="flex: 5.6;"></div>
  <div class="c-tripartite-flow__segment is-home" style="flex: 4.8;"></div>
  <div class="c-tripartite-flow__segment is-grid" style="flex: 1.2;"></div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-tripartite-flow {
  display: flex;
  align-items: center;
  height: 28px;
  width: 100%;
  gap: 6px;
  margin: 16px 0;
}
.c-tripartite-flow__segment {
  height: 100%;
  border-radius: var(--hg-radius-pill);
  transition: flex 0.4s ease;
}
.c-tripartite-flow__segment.is-solar {
  background-color: var(--hg-color-solar);
  box-shadow: 0 0 12px var(--hg-color-solar-glow);
}
.c-tripartite-flow__segment.is-home {
  background-color: var(--hg-color-mint);
  box-shadow: 0 0 12px var(--hg-color-mint-glow);
}
.c-tripartite-flow__segment.is-grid {
  background-color: var(--hg-color-grid-neutral);
}
```
- **Tokens consumidos**: `--hg-color-solar`, `--hg-color-solar-glow`, `--hg-color-mint`, `--hg-color-mint-glow`, `--hg-color-grid-neutral`, `--hg-radius-pill`.
- **Comportamiento JS**: Cálculo reactivo del valor de `flex` para cada segmento.
- **Dependencias**: Ninguna.
- **Responsive**: 100% de ancho adaptativo.

---

### [38] FlowMetricLegendGroup — Tipografía / Métricas

- **Descripción funcional**: Fila con 3 columnas de telemetría que cuantifican la potencia en kilovatios (kW) de cada rama de flujo.
- **Anatomía**: Grupo horizontal de 3 bloques con valor en negrita y leyenda descriptiva.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `solarText: string = '5.6kw'`, `homeText: string = '4.8kw'`, `gridText: string = '1.2kw'`.
- **Código HTML**:
```html
<div class="c-flow-legend-group">
  <div class="c-flow-legend-group__col">
    <span class="c-flow-legend-group__val">5.6kw</span>
    <span class="c-flow-legend-group__label">Solar</span>
  </div>
  <div class="c-flow-legend-group__col">
    <span class="c-flow-legend-group__val">4.8kw</span>
    <span class="c-flow-legend-group__label">Home</span>
  </div>
  <div class="c-flow-legend-group__col">
    <span class="c-flow-legend-group__val">1.2kw</span>
    <span class="c-flow-legend-group__label">Grid</span>
  </div>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-flow-legend-group {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.c-flow-legend-group__col {
  display: flex;
  flex-direction: column;
}
.c-flow-legend-group__val {
  font-size: 14px;
  font-weight: 700;
  color: var(--hg-text-primary);
}
.c-flow-legend-group__label {
  font-size: 12px;
  color: var(--hg-text-secondary);
}
```
- **Tokens consumidos**: `--hg-text-primary`, `--hg-text-secondary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Proporcional.

---

### [39] StatusPillBadge — Tipografía / Badges

- **Descripción funcional**: Píldora de estado de balance global que indica el rendimiento porcentual (*100%*).
- **Anatomía**: Etiqueta `<span>` en cápsula compacta.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `text: string = '100%'`.
- **Código HTML**:
```html
<span class="c-status-pill-badge">
  100%
</span>
```
- **Estilos CSS encapsulados**:
```css
.c-status-pill-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 12px;
  background-color: var(--hg-surface-elevated);
  border: 1px solid var(--hg-border-subtle);
  border-radius: var(--hg-radius-pill);
  font-size: 13px;
  font-weight: 700;
  color: var(--hg-text-primary);
}
```
- **Tokens consumidos**: `--hg-surface-elevated`, `--hg-border-subtle`, `--hg-radius-pill`, `--hg-text-primary`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Compacto.

---

### [40] FlowFooterMetrics — Tipografía / Leyendas

- **Descripción funcional**: Fila al pie del widget de flujo que reporta el porcentaje de autosuficiencia y el inversor activo.
- **Anatomía**: Flexbox horizontal con dos métricas clave.
- **Variantes**: Default.
- **Estados**: Default.
- **Props**: `selfSufficiency: string = 'Self-sufficiency: 92%'`, `inverter: string = 'Active Inverter #01'`.
- **Código HTML**:
```html
<div class="c-flow-footer">
  <span class="c-flow-footer__suff">Self-sufficiency: <strong>92%</strong></span>
  <span class="c-flow-footer__inv">Active Inverter #01</span>
</div>
```
- **Estilos CSS encapsulados**:
```css
.c-flow-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--hg-text-secondary);
  margin-top: 14px;
}
.c-flow-footer__suff strong {
  color: var(--hg-color-mint);
}
.c-flow-footer__inv {
  color: var(--hg-color-mint);
  font-weight: 500;
}
```
- **Tokens consumidos**: `--hg-text-secondary`, `--hg-color-mint`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Flex simple.

---

### [41] AppFooterMeta — Layout / Textos

- **Descripción funcional**: Barra de pie de página con créditos de la plataforma HelioGuard, estado de sincronización y versión.
- **Anatomía**: Elemento `<footer>` centrado con dot verde de enlace satelital.
- **Variantes**: Default.
- **Estados**: Connected, Syncing, Disconnected.
- **Props**: `platformText: string`, `version: string = 'v2.4.0'`.
- **Código HTML**:
```html
<footer class="c-app-footer" role="contentinfo">
  <span class="c-app-footer__dot" aria-hidden="true"></span>
  <span class="c-app-footer__text">
    HelioGuard Energy Intelligence Platform • Live Telemetry Sync: Connected • v2.4.0
  </span>
</footer>
```
- **Estilos CSS encapsulados**:
```css
.c-app-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px 0 12px 0;
  font-size: 12px;
  color: var(--hg-text-muted);
}
.c-app-footer__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--hg-color-mint);
  box-shadow: 0 0 6px var(--hg-color-mint);
}
```
- **Tokens consumidos**: `--hg-text-muted`, `--hg-color-mint`.
- **Comportamiento JS**: Ninguno.
- **Dependencias**: Ninguna.
- **Responsive**: Texto centrado responsivo.

---

## Índice Navegable de Componentes Generados

1. [01 AppShell / LayoutContainer](#01-appshell--layoutcontainer--layout--estructura)
2. [02 Header / GlobalNavbar](#02-header--globalnavbar--layout--navegación)
3. [03 BrandLogo / ShieldMark](#03-brandlogo--shieldmark--identidad--branding)
4. [04 NavPillSwitcher](#04-navpillswitcher--navegación--tabs)
5. [05 NavPillItem](#05-navpillitem--navegación--controles)
6. [06 IconButton / ActionCircular](#06-iconbutton--actioncircular--botones--acciones)
7. [07 NotificationDot](#07-notificationdot--elementos-decorativos--feedback)
8. [08 UserAvatar](#08-useravatar--identidad--perfil)
9. [09 GridDashboardLayout](#09-griddashboardlayout--layout--estructura)
10. [10 CardContainer](#10-cardcontainer--layout--estructura)
11. [11 CardHeaderWithAction](#11-cardheaderwithaction--estructuras-de-layout)
12. [12 DiagonalArrowIcon](#12-diagonalarrowicon--elementos-decorativos--iconos)
13. [13 HeroWelcomeCard](#13-herowelcomecard--layout--contenedores)
14. [14 DisplayHeading](#14-displayheading--tipografía--encabezados)
15. [15 BodyTextSubhead](#15-bodytextsubhead--tipografía--textos)
16. [16 StatusPillButtonGroup](#16-statuspillbuttongroup--botones--grupos)
17. [17 TelemetryIconButton](#17-telemetryiconbutton--botones--acciones)
18. [18 MetricTimeDisplay](#18-metrictimedisplay--tipografía--kpis)
19. [19 EqualizerBarChart](#19-equalizerbarchart--elementos-visuales--gráficos)
20. [20 RadialProgressGauge](#20-radialprogressgauge--elementos-visuales--gráficos)
21. [21 RadialMetricLabel](#21-radialmetriclabel--tipografía--feedback)
22. [22 SubcardFooterLabel](#22-subcardfooterlabel--tipografía--leyendas)
23. [23 MetricTotalKpi](#23-metrictotalkpi--tipografía--kpis)
24. [24 StripedProgressBar](#24-stripedprogressbar--elementos-visuales--progresos)
25. [25 ProgressMetaLabel](#25-progressmetalabel--tipografía--leyendas)
26. [26 BiphaseLoadDisplay](#26-biphaseloaddisplay--elementos-visuales--telemetría)
27. [27 SegmentedSpectralBar](#27-segmentedspectralbar--elementos-visuales--gráficos)
28. [28 DropdownPillSelect](#28-dropdownpillselect--inputs-y-controles--select)
29. [29 MonthlyBarChart](#29-monthlybarchart--elementos-visuales--gráficos)
30. [30 FinancialYieldBadge](#30-financialyieldbadge--tipografía--badges)
31. [31 ChartLegendIndicator](#31-chartlegendindicator--elementos-decorativos--leyendas)
32. [32 IconBadgeContainer](#32-iconbadgecontainer--elementos-decorativos--contenedores)
33. [33 FinancialMetricDisplay](#33-financialmetricdisplay--tipografía--kpis)
34. [34 SubmetricStatusIndicator](#34-submetricstatusindicator--tipografía--feedback)
35. [35 WeatherMetricDisplay](#35-weathermetricdisplay--tipografía--kpis)
36. [36 AuxiliaryInfoRow](#36-auxiliaryinforow--tipografía--textos)
37. [37 TripartiteFlowBar](#37-tripartiteflowbar--elementos-visuales--diagramas)
38. [38 FlowMetricLegendGroup](#38-flowmetriclegendgroup--tipografía--métricas)
39. [39 StatusPillBadge](#39-statuspillbadge--tipografía--badges)
40. [40 FlowFooterMetrics](#40-flowfootermetrics--tipografía--leyendas)
41. [41 AppFooterMeta](#41-appfootermeta--layout--textos)
