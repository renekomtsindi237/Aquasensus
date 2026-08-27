import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';
import { aucunChampVolume } from '../helpers/charte';

test.describe('UC-8 / SQ1 — Identité et administration', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('non passant : identifiants invalides (401)', async ({ page }) => {
    await page.goto('/');
    await page.getByLabel('Identifiant').fill('admin@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('mauvais');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page.getByRole('status')).toContainText(/identifiant ou mot de passe incorrect/i);
  });

  test('non passant : compte verrouillé (423)', async ({ page }) => {
    await page.goto('/');
    await page.getByLabel('Identifiant').fill('verrouille@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('ChangeMoi!2026');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page.getByRole('status')).toContainText(/compte verrouillé/i);
  });

  test('passant : mot de passe temporaire, pas de navigation (EF-83)', async ({ page }) => {
    await page.goto('/');
    await page.getByLabel('Identifiant').fill('tempo@aquasensus.local');
    await page.getByLabel('Mot de passe').fill('ChangeMoi!2026');
    await page.getByRole('button', { name: 'Entrer' }).click();
    await expect(page).toHaveURL(/\/$/);
    await expect(page.getByRole('status')).toContainText(/mot de passe temporaire/i);
  });

  test('reset : réponse univoque (pas d’énumération)', async ({ page }) => {
    await page.goto('/');
    await page.getByLabel('Identifiant').fill('inconnu@aquasensus.local');
    await page.getByRole('button', { name: /mot de passe oublié/i }).click();
    await expect(page.getByRole('status')).toContainText(/si un compte correspond/i);
  });

  test('usager : redirection liste, pas la file', async ({ page }) => {
    await connecter(page, 'habitant@aquasensus.local');
    await expect(page).toHaveURL(/\/points/);
  });

  test('technicien : accès points, pas admin', async ({ page }) => {
    await connecter(page, 'tech@aquasensus.local');
    await page.goto('/comptes');
    await expect(page.getByText(/réservé à l.administrateur/i)).toBeVisible();
  });

  test('admin : création de compte', async ({ page }) => {
    await connecter(page);
    await page.goto('/comptes');
    await page.getByLabel('Identifiant').fill('nouveau.tech@aquasensus.local');
    await page.getByLabel('Nom').fill('Nouveau tech');
    await page.getByRole('button', { name: 'Créer un technicien' }).click();
    await expect(page.getByText('nouveau.tech@aquasensus.local')).toBeVisible();
  });

  test('non passant : identifiant déjà pris (409)', async ({ page }) => {
    await connecter(page);
    await page.goto('/comptes');
    await page.getByLabel('Identifiant').fill('admin@aquasensus.local');
    await page.getByLabel('Nom').fill('Doublon');
    await page.getByRole('button', { name: 'Créer un technicien' }).click();
    await expect(page.getByText(/compte existe déjà/i)).toBeVisible();
  });

  test('admin : suspension de compte', async ({ page }) => {
    await connecter(page);
    await page.goto('/comptes');
    await page.getByRole('button', { name: 'Suspendre' }).click();
    await expect(page.getByText(/SUSPENDU/)).toBeVisible();
  });
});

test.describe('UC-7 canal / SQ3–SQ4 — SMS et USSD simulés', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('non passant : console sans ADMIN', async ({ page }) => {
    await page.goto('/simulation');
    await expect(page.getByText(/journal inaccessible|administrateur/i)).toBeVisible();
  });

  test('SQ3 passant : AQS CODE SYMPTOME', async ({ page }) => {
    await connecter(page);
    await page.goto('/simulation');
    await page.getByRole('button', { name: 'Injecter' }).click();
    await expect(page.getByRole('status')).toContainText(/SIG-2026-01187|enregistre/i);
  });

  test('SQ3 non passant : format SMS invalide', async ({ page }) => {
    await connecter(page);
    await page.goto('/simulation');
    await page.getByLabel('Contenu').fill('AIDE PANNE');
    await page.getByRole('button', { name: 'Injecter' }).click();
    await expect(page.getByRole('status')).toContainText(/format non reconnu/i);
  });

  test('SQ4 passant : *123# ouvre le menu', async ({ page }) => {
    await connecter(page);
    await page.goto('/simulation');
    await page.getByRole('button', { name: 'Envoyer' }).click();
    await expect(page.locator('pre')).toContainText(/AquaSensus/);
    await expect(page.locator('pre')).toContainText(/Signaler/);
  });

  test('SQ4 non passant : code ouvrage inconnu', async ({ page }) => {
    await connecter(page);
    await page.goto('/simulation');
    await page.getByLabel('Saisie').fill('YDE-999');
    await page.getByRole('button', { name: 'Envoyer' }).click();
    await expect(page.locator('pre')).toContainText(/code inconnu/i);
  });

  test('SQ4 alternatif : session expirée', async ({ page }) => {
    await connecter(page);
    await page.goto('/simulation');
    await page.getByLabel('Saisie').fill('TIMEOUT');
    await page.getByRole('button', { name: 'Envoyer' }).click();
    await expect(page.locator('pre')).toContainText(/session expiree/i);
  });
});

test.describe('UC-7 charge d’usage — H-2', () => {
  test('aucun écran de relevé (litres, bidons, pompage)', async ({ page }) => {
    await intercepterApi(page);
    await page.goto('/signaler');
    await aucunChampVolume(page);
    await page.goto('/points');
    await aucunChampVolume(page);
    await page.goto('/');
    await aucunChampVolume(page);
  });
});
