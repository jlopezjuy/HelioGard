# Escenarios de simulación de HelioGuard (Fase 3)

Cada escenario representa un proceso documentado en `Idea Original` o en las ampliaciones SDD verificables sobre datos simulados. Cuando la idea original no define una fórmula o umbral, se declara explícitamente la ausencia y no se inventa comportamiento como original.

---

## ESC-01 — Ciclo diurno normal y recuperación gradual de suciedad (`Idea Original/app.py:L29-L49`)

1. **Condiciones iniciales:** Hora simulada `07:00`, `modo = 'normal'`, `suciedad = 1.0` (o `< 1.0` si viene de ensuciamiento previo), `perdidaKwh = 0`, `hist = []` (máx. 12 muestras, pasos `PASO_H = 5/60` h).
2. **Datos de entrada:** `peakKw` (default `5.0` kW en prototipo original; configurable a `48.0` kW para B2), `tarifa` (default `120` ARS/kWh en original o `200` ARS/kWh en B2), ruido acotado (`temp ±0.4 °C`, `pot ±1.5 %`, `V 380 ± 3 V`, `irr ±2 %`).
3. **Acciones del usuario:** Iniciar reproducción (`Play`), avanzar paso manual (`+5 min`), ajustar `peakKw` / `tarifa`, o volver a `Día normal` tras ensuciar.
4. **Reglas o fórmulas aplicadas:**
   - `irr = max(0, sin(π · (h - 6.5) / 12)) · 1000` (W/m², campana `06:30–18:30`).
   - `temp = 20 + irr · 0.025 + ruido_temp` (°C).
   - `esperada = peakKw · (irr / 1000) · (1 - 0.004 · (temp - 25))` (kW, coeficiente térmico `-0.4 %/°C`).
   - Si viene sucio: `suciedad = min(1.0, suciedad + 0.05)` por paso.
   - `potencia = esperada · suciedad · ruido_pot`, `tension = 380 + ruido_v`, `corriente = potencia · 1000 / tension`.
5. **Cambios de estado:** Reloj avanza `+5 min` por tick; al superar `19:00` reinicia el día a `07:00` y limpia la serie temporal. Si `suciedad < 1.0`, converge a `1.0` en ≤ 9 pasos.
6. **Eventos y alertas:** Ninguna alerta si `ratio >= 0.93`. Estado global `verde`.
7. **Resultado esperado:** Curva en campana de `potencia` siguiendo a `esperada` hasta ~`5 kW` al mediodía (`12:30`), con pérdida acumulada cercana a `0`.
8. **Cómo comprobar que es correcta:** A las `12:30` con `temp = 45 °C` (`20 + 1000*0.025`) y `peakKw = 5`, `esperada = 5 · 1 · (1 - 0.004 · 20) = 4.60 kW`. Si venía de `suciedad = 0.55`, tras 9 ticks en `normal` vuelve a `1.0`.

---

## ESC-02 — Ensuciamiento gradual y alerta `Rendimiento bajo` (`Idea Original/app.py:L41-L42, L84-L91, L102-L104`)

1. **Condiciones iniciales:** Hora diurna útil (`esperada > 0.5 kW`, p. ej. `08:30–17:00`), `suciedad = 1.0`, `estado = 'verde'`.
2. **Datos de entrada:** Modo `'suciedad'`, tasa de ensuciamiento `-0.012` por paso de 5 minutos, piso físico simulado `0.55`.
3. **Acciones del usuario:** Pulsar **`Ensuciar panel`** y observar al menos 6–10 pasos de simulación (automático o paso manual).
4. **Reglas o fórmulas aplicadas:**
   - `suciedad = max(0.55, suciedad - 0.012)` por paso.
   - Ventana móvil `hist` (últimas 12 lecturas): se filtran lecturas útiles `util = hist.filter(r => r.esperada > 0.5)`.
   - Si `util.length >= 6` y `actual.esperada > 0.5` y `sum(potencia in util) / sum(esperada in util) < 0.93` → emite alerta amarilla.
   - Contabilización de pérdida: si `estado !== 'rojo'` y `potencia < 0.97 · esperada`, `perdidaKwh += (esperada - potencia) · (5/60)`; `perdidaPesos = round(perdidaKwh · tarifa)`.
5. **Cambios de estado:** `suciedad` desciende progresivamente (`1.0 → 0.988 → ... → 0.55`); al caer el promedio móvil por debajo de `0.93`, `estado` pasa de `verde` a `amarillo`.
6. **Eventos y alertas:** Alerta **`Rendimiento bajo`** (nivel `amarillo`): *"Producción por debajo de lo esperado. Programar limpieza."*
7. **Resultado esperado:** La curva real se separa por debajo de la curva esperada; el contador de `kWh` y `ARS` perdidos crece de forma proporcional al déficit y a la tarifa.
8. **Cómo comprobar que es correcta:** Con `esperada > 0.5 kW`, durante los primeros 5 pasos la alerta aún no dispara si el promedio móvil sigue `>= 0.93`; al cruzar `< 0.93` con `>= 6` muestras útiles aparece la alerta amarilla y `perdidaKwh > 0`.

---

## ESC-03 — Ataque Replay / Stream congelado (`Idea Original/app.py:L51-L56, L77-L82, L102-L104`)

1. **Condiciones iniciales:** Sistema operando con lecturas previas en `hist`, `perdidaKwh = P0`.
2. **Datos de entrada:** Modo `'replay'` (prototipo original) o evento `REPLAY` / 5 lecturas consecutivas con `(potencia, tension)` idénticas (sin ruido térmico/eléctrico).
3. **Acciones del usuario:** Pulsar **`Ataque replay`** (en `/energia` simulación diurna) o **`Stream congelado 5×`** / **`Replay exacto`** / **`Misma secuencia`** (en `/telemetria`).
4. **Reglas o fórmulas aplicadas:**
   - Prototipo original: al entrar en `'replay'`, congela la primera lectura (`congelado`) y la devuelve sin variación en los ticks siguientes.
   - Detector: toma las últimas `5` lecturas de `hist`; si `len === 5` y todas tienen exactamente el mismo par `(potencia, tension)` → emite alerta roja **`Posible replay`**.
   - Además en `rules.ts`: si se repiten `gatewayId+eventId` (`DUPLICATE_EVENT` → `QUARANTINED`) o `deviceId+bootId+sequenceNumber` (`REPLAY` → `UNDER_REVIEW`), `useInEnergy = false`.
   - Congelamiento contable: cuando `estado === 'rojo'`, **no se acumula `perdidaKwh`**.
5. **Cambios de estado:** Los primeros 4 ticks en modo replay aún no completan las 5 muestras idénticas; en el **5.º tick idéntico**, `estado` pasa a `rojo` y `perdidaKwh` queda congelado en `P0`.
6. **Eventos y alertas:** Alerta **`Posible replay`** (nivel `rojo`): *"Cinco lecturas idénticas seguidas. Los sensores reales siempre tienen ruido."* En `/telemetria`, `TelemetryIntegrityEvent` + `SecurityIncident` (`INVESTIGATING`, sin afirmar ataque confirmado).
7. **Resultado esperado:** El reloj sigue avanzando, la línea real queda plana, se dispara la bandera roja de integridad y el monto económico no se contamina.
8. **Cómo comprobar que es correcta:** Verificar que hasta el tick 4 de valores idénticos no hay `Posible replay`, en el tick 5 aparece la alerta roja y `perdidaKwh` permanece igual antes y después del tick 5 aunque `potencia != esperada`.

---

## ESC-04 — Inyección falsa / Inconsistencia `V × I` (`Idea Original/app.py:L57-L58, L69-L75`)

1. **Condiciones iniciales:** Sistema en ejecución diurna o ingestión en `/telemetria`.
2. **Datos de entrada:** Modo `'inyeccion'` (`potencia = esperada · 1.7 + 0.8`, manteniendo `tension` y `corriente` sin ajustar) o mensaje de telemetría con `powerW` inconsistente respecto de `voltageV · currentA` (`> 8 %`) o respecto de `expectedPowerW` (`> 1.25 · esperada + 0.3 kW`).
3. **Acciones del usuario:** Pulsar **`Inyección falsa`** (en `/energia`) o **`Inyección V×I inconsistente`** / **`Inyección falsa`** (en `/telemetria` o guion).
4. **Reglas o fórmulas aplicadas:**
   - `pvi = (tension · corriente) / 1000` (kW).
   - Dispara **`Dato imposible`** (`rojo`) si: `|potencia - pvi| > 0.08 · max(pvi, 0.1)` **O** `potencia > 1.25 · esperada + 0.3`.
   - Si `estado === 'rojo'`, congela `perdidaKwh` (y en `evaluateIngest`, `useInEnergy = false`, `trustLevel = 'SUSPECT'`).
5. **Cambios de estado:** En el **primer tick** de inyección falsa, `estado` cambia inmediatamente a `rojo`.
6. **Eventos y alertas:** Alerta **`Dato imposible`** (nivel `rojo`): *"Potencia no coincide con V×I ni con la radiación. Sensor roto o dato inyectado."* Nunca muestra *"ataque confirmado"*.
7. **Resultado esperado:** Se detecta incluso si la potencia inyectada es menor al techo absoluto de `115 kW` (por ejemplo, `82 kW` sobre un bloque de `48 kW` esperados o un salto de potencia sin cambio de `V × I`), se aísla del cálculo energético y queda trazado en `/integridad`.
8. **Cómo comprobar que es correcta:** Inyectar `potencia = esperada · 1.7 + 0.8` con `V = 380 V`, `I = 10 A` (`pvi = 3.8 kW`): debe disparar `Dato imposible` inmediato (`useInEnergy = false`) y mantener `perdidaKwh` invariable.

---

## ESC-05 — Cadena completa Parque → Incidente → Orden → Misión Dron → Recuperación (`pichoriginal.md:L14-L16` + SDD `FR-006`)

1. **Condiciones iniciales:** Bloque B2 con `SOILING` (`31 kW` vs `48 kW` esperados; déficit `4.250 Wh`, `ARS 697`), sin bloqueo de viento/helada/techo, fabricante/operador/sitio conformes.
2. **Acciones del usuario:** Seleccionar `Sector sucio` → `Crear misión` → `Revisar seguridad` → `Aprobar` (rol `OWNER`/`ADMIN`) → `Despachar` → `Animar dron` (8 pasos) → `Verificar`.
3. **Reglas o fórmulas aplicadas:** Máquina de estados `DRAFT → SAFETY_REVIEW → AWAITING_AUTHORIZATION → AUTHORIZED → DISPATCHED → IN_FLIGHT → COMPLETED` (`MISSION_NEXT`), validando `safetyBlockReasons` (viento, helada, techo, fabricante, operador, sitio) y `droneProgress >= 1`.
4. **Resultado esperado:** Al verificar, B2 vuelve a `NONE` (`46.2 kW`), se calcula `recovered = before - after` (`3.800 Wh`), y se registra `VerificationResult` (`RECOVERED`).
5. **Cómo comprobar que es correcta:** Ejecución de `e2e/demo.spec.ts` + tests de `rules.spec.ts` (bloqueo por helada/viento y transición completa).

---

## ESC-06 — Escenarios y procesos SIN fórmula documentada en `Idea Original` (ausencias explícitas)

Para respetar estrictamente la regla de no inventar algoritmos como si fueran originales, se deja constancia de que **ninguno de los siguientes procesos tiene fórmula ni algoritmo especificado en `Idea Original`**:
- **Mapa térmico satelital por panel y detección de hotspots por temperatura (`pichoriginal.md:L14-L15`)**: No existen umbrales térmicos ni modelo radiométrico en `app.py` / `index.html`. En HelioGuard actual se modela honestamente como vista cenital por bloque con leyenda *"No es una ortofoto ni una termografía de paneles"*.
- **Visión artificial / reconocimiento de suciedad o microfisuras por cámara de dron**: Ausente en `Idea Original`.
- **Diodo bypass perforado, microfisuras, corrosión y sombreado parcial de string**: Ausentes en `Idea Original` (solo existen como texto ilustrativo en maquetas Stitch o eventos discretos en SDD).
- **Enrutamiento autónomo real del dron (`lat/lon`, altitud, batería de dron)**: Ausente en `Idea Original`; en Angular el dron sigue una trayectoria simulada lineal de 8 pasos (`0..1`) con etiqueta explícita *"No es una aeronave real"*.
- **Nota sobre `ideas/demos/helioguard_lab.html`**: El modelo regional de 6 zonas climáticas del NOA (`Abra Pampa`, `Tilcara`, `San Salvador`, `Orán`, `Cafayate`, `Tartagal`) y la descomposición secuencial `dustFactor · frostFactor · faultFactor` son **ampliaciones posteriores** de `ideas/demos/`, no fórmulas de `Idea Original`.
