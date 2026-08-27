import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-carte',
  imports: [RouterLink],
  template: `
    <main class="page">
      <img class="logo" src="assets/brand/aquasensus-logo.png" width="180" alt="AquaSensus" />
      <h1>Carte des points d’eau</h1>
      <p class="intro">OpenStreetMap (fond neutre). État = couleur + forme + libellé. Aucun volume.</p>
      <p class="attrib">© contributeurs OpenStreetMap</p>
      @if (erreur) {
        <p class="erreur">{{ erreur }}</p>
      }
      <div class="scene" role="region" [attr.aria-label]="'Carte de ' + marqueurs.length + ' points d’eau'">
        @for (m of affiches; track m.id) {
          <a class="mark" [routerLink]="['/points']" [style.left.%]="m.x" [style.top.%]="m.y"
             [attr.data-forme]="m.formeMarqueur" [attr.title]="m.code + ' — ' + m.libelleEtat">
            <span class="forme" [attr.data-forme]="m.formeMarqueur" [attr.data-etat]="m.etat"></span>
            <span class="lib">{{ m.libelleEtat }}</span>
          </a>
        }
        @if (groupes.length) {
          @for (g of groupes; track g.cle) {
            <div class="cluster" [style.left.%]="g.x" [style.top.%]="g.y" [class.panne]="g.panne">
              {{ g.n }}
            </div>
          }
        }
      </div>
      <ul class="legende">
        <li><span class="forme" data-forme="cercle-plein" data-etat="OPERATIONNEL"></span> Opérationnel</li>
        <li><span class="forme" data-forme="cercle-anneau" data-etat="SOUS_SURVEILLANCE"></span> Sous surveillance</li>
        <li><span class="forme" data-forme="triangle" data-etat="RISQUE_ELEVE"></span> Risque élevé</li>
        <li><span class="forme" data-forme="losange" data-etat="EN_PANNE"></span> En panne</li>
        <li><span class="forme" data-forme="losange-lisere" data-etat="EN_REPARATION"></span> En réparation</li>
        <li><span class="forme" data-forme="cercle-barre" data-etat="HORS_SERVICE"></span> Hors service</li>
      </ul>
    </main>
  `,
  styles: [`
    .page { max-width: 56rem; margin: var(--aqs-space-4) auto; padding: var(--aqs-space-4); }
    .logo { width: min(100%, 180px); height: auto; }
    h1 { font-size: var(--aqs-font-size-h2); }
    .intro, .attrib { color: var(--aqs-color-text-secondary); font-size: var(--aqs-font-size-caption); }
    .scene {
      position: relative; height: 28rem; border: 1px solid var(--aqs-color-border);
      border-radius: var(--aqs-radius-md); background: var(--aqs-color-surface-muted);
      overflow: hidden;
    }
    .mark { position: absolute; transform: translate(-50%, -50%); text-decoration: none; text-align: center; min-width: 48px; }
    .lib { display: block; font-size: var(--aqs-font-size-caption); color: var(--aqs-color-text); font-weight: 600; }
    .forme { display: inline-block; width: 16px; height: 16px; vertical-align: middle; margin-right: 4px; }
    .forme[data-forme="cercle-plein"] { border-radius: 50%; background: var(--aqs-color-state-operationnel); }
    .forme[data-forme="cercle-anneau"] { border-radius: 50%; border: 3px solid var(--aqs-color-state-surveillance); background: var(--aqs-color-state-surveillance-bg); }
    .forme[data-forme="triangle"] { width: 0; height: 0; border-left: 8px solid transparent; border-right: 8px solid transparent; border-bottom: 14px solid var(--aqs-color-state-risque); background: transparent; }
    .forme[data-forme="losange"] { transform: rotate(45deg); background: var(--aqs-color-state-panne); }
    .forme[data-forme="losange-lisere"] { transform: rotate(45deg); background: var(--aqs-color-state-reparation); box-shadow: 0 0 0 2px var(--aqs-color-state-reparation-text); }
    .forme[data-forme="cercle-barre"] { border-radius: 50%; background: var(--aqs-color-state-hors-service); position: relative; }
    .cluster {
      position: absolute; transform: translate(-50%, -50%); min-width: 48px; min-height: 48px;
      border-radius: 50%; background: var(--aqs-color-brand-deep); color: var(--aqs-color-text-on-action);
      display: flex; align-items: center; justify-content: center; font-weight: 700;
    }
    .cluster.panne { box-shadow: 0 0 0 3px var(--aqs-color-state-panne); }
    .legende { list-style: none; padding: 0; display: grid; grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr)); gap: var(--aqs-space-2); }
    .erreur { color: var(--aqs-color-feedback-error); }
  `]
})
export class CarteComponent {
  marqueurs: Marqueur[] = [];
  affiches: (Marqueur & { x: number; y: number })[] = [];
  groupes: { cle: string; x: number; y: number; n: number; panne: boolean }[] = [];
  erreur = '';

  constructor(http: HttpClient) {
    http.get<Marqueur[]>('/api/v1/water-points/map').subscribe({
      next: (r) => {
        this.marqueurs = r;
        this.projeter();
      },
      error: () => (this.erreur = 'Carte indisponible.')
    });
  }

  private projeter(): void {
    if (!this.marqueurs.length) {
      return;
    }
    const lats = this.marqueurs.map((m) => Number(m.latitude));
    const lons = this.marqueurs.map((m) => Number(m.longitude));
    const minLa = Math.min(...lats) - 0.02;
    const maxLa = Math.max(...lats) + 0.02;
    const minLo = Math.min(...lons) - 0.02;
    const maxLo = Math.max(...lons) + 0.02;
    const points = this.marqueurs.map((m) => ({
      ...m,
      x: ((Number(m.longitude) - minLo) / (maxLo - minLo)) * 100,
      y: (1 - (Number(m.latitude) - minLa) / (maxLa - minLa)) * 100
    }));
    if (points.length <= 50) {
      this.affiches = points;
      this.groupes = [];
      return;
    }
    const buckets = new Map<string, typeof points>();
    for (const p of points) {
      const cle = Math.round(p.x / 8) + ':' + Math.round(p.y / 8);
      const arr = buckets.get(cle) ?? [];
      arr.push(p);
      buckets.set(cle, arr);
    }
    this.affiches = [];
    this.groupes = [];
    for (const [cle, arr] of buckets) {
      if (arr.length === 1) {
        this.affiches.push(arr[0]);
      } else {
        this.groupes.push({
          cle,
          x: arr.reduce((s, a) => s + a.x, 0) / arr.length,
          y: arr.reduce((s, a) => s + a.y, 0) / arr.length,
          n: arr.length,
          panne: arr.some((a) => a.etat === 'EN_PANNE')
        });
      }
    }
  }
}

interface Marqueur {
  id: string;
  code: string;
  latitude: number;
  longitude: number;
  etat: string;
  libelleEtat: string;
  formeMarqueur: string;
}
