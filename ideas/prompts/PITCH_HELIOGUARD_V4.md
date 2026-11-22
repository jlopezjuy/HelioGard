# Pitch HelioGuard — guion verificable (demo hackatón)

## Pitch de ~2 minutos (ensayar con tiempo real)

**Problema.** «En Jujuy tenemos un recurso solar excepcional. Pero una planta solo genera valor cuando produce y sus mediciones son confiables. Suciedad, fallas eléctricas o datos inconsistentes pueden ocultar pérdidas de energía y de dinero».

**Solución.** «HelioGuard compara telemetría con un modelo de producción esperado alimentado por datos meteorológicos externos. Cuando encuentra diferencias, verifica la calidad de los datos, localiza dónde investigar y coordina una respuesta».

**Demo.** «Lo que van a ver ahora es un prototipo con datos simulados. Primero, la planta funciona normalmente. Activo ‘Sector sucio’: cae la producción, se identifica el inversor o string sospechoso y se estima el impacto económico. A continuación HelioGuard genera una misión de mantenimiento; se revisan condiciones de seguridad y, en la simulación, un dron llega y limpia. Luego verificamos si mejoró la producción. Pero una pérdida también puede quedar oculta detrás de datos incorrectos. Reenvío la misma lectura: HelioGuard detecta el evento/contador de secuencia duplicado y evita usarlo de nuevo. Si inyecto un valor imposible para las condiciones modeladas, no afirma que hubo un ataque: lo marca como sospechoso y abre una investigación».

**Negocio.** «Cobramos una suscripción de monitoreo por instalación, con planes para empresas instaladoras, cooperativas y parques solares. La auditoría inicial ayuda a conocer el riesgo; el seguimiento continuo genera valor recurrente».

**Cierre.** «Hoy validamos el flujo con datos simulados. El siguiente paso es conectarlo a inversores, servicios meteorológicos y operadores de drones autorizados. HelioGuard: energía renovable con una capa de confianza. Somos Alpha Digital y nuestro equipo. Gracias».

## Pitch de ~30 segundos

«En Jujuy podemos generar mucha energía solar, pero ¿cómo verificamos que una planta produzca lo esperado y que sus datos sean confiables? HelioGuard combina telemetría y estimaciones meteorológicas externas para detectar pérdidas, señalar mediciones sospechosas y coordinar una intervención segura con técnicos o drones. Hoy demostramos el flujo con datos simulados. HelioGuard: energía renovable con una capa de confianza».

## Guion 60 segundos exactos de interacción (objetivo, no garantizado)

| Momento | Control | Qué muestra |
|---|---|---|
| 0–12 s | Planta normal | Esperado, real, fuente y marca `SIMULADO` |
| 12–27 s | Sector sucio | Bloque marcado por datos por string, pérdida y ARS |
| 27–43 s | Proponer/aprobar misión | Dron/operador simulado, seguridad y recuperación |
| 43–60 s | Ataque replay | Secuencia duplicada retenida con evidencia y evento de integridad |

**Plan B:** capturar video local de la ruta si falla navegador/proyector. No depender de internet ni credenciales reales.

## Preguntas del jurado

**¿Un satélite ve un panel?** No. Estima condiciones meteorológicas y aporta contexto; localizar sectores usa telemetría e inspección, y la termografía se obtiene con dron apto.

**¿Demostraron un ataque?** Replay/duplicados pueden detectarse mediante contadores y autenticación. Una inconsistencia física solo abre investigación; no demuestra autor malicioso.

**¿El dron vuela solo?** No en este MVP: es una simulación de misión propuesta y autorizada. Integración real implica operador, regulación y compatibilidad técnica.

**¿Los datos son reales?** No, son sintéticos en esta demo. La validez cuantitativa y ahorros están pendientes de piloto con inversores reales.

**¿Qué los diferencia?** Integrar diagnóstico energético, integridad de telemetría y respuesta operativa sin encerrar al cliente en una sola marca de inversor.

**¿Quién paga?** Operadores, instaladores, mantenedoras y cooperativas; segmentar según escala, servicio/SLA y costo real.

**¿Qué pasa sin internet?** La Raspberry puede seguir registrando y alertando localmente con energía y red LAN disponibles; el VPS sincroniza después.

## Nombre

HelioGuard es nombre de trabajo. Verificar dominio, marca registrada y posibles colisiones en Argentina antes de registrar identidad comercial.
