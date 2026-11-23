import { Component, input } from '@angular/core';

@Component({
  selector: 'hg-button',
  host: { style: 'display: contents' },
  template: `
    <button
      class="hg-btn"
      [class.hg-btn-primary]="variant() === 'primary'"
      [class.hg-btn-secondary]="variant() === 'secondary'"
      [class.hg-btn-ghost]="variant() === 'ghost'"
      [class.hg-btn-danger]="variant() === 'danger'"
      [attr.type]="type()"
      [disabled]="disabled() || loading()"
      [attr.aria-busy]="loading() ? 'true' : null"
    >
      @if (loading()) {
        <span class="hg-spinner" aria-hidden="true"></span>
        <span class="sr-only">Cargando</span>
      }
      <ng-content />
    </button>
  `,
})
export class HgButton {
  readonly variant = input<'primary' | 'secondary' | 'ghost' | 'danger'>('primary');
  readonly type = input<'button' | 'submit' | 'reset'>('button');
  readonly disabled = input(false);
  readonly loading = input(false);
}
