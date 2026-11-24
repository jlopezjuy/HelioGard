# Validación de simulaciones (`VALIDACION_SIMULACIONES.md`)

**Fecha:** 2026-11-23  
**Aplicación verificada:** `frontend/helioguard-web`  
**Integridad de `Idea Original`:** Sin modificaciones en ningún archivo de `Idea Original/`.

---

## 1. Pruebas unitarias ejecutadas (`npx ng test --watch=false`)

Resultado: **4 archivos de test, 31/31 pruebas en verde** (Vitest v4.1.12 + jsdom).

| Archivo de prueba | Casos cubiertos | Resultado |
|---|---|---|
| `src/app/core/domain/diurnal-sim.spec.ts` (nuevo — 6 tests) | 1. Curva solar `06:30–18:30`, temp de panel y potencia esperada `-0.4 %/°C` (`4.60 kW` al mediodía).<br>2. Régimen normal sin falsas alarmas.<br>3. Degradación gradual (`-0.012`/paso hasta piso `0.55`), alerta amarilla `Rendimiento bajo` (`ratio < 0.93` con `>= 6` muestras útiles), pérdida `kWh`/`ARS` y recuperación (`+0.05`/paso hasta `1.0` y limpieza de ventana de 12 lecturas).<br>4. Ataque replay congelado: silencio los primeros 4 ticks, disparo de `Posible replay` en el 5.º tick idéntico y congelamiento de `perdidaKwh`.<br>5. Inyección falsa (`1.7·esperada + 0.8`): disparo inmediato de `Dato imposible` y congelamiento de pérdida.<br>6. Condiciones extremas: reinicio de jornada `19:00 → 07:00`, piso `0.1 kW` en chequeo `V × I`, y recálculo por cambio de tarifa. | **PASS (6/6)** |
| `src/app/core/domain/noa-and-intervention.spec.ts` (nuevo — 4 tests) | 1. 6 zonas climáticas del NOA y descomposición secuencial (`polvo`, `escarcha`, `falla`) en Abra Pampa.<br>2. Regla de escarcha ignorada cuando `tmin > 0 °C` (Orán).<br>3. Cálculo de pérdida, recuperación y `paybackDays` en rutas `drone` / `technician` / `owner`.<br>4. Compuertas de seguridad para helada, viento fuerte y acceso en techo. | **PASS (4/4)** |
| `src/app/core/domain/rules.spec.ts` (ampliado — 13 tests) | Los 12 tests originales de integración Wh, contadores, déficit no comparable, ARS, reparto de causas, escenarios de pérdida, replay/duplicado/boot/8 MW, outbox WAN, FSM de misión, multi-tenant y cobertura 47/47 + **nuevo test** de inyección moderada (`82.4 kW` < `115 kW` detectada por `V × I`) y stream congelado de 5 muestras idénticas (`REPLAY`). | **PASS (13/13)** |
| `src/app/shared/ui/tokens.spec.ts` (8 tests) | Pares de contraste del Design System Stitch (`hg-*`). | **PASS (8/8)** |

---

## 2. Compilación de producción (`npm run build`)

- `ng build` ejecutado después de la última edición en `frontend/helioguard-web`: **PASS** (0 errores, 0 warnings).

---

## 3. Validación en navegador y resoluciones (`Playwright` + `ng serve`)

Ejecutado contra `http://127.0.0.1:4310/` usando `chromium_headless_shell-1248`:

- **Regresión e2e (`e2e/demo.spec.ts`):** Flujo completo `/parque` → `Sector sucio` → `FROST` → `Crear misión` → `Revisar seguridad` → `Aprobar` → `Despachar` → `Animar dron` (×8) → `Verificar` → `Evento original` → `Replay exacto` → `Inyección falsa` (sin `"ataque confirmado"`) → `Restablecer demo` → `/inicio`: **PASS (`E2E_REGRESSION: PASS`)**.
- **Simulación diurna en `/energia` (`DIURNAL_SIM_UI: PASS`):** Pestaña `Simulación diurna (07:00–19:00)` verificada con avance `+30 min`, `Ensuciar panel` → `Rendimiento bajo`, `Ataque replay` (×5 `+5 min`) → `Posible replay`, e `Inyección falsa` → `Dato imposible`.
- **Nuevos detectores en `/telemetria` (`TELEMETRY_NEW_DETECTORS_UI: PASS`):** `Inyección V×I inconsistente` dispara `PHYSICAL_INCONSISTENCY` (`V*I`) y `Stream congelado 5×` dispara `REPLAY` tras 5 muestras idénticas.
- **Simulación regional NOA en `/energia` (`NOA_REGIONAL_UI: PASS`) y simulador de intervención en `/mantenimiento` (`INTERVENTION_SIM_UI: PASS`):** Verificados en `1440×900`, `768×1024` y `390×844` con `OVERFLOWS: NONE` tras habilitar `flex-wrap` en `.hg-tabs` (`styles.css`).
- **Evidencias visuales:** `docs/screenshots/inicio-1440-navfix.png`, `docs/screenshots/energia-diurna-{1440,768,390}.png`, `docs/screenshots/energia-noa-{1440,768,390}.png`, `docs/screenshots/telemetria-vxi-{1440,768,390}.png`, `docs/screenshots/mantenimiento-flow-{1440,768,390}.png`.

---

## 4. Pendientes / fuera de alcance de este ciclo

1. **Sprint S4 (P2):** Implementación en `backend/` (JHipster) de las 47 entidades JDL, persistencia PostgreSQL, ingestión real MQTT/Modbus y firma criptográfica real.
2. **Playwright binario local:** Instalar `npx playwright install chromium-headless-shell` para que `npm run e2e` ejecute el CLI directo sin indicar ruta al binario `1248`.
