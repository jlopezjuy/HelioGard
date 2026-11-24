# Sprint S0.5 Validation & Sprint S1: Multi-tenant JWT Context & Core Access Control

Repo: `/Users/juanlopez/Proyectos/Solar/backend` (branch `develop`)
Scope: Validate S0.5 JDL/Liquibase baseline and implement Sprint S1 SDD (tenant from JWT + `active_org`, `TenantContextService`, `PermissionService`, `Organization`, `OrganizationMembership`, `Site`, `Installation`, `InstallationAccessGrant`, S1 Liquibase constraints, tests T01-T05 + edge cases, and `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`).

## Specs

- `S0.5`: Validate JDL v5 freeze (`domain.v5.review.jdl`, 67 entities, Liquibase migrations, compilation & IT runner) before S1 changes.
- `S1.1` (`RB-TEN-001`): Resolve tenant from authenticated JWT (`login`) -> `OrganizationMembership` `ACTIVE` -> `Organization` `ACTIVE`. 0 active orgs -> 403 `NO_ACTIVE_ORGANIZATION`. 1 active org -> auto-select. 2+ active orgs -> require signed `active_org` claim in JWT, else 409 `TENANT_SELECTION_REQUIRED` / 403 `INVALID_ACTIVE_ORGANIZATION`. Never use `findFirst()`.
- `S1.2`: `POST /api/auth/active-organization` validates `OrganizationMembership` `ACTIVE` + `Organization` `ACTIVE` for the requested organization and issues a new signed JWT with `active_org` claim. Never trust `X-Organization-Id` header. `GET /api/me/context` returns current user, active memberships, and active organization (if resolved).
- `S1.3` (`RB-ID-001`, `RB-ID-002`, `RB-ID-003`): Implement `TenantContextService` and `PermissionService` (own org OR valid non-revoked `InstallationAccessGrant` within `[validFrom, validUntil)`). Protect `Organization`, `OrganizationMembership`, `Site`, `Installation`, `InstallationAccessGrant`: command/create DTOs without editable `organizationId` (server assigns `organization` from `TenantContextService`), `Site` must belong to current org, `nominalDcKw > 0`, valid `gridMode`, unique `code` per tenant, initial status `COMMISSIONING`, list/detail filtered by own org + valid grants, uniform 403/404 on IDOR, forbid generic org reassignment.
- `S1.4`: Incremental Liquibase migration for S1 constraints (`uq_site_pk_tenant`, `uq_installation_pk_tenant`, `fk_installation_site_same_tenant`, `uq_installation_tenant_code`) preserving existing migrations (expand/contract, no DROP).
- `S1.5`: Tests `T01`–`T05` plus multi-org, expired/revoked claim, external collaborator grant, IDOR, and list/batch protection.
- `S1.6`: Create/update `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` with RB-IDs, classes/methods, endpoints, migration, tests, real results, and pending items.

## Tasks

- [x] `T1` (`S0.5`, `inline`): Validate S0.5 compilation, Liquibase, and IT runner | commit: pending
- [x] `T2` (`S1.1`, `S1.2`, `inline`): Implement JWT `active_org` claim reader/issuer, `POST /api/auth/active-organization`, `GET /api/me/context`, `TenantContextService`, `PermissionService` | commit: pending
- [x] `T3` (`S1.3`, `S1.4`, `inline`): Add S1 Liquibase composite/unique constraints and enforce SDD rules on `Organization`, `OrganizationMembership`, `Site`, `Installation`, `InstallationAccessGrant` services/DTOs/resources | commit: pending
- [x] `T4` (`S1.5`, `S1.6`, `inline`): Implement integration/unit tests (T01-T05 + edge cases), run real tests, and write `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md` | commit: pending

## Log

- `L1`: User requested SDD implementation starting with Sprint S1 after validating S0.5 in `/Users/juanlopez/Proyectos/Solar/backend` (note: prompt wrote `ideas/backend`, actual repo is `backend`), with JWT-resolved tenant (`active_org`), `TenantContextService`, `PermissionService`, `POST /api/auth/active-organization`, `GET /api/me/context`, `GET/POST /api/installations`, `GET /api/installations/{installationId}`, tests T01-T05 + IDOR/revoked/multi-org, S1 Liquibase constraints, and `docs/backend-sdd/IMPLEMENTACION_LOGICA_NEGOCIO.md`.
