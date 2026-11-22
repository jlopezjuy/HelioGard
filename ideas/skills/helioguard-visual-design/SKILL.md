---
name: helioguard-visual-design
summary: Guía de diseño de interfaz Angular + Tailwind para HelioGuard, basada en la Quebrada de Humahuaca y una UX industrial verificable.
---

# Skill local: HelioGuard Visual Design

Úsala antes de maquetar, después de cada feature y durante el QA final. Funciona como instrucción portable para asistentes que admiten skills. No requiere instalación externa.

## Leer obligatoriamente
1. `docs/04_DESIGN_SYSTEM_QUEBRADA.md` — paleta, tipografía, anti-slop, jerarquía.
2. `docs/05_FRONTEND_JDL_MATRIX.md` — entidades y flujos relevantes.
3. `docs/03_SPRINTS_FRONTEND_DEMO_F0_F7.md` — entregas funcionales.

## Secuencia de trabajo
1. Derivar del JDL los objetivos del usuario y el flujo principal (no 47 CRUD).
2. Dibujar estructura de navegación + 3 composiciones distintas para: Centro de operaciones, Ficha de instalación y Auditoría. Elegir la más legible con la densidad del contenido real.
3. Definir y verificar tokens de colores y contraste, tipografía, elevaciones mínimas y dimensiones base.
4. Construir estados vacíos, error, éxito, sin internet, sin medición y permiso insuficiente; con acciones reales en el mock.
5. Hacer capturas en 375/768/1280/1440. Auditar con criterios concretos: escaneabilidad, densidad, flujo, acciones, contraste, consistencia, diferencias visuales entre módulos, originalidad.
6. Reparar hallazgos y volver a probar; no aprobar únicamente por apariencia.

## Especificidad de HelioGuard
- En integridad, representar secuencia / evento / estado / evidencia antes que iconos de ciberseguridad decorativos.
- En mapa, poner sello «Vista cenital simulada» y diferenciar irradiancia externa de termografía de dron.
- En misiones, indicar permiso pendiente/aprobado y bloquear animación de despegue sin el checklist.

## Checklist anti-slop
- ¿El usuario detecta el estado de la instalación antes de ver KPI secundarios?
- ¿Existe una única acción primaria coherente por pantalla?
- ¿Las alarmas indican origen, gravedad, tiempo y acción?
- ¿Los gráficos muestran unidades, tiempos, leyenda, datos ausentes?
- ¿Los filtros y tablas funcionan con datos realistas?
- ¿Los estados semánticos se distinguen sin depender solo de color?
- ¿Los módulos tienen una jerarquía propia sin volverse inconsistentes?
- ¿La estética mantiene referencias reales a la Quebrada sin clichés folclóricos?
- ¿El diseño evita exceso de adornos, sombras, tarjetas repetidas, gradientes genéricos y emojis?

## Integración con herramientas externas
Si el agente puede instalar skills, primero buscar sus skills oficiales ya instaladas (ej. Angular), y complementar con esta skill local. Para instalar otra desde internet: verificar autor, licencia, reputación y contenido ejecutable; no ejecutar scripts o instaladores desconocidos, ni conceder accesos innecesarios. Si no puede instalarlas, seguir esta skill local y reportar la limitación, **sin detener el proyecto**.
