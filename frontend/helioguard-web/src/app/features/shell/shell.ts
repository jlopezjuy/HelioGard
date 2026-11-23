import { NgClass } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import {
  LucideActivity,
  LucideBuilding,
  LucideClipboardCheck,
  LucideDrone,
  LucideFactory,
  LucideFileSearch,
  LucideLayers,
  LucideMap,
  LucideMenu,
  LucideRadio,
  LucideRotateCcw,
  LucideSettings,
  LucideShield,
  LucideSun,
  LucideTriangleAlert,
  LucideUnplug,
  LucideWallet,
  LucideWrench,
  LucideX,
} from '@lucide/angular';
import { currentRole } from '../../core/domain/access';
import type { MembershipRole } from '../../core/domain/model';
import { formatLocal } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';

interface NavItem {
  path: string;
  label: string;
  roles: MembershipRole[];
}

@Component({
  selector: 'app-shell',
  imports: [
    NgClass,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    LucideSun,
    LucideMap,
    LucideFactory,
    LucideRadio,
    LucideActivity,
    LucideTriangleAlert,
    LucideShield,
    LucideFileSearch,
    LucideDrone,
    LucideClipboardCheck,
    LucideWrench,
    LucideWallet,
    LucideLayers,
    LucideBuilding,
    LucideSettings,
    LucideMenu,
    LucideX,
    LucideRotateCcw,
    LucideUnplug,
  ],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly menuOpen = signal(false);
  protected readonly nav: NavItem[] = [
    { path: '/inicio', label: 'Inicio', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
    { path: '/parque', label: 'Parque', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
    { path: '/instalaciones', label: 'Instalaciones', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
    { path: '/telemetria', label: 'Telemetría', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
    { path: '/energia', label: 'Energía', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
    { path: '/incidentes', label: 'Incidentes', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR'] },
    { path: '/integridad', label: 'Integridad', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
    { path: '/fuentes', label: 'Fuentes', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
    { path: '/misiones', label: 'Misiones', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
    { path: '/auditorias', label: 'Auditorías', roles: ['OWNER', 'ADMIN', 'AUDITOR', 'TECHNICIAN'] },
    { path: '/mantenimiento', label: 'Mantenimiento', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
    { path: '/economia', label: 'Economía', roles: ['OWNER', 'ADMIN', 'OPERATOR'] },
    { path: '/planes', label: 'Planes', roles: ['OWNER', 'ADMIN'] },
    { path: '/organizaciones', label: 'Organizaciones', roles: ['OWNER', 'ADMIN'] },
    { path: '/configuracion', label: 'Configuración', roles: ['OWNER', 'ADMIN', 'OPERATOR'] },
  ];
  protected readonly visibleNav = computed(() => {
    const role = currentRole(this.store.snapshot());
    return this.nav.filter((item) => role && item.roles.includes(role));
  });
  protected readonly userName = computed(() => this.store.snapshot().users.find((user) => user.id === this.store.snapshot().session.userId)?.name ?? '');
  protected readonly orgName = computed(() => this.store.snapshot().organizations.find((org) => org.id === this.store.snapshot().session.organizationId)?.name ?? '');
  protected readonly role = computed(() => currentRole(this.store.snapshot()));
  protected readonly clock = computed(() => formatLocal(this.store.snapshot().clockIso, 'America/Argentina/Jujuy'));
  protected readonly offline = computed(() => this.store.snapshot().scenario.wanOffline);
  protected readonly users = computed(() => this.store.snapshot().users);

  protected close(): void {
    this.menuOpen.set(false);
  }

  protected reset(): void {
    this.store.reset();
  }
}
