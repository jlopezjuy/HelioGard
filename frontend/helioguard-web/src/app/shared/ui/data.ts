import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'hg-table',
  template: '<div class="hg-table-wrap"><ng-content /></div>',
})
export class HgTable {}

@Component({
  selector: 'hg-search',
  template: `
    <label class="hg-search">
      <span class="sr-only">{{ label() }}</span>
      <input
        class="hg-control"
        type="search"
        [value]="value()"
        [attr.placeholder]="placeholder()"
        (input)="onInput($event)"
      />
    </label>
  `,
})
export class HgSearch {
  readonly label = input('Buscar');
  readonly placeholder = input('Buscar');
  readonly value = input('');
  readonly valueChange = output<string>();

  protected onInput(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLInputElement) this.valueChange.emit(target.value);
  }
}

@Component({
  selector: 'hg-pagination',
  template: `
    <nav class="hg-pages" [attr.aria-label]="label()">
      <button type="button" class="hg-btn hg-btn-ghost" [disabled]="page() <= 1" (click)="go(page() - 1)">Anterior</button>
      @for (item of items(); track item) {
        <button
          type="button"
          class="hg-btn"
          [class.hg-btn-primary]="item === page()"
          [class.hg-btn-ghost]="item !== page()"
          [attr.aria-current]="item === page() ? 'page' : null"
          (click)="go(item)"
        >
          {{ item }}
        </button>
      }
      <button type="button" class="hg-btn hg-btn-ghost" [disabled]="page() >= pages()" (click)="go(page() + 1)">Siguiente</button>
    </nav>
  `,
})
export class HgPagination {
  readonly page = input(1);
  readonly pages = input(1);
  readonly label = input('Páginas');
  readonly pageChange = output<number>();
  protected readonly items = computed(() => Array.from({ length: Math.max(1, this.pages()) }, (_, index) => index + 1));

  protected go(page: number): void {
    if (page < 1 || page > this.pages() || page === this.page()) return;
    this.pageChange.emit(page);
  }
}
