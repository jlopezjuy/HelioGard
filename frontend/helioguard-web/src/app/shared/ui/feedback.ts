import { Component, input, output } from '@angular/core';
import { LucideCircleAlert, LucideCircleCheck, LucideInfo, LucideTriangleAlert, LucideWifiOff } from '@lucide/angular';
import { HgButton } from './button';

@Component({
  selector: 'hg-alert',
  imports: [LucideInfo, LucideCircleCheck, LucideTriangleAlert, LucideCircleAlert],
  host: {
    class: 'hg-alert',
    role: 'status',
    '[class.hg-alert-success]': 'tone() === "success"',
    '[class.hg-alert-warning]': 'tone() === "warning"',
    '[class.hg-alert-danger]': 'tone() === "danger"',
    '[attr.role]': 'tone() === "danger" || tone() === "warning" ? "alert" : "status"',
  },
  template: `
    @switch (tone()) {
      @case ('success') { <svg lucideCircleCheck [size]="18" aria-hidden="true"></svg> }
      @case ('warning') { <svg lucideTriangleAlert [size]="18" aria-hidden="true"></svg> }
      @case ('danger') { <svg lucideCircleAlert [size]="18" aria-hidden="true"></svg> }
      @default { <svg lucideInfo [size]="18" aria-hidden="true"></svg> }
    }
    <div>
      <strong>{{ title() }}</strong>
      <p><ng-content /></p>
    </div>
  `,
})
export class HgAlert {
  readonly tone = input<'info' | 'success' | 'warning' | 'danger'>('info');
  readonly title = input.required<string>();
}

@Component({
  selector: 'hg-sensor',
  imports: [LucideCircleCheck, LucideTriangleAlert, LucideCircleAlert, LucideWifiOff, LucideInfo],
  host: {
    class: 'hg-sensor',
    '[class.hg-sensor-ok]': 'tone() === "ok"',
    '[class.hg-sensor-watch]': 'tone() === "watch"',
    '[class.hg-sensor-alert]': 'tone() === "alert"',
    '[class.hg-sensor-offline]': 'tone() === "offline"',
    '[class.hg-sensor-unknown]': 'tone() === "unknown"',
  },
  template: `
    @switch (tone()) {
      @case ('ok') { <svg lucideCircleCheck [size]="16" aria-hidden="true"></svg> }
      @case ('watch') { <svg lucideTriangleAlert [size]="16" aria-hidden="true"></svg> }
      @case ('alert') { <svg lucideCircleAlert [size]="16" aria-hidden="true"></svg> }
      @case ('offline') { <svg lucideWifiOff [size]="16" aria-hidden="true"></svg> }
      @default { <svg lucideInfo [size]="16" aria-hidden="true"></svg> }
    }
    <span>{{ label() }}</span>
  `,
})
export class HgSensor {
  readonly tone = input<'ok' | 'watch' | 'alert' | 'offline' | 'unknown'>('unknown');
  readonly label = input.required<string>();
}

@Component({
  selector: 'hg-empty',
  template: `
    <div class="hg-card">
      <h2 style="margin: 0; font-size: 1.125rem">{{ title() }}</h2>
      <p class="hg-note">{{ detail() }}</p>
      <ng-content />
    </div>
  `,
})
export class HgEmpty {
  readonly title = input.required<string>();
  readonly detail = input.required<string>();
}

@Component({
  selector: 'hg-error',
  imports: [HgButton],
  template: `
    <div class="hg-card" role="alert">
      <h2 style="margin: 0; font-size: 1.125rem">{{ title() }}</h2>
      <p class="hg-note">{{ detail() }}</p>
      @if (retryLabel()) {
        <hg-button variant="secondary" (click)="retry.emit()">{{ retryLabel() }}</hg-button>
      }
    </div>
  `,
})
export class HgError {
  readonly title = input.required<string>();
  readonly detail = input.required<string>();
  readonly retryLabel = input<string | null>(null);
  readonly retry = output<void>();
}

@Component({
  selector: 'hg-skeleton',
  host: { class: 'hg-skeleton', 'aria-hidden': 'true' },
  template: `
    @for (line of lines(); track line) {
      <i [style.width.%]="line"></i>
    }
  `,
})
export class HgSkeleton {
  readonly lines = input<readonly number[]>([100, 72, 88]);
}
