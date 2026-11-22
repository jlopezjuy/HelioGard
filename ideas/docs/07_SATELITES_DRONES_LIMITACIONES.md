# Fuentes satelitales, clima y operación de drones — afirmaciones permitidas

## 1. Qué datos ayudan realmente

| Fuente | Se puede demostrar | No se debe afirmar |
|---|---|---|
| Servicios de irradiancia satelital / modelos meteorológicos | irradiancia estimada, nubes y condiciones externas para referencia de producción | medición instantánea exacta en cada panel o prueba de integridad criptográfica |
| Sentinel-2 MSI | imágenes ópticas multiespectrales (10/20/60 m) para contexto espacial según producto/nubosidad | mapa térmico infrarrojo individual de módulos |
| GOES ABI | meteorología y nubes con frecuencia temporal elevada y resolución ~0,5–2 km según canal | identificar un string o panel desde espacio |
| Telemetría por inversor / string | producción eléctrica y fallas por circuito si disponible | saber cuál panel está sucio si sólo existe potencia total |
| Dron con cámara IR apta | localizar anomalías térmicas con metodología y georreferenciación adecuadas | inferir automáticamente suciedad de un hotspot sin inspección adicional |
| Foto del técnico/sensor de suciedad | evidencia local para confirmar suciedad | inferir automáticamente pérdida exacta sin modelo/comparación |

**Nunca:** «satélite entrega un mapa térmico por pase de los paneles», «satélite certifica que nos están hackeando», «dron despega siempre sin operador».

## 2. Correlación y localización

1. Obtener instalación georreferenciada y configuración nominal (kWp, tilt, azimuth, inversores, strings).
2. Obtener irradiancia/meteo externa con timestamp, frecuencia, resolución y licencia guardados en `EvidenceSource` / `DataProvenance`.
3. Calcular esperado bajo condiciones reales + cobertura/calidad del modelo; contrastar medición real.
4. Revisar alarmas inverter y distribución por strings. Zona sospechosa puede ser bloque, no coordenada de panel único.
5. Si la evidencia es insuficiente, proponer inspección visual/térmica; documentar resultado y actualizar diagnóstico.

## 3. Condiciones para dron

- Diferenciar misión de inspección vs limpieza; la primera puede ser suficiente.
- Secuencia: `DRAFT → SAFETY_REVIEW → AWAITING_AUTHORIZATION → AUTHORIZED → DISPATCHED → IN_FLIGHT → COMPLETED/ABORTED` con controles de transición desde backend.
- Autorizar sólo con operador habilitado, aeronave apta, clima y visibilidad dentro de límites, accesos, permisos aplicables, fabricante del módulo y método de limpieza validado.
- No operar cerca de partes eléctricas expuestas, con viento excesivo, bajo granizo, con hielo adherido ni con métodos de agua química incompatibles.
- Dron agrícola reacondicionado no equivale a dron limpiador certificado. La presión del agua y el remanente de químicos pueden dañar módulos.
- El ROI incluye energía recuperable, acceso, agua/seguridad/operador/costo/recuperación. La automatización **coordina**, no sustituye criterio humano.

## 4. Contexto del NOA

- Puna/Abra Pampa: heladas, amplitud térmica, viento, polvo, altitud, Voc del array a bajas temperaturas y protección de baterías.
- Quebrada: polvo, sombras de montaña y heladas; umbrales personalizados por instalación.
- Valles/San Salvador/Salta: lluvia de verano, granizo, tormentas y riesgos de sobretensión.
- Yungas/Orán: humedad, nubes y corrosión; no presumir suciedad por nubosidad.
- Valles Calchaquíes: polvo/viento y variabilidad térmica.
- Chaco salteño: calor, humo, incendios y episodios de polvo.

## 5. Referencias para verificar antes de desarrollo real

- ESA Sentinel-2: https://www.esa.int/Applications/Observing_the_Earth/Copernicus/Sentinel-2/Facts_and_figures
- NOAA ABI: https://www.nesdis.noaa.gov/our-satellites/currently-flying/goes-east-west/abi-scan-mode-information-and-channel-summary
- NASA POWER: https://power.larc.nasa.gov/
- pvlib: https://pvlib-python.readthedocs.io/
- RdTools: https://rdtools.readthedocs.io/
- ANAC Argentina: https://www.argentina.gob.ar/anac (consultar RAAC y requisitos aplicables vigentes para la operación concreta)
- Normas IEC 61724-1 (rendimiento), IEC 62446-1 (inspección), IEC TS 62446-3 (IR): comprobar edición vigente y acceso legal.

No suponer que una API externa es gratuita comercialmente; licencias, cuotas, precisión, cobertura y disponibilidad se validan por proveedor.
