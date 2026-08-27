import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';
import { intercepterApi } from '../helpers/api-mock';

const pagesCritiques = [
  { path: '/', nom: 'connexion' },
  { path: '/signaler', nom: 'signalement' },
  { path: '/points', nom: 'liste' },
  { path: '/file', nom: 'file délégué' },
  { path: '/carte', nom: 'carte' }
] as const;

test.describe('Accessibilité parcours critiques (QA-7, ENF-40)', () => {
  for (const ecran of pagesCritiques) {
    test(`axe-core : ${ecran.nom}`, async ({ page }) => {
      await intercepterApi(page);
      await page.goto(ecran.path);
      const results = await new AxeBuilder({ page })
        .withTags(['wcag2a', 'wcag2aa', 'wcag21aa'])
        .disableRules(['color-contrast'])
        .analyze();
      const graves = results.violations.filter((v) => v.impact === 'critical' || v.impact === 'serious');
      expect(graves, graves.map((v) => v.id).join(', ')).toEqual([]);
    });
  }

  test('clavier : le lien d’évitement reçoit le focus', async ({ page }) => {
    await intercepterApi(page);
    await page.goto('/');
    await page.keyboard.press('Tab');
    await expect(page.getByRole('link', { name: 'Aller au contenu' })).toBeFocused();
  });

  test('viewport mobile : navigation et formulaire restent utilisables', async ({ page }) => {
    await intercepterApi(page);
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto('/signaler');
    await expect(page.getByRole('navigation', { name: 'Navigation principale' })).toBeVisible();
    const envoyer = page.getByRole('button', { name: 'Envoyer' });
    const box = await envoyer.boundingBox();
    expect(box?.height).toBeGreaterThanOrEqual(47);
    expect(box!.width).toBeLessThanOrEqual(390);
  });
});
