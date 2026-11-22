# HelioGuard

Telemetría solar en la que se puede confiar. Muestra la producción de una instalación solar y detecta cuándo los datos fallan o están manipulados.

Los datos son simulados. Proyecto para el Hackatón Tren Solar (energías renovables y telemetría).

## Detectores
- **Dato imposible:** la potencia no coincide con tensión × corriente, o supera lo físicamente posible.
- **Posible replay:** 5 lecturas idénticas seguidas (datos congelados o repetidos).
- **Rendimiento bajo:** produce menos del 93% de lo esperado (probable suciedad).

## Versión con backend (Python + Flask)
```bash
pip install -r requirements.txt
python app.py
```
Abrir http://localhost:5000

## Versión solo navegador (sin servidor)
Abrir `docs/index.html` con doble clic. Funciona sin internet.
Para publicarla: Settings > Pages > Branch `main` > carpeta `/docs`.

## Archivos
- `app.py`: backend Flask (simulador, detectores, API).
- `index.html`: frontend que consume la API.
- `docs/index.html`: versión autónoma que corre toda en el navegador.

## API
- `GET /api/telemetria`: siguiente lectura, estado y alertas.
- `POST /api/modo` con `{"modo": "normal|suciedad|replay|inyeccion"}`.
- `POST /api/reiniciar`: reinicia el día simulado.
