import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-comptes',
  imports: [FormsModule],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Comptes</h1>
      <p class="intro">ADMIN uniquement (EF-81). Premier mot de passe à changer. Aucun volume d’eau.</p>
      <form (ngSubmit)="creer()">
        <label>Identifiant <input name="id" [(ngModel)]="identifiant" required /></label>
        <label>Nom <input name="nom" [(ngModel)]="nom" required /></label>
        <label>Mot de passe temporaire <input name="mdp" [(ngModel)]="tempo" required /></label>
        <button type="submit">Créer un technicien</button>
      </form>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <ul>
        @for (u of users; track u.id) {
          <li>
            {{ u.identifiant }} — {{ u.statut }}
            @if (u.statut === 'ACTIF') {
              <button type="button" (click)="suspendre(u.id)">Suspendre</button>
            }
          </li>
        }
      </ul>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    label { display: flex; flex-direction: column; gap: var(--aqs-space-2); margin-bottom: var(--aqs-space-3); }
    input, button { min-height: 48px; }
    button { background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); }
    .intro { color: var(--aqs-color-text-secondary); }
    .erreur { color: var(--aqs-color-feedback-error); }
    ul { list-style: none; padding: 0; }
    li { border: 1px solid var(--aqs-color-border); padding: var(--aqs-space-3); margin-bottom: var(--aqs-space-2); }
  `]
})
export class ComptesComponent {
  users: Compte[] = [];
  identifiant = '';
  nom = '';
  tempo = 'Tempo!2026';
  erreur = '';

  constructor(private readonly http: HttpClient) {
    this.charger();
  }

  charger(): void {
    this.http.get<Compte[]>('/api/v1/users').subscribe({
      next: (u) => (this.users = u),
      error: () => (this.erreur = 'Réservé à l’administrateur.')
    });
  }

  creer(): void {
    this.http
      .post('/api/v1/users', {
        identifiant: this.identifiant,
        nomAffichage: this.nom,
        motDePasseTemporaire: this.tempo,
        roles: ['TECHNICIEN'],
        comiteIds: []
      })
      .subscribe({
        next: () => this.charger(),
        error: (err) =>
          (this.erreur =
            err.status === 409
              ? 'Un compte existe déjà pour cet identifiant.'
              : 'Création impossible.')
      });
  }

  suspendre(id: string): void {
    this.http.patch('/api/v1/users/' + id, { statut: 'SUSPENDU' }).subscribe({
      next: () => this.charger(),
      error: () => (this.erreur = 'Suspension impossible.')
    });
  }
}

interface Compte {
  id: string;
  identifiant: string;
  statut: string;
}
