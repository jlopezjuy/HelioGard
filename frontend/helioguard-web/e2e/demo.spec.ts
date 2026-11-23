import { expect, test } from '@playwright/test';

test('recorre suciedad, misión, integridad y el rol técnico', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 900 });
  await page.goto('/parque');
  await page.evaluate(() => localStorage.removeItem('helioguard.demo.v4'));
  await page.reload();
  await page.getByRole('button', { name: 'Sector sucio' }).click();
  await expect(page.getByRole('button', { name: /Bloque B2 Suciedad sospechada/ })).toBeVisible();
  await page.screenshot({ path: 'docs/screenshots/parque-1440.png' });

  await page.getByLabel('Escenario de pérdida').selectOption('FROST');
  await expect(page.getByRole('status').filter({ hasText: 'Helada' })).toBeVisible();
  await page.getByRole('button', { name: 'Sector sucio' }).click();

  await page.getByRole('button', { name: 'Crear misión' }).click();
  await expect(page.getByText('Incident → WorkOrder → DroneMission en DRAFT')).toBeVisible();
  await page.getByRole('button', { name: 'Revisar seguridad' }).click();
  await expect(page.getByText('espera autorización')).toBeVisible();
  await page.getByRole('button', { name: 'Aprobar' }).click();
  await expect(page.getByText('permiso operativo real')).toBeVisible();
  await page.getByRole('button', { name: 'Despachar' }).click();
  await expect(page.getByText('DISPATCHED y luego IN_FLIGHT')).toBeVisible();
  await expect(page.getByText('Dron simulado')).toBeVisible();

  for (let step = 0; step < 8; step += 1) {
    await page.getByRole('button', { name: 'Animar dron' }).click();
  }
  await expect(page.getByText('Recorrido simulado 100 %')).toBeVisible();
  await page.getByRole('button', { name: 'Verificar' }).click();
  await expect(page.getByText('Verificación registrada')).toBeVisible();

  await page.getByRole('button', { name: 'Evento original' }).click();
  await page.getByRole('button', { name: 'Replay exacto' }).click();
  await expect(page.getByText('Idempotencia por gatewayId+eventId')).toBeVisible();
  await page.getByRole('button', { name: 'Inyección falsa' }).click();
  await expect(page.getByText('No demuestra intrusión')).toBeVisible();
  await expect(page.getByText(/ataque confirmado/i)).toHaveCount(0);

  await page.getByRole('button', { name: 'Restablecer demo' }).click();
  await page.goto('/inicio');
  await expect(page.getByRole('heading', { name: /está en régimen/ })).toBeVisible();

  await page.getByLabel('Usuario demo').selectOption('usr-tech');
  await expect(page.getByText('Técnico Demo · TECHNICIAN')).toBeVisible();
  await page.goto('/economia');
  await expect(page.getByText('Permiso denegado para /economia')).toBeVisible();
});
