import { signal, type WritableSignal } from '@angular/core';

export function readyPhase(): WritableSignal<'loading' | 'ready'> {
  const phase = signal<'loading' | 'ready'>('loading');
  const quick = typeof matchMedia !== 'undefined' && matchMedia('(prefers-reduced-motion: reduce)').matches;
  setTimeout(() => phase.set('ready'), quick ? 0 : 240);
  return phase;
}
