import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-file',
  imports: [RouterLink, FormsModule],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="220" alt="AquaSensus" />
      <h1>File de travail</h1>
      <p class="intro">Signalements à qualifier et interventions actives du périmètre — aucun volume d’eau.</p>
      <p>
        <a routerLink="/points">Points d’eau</a>
        ·
        <a routerLink="/">Connexion</a>
      </p>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      @if (message) {
        <p class="ok" role="status">{{ message }}</p>
      }
      <h2>À qualifier</h2>
      @if (file.signalementsAQualifier.length === 0) {
        <p class="vide">Aucun signalement en attente.</p>
      }
      <label>Motif de qualification
        <input name="motif" [(ngModel)]="motif" />
      </label>
      <ul>
        @for (s of file.signalementsAQualifier; track s.id) {
          <li>
            <strong>{{ s.reference }}</strong>
            <span>{{ s.categorie }}</span>
            <small>Priorité {{ s.priorite }}</small>
            <button type="button" (click)="qualifier(s.id, 'QUALIFIE')">Qualifier</button>
            <button type="button" (click)="qualifier(s.id, 'REJETE')">Rejeter</button>
          </li>
        }
      </ul>
      <h2>Interventions actives</h2>
      @if (file.interventionsActives.length === 0) {
        <p class="vide">Aucune intervention ouverte.</p>
      }
      <ul>
        @for (i of file.interventionsActives; track i.id) {
          <li>
            <strong>{{ i.reference }}</strong>
            <span>{{ i.statut }}</span>
            @if (i.echeanceSouhaitee) {
              <small>Échéance {{ i.echeanceSouhaitee }}</small>
            }
          </li>
        }
      </ul>
      <h2>Alertes à traiter</h2>
      @if (file.alertesActives.length === 0) {
        <p class="vide">Aucune alerte active.</p>
      }
      <ul>
        @for (a of file.alertesActives; track a.id) {
          <li>
            <strong>{{ a.typeRegle }}</strong>
            <span>{{ a.niveau }}</span>
            <small>{{ a.explication }}</small>
          </li>
        }
      </ul>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-6) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 220px); height: auto; }
    h1, h2 { color: var(--aqs-color-text); }
    h1 { font-size: var(--aqs-font-size-h2); }
    h2 { font-size: var(--aqs-font-size-h3); margin-top: var(--aqs-space-6); }
    .intro, .vide, small { color: var(--aqs-color-text-secondary); }
    ul { list-style: none; padding: 0; }
    li { border: 1px solid var(--aqs-color-border); border-radius: var(--aqs-radius-md);
         padding: var(--aqs-space-4); margin-bottom: var(--aqs-space-3); background: var(--aqs-color-surface); }
    span { display: block; font-weight: 600; color: var(--aqs-color-action); }
    a { color: var(--aqs-color-action); }
    .erreur { color: var(--aqs-color-feedback-error); }
    .ok { color: var(--aqs-color-action); font-weight: 600; }
    label { display: flex; flex-direction: column; gap: var(--aqs-space-2); margin-bottom: var(--aqs-space-3); }
    input, button { min-height: 48px; }
    button { margin-right: var(--aqs-space-2); margin-top: var(--aqs-space-2);
             background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); font-weight: 600; padding: 0 var(--aqs-space-3); }
  `]
})
export class FileComponent {
  file: FileTravail = { signalementsAQualifier: [], interventionsActives: [], alertesActives: [] };
  erreur = '';
  message = '';
  motif = '';

  constructor(private readonly http: HttpClient) {
    this.http.get<FileTravail>('/api/v1/work-queue').subscribe({
      next: (r) => (this.file = r),
      error: () =>
        (this.erreur = 'File indisponible. Connectez-vous en délégué ou administrateur.')
    });
  }

  qualifier(id: string, decision: 'QUALIFIE' | 'REJETE'): void {
    this.erreur = '';
    this.message = '';
    this.http
      .patch<{ statut: string }>(`/api/v1/reports/${id}/qualification`, {
        decision,
        motif: this.motif
      })
      .subscribe({
        next: (r) => (this.message = 'Décision enregistrée : ' + r.statut),
        error: (err) =>
          (this.erreur =
            err.status === 422
              ? 'Un motif est obligatoire pour qualifier ou rejeter.'
              : 'Qualification impossible.')
      });
  }
}

interface FileTravail {
  signalementsAQualifier: { id: string; reference: string; categorie: string; priorite: number }[];
  interventionsActives: {
    id: string;
    reference: string;
    statut: string;
    echeanceSouhaitee: string | null;
  }[];
  alertesActives: { id: string; typeRegle: string; niveau: string; explication: string }[];
}
