import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { lireFileLocale, marquerConflit, marquerEnvoye } from './file-locale';

@Injectable({ providedIn: 'root' })
export class SynchronisationService {
  constructor(private readonly http: HttpClient) {}

  async rejouer(): Promise<void> {
    const file = lireFileLocale().filter((e) => e.statut === 'EN_ATTENTE');
    for (const e of file) {
      if (e.type !== 'SIGNALEMENT' || !e.corps) {
        continue;
      }
      try {
        await firstValueFrom(
          this.http.post('/api/v1/reports', e.corps, {
            headers: new HttpHeaders({ 'X-Client-Request-Id': e.id })
          })
        );
        marquerEnvoye(e.id);
      } catch (err: unknown) {
        const status = (err as { status?: number }).status;
        if (status === 409 || status === 422) {
          marquerConflit(e.id, 'Le serveur a refusé la copie locale (autorité serveur).');
        }
      }
    }
  }
}
