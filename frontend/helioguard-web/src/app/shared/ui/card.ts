import { Component, input } from '@angular/core';

@Component({
  selector: 'hg-card',
  template: `
    <article class="hg-card">
      @if (title()) {
        <header class="hg-card-head">
          <h2>{{ title() }}</h2>
          <ng-content select="[hgCardAction]" />
        </header>
      }
      <ng-content />
    </article>
  `,
})
export class HgCard {
  readonly title = input<string | null>(null);
}

@Component({
  selector: 'hg-kpi',
  template: `
    <article class="hg-card">
      <p class="hg-kicker">{{ label() }}</p>
      <p class="hg-metric">{{ value() }}<span class="hg-unit">{{ unit() }}</span></p>
      @if (note()) {
        <p class="hg-note">{{ note() }}</p>
      }
    </article>
  `,
})
export class HgKpi {
  readonly label = input.required<string>();
  readonly value = input.required<string>();
  readonly unit = input('');
  readonly note = input<string | null>(null);
}
