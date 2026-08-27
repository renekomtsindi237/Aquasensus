import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  imports: [RouterLink],
  template: `
    <header class="haut">
      <img src="assets/brand/aquasensus-logo.png" width="220" height="50" alt="AquaSensus" />
      <nav aria-label="Accès compte">
        <a routerLink="/points">Points d’eau</a>
        <a class="btn-ghost" routerLink="/connexion">Se connecter</a>
        <a class="btn" routerLink="/connexion" [queryParams]="{ inscription: '1' }">Créer un compte</a>
      </nav>
    </header>
    <main class="hero" id="contenu-landing">
      <p class="signature">Anticiper la panne, garder l’eau.</p>
      <h1>Le suivi partagé des forages communautaires</h1>
      <p class="lead">
        AquaSensus aide les quartiers périphériques de Yaoundé et les zones rurales du Cameroun à
        signaler, coordonner et anticiper les pannes — avant que des centaines de familles soient
        privées d’eau potable. Aucun volume d’eau n’est relevé : les habitants puisent librement.
      </p>
      <p class="actions">
        <a class="btn" routerLink="/signaler">Signaler un incident</a>
        <a class="btn-ghost" routerLink="/connexion">J’ai déjà un compte</a>
      </p>
    </main>
    <section class="piliers" aria-label="Ce que fait AquaSensus">
      <article>
        <h2>Signalement humain</h2>
        <p>Un habitant, un SMS simulé ou un délégué : le comité est prévenu sans capteur cher.</p>
      </article>
      <article>
        <h2>File de maintenance</h2>
        <p>Qualification, affectation du technicien, suivi jusqu’au rétablissement.</p>
      </article>
      <article>
        <h2>Alerte d’usure</h2>
        <p>Charge d’usage estimée (population, saison, dernière maintenance) — jamais un compteur.</p>
      </article>
    </section>
  `,
  styles: [`
    .haut {
      display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between;
      gap: var(--aqs-space-4); padding: var(--aqs-space-4) var(--aqs-space-6);
      background: var(--aqs-color-surface); border-bottom: 1px solid var(--aqs-color-border);
    }
    .haut img { width: min(100%, 220px); height: auto; }
    .haut nav { display: flex; flex-wrap: wrap; gap: var(--aqs-space-3); align-items: center; }
    .haut a { color: var(--aqs-color-action); min-height: 48px; display: inline-flex; align-items: center; font-weight: 600; }
    .hero {
      max-width: 48rem; margin: 0 auto; padding: var(--aqs-space-12) var(--aqs-space-6);
    }
    .signature {
      color: var(--aqs-color-accent-text); font-weight: 700; letter-spacing: 0.02em;
      margin: 0 0 var(--aqs-space-3);
    }
    h1 { font-size: var(--aqs-font-size-h1); margin: 0 0 var(--aqs-space-4); line-height: var(--aqs-line-height-tight); }
    .lead { color: var(--aqs-color-text-secondary); font-size: var(--aqs-font-size-body-lg); margin: 0 0 var(--aqs-space-8); }
    .actions { display: flex; flex-wrap: wrap; gap: var(--aqs-space-3); }
    .btn, .btn-ghost {
      min-height: 48px; padding: 0 var(--aqs-space-5); border-radius: var(--aqs-radius-md);
      display: inline-flex; align-items: center; font-weight: 600; text-decoration: none;
    }
    .btn { background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); }
    .btn-ghost { background: var(--aqs-color-action-subtle); color: var(--aqs-color-action); }
    .piliers {
      display: grid; gap: var(--aqs-space-4); padding: 0 var(--aqs-space-6) var(--aqs-space-12);
      max-width: 72rem; margin: 0 auto;
      grid-template-columns: repeat(auto-fit, minmax(16rem, 1fr));
    }
    .piliers article {
      background: var(--aqs-color-surface); border: 1px solid var(--aqs-color-border);
      border-radius: var(--aqs-radius-lg); padding: var(--aqs-space-6); box-shadow: var(--aqs-elev-1);
    }
    .piliers h2 { font-size: var(--aqs-font-size-h4); margin: 0 0 var(--aqs-space-3); }
    .piliers p { margin: 0; color: var(--aqs-color-text-secondary); }
  `]
})
export class LandingComponent {}
