import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthReponse, SessionService } from '../session.service';

@Component({
  selector: 'app-connexion',
  imports: [FormsModule, RouterLink],
  templateUrl: './connexion.component.html',
  styleUrl: './connexion.component.css'
})
export class ConnexionComponent {
  mode: 'connexion' | 'inscription' = 'connexion';
  identifiant = '';
  motDePasse = '';
  nomAffichage = '';
  message = '';
  enCours = false;

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly session: SessionService,
    private readonly route: ActivatedRoute
  ) {
    if (this.route.snapshot.queryParamMap.get('inscription') === '1') {
      this.mode = 'inscription';
    }
    if (this.session.connecte()) {
      void this.router.navigateByUrl(
        this.session.doitChanger() ? '/mot-de-passe' : this.session.accueil()
      );
    }
  }

  soumettre(): void {
    this.enCours = true;
    this.message = '';
    this.http
      .post<AuthReponse>('/api/v1/auth/login', {
        identifiant: this.identifiant,
        motDePasse: this.motDePasse
      })
      .subscribe({
        next: (r) => this.apresAuth(r),
        error: (err) => {
          this.message =
            err.status === 423
              ? 'Compte verrouillé. Réessayez plus tard.'
              : 'Identifiant ou mot de passe incorrect.';
          this.enCours = false;
        }
      });
  }

  inscrire(): void {
    this.enCours = true;
    this.message = '';
    this.http
      .post<AuthReponse>('/api/v1/auth/register', {
        identifiant: this.identifiant,
        nomAffichage: this.nomAffichage,
        motDePasse: this.motDePasse
      })
      .subscribe({
        next: (r) => this.apresAuth(r),
        error: (err) => {
          this.message =
            err.status === 409
              ? 'Un compte existe déjà pour cet identifiant.'
              : 'Inscription impossible. Identifiant, nom et mot de passe (10 caractères min.).';
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

  private apresAuth(r: AuthReponse): void {
    this.session.enregistrer(r);
    if (r.doitChangerMotDePasse) {
      void this.router.navigateByUrl('/mot-de-passe');
      return;
    }
    void this.router.navigateByUrl(this.session.accueil());
  }
}
