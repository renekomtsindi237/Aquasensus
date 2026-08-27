import { Component, HostListener } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { compterAEnvoyer, lireConflit } from './file-locale';
import { SynchronisationService } from './synchronisation.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
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
    <nav class="nav" aria-label="Navigation principale">
      <a routerLink="/points">Liste</a>
      <a routerLink="/carte">Carte</a>
      <a routerLink="/signaler">Signaler</a>
      <a routerLink="/file">File</a>
      <a routerLink="/kpi">KPI</a>
      <a routerLink="/notifications">Alertes</a>
      <a routerLink="/admin">Référentiels</a>
      <a routerLink="/simulation">SMS/USSD</a>
      <a routerLink="/comptes">Comptes</a>
      <a routerLink="/">Connexion</a>
    </nav>
    <div id="contenu">
      <router-outlet />
    </div>
  `,
  styles: [`
    .hors-ligne {
      margin: 0; padding: var(--aqs-space-3) var(--aqs-space-4);
      background: var(--aqs-color-surface-muted); color: var(--aqs-color-feedback-offline);
      text-align: center; font-weight: 600;
    }
    .sync {
      margin: 0; padding: var(--aqs-space-2) var(--aqs-space-4);
      background: var(--aqs-color-action-subtle); color: var(--aqs-color-action);
      text-align: center;
    }
    .conflit {
      margin: 0; padding: var(--aqs-space-2) var(--aqs-space-4);
      background: var(--aqs-color-state-panne-bg); color: var(--aqs-color-state-panne-text);
      text-align: center;
    }
    .nav {
      display: flex; flex-wrap: wrap; gap: var(--aqs-space-3);
      padding: var(--aqs-space-3) var(--aqs-space-4);
      border-bottom: 1px solid var(--aqs-color-border);
      background: var(--aqs-color-surface);
    }
    .nav a { color: var(--aqs-color-action); min-height: 48px; display: inline-flex; align-items: center; }
    .skip {
      position: absolute; left: -999px; top: var(--aqs-space-2);
      background: var(--aqs-color-action); color: var(--aqs-color-text-on-action);
      padding: var(--aqs-space-2) var(--aqs-space-4); z-index: 10;
    }
    .skip:focus { left: var(--aqs-space-2); }
  `]
})
export class AppComponent {
  enLigne = typeof navigator === 'undefined' ? true : navigator.onLine;
  aEnvoyer = 0;
  conflit: string | null = null;

  constructor(private readonly sync: SynchronisationService) {
    this.actualiserSync();
  }

  @HostListener('window:online')
  @HostListener('window:offline')
  @HostListener('window:focus')
  @HostListener('window:storage')
  actualiserLigne(): void {
    this.enLigne = navigator.onLine;
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
