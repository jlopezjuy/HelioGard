import { Component, inject, signal } from '@angular/core';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.html',
})
export class SettingsPage {
  protected readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly message = signal('');

  protected download(): void {
    const blob = new Blob([this.store.exportJson()], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'helioguard-demo.json';
    link.click();
    URL.revokeObjectURL(url);
    this.message.set('Exportación descargada. Sigue siendo un fixture, no un respaldo operativo.');
  }

  protected async onFile(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    this.message.set(this.store.importJson(await file.text()));
    input.value = '';
  }
}
