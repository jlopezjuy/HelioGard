import { InjectionToken } from '@angular/core';
import type { DemoStore } from '../../infra/mock/demo-store';

/** Puerto de lectura y comandos. Una implementación HTTP futura reemplaza el mock, no las pantallas. */
export const DEMO_REPOSITORY = new InjectionToken<DemoStore>('DEMO_REPOSITORY');
