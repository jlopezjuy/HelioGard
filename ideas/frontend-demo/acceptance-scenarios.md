# HelioGuard — pruebas de aceptación del frontend sin backend

**Ejecutar con Angular real una vez generado. Estos casos NO fueron ejecutados todavía.** Datos de referencia: `frontend-demo/demo-fixtures.example.json`.

```gherkin
Feature: Aislamiento simulado entre organizaciones
  Given inicio sesión como dueña de Casa Andina
  When ingreso al listado de instalaciones
  Then solo veo las instalaciones de mi organización
  When ingreso manualmente el ID de una instalación de Salta
  Then veo acceso no autorizado (simulación UI)

Feature: Proveedor externo con acceso delegado
  Given soy técnico de Andes Solar Jujuy
  And Casa Andina tiene un InstallationAccessGrant vigente
  When abro la instalación Casa Andina
  Then puedo ver únicamente el alcance MAINTENANCE autorizado
  And no accedo a otras instalaciones de Familia Andina

Feature: Suciedad en parque solar
  Given el estado normal de Puna Norte
  When activo el escenario de suciedad sectorizada
  Then aparece déficit de energía con kWh y período
  And se genera incidente deduplicado
  When elijo dron de limpieza en condiciones seguras
  Then veo despacho y ejecución simulada
  And la validación muestra antes/después y cierra con evidencia

Feature: Nube no es suciedad
  When activo nube pasajera
  Then la producción real cambia
  And no se registra suciedad confirmada ni despacho innecesario

Feature: Helada bloquea limpieza insegura
  Given casa híbrida de Abra Pampa en una mañana bajo cero
  When intento limpiar con agua
  Then el sistema bloquea esa acción y explica el motivo

Feature: Falla de inversor requiere técnico
  When activo falla del inversor
  Then la recomendación es inspección técnica
  And no se propone limpiar como resolución del problema

Feature: Trabajo offline con persistencia
  Given hay una orden en progreso
  When simulo pérdida de internet
  Then puedo seguir completando formularios en la demo
  And la acción persiste al recargar
  When restauro conexión simulada
  Then la cola indica sincronización sin duplicados

Feature: Tarifas en ARS
  Given ingreso precios distintos para autoconsumo e inyección
  When modifico la energía perdida
  Then el valor monetario responde de forma consistente
  And se indica claramente que las tarifas son ficticias
```

## Criterios verificables

- Cambiar de tenant no filtra visualmente después de render: el repositorio mock devuelve solo el ámbito permitido antes del render.
- Los formularios crean datos, validan límites, conservan selección y permiten editar/cancelar.
- Todos los estados de orden de trabajo siguen transiciones coherentes y auditables.
- No se genera éxito automático por click de "validar" sin verificar recuperación simulada.
- El demo no hace peticiones a `localhost:8080`, cloud, Mapbox, Firebase ni APIs satelitales.
- En móviles 375px, no hay tableros ilegibles ni scroll horizontal.


## HelioGuard v4 — integridad y drones

```gherkin
Feature: Integridad de telemetría con eventos simulados
  Scenario: Reenviar lectura con misma secuencia
    Given una lectura aceptada de inverter-03 con bootId A y sequenceNumber 1542
    When llega una lectura nueva con eventId distinto pero el mismo bootId y sequenceNumber
    Then no aumenta el contador de mediciones válidas
    And se registra un TelemetryIntegrityEvent de tipo REPLAY

  Scenario: Valor físicamente inconsistente
    Given un límite físico modelado con incertidumbre
    When se informa potencia fuera de ese límite
    Then el dato se marca SUSPECT
    And se crea un caso UNDER_REVIEW
    And no se presenta como ataque confirmado

Feature: Operaciones de drones simuladas
  Scenario: Rechazar despacho por falta de autorización
    Given una misión de limpieza creada
    And el checklist está completo pero falta aprobación del operador
    When el usuario pide despachar
    Then la misión permanece AWAITING_AUTHORIZATION
    And no comienza animación de vuelo
  Scenario: Validar recuperación
    Given una misión autorizada y completada en demo
    When se ingresa resultado de nueva producción normalizada
    Then se guarda VerificationResult con método, tiempo y evidencia
```
