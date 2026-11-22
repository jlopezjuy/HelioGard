# CLI oficiales y despliegue Docker — HelioGuard v4

**Este documento describe comandos a ejecutar por el implementador; no se han ejecutado aquí los generadores ni builds.** Usar versiones compatibles verificadas en el equipo y fijarlas en `package-lock.json`, `package.json`, FVM y Dockerfiles. Inspeccionar antes el dashboard y repositorios existentes.

## 1. Preflight

```bash
node --version && npm --version
java -version
fvm --version && fvm flutter --version
docker version && docker compose version
# Si no existe herramienta, instalar siguiendo proveedor oficial y registrar versión.
```

## 2. Hackatón: Angular standalone + Tailwind, sin backend

Ejecutar desde raíz del repositorio real. Si ya existe Angular, **NO crear un segundo proyecto**; preservar rutas, pantalla y componentes existentes, introducir repositorios mock intercambiables.

```bash
# Solo cuando NO exista proyecto Angular.
npx @angular/cli@latest new helioguard-web \
  --directory=apps/helioguard-web \
  --routing --standalone --style=css --strict --skip-git
cd apps/helioguard-web
npx ng add tailwindcss
npx ng generate component features/operations/pages/control-center
npx ng generate service core/data/mock-installation-repository
npx ng build
npx ng test --watch=false
```

Instalación oficial Angular/Tailwind: https://angular.dev/guide/tailwind. El proyecto debe arrancar con `npx ng serve --host 127.0.0.1 --port 4200` y funcionar con **cero endpoints cloud**. Para portabilidad las ilustraciones y tipografía pueden quedar empaquetadas, sin CDNs en runtime.

## 3. Backend central JHipster (después de S0.5)

```bash
# A modo de ejemplo con versión acordada por el equipo.
npm install -g generator-jhipster@9.4.0
mkdir -p apps/helioguard-api
cd apps/helioguard-api
jhipster --skip-client
# Configuración guía: monolith / Java 21 / PostgreSQL / Liquibase / auth definida por ADR.
# ONLY después de revisar el JDL en entorno de prueba:
jhipster jdl ../../specs/domain.v4.draft.jdl --json-only
# Inspeccionar .jhipster/*.json generados y luego generar entidades/migraciones en sandbox.
./mvnw test
```

El comando `--json-only` no valida por sí solo 3FN, RLS ni relaciones cross-tenant. Puede fallar con versiones del generador; corregir errores con pruebas, no editar a ciegas. No exponer los endpoints JPA generados globalmente.

## 4. Flutter mediante FVM

```bash
mkdir -p apps/helioguard-mobile && cd apps/helioguard-mobile
fvm install stable
fvm use stable
fvm flutter create --project-name helioguard_mobile --org ar.helioguard --platforms=android,ios .
fvm flutter pub add flutter_riverpod go_router dio drift path_provider
fvm flutter pub add --dev build_runner drift_dev
fvm flutter analyze
fvm flutter test
```

Fijar versión de Flutter de `fvm_config.json` antes de CI. Flutter se instala en dispositivo/emulador con `fvm flutter run`; **Docker no ejecuta la UI Android/iOS**.

## 5. Raspberry Pi / ESP32

```bash
# ESP-IDF ya instalado y versionado según hardware:
idf.py create-project helioguard-sensor
cd helioguard-sensor
idf.py set-target esp32
idf.py build
# Para flashear: requiere conexión física y permisos, no hacerlo sin hardware validado.
```

Raspberry: servicio edge para Modbus/API local, SQLite/outbox, MQTT broker o cliente, sincronización HTTPS. Verificar imágenes ARM64 y logs; no montar un backend JHipster completo en Pi sólo para demo.

## 6. Docker Compose

```bash
cp infra/.env.example infra/.env
# Configurar secretos reales fuera del repositorio; infra/.env no se versiona.
docker compose --env-file infra/.env -f infra/compose.infra.yaml config
docker compose --env-file infra/.env -f infra/compose.infra.yaml up -d
# Ejecutar cuando las imágenes edge/cloud realmente existan:
docker compose --env-file infra/.env -f infra/compose.edge.example.yaml config
docker compose --env-file infra/.env -f infra/compose.cloud.example.yaml config
# Demo web opcional una vez construido el proyecto Angular:
docker compose -f infra/compose.web-demo.example.yaml up --build
```

**Plantillas no son infraestructura lista:** componer config puede requerir variables APP_VERSION/SECRET, imágenes aún no construidas y Dockerfile Angular generado. No publicar PostgreSQL, Modbus, MQTT ni dispositivos a internet. TLS externo, auth por gateway y backup/restore comprobado antes de producción.

## 7. Segregación entornos

- DEV: DB/MQTT local y generador sintético, nunca datos de operación en abierto.
- EDGE: lectura real solo autorizada, datos offline, broker con ACL, acceso LAN autenticado, sin Internet público sobre Modbus.
- CLOUD: JHipster + PostgreSQL + worker energía + Angular, proxy TLS y monitoreo de seguridad, backups.
- MOBILE: Flutter compilado con CLI/FVM; sus APIs se adaptan a LAN/cloud sin confiar en simulación de roles.

## 8. Validaciones no optativas

- CLI: `jhipster jdl` real sobre dominio v4, migrations SQL y `./mvnw test`.
- Angular: `ng build`, `ng test`, E2E y QA visual.
- Flutter: `flutter analyze`, `flutter test`, pruebas dispositivos/offline.
- Docker: `compose config`, healthchecks y restauración de backup.
- Segurança: flujos cross-tenant negativos y replay/seq anormal reproducible.
