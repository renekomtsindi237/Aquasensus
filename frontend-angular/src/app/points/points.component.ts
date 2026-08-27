import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-points',
  imports: [RouterLink],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="220" alt="AquaSensus" />
      <h1>Points d’eau</h1>
      <p class="intro">Consultation publique — aucun volume n’est demandé.</p>
      <p>
        <a routerLink="/signaler">Signaler un incident</a>
        ·
        <a routerLink="/file">File de travail</a>
        ·
        <a routerLink="/">Connexion</a>
      </p>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <ul>
        @for (p of points; track p.id) {
          <li>
            <strong>{{ p.code }}</strong> — {{ p.nomUsage }}
            <span class="etat">{{ p.etat }}</span>
            <small>{{ p.localiteChemin }}</small>
          </li>
        }
      </ul>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-6) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 220px); height: auto; }
    h1 { color: var(--aqs-color-text); font-size: var(--aqs-font-size-h2); }
    .intro, small { color: var(--aqs-color-text-secondary); }
    ul { list-style: none; padding: 0; }
    li { border: 1px solid var(--aqs-color-border); border-radius: var(--aqs-radius-md);
         padding: var(--aqs-space-4); margin-bottom: var(--aqs-space-3); background: var(--aqs-color-surface); }
    .etat { display: block; font-weight: 600; color: var(--aqs-color-action); }
    a { color: var(--aqs-color-action); }
    .erreur { color: var(--aqs-color-feedback-error); }
  `]
})
export class PointsComponent {
  points: PointPublic[] = [];
  erreur = '';

  constructor(http: HttpClient) {
    http.get<{ elements: PointPublic[] }>('/api/v1/water-points').subscribe({
      next: (r) => (this.points = r.elements),
      error: () => (this.erreur = 'Liste indisponible. Vérifiez que l’API tourne.')
    });
  }
}

interface PointPublic {
  id: string;
  code: string;
  nomUsage: string;
  etat: string;
  localiteChemin: string;
}
