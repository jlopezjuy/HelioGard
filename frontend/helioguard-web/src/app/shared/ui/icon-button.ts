import { Component, input } from '@angular/core';

@Component({
  selector: 'hg-icon-button',
  host: { style: 'display: contents' },
  template: `
    <button type="button" class="hg-icon-btn" [disabled]="disabled()" [attr.aria-label]="label()">
      <ng-content />
      @if (badge()) {
        <span class="hg-dot" aria-hidden="true"></span>
      }
    </button>
  `,
})
export class HgIconButton {
  readonly label = input.required<string>();
  readonly badge = input(false);
  readonly disabled = input(false);
}
