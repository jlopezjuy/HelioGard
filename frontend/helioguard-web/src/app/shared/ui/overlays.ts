import { afterRenderEffect, Component, ElementRef, input, model, viewChild } from '@angular/core';
import { LucideX } from '@lucide/angular';
import { HgIconButton } from './icon-button';

@Component({
  selector: 'hg-modal',
  imports: [HgIconButton, LucideX],
  template: `
    <dialog #dlg class="hg-dialog" (close)="syncClose()" (click)="onBackdrop($event)">
      <div class="hg-card-head">
        <h2 style="margin: 0; font-size: 1.125rem">{{ title() }}</h2>
        <hg-icon-button label="Cerrar diálogo" (click)="open.set(false)">
          <svg lucideX [size]="16"></svg>
        </hg-icon-button>
      </div>
      <div style="margin-top: 0.75rem"><ng-content /></div>
    </dialog>
  `,
})
export class HgModal {
  readonly title = input.required<string>();
  readonly open = model(false);
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dlg');

  constructor() {
    afterRenderEffect(() => {
      const element = this.dialog()?.nativeElement;
      if (!element) return;
      if (this.open() && !element.open) element.showModal();
      if (!this.open() && element.open) element.close();
    });
  }

  protected syncClose(): void {
    if (this.open()) this.open.set(false);
  }

  protected onBackdrop(event: Event): void {
    if (event.target === this.dialog()?.nativeElement) this.open.set(false);
  }
}

@Component({
  selector: 'hg-drawer',
  imports: [HgIconButton, LucideX],
  template: `
    <dialog #dlg class="hg-drawer" (close)="syncClose()" (click)="onBackdrop($event)">
      <div class="hg-card-head">
        <h2 style="margin: 0; font-size: 1.125rem">{{ title() }}</h2>
        <hg-icon-button label="Cerrar panel" (click)="open.set(false)">
          <svg lucideX [size]="16"></svg>
        </hg-icon-button>
      </div>
      <div style="margin-top: 0.75rem"><ng-content /></div>
    </dialog>
  `,
})
export class HgDrawer {
  readonly title = input.required<string>();
  readonly open = model(false);
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dlg');

  constructor() {
    afterRenderEffect(() => {
      const element = this.dialog()?.nativeElement;
      if (!element) return;
      if (this.open() && !element.open) element.showModal();
      if (!this.open() && element.open) element.close();
    });
  }

  protected syncClose(): void {
    if (this.open()) this.open.set(false);
  }

  protected onBackdrop(event: Event): void {
    if (event.target === this.dialog()?.nativeElement) this.open.set(false);
  }
}

@Component({
  selector: 'hg-tooltip',
  template: `
    <span class="hg-tip">
      <ng-content />
      <span role="tooltip" class="hg-tip-bubble">{{ text() }}</span>
    </span>
  `,
})
export class HgTooltip {
  readonly text = input.required<string>();
}
