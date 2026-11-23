import { Component, input } from '@angular/core';

@Component({
  selector: 'hg-badge',
  host: {
    class: 'hg-badge',
    '[class.hg-badge-neutral]': 'tone() === "neutral"',
    '[class.hg-badge-solar]': 'tone() === "solar"',
    '[class.hg-badge-mint]': 'tone() === "mint"',
    '[class.hg-badge-amber]': 'tone() === "amber"',
    '[class.hg-badge-coral]': 'tone() === "coral"',
  },
  template: '<ng-content />',
})
export class HgBadge {
  readonly tone = input<'neutral' | 'solar' | 'mint' | 'amber' | 'coral'>('neutral');
}
