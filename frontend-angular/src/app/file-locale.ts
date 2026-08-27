const CLE = 'aqs.file-locale';
const CLE_CONFLIT = 'aqs.conflit';
const CLE_BROUILLON = 'aqs.brouillon-signalement';

export interface ElementFileLocale {
  id: string;
  statut: 'EN_ATTENTE' | 'ENVOYE' | 'EN_CONFLIT';
  resume: string;
  etatServeur?: string;
  type?: 'SIGNALEMENT';
  corps?: Record<string, unknown>;
}

export function lireFileLocale(): ElementFileLocale[] {
  try {
    const brut = localStorage.getItem(CLE);
    return brut ? (JSON.parse(brut) as ElementFileLocale[]) : [];
  } catch {
    return [];
  }
}

function ecrire(items: ElementFileLocale[]): void {
  localStorage.setItem(CLE, JSON.stringify(items));
  window.dispatchEvent(new StorageEvent('storage', { key: CLE }));
}

export function compterAEnvoyer(): number {
  return lireFileLocale().filter((e) => e.statut === 'EN_ATTENTE').length;
}

export function lireConflit(): string | null {
  try {
    return localStorage.getItem(CLE_CONFLIT);
  } catch {
    return null;
  }
}

export function empilerSignalement(uuidClient: string, corps: Record<string, unknown>, resume: string): void {
  const items = lireFileLocale();
  items.push({ id: uuidClient, statut: 'EN_ATTENTE', resume, type: 'SIGNALEMENT', corps });
  ecrire(items);
}

export function marquerEnvoye(id: string): void {
  ecrire(lireFileLocale().map((e) => (e.id === id ? { ...e, statut: 'ENVOYE' } : e)));
}

export function marquerConflit(id: string, etatServeur: string): void {
  localStorage.setItem(CLE_CONFLIT, etatServeur);
  ecrire(lireFileLocale().map((e) => (e.id === id ? { ...e, statut: 'EN_CONFLIT', etatServeur } : e)));
}

export function lireBrouillon(): Record<string, string> {
  try {
    const brut = sessionStorage.getItem(CLE_BROUILLON);
    return brut ? (JSON.parse(brut) as Record<string, string>) : {};
  } catch {
    return {};
  }
}

export function sauverBrouillon(champs: Record<string, string>): void {
  sessionStorage.setItem(CLE_BROUILLON, JSON.stringify(champs));
}
