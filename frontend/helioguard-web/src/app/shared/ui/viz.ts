import { Component, computed, input } from '@angular/core';

const GAUGE_RADIUS = 46;
const GAUGE_CIRC = 2 * Math.PI * GAUGE_RADIUS;

export interface HgFlowPart {
  label: string;
  value: number;
  tone: 'solar' | 'mint' | 'grid';
}

@Component({
  selector: 'hg-gauge',
  template: `
    <div
      class="hg-gauge"
      role="progressbar"
      [attr.aria-valuenow]="clamped()"
      aria-valuemin="0"
      aria-valuemax="100"
      [attr.aria-label]="label()"
    >
      <svg viewBox="0 0 120 120" aria-hidden="true">
        <circle cx="60" cy="60" r="46" fill="none" stroke="#232e3a" stroke-width="8" stroke-dasharray="3 5" />
        <circle
          cx="60"
          cy="60"
          r="46"
          fill="none"
          stroke="#e6ff00"
          stroke-width="8"
          stroke-linecap="round"
          [attr.stroke-dasharray]="circ"
          [attr.stroke-dashoffset]="offset()"
        />
      </svg>
      <div class="hg-gauge-copy">
        <span class="hg-metric" style="font-size: 1.75rem">{{ clamped() }}%</span>
        <span class="hg-note">{{ status() }}</span>
      </div>
    </div>
  `,
})
export class HgGauge {
  readonly percent = input(0);
  readonly status = input('Sin estado');
  readonly label = input('Porcentaje');
  protected readonly circ = GAUGE_CIRC;
  protected readonly clamped = computed(() => Math.min(100, Math.max(0, Math.round(this.percent()))));
  protected readonly offset = computed(() => GAUGE_CIRC * (1 - this.clamped() / 100));
}

@Component({
  selector: 'hg-equalizer',
  template: `
    <div class="hg-bars" role="img" [attr.aria-label]="label()">
      @for (bar of bars(); track $index) {
        <span [class.is-on]="$index < activeUntil()" [style.height.%]="bar"></span>
      }
    </div>
  `,
})
export class HgEqualizer {
  readonly bars = input.required<readonly number[]>();
  readonly activeUntil = input(0);
  readonly label = input('Barras de carga');
}

@Component({
  selector: 'hg-progress',
  template: `
    <div>
      <div class="hg-card-head">
        <span class="hg-kicker">{{ label() }}</span>
        <span class="hg-note">{{ value() }}%</span>
      </div>
      <div class="hg-track" role="progressbar" [attr.aria-valuenow]="value()" aria-valuemin="0" aria-valuemax="100" [attr.aria-label]="label()">
        <b [style.width.%]="value()"></b>
        <i></i>
      </div>
    </div>
  `,
})
export class HgProgress {
  readonly label = input.required<string>();
  readonly value = input(0);
}

@Component({
  selector: 'hg-flow',
  template: `
    <div>
      <div class="hg-flow" role="img" [attr.aria-label]="summary()">
        @for (part of parts(); track part.label) {
          <span
            [class.is-solar]="part.tone === 'solar'"
            [class.is-mint]="part.tone === 'mint'"
            [class.is-grid]="part.tone === 'grid'"
            [style.flex]="part.value + ' 1 0'"
          ></span>
        }
      </div>
      <ul class="hg-row" style="margin: 0.6rem 0 0; padding: 0; list-style: none">
        @for (part of parts(); track part.label) {
          <li class="hg-note"><strong style="color: var(--color-hg-ink)">{{ part.value }}</strong> {{ part.label }}</li>
        }
      </ul>
    </div>
  `,
})
export class HgFlow {
  readonly parts = input.required<readonly HgFlowPart[]>();
  protected summary(): string {
    return this.parts().map((part) => `${part.label} ${part.value}`).join(', ');
  }
}

@Component({
  selector: 'hg-chart-frame',
  template: `
    <section class="hg-frame" [attr.aria-label]="title()">
      <header>
        <div>
          <h3>{{ title() }}</h3>
          <p class="hg-note">{{ unit() }} · {{ rangeLabel() }}</p>
        </div>
        <ng-content select="[hgChartAction]" />
      </header>
      <div class="hg-chart-stage"><ng-content /></div>
      @if (note()) {
        <p class="hg-note">{{ note() }}</p>
      }
    </section>
  `,
})
export class HgChartFrame {
  readonly title = input.required<string>();
  readonly unit = input.required<string>();
  readonly rangeLabel = input.required<string>();
  readonly note = input<string | null>(null);
}

@Component({
  selector: 'hg-map-frame',
  template: `
    <section class="hg-frame" [attr.aria-label]="title()">
      <header>
        <div>
          <p class="hg-stamp">Vista cenital simulada</p>
          <h3 style="margin-top: 0.45rem">{{ title() }}</h3>
        </div>
        <ng-content select="[hgMapAction]" />
      </header>
      <div class="hg-map-stage"><ng-content /></div>
      <p class="hg-note">No es una ortofoto ni una termografía de paneles. La irradiancia del producto sale de un modelo, no de una imagen satelital.</p>
    </section>
  `,
})
export class HgMapFrame {
  readonly title = input.required<string>();
}
