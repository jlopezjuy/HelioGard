import { NgTemplateOutlet } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import {
  LucideActivity,
  LucideBell,
  LucideBuilding,
  LucideClipboardCheck,
  LucideDrone,
  LucideFactory,
  LucideFileSearch,
  LucideLayers,
  LucideMap,
  LucideMenu,
  LucideRadio,
  LucideSettings,
  LucideShield,
  LucideSun,
  LucideTriangleAlert,
  LucideWallet,
  LucideWrench,
} from '@lucide/angular';
import { HgAlert } from '../../shared/ui/feedback';
import { HgButton } from '../../shared/ui/button';
import { HgDrawer } from '../../shared/ui/overlays';
import { HgBrand, HgNavPill, type HgNavItem } from '../../shared/ui/navigation';
import { currentRole } from '../../core/domain/access';
import type { MembershipRole } from '../../core/domain/model';
import { formatLocal } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';

interface NavItem {
  path: string;
  label: string;
  roles: MembershipRole[];
}

interface NavGroup {
  label: string;
  items: NavItem[];
}

@Component({
  selector: 'app-shell',
  imports: [
    NgTemplateOutlet,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    HgAlert,
    HgBrand,
    HgButton,
    HgDrawer,
    HgNavPill,
    LucideBell,
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
  ],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly menuOpen = signal(false);
  private readonly nav: NavGroup[] = [
    {
      label: 'Operar',
      items: [
        { path: '/inicio', label: 'Inicio', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
        { path: '/parque', label: 'Parque', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
        { path: '/instalaciones', label: 'Instalaciones', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR', 'VIEWER'] },
        { path: '/incidentes', label: 'Incidentes', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN', 'AUDITOR'] },
        { path: '/misiones', label: 'Misiones', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
        { path: '/mantenimiento', label: 'Mantenimiento', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
      ],
    },
    {
      label: 'Medir',
      items: [
        { path: '/telemetria', label: 'Telemetría', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'TECHNICIAN'] },
        { path: '/energia', label: 'Energía', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
        { path: '/economia', label: 'Economía', roles: ['OWNER', 'ADMIN', 'OPERATOR'] },
      ],
    },
    {
      label: 'Confiar',
      items: [
        { path: '/integridad', label: 'Integridad', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
        { path: '/fuentes', label: 'Fuentes', roles: ['OWNER', 'ADMIN', 'OPERATOR', 'AUDITOR'] },
        { path: '/auditorias', label: 'Auditorías', roles: ['OWNER', 'ADMIN', 'AUDITOR', 'TECHNICIAN'] },
      ],
    },
    {
      label: 'Cuenta',
      items: [
        { path: '/planes', label: 'Planes', roles: ['OWNER', 'ADMIN'] },
        { path: '/organizaciones', label: 'Organizaciones', roles: ['OWNER', 'ADMIN'] },
        { path: '/configuracion', label: 'Configuración', roles: ['OWNER', 'ADMIN', 'OPERATOR'] },
      ],
    },
  ];
  protected readonly visibleNav = computed(() => {
    const role = currentRole(this.store.snapshot());
    return this.nav
      .map((group) => ({ label: group.label, items: group.items.filter((item) => role && item.roles.includes(role)) }))
      .filter((group) => group.items.length > 0);
  });
  protected readonly userName = computed(() => this.store.snapshot().users.find((user) => user.id === this.store.snapshot().session.userId)?.name ?? '');
  protected readonly orgName = computed(() => this.store.snapshot().organizations.find((org) => org.id === this.store.snapshot().session.organizationId)?.name ?? '');
  protected readonly role = computed(() => currentRole(this.store.snapshot()));
  protected readonly clock = computed(() => formatLocal(this.store.snapshot().clockIso, 'America/Argentina/Jujuy'));
  protected readonly offline = computed(() => this.store.snapshot().scenario.wanOffline);
  protected readonly users = computed(() => this.store.snapshot().users);
  protected readonly pillItems = computed<HgNavItem[]>(() =>
    this.visibleNav().flatMap((group) => group.items.map((item) => ({ id: item.path, label: item.label, href: item.path }))),
  );
  protected readonly mobileTabs = computed(() => {
    const preferred = ['/inicio', '/parque', '/telemetria', '/incidentes'];
    const items = this.visibleNav().flatMap((group) => group.items);
    return preferred.flatMap((path) => {
      const item = items.find((entry) => entry.path === path);
      return item ? [item] : [];
    });
  });
  protected readonly canConfigure = computed(() => this.pillItems().some((item) => item.href === '/configuracion'));
  protected readonly initials = computed(() =>
    this.userName()
      .split(' ')
      .slice(0, 2)
      .map((part) => part[0] ?? '')
      .join('')
      .toUpperCase(),
  );

  protected close(): void {
    this.menuOpen.set(false);
  }

  protected reset(): void {
    this.store.reset();
  }
}
