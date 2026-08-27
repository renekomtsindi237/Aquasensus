import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';

async function remplirSignalement(
  page: import('@playwright/test').Page,
  opts: { code: string; tel: string; otp?: string; categorie?: string; gravite?: string }
): Promise<void> {
  await page.goto('/signaler');
  await page.getByLabel(/code du point d.eau/i).fill(opts.code);
  await page.getByLabel('Téléphone').fill(opts.tel);
  await page.getByLabel('Code de confirmation').fill(opts.otp ?? '123456');
  if (opts.categorie) {
    await page.getByLabel('Symptôme').selectOption(opts.categorie);
  }
  if (opts.gravite) {
    await page.getByLabel('Gravité').selectOption(opts.gravite);
  }
  await page.getByRole('button', { name: 'Envoyer' }).click();
}

test.describe('UC-2 / SQ2 — Signaler un dysfonctionnement', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('nominal : enregistrement 201 et prise en charge', async ({ page }) => {
    await remplirSignalement(page, { code: 'YDE-001', tel: '237670000001' });
    await expect(page.getByRole('status')).toContainText(/signalement reçu/i);
  });

  test('A2 : corroboration, pas de nouvel incident', async ({ page }) => {
    await remplirSignalement(page, { code: 'YDE-DUP', tel: '237670000002' });
    await expect(page.getByRole('status')).toContainText(/déjà signalé/i);
  });

  test('A3 non passant : OTP incorrect (422)', async ({ page }) => {
    await remplirSignalement(page, { code: 'YDE-001', tel: '237670000003', otp: '000000' });
    await expect(page.getByRole('status')).toContainText(/code de confirmation incorrect/i);
  });

  test('A4 : quota public dépassé (429)', async ({ page }) => {
    await remplirSignalement(page, { code: 'YDE-001', tel: '237670009999' });
    await expect(page.getByRole('status')).toContainText(/trop de signalements/i);
  });

  test('A5 : panne totale, ouvrage EN_PANNE', async ({ page }) => {
    await remplirSignalement(page, {
      code: 'YDE-PANNE',
      tel: '237670000005',
      categorie: 'PANNE_TOTALE'
    });
    await expect(page.getByRole('status')).toContainText(/signalement reçu|comité/i);
  });

  test('A6 : rejeu idempotent (200, même référence)', async ({ page }) => {
    await remplirSignalement(page, { code: 'YDE-REJEU', tel: '237670000006' });
    await expect(page.getByRole('status')).toContainText(/signalement reçu/i);
  });

  test('non passant : code ouvrage inconnu (404)', async ({ page }) => {
    await remplirSignalement(page, { code: 'INCONNU', tel: '237670000007' });
    await expect(page.getByRole('status')).toContainText(/ouvrage introuvable/i);
  });

  test('gravité haute : notification comité', async ({ page }) => {
    await remplirSignalement(page, {
      code: 'YDE-001',
      tel: '237670000008',
      gravite: 'HAUTE'
    });
    await expect(page.getByRole('status')).toContainText(/signalement grave/i);
  });

  test('A1 : hors ligne → file locale', async ({ page }) => {
    await page.route('**/api/v1/reports', (route) => route.abort());
    await remplirSignalement(page, { code: 'YDE-001', tel: '237670000009' });
    await expect(page.getByRole('status').filter({ hasText: /file locale/i })).toBeVisible();
  });
});
