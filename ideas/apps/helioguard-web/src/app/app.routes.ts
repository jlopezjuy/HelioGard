import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { roleAllows } from './core/domain/access';
import { DemoStore } from './infra/mock/demo-store';

const allow: CanActivateFn = (route) => {
  const store = inject(DemoStore);
  const router = inject(Router);
  const path = '/' + (route.routeConfig?.path ?? '').split('/')[0];
  if (roleAllows(path, store.role())) {
    return true;
  }
  return router.createUrlTree(['/denegado'], { queryParams: { desde: path } });
};

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/shell/shell').then((m) => m.Shell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', canActivate: [allow], loadComponent: () => import('./features/home/home').then((m) => m.Home) },
      { path: 'parque', canActivate: [allow], loadComponent: () => import('./features/park/park').then((m) => m.Park) },
      {
        path: 'instalaciones',
        canActivate: [allow],
        loadComponent: () => import('./features/installations/installations').then((m) => m.Installations),
      },
      {
        path: 'instalaciones/:id',
        canActivate: [allow],
        loadComponent: () => import('./features/installations/installation-detail').then((m) => m.InstallationDetail),
      },
      {
        path: 'telemetria',
        canActivate: [allow],
        loadComponent: () => import('./features/telemetry/telemetry').then((m) => m.Telemetry),
      },
      { path: 'energia', canActivate: [allow], loadComponent: () => import('./features/energy/energy').then((m) => m.Energy) },
      {
        path: 'integridad',
        canActivate: [allow],
        loadComponent: () => import('./features/integrity/integrity').then((m) => m.Integrity),
      },
      { path: 'fuentes', canActivate: [allow], loadComponent: () => import('./features/sources/sources').then((m) => m.Sources) },
      {
        path: 'incidentes',
        canActivate: [allow],
        loadComponent: () => import('./features/incidents/incidents').then((m) => m.Incidents),
      },
      { path: 'misiones', canActivate: [allow], loadComponent: () => import('./features/missions/missions').then((m) => m.Missions) },
      { path: 'auditorias', canActivate: [allow], loadComponent: () => import('./features/audits/audits').then((m) => m.Audits) },
      {
        path: 'mantenimiento',
        canActivate: [allow],
        loadComponent: () => import('./features/maintenance/maintenance').then((m) => m.Maintenance),
      },
      { path: 'economia', canActivate: [allow], loadComponent: () => import('./features/economy/economy').then((m) => m.Economy) },
      { path: 'planes', canActivate: [allow], loadComponent: () => import('./features/plans/plans').then((m) => m.Plans) },
      {
        path: 'organizaciones',
        canActivate: [allow],
        loadComponent: () => import('./features/organizations/organizations').then((m) => m.Organizations),
      },
      {
        path: 'configuracion',
        canActivate: [allow],
        loadComponent: () => import('./features/settings/settings').then((m) => m.Settings),
      },
      { path: 'denegado', loadComponent: () => import('./features/denied/denied').then((m) => m.Denied) },
    ],
  },
  { path: '**', redirectTo: 'inicio' },
];
