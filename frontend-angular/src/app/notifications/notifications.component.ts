import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-notifications',
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Notifications in-app</h1>
      <p class="intro">Canaux in-app et SMS simulé (EF-70, EF-71). Aucun volume d’eau.</p>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <ul>
        @for (n of items; track n.id) {
          <li>
            <strong>{{ n.titre }}</strong>
            <span>{{ n.canal }} · {{ n.statut }}</span>
            <p>{{ n.corps }}</p>
          </li>
        }
      </ul>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    .intro { color: var(--aqs-color-text-secondary); }
    ul { list-style: none; padding: 0; }
    li { border: 1px solid var(--aqs-color-border); border-radius: var(--aqs-radius-md);
         padding: var(--aqs-space-4); margin-bottom: var(--aqs-space-3); background: var(--aqs-color-surface); }
    span { display: block; color: var(--aqs-color-text-secondary); }
    .erreur { color: var(--aqs-color-feedback-error); }
  `]
})
export class NotificationsComponent {
  items: Notif[] = [];
  erreur = '';

  constructor(http: HttpClient) {
    http.get<Notif[]>('/api/v1/notifications').subscribe({
      next: (n) => (this.items = n),
      error: () => (this.erreur = 'Connectez-vous pour voir les notifications.')
    });
  }
}

interface Notif {
  id: string;
  canal: string;
  titre: string;
  corps: string;
  statut: string;
}
