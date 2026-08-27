import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-referentiels',
  imports: [FormsModule],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Référentiels</h1>
      <p class="intro">Localités, comités, types de pièces, symptômes (EF-84). Suppression logique des comités. Aucun volume.</p>
      @if (message) {
        <p class="ok">{{ message }}</p>
      }
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <h2>Import CSV d’ouvrages</h2>
      <textarea name="csv" [(ngModel)]="csv" rows="5"
        placeholder="code,nomUsage,type,latitude,longitude,localiteCode,comiteId,populationDesservie"></textarea>
      <button type="button" (click)="importer()">Importer</button>
      <h2>Types de pièces</h2>
      <ul>
        @for (t of pieces; track t.code) {
          <li>{{ t.code }} — {{ t.libelle }}</li>
        }
      </ul>
      <h2>Catégories de symptômes</h2>
      <p>{{ symptomes.join(', ') }}</p>
    </main>
  `,
  styles: [`
    .page { max-width: 40rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    .intro { color: var(--aqs-color-text-secondary); }
    textarea, button { width: 100%; min-height: 48px; margin-bottom: var(--aqs-space-3); }
    button { background: var(--aqs-color-action); color: var(--aqs-color-text-on-action); border: 0;
             border-radius: var(--aqs-radius-md); font-weight: 600; }
    .ok { color: var(--aqs-color-action); }
    .erreur { color: var(--aqs-color-feedback-error); }
    ul { list-style: none; padding: 0; }
  `]
})
export class AdminReferentielsComponent {
  csv = '';
  message = '';
  erreur = '';
  pieces: { code: string; libelle: string }[] = [];
  symptomes: string[] = [];

  constructor(private readonly http: HttpClient) {
    this.http.get<{ code: string; libelle: string }[]>('/api/v1/admin/types-pieces').subscribe({
      next: (p) => (this.pieces = p),
      error: () => (this.erreur = 'Réservé à l’administrateur.')
    });
    this.http.get<string[]>('/api/v1/admin/symptomes').subscribe({
      next: (s) => (this.symptomes = s)
    });
  }

  importer(): void {
    this.http
      .post<{ lignes: { numero: number; ok: boolean; message: string }[] }>(
        '/api/v1/water-points/import',
        this.csv,
        { headers: { 'Content-Type': 'text/csv' } }
      )
      .subscribe({
        next: (r) => {
          const ko = (r.lignes ?? []).filter((l) => !l.ok);
          this.message = ko.length
            ? ko.map((l) => `Ligne ${l.numero} : ${l.message}`).join(' ')
            : 'Import terminé (rapport ligne à ligne).';
        },
        error: () => (this.erreur = 'Import refusé.')
      });
  }
}
