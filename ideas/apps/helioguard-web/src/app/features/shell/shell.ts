import { Component, computed, inject, signal } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import {
  LucideActivity,
  LucideBuilding2,
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
import { roleAllows } from '../../core/domain/access';
import type { MembershipRole } from '../../core/domain/model';
import { localIso } from '../../core/domain/rules';
import { DemoStore } from '../../infra/mock/demo-store';

interface NavItem {
  path: string;
  label: string;
  icon: 'sun' | 'map' | 'factory' | 'radio' | 'activity' | 'alert' | 'shield' | 'search' | 'drone' | 'check' | 'wrench' | 'wallet' | 'layers' | 'building' | 'settings';
}

@Component({
  selector: 'app-shell',
  imports: [
    NgTemplateOutlet,
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
    LucideBuilding2,
    LucideSettings,
    LucideMenu,
    LucideX,
    LucideRotateCcw,
    LucideUnplug,
  ],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly store = inject(DemoStore);
  protected readonly menuOpen = signal(false);
  private readonly items: NavItem[] = [
    { path: '/inicio', label: 'Inicio', icon: 'sun' },
    { path: '/parque', label: 'Parque', icon: 'map' },
    { path: '/instalaciones', label: 'Instalaciones', icon: 'factory' },
    { path: '/telemetria', label: 'Telemetría', icon: 'radio' },
    { path: '/energia', label: 'Energía', icon: 'activity' },
    { path: '/incidentes', label: 'Incidentes', icon: 'alert' },
    { path: '/integridad', label: 'Integridad', icon: 'shield' },
    { path: '/fuentes', label: 'Fuentes', icon: 'search' },
    { path: '/misiones', label: 'Misiones', icon: 'drone' },
    { path: '/auditorias', label: 'Auditorías', icon: 'check' },
    { path: '/mantenimiento', label: 'Mantenimiento', icon: 'wrench' },
    { path: '/economia', label: 'Economía', icon: 'wallet' },
    { path: '/planes', label: 'Planes', icon: 'layers' },
    { path: '/organizaciones', label: 'Organizaciones', icon: 'building' },
    { path: '/configuracion', label: 'Configuración', icon: 'settings' },
  ];
  protected readonly nav = computed(() => this.items.filter((item) => roleAllows(item.path, this.store.role())));
  protected readonly organizationLabel = computed(() => this.store.organization()?.name ?? 'Sin organización');
  protected readonly clock = computed(() => {
    const site = this.store.state().sites.find((item) => item.id === this.store.selected()?.siteId);
    return localIso(this.store.state().clockIso, site?.timeZone ?? 'America/Argentina/Jujuy');
  });

  protected close(): void {
    this.menuOpen.set(false);
  }

  protected skip(event: Event): void {
    event.preventDefault();
    document.getElementById('contenido')?.focus();
  }

  protected reset(): void {
    this.store.reset();
    this.close();
  }
}

export type ShellRole = MembershipRole;
