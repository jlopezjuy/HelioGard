# Informe de la demo Angular

Fecha: 22 de noviembre de 2026. App: `frontend/helioguard-web`. No hay backend, `HttpClient` ni llamadas a `localhost:8080`.

## Decisiones

- El proyecto vive en `frontend/helioguard-web` porque `frontend/` estaba vacío. No se tocó otro dashboard.
- Angular CLI 21 generó el esqueleto (`@angular/cli` local 21.2.30). El CLI global de la máquina es 22.3.0 y no se usó para regenerar el proyecto.
- Tailwind 4 se configuró a mano con `.postcssrc.json`, como documenta Angular: https://angular.dev/guide/tailwind
- Persistencia en `localStorage` (`helioguard.demo.v4`). El volumen es chico; no se usó IndexedDB.
- IBM Plex no se empaqueta. La interfaz usa la pila del sistema. Iconos: `@lucide/angular` 1.77.0, ISC. `lucide-angular` 1.0.0 estaba deprecado y se reemplazó.
- `Site.climateZone` es una anotación de demo. No está en el JDL.
- El reloj de la demo es un ISO UTC dentro del snapshot. La pantalla lo muestra en `America/Argentina/Jujuy`.
- Las reglas de Wh, dedup, causas, misión y tenant están en funciones puras. El store solo persiste el resultado.

## Comandos ejecutados

`npx ng build` (producción), 22 nov 2026, exit 0. Initial total 386,28 kB (transferencia estimada 92,86 kB).

`npx ng test --watch=false`, Vitest 4.1.12, exit 0. 1 archivo, 12 tests, todos pasaron. Cubren kWh, déficit no comparable, ARS 697, causas, los escenarios inversor, nube, helada, granizo y humo, replay exacto, misma secuencia, boot nuevo, valor de 8 MW, corte de WAN, bloqueo de misión, tenant y las 47 entidades con fixture.

`npx playwright test`, exit 0. 1 spec, Chromium, contra `ng serve` en `127.0.0.1:4310`. Recorre polvo, helada, la cadena de misión hasta verificación, replay, inyección sin afirmar ataque, restablecer y el técnico en `/economia`.

## Recorrido en navegador

Con `npx ng serve --host 127.0.0.1 --port 4200`:

- `/inicio` mostró Puna Norte en régimen y luego, con Sector sucio, 4,25 kWh y $ 697,00.
- Crear misión dejó el aviso «Incident → WorkOrder → DroneMission en DRAFT» y el incidente abierto.
- `/parque` mostró el bloque B2 con trama y el texto «Suciedad sospechada», 7,75 kWh / 12 kWh.
- Los recursos de red observados en la página no salieron de `127.0.0.1`.

La captura `docs/screenshots/parque-1440.png` la tomó ese spec a 1440×900, con B2 en polvo. También están `parque-375.png`, `parque-768.png` y `parque-1280.png`. La primera versión del mapa dejaba un vacío bajo los bloques y en 375 el encabezado se comprimía a una letra de ancho; ambas cosas se corrigieron antes de las capturas finales.

## Auditoría visual

- El inicio no abre con cuatro KPI. La pregunta es si el bloque rinde.
- El estado del bloque lleva texto, no solo color.
- El gráfico de energía usa barras, eje de hora y unidad Wh.
- El ámbar `#9A6E2C` no se usa como texto (contraste calculado 4,12 sobre marfil).
- En 375 el dock de la demo se desplaza en horizontal. La barra lateral queda detrás del botón de menú.
- El dock de escritorio empieza a la derecha de la barra. En la captura de 1440 se lee Configuración.

## Pendiente

- No hay IndexedDB.
- La firma criptográfica no se simula como verdadera: toda lectura dice que no está criptográficamente verificada.
- El JDL sigue en borrador. Esta app no lo validó con JHipster.
