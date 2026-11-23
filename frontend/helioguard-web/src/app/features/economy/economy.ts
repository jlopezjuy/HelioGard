import { Component, computed, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { canSeeEconomics, visibleInstallations } from '../../core/domain/access';
import { formatArs, formatWh } from '../../core/domain/rules';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-economy',
  imports: [ReactiveFormsModule],
  templateUrl: './economy.html',
})
export class EconomyPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly formatArs = formatArs;
  protected readonly formatWh = formatWh;
  protected readonly form = new FormGroup({
    purchase: new FormControl(200, { nonNullable: true, validators: [Validators.min(0), Validators.required] }),
    exported: new FormControl(80, { nonNullable: true, validators: [Validators.min(0), Validators.required] }),
  });

  constructor() {
    const rate = this.store.snapshot().tariffRates.find((item) => item.id === 'rate-puna');
    if (rate) {
      this.form.setValue({ purchase: rate.purchasePricePerKwh, exported: rate.exportPricePerKwh });
    }
  }
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const visible = visibleInstallations(snapshot).filter((item) => canSeeEconomics('FULL') && item.organizationId === snapshot.session.organizationId);
    const installation = visible.find((item) => item.id === 'ins-puna');
    return {
      installation,
      plan: snapshot.tariffPlans.find((item) => item.installationId === installation?.id) ?? null,
      rate: snapshot.tariffRates.find((item) => item.id === 'rate-puna') ?? null,
      economy: snapshot.economicAssessments.find((item) => item.id === 'eco-b2') ?? null,
    };
  });

  protected save(): void {
    if (this.form.invalid) {
      return;
    }
    const value = this.form.getRawValue();
    this.store.tariff(value.purchase, value.exported);
  }
}
