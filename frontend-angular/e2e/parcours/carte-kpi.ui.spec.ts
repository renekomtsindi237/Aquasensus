import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';
import { aucunChampVolume, logoMarque } from '../helpers/charte';

test.describe('Carte et KPI — UX/UI', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('carte : légende permanente, forme + libellé (ENF-43)', async ({ page }) => {
    await page.goto('/carte');
    await logoMarque(page);
    await expect(page.getByRole('heading', { name: /carte des points d.eau/i })).toBeVisible();
    await expect(page.getByText(/couleur \+ forme \+ libellé/i)).toBeVisible();
    const scene = page.getByRole('region', { name: /carte de \d+ points/i });
    await expect(scene).toBeVisible();
    await expect(page.locator('.legende')).toContainText('Opérationnel');
    await expect(page.locator('.legende')).toContainText('En panne');
    await expect(page.locator('.legende')).toContainText('Hors service');
    await expect(page.locator('.mark .lib').first()).toBeVisible();
    await expect(page.locator('.forme[data-forme="losange"]')).toHaveCount(2);
    await aucunChampVolume(page);
  });

  test('KPI : agrégats, HORS_SERVICE exclu, export CSV sans nominatif', async ({ page }) => {
    await connecter(page);
    await page.goto('/kpi');
    await expect(page.getByRole('heading', { name: 'Tableau de bord' })).toBeVisible();
    await expect(page.getByText('HORS_SERVICE exclu des KPI de disponibilité (RG-12). Aucun volume d\'eau.')).toBeVisible();
    await expect(page.getByText('180 min')).toBeVisible();
    const download = page.waitForEvent('download');
    await page.getByRole('button', { name: 'Export CSV' }).click();
    const fichier = await download;
    expect(fichier.suggestedFilename()).toMatch(/aquasensus-kpi\.csv/);
  });
});
