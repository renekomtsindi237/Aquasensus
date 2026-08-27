import { Injectable, computed, signal } from '@angular/core';

const CLE_JETON = 'aqs.jeton';
const CLE_NOM = 'aqs.nom';
const CLE_ROLES = 'aqs.roles';
const CLE_CHANGER = 'aqs.doitChanger';

export interface AuthReponse {
  jetonAcces: string;
  nomAffichage: string;
  roles: string[];
  doitChangerMotDePasse: boolean;
}

export interface SessionEtat {
  jeton: string;
  nom: string;
  roles: string[];
  doitChanger: boolean;
}

/** Session JWT côté PWA (EF-80). Les rôles ne font qu’orienter l’UI ; l’API reste autoritaire (DA-10). */
@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly etat = signal<SessionEtat | null>(lireStockage());

  readonly connecte = computed(() => !!this.etat()?.jeton);
  readonly doitChanger = computed(() => !!this.etat()?.doitChanger);
  readonly nom = computed(() => this.etat()?.nom ?? '');
  readonly roles = computed(() => this.etat()?.roles ?? []);

  enregistrer(reponse: AuthReponse): void {
    const suivant: SessionEtat = {
      jeton: reponse.jetonAcces,
      nom: reponse.nomAffichage ?? '',
      roles: reponse.roles ?? [],
      doitChanger: !!reponse.doitChangerMotDePasse
    };
    ecrireStockage(suivant);
    this.etat.set(suivant);
  }

  motDePasseChange(): void {
    const courant = this.etat();
    if (!courant) {
      return;
    }
    const suivant = { ...courant, doitChanger: false };
    ecrireStockage(suivant);
    this.etat.set(suivant);
  }

  deconnecter(): void {
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.removeItem(CLE_JETON);
      sessionStorage.removeItem(CLE_NOM);
      sessionStorage.removeItem(CLE_ROLES);
      sessionStorage.removeItem(CLE_CHANGER);
    }
    this.etat.set(null);
  }

  recharger(): void {
    this.etat.set(lireStockage());
  }

  aUnDes(attendus: string[]): boolean {
    const roles = this.roles();
    return attendus.some((r) => roles.includes(r));
  }

  /** Accueil après connexion / inscription (présentation, pas une règle métier). */
  accueil(): string {
    return '/accueil';
  }

  rolePrincipal(): string {
    const ordre = ['ADMIN', 'DELEGUE', 'PARTENAIRE', 'TECHNICIEN', 'USAGER'];
    return ordre.find((r) => this.roles().includes(r)) ?? 'USAGER';
  }

  libelleRole(): string {
    const noms: Record<string, string> = {
      ADMIN: 'Administrateur',
      DELEGUE: 'Délégué de comité',
      PARTENAIRE: 'Partenaire',
      TECHNICIEN: 'Technicien',
      USAGER: 'Usager'
    };
    return noms[this.rolePrincipal()] ?? this.rolePrincipal();
  }

  peutFile(): boolean {
    return this.connecte() && !this.doitChanger() && this.aUnDes(['DELEGUE', 'ADMIN']);
  }

  peutKpi(): boolean {
    return this.connecte() && !this.doitChanger() && this.aUnDes(['PARTENAIRE', 'DELEGUE', 'ADMIN']);
  }

  peutAlertes(): boolean {
    return this.connecte() && !this.doitChanger();
  }

  peutAdmin(): boolean {
    return this.connecte() && !this.doitChanger() && this.aUnDes(['ADMIN']);
  }
}

function lireStockage(): SessionEtat | null {
  if (typeof sessionStorage === 'undefined') {
    return null;
  }
  const jeton = sessionStorage.getItem(CLE_JETON);
  if (!jeton) {
    return null;
  }
  let roles: string[] = [];
  try {
    const brut = JSON.parse(sessionStorage.getItem(CLE_ROLES) ?? '[]');
    roles = Array.isArray(brut) ? brut : [];
  } catch {
    roles = [];
  }
  return {
    jeton,
    nom: sessionStorage.getItem(CLE_NOM) ?? '',
    roles,
    doitChanger: sessionStorage.getItem(CLE_CHANGER) === '1'
  };
}

function ecrireStockage(etat: SessionEtat): void {
  sessionStorage.setItem(CLE_JETON, etat.jeton);
  sessionStorage.setItem(CLE_NOM, etat.nom);
  sessionStorage.setItem(CLE_ROLES, JSON.stringify(etat.roles));
  sessionStorage.setItem(CLE_CHANGER, etat.doitChanger ? '1' : '0');
}
