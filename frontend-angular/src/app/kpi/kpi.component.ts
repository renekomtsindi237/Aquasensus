import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-kpi',
  imports: [FormsModule],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Tableau de bord</h1>
      <p class="intro">Agrégats SQL (EF-53, EF-54). HORS_SERVICE hors disponibilité (RG-12). Aucun volume d’eau.</p>
      <form (ngSubmit)="charger()">
        <label>Début
          <input type="datetime-local" name="debut" [(ngModel)]="debut" />
        </label>
        <label>Fin
          <input type="datetime-local" name="fin" [(ngModel)]="fin" />
        </label>
        <label>Localité (UUID)
          <input name="localite" [(ngModel)]="localiteId" />
        </label>
        <label>Comité (UUID)
          <input name="comite" [(ngModel)]="comiteId" />
        </label>
        <button type="submit">Actualiser</button>
        <button type="button" (click)="exporter()">Export CSV</button>
        <button type="button" (click)="exporterPdf()">Export PDF</button>
      </form>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      @if (kpi) {
        <dl>
          <dt>Rétablissement médian</dt>
          <dd>{{ minutes(kpi.retablissementMedianMinutes) }}</dd>
          <dt>Rétablissement P90</dt>
          <dd>{{ minutes(kpi.retablissementP90Minutes) }}</dd>
          <dt>Ouvrages suivis (hors HORS_SERVICE)</dt>
          <dd>{{ kpi.ouvragesActifsHorsHorsService }}</dd>
          <dt>Hors service exclus</dt>
          <dd>{{ kpi.horsServiceExclus }}</dd>
          <dt>Alertes actives</dt>
          <dd>{{ kpi.alertesActives }}</dd>
          <dt>Interventions en cours</dt>
          <dd>{{ kpi.interventionsEnCours }}</dd>
          <dt>Délai affectation médian</dt>
          <dd>{{ minutes(kpi.delaiAffectationMedianMinutes) }}</dd>
          <dt>Taux d’anticipation</dt>
          <dd>{{ (kpi.tauxAnticipation * 100).toFixed(0) }} %</dd>
        </dl>
        <h2>Points par état</h2>
        <ul>
          @for (e of etats; track e) {
            <li>{{ e }} : {{ kpi.pointsParEtat[e] || 0 }}</li>
          }
        </ul>
        <p class="note">{{ kpi.note }}</p>
        @if (budget.length) {
          <h2>Budget indicatif (EF-27)</h2>
          <ul>
            @for (b of budget; track b.comiteId) {
              <li>Comité {{ b.comiteId }} — pièces {{ b.coutPieces }} / main-d’œuvre {{ b.coutMainOeuvre }}</li>
            }
          </ul>
        }
      }
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    form { display: grid; gap: var(--aqs-space-3); margin-bottom: var(--aqs-space-4); }
    label { display: flex; flex-direction: column; gap: var(--aqs-space-2); }
    input, button { min-height: 48px; }
    button { background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); font-weight: 600; }
    dl { display: grid; grid-template-columns: 1fr auto; gap: var(--aqs-space-2) var(--aqs-space-4); }
    dt { color: var(--aqs-color-text-secondary); }
    dd { margin: 0; font-weight: 600; }
    .intro, .note { color: var(--aqs-color-text-secondary); }
    .erreur { color: var(--aqs-color-feedback-error); }
    ul { list-style: none; padding: 0; }
  `]
})
export class KpiComponent {
  debut = '';
  fin = '';
  localiteId = '';
  comiteId = '';
  kpi: Kpi | null = null;
  budget: BudgetLigne[] = [];
  erreur = '';
  readonly etats = [
    'OPERATIONNEL',
    'SOUS_SURVEILLANCE',
    'RISQUE_ELEVE',
    'EN_PANNE',
    'EN_REPARATION',
    'HORS_SERVICE'
  ];

  constructor(private readonly http: HttpClient) {
    this.charger();
  }

  minutes(v: number | null): string {
    return v == null ? '—' : v + ' min';
  }

  charger(): void {
    this.erreur = '';
    const params = this.params();
    this.http.get<Kpi>('/api/v1/dashboard/kpi', { params }).subscribe({
      next: (k) => (this.kpi = k),
      error: () => (this.erreur = 'KPI réservés au partenaire, délégué ou administrateur. Connectez-vous.')
    });
    this.http.get<BudgetLigne[]>('/api/v1/dashboard/budget', { params }).subscribe({
      next: (b) => (this.budget = b),
      error: () => (this.budget = [])
    });
  }

  exporter(): void {
    const params = this.params();
    this.http.get('/api/v1/dashboard/export', { params, responseType: 'text' }).subscribe({
      next: (csv) => {
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'aquasensus-kpi.csv';
        a.click();
      },
      error: () => (this.erreur = 'Export impossible.')
    });
  }

  exporterPdf(): void {
    const params = this.params();
    this.http.get('/api/v1/dashboard/export.pdf', { params, responseType: 'blob' }).subscribe({
      next: (blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'aquasensus-synthese.pdf';
        a.click();
      },
      error: () => (this.erreur = 'Export PDF impossible.')
    });
  }

  private params(): HttpParams {
    let params = new HttpParams();
    if (this.debut) {
      params = params.set('debut', new Date(this.debut).toISOString());
    }
    if (this.fin) {
      params = params.set('fin', new Date(this.fin).toISOString());
    }
    if (this.localiteId) {
      params = params.set('localiteId', this.localiteId);
    }
    if (this.comiteId) {
      params = params.set('comiteId', this.comiteId);
    }
    return params;
  }
}

interface Kpi {
  retablissementMedianMinutes: number | null;
  retablissementP90Minutes: number | null;
  pointsParEtat: Record<string, number>;
  horsServiceExclus: number;
  ouvragesActifsHorsHorsService: number;
  alertesActives: number;
  interventionsEnCours: number;
  delaiAffectationMedianMinutes: number | null;
  tauxAnticipation: number;
  note: string;
}

interface BudgetLigne {
  comiteId: string;
  coutPieces: number;
  coutMainOeuvre: number;
}
