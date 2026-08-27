import { Page } from '@playwright/test';

export async function connecter(
  page: Page,
  identifiant = 'admin@aquasensus.local',
  motDePasse = 'ChangeMoi!2026'
): Promise<void> {
  await page.goto('/connexion');
  await page.getByLabel('Identifiant').fill(identifiant);
  await page.getByLabel('Mot de passe').fill(motDePasse);
  await page.getByRole('button', { name: 'Entrer' }).click();
}
