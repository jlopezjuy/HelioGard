# HelioGuard — Resolución segura de empresa actual desde usuario autenticado

## Decisión

El patrón del proyecto anterior es válido como idea: usuario logueado -> empresa del usuario -> entidad. Aquí el modelo usa `OrganizationMembership` y `Organization`, no `EmpresaUsuario` y `Empresa`.

**No usar** `.findByMemberIsCurrentUser().stream().findFirst()` en un SaaS multiempresa. El orden de resultados no es contractual; podría escribir en la organización equivocada.

## Comportamiento especificado

```text
JWT verificado -> principal/login
       |
       v
Memberships ACTIVE del usuario (Organization ACTIVE)
       |
       +-- 0: 403 NO_ACTIVE_ORGANIZATION
       +-- 1: organización única (sin parámetro de empresa)
       +-- 2+: claim signed active_org presente y pertenece al usuario?
                  +-- no: 409 TENANT_SELECTION_REQUIRED o 403 INVALID_ACTIVE_ORGANIZATION
                  +-- sí: organización activa
```

Opcional: exigir `active_org` en todos los tokens de usuarios multiempresa y de una sola empresa para simplificar RLS. La selección ocurre tras login o al cambiar empresa, por endpoint especial que **emite un token nuevo**; el frontend no selecciona la empresa mediante headers editables o filtros de recursos.

No hace falta usar `organizationId` en el path de negocio. Para seleccionar otra organización sí se necesita un identificador o slug en la petición de **cambio de contexto**, que el servidor valida y firma; de otro modo la selección multiempresa no es posible.

### Contrato de componente

```java
public interface ActiveOrganizationClaimReader {
    // Lee un claim ya VERIFICADO por la infraestructura JWT del proyecto.
    // Nunca analiza un JWT sin validar firma/issuer/audience/expiración.
    Optional<Long> getActiveOrganizationId();
}

public interface TenantContextService {
    CurrentTenant requireCurrentTenant();
    void requireAccessToInstallation(Long installationId, PermissionScope operation);
}

public record CurrentTenant(Long organizationId, String userLogin, MembershipRole role) {}
```

**Pseudocódigo de servicio** (ajustar al paquete Java real y a cómo JHipster representa el `Authentication`):

```java
@Transactional(readOnly = true)
public CurrentTenant requireCurrentTenant() {
    String login = SecurityUtils.getCurrentUserLogin()
        .orElseThrow(() -> new AccessDeniedException("No autenticado"));

    List<OrganizationMembership> memberships = membershipRepository
        .findActiveMembershipsByUserLogin(login); // método JPA propio

    if (memberships.isEmpty()) {
        throw new AccessDeniedException("Sin empresa activa");
    }

    Optional<Long> claim = activeOrganizationClaimReader.getActiveOrganizationId();
    OrganizationMembership selected;
    if (claim.isPresent()) {
        selected = memberships.stream()
            .filter(m -> m.getOrganization().getId().equals(claim.get()))
            .findAny()
            .orElseThrow(() -> new AccessDeniedException("Empresa no autorizada"));
    } else if (memberships.size() == 1) {
        selected = memberships.get(0);
    } else {
        throw new TenantSelectionRequiredException();
    }

    return new CurrentTenant(
        selected.getOrganization().getId(), login, selected.getRole());
}
```

**Consulta JPA conceptual** (adaptar nombres según entidades generadas):

```java
@Query("""
   select m from OrganizationMembership m
   join fetch m.organization o
   join m.member u
   where u.login = :login
     and m.status = :memberStatus
     and o.status = :orgStatus
   """)
List<OrganizationMembership> findByLoginAndStatus(
    @Param("login") String login,
    @Param("memberStatus") MembershipStatus memberStatus,
    @Param("orgStatus") OrganizationStatus orgStatus);
```

La función `findActiveMembershipsByUserLogin` del pseudocódigo puede implementarse usando la consulta anterior; no asumir que JHipster la genera.

### Ejemplo de creación

```java
@Transactional
public Installation create(CreateInstallationCommand command) {
    CurrentTenant tenant = tenantContext.requireCurrentTenant();
    var site = siteRepository.findByIdAndOrganizationId(
        command.siteId(), tenant.organizationId()
    ).orElseThrow(() -> new AccessDeniedException("Sitio no autorizado"));

    var installation = new Installation();
    installation.setOrganization(site.getOrganization()); // servidor
    installation.setSite(site);
    installation.setName(command.name());
    // Completar campos requeridos del JDL, validaciones, etc.
    return installationRepository.save(installation);
}
```

`CreateInstallationCommand` no contiene `organizationId`. Una petición puede referenciar `siteId` o `installationId` sin convertir esos IDs en permiso.

## Transacciones y RLS

La autorización en Java protege llamadas REST y operaciones de servicio. Si se usa PostgreSQL RLS, establecer tenant transaction-local `SET LOCAL` y limpiar automáticamente al concluir transacción; probar reuso del pool y jobs de sistema. Para recursos delegados entre empresas, las políticas RLS deben tener reglas específicas o emplear procedimientos de alcance controlado; no asumir que igualar `organization_id` al tenant activo cubre colaboración.

## Protección del token

- JWT debe validar firma, caducidad, emisor/audiencia conforme a configuración.
- `active_org` debe ser emitido por backend tras validar membresía, no escrito por Angular/Flutter.
- Validar estado ACTIVE contra base, incluso si el JWT fue emitido antes de una revocación.
- La selección de tenant no es elevación de privilegios; cambiar de organización no cambia roles fuera de los membership autorizados.
- Para máquinas, vinculación del gateway y sus instalaciones en DB, credenciales independientes de usuario humano.

## Pruebas obligatorias

| Escenario | Resultado |
|---|---|
| Usuario una empresa | Organización resuelta desde JWT/login + membership |
| Usuario 2 empresas sin claim | Rechazo seguro, nunca `findFirst` |
| Claim de empresa B sin membership activo | 403 |
| Usuario cambia de A a B legalmente | Nuevo JWT y permisos de B |
| Usuario A busca `installationId` de B | 403 o 404 según política uniforme |
| Membership suspendido durante JWT vigente | Denegación desde DB |
| Técnico A delegado a una instalación de B | Solo instalación autorizada |
| RLS usa conexión reciclada | No hereda tenant anterior |
| Gateway A publica instalación B | Evento rechazado y auditado |
| Cliente añade `organizationId` al DTO | Campo ignorado/rechazado; no cambia la propiedad |
