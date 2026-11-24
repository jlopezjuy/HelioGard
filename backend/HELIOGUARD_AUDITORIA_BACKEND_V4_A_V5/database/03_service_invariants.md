# Invariantes transaccionales y de negocio fuera de JDL

**No son instrucciones SQL ejecutadas.** Se deben implementar en Spring Boot, migraciones Liquibase y tests.

## Tenant / propietarios / delegación

- Una `Installation` pertenece a una `Organization`. `Site`, `SolarSector`, `SolarArray`, `SolarString`, `Equipment`, `Gateway` deben corresponder al propietario. `organization_id` duplicado por razones de seguridad es **desnormalización física controlada**; exige FKs compuestas, no simples filtros Java.
- `OrganizationMembership` autoriza acciones de la organización activa. `InstallationAccessGrant` concede permisos por instalación; toda solicitud de un prestador debe verificar grant vigente, `ServiceAgreement`, scope y membresía actual.
- `DroneAsset` corresponde al prestador; `DroneMission` pertenece al tenant dueño de instalación. La asignación cross-tenant se autoriza expresamente y se audita.
- Compartir evidencia/fotos/ubicación debe pasar por autorizaciones; URLs de storage firmadas por recurso.
- SQL RLS debe negar acceso sin `app.tenant_id` y no puede confundirse con permisos cross-tenant. Para técnico externo usar servicio bajo rol seguro con políticas específicas o función controlada.

## Topología / adquisición

- `SolarString` pertenece a `SolarArray`; el array puede asociarse a sector georreferenciado. `InverterMpptInput` debe apuntar a `Equipment.kind=INVERTER`. `StringInputAssignment` vigente relaciona ese string con exactamente una entrada compatible según el equipo; no solapar periodos. No inferir panel individual por dato del inversor.
- `TelemetryChannelBinding` versiona qué gateway observa qué canal. `TelemetryChannel` hereda instalación de `Equipment`; gateway y canal deben corresponder a la misma instalación (salvo diseño explícito de gateway compartido, hoy no soportado).
- `EquipmentConnection` origen y destino deben pertenecer a la misma instalación y ser eléctricamente compatibles. No enviar comandos de maniobra desde la app.

## Metereología y energía

- `WeatherIngestionBatch` solo almacena metadatos de importación; registros meteorológicos por intervalo van a tabla SQL con trazabilidad, unidades y calidad. `EvidenceSource.kind=SATELLITE_WEATHER` **no implica termografía de paneles**.
- `EnergyModelConfiguration` es una instantánea inmutable; `EnergyModelRun` referencia versión, dataset y ventana; en `INSUFFICIENT_DATA` es válido que `expectedWh=NULL` (no inventar cero), con nota de calidad.
- `expectedWh` y `actualWh` deben provenir de intervalos compatibles, con suficiente cobertura, no de lecturas instantáneas W. `expectedLowerWh <= expectedWh <= expectedUpperWh` si se conocen todas. Validar cobertura 0..100.
- `LossAssessment` no puede confirmar suciedad por déficit bruto. Suma de hipótesis atribuibles **no debe superar** pérdida total. El residual desconocido debe permanecer visible. El clima ordinario ajusta el esperado y no es pérdida evitable.
- `EconomicAssessment` toma precio vigente y distingue autoconsumo vs exportación; si falta curva de carga se presenta supuesto explícito.

## Alertas y datos sospechosos

- `AlertRule` define la identidad y severidad; `AlertRuleVersion` define fórmula/ventana/cooldown/version. Sólo versiones activas y compatibles pueden evaluar. Diferenciar `OFFLINE_TELEMETRY` vs `INVERTER_FAULT` y amanecer normal vs incidente.
- El receiver canonical usa `(gatewayId,eventId)` como idempotencia. `(deviceUid,bootId,sequenceNumber)` es otro control; retransmisión idéntica puede ser legítima. `TelemetryIntegrityEvent` registra evidencia técnica, no declara automáticamente ataque confirmado.
- `SecurityIncident` agrupa evidencias, actores y decisiones en bitácora append-only. Las claves/secretos se guardan fuera de JPA.

## Auditoría y mantenimiento

- `AuditTemplate` publicado (incluidas sus preguntas) es inmutable, o debe generarse snapshot. `AuditAnswer.auditQuestion` debe pertenecer a la misma versión que `AuditReport.auditTemplate`. Firma sólo si checklist requerido y evidencia cumplen condiciones.
- `MaintenancePlanVersion` publicado es inmutable; `PlanBenefit` detalla prestaciones, cupos y vigencias. `PlanEligibilityAssessment` clasifica evaluación, no vende seguros. `PlanSubscription` conserva monto y versión acordada.
- `WorkOrderAssignment` historial de profesionales/proveedores. No cruzar tenant de un técnico externo sin delegación. `WorkOrderApproval` registra consentimiento cuando se requiere y debe estar válido antes de ejecutar.
- Limpieza por propietario: registrar intervención y bloquear recomendaciones peligrosas (tejado, hielo, incendio o daño eléctrico). La aplicación no certifica seguridad física por sí sola.
- `RecoveryAssessment` y `VerificationResult`: estado `INCONCLUSIVE` si faltan mediciones meteorológicamente comparables, sin cerrar como recuperado.

## Drones

- `DroneMissionTarget` puede representar sector, string o equipo: **exactamente una** referencia según `kind`; sin geometría/telemetría apropiada no se finge geolocalización de panel.
- `MissionSafetyCheck`: lista de verificaciones aprobadas (según fabricante/clima/operador/permiso) con vigencia; nunca usar un chequeo de la demo como autorización física.
- `DroneMission`: solo puede pasar a `DISPATCHED` con autorización válida, piloto, dron disponible, condiciones operativas compatibles y orden/propósito permitido. Debe auditar eventos y rechazos, nunca ejecutar vuelo real desde un endpoint CRUD.

## Simulaciones

- Prototipos Angular se ejecutan en un espacio `SIMULATED` aislado. El backend de producción NO debe consumir sus eventos de diagnóstico, órdenes o drones. Si se persistieran escenarios futuros, crear `simulation` bounded context con claves separadas y sin adaptadores de salida a hardware.
