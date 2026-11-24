# Auditoría funcional y de cobertura: Idea Original vs. HelioGuard actual

**Alcance auditado:**
- **Idea Original (solo lectura, sin modificaciones):** `Idea Original/pichoriginal.md`, `Idea Original/README.md`, `Idea Original/app.py`, `Idea Original/index.html`, `Idea Original/mnt_user-data_outputs_helioguard_docs_index.html`, `Idea Original/requirements.txt`, `Idea Original/helioguard.zip` (verificado idéntico al contenido extraído).
- **Proyecto actual:** `frontend/helioguard-web` (aplicación Angular 21 canónica), `ideas/specs/` (`REQUIREMENTS.md`, `domain.v4.draft.jdl`), `ideas/docs/` (`00`–`13`), `ideas/demos/` (`helioguard_lab.html`, `helioguard_orbit.html`, `helioguard_flow.html`), `ideas/maquetas/` (Stitch Design System) y `backend/` (scaffolding JHipster).

---

## 1. Síntesis de la Idea Original (evidencia documental)

La carpeta `Idea Original` contiene **dos capas claramente diferenciadas**:

1. **Visión de pitch (`Idea Original/pichoriginal.md`)**:
   - **Problema:** En Jujuy y Argentina nadie puede verificar externamente si un panel está sucio, degradado o si los datos reportados por la planta son verídicos (`pichoriginal.md:L5-L6, L27`).
   - **Solución propuesta:** Sistema autónomo que usa un satélite como testigo independiente (irradiación y mapa térmico) para detectar pérdida o manipulación de datos, encierra el sector afectado en rojo con coordenadas, aísla telemetría comprometida y despacha un dron automatizado de limpieza hasta recuperar producción (`pichoriginal.md:L9, L14-L17, L36`).
   - **Modelo comercial:** Suscripción mensual por planta para cooperativas, parques solares, instaladores y municipios rurales, vinculado a auditorías continuas de ciberseguridad de Alpha Digital (`pichoriginal.md:L20, L38, L42`).
2. **Prototipo funcional ejecutado (`Idea Original/app.py`, `index.html`, `mnt_user-data_outputs_helioguard_docs_index.html`, `README.md`)**:
   - Simulador en memoria (`Flask` + `canvas` HTML5, un solo usuario) de una instalación de **5 kW** (`PICO_KW = 5.0`), tarifa ejemplo **120 $/kWh**, ciclo diurno **07:00–19:00** en pasos de **5 minutos simulados** (`PASO_H = 5/60`) por segundo (`app.py:L13-L23`).
   - Modelo físico térmico/irradiancia (`app.py:L29-L49`) y 3 detectores sobre ventana deslizante de 12 lecturas (`app.py:L63-L92`):
     1. **`Dato imposible` (`rojo`)**: inconsistencia eléctrica `|P - (V·I/1000)| > 0.08 · max(V·I/1000, 0.1)` o potencia físicamente imposible `P > 1.25 · esperada + 0.3`.
     2. **`Posible replay` (`rojo`)**: 5 pares consecutivos idénticos de `(potencia, tension)`.
     3. **`Rendimiento bajo` (`amarillo`)**: promedio móvil `sum(potencia)/sum(esperada) < 0.93` sobre `>= 6` lecturas diurnas útiles (`esperada > 0.5` kW).
   - Contabilización de pérdida energética y económica solo si `estado != "rojo"` y `potencia < 0.97 · esperada` (`app.py:L102-L114`).

**Ausencias explícitas en `Idea Original`:** No hay código ni fórmulas para hardware real (ESP32/Raspberry), base de datos, WebSockets, visión por computadora/IA, API satelital real, API/ruta de dron, sombras parciales, diodos bypass, fallas de inversor, microfisuras ni corrosión.

---

## 2. Matriz comparativa de cobertura (Idea Original vs. estado actual)

| ID | Requisito original | Fuente (`Idea Original`) | Implementación actual | Estado | Diferencias | Acción recomendada |
|---|---|---|---|---|---|---|
| ORIG-01 | Comparar potencia real vs. esperada por irradiación y temperatura (`-0.4 %/°C`) | `pichoriginal.md:L9,L34`; `app.py:L29-L39,L85` | `frontend/.../rules.ts` usa `SECTOR` estático (`48 kW` / `46.2 kW`); la curva diurna + temp existe solo en `ideas/demos/helioguard_lab.html` | **Implementado parcialmente** | Angular no ejecuta la simulación temporal `07:00–19:00` ni el derating térmico `-0.4 %/°C` paso a paso | Implementar motor temporal diurno fidelizado en Angular (`/energia`) |
| ORIG-02 | Simular degradación gradual por suciedad (`-0.012`/paso hasta `0.55`) y recuperación (`+0.05`/paso) | `app.py:L41-L44`; `index.html:L74` | `actions.ts:L123-L131` cambia instantáneamente entre potencias fijas (`46.2 kW` ↔ `31 kW`) y limpia al verificar misión | **Implementado parcialmente** | Falta evolución continua paso a paso en Angular (solo existe en `helioguard_lab.html` y `orbit.html` de `ideas/demos/`) | Integrar simulación temporal con Play/Pausa/Paso y slider/tasa de ensuciamiento |
| ORIG-03 | Detector `Rendimiento bajo` (`amarillo`: `ratio < 0.93` en `>= 6` muestras con `esperada > 0.5 kW`) | `app.py:L84-L91`; `README.md:L10` | `actions.ts` marca déficit estático en B2; `rules.ts` no evalúa promedio móvil de ratio sobre ventana deslizante | **Solo simulado** | No existe detector estadístico de ventana deslizante en `frontend/helioguard-web` | Incorporar el detector exacto en el motor diurno y probar umbral `0.93` |
| ORIG-04 | Detector `Dato imposible` (`rojo`: `|P - V·I| > 8 %` o `P > 1.25·esperada + 0.3`) | `app.py:L69-L75`; `README.md:L8` | `rules.ts:L178-L190` valida techo absoluto `powerW > 110_000 + 5_000` (`8 MW` spike) | **Implementado parcialmente** | `rules.ts` no verifica coherencia eléctrica `V × I` ni techo relativo `1.25·esperada + 0.3`; una inyección moderada (`82 kW`) pasaría como válida | Agregar validación `V × I` y techo relativo en `rules.ts` + simulación de inyección en `/telemetria` y `/energia` |
| ORIG-05 | Detector `Posible replay` (`rojo`: 5 lecturas consecutivas con `(P, V)` idénticos) | `app.py:L77-L82`; `README.md:L9` | `rules.ts:L141-L176` detecta duplicado `gatewayId+eventId` y replay de secuencia `deviceId+bootId+sequenceNumber` | **Implementado parcialmente** | El enfoque JDL detecta replay de protocolo, pero no detecta un stream congelado con `eventId`/`seq` crecientes y valores `(P, V)` idénticos sin ruido | Agregar detector de stream congelado (5 muestras idénticas) en `rules.ts` y el motor diurno |
| ORIG-06 | Congelar pérdida acumulada (`kWh` y `ARS`) cuando `estado == rojo` y aplicar banda muerta `3 %` (`P < 0.97·esperada`) | `app.py:L102-L104,L113-L114` | `rules.ts:L192-L204` marca `useInEnergy: false` por mensaje; `lossAmountArs` (`rules.ts:L74-L85`) monetiza autoconsumo + exportación | **Implementado parcialmente** | `recomputeSector` en `actions.ts` no congela la pérdida de `SECTOR` ante alerta roja de telemetría ni aplica explícitamente la banda muerta del 3 % | Reproducir regla de congelamiento de pérdida ante `rojo` en el simulador diurno y pruebas |
| ORIG-07 | Aislar telemetría comprometida y alertar sin atribución falsa de ataque | `pichoriginal.md:L17`; `app.py:L75,L81` | `actions.ts:L489-L617` (`QUARANTINED`, `SUSPECT`, `useInEnergy: false`, `SecurityIncident` sin "ataque confirmado") | **Implementado y verificado** | Evolucionado positivamente: separa `Incident` de `SecurityIncident` y evita afirmar intrusión sin criptografía | Mantener y cubrir también el nuevo caso `V × I` y stream congelado |
| ORIG-08 | Serie temporal en vivo (`potencia` vs. `esperada`) con reloj diurno y botón Reiniciar | `app.py:L106-L129`; `index.html:L54,L78,L106-L144` | `/energia` muestra 4 barras estáticas (`10:00–10:45`); `shell` tiene reloj fijo y `Restablecer demo` | **Implementado parcialmente** | Falta gráfico de serie temporal continua `07:00–19:00` con Play/Pausa/Paso/Reinicio en Angular | Agregar pestaña de serie temporal diurna en `/energia` |
| ORIG-09 | Uso de satélite como testigo independiente (irradiación y mapa térmico) | `pichoriginal.md:L14,L17,L23,L34-L36` | `model.ts` (`EvidenceSource`, `DataProvenance`), `/fuentes`, `07_SATELITES_DRONES_LIMITACIONES.md` | **Solo simulado** | **Simplificación/corrección intencional:** SDD reemplaza "termografía satelital por panel" por referencia meteorológica/irradiancia (`GOES`/`NASA POWER`) con límite explícito en `/parque` y `/fuentes` | Mantener el límite documental; no simular termografía satelital falsa |
| ORIG-10 | Encerrar sector afectado en rojo con coordenadas (vista cenital) | `pichoriginal.md:L14-L15` | `/parque` (`park.html`) resalta bloque B2 con trama y estado; `orbit.html` tiene grilla SVG `A1..C3` | **Implementado y verificado** | Cubierto para 4 bloques (`B1–B4`) en `/parque` con etiquetas accesibles | Conservar; evaluar mostrar coordenadas de bloque/string si aporta |
| ORIG-11 | Despacho automatizado de dron de limpieza a coordenadas hasta recuperar producción | `pichoriginal.md:L16,L36` | `/misiones` + `/parque` + `actions.ts:L193-L483` (`DRAFT -> COMPLETED` con checklist de seguridad y 8 pasos) | **Implementado y verificado** *(con restricción de seguridad)* | **Mejora de seguridad intencional:** reemplaza el despacho totalmente autónomo por máquina de estados con revisión de seguridad, aprobación humana simulada y bloqueos de viento/helada/techo | Conservar la máquina de estados con aprobación y bloqueos |
| ORIG-12 | Modelo comercial mensual por planta (cooperativas, parques, instaladores, municipios) y auditorías | `pichoriginal.md:L20,L38,L42` | `/planes`, `/auditorias`, `/organizaciones`, `/economia` (`09_MODELO_NEGOCIO_AUDITORIA_COBERTURA.md`) | **Solo simulado** | Ampliado a multi-tenant con 3 orgs, grants temporales, planes sin seguro y auditorías offline con SHA-256 mock | Mantener en modo demo; backend JPA aún no implementa estas entidades |
| ORIG-13 | Persistencia en base de datos, backend REST de dominio y tiempo real | `app.py:L18-L23,L95-L134` | `backend/` tiene solo scaffold JHipster de usuarios/auth; `DemoStore` persiste en `localStorage` (`helioguard.demo.v4`) | **No implementado** *(backend de dominio)* | En `backend/` no existen entidades de `domain.v4.draft.jdl` ni `/api/telemetria`; en frontend todo corre en `localStorage` | Documentado en `13_ESTADO_ENTREGA_Y_VALIDACIONES.md`; fuera de alcance de esta fase frontend-mock |
| ORIG-14 | Detección por visión artificial / sensores reales ESP32-Raspberry / WebSocket | Ausente en `Idea Original` (solo mención genérica a sensores y "alerta de dron") | Edge simulado en `/telemetria` (`INV-03`, `ESP-03`, `raspi-demo-01`, `edgeBuffer`); sin hardware real ni IA/CV | **No verificable** | No existen especificaciones ni algoritmos en `Idea Original` para hardware real o visión artificial | No inventar fórmulas ni afirmar verificación de hardware/CV |

---

## 3. Hallazgos críticos: brechas, simplificaciones, contradicciones y ampliaciones

### 3.1 Funcionalidades de `Idea Original` perdidas o incompletas en `frontend/helioguard-web`
1. **Motor temporal diurno (`07:00–19:00`)**: `Idea Original/app.py` tenía un lazo vivo de simulación continua (irradiancia solar en campana, temperatura de panel, derating térmico `-0.4 %/°C`, ensuciamiento gradual `-0.012`/paso, limpieza gradual `+0.05`/paso, gráfico real vs. esperada). En `frontend/helioguard-web` quedó reducido a 4 intervalos estáticos (`10:00–10:45`).
2. **Validación eléctrica `V × I` y techo relativo `1.25 · esperada + 0.3`**: En `rules.ts`, `evaluateIngest` solo compara contra un máximo fijo de `115 kW`, dejando pasar inyecciones falsas moderadas.
3. **Detección de replay por stream congelado (`5` pares `(P, V)` idénticos)**: `rules.ts` solo detecta repetición de `eventId` o `sequenceNumber`, no sensores congelados que emiten con `seq` nuevo.
4. **Regla de promedio móvil (`ratio < 0.93` en `>= 6` muestras útiles) y congelamiento de pérdidas ante alerta roja**: presente en `app.py`, ausente en el cálculo de `SECTOR` de `actions.ts`.

### 3.2 Contradicciones y correcciones intencionales documentadas en SDD
- **Termografía satelital individual (`pichoriginal.md:L14-L15`) vs. realidad física (`07_SATELITES_DRONES_LIMITACIONES.md`)**: El propio `pichoriginal.md:L35` reconoce en preguntas del jurado que *"un satélite no ve un panel individual"*. SDD v4 corrigió el alcance: usa datos meteorológicos/irradiancia (`GOES`/`NASA POWER`) y exige etiqueta explícita *"No es una ortofoto ni una termografía de paneles"*.
- **Dron completamente autónomo (`pichoriginal.md:L16`) vs. autorización y seguridad (`07_SATELITES_DRONES_LIMITACIONES.md`)**: SDD v4 prohibió el despegue automático sin revisión de viento, helada, trabajo en techo y permiso humano/regulatorio (ANAC/RAAC).
- **Seguro solar implícito vs. planes de mantenimiento (`09_MODELO_NEGOCIO_AUDITORIA_COBERTURA.md`)**: SDD v4 descarta pólizas de seguro y define planes de mantenimiento con SLA y exclusiones explícitas.
- **Atribución de ciberataques**: SDD v4 (`06_SEGURIDAD_TELEMETRIA_ATAQUES.md`) exige no afirmar *"ataque confirmado"* sin firma criptográfica; la telemetría anómala se marca `SUSPECT`/`QUARANTINED` y abre investigación.

### 3.3 Ampliaciones posteriores de HelioGuard (no presentes en `Idea Original`)
- Modelo de **47 entidades JDL** (`domain.v4.draft.jdl`) y matriz de cobertura 47/47 en `coverage.ts`.
- **Multi-tenancy** con 3 organizaciones (`Andes Solar Jujuy`, `Casa Andina`, `Sol del Norte Salta`), 6 roles RBAC y `InstallationAccessGrant` acotados en tiempo.
- **Resiliencia Edge Local-First** (`edgeBuffer` cuando la WAN cae y drenado idempotente al reconectar).
- **Auditorías de campo pre-servicio**, **Órdenes de trabajo con evidencia antes/después** y **Reglas climáticas del NOA** (Puna, Quebrada, Valles, Yungas, Valles Calchaquíes, Chaco en `ideas/demos/helioguard_lab.html`).
- **Design System Google Stitch** (`hg-*` en `shared/ui` y catálogo `/sistema`).
