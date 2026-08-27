import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';
import { aucunChampVolume } from '../helpers/charte';

test.describe('UC-4 / UC-6 / UC-10 — File, rétablissement, KPI (SQ7)', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('UC-4 : interventions actives dans la file (pas de volume)', async ({ page }) => {
    await connecter(page);
    await page.goto('/file');
    await expect(page.getByRole('heading', { name: 'Interventions actives' })).toBeVisible();
    await expect(page.getByText('AFFECTEE')).toBeVisible();
    await aucunChampVolume(page);
  });

  test('UC-10 non passant : KPI sans jeton redirige vers la connexion', async ({ page }) => {
    await page.goto('/kpi');
    await expect(page).toHaveURL(/\/connexion/);
    await expect(page.getByRole('heading', { name: 'Connexion' })).toBeVisible();
  });

  test('UC-10 passant : agrégats, HORS_SERVICE exclu, budget', async ({ page }) => {
    await connecter(page);
    await page.goto('/kpi');
    await expect(page.getByText('180 min')).toBeVisible();
    await expect(page.getByText(/hors_service exclu/i)).toBeVisible();
    await expect(page.getByText(/budget indicatif/i)).toBeVisible();
    await aucunChampVolume(page);
  });

  test('UC-10 : export PDF', async ({ page }) => {
    await connecter(page);
    await page.goto('/kpi');
    const download = page.waitForEvent('download');
    await page.getByRole('button', { name: 'Export PDF' }).click();
    const fichier = await download;
    expect(fichier.suggestedFilename()).toMatch(/aquasensus-synthese\.pdf/);
  });

  test('UC-6 : notifications de clôture / gravité (canal in-app)', async ({ page }) => {
    await connecter(page);
    await page.goto('/notifications');
    await expect(page.getByText('Signalement grave')).toBeVisible();
    await expect(page.getByText(/canaux in-app.*aucun volume d.eau/i)).toBeVisible();
  });

  test('UC-6 non passant : notifications anonymes redirigent vers la connexion', async ({ page }) => {
    await page.goto('/notifications');
    await expect(page).toHaveURL(/\/connexion/);
    await expect(page.getByRole('heading', { name: 'Connexion' })).toBeVisible();
  });
});
