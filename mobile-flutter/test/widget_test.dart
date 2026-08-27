import 'package:aquasensus_mobile/main.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'helpers/fake_api_client.dart';

Future<void> allerConnexion(WidgetTester tester) async {
  await tester.tap(find.text('Se connecter'));
  await tester.pumpAndSettle();
}

void main() {
  testWidgets('affiche la présentation sans volume saisi', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    expect(find.textContaining('Anticiper la panne'), findsOneWidget);
    expect(find.textContaining('volume'), findsOneWidget);
    expect(find.text('Litres'), findsNothing);
    expect(find.text('Bidons'), findsNothing);
  });

  testWidgets('SQ1 non passant : mot de passe incorrect (401)', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await allerConnexion(tester);
    await tester.enterText(find.byType(TextField).at(0), 'admin@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'mauvais');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('incorrect'), findsOneWidget);
  });

  testWidgets('SQ1 non passant : compte verrouillé (423)', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await allerConnexion(tester);
    await tester.enterText(find.byType(TextField).at(0), 'verrouille@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('verrouillé'), findsOneWidget);
  });

  testWidgets('SQ1 MDP temporaire : écran de changement (EF-83)', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await allerConnexion(tester);
    await tester.enterText(find.byType(TextField).at(0), 'tempo@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.text('Changer le mot de passe'), findsOneWidget);
  });

  testWidgets('SQ1 passant : ouvre le terrain', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await allerConnexion(tester);
    await tester.enterText(find.byType(TextField).at(0), 'tech@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.text('Terrain'), findsOneWidget);
    expect(find.textContaining('YDE-001'), findsOneWidget);
  });
}
