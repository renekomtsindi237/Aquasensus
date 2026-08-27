import { Component, HostListener, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { compterAEnvoyer, lireConflit } from './file-locale';
import { SessionService } from './session.service';
import { SynchronisationService } from './synchronisation.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (!enLigne) {
      <p class="hors-ligne" role="status">Hors ligne — consultation des données déjà chargées. Aucun volume d’eau.</p>
    }
    @if (aEnvoyer > 0) {
      <p class="sync" role="status">{{ aEnvoyer }} élément(s) à envoyer</p>
    }
    @if (conflit) {
      <p class="conflit" role="alert">Conflit de synchronisation : le serveur fait autorité. {{ conflit }}</p>
    }
    <a class="skip" href="#contenu">Aller au contenu</a>
    <div class="cadre" [class.coquille]="coquille()">
      @if (coquille()) {
        <aside class="barre" [class.ouverte]="menuOuvert" id="nav-laterale">
          <div class="marque">
            <img src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
            <p class="qui">{{ session.nom() }}</p>
            <p class="role">{{ session.libelleRole() }}</p>
          </div>
          <nav aria-label="Navigation principale">
            <a routerLink="/accueil" routerLinkActive="actif">Tableau de bord</a>
            <a routerLink="/points" routerLinkActive="actif">Liste</a>
            <a routerLink="/carte" routerLinkActive="actif">Carte</a>
            <a routerLink="/signaler" routerLinkActive="actif">Signaler</a>
            @if (session.peutFile()) {
              <a routerLink="/file" routerLinkActive="actif">File</a>
            }
            @if (session.peutKpi()) {
              <a routerLink="/kpi" routerLinkActive="actif">KPI détaillés</a>
            }
            @if (session.peutAlertes()) {
              <a routerLink="/notifications" routerLinkActive="actif">Alertes</a>
            }
            @if (session.peutAdmin()) {
              <a routerLink="/admin" routerLinkActive="actif">Référentiels</a>
              <a routerLink="/simulation" routerLinkActive="actif">SMS/USSD</a>
              <a routerLink="/comptes" routerLinkActive="actif">Comptes</a>
            }
          </nav>
        </aside>
      }
      <div class="colonne">
        @if (coquille()) {
          <header class="bandeau">
            <button type="button" class="menu" (click)="menuOuvert = !menuOuvert" aria-controls="nav-laterale"
              [attr.aria-expanded]="menuOuvert">Menu</button>
            <p class="produit">AquaSensus · {{ dateDuJour }}</p>
            <div class="outils">
              <span class="profil">{{ session.nom() }} · {{ session.libelleRole() }}</span>
              <button type="button" class="deco" (click)="deconnecter()">Déconnexion</button>
            </div>
          </header>
        } @else if (bandeauPublic()) {
          <nav class="public" aria-label="Navigation principale">
            <a routerLink="/">Accueil</a>
            <a routerLink="/points">Liste</a>
            <a routerLink="/carte">Carte</a>
            <a routerLink="/signaler">Signaler</a>
            <a routerLink="/connexion">Connexion</a>
          </nav>
        }
        <div id="contenu">
          <router-outlet />
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hors-ligne, .sync, .conflit { margin: 0; padding: var(--aqs-space-3) var(--aqs-space-4); text-align: center; font-weight: 600; }
    .hors-ligne { background: var(--aqs-color-surface-muted); color: var(--aqs-color-feedback-offline); }
    .sync { background: var(--aqs-color-action-subtle); color: var(--aqs-color-action); }
    .conflit { background: var(--aqs-color-state-panne-bg); color: var(--aqs-color-state-panne-text); }
    .skip {
      position: absolute; left: -999px; top: var(--aqs-space-2);
      background: var(--aqs-color-action); color: var(--aqs-color-text-on-action);
      padding: var(--aqs-space-2) var(--aqs-space-4); z-index: 20;
    }
    .skip:focus { left: var(--aqs-space-2); }
    .cadre.coquille { display: flex; min-height: 100vh; }
    .barre {
      width: 16.5rem; flex-shrink: 0; background: var(--aqs-color-chrome); color: var(--aqs-color-chrome-text);
      display: flex; flex-direction: column; padding: var(--aqs-space-5) var(--aqs-space-4);
    }
    .marque img { width: min(100%, 180px); height: auto; }
    .qui { margin: var(--aqs-space-4) 0 0; font-weight: 700; }
    .role { margin: 0 0 var(--aqs-space-6); color: var(--aqs-color-chrome-muted); font-size: var(--aqs-font-size-caption); }
    .barre nav { display: flex; flex-direction: column; gap: var(--aqs-space-1); }
    .barre a {
      color: var(--aqs-color-chrome-text); min-height: 48px; display: flex; align-items: center;
      padding: 0 var(--aqs-space-3); border-radius: var(--aqs-radius-md); text-decoration: none; font-weight: 600;
    }
    .barre a:hover { background: var(--aqs-color-chrome-hover); }
    .barre a.actif { background: var(--aqs-color-chrome-active); }
    .colonne { flex: 1; min-width: 0; background: var(--aqs-color-background); }
    .bandeau {
      display: flex; flex-wrap: wrap; align-items: center; gap: var(--aqs-space-3);
      padding: var(--aqs-space-3) var(--aqs-space-5); background: var(--aqs-color-surface);
      border-bottom: 1px solid var(--aqs-color-border);
    }
    .produit { margin: 0; flex: 1; font-weight: 600; }
    .outils { display: flex; flex-wrap: wrap; align-items: center; gap: var(--aqs-space-3); }
    .profil { color: var(--aqs-color-text-secondary); font-size: var(--aqs-font-size-body-sm); }
    .deco {
      min-height: 48px; padding: 0 var(--aqs-space-4); border: 0; border-radius: var(--aqs-radius-md);
      background: var(--aqs-color-feedback-error); color: var(--aqs-color-text-on-action); font-weight: 600; cursor: pointer;
    }
    .menu { display: none; min-height: 48px; background: var(--aqs-color-action); color: var(--aqs-color-text-on-action);
      border: 0; border-radius: var(--aqs-radius-md); padding: 0 var(--aqs-space-4); font-weight: 600; }
    .public {
      display: flex; flex-wrap: wrap; gap: var(--aqs-space-3); padding: var(--aqs-space-3) var(--aqs-space-4);
      border-bottom: 1px solid var(--aqs-color-border); background: var(--aqs-color-surface);
    }
    .public a { color: var(--aqs-color-action); min-height: 48px; display: inline-flex; align-items: center; font-weight: 600; }
    @media (max-width: 767px) {
      .cadre.coquille { display: block; }
      .menu { display: inline-flex; align-items: center; }
      .barre { display: none; width: 100%; }
      .barre.ouverte { display: flex; }
    }
  `]
})
export class AppComponent {
  readonly session = inject(SessionService);
  private readonly router = inject(Router);
  readonly chemin = signal(this.router.url);
  menuOuvert = false;
  enLigne = typeof navigator === 'undefined' ? true : navigator.onLine;
  aEnvoyer = 0;
  conflit: string | null = null;
  readonly dateDuJour = new Intl.DateTimeFormat('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(new Date());

  constructor(private readonly sync: SynchronisationService) {
    this.actualiserSync();
    this.router.events.pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd)).subscribe((e) => {
      this.chemin.set(e.urlAfterRedirects);
      this.menuOuvert = false;
    });
  }

  private page(): string {
    return (this.chemin().split('?')[0] || '/');
  }

  presentation(): boolean {
    const p = this.page();
    return p === '/' || p.startsWith('/connexion') || p.startsWith('/mot-de-passe');
  }

  coquille(): boolean {
    return this.session.connecte() && !this.session.doitChanger() && !this.presentation();
  }

  bandeauPublic(): boolean {
    return !this.coquille() && !this.presentation();
  }

  deconnecter(): void {
    this.session.deconnecter();
    void this.router.navigateByUrl('/');
  }

  @HostListener('window:online')
  @HostListener('window:offline')
  @HostListener('window:focus')
  @HostListener('window:storage')
  actualiserLigne(): void {
    this.enLigne = navigator.onLine;
    this.session.recharger();
    this.actualiserSync();
    if (this.enLigne) {
      void this.sync.rejouer().then(() => this.actualiserSync());
    }
  }

  private actualiserSync(): void {
    this.aEnvoyer = compterAEnvoyer();
    this.conflit = lireConflit();
  }
}
