import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { aucunChampVolume, cibleTactile, logoMarque } from '../helpers/charte';

test.describe('Connexion — UX/UI', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
    await page.goto('/');
  });

  test('affiche la promesse produit, le logo et aucun champ volume (H-2)', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Connexion' })).toBeVisible();
    await expect(page.getByText(/aucun relevé de volume d.eau/i)).toBeVisible();
    await logoMarque(page);
    await aucunChampVolume(page);
    await expect(page.getByLabel('Identifiant')).toBeVisible();
    await expect(page.getByLabel('Mot de passe')).toBeVisible();
  });

  test('lien d’évitement et navigation nommée (ENF-40)', async ({ page }) => {
    await expect(page.getByRole('link', { name: 'Aller au contenu' })).toHaveAttribute('href', '#contenu');
    await expect(page.getByRole('navigation', { name: 'Navigation principale' })).toBeVisible();
  });

  test('cible tactile 48 px sur Entrer (charte)', async ({ page }) => {
    await cibleTactile(page.getByRole('button', { name: 'Entrer' }));
  });

  test('refuse un mot de passe incorrect avec un message clair', async ({ page }) => {
    await page.getByLabel('Identifiant').fill('admin@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('mauvais');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page.getByRole('status')).toContainText(/identifiant ou mot de passe incorrect/i);
  });

  test('connecte un admin et ouvre la file de travail', async ({ page }) => {
    await page.getByLabel('Identifiant').fill('admin@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('ChangeMoi!2026');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page).toHaveURL(/\/file/);
    await expect(page.getByRole('heading', { name: 'File de travail' })).toBeVisible();
    await expect(page.getByText('SIG-2026-00007')).toBeVisible();
  });

  test('mot de passe oublié : message univoque (pas d’énumération)', async ({ page }) => {
    await page.getByLabel('Identifiant').fill('inconnu@aquasensus.local');
    await page.getByRole('button', { name: /mot de passe oublié/i }).click();
    await expect(page.getByRole('status')).toContainText(/si un compte correspond/i);
  });
});
