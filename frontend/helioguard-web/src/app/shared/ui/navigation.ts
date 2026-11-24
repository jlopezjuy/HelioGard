import { Component, ElementRef, input, model, viewChildren } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

export interface HgNavItem {
  id: string;
  label: string;
  href?: string;
}

export interface HgTab {
  id: string;
  label: string;
}

@Component({
  selector: 'hg-brand',
  imports: [RouterLink],
  template: `
    <a class="hg-brand" routerLink="/inicio">
      <img class="hg-brand-logo" src="logo/Logo01.png" alt="" width="34" height="34" aria-hidden="true" />
      <span>Helio<em>Guard</em></span>
    </a>
  `,
})
export class HgBrand {}

@Component({
  selector: 'hg-nav-pill',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="hg-nav" [class.hg-nav-scroll]="scroll()" [attr.aria-label]="label()">
      @for (item of items(); track item.id) {
        @if (item.href) {
          <a [routerLink]="item.href" routerLinkActive="is-active" ariaCurrentWhenActive="page">{{ item.label }}</a>
        } @else {
          <button type="button" [class.is-active]="active() === item.id" (click)="active.set(item.id)">{{ item.label }}</button>
        }
      }
    </nav>
  `,
})
export class HgNavPill {
  readonly label = input('Secciones');
  readonly items = input.required<readonly HgNavItem[]>();
  readonly active = model('');
  readonly scroll = input(false);
}

@Component({
  selector: 'hg-tabs',
  template: `
    <div class="hg-tabs" role="tablist" [attr.aria-label]="label()">
      @for (tab of tabs(); track tab.id; let index = $index) {
        <button
          #tabBtn
          type="button"
          role="tab"
          [id]="'hg-tab-' + tab.id"
          [attr.aria-controls]="'hg-panel-' + tab.id"
          [attr.aria-selected]="selected() === tab.id"
          [tabindex]="selected() === tab.id ? 0 : -1"
          (click)="pick(tab.id)"
          (keydown)="onKey($event, index)"
        >
          {{ tab.label }}
        </button>
      }
    </div>
  `,
})
export class HgTabs {
  readonly label = input('Pestañas');
  readonly tabs = input.required<readonly HgTab[]>();
  readonly selected = model.required<string>();
  private readonly buttons = viewChildren<ElementRef<HTMLButtonElement>>('tabBtn');

  protected pick(id: string): void {
    this.selected.set(id);
  }

  protected onKey(event: KeyboardEvent, index: number): void {
    const list = this.tabs();
    let next = -1;
    if (event.key === 'ArrowRight' || event.key === 'ArrowDown') next = (index + 1) % list.length;
    if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') next = (index - 1 + list.length) % list.length;
    if (event.key === 'Home') next = 0;
    if (event.key === 'End') next = list.length - 1;
    if (next < 0) return;
    event.preventDefault();
    const id = list[next]?.id;
    if (!id) return;
    this.selected.set(id);
    queueMicrotask(() => this.buttons()[next]?.nativeElement.focus());
  }
}
