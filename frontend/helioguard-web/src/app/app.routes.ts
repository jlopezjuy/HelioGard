import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { currentRole } from './core/domain/access';
import type { MembershipRole } from './core/domain/model';
import { Shell } from './features/shell/shell';
import { DEMO_REPOSITORY } from './infra/mock/demo-store';

const ALL: MembershipRole[] = ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'];

export const roleGuard: CanActivateFn = (route) => {
  const roles = route.data['roles'] as MembershipRole[] | undefined;
  const role = currentRole(inject(DEMO_REPOSITORY).snapshot());
  if (!roles || (role && roles.includes(role))) {
    return true;
  }
  return inject(Router).createUrlTree(['/inicio'], {
    queryParams: { denegado: route.routeConfig?.path ?? 'ruta' },
  });
};

function guarded(roles: MembershipRole[]) {
  return { canActivate: [roleGuard], data: { roles } };
}

export const routes: Routes = [
  {
    path: 'sistema',
    loadComponent: () => import('./features/catalog/catalog').then((m) => m.CatalogPage),
  },
  {
    path: '',
    component: Shell,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', ...guarded(ALL), loadComponent: () => import('./features/home/home').then((m) => m.HomePage) },
      { path: 'parque', ...guarded(ALL), loadComponent: () => import('./features/park/park').then((m) => m.ParkPage) },
      {
        path: 'instalaciones',
        ...guarded(ALL),
        loadComponent: () => import('./features/installations/installations').then((m) => m.InstallationsPage),
      },
      {
        path: 'instalaciones/:id',
        ...guarded(ALL),
        loadComponent: () => import('./features/installations/installation-detail').then((m) => m.InstallationDetailPage),
      },
      {
        path: 'telemetria',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN']),
        loadComponent: () => import('./features/telemetry/telemetry').then((m) => m.TelemetryPage),
      },
      {
        path: 'energia',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR']),
        loadComponent: () => import('./features/energy/energy').then((m) => m.EnergyPage),
      },
      {
        path: 'incidentes',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR']),
        loadComponent: () => import('./features/incidents/incidents').then((m) => m.IncidentsPage),
      },
      {
        path: 'integridad',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR']),
        loadComponent: () => import('./features/integrity/integrity').then((m) => m.IntegrityPage),
      },
      {
        path: 'fuentes',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR']),
        loadComponent: () => import('./features/sources/sources').then((m) => m.SourcesPage),
      },
      {
        path: 'misiones',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN']),
        loadComponent: () => import('./features/missions/missions').then((m) => m.MissionsPage),
      },
      {
        path: 'auditorias',
        ...guarded(['OWNER', 'ADMIN', 'AUDITOR', 'TECHNICIAN']),
        loadComponent: () => import('./features/audits/audits').then((m) => m.AuditsPage),
      },
      {
        path: 'mantenimiento',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN']),
        loadComponent: () => import('./features/maintenance/maintenance').then((m) => m.MaintenancePage),
      },
      {
        path: 'economia',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR']),
        loadComponent: () => import('./features/economy/economy').then((m) => m.EconomyPage),
      },
      {
        path: 'planes',
        ...guarded(['OWNER', 'ADMIN']),
        loadComponent: () => import('./features/plans/plans').then((m) => m.PlansPage),
      },
      {
        path: 'organizaciones',
        ...guarded(['OWNER', 'ADMIN']),
        loadComponent: () => import('./features/organizations/organizations').then((m) => m.OrganizationsPage),
      },
      {
        path: 'configuracion',
        ...guarded(['OWNER', 'ADMIN', 'OPERATOR']),
        loadComponent: () => import('./features/settings/settings').then((m) => m.SettingsPage),
      },
    ],
  },
];
