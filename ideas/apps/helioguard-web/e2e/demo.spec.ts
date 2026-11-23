import { expect, test, type Page } from '@playwright/test';
import { contrastRatio } from '../src/app/core/domain/contrast';

const sizes = [
  { width: 375, height: 812, name: '375' },
  { width: 768, height: 1024, name: '768' },
  { width: 1280, height: 800, name: '1280' },
  { width: 1440, height: 900, name: '1440' },
];

test('recorre suciedad, replay y aislamiento de Salta', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toContainText('Puna Norte');
  await page.getByTestId('scenario-soiling').click();
  await expect(page.getByRole('status')).toContainText('Sector A2');
  await page.getByTestId('replay').click();
  await expect(page.getByText(/Mediciones válidas:/)).toContainText('1');
  await page.goto('/integridad');
  await expect(page.getByText('REPLAY · QUARANTINED')).toBeVisible();
  await page.goto('/instalaciones/ins-salta');
  await expect(page.getByRole('heading', { level: 1 })).toContainText('Acceso no autorizado');
  await page.locator('select').first().selectOption({ label: 'Propietaria Demo' });
  await page.goto('/instalaciones');
  await expect(page.getByRole('link', { name: 'Casa Andina' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Puna Norte' })).toHaveCount(0);
});

test('la misión llega a verificación solo con la secuencia válida', async ({ page }) => {
  await page.goto('/misiones');
  await page.getByTestId('propose-cleaning').click();
  const mission = page.getByRole('heading', { level: 2 });
  await expect(mission).toContainText('DRAFT');
  await page.getByRole('button', { name: 'Despachar' }).click();
  await expect(mission).toContainText('DRAFT');
  await page.getByRole('button', { name: 'Revisar seguridad' }).click();
  await expect(mission).toContainText('AWAITING_AUTHORIZATION');
  await page.getByTestId('approve-mission').click();
  await expect(mission).toContainText('AUTHORIZED');
  await page.getByTestId('dispatch').click();
  await expect(mission).toContainText('DISPATCHED');
  await page.getByRole('button', { name: 'Iniciar recorrido' }).click();
  await expect(mission).toContainText('IN_FLIGHT');
  await page.goto('/parque');
  await expect(page.getByTestId('drone')).toBeVisible();
  await expect(page.getByText('Dron en recorrido simulado')).toBeVisible();
  await page.goto('/misiones');
  await page.getByRole('button', { name: 'Completar' }).click();
  await expect(mission).toContainText('COMPLETED');
  await page.getByRole('button', { name: 'Verificar recuperación' }).click();
  await expect(page.getByText(/RECOVERED · mismo-intervalo/)).toBeVisible();
});

test('teclado, nombres accesibles y contraste renderizado', async ({ page }) => {
  await page.goto('/parque');
  const skip = page.getByRole('link', { name: 'Saltar al contenido' });
  await skip.focus();
  await expect(skip).toBeFocused();
  await page.keyboard.press('Enter');
  await expect(page.locator('#contenido')).toBeFocused();

  const a1 = page.getByRole('button', { name: /Bloque A1 Norte/ });
  const a2 = page.getByRole('button', { name: /Bloque A2 Este/ });
  await a1.focus();
  await page.keyboard.press('Tab');
  await expect(a2).toBeFocused();
  const outline = await a2.evaluate((element) => getComputedStyle(element).outlineWidth);
  expect(Number.parseFloat(outline)).toBeGreaterThanOrEqual(3);
  await page.keyboard.press('Enter');
  await expect(a2).toHaveAttribute('aria-pressed', 'true');

  const order = await page.evaluate(() => {
    const elements = Array.from(document.querySelectorAll<HTMLElement>('a, button, select'));
    return elements
      .filter((element) => getComputedStyle(element).display !== 'none' && element.tabIndex >= 0)
      .slice(0, 4)
      .map((element) => (element.getAttribute('aria-label') || element.textContent || '').trim().replace(/\s+/g, ' ').slice(0, 40));
  });
  expect(order[0]).toContain('Saltar al contenido');

  const tree = await page.locator('main').ariaSnapshot();
  expect(tree).toContain('Bloque A2 Este');
  expect(tree).toContain('Sector sucio');

  const heading = await colors(page.getByRole('heading', { level: 1 }));
  const muted = await colors(page.locator('main p').nth(1));
  const action = await colors(page.getByRole('button', { name: 'Sector sucio' }));
  expect(contrastRatio(heading.color, heading.background)).toBeGreaterThanOrEqual(4.5);
  expect(contrastRatio(muted.color, muted.background)).toBeGreaterThanOrEqual(4.5);
  expect(contrastRatio(action.color, action.background)).toBeGreaterThanOrEqual(4.5);
});

test('con movimiento reducido el dron queda quieto y visible', async ({ browser }) => {
  const context = await browser.newContext({
    baseURL: 'http://127.0.0.1:4321',
    reducedMotion: 'reduce',
    viewport: { width: 1440, height: 900 },
  });
  const page = await context.newPage();
  await fly(page);
  await page.goto('/parque');
  const drone = page.getByTestId('drone');
  await expect(drone).toBeVisible();
  const motion = await drone.evaluate((element) => {
    const style = getComputedStyle(element);
    return `${style.animationName} ${style.animationDuration}`;
  });
  expect(motion).toContain('none');
  await page.screenshot({ path: 'docs/screenshots/parque-vuelo-reduced-motion-1440.png', fullPage: true });
  await context.close();
});

test('capturas de inicio, parque, integridad, misiones y auditoría', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('scenario-soiling').click();
  await capture(page, 'inicio');
  await capture(page, 'parque', '/parque');
  await page.goto('/');
  await page.getByTestId('replay').click();
  await capture(page, 'integridad', '/integridad');
  await capture(page, 'auditorias', '/auditorias');
  await fly(page);
  await capture(page, 'misiones', '/misiones');
  await capture(page, 'parque-vuelo', '/parque');
  await expect(page.getByTestId('drone')).toBeVisible();
});

async function fly(page: Page): Promise<void> {
  await page.goto('/misiones');
  await page.getByTestId('propose-cleaning').click();
  await page.getByRole('button', { name: 'Revisar seguridad' }).click();
  await page.getByTestId('approve-mission').click();
  await page.getByTestId('dispatch').click();
  await page.getByRole('button', { name: 'Iniciar recorrido' }).click();
  await expect(page.getByRole('heading', { level: 2 })).toContainText('IN_FLIGHT');
}

async function capture(page: Page, name: string, route?: string): Promise<void> {
  for (const size of sizes) {
    await page.setViewportSize({ width: size.width, height: size.height });
    if (route) {
      await page.goto(route);
    }
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
    const extra = await page.evaluate(() => document.documentElement.scrollWidth - document.documentElement.clientWidth);
    expect(extra).toBeLessThanOrEqual(1);
    await page.screenshot({ path: `docs/screenshots/${name}-${size.name}.png`, fullPage: true, animations: 'disabled' });
  }
}

async function colors(locator: ReturnType<Page['locator']>): Promise<{ color: string; background: string }> {
  return locator.evaluate((element) => {
    let node: HTMLElement | null = element as HTMLElement;
    let background = 'rgba(0, 0, 0, 0)';
    while (node) {
      const value = getComputedStyle(node).backgroundColor;
      if (value && value !== 'rgba(0, 0, 0, 0)' && value !== 'transparent') {
        background = value;
        break;
      }
      node = node.parentElement;
    }
    return { color: getComputedStyle(element).color, background };
  });
}
