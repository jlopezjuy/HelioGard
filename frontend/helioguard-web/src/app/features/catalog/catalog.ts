import { Component, computed, signal } from '@angular/core';
import {
  LucideBell,
  LucideSearch,
  LucideSettings,
} from '@lucide/angular';
import {
  HgAlert,
  HgBadge,
  HgBrand,
  HgButton,
  HgCard,
  HgChartFrame,
  HgDrawer,
  HgEmpty,
  HgEqualizer,
  HgError,
  HgField,
  HgControl,
  HgFlow,
  HgGauge,
  HgIconButton,
  HgKpi,
  HgMapFrame,
  HgModal,
  HgNavPill,
  HgPagination,
  HgProgress,
  HgSearch,
  HgSensor,
  HgSkeleton,
  HgTable,
  HgTabs,
  HgTooltip,
  type HgFlowPart,
  type HgNavItem,
  type HgTab,
} from '../../shared/ui';

interface SampleRow {
  block: string;
  state: string;
  wh: string;
}

const ROWS: readonly SampleRow[] = [
  { block: 'B1', state: 'En régimen', wh: '1 240' },
  { block: 'B2', state: 'Por debajo del modelo', wh: '860' },
  { block: 'B3', state: 'En régimen', wh: '1 180' },
  { block: 'B4', state: 'Sin lectura', wh: '—' },
];

@Component({
  selector: 'app-catalog',
  imports: [
    LucideBell,
    LucideSearch,
    LucideSettings,
    HgAlert,
    HgBadge,
    HgBrand,
    HgButton,
    HgCard,
    HgChartFrame,
    HgControl,
    HgDrawer,
    HgEmpty,
    HgEqualizer,
    HgError,
    HgField,
    HgFlow,
    HgGauge,
    HgIconButton,
    HgKpi,
    HgMapFrame,
    HgModal,
    HgNavPill,
    HgPagination,
    HgProgress,
    HgSearch,
    HgSensor,
    HgSkeleton,
    HgTable,
    HgTabs,
    HgTooltip,
  ],
  templateUrl: './catalog.html',
})
export class CatalogPage {
  protected readonly sections = [
    ['marca', 'Marca y navegación'],
    ['color', 'Color'],
    ['acciones', 'Acciones'],
    ['formularios', 'Formularios'],
    ['datos', 'Datos'],
    ['capas', 'Capas'],
    ['estado', 'Estados'],
    ['telemetria', 'Telemetría'],
  ] as const;
  protected readonly pillItems: readonly HgNavItem[] = [
    { id: 'inicio', label: 'Inicio', href: '/inicio' },
    { id: 'parque', label: 'Parque', href: '/parque' },
    { id: 'telemetria', label: 'Telemetría', href: '/telemetria' },
  ];
  protected readonly tabs: readonly HgTab[] = [
    { id: 'esperado', label: 'Esperado' },
    { id: 'real', label: 'Real' },
    { id: 'causa', label: 'Causa' },
  ];
  protected readonly tab = signal('esperado');
  protected readonly site = signal('');
  protected readonly siteError = computed(() => (this.site().trim() ? null : 'El sitio es obligatorio'));
  protected readonly query = signal('');
  protected readonly page = signal(1);
  protected readonly modal = signal(false);
  protected readonly drawer = signal(false);
  protected readonly retried = signal(false);
  protected readonly block = signal('B2');
  protected readonly swatches = [
    ['Canvas', '#070A0D'],
    ['Tarjeta', '#10161D'],
    ['Elevado', '#18222C'],
    ['Neón', '#E6FF00'],
    ['Menta', '#10B981'],
    ['Ámbar', '#F59E0B'],
    ['Coral', '#EF4444'],
    ['Red', '#64748B'],
  ] as const;
  protected readonly bars = [35, 55, 70, 40, 85, 60, 45, 65, 80, 50, 90, 30];
  protected readonly flow: readonly HgFlowPart[] = [
    { label: 'Solar', value: 5.6, tone: 'solar' },
    { label: 'Sitio', value: 4.8, tone: 'mint' },
    { label: 'Red', value: 1.2, tone: 'grid' },
  ];
  protected readonly filtered = computed(() => {
    const query = this.query().trim().toLowerCase();
    return ROWS.filter((row) => !query || `${row.block} ${row.state}`.toLowerCase().includes(query));
  });
  protected readonly pageRows = computed(() => {
    const start = (this.page() - 1) * 2;
    return this.filtered().slice(start, start + 2);
  });
  protected readonly pageCount = computed(() => Math.max(1, Math.ceil(this.filtered().length / 2)));

  protected setQuery(value: string): void {
    this.query.set(value);
    this.page.set(1);
  }

  protected setSite(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLInputElement) this.site.set(target.value);
  }
}
