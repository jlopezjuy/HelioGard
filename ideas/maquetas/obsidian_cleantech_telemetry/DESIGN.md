---
name: Obsidian CleanTech Telemetry
colors:
  surface: '#111417'
  surface-dim: '#111417'
  surface-bright: '#37393d'
  surface-container-lowest: '#0b0e11'
  surface-container-low: '#191c1f'
  surface-container: '#1d2023'
  surface-container-high: '#272a2e'
  surface-container-highest: '#323538'
  on-surface: '#e1e2e7'
  on-surface-variant: '#c7c9ac'
  inverse-surface: '#e1e2e7'
  inverse-on-surface: '#2e3134'
  outline: '#909378'
  outline-variant: '#464932'
  surface-tint: '#bbd200'
  primary: '#ffffff'
  on-primary: '#2d3400'
  primary-container: '#d6f000'
  on-primary-container: '#5e6b00'
  inverse-primary: '#596400'
  secondary: '#70ffba'
  on-secondary: '#003822'
  secondary-container: '#01e599'
  on-secondary-container: '#00613e'
  tertiary: '#ffffff'
  on-tertiary: '#00354a'
  tertiary-container: '#c4e7ff'
  on-tertiary-container: '#006c93'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d6f000'
  primary-fixed-dim: '#bbd200'
  on-primary-fixed: '#191e00'
  on-primary-fixed-variant: '#424b00'
  secondary-fixed: '#4dffb2'
  secondary-fixed-dim: '#00e297'
  on-secondary-fixed: '#002112'
  on-secondary-fixed-variant: '#005234'
  tertiary-fixed: '#c4e7ff'
  tertiary-fixed-dim: '#7bd0ff'
  on-tertiary-fixed: '#001e2c'
  on-tertiary-fixed-variant: '#004c69'
  background: '#111417'
  on-background: '#e1e2e7'
  surface-variant: '#323538'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.03em
  display-sm:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '600'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  metric-xl:
    fontFamily: Space Grotesk
    fontSize: 38px
    fontWeight: '700'
    lineHeight: 42px
    letterSpacing: -0.03em
  metric-md:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.02em
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-code:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-tag:
    fontFamily: Manrope
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1.25rem
  gutter-mobile: 0.75rem
  margin: 2rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-2xl: 3rem
---

## Brand & Style

This design system establishes a high-performance, precision clean-tech visual language tailored for modern residential and commercial energy management. Drawing inspiration from modern aerospace telemetry and luxury electric vehicle dashboards, the interface combines an ultra-deep obsidian background with focused bioluminescent highlights. 

The aesthetic is anchored in **Technical Minimalism with Glassmorphic Depth**:
- **Personality:** Authoritative, high-tech, ecologically conscious, and mathematically precise.
- **Atmosphere:** An immersive dark canvas that eliminates screen glare while emphasizing hyper-vibrant energy generation metrics, battery levels, and telemetry charts.
- **Visual Weight:** Ultra-subtle cool-slate tinted container borders, layered translucent backing panels, radial glowing accents around energy rings, and pillular interactive controls that feel physical yet digital.

## Colors

The color architecture is built around an uncompromising dark palette optimized for contrast, legible telemetry, and glowing visual cues:

- **Primary (`#E4FF1A` / Electric Lime):** Denotes active solar generation, peak battery discharge, interactive selections, and primary focus states. It radiates against dark backgrounds with an unmistakable high-voltage presence.
- **Secondary (`#00E599` / Mint Emerald):** Represents grid independence, sufficiency, clean storage balance, positive energy returns, and optimal system health.
- **Tertiary (`#38BDF8` / Electric Sky):** Used selectively for secondary telemetry streams, meteorological solar irradiance feeds, and ambient cooling/weather indicators.
- **Neutrals & Surfaces:**
  - `Surface Base`: `#080A0C` (deep obsidian canvas).
  - `Surface Container Low`: `#0F1318` (card backgrounds, floating modular panels).
  - `Surface Container High`: `#192028` (elevated modal panels, pills, and dropdown sheets).
  - `Border / Ghost Outline`: `rgba(255, 255, 255, 0.08)` to `rgba(255, 255, 255, 0.14)`.
  - `On-Surface Primary`: `#F8FAFC` (pure readability).
  - `On-Surface Muted`: `#94A3B8` (unit descriptors, axis labels, timestamps).

## Typography

The typographic strategy balances computational precision with clear editorial legibility:

- **Headlines & Primary Metrics (`Space Grotesk`):** Engineered geometric grotesque that lends an unmistakable technological identity to large figures, hero headings, and key energy readouts.
- **Body & Structural Text (`Manrope`):** A geometric, modern sans-serif featuring open apertures and balanced proportions that ensure effortless scanning in low-light environments.
- **Telemetry & Timestamp Data (`JetBrains Mono`):** Applied to telemetry axis ticks, unit tags (e.g., `kWh`, `kW`, `AUD`), and system operational logs to align decimal figures and reinforce the instrumentation feel.

## Layout & Spacing

The platform uses a 12-column adaptive fluid grid designed to display modular, telemetry-dense monitoring cards side-by-side without visual friction:

- **Grid Architecture:** 
  - **Desktop:** 12 columns, `1.25rem` (20px) gutters, `2rem` outer padding.
  - **Tablet:** 6 columns, `1rem` gutters, `1.5rem` outer canvas margin.
  - **Mobile:** 2 columns or stacked single-column flow, `0.75rem` gutters, `1rem` canvas margin.
- **Rhythm & Nesting:** High-level dashboard layouts prioritize horizontal distribution with balanced modular cards. Card interiors strictly enforce an 8px incremental scale (`space-xs` through `space-xl`) to establish coherent data groupings, positioning telemetry charts comfortably alongside quick-metric summaries.

## Elevation & Depth

Rather than relying on classic drop shadows, this design system produces physical visual separation through **Layered Obsidian Surfaces, Translucent Blurs, and Bioluminescent Edge Highlights**:

1. **Base Tier (Level 0):** Pure background canvas `#080A0C` layered with subtle, ambient radial gradients reflecting the current generation status (e.g., a faint 4% opacity emerald/lime wash).
2. **Card Surfaces (Level 1):** Solid obsidian `#0F1318` or glassmorphic tinted overlay `rgba(15, 19, 24, 0.75)` with `backdrop-filter: blur(16px)` and a continuous 1px outline of `rgba(255, 255, 255, 0.08)`.
3. **Elevated Overlays & Modals (Level 2):** Elevated containers (`#192028`) featuring a compound shadow: `0 20px 40px -10px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.12)`.
4. **Neon Glow & Active Focal Depth:** Active status rings, toggled badges, and primary battery indicators incorporate an ambient outer glow: `box-shadow: 0 0 24px -4px rgba(228, 255, 26, 0.35)`.

## Shapes

The design system embraces a generous, organic pill-shaped geometric philosophy:

- **Cards & Data Modules:** Enclosed within smooth `rounded-2xl` (1.5rem / 24px) to `rounded-3xl` (2rem / 32px) radii, giving complex telemetry containers a tactile, hardware-calibrated appearance.
- **Controls & Navigation:** All navigation links, search capsules, status chips, and primary trigger buttons adopt complete pill geometry (`rounded-full` / 9999px).
- **Chart Elements:** Bar meters and segmented battery tracks utilize rounded end-caps (`rounded-full`) to maintain an approachable, cohesive feel throughout numeric data displays.

## Components

### Buttons & Interactive Controls
- **Primary Action Buttons:** Pill capsules filled with `#E4FF1A` text in `#080A0C`, bold `Manrope` typography, hover state transitioning into a luminous electric halo (`0 0 20px rgba(228, 255, 26, 0.4)`).
- **Secondary / Ghost Buttons:** Translucent slate pills (`rgba(255, 255, 255, 0.06)`) with a crisp 1px border (`rgba(255, 255, 255, 0.12)`), transitioning to brighter border contrast upon hover.
- **Pill Navigation Bar:** A floating frosted capsule housing nav items; the active route is represented by a solid dark slate chip (`#1E2630`) framed with an ultra-thin border and white primary text.

### Telemetry Cards & Metric Blocks
- Modular containers finished with 24px corner radii, ghost borders, and a corner icon link (diagonal arrow in a subtle circular container).
- Metric titles are set in muted cool gray (`#94A3B8`), followed by oversized metric readouts in `Space Grotesk` accompanied by baseline-aligned mono units.

### Circular Radial Status Gauges
- Dual concentric tracks: a background track styled with subtle hatch marks or dashed lines (`rgba(255, 255, 255, 0.15)`), and a primary active sweep colored in glowing `#E4FF1A` or `#00E599` with smooth circular terminals. Centered values report percentage and current operation ("Charging...", "Exporting").

### Telemetry Charts & Equalizer Graphs
- Multi-column bar arrays with rounded tops: positive solar feed is populated in electric lime `#E4FF1A`, while baseline consumption or secondary metrics appear in soft muted charcoal `#2A3441` or mint `#00E599`.
- Dropdown timeline selectors (e.g., "6 months", "Live", "24 Hours") feature compact pill shapes with clean chevron indicators.

### Progress & Load-Balance Segment Bars
- Multi-segment pill tracks visually communicating power flows between solar array, battery unit, and home grid. Glowing neon fills dictate active energy delivery, while standby buffers maintain low-contrast gray tones.