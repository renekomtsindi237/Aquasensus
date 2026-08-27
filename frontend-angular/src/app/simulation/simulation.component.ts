import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-simulation',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Simulation SMS / USSD</h1>
      <p class="intro">Aucun opérateur réel (DA-09, EF-64). Format SMS : AQS CODE SYMPTOME. USSD : *123#.</p>
      <section>
        <h2>SMS entrant</h2>
        <form (ngSubmit)="envoyerSms()">
          <label>Numéro fictif
            <input name="smsNum" [(ngModel)]="smsNumero" required />
          </label>
          <label>Contenu
            <input name="smsTxt" [(ngModel)]="smsContenu" required />
          </label>
          <button type="submit">Injecter</button>
        </form>
        @if (smsReponse) {
          <p role="status">{{ smsReponse }}</p>
        }
      </section>
      <section>
        <h2>Session USSD</h2>
        <form (ngSubmit)="envoyerUssd()">
          <label>Numéro fictif
            <input name="ussdNum" [(ngModel)]="ussdNumero" required />
          </label>
          <label>Saisie
            <input name="ussdTxt" [(ngModel)]="ussdSaisie" required />
          </label>
          <button type="submit">Envoyer</button>
        </form>
        @if (ussdEcran) {
          <pre>{{ ussdEcran }}</pre>
        }
      </section>
      <section>
        <h2>Journal</h2>
        <button type="button" (click)="chargerJournal()">Actualiser</button>
        <ul>
          @for (m of messages; track m.id) {
            <li>
              <strong>{{ m.direction }}</strong> {{ m.canal }} — {{ m.numeroFictif }}
              <span>{{ m.contenu }}</span>
            </li>
          }
        </ul>
      </section>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <p><a routerLink="/points">Retour</a></p>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    section { margin-bottom: var(--aqs-space-6); }
    label { display: flex; flex-direction: column; gap: var(--aqs-space-2); margin-bottom: var(--aqs-space-3); }
    input, button { min-height: 48px; }
    button { background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); font-weight: 600; padding: 0 var(--aqs-space-4); }
    pre { background: var(--aqs-color-surface-muted); padding: var(--aqs-space-4); white-space: pre-wrap; }
    ul { list-style: none; padding: 0; }
    li { border: 1px solid var(--aqs-color-border); padding: var(--aqs-space-3); margin-bottom: var(--aqs-space-2);
         background: var(--aqs-color-surface); }
    span { display: block; color: var(--aqs-color-text-secondary); }
    .intro { color: var(--aqs-color-text-secondary); }
    .erreur { color: var(--aqs-color-feedback-error); }
  `]
})
export class SimulationComponent {
  smsNumero = '237600011122';
  smsContenu = 'AQS YDE-042 PANNE';
  smsReponse = '';
  ussdNumero = '237600011133';
  ussdSaisie = '*123#';
  ussdSessionId: string | null = null;
  ussdEcran = '';
  messages: MessageSim[] = [];
  erreur = '';

  constructor(private readonly http: HttpClient) {
    this.chargerJournal();
  }

  envoyerSms(): void {
    this.erreur = '';
    this.http
      .post<{ reponse: string }>('/api/v1/simulation/sms/inbound', {
        numeroFictif: this.smsNumero,
        contenu: this.smsContenu
      })
      .subscribe({
        next: (r) => {
          this.smsReponse = r.reponse;
          this.chargerJournal();
        },
        error: () => (this.erreur = 'Console réservée à l’administrateur.')
      });
  }

  envoyerUssd(): void {
    this.erreur = '';
    this.http
      .post<{ sessionId: string | null; ecran: string; termine: boolean }>(
        '/api/v1/simulation/ussd/session',
        {
          sessionId: this.ussdSessionId,
          numeroFictif: this.ussdNumero,
          saisie: this.ussdSaisie
        }
      )
      .subscribe({
        next: (r) => {
          this.ussdEcran = r.ecran;
          this.ussdSessionId = r.termine ? null : r.sessionId;
          this.ussdSaisie = '';
          this.chargerJournal();
        },
        error: () => (this.erreur = 'Console réservée à l’administrateur.')
      });
  }

  chargerJournal(): void {
    this.http.get<MessageSim[]>('/api/v1/simulation/messages').subscribe({
      next: (m) => (this.messages = m),
      error: () => (this.erreur = 'Journal inaccessible (rôle ADMIN requis).')
    });
  }
}

interface MessageSim {
  id: string;
  direction: string;
  canal: string;
  numeroFictif: string;
  contenu: string;
}
