import { test, expect } from '@playwright/test';
import { intercepterApi } from '../helpers/api-mock';
import { connecter } from '../helpers/session';

test.describe('UC-5 / UC-8 / UC-9 — Intervention, alertes prédictives', () => {
  test.beforeEach(async ({ page }) => {
    await intercepterApi(page);
  });

  test('UC-9 passant : alerte avec explication (sans volume d’eau)', async ({ page }) => {
    await connecter(page);
    await expect(page.getByRole('heading', { name: 'Alertes à traiter' })).toBeVisible();
    await expect(page.getByText(/échéance d.entretien/i)).toBeVisible();
    await expect(page.getByText(/aucun volume d.eau/).first()).toBeVisible();
    await expect(page.getByText('ELEVE')).toBeVisible();
  });

  test('UC-5 : dossier d’intervention visible (référence, statut, échéance)', async ({ page }) => {
    await connecter(page);
    await expect(page.getByText('INT-2026-0003')).toBeVisible();
    await expect(page.getByText('Échéance 2026-09-01')).toBeVisible();
  });
});
