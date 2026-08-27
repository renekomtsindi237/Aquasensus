import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-mot-de-passe',
  imports: [FormsModule],
  template: `
    <main class="ecran">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="280" height="80" alt="AquaSensus" />
      <h1>Changer le mot de passe</h1>
      <p class="intro">Compte créé par un administrateur : mot de passe temporaire à remplacer (EF-83). Aucun volume d’eau.</p>
      <form (ngSubmit)="soumettre()">
        <label>
          Mot de passe actuel
          <input name="actuel" type="password" [(ngModel)]="actuel" autocomplete="current-password" required />
        </label>
        <label>
          Nouveau mot de passe
          <input
            name="nouveau"
            type="password"
            [(ngModel)]="nouveau"
            autocomplete="new-password"
            minlength="10"
            required
          />
        </label>
        <button type="submit" [disabled]="enCours">Enregistrer</button>
      </form>
      @if (message) {
        <p class="message" role="status">{{ message }}</p>
      }
    </main>
  `,
  styleUrl: '../connexion/connexion.component.css'
})
export class MotDePasseComponent {
  actuel = '';
  nouveau = '';
  message = '';
  enCours = false;

  constructor(
    private readonly http: HttpClient,
    private readonly session: SessionService,
    private readonly router: Router
  ) {}

  soumettre(): void {
    this.enCours = true;
    this.message = '';
    this.http
      .post('/api/v1/auth/password/change', { actuel: this.actuel, nouveau: this.nouveau }, { responseType: 'text' })
      .subscribe({
      next: () => {
        this.session.motDePasseChange();
        void this.router.navigateByUrl(this.session.accueil());
      },
      error: (err) => {
        this.message =
          err.status === 401
            ? 'Mot de passe actuel incorrect.'
            : 'Changement impossible. Vérifiez la longueur (10 caractères minimum).';
        this.enCours = false;
      }
    });
  }
}
