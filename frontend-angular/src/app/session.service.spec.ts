import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';

describe('SessionService', () => {
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({});
  });

  it('oriente l’accueil selon le rôle', () => {
    const s = TestBed.inject(SessionService);
    s.enregistrer({
      jetonAcces: 't',
      nomAffichage: 'A',
      roles: ['ADMIN'],
      doitChangerMotDePasse: false
    });
    expect(s.accueil()).toBe('/accueil');
    expect(s.rolePrincipal()).toBe('ADMIN');
    s.enregistrer({
      jetonAcces: 't',
      nomAffichage: 'P',
      roles: ['PARTENAIRE'],
      doitChangerMotDePasse: false
    });
    expect(s.accueil()).toBe('/accueil');
    expect(s.rolePrincipal()).toBe('PARTENAIRE');
    s.enregistrer({
      jetonAcces: 't',
      nomAffichage: 'U',
      roles: ['USAGER'],
      doitChangerMotDePasse: false
    });
    expect(s.accueil()).toBe('/accueil');
    expect(s.rolePrincipal()).toBe('USAGER');
  });
});
