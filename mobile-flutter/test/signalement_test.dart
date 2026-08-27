import 'package:aquasensus_mobile/ecrans/parcours.dart';
import 'package:aquasensus_mobile/main.dart';
import 'package:aquasensus_mobile/session.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'helpers/fake_api_client.dart';

Future<void> _ouvrirSignaler(WidgetTester tester, {required FakeApiClient api, FileLocale? file}) async {
  await tester.pumpWidget(AquasensusApp(api: api, file: file ?? FileLocale()));
  await tester.tap(find.text('Signaler sans compte'));
  await tester.pumpAndSettle();
}

Future<void> _envoyer(
  WidgetTester tester, {
  required String code,
  required String tel,
  String? otp,
}) async {
  await tester.enterText(find.byType(TextField).at(0), code);
  await tester.enterText(find.byType(TextField).at(1), tel);
  if (otp != null) {
    await tester.enterText(find.byType(TextField).at(2), otp);
  }
  await tester.tap(find.text('Envoyer'));
  await tester.pump();
  await tester.pumpAndSettle();
}

void main() {
  testWidgets('UC-2 nominal : prise en charge', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'YDE-001', tel: '237670000001');
    expect(find.textContaining('Signalement reçu'), findsOneWidget);
  });

  testWidgets('UC-2 A2 corroboration', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'YDE-DUP', tel: '237670000002');
    expect(find.textContaining('Déjà signalé'), findsOneWidget);
  });

  testWidgets('UC-2 A3 OTP incorrect (422)', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'YDE-001', tel: '237670000003', otp: '000000');
    expect(find.textContaining('confirmation incorrect'), findsOneWidget);
  });

  testWidgets('UC-2 A4 quota (429)', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'YDE-001', tel: '237670009999');
    expect(find.textContaining('Trop de signalements'), findsOneWidget);
  });

  testWidgets('UC-2 non passant : ouvrage 404', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'INCONNU', tel: '237670000007');
    expect(find.textContaining('Ouvrage introuvable'), findsOneWidget);
  });

  testWidgets('UC-2 A6 rejeu 200', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    await _envoyer(tester, code: 'YDE-REJEU', tel: '237670000006');
    expect(find.textContaining('Signalement reçu'), findsOneWidget);
  });

  testWidgets('UC-2 A1 hors ligne → file locale', (tester) async {
    final api = FakeApiClient()..enLigne = false;
    final file = FileLocale();
    final session = SessionTerrain(api: api, file: file);
    await tester.pumpWidget(
      AqsScope(
        session: session,
        child: MaterialApp(
          theme: AqsTheme.light(),
          home: const EcranSignaler(),
        ),
      ),
    );
    await tester.enterText(find.byType(TextField).at(0), 'YDE-001');
    await tester.enterText(find.byType(TextField).at(1), '237670000009');
    await tester.tap(find.text('Envoyer'));
    await tester.pump();
    expect(file.aEnvoyer, 1);
    expect(find.textContaining('élément(s) à envoyer'), findsOneWidget);
  });

  testWidgets('H-2 : pas de champ litres / bidons', (tester) async {
    await _ouvrirSignaler(tester, api: FakeApiClient());
    expect(find.widgetWithText(TextField, 'Litres'), findsNothing);
    expect(find.textContaining('bidon'), findsNothing);
  });
}
