import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';

test.describe('UC-3 / SQ5 — Qualifier un signalement', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('non passant : anonyme, file 403', async ({ page }) => {
    await page.goto('/file');
    await expect(page.getByText(/file indisponible/i)).toBeVisible();
  });

  test('non passant : hors périmètre (403)', async ({ page }) => {
    await connecter(page, 'delegue-exterieur@aquasensus.local');
    await expect(page).toHaveURL(/\/file/);
    await expect(page.getByText(/file indisponible/i)).toBeVisible();
  });

  test('passant : file priorisée (signalement, intervention, alerte)', async ({ page }) => {
    await connecter(page);
    await expect(page.getByText('SIG-2026-00007')).toBeVisible();
    await expect(page.getByText('INT-2026-0003')).toBeVisible();
    await expect(page.getByText('R1_ECHEANCE_MAINTENANCE')).toBeVisible();
  });

  test('non passant : qualification sans motif (RG-11, 422)', async ({ page }) => {
    await connecter(page);
    await page.getByRole('button', { name: 'Qualifier' }).click();
    await expect(page.getByText(/motif est obligatoire/i)).toBeVisible();
  });

  test('passant : qualifier avec motif', async ({ page }) => {
    await connecter(page);
    await page.getByLabel(/motif de qualification/i).fill('Panne confirmée sur place');
    await page.getByRole('button', { name: 'Qualifier' }).click();
    await expect(page.getByRole('status')).toContainText(/QUALIFIE/);
  });

  test('passant : rejeter avec motif', async ({ page }) => {
    await connecter(page);
    await page.getByLabel(/motif de qualification/i).fill('Hors périmètre du comité');
    await page.getByRole('button', { name: 'Rejeter' }).click();
    await expect(page.getByRole('status')).toContainText(/REJETE/);
  });
});
