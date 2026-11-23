# HelioGuard web

Prototipo Angular standalone para la demo de hackatón. Corre sin backend, sin API y sin red en tiempo de ejecución. Los datos son fixtures deterministas (`helioguard-hackaton-v4`) guardados en `localStorage`.

El JDL de `ideas/specs/domain.v4.draft.jdl` es un borrador. Esta app no afirma que exista un backend generado.

## Ejecutar

```bash
cd frontend/helioguard-web
npm install
npm start
```

Abrí `http://127.0.0.1:4200`. No hace falta PostgreSQL, Docker ni un servidor en el puerto 8080.

```bash
npm test
npm run build
```

## Demo de 2 minutos

1. Inicio: Puna Norte en régimen simulado.
2. **Sector sucio**: el bloque B2 cae, aparece el déficit en Wh y el monto ARS de demostración.
3. **Crear misión**, **Revisar seguridad**, **Aprobar**, **Despachar**, **Animar dron**, **Verificar**.
4. **Evento original** y **Replay exacto**: la segunda lectura se retiene.
5. **Inyección falsa**: queda `UNDER_REVIEW`, sin afirmar un ataque.
6. **Restablecer demo** vuelve a la semilla, también con la WAN cortada o la misión a medias.

La aprobación de la misión dice en pantalla que no es un permiso operativo real. La app no controla un dron.

## Arquitectura

- `src/app/core/domain`: reglas puras (Wh, dedup, misión, acceso).
- `src/app/infra/mock`: semilla, `localStorage` y `DemoStore`.
- `src/app/features`: pantallas lazy.
- Un reemplazo HTTP debería implementar el mismo flujo de `actions.ts` sin reescribir las pantallas. Hoy no hay `HttpClient`.

## Tipografía e iconos

IBM Plex no se empaqueta: la licencia OFL permite redistribuirlo, pero esta demo usa la pila del sistema para no descargar fuentes en runtime. Los iconos salen de `@lucide/angular` (ISC), instalados en local.
