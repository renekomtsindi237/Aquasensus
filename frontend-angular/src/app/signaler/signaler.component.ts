import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { empilerSignalement, lireBrouillon, sauverBrouillon } from '../file-locale';

@Component({
  selector: 'app-signaler',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="page">
      <h1>Signaler un incident</h1>
      <p class="intro">Moins d’une minute. Code simulé : 123456 (pas d’opérateur SMS).</p>
      <form (ngSubmit)="envoyer()">
        <label>Code du point d’eau
          <input name="code" [(ngModel)]="code" required (ngModelChange)="sauver()" />
        </label>
        <label>Symptôme
          <select name="categorie" [(ngModel)]="categorie">
            <option value="PANNE_TOTALE">Panne totale</option>
            <option value="DEBIT_FAIBLE">Débit faible</option>
            <option value="EAU_TROUBLE">Eau trouble</option>
            <option value="EAU_MALODORANTE">Eau malodorante</option>
            <option value="BRUIT_ANORMAL">Bruit anormal</option>
            <option value="FUITE">Fuite</option>
            <option value="DEGRADATION_OUVRAGE">Dégradation</option>
            <option value="ATTENTE_EXCESSIVE">Attente excessive</option>
            <option value="AUTRE">Autre</option>
          </select>
        </label>
        <label>Gravité
          <select name="gravite" [(ngModel)]="gravite">
            <option value="FAIBLE">Faible</option>
            <option value="MOYENNE">Moyenne</option>
            <option value="HAUTE">Haute</option>
          </select>
        </label>
        <label>Téléphone
          <input name="tel" [(ngModel)]="telephone" required />
        </label>
        <label>Code de confirmation
          <input name="otp" [(ngModel)]="otp" required />
        </label>
        <button type="submit" [disabled]="enCours">Envoyer</button>
      </form>
      @if (message) {
        <p role="status">{{ message }}</p>
      }
      <p><a routerLink="/points">Retour à la liste</a></p>
    </main>
  `,
  styles: [`
    .page { max-width: 28rem; margin: var(--aqs-space-6) auto; padding: var(--aqs-space-4); }
    label { display: flex; flex-direction: column; gap: var(--aqs-space-2); margin-bottom: var(--aqs-space-4); }
    input, select, button { min-height: 48px; }
    button { width: 100%; background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); font-weight: 600; }
    a { color: var(--aqs-color-action); }
    .intro { color: var(--aqs-color-text-secondary); }
  `]
})
export class SignalerComponent {
  code = '';
  categorie = 'DEBIT_FAIBLE';
  gravite = 'MOYENNE';
  telephone = '';
  otp = '123456';
  message = '';
  enCours = false;

  constructor(private readonly http: HttpClient) {
    const b = lireBrouillon();
    this.code = b['code'] ?? '';
    this.telephone = b['telephone'] ?? '';
  }

  sauver(): void {
    sauverBrouillon({ code: this.code, telephone: this.telephone, categorie: this.categorie, gravite: this.gravite });
  }

  envoyer(): void {
    this.enCours = true;
    this.sauver();
    const uuid = crypto.randomUUID();
    const corps = {
      pointEauCode: this.code,
      categorie: this.categorie,
      gravite: this.gravite,
      canal: 'WEB',
      declarantTelephone: this.telephone,
      codeOtp: this.otp
    };
    const headers = new HttpHeaders({ 'X-Client-Request-Id': uuid });
    this.http
      .post<{ priseEnCharge: { message: string } }>('/api/v1/reports', corps, { headers })
      .subscribe({
        next: (r) => {
          this.message = r.priseEnCharge.message;
          this.enCours = false;
        },
        error: (err) => {
          if (err.status === 0) {
            empilerSignalement(uuid, corps, 'Signalement ' + this.code);
            this.message = 'Hors ligne : signalement mis en file locale (EN_ATTENTE). Même identifiant au rejeu.';
          } else if (err.status === 429) {
            this.message = 'Trop de signalements depuis ce numéro. Réessayez plus tard.';
          } else if (err.status === 404) {
            this.message = 'Ouvrage introuvable. Vérifiez le code du point d’eau.';
          } else if (err.status === 422) {
            this.message = 'Code de confirmation incorrect.';
          } else {
            this.message = 'Envoi impossible. Vérifiez le code du forage et le formulaire.';
          }
          this.enCours = false;
        }
      });
  }
}
