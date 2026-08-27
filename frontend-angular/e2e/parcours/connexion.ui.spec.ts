import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { aucunChampVolume, cibleTactile, logoMarque } from '../helpers/charte';

test.describe('Présentation — landing', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
    await page.goto('/');
  });

  test('affiche la promesse produit, le logo et aucun champ volume (H-2)', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /suivi partagé des forages/i })).toBeVisible();
    await expect(page.getByText(/aucun volume d.eau n.est relevé/i)).toBeVisible();
    await logoMarque(page);
    await aucunChampVolume(page);
  });

  test('lien d’évitement (ENF-40)', async ({ page }) => {
    await expect(page.getByRole('link', { name: 'Aller au contenu' })).toHaveAttribute('href', '#contenu');
  });
});

test.describe('Connexion — UX/UI', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
    await page.goto('/connexion');
  });

  test('formulaire de connexion sans volume', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Connexion' })).toBeVisible();
    await expect(page.getByLabel('Identifiant')).toBeVisible();
    await expect(page.getByLabel('Mot de passe')).toBeVisible();
    await aucunChampVolume(page);
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

  test('connecte un admin vers le tableau de bord plateforme', async ({ page }) => {
    await page.getByLabel('Identifiant').fill('admin@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('ChangeMoi!2026');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page).toHaveURL(/\/accueil/);
    await expect(page.getByRole('heading', { name: 'Pilotage plateforme' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Comptes', exact: true })).toBeVisible();
  });

  test('mot de passe oublié : message univoque (pas d’énumération)', async ({ page }) => {
    await page.getByLabel('Identifiant').fill('inconnu@aquasensus.local');
    await page.getByRole('button', { name: /mot de passe oublié/i }).click();
    await expect(page.getByRole('status')).toContainText(/si un compte correspond/i);
  });
});
