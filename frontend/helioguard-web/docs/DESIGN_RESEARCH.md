# Investigación de diseño usada en esta implementación

Se consultaron estas páginas el 22 de noviembre de 2026. No se atribuyen hilos de foros que no se abrieron.

## Fuentes

- W3C, Understanding SC 1.4.1 Use of Color: https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html
- Nielsen Norman Group, Page Laubheimer, «Dashboards: Making Charts and Graphs Easier to Understand» (18 jun 2017): https://www.nngroup.com/articles/dashboards-preattentive/
- Angular, «Using Tailwind CSS with Angular»: https://angular.dev/guide/tailwind
- Dirección local: `ideas/docs/04_DESIGN_SYSTEM_QUEBRADA.md` y `ideas/skills/helioguard-visual-design/SKILL.md`
- r/UXDesign, «Dashboard Design: Focused on Operations?» (15 oct 2025): https://www.reddit.com/r/UXDesign/comments/1o6ye54/dashboard_design_focused_on_operations/ — ordenar atención, contexto y cola de trabajo.
- r/UXDesign, «What important things should I consider in designing an analytics dashboard?» (18 dic 2023): https://www.reddit.com/r/UXDesign/comments/18liurr/what_important_things_should_i_consider_in/ — tres capas: ¿hay que preocuparse?, ¿por qué?, ¿qué se hace?
- r/UXDesign, «live dashboard for executive level» (29 feb 2024): https://www.reddit.com/r/UXDesign/comments/1b2zoml/what_are_the_key_things_to_keep_in_mind_while/ — el número pide un «y ahora qué».
- St. Germain / Hugo / Blanc, «Design Concepts for an Outage Control Center Information Dashboard» (OSTI, 2015): https://www.osti.gov/servlets/purl/1244646 — lo importante arriba, color solo para lo saliente, la pantalla no decide sola.
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
