import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-connexion',
  imports: [FormsModule, RouterLink],
  templateUrl: './connexion.component.html',
  styleUrl: './connexion.component.css'
})
export class ConnexionComponent {
  identifiant = '';
  motDePasse = '';
  message = '';
  enCours = false;

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  soumettre(): void {
    this.enCours = true;
    this.message = '';
    this.http
      .post<{ nomAffichage: string; jetonAcces: string; roles: string[]; doitChangerMotDePasse: boolean }>(
        '/api/v1/auth/login',
        {
          identifiant: this.identifiant,
          motDePasse: this.motDePasse
        }
      )
      .subscribe({
        next: (r) => {
          sessionStorage.setItem('aqs.jeton', r.jetonAcces);
          if (r.doitChangerMotDePasse) {
            this.message = 'Changez le mot de passe temporaire (EF-83) via l’API ou un administrateur.';
            this.enCours = false;
            return;
          }
          const roles = r.roles ?? [];
          const file = roles.includes('DELEGUE') || roles.includes('ADMIN');
          void this.router.navigateByUrl(file ? '/file' : '/points');
        },
        error: (err) => {
          this.message =
            err.status === 423
              ? 'Compte verrouillé. Réessayez plus tard.'
              : 'Identifiant ou mot de passe incorrect.';
          this.enCours = false;
        }
      });
  }

  demanderReset(): void {
    this.http
      .post('/api/v1/auth/password/reset-request', { identifiant: this.identifiant })
      .subscribe({
        next: () =>
          (this.message = 'Si un compte correspond, un code a été envoyé (canal simulé).'),
        error: () => (this.message = 'Demande impossible.')
      });
  }
}
