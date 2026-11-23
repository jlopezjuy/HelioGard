# Investigación visual usada para HelioGuard

Consultada el 22/11/2026. No se instaló ninguna skill externa: la guía aplicada es `ideas/skills/helioguard-visual-design/SKILL.md`. No se ejecutaron instaladores de terceros.

## Antipatrones que se evitaron

1. Tarjetas de igual peso y el mismo layout de hero + tres features. Lo describe un hilo de r/UI_Design sobre interfaces generadas: [What is it about AI-designed interfaces that makes them so obvious?](https://www.reddit.com/r/UI_Design/comments/1xifkp9/what_is_it_about_aidesigned_interfaces_that_makes/). El comentario de u/iBuildRandomStuff señala espaciado uniforme, una sola sans y badges que no significan nada.
2. Gradiente, hero enorme y botones genéricos repetidos entre builders. [AI web builders are ruining the status of design](https://www.reddit.com/r/UI_Design/comments/1nnnd3h/ai_web_builders_are_ruining_the_status_of_design/).
3. Pantallas solo del camino feliz, sin vacío, error o permiso. [I've been trying AI design tools like Lovable/V0 but I struggle with edge cases](https://www.reddit.com/r/UI_Design/comments/1np5zmt/ive_been_trying_ai_design_tools_like_lovablev0/).
4. Jerarquía hecha solo con cards. En r/web_design, u/freew1ll_ recomienda múltiplos de 8 px y quitar cards cuando el espacio ya agrupa: [What are some key guidelines that you personally follow](https://www.reddit.com/r/web_design/comments/1d4q6vk/what_are_some_key_guidelines_that_you_personally/).
5. Dashboards que cuentan quince historias con la misma intensidad. [I did a redesign of a dashboard](https://www.reddit.com/r/web_design/comments/y592om/i_did_a_redesign_of_a_dashboard_i_found_here_on/) y [Dashboard layout ideas for SaaS analytics](https://www.reddit.com/r/web_design/comments/1ry07th/dashboard_layout_ideas_for_saas_analytics_product/).

## Prácticas que sí se aplicaron

- NN/g: la longitud y la posición comunican cantidad mejor que el color. [Dashboards: Making Charts and Graphs Easier to Understand](https://www.nngroup.com/articles/dashboards-preattentive/). Por eso el inicio usa barras de esperado/real y una tabla, no un gauge.
- NN/g: título que dice el hallazgo y color solo en la serie que importa. [Contrast: One of the 3Cs for Better Charts](https://www.nngroup.com/articles/contrast-charts/). El título del inicio cambia con el escenario.
- WCAG 2.2, contraste no textual 3:1. [Understanding 1.4.11](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast). Texto de interfaz en `#24302D` sobre `#F7F4EE`. El ámbar `#9A6E2C` no se usa como texto chico.
- El color no es la única señal. [Charts and graphs, NSW Design System](https://designsystem.nsw.gov.au/docs/content/methods/data-visualisation.html). Cada bloque dice «En banda», «Atención» o «Sin medición», y el de atención además lleva trama.
- Densidad para quien usa la herramienta todos los días, con aire entre grupos. [Information density vs white space](https://www.reddit.com/r/web_design/comments/v1mmy2/information_density_vs_white_space/).

## Decisiones de esta app

- Dirección «Estratos de la Quebrada»: fondo marfil, acción terracota, datos en pizarra, trama de filas de módulos en la vista cenital. Sin postal ni degradado púrpura.
- IBM Plex Sans y IBM Plex Mono entran por `@fontsource/ibm-plex-sans` y `@fontsource/ibm-plex-mono`, licencia SIL OFL 1.1 (Copyright 2019 IBM Corp.). No hay CDN de fuentes.
- Iconos de `@lucide/angular`. No hay emoji en la interfaz.
- El inicio abre con el estado de la instalación y una acción primaria (abrir la vista cenital), no con cuatro KPI.

## Auditoría visual de esta pasada

Capturas reales en `docs/screenshots/` después de corregir un choque de CSS: `.btn` estaba fuera de capa y tapaba `md:hidden`, así que el menú hamburguesa seguía en 1280 y 1440. Con el botón dentro de `@layer components` el escritorio muestra solo la barra lateral.

Quedó visible, y se acepta para la demo: en 375 px la leyenda de tres estados pasa a una columna y el nombre de la organización se corta en la barra. No hay scroll horizontal en la vista del parque. Los botones de escenario miden al menos 44 px de alto.
