# Investigación de diseño usada en esta implementación

Se consultaron estas páginas el 22 de noviembre de 2026. No se atribuyen hilos de foros que no se abrieron.

## Fuentes

- W3C, Understanding SC 1.4.1 Use of Color: https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html
- Nielsen Norman Group, Page Laubheimer, «Dashboards: Making Charts and Graphs Easier to Understand» (18 jun 2017): https://www.nngroup.com/articles/dashboards-preattentive/
- Angular, «Using Tailwind CSS with Angular»: https://angular.dev/guide/tailwind
- Dirección local: `ideas/docs/04_DESIGN_SYSTEM_QUEBRADA.md` y `ideas/skills/helioguard-visual-design/SKILL.md`
- Iconos: `@lucide/angular` 1.77.0, licencia ISC, leída en el `README.md` del paquete instalado. https://lucide.dev/license

## Antipatrones que se evitaron

1. **Color como único estado.** WCAG 1.4.1 pide texto, forma o patrón además del color. En el parque, la suciedad usa trama y la palabra «Suciedad sospechada».
2. **Tablero de tarjetas iguales.** NN/g define el dashboard operativo como una vista que responde rápido una pregunta («¿me quedo sin combustible?»), no como un portal de módulos. El inicio abre con la frase de la planta y una sola acción, «Abrir el parque».
3. **Tortas, donas y medidores** para comparar cantidades. NN/g desaconseja codificar magnitud con área o ángulo. El gráfico de energía usa barras (longitud) con ejes «Wh» y «hora local».
4. **Color para expresar magnitud.** NN/g indica que el matiz no tiene orden cuantitativo. Esperado y real se distinguen por posición y por la leyenda textual.
5. **Gráficos 3D.** NN/g muestra que la perspectiva deforma el valor. No hay gráficos 3D.

## Contraste calculado en esta sesión

Relación de contraste (fórmula WCAG), no una medición de laboratorio con pantalla calibrada:

| Par | Relación |
|---|---|
| `#8F432E` sobre `#F7F4EE` | 6.35 |
| `#315D57` sobre `#F7F4EE` | 6.75 |
| `#24302D` sobre `#F7F4EE` | 12.46 |
| `#FFFFFF` sobre `#8F432E` | 6.98 |
| `#9A6E2C` sobre `#F7F4EE` | 4.12 |

El ámbar no se usa como texto normal. El botón principal usa blanco sobre terracota.

## Qué no se instaló

No se agregó una skill externa de UI. La skill local de HelioGuard alcanza y no requiere ejecutar instaladores de terceros.
