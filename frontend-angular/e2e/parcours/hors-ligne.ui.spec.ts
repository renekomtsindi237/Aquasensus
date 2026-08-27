import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';

test.describe('Hors ligne — UX', () => {
  test('bandeau hors ligne (charte, EF-95)', async ({ page, context }) => {
    await intercepterApi(page);
    await page.goto('/points');
    await context.setOffline(true);
    await page.evaluate(() => window.dispatchEvent(new Event('offline')));
    await expect(page.getByRole('status').filter({ hasText: /hors ligne/i })).toBeVisible();
    await expect(page.getByText(/aucun volume d.eau/i)).toBeVisible();
  });
});
