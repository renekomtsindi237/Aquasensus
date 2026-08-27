import { Page, Route } from '@playwright/test';

const KPI = {
  retablissementMedianMinutes: 180,
  retablissementP90Minutes: 420,
  pointsParEtat: {
    OPERATIONNEL: 4,
    SOUS_SURVEILLANCE: 1,
    RISQUE_ELEVE: 1,
    EN_PANNE: 1,
    EN_REPARATION: 0,
    HORS_SERVICE: 1
  },
  horsServiceExclus: 1,
  ouvragesActifsHorsHorsService: 7,
  alertesActives: 2,
  interventionsEnCours: 1,
  delaiAffectationMedianMinutes: 90,
  tauxAnticipation: 0.5,
  note: 'HORS_SERVICE exclu des KPI de disponibilité (RG-12). Aucun volume d\'eau.'
};

function json(route: Route, status: number, body: unknown): Promise<void> {
  return route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body)
  });
}

function jeton(route: Route): string {
  return route.request().headers()['authorization'] ?? '';
}

function estAdmin(route: Route): boolean {
  return jeton(route).includes('admin');
}

function peutKpi(route: Route): boolean {
  const a = jeton(route);
  return a.includes('admin') || a.includes('delegue') || a.includes('partenaire');
}

function estDelegueOuAdmin(route: Route): boolean {
  const a = jeton(route);
  return a.includes('admin') || a.includes('delegue');
}

function prive(route: Route, path: string): boolean {
  const auth = jeton(route);
  if (auth) {
    return false;
  }
  return (
    path.includes('/dashboard') ||
    path.endsWith('/work-queue') ||
    path.includes('/users') ||
    path.includes('/simulation') ||
    path.includes('/admin') ||
    path.endsWith('/notifications') ||
    path.includes('/engine/') ||
    path.includes('/qualification') ||
    path.includes('/water-points/import')
  );
}

/** Intercepte l’API : scénarios nominaux et alternatives des séquences SQ1–SQ7. */
export async function intercepterApi(page: Page): Promise<void> {
  const comptes = [
    { id: 'u1', identifiant: 'admin@aquasensus.local', nomAffichage: 'Admin', statut: 'ACTIF' }
  ];

  await page.route('**/api/v1/**', (route: Route) => {
    const url = new URL(route.request().url());
    const method = route.request().method();
    const path = url.pathname;
    const body = (): Record<string, unknown> => {
      try {
        return (route.request().postDataJSON() as Record<string, unknown>) ?? {};
      } catch {
        return {};
      }
    };

    if (prive(route, path)) {
      return json(route, 403, { titre: 'Accès refusé' });
    }

    const adminSeul =
      path.includes('/users') ||
      path.includes('/admin') ||
      path.includes('/simulation') ||
      path.includes('/water-points/import') ||
      path.includes('/engine/');
    if (adminSeul && !estAdmin(route)) {
      return json(route, 403, { titre: 'Accès refusé' });
    }
    if (
      (path.endsWith('/work-queue') || path.includes('/qualification')) &&
      !estDelegueOuAdmin(route)
    ) {
      return json(route, 403, { titre: 'Accès refusé' });
    }
    if (path.includes('/dashboard') && !peutKpi(route)) {
      return json(route, 403, { titre: 'Accès refusé' });
    }

    if (method === 'POST' && path.endsWith('/auth/login')) {
      const b = body();
      const identifiant = String(b.identifiant ?? '');
      const motDePasse = String(b.motDePasse ?? '');
      if (identifiant.includes('verrouille')) {
        return json(route, 423, { titre: 'Compte verrouillé' });
      }
      if (motDePasse === 'mauvais' || motDePasse === '') {
        return json(route, 401, { titre: 'Identifiants invalides' });
      }
      const admin = identifiant.includes('admin');
      const delegue = identifiant.includes('delegue');
      const partenaire = identifiant.includes('partenaire');
      const tech = identifiant.includes('tech');
      const roles = admin
        ? ['ADMIN']
        : delegue
          ? ['DELEGUE']
          : partenaire
            ? ['PARTENAIRE']
            : tech
              ? ['TECHNICIEN']
              : ['USAGER'];
      return json(route, 200, {
        jetonAcces: identifiant.includes('exterieur')
          ? 'e2e-hors'
          : admin
            ? 'e2e-admin'
            : delegue
              ? 'e2e-delegue'
              : partenaire
                ? 'e2e-partenaire'
                : tech
                  ? 'e2e-tech'
                  : 'e2e-usager',
        nomAffichage: 'Compte démo',
        roles,
        doitChangerMotDePasse: identifiant.includes('tempo')
      });
    }

    if (method === 'POST' && path.endsWith('/auth/register')) {
      const b = body();
      const identifiant = String(b.identifiant ?? '');
      const motDePasse = String(b.motDePasse ?? '');
      if (identifiant.includes('admin@') || identifiant === 'habitant@aquasensus.local') {
        return json(route, 409, { titre: 'Un compte existe déjà pour cet identifiant.' });
      }
      if (motDePasse.length < 10) {
        return json(route, 400, { titre: 'Mot de passe trop court' });
      }
      return json(route, 201, {
        jetonAcces: 'e2e-usager',
        nomAffichage: String(b.nomAffichage ?? 'Usager'),
        roles: ['USAGER'],
        doitChangerMotDePasse: false
      });
    }

    if (method === 'POST' && path.endsWith('/auth/password/change')) {
      if (!jeton(route)) {
        return json(route, 401, { titre: 'Non authentifié' });
      }
      return route.fulfill({ status: 204 });
    }

    if (method === 'POST' && path.includes('/auth/password/reset-request')) {
      return json(route, 202, {
        message: 'Si un compte correspond, un code a été envoyé (canal simulé).'
      });
    }

    if (method === 'POST' && path.endsWith('/reports')) {
      const b = body();
      const code = String(b.pointEauCode ?? '');
      const otp = String(b.codeOtp ?? '');
      const tel = String(b.declarantTelephone ?? '');
      if (code === 'INCONNU' || code === 'YDE-999') {
        return json(route, 404, { titre: 'Ouvrage introuvable' });
      }
      if (otp && otp !== '123456') {
        return json(route, 422, { codeRegle: 'EF-11', titre: 'Code de confirmation incorrect.' });
      }
      if (tel.endsWith('9999')) {
        return json(route, 429, { titre: 'Quota dépassé' });
      }
      const corroboration = code === 'YDE-DUP';
      const rejeu = code === 'YDE-REJEU';
      const panne = String(b.categorie) === 'PANNE_TOTALE' && code === 'YDE-PANNE';
      const haute = String(b.gravite) === 'HAUTE';
      let message = 'Signalement reçu. Le comité va en prendre connaissance.';
      if (corroboration) {
        message = 'Déjà signalé par 2 personne(s). Le comité a été averti.';
      }
      if (haute) {
        message = 'Signalement grave : le comité a été notifié.';
      }
      return json(route, rejeu ? 200 : 201, {
        id: '11111111-1111-1111-1111-111111111111',
        reference: rejeu ? 'SIG-2026-00001' : 'SIG-2026-00042',
        statut: corroboration ? 'DOUBLON' : 'RECU',
        nbCorroborations: corroboration ? 1 : 0,
        pointEau: {
          code: code || 'YDE-042',
          nomUsage: 'Forage Nkolbisson',
          etat: panne ? 'EN_PANNE' : 'OPERATIONNEL'
        },
        priseEnCharge: {
          dejaSignale: corroboration,
          interventionEnCours: false,
          message
        }
      });
    }

    if (method === 'PATCH' && path.includes('/qualification')) {
      const b = body();
      if (!b.motif || String(b.motif).trim() === '') {
        return json(route, 422, { codeRegle: 'RG-11', titre: 'Un motif est obligatoire.' });
      }
      return json(route, 200, {
        id: path.split('/')[4],
        reference: 'SIG-2026-00007',
        statut: b.decision,
        motif: b.motif
      });
    }

    if (path.endsWith('/water-points/map')) {
      return json(route, 200, [
        {
          id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
          code: 'YDE-001',
          etat: 'OPERATIONNEL',
          libelleEtat: 'Opérationnel',
          formeMarqueur: 'cercle-plein',
          latitude: 3.87,
          longitude: 11.52
        },
        {
          id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2',
          code: 'YDE-002',
          etat: 'EN_PANNE',
          libelleEtat: 'En panne',
          formeMarqueur: 'losange',
          latitude: 3.88,
          longitude: 11.53
        }
      ]);
    }

    if (method === 'POST' && path.endsWith('/water-points/import')) {
      const csv = route.request().postData() ?? '';
      if (csv.toLowerCase().includes('volume') || csv.toLowerCase().includes('litre')) {
        return json(route, 200, {
          lignes: [{ numero: 1, ok: false, message: 'Colonne volume interdite (H-2).' }]
        });
      }
      if (!csv.includes('YDE-') && !csv.includes('code')) {
        return json(route, 400, { titre: 'CSV vide ou invalide' });
      }
      return json(route, 200, { lignes: [{ numero: 2, ok: true, message: 'Créé YDE-IMP-1' }] });
    }

    if (path.includes('/water-points') && method === 'GET') {
      return json(route, 200, {
        elements: [
          {
            id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
            code: 'YDE-001',
            nomUsage: 'Forage Nkolbisson Marché',
            etat: 'OPERATIONNEL',
            localiteChemin: 'Centre / Yaoundé / Nkolbisson'
          }
        ],
        page: 0,
        taille: 20
      });
    }

    if (path.endsWith('/work-queue')) {
      const auth = route.request().headers()['authorization'] ?? '';
      if (auth.includes('hors')) {
        return json(route, 403, { titre: 'Hors périmètre' });
      }
      return json(route, 200, {
        signalementsAQualifier: [
          {
            id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
            reference: 'SIG-2026-00007',
            categorie: 'PANNE_TOTALE',
            priorite: 1
          }
        ],
        interventionsActives: [
          {
            id: 'int-1',
            reference: 'INT-2026-0003',
            statut: 'AFFECTEE',
            echeanceSouhaitee: '2026-09-01'
          }
        ],
        alertesActives: [
          {
            id: 'alerte-1',
            typeRegle: 'R1_ECHEANCE_MAINTENANCE',
            niveau: 'ELEVE',
            explication: 'Échéance d’entretien à 93 % — 450 habitants, saison sèche. Aucun volume d\'eau.'
          }
        ]
      });
    }

    if (path.includes('/dashboard/kpi')) {
      return json(route, 200, KPI);
    }

    if (path.includes('/dashboard/budget')) {
      return json(route, 200, [
        { comiteId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', coutPieces: 12000, coutMainOeuvre: 8000 }
      ]);
    }

    if (path.includes('/dashboard/export.pdf')) {
      return route.fulfill({ status: 200, contentType: 'application/pdf', body: '%PDF-1.4 mock' });
    }

    if (path.includes('/dashboard/export')) {
      return route.fulfill({
        status: 200,
        contentType: 'text/csv',
        body: 'indicateur,valeur\nnote,"Aucun volume d\'eau"\n'
      });
    }

    if (path.endsWith('/notifications')) {
      return json(route, 200, [
        {
          id: 'n1',
          canal: 'IN_APP',
          evenement: 'SIGNALEMENT_GRAVE',
          titre: 'Signalement grave',
          corps: 'Un signalement de gravite haute a ete recu. Aucun volume d\'eau.',
          statut: 'ENVOYEE',
          lue: false
        }
      ]);
    }

    if (path.endsWith('/users') && method === 'GET') {
      return json(route, 200, comptes);
    }

    if (path.endsWith('/users') && method === 'POST') {
      const b = body();
      if (String(b.identifiant) === 'admin@aquasensus.local') {
        return json(route, 409, { titre: 'Un compte existe déjà pour cet identifiant.' });
      }
      comptes.push({
        id: 'u-new',
        identifiant: String(b.identifiant),
        nomAffichage: String(b.nomAffichage),
        statut: 'ACTIF'
      });
      return json(route, 201, { id: 'u-new', identifiant: b.identifiant, statut: 'ACTIF' });
    }

    if (method === 'PATCH' && path.includes('/users/')) {
      const id = path.split('/').pop();
      const cible = comptes.find((c) => c.id === id);
      if (!cible) {
        return json(route, 404, { titre: 'Introuvable' });
      }
      cible.statut = 'SUSPENDU';
      return json(route, 200, cible);
    }

    if (path.endsWith('/admin/types-pieces')) {
      return json(route, 200, [{ code: 'JOINT', libelle: 'Joint / garniture', actif: true }]);
    }

    if (path.endsWith('/admin/symptomes')) {
      return json(route, 200, ['PANNE_TOTALE', 'DEBIT_FAIBLE']);
    }

    if (method === 'POST' && path.endsWith('/simulation/sms/inbound')) {
      const b = body();
      const contenu = String(b.contenu ?? '');
      if (!contenu.toUpperCase().startsWith('AQS ')) {
        return json(route, 200, {
          reponse: 'AquaSensus: format non reconnu. Envoyez AQS <code> <symptome>.'
        });
      }
      return json(route, 200, {
        reponse: 'AquaSensus: signalement SIG-2026-01187 enregistre pour YDE-042. Le comite est averti.'
      });
    }

    if (method === 'POST' && path.endsWith('/simulation/ussd/session')) {
      const b = body();
      const saisie = String(b.saisie ?? '');
      if (saisie === 'TIMEOUT' || saisie === 'EXPIRE') {
        return json(route, 200, {
          sessionId: null,
          ecran: 'Session expiree. Composez *123# a nouveau.',
          termine: true
        });
      }
      if (saisie === 'YDE-999' || saisie === 'INCONNU') {
        return json(route, 200, {
          sessionId: 'sess-1',
          ecran: 'Code inconnu. Reessayez.',
          termine: false
        });
      }
      if (saisie === '*123#') {
        return json(route, 200, {
          sessionId: 'sess-1',
          ecran: 'AquaSensus\n1. Signaler un probleme\n2. Etat d\'un point d\'eau\n3. Mes signalements',
          termine: false
        });
      }
      if (saisie === '1') {
        return json(route, 200, {
          sessionId: 'sess-1',
          ecran: 'Entrez le code du point d\'eau (ex: YDE-042)',
          termine: false
        });
      }
      if (saisie === 'YDE-042') {
        return json(route, 200, {
          sessionId: 'sess-1',
          ecran: '1. Panne totale  2. Debit faible\n3. Eau trouble  4. Bruit',
          termine: false
        });
      }
      return json(route, 200, {
        sessionId: null,
        ecran: 'Signalement SIG-2026-01187 enregistre. Le comite est averti.',
        termine: true
      });
    }

    if (path.endsWith('/simulation/messages')) {
      return json(route, 200, [
        {
          id: 'm1',
          direction: 'ENTRANT',
          canal: 'SMS',
          numeroFictif: '****1122',
          contenu: 'AQS YDE-042 PANNE'
        }
      ]);
    }

    if (path.includes('/engine/parameters')) {
      return json(route, 200, { version: 'v1', contenu: '{"seuilR1":0.93}', actif: true });
    }

    return json(route, 404, { titre: 'Non mocké en e2e' });
  });
}
