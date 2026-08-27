import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { aucunChampVolume, cibleTactile } from '../helpers/charte';

test.describe('Signalement et liste — UX/UI', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('liste publique : code, état, localité, pas de volume', async ({ page }) => {
    await page.goto('/points');
    await expect(page.getByRole('heading', { name: /points d.eau/i })).toBeVisible();
    await expect(page.getByText(/aucun volume n.est demandé/i)).toBeVisible();
    await expect(page.getByText('YDE-001')).toBeVisible();
    await expect(page.getByText('Forage Nkolbisson Marché')).toBeVisible();
    await expect(page.getByText('OPERATIONNEL')).toBeVisible();
    await aucunChampVolume(page);
  });

  test('formulaire signalement : OTP simulé, cibles tactiles, envoi', async ({ page }) => {
    await page.goto('/signaler');
    await expect(page.getByRole('heading', { name: 'Signaler un incident' })).toBeVisible();
    await expect(page.getByText(/123456/)).toBeVisible();
    await expect(page.locator('form')).not.toContainText(/volume|litre|bidon/i);
    await cibleTactile(page.getByRole('button', { name: 'Envoyer' }));

    await page.getByLabel(/code du point d.eau/i).fill('YDE-001');
    await page.getByLabel('Téléphone').fill('237670000001');
    await page.getByLabel('Code de confirmation').fill('123456');
    await page.getByRole('button', { name: 'Envoyer' }).click();
    await expect(page.getByRole('status')).toContainText(/signalement reçu/i);
  });
});
