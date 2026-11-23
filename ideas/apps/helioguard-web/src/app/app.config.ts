import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { DEMO_REPOSITORY } from './core/data/ports';
import { routes } from './app.routes';
import { DemoStore } from './infra/mock/demo-store';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    { provide: DEMO_REPOSITORY, useExisting: DemoStore },
  ],
};
