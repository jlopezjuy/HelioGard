# Reporte de la demo Angular

Fecha de ejecución: 22/11/2026. Máquina local. No hay backend, PostgreSQL, MQTT ni APIs externas.

## Decisiones

- App nueva en `ideas/apps/helioguard-web`. No se modificó `frontend/helioguard-web` ni los documentos del paquete SDD.
- Angular CLI 21.2 dejó el esqueleto (`package.json`, `angular.json`, tsconfig). El resto del código se completó sobre ese proyecto porque un reintento de `ng new` no pudo borrar la carpeta a medias.
- Standalone, rutas lazy, signals, TypeScript strict, Tailwind CSS 4 con los tokens de Quebrada.
- Puerto `DEMO_REPOSITORY` → `DemoStore`. Una implementación HTTP reemplazaría el store, no las pantallas.
- Persistencia en `localStorage` (`helioguard.demo.v4`), con exportar, importar y restablecer. IndexedDB no se usó: el estado cabe en un JSON.
- Tres organizaciones: Andes Solar Jujuy, Familia Andina y Sol del Norte Salta. El grant de la casa es MAINTENANCE.
- IBM Plex local vía Fontsource, OFL 1.1. Iconos Lucide. Sin CDN en runtime.
- No se instalaron skills externas. Se usó `skills/helioguard-visual-design/SKILL.md`.

## Qué hace la demo

Parque cenital de cuatro bloques, escenarios de polvo, nube, inversor, helada, granizo, humo y gateway caído. Incidente, orden, misión con autorización obligatoria, recorrido y verificación. Replay por `deviceId+bootId+sequenceNumber`, idempotencia por `eventId` y valor de 8 MW como inconsistencia física. Auditoría, tarifas ARS, planes versionados y corte de WAN simulado.

## Pruebas ejecutadas

Desde `ideas/apps/helioguard-web`:

| Comando | Resultado |
|---|---|
| `npx ng test --watch=false` | 4 archivos, 21 pruebas, todas verdes. Vitest 4.1.12. Incluye el contraste de los tokens |
| `npx ng build` | Completó en la corrida anterior. Bundle inicial sin comprimir 314,66 kB |
| `CI=1 npx playwright test` | 5 pruebas verdes en Chromium: aislamiento, misión completa hasta RECOVERED, teclado y contraste renderizado, movimiento reducido, capturas |

Las pruebas de negocio cubren Wh contra kW, déficit no comparable, causas que no superan el déficit, nube sin suciedad, replay / duplicado / boot nuevo, 8 MW, salto de misión prohibido, bloqueos de helada, viento, techo y autorización, aislamiento de tenants, replay persistido, recálculo de ARS y contraste AA de los tokens. El ámbar `#9A6E2C` sobre marfil mide 4,12:1 y no se usa como texto chico.

## Capturas

Tomadas por Playwright en la última corrida verde, en 375, 768, 1280 y 1440 px:

- `docs/screenshots/inicio-*.png`
- `docs/screenshots/parque-*.png`
- `docs/screenshots/parque-vuelo-*.png` con la misión en `IN_FLIGHT` y la marca `DR-DEMO-1` dentro del bloque A2
- `docs/screenshots/parque-vuelo-reduced-motion-1440.png`
- `docs/screenshots/integridad-*.png`
- `docs/screenshots/misiones-*.png`
- `docs/screenshots/auditorias-*.png`

En esas capturas no hay scroll horizontal. La leyenda del parque dice «atención» con texto, no solo con color. El chip del dron quedó debajo del déficit de A2 para no tapar el nombre del bloque.

## Pendiente

- El JDL no se validó con el generador JHipster. **NO VERIFICADO.** Sigue fuera de este frontend.
- No hay una pasada con un lector de pantalla real. Playwright comprobó el árbol accesible del parque, el enlace «Saltar al contenido», el foco de 3 px y Tab entre los bloques A1 y A2.
- El contraste se midió con la fórmula WCAG sobre los tokens y sobre el título, el texto secundario y el botón terracota ya renderizados. No se usó una auditoría axe de toda la app.

## Límites que la interfaz repite

No hay termografía satelital de paneles. Una inconsistencia física no es un ataque. La aprobación de la misión no autoriza un vuelo real. Los ARS son ficticios.
