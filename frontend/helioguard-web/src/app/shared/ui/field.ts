import { Component, Directive, ElementRef, contentChildren, effect, inject, input } from '@angular/core';

let fieldSeq = 0;

@Directive({
  selector: 'input[hgControl], select[hgControl], textarea[hgControl]',
  host: { class: 'hg-control' },
})
export class HgControl {
  private readonly el = inject(ElementRef<HTMLElement>);

  mark(invalid: boolean, describedBy: string): void {
    const node = this.el.nativeElement;
    if (invalid) node.setAttribute('aria-invalid', 'true');
    else node.removeAttribute('aria-invalid');
    if (describedBy) node.setAttribute('aria-describedby', describedBy);
    else node.removeAttribute('aria-describedby');
  }
}

@Component({
  selector: 'hg-field',
  template: `
    <label class="hg-field">
      <span [id]="labelId">{{ label() }}</span>
      <ng-content />
      @if (hint()) {
        <span class="hg-hint" [id]="hintId">{{ hint() }}</span>
      }
      @if (error()) {
        <span class="hg-field-error" [id]="errorId" role="alert">{{ error() }}</span>
      }
    </label>
  `,
})
export class HgField {
  readonly label = input.required<string>();
  readonly hint = input<string | null>(null);
  readonly error = input<string | null>(null);
  private readonly seq = ++fieldSeq;
  readonly labelId = `hg-label-${this.seq}`;
  readonly hintId = `hg-hint-${this.seq}`;
  readonly errorId = `hg-err-${this.seq}`;
  private readonly controls = contentChildren(HgControl);

  constructor() {
    effect(() => {
      const invalid = Boolean(this.error());
      const described = [this.hint() ? this.hintId : '', invalid ? this.errorId : ''].filter(Boolean).join(' ');
      for (const control of this.controls()) {
        control.mark(invalid, described);
      }
    });
  }
}
