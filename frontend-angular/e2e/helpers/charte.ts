import { expect, Locator, Page } from '@playwright/test';

/** H-2 : pas de saisie de litres / bidons / seaux. La mention « aucun volume » est autorisée. */
export async function aucunChampVolume(page: Page): Promise<void> {
  await expect(page.getByLabel(/litre|bidon|seau|m³|compteur/i)).toHaveCount(0);
  const texte = await page.locator('body').innerText();
  expect(texte).not.toMatch(/\b(litres?|m³|\bm3\b|bidons?|seaux?)\b/i);
}

export async function logoMarque(page: Page): Promise<void> {
  const logo = page.getByRole('img', { name: 'AquaSensus' }).first();
  await expect(logo).toBeVisible();
  await expect(logo).toHaveAttribute('src', /aquasensus-logo\.png/);
}

export async function cibleTactile(locator: Locator, minPx = 48): Promise<void> {
  const box = await locator.boundingBox();
  expect(box, 'élément visible pour la cible tactile').toBeTruthy();
  expect(box!.height).toBeGreaterThanOrEqual(minPx - 1);
}
