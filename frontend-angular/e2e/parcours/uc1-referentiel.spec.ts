import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';
import { aucunChampVolume } from '../helpers/charte';

test.describe('UC-1 — Consulter l’état d’un point d’eau', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('passant : liste publique sans compte (code, état, localité)', async ({ page }) => {
    await page.goto('/points');
    await expect(page.getByRole('heading', { name: /points d.eau/i })).toBeVisible();
    await expect(page.getByText('YDE-001')).toBeVisible();
    await expect(page.getByText('OPERATIONNEL')).toBeVisible();
    await expect(page.getByText(/Nkolbisson/)).toBeVisible();
    await aucunChampVolume(page);
  });

  test('passant : carte avec légende et formes d’état', async ({ page }) => {
    await page.goto('/carte');
    await expect(page.getByRole('heading', { name: /carte des points d.eau/i })).toBeVisible();
    await expect(page.getByRole('region', { name: /carte de \d+ points/i })).toBeVisible();
    await expect(page.locator('.legende')).toContainText('En panne');
  });

  test('non passant : import CSV sans authentification (403)', async ({ page }) => {
    await page.goto('/admin');
    await expect(page.getByText(/réservé à l.administrateur/i)).toBeVisible();
  });

  test('passant : import CSV d’ouvrages (admin)', async ({ page }) => {
    await connecter(page);
    await page.goto('/admin');
    await expect(page.getByRole('heading', { name: 'Référentiels' })).toBeVisible();
    await page.locator('textarea[name="csv"]').fill(
      'code,nomUsage,type,latitude,longitude,localiteCode,comiteId,populationDesservie\nYDE-IMP-1,Forage test,FORAGE,3.8,11.5,NKOL,c1,400'
    );
    await page.getByRole('button', { name: 'Importer' }).click();
    await expect(page.getByText(/import terminé/i)).toBeVisible();
  });

  test('non passant : colonne volume refusée (H-2)', async ({ page }) => {
    await connecter(page);
    await page.goto('/admin');
    await page.locator('textarea[name="csv"]').fill('code,volume\nYDE-001,1200');
    await page.getByRole('button', { name: 'Importer' }).click();
    await expect(page.getByText(/colonne volume interdite/i)).toBeVisible();
  });

  test('non passant : CSV vide (400)', async ({ page }) => {
    await connecter(page);
    await page.goto('/admin');
    await page.locator('textarea[name="csv"]').fill('   ');
    await page.getByRole('button', { name: 'Importer' }).click();
    await expect(page.getByText(/import refusé/i)).toBeVisible();
  });
});
