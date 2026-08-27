import 'package:aquasensus_mobile/ecrans/parcours.dart';
import 'package:aquasensus_mobile/main.dart';
import 'package:aquasensus_mobile/session.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'helpers/fake_api_client.dart';

Future<void> _grandeSurface(WidgetTester tester) async {
  tester.view.physicalSize = const Size(800, 2000);
  tester.view.devicePixelRatio = 1.0;
  addTearDown(tester.view.resetPhysicalSize);
}

Future<void> _ouvrirIntervention(WidgetTester tester, {required FakeApiClient api, FileLocale? file}) async {
  await _grandeSurface(tester);
  await tester.pumpWidget(AquasensusApp(api: api, file: file ?? FileLocale()));
  await tester.tap(find.text('Se connecter'));
  await tester.pumpAndSettle();
  await tester.enterText(find.byType(TextField).at(0), 'tech@aquasensus.local');
  await tester.enterText(find.byType(TextField).at(1), 'ok');
  await tester.tap(find.text('Entrer'));
  await tester.pumpAndSettle();
  await tester.tap(find.byKey(const Key('btn-intervention')));
  await tester.pumpAndSettle();
}

void main() {
  testWidgets('SQ6 : démarrer hors ligne empile un élément', (tester) async {
    await _grandeSurface(tester);
    final api = FakeApiClient()..enLigne = false;
    final file = FileLocale();
    final session = SessionTerrain(api: api, file: file);
    await tester.pumpWidget(
      AqsScope(
        session: session,
        child: MaterialApp(
          theme: AqsTheme.light(),
          home: const EcranIntervention(),
        ),
      ),
    );
    await tester.tap(find.byKey(const Key('btn-demarrer')));
    await tester.pump();
    expect(file.aEnvoyer, 1);
    expect(find.byKey(const Key('aqs-status')), findsOneWidget);
  });

  testWidgets('SQ6 non passant : compte rendu incomplet', (tester) async {
    await _ouvrirIntervention(tester, api: FakeApiClient());
    await tester.tap(find.byKey(const Key('btn-realisee')));
    await tester.pump();
    expect(find.textContaining('Diagnostic et action requis'), findsOneWidget);
  });

  testWidgets('SQ6 passant : file puis sync « Tout est synchronisé »', (tester) async {
    final file = FileLocale();
    await _ouvrirIntervention(tester, api: FakeApiClient(), file: file);
    await tester.tap(find.byKey(const Key('btn-demarrer')));
    final champs = find.byType(TextField);
    await tester.enterText(champs.at(0), 'Joint usé');
    await tester.enterText(champs.at(1), 'Usure');
    await tester.enterText(champs.at(2), 'Remplacement joint');
    await tester.tap(find.text('Enregistrer le compte rendu'));
    await tester.pump();
    await tester.enterText(champs.at(3), 'JOINT-01');
    await tester.tap(find.byKey(const Key('btn-piece')));
    await tester.pump();
    await tester.tap(find.byKey(const Key('btn-realisee')));
    await tester.pump();
    expect(file.aEnvoyer, greaterThanOrEqualTo(3));
    await tester.tap(find.byKey(const Key('btn-sync')));
    await tester.pumpAndSettle();
    expect(find.text('Tout est synchronisé'), findsOneWidget);
    expect(file.aEnvoyer, 0);
  });

  testWidgets('SQ6 conflit : bandeau autorité serveur', (tester) async {
    await _grandeSurface(tester);
    final api = FakeApiClient();
    final file = FileLocale();
    file.empiler(
      id: 'conflit-terrain',
      type: 'TRANSITION',
      corps: {'interventionId': 'i1', 'cible': 'EN_COURS'},
      resume: 'x',
    );
    await tester.pumpWidget(AquasensusApp(api: api, file: file));
    await tester.tap(find.text('Se connecter'));
    await tester.pumpAndSettle();
    await tester.enterText(find.byType(TextField).at(0), 'tech@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ok');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('1 élément(s) à envoyer'), findsOneWidget);
    await tester.tap(find.byKey(const Key('btn-intervention')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('btn-sync')));
    await tester.pumpAndSettle();
    expect(find.textContaining('serveur fait autorité'), findsWidgets);
  });
}
