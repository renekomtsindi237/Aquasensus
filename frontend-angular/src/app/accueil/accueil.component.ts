import { KeyValuePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-accueil',
  imports: [RouterLink, KeyValuePipe],
  templateUrl: './accueil.component.html',
  styleUrl: './accueil.component.css'
})
export class AccueilComponent {
  readonly session = inject(SessionService);
  readonly role = this.session.rolePrincipal();
  erreur = '';
  kpi: Kpi | null = null;
  file: FileTravail = { signalementsAQualifier: [], interventionsActives: [], alertesActives: [] };
  users: { identifiant: string; statut: string }[] = [];
  points: { code: string; nomUsage: string; etat: string }[] = [];

  constructor(http: HttpClient) {
    if (this.session.peutKpi()) {
      http.get<Kpi>('/api/v1/dashboard/kpi').subscribe({
        next: (k) => (this.kpi = k),
        error: () => (this.erreur = 'Indicateurs indisponibles pour ce compte.')
      });
    }
    if (this.session.peutFile()) {
      http.get<FileTravail>('/api/v1/work-queue').subscribe({
        next: (f) => (this.file = f),
        error: () => {
          if (this.role === 'DELEGUE') {
            this.erreur = 'File indisponible. Connectez-vous en délégué ou administrateur.';
          }
        }
      });
    }
    if (this.session.peutAdmin()) {
      http.get<{ identifiant: string; statut: string }[]>('/api/v1/users').subscribe({
        next: (u) => (this.users = u),
        error: () => undefined
      });
    }
    if (this.role === 'USAGER' || this.role === 'TECHNICIEN') {
      http.get<{ elements: { code: string; nomUsage: string; etat: string }[] }>('/api/v1/water-points').subscribe({
        next: (r) => (this.points = r.elements ?? []),
        error: () => (this.erreur = 'Liste des ouvrages indisponible.')
      });
    }
  }

  minutes(v: number | null | undefined): string {
    return v == null ? '—' : v + ' min';
  }

  maxEtat(): number {
    if (!this.kpi) {
      return 1;
    }
    return Math.max(1, ...Object.values(this.kpi.pointsParEtat));
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

interface FileTravail {
  signalementsAQualifier: { id: string; reference: string; categorie: string; priorite: number }[];
  interventionsActives: { id: string; reference: string; statut: string; echeanceSouhaitee: string | null }[];
  alertesActives: { id: string; typeRegle: string; niveau: string; explication: string }[];
}
