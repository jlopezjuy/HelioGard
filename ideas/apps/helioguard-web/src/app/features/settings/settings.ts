import { Component, inject, signal } from '@angular/core';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.html',
})
export class Settings {
  protected readonly store = inject(DemoStore);
  protected readonly raw = signal('');
  protected readonly error = signal<string | null>(null);

  protected download(): void {
    const blob = new Blob([this.store.exportJson()], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'helioguard-demo.json';
    link.click();
    URL.revokeObjectURL(url);
  }

  protected load(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) {
      return;
    }
    void file.text().then((text) => {
      this.raw.set(text);
      this.error.set(this.store.importJson(text));
    });
  }

  protected apply(): void {
    this.error.set(this.store.importJson(this.raw()));
  }
}
