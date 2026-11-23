import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: 'e2e',
  timeout: 60_000,
  use: { baseURL: 'http://127.0.0.1:4310', ...devices['Desktop Chrome'] },
  webServer: {
    command: 'npx ng serve --host 127.0.0.1 --port 4310',
    url: 'http://127.0.0.1:4310',
    reuseExistingServer: false,
    timeout: 120_000,
  },
});
