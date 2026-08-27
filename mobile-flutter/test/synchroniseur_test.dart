import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:aquasensus_mobile/sync/synchroniseur.dart';
import 'package:flutter_test/flutter_test.dart';

import 'helpers/fake_api_client.dart';

void main() {
  test('SQ6 appliquée : 201 puis ENVOYE', () async {
    final api = FakeApiClient();
    final file = FileLocale();
    file.empiler(
      id: 'ok-1',
      type: 'TRANSITION',
      corps: {'interventionId': 'i1', 'cible': 'EN_COURS'},
      resume: 'go',
    );
    await Synchroniseur(api: api, file: file).rejouer();
    expect(file.elements.single.statut, StatutSync.envoye);
  });

  test('SQ6 déjà traitée : 200, marqué ENVOYE', () async {
    final api = FakeApiClient();
    final file = FileLocale();
    file.empiler(
      id: 'ok-2',
      type: 'TRANSITION',
      corps: {'interventionId': 'i1', 'cible': 'EN_COURS'},
      resume: 'go',
    );
    final sync = Synchroniseur(api: api, file: file);
    await sync.rejouer();
    file.elements.single.statut = StatutSync.enAttente;
    await sync.rejouer();
    expect(file.elements.single.statut, StatutSync.envoye);
  });

  test('SQ6 conflit métier : 422 EN_CONFLIT, file non purgée', () async {
    final api = FakeApiClient();
    final file = FileLocale();
    file.empiler(
      id: 'conflit-1',
      type: 'TRANSITION',
      corps: {'interventionId': 'i1', 'cible': 'REALISEE'},
      resume: 'go',
    );
    await Synchroniseur(api: api, file: file).rejouer();
    expect(file.elements.single.statut, StatutSync.enConflit);
    expect(file.aEnvoyer, 0);
    expect(file.elements, isNotEmpty);
  });

  test('coupure réseau : reste EN_ATTENTE', () async {
    final api = FakeApiClient()..enLigne = false;
    final file = FileLocale();
    file.empiler(id: 'x', type: 'SIGNALEMENT', corps: {'pointEauCode': 'YDE-001'}, resume: 's');
    await Synchroniseur(api: api, file: file).rejouer();
    expect(file.elements.single.statut, StatutSync.enAttente);
  });
}
