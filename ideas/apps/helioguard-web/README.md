# HelioGuard web — demo de hackatón

Aplicación Angular standalone, sin backend y sin llamadas de red en tiempo de ejecución. Los datos salen de una semilla determinista (`helioguard-hackaton-v4`) y se guardan en `localStorage`.

## Ejecutar

```bash
cd apps/helioguard-web
npm install
npx ng serve
```

Abrí `http://127.0.0.1:4200`. La persona inicial es Operadora Demo, de Andes Solar Jujuy, sobre Puna Norte.

## Comprobar

```bash
npx ng test --watch=false
npx ng build
npx playwright test
```

Playwright levanta la app en `http://127.0.0.1:4321`. La primera vez hace falta `npx playwright install chromium`.

## Recorrido

1. En Inicio o Parque, **Sector sucio**.
2. En Incidentes, crear la orden. En Misiones, proponer limpieza, revisar seguridad, aprobar, despachar, recorrer y verificar.
3. **Replay** e **Inyección física** en Integridad.
4. **Restablecer demo** vuelve a la semilla.
5. Cambiar a Propietaria Demo oculta Puna Norte y Salta.

El detalle de cobertura, la investigación visual y el reporte de pruebas están en `docs/`.
